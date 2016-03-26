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

import de.hhu.phil.men.process.ComputeSimilarity;
import de.hhu.phil.buildvectors.StanardContextVectorBuilder;
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

/**
 * Involved processes that can be run one by one and independently (as in
 * <cod>ProcessChain1Sequential.java</cod> and <code>ProcessChainModel2</code>
 *
 * @author Behrang QasemiZadeh <zadeh at phil.hhu.de>
 */
public class StaticStepProcesses {

    private final static int NUMBER_OF_THREAD_SERVER_INDEX = 40;
    private final static int NUMBER_OF_THREAD_SERVER_PROCESS = 40;

    public static void processFetch(ExpSet expSet) throws FileNotFoundException, Exception {
        System.out.println("Fectching results using NoSkE searcher ... ");
        Set<MenEntry> readMenData = MenUtils.readMenData(expSet.getMenFile());
        System.out.println("Dumping files at " + expSet.getRawContextFilesPath());
        Step1CollectFreqVectorBits s1 = new Step1CollectFreqVectorBits(expSet, NUMBER_OF_THREAD_SERVER_INDEX);
        for (MenEntry ent : readMenData) {
            ContextQueryLemmaTag contextQueryLemmaTag = new ContextQueryLemmaTag(ent.getLemma(), ent.getTag());
            List<FCRITInfo> fcritContextQueries = expSet.getFCRITContextQueries();
            for (FCRITInfo fci : fcritContextQueries) {
                s1.usingCMSFreqFiles(contextQueryLemmaTag, fci);
            }
        }

        s1.shutdownWait();
    }

    public static void processAggregiate(ExpSet expSet, String normSimWeightType) throws IOException, Exception {

        ExecutorService newFixedThreadPool = Executors.newFixedThreadPool(NUMBER_OF_THREAD_SERVER_PROCESS);

        Set<MenEntry> readMenData = MenUtils.readMenData(expSet.getMenFile());
        for (MenEntry me : readMenData) {
            ContextQueryLemmaTag contextQueryLemmaTag = new ContextQueryLemmaTag(me.getLemma(), me.getTag());
            StanardContextVectorBuilder scv = new StanardContextVectorBuilder(expSet, normSimWeightType, contextQueryLemmaTag);
            // scv.aggregateFreq2File();
            newFixedThreadPool.submit(scv);
        }

        newFixedThreadPool.shutdown();
        while (!newFixedThreadPool.awaitTermination(1, TimeUnit.SECONDS));
        System.out.println("Done fetching concrodance");
    }

    public static void processSimilarities(ExpSet expSet, String simType) throws IOException, Exception {
        
        ExecutorService newFixedThreadPool = Executors.newFixedThreadPool(NUMBER_OF_THREAD_SERVER_PROCESS);
        List<MenPair> readMenPair = MenUtils.readMenPair(expSet.getMenFile());
        File fout = new File(expSet.getSimilarityResultFile(simType));
        if (!fout.getParentFile().exists()) {
            fout.getParentFile().mkdirs();
        }
        PrintWriter pw = new PrintWriter(new FileWriter(fout));
        for (MenPair me : readMenPair) {
            ContextQueryLemmaTag cltx1 = new ContextQueryLemmaTag(me.getMenEntry1().getLemma(), me.getMenEntry1().getTag());
            ContextQueryLemmaTag cltx2 = new ContextQueryLemmaTag(me.getMenEntry2().getLemma(), me.getMenEntry2().getTag());
            ComputeSimilarity cs = new ComputeSimilarity(expSet, simType, cltx1, cltx2,
                    pw);
            newFixedThreadPool.submit(cs);
            // break;
        }
        newFixedThreadPool.shutdown();
        while (!newFixedThreadPool.awaitTermination(1, TimeUnit.SECONDS));
        pw.close();
        STEPCompareResults.compareResults(expSet, simType);
        expSet.printConfiguration();

    }
}
