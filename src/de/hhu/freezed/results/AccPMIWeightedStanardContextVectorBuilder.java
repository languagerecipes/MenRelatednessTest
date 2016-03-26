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
package de.hhu.freezed.results;

import de.hhu.phil.buildvectors.ContextFrequencyLoader;
import de.hhu.phil.men.process.ComputeSimilarity;
import de.hhu.phil.men.process.STEPCompareResults;
import de.phil.hhu.men.utils.MenUtils;
import de.phil.hhu.obj.MenEntry;
import de.phil.hhu.obj.MenPair;

import ie.pars.experiment.context.ContextQueryLemmaTag;
import ie.pars.experiment.context.ExpSet;
import ie.pars.experiment.context.FCRITInfo;
import ie.pars.noske.parse.obj.FrequencyLine;
import ie.pars.utils.MapUtils;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import ie.pars.experiment.context.IContextQuery;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.math.BigDecimal;
import numbercruncher.mathutils.BigFunctions;

/**
 * add type of distance to the ExpSet
 * add rewrite switch so ser can decide if something must be rewritten
 * @author Behrang QasemiZadeh <zadeh at phil.hhu.de>
 */
public class AccPMIWeightedStanardContextVectorBuilder implements Runnable {

    private ExpSet expSet;
    private IContextQuery contextQueryLemmaTag;
    private ContextFrequencyLoader cfProfiles;
    private String weightingName;

    // just write a weighting interface to do the job
    public AccPMIWeightedStanardContextVectorBuilder(ExpSet expSet,
            String weightingName,
            IContextQuery contextQueryLemmaTag, ContextFrequencyLoader cfProfiles) throws Exception {
        this.expSet = expSet;
        this.contextQueryLemmaTag = contextQueryLemmaTag;
        this.cfProfiles = cfProfiles;
        this.weightingName = weightingName;
    }

    private void aggregateFreq2File() throws Exception {
        TreeMap<String, Double> mapVector = new TreeMap();
        File expVectorFile = new File(expSet.getSparseVectorPresentations(contextQueryLemmaTag, weightingName));
        if (!expVectorFile.getParentFile().exists()) {
            expVectorFile.getParentFile().mkdirs();
        }

         int thisLemmaWithoutITsTag = this.cfProfiles.getContextFrequency(
                this.contextQueryLemmaTag.getContextQueryFileIdentifier().split("-")[0]); //this is an inaccurate numberjust to see  a quick results
        // System.out.println( this.contextQueryLemmaTag.getContextQueryFileIdentifier().split("-")[0]);
        double totalAll = this.cfProfiles.getTotalFreq();
        double c_x = Math.log(thisLemmaWithoutITsTag);
        double c_all = Math.log(totalAll);
        
        List<FCRITInfo> fcritContextQueries = expSet.getFCRITContextQueries();
        for (FCRITInfo fcrit : fcritContextQueries) {
            String rawContextFilesName = expSet.getRawContextFilesName(contextQueryLemmaTag, fcrit);
            //System.out.println(rawContextFilesName);
            File f = new File(rawContextFilesName);
            if (!f.exists()) {
                throw new Exception("The frequenies are not fetched yet for " + rawContextFilesName + "\n" +fcrit.toString()+"\n"+contextQueryLemmaTag.toString() );
            }

            //String target = f.getName();
            BufferedReader br = new BufferedReader(new FileReader(f));
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().length() != 0) {
                    FrequencyLine fl;
                    fl = FrequencyLine.fromFileLine(line);
                    //countall += fl.getFreq();
             
                   // double c_xy = Math.log(fl.getFreq());
                    //int cFTAll = 1;
                    if (this.cfProfiles.hasKey(fl.getContext())) {
                        int cFTAll = cfProfiles.getContextFrequency(fl.getContext());

                        // double c_y = Math.log(cFTAll);
                        double c_xy = Math.log(fl.getFreq());
                        double c_y = Math.log(cFTAll);
                        double ppmif;// = 0.0;
                //if(Double.compare(c_y, 0.0)!=1){ // to filter by freq 1
                        ppmif
                                = Math.max(0, //expSet.getContextWindwoSize()*
                                        c_xy + c_all
                                        - c_x - c_y
                                );
                 
//                        double ppmif
//                                = Math.max(0, //expSet.getContextWindwoSize()*
//                                fl.getFreq() * 1.0
//                                /// cFTAll
//                                ;
                        // -  c_all
                        //c_x - c_y

                        //  );
                        MapUtils.updateMap(mapVector, fl.getContext(), ppmif);
                    } else {
                        System.err.println("* " + fl.getContext() + " is errornous ... check why!");
                    }
                }
            }
            br.close();

        }
        BufferedWriter writer
                = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(expVectorFile),
                        StandardCharsets.UTF_8));
        for (String key : mapVector.keySet()) {
                double weught = mapVector.get(key); // p(c_w) count of all the occurances of word c_w in the corpus; 
            writer.append(key + " : " + weught + "\n");
    
        }
        writer.close();

    }

             
    
    public static void main(String[] s) throws IOException, Exception {
        int numebrOfThreads = 2;
        String simType = "sppmi-f";
        //String weighting = "ppmi-f"; // change weightintg to an interface that impelments the thing
        
        ExpSet expSetBase = new ExpSet("15032015-", 0, 0); // just to load freq profiles once
        ContextFrequencyLoader cfl = new ContextFrequencyLoader(expSetBase); // cetainly this can go out of this lopp for this set of experiments .. that is when only context size changes!
        for (int i = 1; i < 5; i++) {

            ExpSet expSet = new ExpSet("15032015-", -1 * i, 1 * i);
            System.out.println(expSet.getExpIdentifier());
            ExecutorService newFixedThreadPool;
            newFixedThreadPool = Executors.newFixedThreadPool(numebrOfThreads);
            Set<MenEntry> readMenData = MenUtils.readMenData(expSet.getMenFile());
            for (MenEntry me : readMenData) {
                ContextQueryLemmaTag contextQueryLemmaTag = new ContextQueryLemmaTag(me.getLemma(), me.getTag());
                AccPMIWeightedStanardContextVectorBuilder scv = new AccPMIWeightedStanardContextVectorBuilder(
                        expSet, simType, contextQueryLemmaTag, cfl);
                // scv.aggregateFreq2File();
                // 
                newFixedThreadPool.submit(scv);
            }
            newFixedThreadPool.shutdown();
            
            while (!newFixedThreadPool.awaitTermination(1, TimeUnit.SECONDS));
            new Thread(new Runnable() {
                public void run() {
                    computingSim(simType , expSet);
                }
            }).start();

        }
      
        System.out.println("Done ... waiting for resukts");
        
        
    }
    
    private static void computingSim(String simTypeIden, ExpSet expSet)  {

        try {
            List<MenPair> readMenPair = MenUtils.readMenPair(expSet.getMenFile());
            File fout = new File(expSet.getSimilarityResultFile(simTypeIden));
            if (!fout.getParentFile().exists()) {
                fout.getParentFile().mkdirs();
            }
            PrintWriter pw = new PrintWriter(new FileWriter(fout));
            
            ExecutorService newFixedThreadPool;
            newFixedThreadPool = Executors.newFixedThreadPool(10);
            for (MenPair me : readMenPair) {
                ContextQueryLemmaTag cltx1 = new ContextQueryLemmaTag(me.getMenEntry1().getLemma(), me.getMenEntry1().getTag());
                ContextQueryLemmaTag cltx2 = new ContextQueryLemmaTag(me.getMenEntry2().getLemma(), me.getMenEntry2().getTag());
                ComputeSimilarity cs = new ComputeSimilarity(expSet,simTypeIden, cltx1, cltx2,
                        pw);
                //cs.run();
                newFixedThreadPool.submit(cs);
                // break;
            }
            newFixedThreadPool.shutdown();
            while (!newFixedThreadPool.awaitTermination(1, TimeUnit.SECONDS));
            
            pw.close();
            STEPCompareResults.compareResults(expSet, simTypeIden);
            expSet.printConfiguration();
        } catch (IOException ex) {
            Logger.getLogger(AccPMIWeightedStanardContextVectorBuilder.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(AccPMIWeightedStanardContextVectorBuilder.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void run() {
        try {
            this.aggregateFreq2File();
            
        } catch (Exception ex) {
            Logger.getLogger(AccPMIWeightedStanardContextVectorBuilder.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

//    private void ppmiFromStandardSparse() {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }

}
