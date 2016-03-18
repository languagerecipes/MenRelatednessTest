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
package de.phil.hhu.men.standard.processchains;

import de.hhu.phil.buildvectors.StanardContextVector;
import static de.phil.hhu.men.standard.processchains.StaticStepProcesses.processSimilarities;
import de.phil.hhu.men.utils.MenUtils;
import de.hhu.phil.men.process.STEPCompareResults;
import de.hhu.phil.buildvectors.Step1CollectFreq;
import de.phil.hhu.obj.MenEntry;
import de.phil.hhu.obj.MenPair;
import ie.pars.experiment.context.ContextQueryLemmaTag;
import ie.pars.experiment.context.ExpSet;
import ie.pars.experiment.context.FCRITInfo;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Compute men relatedness test for the whole process of fetching freq, 
 * building vectors and finally computing similarity.
 * 
 * For each i+i model (i being the size of context windows), 
 * this is  synchronized old fashion pipeline
 * @author Behrang QasemiZadeh <zadeh at phil.hhu.de>
 */
public class ProcessChainModel2 {

    // this must take about the same time 18 Minutes 
//  slightly faster // also have a period of useful computing
    public static void main(String[] s) throws FileNotFoundException, Exception {
        int theSecodn = 40;
        for (int i = 1; i < 2; i++) {

            ExpSet expSet = new ExpSet("t15032015-", -1 * i, i);
            String simType = "cos";
            StaticStepProcesses.processFetch(expSet);
            StaticStepProcesses.processAggregiate(expSet);
            StaticStepProcesses.processSimilarities(expSet,"cos");
            
        }
    }


}
