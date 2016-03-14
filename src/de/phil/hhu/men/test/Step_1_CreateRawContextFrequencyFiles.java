/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.phil.hhu.men.test;

import ie.pars.experiment.context.ExpSet;

import ie.pars.nlp.sketchengine.interactions.FreqSKEInteraction;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author bq
 */
public class Step_1_CreateRawContextFrequencyFiles {

    public static void main(String[] ss) throws FileNotFoundException, Exception {
        
        if (ss.length != 3) {
            throw new Exception("Provide arguments for setting");
        }
        ExpSet expSet = new ExpSet(ss[0], Integer.parseInt(ss[1]), Integer.parseInt(ss[2]));
       new  Step_1_CreateRawContextFrequencyFiles(expSet);
    }

    public Step_1_CreateRawContextFrequencyFiles(ExpSet expSet) throws IOException, InterruptedException, Exception {
        TreeMap<String, Writer> outputWriters = new TreeMap();
        TreeSet<String> readMenData = readMenData(expSet.menFile);
        
        System.out.println("Fectching results using NoSkE searcher ... ");
        System.out.println("Dumping files at " + expSet.getRawContextFiles());
        ExecutorService newFixedThreadPool = Executors.newFixedThreadPool(48);
        for (String ent : readMenData) {
            String[] split = ent.split("-");
            String cqlQuery = "[lemma=\"" + split[0] + "\" & tag=\"" + split[1].toUpperCase() + ".*\"]";
            System.out.println("Q: " + split[0] + " " + split[1]);
            File output = new File(expSet.getRawContextFiles() + ent);
            if (!output.getParentFile().exists()) {
                output.getParentFile().mkdirs();
            }

            if (output.exists()) {
                // change this part for enabling the machine to build moels increamentally
                System.out.println("Skipped creating concordance and file " + output.getName());
            } else {
                Writer writer = null;
                if (outputWriters.containsKey(output.getName())) {
                    writer = outputWriters.get(output.getName());
                } else {
                    OutputStreamWriter outputStreamWriter
                            //append to the end of file
                            = new OutputStreamWriter(new FileOutputStream(output, true),
                                    StandardCharsets.UTF_8);
                    outputWriters.put(output.getName(), outputStreamWriter);
                    writer = outputStreamWriter;
                }

                for (String fcrit : expSet.getFCRITContextQueries()) {
                    Future<?> submit = newFixedThreadPool.submit(new FreqSKEInteraction(
                            writer,
                            expSet.baseURL,
                            expSet.runCGI,
                            expSet.corpus,
                            cqlQuery,
                            fcrit,
                            0, // i.e., cut-off freq point set to 0 
                            0, // i.e., no example ... probably remove it soon
                            true, // prse the json
                            true) // write eveyhing into one file
                    );
                    // perhaps some room to play with submit and synchronizing the write 
                    // for the future

                }
            }
        }
        newFixedThreadPool.shutdown();
        while (!newFixedThreadPool.awaitTermination(1, TimeUnit.SECONDS));
        // newFixedThreadPool.
        //   newFixedThreadPool.awaitTermination(1, TimeUnit.MINUTES);

        for (Writer w : outputWriters.values()) {
            w.close();
        }
    }

    private static TreeSet<String> readMenData(String file) throws IOException {
        TreeSet<String> menEntry = new TreeSet();
        BufferedReader br = new BufferedReader(new FileReader(new File(file)));
        String line = null;
        while ((line = br.readLine()) != null) {
            menEntry.add(line.split(" ")[0]);
            menEntry.add(line.split(" ")[1]);
        }
        return menEntry;
    }

}
