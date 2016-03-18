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
 * took 18 minutes
 * @author Behrang QasemiZadeh <zadeh at phil.hhu.de>
 */
public class ProcessChainModel3 {

    static ExecutorService experimentComputeChain;
    public static void main(String[] s) throws FileNotFoundException, Exception {
       experimentComputeChain  = Executors.newFixedThreadPool(10);
        
        int theSecodn = 20;

        for (int i = 1; i < 110; i++) {

            ExpSet expSet = new ExpSet("15032015-", -1 * i, i);
            String simType = "cos";
            Set<MenEntry> readMenData = MenUtils.readMenData(expSet.menFile);
            System.out.println("Fectching results using NoSkE searcher ... ");
            System.out.println("Dumping files at " + expSet.getRawContextFilesPath());
            Step1CollectFreq s1 = new Step1CollectFreq(expSet, 40);
            for (MenEntry ent : readMenData) {
                ContextQueryLemmaTag contextQueryLemmaTag = new ContextQueryLemmaTag(ent.getLemma(), ent.getTag());
                List<FCRITInfo> fcritContextQueries = expSet.getFCRITContextQueries();
                for (FCRITInfo fci : fcritContextQueries) {
                    s1.usingCMSFreqFiles(contextQueryLemmaTag, fci);
                }
            }

            s1.shutdownWait();
            experimentComputeChain.submit(new Chain3Callable(theSecodn, expSet, simType));
     
        }
        experimentComputeChain.shutdown();
        

    }


}
