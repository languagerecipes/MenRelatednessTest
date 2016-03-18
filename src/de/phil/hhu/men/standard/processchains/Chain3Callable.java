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
import de.hhu.phil.buildvectors.StanardContextVector;
import de.phil.hhu.men.standard.processchains.ProcessChainModel3;
import de.phil.hhu.men.utils.MenUtils;
import de.hhu.phil.men.process.STEPCompareResults;
import de.phil.hhu.obj.MenEntry;
import de.phil.hhu.obj.MenPair;
import ie.pars.experiment.context.ContextQueryLemmaTag;
import ie.pars.experiment.context.ExpSet;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * this one took 18 minutes
 * @author Behrang QasemiZadeh <zadeh at phil.hhu.de>
 */
public class Chain3Callable implements Runnable {

    private int theSecodn;
    private ExpSet expSet;
    private String simType;

    public Chain3Callable(int theSecodn, ExpSet expSet, String simType) {
        this.theSecodn = theSecodn;
        this.expSet = expSet;
        this.simType = simType;

    }

    /**
     *
     * @param theSecodn
     * @param expSet
     * @param simType
     */
    public void secondAndThreadChainParallel() {
        try {
            ExecutorService theSecondStep = Executors.newFixedThreadPool(theSecodn);
            Set<MenEntry> readMenData = MenUtils.readMenData(expSet.menFile);
            for (MenEntry me : readMenData) {
                ContextQueryLemmaTag contextQueryLemmaTag = new ContextQueryLemmaTag(me.getLemma(), me.getTag());
                StanardContextVector scv = new StanardContextVector(expSet, contextQueryLemmaTag);
                // scv.aggregateFreq2File();
                theSecondStep.submit(scv);
            }
            theSecondStep.shutdown();
            while (!theSecondStep.awaitTermination(1, TimeUnit.SECONDS));
            System.out.println("Done");
            ExecutorService execSim = Executors.newFixedThreadPool(20);
            List<MenPair> readMenPair = MenUtils.readMenPair(expSet.menFile);
            File fout = new File(expSet.getSimilarityResultFile(simType));
            if (!fout.getParentFile().exists()) {
                fout.getParentFile().mkdirs();
            }
            PrintWriter pw = new PrintWriter(new FileWriter(fout));

            for (MenPair me : readMenPair) {
                ContextQueryLemmaTag cltx1 = new ContextQueryLemmaTag(me.gerMenEntry1().getLemma(), me.gerMenEntry1().getTag());
                ContextQueryLemmaTag cltx2 = new ContextQueryLemmaTag(me.gerMenEntry2().getLemma(), me.gerMenEntry2().getTag());
                ComputeSimilarity cs = new ComputeSimilarity(expSet, cltx1, cltx2,
                        pw);
                execSim.submit(cs);
                // break;
            }
            execSim.shutdown();
            while (!execSim.awaitTermination(1, TimeUnit.SECONDS));
            pw.close();
            STEPCompareResults.compareResults(expSet, simType);
            expSet.printConfiguration();
        } catch (IOException ex) {
            System.err.println(ex);
            Logger.getLogger(ProcessChainModel3.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            System.err.println(ex);
            Logger.getLogger(ProcessChainModel3.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            System.err.println(ex);
            Logger.getLogger(ProcessChainModel3.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public void run() {
        secondAndThreadChainParallel();
    }
}
