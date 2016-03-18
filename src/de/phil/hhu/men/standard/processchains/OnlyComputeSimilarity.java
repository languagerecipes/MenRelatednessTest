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
import de.phil.hhu.men.utils.MenUtils;
import de.hhu.phil.men.process.STEPCompareResults;
import de.phil.hhu.obj.MenPair;
import ie.pars.experiment.context.ContextQueryLemmaTag;
import ie.pars.experiment.context.ExpSet;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * The main method for processing of the given type of sim for the given
 * contextvectors and fcrits
 *
 * @author Behrang QasemiZadeh <zadeh at phil.hhu.de>
 */
public class OnlyComputeSimilarity {

    public static void main(String[] ss) throws IOException, Exception {
        for (int i = 1; i < 9; i++) {

            ExpSet expSet = new ExpSet("15032015-", -1 * i, 1 * i);
            String simType = "cos";
            //Writer w = new PrintWriter(System.out);
            int numebrOfThreads = 40;
            ExecutorService newFixedThreadPool = Executors.newFixedThreadPool(numebrOfThreads);
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
}
