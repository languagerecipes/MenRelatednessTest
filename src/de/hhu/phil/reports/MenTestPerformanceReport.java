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
package de.hhu.phil.reports;

import static de.phil.hhu.men.standard.processchains.StaticStepProcesses.processSimilarities;
import ie.pars.experiment.context.ExpSet;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import javax.swing.text.NumberFormatter;

/**
 *
 * @author Behrang QasemiZadeh <zadeh at phil.hhu.de>
 */
public class MenTestPerformanceReport {
    public static void main(String[] reportArgs) throws FileNotFoundException, IOException{
        String type = "pmi-new-estimator-pos-only";
        DecimalFormat nf = new DecimalFormat("#.#####");
        for (int i = 1; i < 8; i++) {
            
            ExpSet expSet = new ExpSet("15032015-", -1 * i, i);
            BufferedReader br = new BufferedReader(new FileReader(expSet.getFinalResultReport()));
            // PrintWriter pw = new PrintWriter(new FileWriter(expSet.getFinalResultReport(), true));
            //pw.println("simType " + simType + " SpearmansCorrelation: " + spearManCorrelation);
            //pw.close();

            String line;
            while ((line = br.readLine()) != null) {
                if (type.equals(line.split(" ")[1])) {
                    System.out.println(i + "\t" + i + "+" + i + "\t" + nf.format(Double.parseDouble(line.split(" ")[3])));
                }
            }
            br.close();
        }
    }
    
    
}
