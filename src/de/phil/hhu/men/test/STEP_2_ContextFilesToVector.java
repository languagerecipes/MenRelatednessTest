/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.phil.hhu.men.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.TreeMap;
import ie.pars.experiment.context.ExpSet;
import ie.pars.noske.parse.obj.FrequencyLine;
import ie.pars.utils.MapUtils;

/**
 *
 * @author bq
 */
public class STEP_2_ContextFilesToVector {

    public static void main(String[] ss) throws Exception {
        if (ss.length != 3) {
            throw new Exception("Provide arguments for setting");
        }
        ExpSet expSet = new ExpSet(ss[0], Integer.parseInt(ss[1]), Integer.parseInt(ss[2]));
        STEP_2_ContextFilesToVector step2 = new STEP_2_ContextFilesToVector(expSet);
    }

    public STEP_2_ContextFilesToVector(ExpSet expSet) throws Exception {
        File expVectorFile = new File(expSet.getSparseVectorPresentations());
        if (!expVectorFile.getParentFile().exists()) {
            expVectorFile.getParentFile().mkdirs();
        }
        PrintWriter pw = new PrintWriter(new FileWriter(expVectorFile));
        File direct = new File(expSet.getRawContextFiles());
        for (File f : direct.listFiles()) {
            if (f.isFile()) {

                double countall = 0.0;
                String target = f.getName();
                System.out.print(" * " + target + ": ");
                BufferedReader br = new BufferedReader(new FileReader(f));
                TreeMap<String, Integer> mapVector = new TreeMap();
                String line = null;
                while ((line = br.readLine()) != null) {
                    if (line.trim().length() != 0) {
                        FrequencyLine fl;
                        fl = FrequencyLine.fromFileLine(line);
                        countall += fl.getFreq();
                        MapUtils.updateMap(mapVector, fl.getContext(), fl.getFreq());
                    }
                }
                System.out.println(mapVector.size() + " count all freq: " + (countall / 6));
                if (countall % 6 != 0) {
                    System.err.println(f.getName());
                }
                StringBuilder sb = new StringBuilder();
                for (String key : mapVector.keySet()) {
                    sb.append("\t").append(key).append(" : ").append(mapVector.get(key));
                }
                pw.println(target + sb.toString());
                br.close();
                //break;
            }
        }
        pw.close();
    }

}
