/*
 * Copyright (C) 2016 Behrang QasemiZadeh <zadeh at phil.hhu.de>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.hhu.phil.buildvectors;

import de.hhu.phil.men.process.STEPCompareResults;
import de.phil.hhu.men.utils.MenUtils;
import de.phil.hhu.obj.MenPair;
import ie.pars.clac.vector.SimpleSparse;

import ie.pars.experiment.context.ContextQueryLemmaTag;
import ie.pars.experiment.context.ExpSet;
import ie.pars.experiment.context.FCRITInfo;
import ie.pars.noske.parse.obj.FrequencyLine;
import ie.pars.utils.MapUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import ie.pars.experiment.context.IContextQuery;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * add type of distance to the ExpSet add rewrite switch so ser can decide if
 * something must be rewritten
 *
 * @author Behrang QasemiZadeh <zadeh at phil.hhu.de>
 */
public class Estim2New2PMIWeightedStanardContextVectorBuilder implements Runnable {

    private final ExpSet expSet;
    private final IContextQuery context2;
    private final IContextQuery context1;
    private final ContextFrequencyLoader cfProfiles;

    private final PrintWriter pw;
    double totalAll;
    double c_all;
    String lemma1;
    String lemma2;

    //PrintWriter debugWriter;
    SimpleSparse tspv1;
    SimpleSparse tspv2;
    private TreeMap<String, Integer> keyStringMap;

    //private static ContextFrequencyLoader cfl;
    // just write a weighting interface to do the job
    public Estim2New2PMIWeightedStanardContextVectorBuilder(
            ExpSet expSet,
            IContextQuery context1,
            IContextQuery context2,
            ContextFrequencyLoader cfProfiles,
            PrintWriter pw
    ) throws Exception {

        this.expSet = expSet;
        this.context1 = context1;
        this.context2 = context2;
        this.cfProfiles = cfProfiles;
        keyStringMap = new TreeMap();
        this.pw = pw;
        totalAll = this.cfProfiles.getTotalFreq();
        c_all = Math.log(totalAll);
        tspv1 = new SimpleSparse();
        tspv2 = new SimpleSparse();
        lemma1 = context1.getContextQueryFileIdentifier().split("-")[0];
        lemma2 = context2.getContextQueryFileIdentifier().split("-")[0];
    }

    private int getKey(String element) {
        if (this.keyStringMap.containsKey(element)) {
            return keyStringMap.get(element);
        } else {
            int key = keyStringMap.size();
            key++;
            keyStringMap.put(element, key);
            return key;
        }
    }

    static void getRamReport() {
        int mb = 1024 * 1024;

        //Getting the runtime reference from system
        Runtime runtime = Runtime.getRuntime();

        System.out.println("##### Heap utilization statistics [MB] #####");

        //Print used memory
        System.out.println("Used Memory:"
                + (runtime.totalMemory() - runtime.freeMemory()) / mb);

        //Print free memory
        System.out.println("Free Memory:"
                + runtime.freeMemory() / mb);

        //Print total available memory
        System.out.println("Total Memory:" + runtime.totalMemory() / mb);

        //Print Maximum available memory
        System.out.println("Max Memory:" + runtime.maxMemory() / mb);

    }

    public void mainSript() throws Exception {
        newScriptBuild();
        //PrintWriter riter1 = new PrintWriter(new FileWriter(new File("E:\\corpora\\MEN\\expsets2\\15032015---1-1-ukwacdep\\vectors-deb\\" + lemma1 + "-" + lemma2)));
        //double cosineSimilarity = this.tspv1.cosine(this.tspv2);
        double cosineSimilarity = cosinePMICorrected(this.tspv1, this.tspv2);
        // riter1.println(tspv1.toString());
        // riter1.println(tspv2.toString());

        //  riter1.close();
        synchronized (pw) {
            // System.out.println( //   writer.write(
            pw.append(this.context1.getContextQueryFileIdentifier()
                    + " " + this.context2.getContextQueryFileIdentifier() + " " + cosineSimilarity
                    + "\n");
        }
        // }
    }

    private void newScriptBuild() throws Exception {
        List<FCRITInfo> fcritContextQueries = expSet.getFCRITContextQueries();
        Set<String> keySet = new TreeSet();
        for (FCRITInfo frci : fcritContextQueries) {
            TreeMap<String, Double> contextFcri1 = readContextToMap(context1, frci);
            TreeMap<String, Double> contextFcri2 = readContextToMap(context2, frci);
            keySet.addAll(new TreeSet(contextFcri1.keySet()));
            keySet.addAll(new TreeSet(contextFcri2.keySet()));
            buildVectorPart(keySet, tspv1, contextFcri1, lemma1);
            buildVectorPart(keySet, tspv2, contextFcri2, lemma2);
        }

    }

    private TreeMap<String, Double> readContextToMap(IContextQuery context1, FCRITInfo fcrit) throws Exception {
        TreeMap<String, Double> mapVector = new TreeMap();
        String rawContextFilesName = expSet.getRawContextFilesName(context1, fcrit);
        //System.out.println(rawContextFilesName);
        File f = new File(rawContextFilesName);
        if (!f.exists()) {
            System.err.println(rawContextFilesName);
            throw new Exception("The frequenies are not fetched yet! --> "+ rawContextFilesName );
        }

        //String target = f.getName();
        BufferedReader br = new BufferedReader(new FileReader(f));
        String line;
        while ((line = br.readLine()) != null) {
            if (line.trim().length() != 0) {
                FrequencyLine fl;
                fl = FrequencyLine.fromFileLine(line);
                MapUtils.updateMap(mapVector, fl.getContext(), fl.getFreq());
            }
        }
        br.close();
        return mapVector;
    }

    private void buildVectorPart(Set<String> keySet, SimpleSparse tspv1, TreeMap<String, Double> contextFcri1, String lemma1) {
        Integer contextFrequency2 ;
               contextFrequency2 = this.cfProfiles.getContextFrequency(lemma1);
        double c_x = Math.log(contextFrequency2);
        for (String key : keySet) {
            double c_xy = 0.0;
            if (contextFcri1.containsKey(key)) {
                c_xy = Math.log(contextFcri1.get(key));
            }
            //if(c_xy==0.0){
            // use the above condfition and max 0 then the output must be the same as previous experiments    

            if (c_xy != 0.0) {
                double c_y = Math.log(this.cfProfiles.getContextFrequency0(key));
                double pmi
                        = // ppmi"2
                        Math.max(0, c_xy + c_all
                                - c_x - c_y);

                // normalized by c_xy positive side
//                        Math.max(0,( (c_xy + c_all
//                                - c_x - c_y)/c_xy));
//                                   ( (c_xy + c_all
//                                - c_x - c_y)/Math.max(c_x, c_y));
                int key1 = getKey(key);
                double value = tspv1.getValue(key1);
                if (Double.isInfinite(c_xy)) {
                   // System.out.println("C_xy is infirnite for " + key + " " + lemma1);
                }
                double theValue = pmi + value;
                if (Double.isInfinite(theValue)) {
//                if((pmi + value)==Double.POSITIVE_INFINITY){
//                    theValue = 2*c_all;
//                }else{
//                    theValue = -2*c_all;
//                }
                   // System.out.println("*C_xy is infirnite for " + key + " " + lemma1);
                    theValue = 0.0;
                }
                tspv1.setValue(key1, theValue);
            }
        }
    }

    public double cosinePMICorrected(SimpleSparse ss1, SimpleSparse ss2) {
        double cosine = 0.0;

        int actualDimension = 200000;
        double thisLength1 = getCorrectedLength(ss1, actualDimension);
        double thisLength2 = getCorrectedLength(ss2, actualDimension);

        double dot = 0.0;
        for (Integer key : ss1.getIndices()) {
            double valueSS2 = ss2.getValue(key);
            if (valueSS2 == 0) {
                dot += ss1.getValue(key);
            } else {
                dot += (ss1.getValue(key) * valueSS2);
            }
        }
        for (Integer key : ss2.getIndices()) {
            if (!ss1.hasIndice(key)) {
                dot += ss2.getValue(key);
            }
        }
        cosine = dot / (thisLength1 * thisLength2); // so funny, I thought * has privilige to / but appreantly not on my computer.. without pranthesis I get some crap

        return cosine;
    }
    
  public double liCorrected(SimpleSparse ss1, SimpleSparse ss2, int actualDimension) {
        double cosine = 0.0;

      //  double thisLength1 = getCorrectedLength(ss1, actualDimension);
      //  double thisLength2 = getCorrectedLength(ss2, actualDimension);

        double dot = 0.0;
        for (Integer key : ss1.getIndices()) {
            double valueSS2 = Math.abs(ss2.getValue(key));
            if (valueSS2 == 0) {
                dot += ss1.getValue(key);
            } else {
                dot += Math.abs(ss1.getValue(key) - valueSS2);
            }
        }
        for (Integer key : ss2.getIndices()) {
            if (!ss1.hasIndice(key)) {
                dot += Math.abs(ss2.getValue(key));
            }
        }
        cosine = dot ;
                /// (thisLength1 * thisLength2); // so funny, I thought * has privilige to / but appreantly not on my computer.. without pranthesis I get some crap

        return cosine;
    }
    public double getCorrectedLength(SimpleSparse vector, int actualDimension) {
        double length = 0.0;
        for (double value : vector.getVaules()) {
            length += (value * value);
        }
        int remainLength = actualDimension - vector.getIndices().size();
        if (remainLength > 0) {
            length += remainLength;
        }
        return Math.sqrt(length);
    }

    @Override
    public void run() {
        try {
            mainSript();
        } catch (Exception ex) {
            System.err.println(ex);
        }
    }

    public static void main(String[] ss) throws IOException, Exception {
        //int i = 1;
        ExpSet expSetCFL = new ExpSet("15032015-", 0, 0);
        ContextFrequencyLoader cfl 
                = new ContextFrequencyLoader(expSetCFL);

        for (int i = 29; i < 40; i+=2) {
            int numebrOfThreads = 100;
            ExecutorService newFixedThreadPool;
            newFixedThreadPool = Executors.newFixedThreadPool(numebrOfThreads);

            //String simTypeName = "pmi2c_xy^2";
            //  String simTypeName = "pmi-div-by-c_xy";
            String simTypeName = "pmi-new-estimator-fixed-dim-200k-pos-only";
            ExpSet expSet = new ExpSet("15032015-", -1 * i, 1 * i);
            System.out.println(expSet.getExpIdentifier());

            File fout = new File(expSet.getSimilarityResultFile(simTypeName));
            if (!fout.getParentFile().exists()) {
                fout.getParentFile().mkdirs();
            }
            PrintWriter pw = new PrintWriter(new FileWriter(fout));

            List<MenPair> readMenPair = MenUtils.readMenPair(expSet.getMenFile());
            for (MenPair me : readMenPair) {
                ContextQueryLemmaTag cql1 = new ContextQueryLemmaTag(me.getMenEntry1().getLemma(), me.getMenEntry1().getTag());
                ContextQueryLemmaTag cql2 = new ContextQueryLemmaTag(me.getMenEntry2().getLemma(), me.getMenEntry2().getTag());
                Estim2New2PMIWeightedStanardContextVectorBuilder scv
                        = new Estim2New2PMIWeightedStanardContextVectorBuilder(
                                expSet, cql1, cql2, cfl, pw);
                //scv.mainSript();
                newFixedThreadPool.submit(scv);
            }
            getRamReport();
            newFixedThreadPool.shutdown();
            while (!newFixedThreadPool.awaitTermination(1, TimeUnit.SECONDS));
            pw.close();
            STEPCompareResults.compareResults(expSet, simTypeName);
        }
    }
}
