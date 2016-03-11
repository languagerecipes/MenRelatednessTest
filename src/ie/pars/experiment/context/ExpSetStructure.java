/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ie.pars.experiment.context;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Behrang QasemiZadeh <zadeh at phil.hhu.de>
 */
public class ExpSetStructure {

    public final static int CONTEXT_WINDOW_SIZE_AFTER = 3;
    public final static int CONTEXT_WINDOW_SIZE_BEFORE = -3;
    public static String corpus = "corpusname";
    public static String runCGI = "run.cgi";
    public static String baseURL = "baseurl";

    public static String outputPath = "wherehever";
    public static String menFile = "whereeverinput file is";


    /**
     * Main method for building the context and counting frequencies
     *
     * @return
     */
    public static List<String> getFCRITContextQueries() {
        List<String> simpleFCRITQueryList = new ArrayList();
        for (int i = 1; i <= CONTEXT_WINDOW_SIZE_AFTER; i++) {
            simpleFCRITQueryList.add("lemma/ " + i);

        }
        for (int i = (CONTEXT_WINDOW_SIZE_BEFORE + 1); i < 0; i++) {
            simpleFCRITQueryList.add("lemma/ " + i);

        }
        return simpleFCRITQueryList;

    }
}
