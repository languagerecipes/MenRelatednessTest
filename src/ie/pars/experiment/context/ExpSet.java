/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ie.pars.experiment.context;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Behrang QasemiZadeh <zadeh at phil.hhu.de>
 */
public class ExpSet {

    public String menFile = "E:\\corpora\\MEN\\MEN_dataset_lemma_form_full";
    public String corpus = "ukwacdep";
    public String runCGI = "/bonito/run.cgi/";
    public String baseURL = "corpora.phil.hhu.de";

    private String ROOT_OF_ROOT = "E:\\corpora\\MEN\\expsets\\";
    private int contextWindowSizeBefore;
    private int contextWindwoSizeAfter;
    public String userDefinedIdentifier;
//    private String simMeasureType;

    public ExpSet(String userDefinedIdentifier, int contextWindowSizeBefore, int contextWindwoSizeAfter) {
        this.userDefinedIdentifier = userDefinedIdentifier;
        this.contextWindowSizeBefore = contextWindowSizeBefore;
        this.contextWindwoSizeAfter = contextWindwoSizeAfter;
    }

    public String getRoot() {
        return ROOT_OF_ROOT + this.getExpIdentifier() + File.separatorChar;

    }

    public void printConfiguration(File file) throws IOException {
        PrintWriter pw = new PrintWriter(new FileWriter(file));

        pw.println("=== Input Dataset ===");
        pw.println(menFile);
        pw.println();

        pw.println("=== Input Source Settings ===");
        pw.println("Corpus: " + corpus);
        pw.println("Websource: " + baseURL + runCGI);
        pw.println();

        pw.println("=== Path to Raw Context Files ===");
        pw.println(this.getRawContextFiles());
        pw.println();

        pw.println("=== Path to Vector Presentation Files ===");
        pw.println(this.getSparseVectorPresentations());
        pw.println();

        pw.println("=== FCRIT Queries ===");
        for (String fcritQuery : this.getFCRITContextQueries()) {
            pw.println(fcritQuery);
        }

        pw.close();

    }

    public List<String> getFCRITContextQueries() {
        List<String> simpleFCRITQueryList = new ArrayList();
        for (int i = 0; i < contextWindwoSizeAfter; i++) {
            simpleFCRITQueryList.add("lemma/ " + (i + 1));

        }
        for (int i = 0; i > contextWindowSizeBefore; i--) {
            simpleFCRITQueryList.add("lemma/ " + (i - 1));

        }
        return simpleFCRITQueryList;

    }

    public String getExpIdentifier() {
        return userDefinedIdentifier + "-" + contextWindowSizeBefore + "-" + contextWindwoSizeAfter + "-" + corpus;
    }

    public String getSimilarityResultFile(String simMeasureType) {
        return getRoot() + simMeasureType + "-vsm.dat." + getExpIdentifier();
    }

    public String getSparseVectorPresentations() {
        return getRoot() + "context-vectors\\vectors." + getExpIdentifier();
    }

    public String getRawContextFiles() {
        return getRoot() + "raw-context-freq-files\\";
    }
    
    public String getFinalResultReport(){
        return getRoot() + "final-result-report.txt";
    }

}
