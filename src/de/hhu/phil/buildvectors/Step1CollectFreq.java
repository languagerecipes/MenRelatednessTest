/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hhu.phil.buildvectors;

import de.phil.hhu.men.utils.MenUtils;
import de.phil.hhu.obj.MenEntry;
import ie.pars.experiment.context.ContextQueryLemmaTag;
import ie.pars.experiment.context.ExpSet;

import ie.pars.nlp.sketchengine.interactions.FreqSKEInteraction;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import ie.pars.experiment.context.ContextQuery;
import ie.pars.experiment.context.FCRITInfo;
import java.util.List;
import java.util.Set;

/**
 *
 * @author bq
 */
public class Step1CollectFreq {

    private final ExecutorService newFixedThreadPool;
    private final ExpSet expSet;

    public Step1CollectFreq(ExpSet expSet, int numebrOfThreads) {
        newFixedThreadPool = Executors.newFixedThreadPool(numebrOfThreads);
        this.expSet = expSet;
    }

    public static void main(String[] ss) throws FileNotFoundException, Exception {

        ExpSet expSet = new ExpSet("15032015-", -1, 1);
        Set<MenEntry> readMenData = MenUtils.readMenData(expSet.menFile);
        System.out.println("Fectching results using NoSkE searcher ... ");
        System.out.println("Dumping files at " + expSet.getRawContextFilesPath());
        Step1CollectFreq s1 = new Step1CollectFreq(expSet, 24);

        for (MenEntry ent : readMenData) {

            ContextQueryLemmaTag contextQueryLemmaTag = new ContextQueryLemmaTag(ent.getLemma(), ent.getTag());
            //System.out.println(contextQueryLemmaTag.toString());
            List<FCRITInfo> fcritContextQueries = expSet.getFCRITContextQueries();
            for (FCRITInfo fci : fcritContextQueries) {
                s1.usingCMSFreqFiles(contextQueryLemmaTag, fci);
            }
        }
        s1.shutdownWait();
    }

    public void usingCMSFreqFiles(ContextQuery cq, FCRITInfo fcritInfo) throws IOException, InterruptedException, Exception {
        //TreeMap<String, Writer> outputWriters = new TreeMap();
        File output = new File(expSet.getRawContextFilesName(cq, fcritInfo));
        if (!output.getParentFile().exists()) {
            output.getParentFile().mkdirs();
        }
        if (output.exists()) {
            // change this part for enabling the machine to build moels increamentally
            System.out.println("Skipped creating profile and file for " + output.getName());
        } else {

            //String contextQuery = expSet.getContextQuery(cq);
            FreqSKEInteraction freqSKEInteraction = new FreqSKEInteraction(
                    output,
                    expSet.baseURL,
                    expSet.runCGI,
                    expSet.corpus,
                    cq.getContextQuery(),
                    fcritInfo.getFcritQuery(),
                    0, // i.e., cut-off freq point set to 0
                    0, // i.e., no example ... probably remove it soon
                    true, // prse the json
                    true); // write eveyhing into one file

            this.newFixedThreadPool.submit(freqSKEInteraction);
            // perhaps some room to play with submit and synchronizing the write 
            // for the future

        }
    }

    public void shutdownWait() throws InterruptedException {
        this.newFixedThreadPool.shutdown();
        // I am not sure if I want to wait here decided later ... for the moment wait for the sever!
        while (!newFixedThreadPool.awaitTermination(1, TimeUnit.SECONDS));
    }

}
