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
package de.hhu.phil.buildvectors.randomized;

import de.hhu.phil.buildvectors.ContextFrequencyLoader;
import de.phil.hhu.men.utils.MenUtils;
import de.phil.hhu.randomized.NormalRandomSimpleVector;
import ie.pars.clac.vector.SimpleSparse;
import ie.pars.clac.vector.SparseVector;
import ie.pars.experiment.context.ExpSet;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 *
 * @author Behrang QasemiZadeh <zadeh at phil.hhu.de>
 */
public class BuildRandomVectorsUnortho11 {

    NormalRandomSimpleVector randomVectorAffixGen;
    NormalRandomSimpleVector completeVector;
    NormalRandomSimpleVector randomVecBaseGen;
    NormalRandomSimpleVector randomVecBaseGen2;
    //TreeMap<String, SimpleSparse> prefixMap;
    TreeMap<String, SimpleSparse> affixMap;
    TreeMap<String, SimpleSparse> stemMap;
    private int totalDim = 1000;
    private int affixDim = 100; //100+100 each

    public BuildRandomVectorsUnortho11() {
        init();
    }

    // I do not need to save this but for the sake of debugging and replication do
    // all I need to save is the tree map for the begin and last
    private void init() {
        //  randomVectorAffixGen = new NormalRandomSimpleVector(0, 100, 2);
        randomVecBaseGen = new NormalRandomSimpleVector(0, 500, 2);
        randomVecBaseGen2 = new NormalRandomSimpleVector(0, 500, 10);
        //  completeVector = new NormalRandomSimpleVector(0, 1000, 2);
        //prefixMap = new TreeMap<>();

        affixMap = new TreeMap<>();
        stemMap = new TreeMap<>();
    }

//    private SimpleSparse getAffixVector(String hypoAffix) {
//        if (affixMap.containsKey(hypoAffix)) {
//            return affixMap.get(hypoAffix);
//        } else {
//            SimpleSparse randomVector = randomVectorAffixGen.getRandomVector();
//            affixMap.put(hypoAffix, randomVector);
//            return randomVector;
//        }
//
//    }
    private SimpleSparse getStemVector(String hypoAffix) {
        if (stemMap.containsKey(hypoAffix)) {
            return stemMap.get(hypoAffix);
        } else {
            SimpleSparse randomVector = randomVecBaseGen.getRandomVector();
            stemMap.put(hypoAffix, randomVector);
            return randomVector;
        }

    }
    

    public SimpleSparse getRandomVectorFF(String word) {
        SimpleSparse ss = new SimpleSparse();
        if (word.length() < ngramThreshold) {
            return randomVecBaseGen2.getRandomVector();
        }
        List<String> profile = getProfile(word, ngramThreshold);
        for (String p : profile) {
            ss.addVectorMultiplyFreq(getStemVector(p), 1.0 );
        }
        ss.addVectorMultiplyFreq(this.randomVecBaseGen.getRandomVector(), 1);
        //ss.normalizeToL2Unity();
        return ss;

    }

    protected List<String> getProfile(String s, int k) {
        ArrayList<String> r = new ArrayList<String>();
        String ngram;
        for (int i = 0; i < (s.length() - k + 1); i++) {
            ngram = s.substring(i, i + k);
            // int position;
            r.add(ngram);
        }
        return r;
    }

//    public SimpleSparse simplestMethod(String word) {
//        SimpleSparse ss = new SimpleSparse();
//        if (word.length() < 4) {
//            ss = completeVector.getRandomVector();
//        } else {
//            String hypoPref = word.substring(0, 2);
//            String hypoSffix = word.substring(word.length() - 2, word.length());
//            String hypoStem = word.substring(2, word.length() - 2);
//
//            SimpleSparse stemVector = getStemVector(hypoStem);
//            SimpleSparse affixVector = getAffixVector(hypoPref);// this can be improved drastically
//            SimpleSparse suffiVec = getAffixVector(hypoSffix);
//            SimpleSparse concatVector = concatVector(stemVector, affixVector);
//            ss = concatVector(concatVector, suffiVec);
//        }
//        return ss;
//
//    }
    private SimpleSparse concatVector(SimpleSparse simple1, SimpleSparse simple2) {
        int offsetDim = simple1.getDimensionality();
        SimpleSparse ssConcat = new SimpleSparse(simple1);
        for (int oldDim : simple2.getIndices()) {
            ssConcat.setValue(oldDim + offsetDim, simple2.getValue(oldDim));
        }
        return ssConcat;

    }
int ngramThreshold = 4;
    
    
    public static void main(String[] s) throws IOException, Exception {
//        BuildRandomVectorsUnortho11 ptc = new BuildRandomVectorsUnortho11();
//        SimpleSparse v1 = ptc.getRandomVectorFF("trump");
//         SimpleSparse v2 = ptc.getRandomVectorFF("trumpism");
//         System.out.println(v1.cosine(v2));
        
        BuildRandomVectorsUnortho11 ptc = new BuildRandomVectorsUnortho11();
        ExpSet expSetBase = new ExpSet("15032015-", 0, 0); // just to load freq profiles once
        PrintWriter pw = new PrintWriter(new FileWriter(new File(expSetBase.getRndVecStd(400, 4) + "test")));

        ContextFrequencyLoader cfl = new ContextFrequencyLoader(expSetBase); // cetainly this can go out of this lopp for this set of experiments .. that is when only context size changes!

        for (String context : cfl.getContextElements()) {
          //  if (cfl.getContextFrequency0(context) > 1) {
                pw.println(context + SimpleSparse.LABEL_DELIMIT + ptc.getRandomVectorFF(context).toStringShorten());
           // }
        }
        pw.close();
    }

    
    
  
}
