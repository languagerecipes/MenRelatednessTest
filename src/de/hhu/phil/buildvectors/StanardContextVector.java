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

import de.phil.hhu.men.utils.MenUtils;
import de.phil.hhu.obj.MenEntry;

import ie.pars.experiment.context.ContextQuery;
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

/**
 *
 * @author Behrang QasemiZadeh <zadeh at phil.hhu.de>
 */
public class StanardContextVector implements Runnable {
    private ExpSet expSet;
    
   

    public StanardContextVector(ExpSet expSet, ContextQuery contextQueryLemmaTag) {
        this.expSet = expSet;
        this.contextQueryLemmaTag = contextQueryLemmaTag;
    }
      
    private ContextQuery contextQueryLemmaTag;
    private void aggregateFreq2File() throws Exception {
        TreeMap<String, Integer> mapVector = new TreeMap();
        // check weather the vector has been cashed before

        File expVectorFile = new File(expSet.getSparseVectorPresentations(contextQueryLemmaTag));
        if (expVectorFile.exists()) {
            System.out.println("Vector has been built before");
            return;
        }
        if (!expVectorFile.getParentFile().exists()) {
            expVectorFile.getParentFile().mkdirs();
        }
       // System.out.println(" * " + contextQueryLemmaTag.toString() + ": ");
        //double countall = 0.0;
        List<FCRITInfo> fcritContextQueries = expSet.getFCRITContextQueries();

        for (FCRITInfo fcrit : fcritContextQueries) {
            String rawContextFilesName = expSet.getRawContextFilesName(contextQueryLemmaTag, fcrit);
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
                    //countall += fl.getFreq();
                    MapUtils.updateMap(mapVector, fl.getContext(), fl.getFreq());
                }
            }
            br.close();

        }
        BufferedWriter writer
                = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(expVectorFile),
                        StandardCharsets.UTF_8));
        
        for (String key : mapVector.keySet()) {
            writer.append(key + " : " + mapVector.get(key)+"\n");
        }
        writer.close();
        
        
    }

    public static void main(String[] s) throws IOException, Exception {
        int numebrOfThreads = 30;
        ExecutorService newFixedThreadPool = Executors.newFixedThreadPool(numebrOfThreads);

        for (int i = 1; i < 9; i++) {

            ExpSet expSet = new ExpSet("15032015-", -1 * i, 1 * i);
            String simType = "cos";
            Set<MenEntry> readMenData = MenUtils.readMenData(expSet.menFile);
            for (MenEntry me : readMenData) {
                ContextQueryLemmaTag contextQueryLemmaTag = new ContextQueryLemmaTag(me.getLemma(), me.getTag());
                StanardContextVector scv = new StanardContextVector(expSet, contextQueryLemmaTag);
               // scv.aggregateFreq2File();
                 newFixedThreadPool.submit(scv);
            }
        }
        newFixedThreadPool.shutdown();
        while (!newFixedThreadPool.awaitTermination(1, TimeUnit.SECONDS));
        System.out.println("Done");
    }

    @Override
    public void run() {
        try {
            this.aggregateFreq2File();
        } catch (Exception ex) {
            Logger.getLogger(StanardContextVector.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    

   
}
