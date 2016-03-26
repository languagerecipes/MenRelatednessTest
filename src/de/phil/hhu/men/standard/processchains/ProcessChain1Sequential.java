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

import de.hhu.phil.buildvectors.StanardContextVectorBuilder;
import static de.phil.hhu.men.standard.processchains.StaticStepProcesses.processAggregiate;
import static de.phil.hhu.men.standard.processchains.StaticStepProcesses.processSimilarities;
import de.phil.hhu.men.utils.MenUtils;
import de.hhu.phil.men.process.STEPCompareResults;
import de.hhu.phil.buildvectors.Step1CollectFreqVectorBits;
import de.phil.hhu.obj.MenEntry;
import de.phil.hhu.obj.MenPair;
import ie.pars.experiment.context.ContextQueryLemmaTag;
import ie.pars.experiment.context.ExpSet;
import ie.pars.experiment.context.FCRITInfo;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import static de.phil.hhu.men.standard.processchains.StaticStepProcesses.processFetch;

/**
 *
 * @author Behrang QasemiZadeh <zadeh at phil.hhu.de>
 */
public class ProcessChain1Sequential {

    private final static int NUMBER_OF_THREAD_SERVER_INDEX = 40;

    public static void main(String[] ss) throws Exception {

        String normWightSimType = "l2Std";
        for (int i = 5; i < 7; i++) {

            ExpSet expSet = new ExpSet("15032015-", -1 * i, i);
            processFetch(expSet);
        }
//        for (int i = 1; 20 < 40; i++) {
//
//            ExpSet expSet = new ExpSet("15032015-", -1 * i, i);
//            processAggregiate(expSet,normWightSimType);
//        }
//        for (int i = 1; i < 20; i++) {
//
//            ExpSet expSet = new ExpSet("15032015-", -1 * i, i);
//            processSimilarities(expSet, normWightSimType);
//        }

    }

 
}
