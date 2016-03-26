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

import de.hhu.phil.buildvectors.randomized.RandomIndexFileReader;
import de.hhu.phil.buildvectors.randomized.RandomIndexFileReader2;
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
import java.util.TreeMap;
import ie.pars.experiment.context.IContextQuery;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Standard random indexing builder
 *
 * @author Behrang QasemiZadeh <zadeh at phil.hhu.de>
 */
public class StdRandomIndexingContextBuilder implements Runnable {

    
    private final ExpSet expSet;
    private final IContextQuery context2;
    private final IContextQuery context1;
    private final ContextFrequencyLoader cfProfiles;

    private final RandomIndexFileReader2 rvif;

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
    public StdRandomIndexingContextBuilder(
            ExpSet expSet,
            IContextQuery context1,
            IContextQuery context2,
            ContextFrequencyLoader cfProfiles,
            RandomIndexFileReader2 rvif,
            PrintWriter pw
    ) throws Exception {
        this.rvif = rvif;
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

//    private int getKey(String element) {
//        if (this.keyStringMap.containsKey(element)) {
//            return keyStringMap.get(element);
//        } else {
//            int key = keyStringMap.size();
//            key++;
//            keyStringMap.put(element, key);
//            return key;
//        }
//    }
    public void mainSript() throws Exception {
        newScriptBuild();
        //PrintWriter riter1 = new PrintWriter(new FileWriter(new File("E:\\corpora\\MEN\\expsets2\\15032015---1-1-ukwacdep\\vectors-deb\\" + lemma1 + "-" + lemma2)));
        //double cosineSimilarity = this.tspv1.cosine(this.tspv2);
        // riter1.println(tspv1.toString());
        // riter1.println(tspv2.toString());
        double cosineSimilarity = this.tspv1.cosine(this.tspv2);

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
        //Set<String> keySet = new TreeSet();
        for (FCRITInfo frci : fcritContextQueries) {
            TreeMap<String, Double> contextFcri1 = readContextToMap(context1, frci);
            TreeMap<String, Double> contextFcri2 = readContextToMap(context2, frci);
           // keySet.addAll(new TreeSet(contextFcri1.keySet()));
           // keySet.addAll(new TreeSet(contextFcri2.keySet()));
            buildVectorPartRI( tspv1, contextFcri1, lemma1);
            buildVectorPartRI( tspv2, contextFcri2, lemma2);
        }

    }

    private TreeMap<String, Double> readContextToMap(IContextQuery context1, FCRITInfo fcrit) throws Exception {
        TreeMap<String, Double> mapVector = new TreeMap();
        String rawContextFilesName = expSet.getRawContextFilesName(context1, fcrit);
        //System.out.println(rawContextFilesName);
        File f = new File(rawContextFilesName);
        if (!f.exists()) {

            throw new Exception("The frequenies are not fetched yet!");
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

    private void buildVectorPartRI(SimpleSparse tspv1, TreeMap<String, Double> contextFcri1, String lemma1) throws IOException {
        Integer targetEntity;
        targetEntity = this.cfProfiles.getContextFrequency(lemma1);
        double c_x = Math.log(targetEntity);

        for (String key : contextFcri1.keySet()) {

            Double get = contextFcri1.get(key);
//            double c_xy = Math.log(get);
//            double c_y = Math.log(this.cfProfiles.getContextFrequency0(key));
//                double pmi
//                        =  c_xy + c_all
//                                - c_x - c_y;
//               if (pmi > 0 && !Double.isNaN(pmi)) {
                SimpleSparse vector = this.rvif.getVector(key);

                tspv1.addVectorMultiplyFreq(vector, get);
             //  }

//            double c_xy = 0.0;
//            if (contextFcri1.containsKey(key)) {
//                c_xy = Math.log(contextFcri1.get(key));
//            }
//            if (c_xy != 0.0) {
//                double c_y = Math.log(this.cfProfiles.getContextFrequency0(key));
//                double pmi
//                        Math.max(0, c_xy + c_all
//                                - c_x - c_y);
//                int key1 = getKey(key);
//                double value = tspv1.getValue(key1);
//                if (Double.isInfinite(c_xy)) {
//                   // System.out.println("C_xy is infirnite for " + key + " " + lemma1);
//                }
//                double theValue = pmi + value;
//                if (Double.isInfinite(theValue)) {
//                    theValue = 0.0;
//                }
            //  tspv1.setValue(key1, theValue);
        }
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
        RandomIndexFileReader2 randomIndexReader = new RandomIndexFileReader2(expSetCFL.getRndVecStd(500, 6)+"sa");
        
        ContextFrequencyLoader cfl 
                = new ContextFrequencyLoader(expSetCFL);
        
        for (int i = 11; i < 13; i+=2) {
            System.out.println("here!");
            int numebrOfThreads = 40;
            ExecutorService newFixedThreadPool;
            newFixedThreadPool = Executors.newFixedThreadPool(numebrOfThreads);

            //String simTypeName = "pmi2c_xy^2";
            //  String simTypeName = "pmi-div-by-c_xy";
            String simTypeName = "std-rnd-vector-d500-nz6sa";
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
                StdRandomIndexingContextBuilder scv
                        = new StdRandomIndexingContextBuilder(
                                expSet, cql1, cql2, cfl, randomIndexReader, pw);
               // scv.mainSript();
                newFixedThreadPool.submit(scv);
            }
            MenUtils.getRamReport();
            newFixedThreadPool.shutdown();
            while (!newFixedThreadPool.awaitTermination(1, TimeUnit.SECONDS));
            pw.close();
            STEPCompareResults.compareResults(expSet, simTypeName);
        }
    }
}
