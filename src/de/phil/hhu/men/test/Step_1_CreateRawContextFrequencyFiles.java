/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.phil.hhu.men.test;

import ie.pars.experiment.context.ExpSet1;
import ie.pars.nlp.sketchengine.interactions.FreqSKEInteraction;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

/**
 *
 * @author bq
 */
public class Step_1_CreateRawContextFrequencyFiles {

    
    public static void main(String[] ss) throws IOException, ParserConfigurationException, SAXException, Exception {
        TreeSet<String> readMenData = readMenData(ExpSet1.menFile);
        System.out.println("Fectching results using NoSkE searcher ... ");
        ExecutorService newFixedThreadPool = Executors.newFixedThreadPool(24);
        for (String ent : readMenData) {
            String[] split = ent.split("-");
            String cqlQuery = "[lemma=\"" + split[0] + "\" & tag=\"" + split[1].toUpperCase() + ".*\"]";
            System.out.println("Q: " + split[0] + " " + split[1]);
            File output = new File(ExpSet1.outputPath + ent + "-" + ExpSet1.corpus);
            
            if (output.exists()) {
                System.out.println("Skipped creating concordance and file " + output.getName());
            } else {
                for(String fcrit: ExpSet1.getFCRITContextQueries()){
                    newFixedThreadPool.submit(new FreqSKEInteraction(
                            output,
                            ExpSet1.baseURL,
                            ExpSet1.runCGI,
                            ExpSet1.corpus,
                            cqlQuery,
                            fcrit,
                            0, // i.e., cut-off freq point set to 0 
                            0, // i.e., no example ... probably remove it soon
                            true, // prse the json
                            true) // write eveyhing into one file
                    );
                }
            }
        }
        
        newFixedThreadPool.shutdown();
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
