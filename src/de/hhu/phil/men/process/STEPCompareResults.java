/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hhu.phil.men.process;

import de.phil.hhu.men.utils.MenUtils;
import de.phil.hhu.obj.MenPair;
import ie.pars.experiment.context.ExpSet;
import java.io.FileWriter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.List;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.apache.commons.math3.stat.correlation.SpearmansCorrelation;

/**
 *
 * @author bq
 */
public class STEPCompareResults {

    public static void main(String[] ss) throws IOException, Exception {

        if (ss.length != 4) {
            throw new Exception("Provide arguments for setting");
        }
        ExpSet expSet = new ExpSet(ss[0], Integer.parseInt(ss[1]), Integer.parseInt(ss[2]));
        // new STEP_4_CompareResults(expSet, ss[3]);
    }

    public static void compareResults(ExpSet expSet, String simType) throws IOException {

        List<MenPair> machineRanked = MenUtils.readMenPair(expSet.getSimilarityResultFile(simType));
        List<MenPair> humanRanked = MenUtils.readMenPair(expSet.getMenFile());

        SpearmansCorrelation spc = new SpearmansCorrelation();

        Collections.sort(machineRanked, MenPair.nameComparator());
        Collections.sort(humanRanked, MenPair.nameComparator());
        double spearManCorrelation = spc.correlation(toDoubleArray(machineRanked), toDoubleArray(humanRanked));

        PrintWriter pw = new PrintWriter(new FileWriter(expSet.getFinalResultReport(), true));
        pw.println("simType " + simType + " SpearmansCorrelation: " + spearManCorrelation);
        pw.close();

//        PearsonsCorrelation ps = new PearsonsCorrelation();
//        double pearson = ps.correlation(toDoubleArray(humanRanked), toDoubleArray(machineRanked));
//        System.out.println(pearsonRea);
    }

    private static double[] toDoubleArray(List<MenPair> resultSortedByName) {
        double[] simRes = new double[resultSortedByName.size()];
        for (int i = 0; i < simRes.length; i++) {
            simRes[i] = resultSortedByName.get(i).getSim();

        }
        return simRes;

    }

}
