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
package de.hhu.phil.men.process;

import ie.pars.clac.vector.SimpleSparse;
import ie.pars.clac.vector.SparseVector;


import ie.pars.experiment.context.ExpSet;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Writer;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import ie.pars.experiment.context.IContextQuery;

/**
 *
 * @author Behrang QasemiZadeh <zadeh at phil.hhu.de>
 */
public class ComputeSimilarity implements Runnable {

    private ExpSet expSet;
    private TreeMap<String, Integer> keyStringMap;
    IContextQuery contextQueryLemmaTag1;
    IContextQuery contextQueryLemmaTag2;
    Writer writer;
    String simWeightingType;
    
    public ComputeSimilarity(ExpSet expSet, 
            String simWeightingType,
            IContextQuery contextQueryLemmaTag1, 
            IContextQuery contextQueryLemmaTag2, 
            Writer writer) throws Exception {
        keyStringMap = new TreeMap();
        this.expSet = expSet;
        this.contextQueryLemmaTag1 = contextQueryLemmaTag1;
        this.contextQueryLemmaTag2 = contextQueryLemmaTag2;
        this.writer = writer;
        this.simWeightingType = simWeightingType;
        
    }

    private SimpleSparse loadAggFreqContextFilesToMap(IContextQuery contextQueryLemmaTag) throws Exception {
        //TreeMap<String, Integer> mapVector = new TreeMap();
        // check weather the vector has been cashed before

        SimpleSparse spa = new SimpleSparse(); // I get a lot of error using the trove objetcs! :-(
      //  SparseVector spv = new 
        //System.out.println(expSet.getSparseVectorPresentations(contextQueryLemmaTag));
        File expVectorFile = new File(expSet.getSparseVectorPresentations(contextQueryLemmaTag, simWeightingType));

        if (!expVectorFile.exists()) {
            throw new Exception("Context vector not yet aggregated  .. future add calling to aggreg...");
        }
        BufferedReader br = new BufferedReader(new FileReader(expVectorFile));
        String line;
        while ((line = br.readLine()) != null) {
            if (line.trim().length() != 0) {
                String[] split = line.split(" : ");
                Integer key = getKey(split[0]);
                double value = Double.parseDouble(split[1]);
                spa.setValue(key, value);
            }
        }
        br.close();

        return spa;
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

    @Override
    public void run() {
        try {
            SimpleSparse vec1 = loadAggFreqContextFilesToMap(contextQueryLemmaTag1);
            SimpleSparse vec2 = loadAggFreqContextFilesToMap(contextQueryLemmaTag2);
            double cosineSimilarity = vec1.cosine(vec2);
            synchronized (writer) {
                writer.write(this.contextQueryLemmaTag1.getContextQueryFileIdentifier()
                        + " " + this.contextQueryLemmaTag2.getContextQueryFileIdentifier() + " " + cosineSimilarity
                        + "\n");
            }
            //System.out.println("--");
        } catch (Exception ex) {
            System.err.println(ex);
            Logger.getLogger(ComputeSimilarity.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

   

}
