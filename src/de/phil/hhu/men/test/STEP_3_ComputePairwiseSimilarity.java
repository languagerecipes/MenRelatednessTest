/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.phil.hhu.men.test;

import ie.pars.clac.vector.SparseVector;
import ie.pars.experiment.context.ExpSet;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 *
 * @author bq
 */
public class STEP_3_ComputePairwiseSimilarity {

    TreeMap<String, Integer> strLingToIntIndex;
    TreeMap<String, SparseVector> entityVectorMap;
    List<SparseVector> entityVectorList;
 
    
    public static void main(String[] ss) throws FileNotFoundException, IOException, Exception {

         if (ss.length != 4) {
            throw new Exception("Provide arguments for setting");
        }
        ExpSet expSet = new ExpSet(ss[0], Integer.parseInt(ss[1]), Integer.parseInt(ss[2]));
        STEP_3_ComputePairwiseSimilarity sp3 = new STEP_3_ComputePairwiseSimilarity(expSet, ss[3]);

    }

    public  STEP_3_ComputePairwiseSimilarity(ExpSet expSet, String simType) throws Exception {

        
        strLingToIntIndex = new TreeMap();
        entityVectorMap = new TreeMap<>();
        
        BufferedReader br = new BufferedReader(new FileReader(new File(expSet.getSparseVectorPresentations())));
        String line;
        while ((line = br.readLine()) != null) {
            String[] vectorBits = line.split("\t");
            SparseVector sp = new SparseVector();
            sp.setLabel(vectorBits[0]);
            for (int i = 1; i < vectorBits.length; i++) {
                
                String[] keyValue = vectorBits[i].split(" : ");
                Integer key = getKey(keyValue[0]);

                sp.put(key, Double.parseDouble(keyValue[1]));

            }
            entityVectorMap.put(sp.getLabel(), sp);

        }

        br.close();
        String menFile = expSet.menFile;

        List<String[]> readMenPair = readMenPair(menFile);

        PrintWriter pw = new PrintWriter(new FileWriter(expSet.getSimilarityResultFile(simType)));

        for (String[] pair : readMenPair) {

            System.out.println("Pair: " + pair[0] + " and " + pair[1]);
            SparseVector pair1Vec = this.entityVectorMap.get(pair[0]);
            SparseVector pair2Vec = this.entityVectorMap.get(pair[1]);
            double cosineSimHP = pair1Vec.getCosineSimHP(pair2Vec);
            pw.println(pair[0] + " " + pair[1] + " " + cosineSimHP);
        }
        pw.close();

    }

    private Integer getKey(String element) {
        if (this.strLingToIntIndex.containsKey(element)) {
            return strLingToIntIndex.get(element);
        } else {
            int key = strLingToIntIndex.size();
            key++;
            strLingToIntIndex.put(element, key);
            return key;
        }
    }

    private static List<String[]> readMenPair(String file) throws IOException {
        List<String[]> menEntry = new ArrayList();
        BufferedReader br = new BufferedReader(new FileReader(new File(file)));
        String line = null;

        while ((line = br.readLine()) != null) {
            String bit[] = new String[2];
            bit[0] = line.split(" ")[0];
            bit[1] = line.split(" ")[1];
            menEntry.add(bit);
        }
        br.close();
        return menEntry;

    }

   
}
