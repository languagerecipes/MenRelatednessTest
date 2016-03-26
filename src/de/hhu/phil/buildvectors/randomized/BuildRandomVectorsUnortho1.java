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

import de.phil.hhu.men.utils.MenUtils;
import de.phil.hhu.randomized.NormalRandomSimpleVector;
import ie.pars.clac.vector.SimpleSparse;
import ie.pars.clac.vector.SparseVector;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 *
 * @author Behrang QasemiZadeh <zadeh at phil.hhu.de>
 */
public class BuildRandomVectorsUnortho1 {

    NormalRandomSimpleVector randomVectorAffixGen;
    NormalRandomSimpleVector completeVector;
    NormalRandomSimpleVector randomVecBaseGen;

    //TreeMap<String, SimpleSparse> prefixMap;
    TreeMap<String, SimpleSparse> affixMap;
    TreeMap<String, SimpleSparse> stemMap;
    private int totalDim = 1000;
    private int affixDim = 100; //100+100 each

    public BuildRandomVectorsUnortho1() {
        init();
    }

    
    // I do not need to save this but for the sake of debugging and replication do
    // all I need to save is the tree map for the begin and last
    private void init() {
        randomVectorAffixGen = new NormalRandomSimpleVector(0, 100, 2);
        randomVecBaseGen = new NormalRandomSimpleVector(0, 800, 2);
        completeVector = new NormalRandomSimpleVector(0, 1000, 2);
        //prefixMap = new TreeMap<>();

        affixMap = new TreeMap<>();
        stemMap = new TreeMap<>();
    }

    private SimpleSparse getAffixVector(String hypoAffix) {
        if (affixMap.containsKey(hypoAffix)) {
            return affixMap.get(hypoAffix);
        } else {
            SimpleSparse randomVector = randomVectorAffixGen.getRandomVector();
            affixMap.put(hypoAffix, randomVector);
            return randomVector;
        }

    }

    private SimpleSparse getStemVector(String hypoAffix) {
        if (stemMap.containsKey(hypoAffix)) {
            return stemMap.get(hypoAffix);
        } else {
            SimpleSparse randomVector = randomVecBaseGen.getRandomVector();
            stemMap.put(hypoAffix, randomVector);
            return randomVector;
        }

    }
    public SimpleSparse secondMethod(String word) {
        SimpleSparse ss = new SimpleSparse();
        if (word.length() < 2) {
            ss = completeVector.getRandomVector();
        } else {
            List<String> stringToBigram = stringToSomegram(word);
            for(String s: stringToBigram){
             ss.addVectorMultiplyFreq(getStemVector(s), 1.0*s.length());
            }
            
        }
        return ss;

    }

    
    public SimpleSparse simplestMethod(String word) {
        SimpleSparse ss = new SimpleSparse();
        if (word.length() < 4) {
            ss = completeVector.getRandomVector();
        } else {
            String hypoPref = word.substring(0, 2);
            String hypoSffix = word.substring(word.length() - 2, word.length());
            String hypoStem = word.substring(2, word.length() - 2);

            SimpleSparse stemVector = getStemVector(hypoStem);
            SimpleSparse affixVector = getAffixVector(hypoPref);// this can be improved drastically
            SimpleSparse suffiVec = getAffixVector(hypoSffix);
            SimpleSparse concatVector = concatVector(stemVector, affixVector);
            ss = concatVector(concatVector, suffiVec);
        }
        return ss;

    }

    private SimpleSparse concatVector(SimpleSparse simple1, SimpleSparse simple2) {
        int offsetDim = simple1.getDimensionality();
        SimpleSparse ssConcat = new SimpleSparse(simple1);
        for (int oldDim : simple2.getIndices()) {
            ssConcat.setValue(oldDim + offsetDim, simple2.getValue(oldDim));
        }
        return ssConcat;

    }

    public static void main(String[] s) throws IOException {
        TreeSet<String> loadItemList = MenUtils.loadItemList("word-list-test", 0, " : ");

       
            BuildRandomVectorsUnortho1 ptc = new BuildRandomVectorsUnortho1();
            
        SimpleSparse simplestMethod = ptc.simplestMethod("this");
        SimpleSparse simplestMethod1 = ptc.simplestMethod("that");
        SimpleSparse simplestMethod2 = ptc.simplestMethod("goodness");
        SimpleSparse simplestMethod3 = ptc.simplestMethod("unhappiness");
        System.out.println(simplestMethod);
        System.out.println(simplestMethod2);
        System.out.println(simplestMethod3);
        System.out.println(simplestMethod2.cosine(simplestMethod3));
        System.out.println(simplestMethod2.cosine(simplestMethod1));

        System.out.println("*" + ptc.secondMethod("unhappy").cosine(ptc.secondMethod("unity")));
        System.out.println("*" + ptc.secondMethod("unhappy").cosine(ptc.secondMethod("happy")));
        System.out.println("*" + ptc.secondMethod("unhappiness").cosine(ptc.secondMethod("happy")));
        
        SimpleSparse secondMethod = ptc.secondMethod("unhappiness");
        SimpleSparse secondMethod2 = ptc.secondMethod("happy");
        System.out.println(secondMethod.cosine(secondMethod2));
        secondMethod.normalizeToL2Unity();
        secondMethod2.normalizeToL2Unity();
        System.out.println(secondMethod.cosine(secondMethod2));
    }

    private List<String> stringToSomegram(String thisString) {
        List<String> bigrams = new ArrayList();
        int i;

        for (int j = 0; j < thisString.length(); j+=1) {
             bigrams.add(thisString.substring(0, j));
            System.out.print(thisString.subSequence(0, j)+" ");
            
        }
         for (int j = thisString.length()-1; j > 0; j-=1) {
             bigrams.add(thisString.substring(j,thisString.length()));
            System.out.print(thisString.substring(j,thisString.length())+" ");
            
        }
         System.out.println("");
//        for (i = 0; i < thisString.length() - 1; i++) {
//            bigrams.add(thisString.substring(i, i + 2));
//            System.out.print(thisString.subSequence(i, i + 2) + " ");
//
//        }
//        if (i < thisString.length()) {
//            bigrams.add(thisString.substring(i, thisString.length()));
//            System.out.println(thisString.substring(i, thisString.length()));
//        }
//
//        for (i = 0; i < thisString.length() - 2; i += 2) {
//            bigrams.add(thisString.substring(i, i + 3));
//            System.out.print(thisString.subSequence(i, i + 3) + " ");
//
//        }
//        if (i < thisString.length()) {
//            bigrams.add(thisString.substring(i, thisString.length()));
//            System.out.print(thisString.substring(i, thisString.length()));
//        }
//        for (i = 0; i < thisString.length() - 3; i += 3) {
//            bigrams.add(thisString.substring(i, i + 4));
//            System.out.print(thisString.subSequence(i, i + 4));
//
//        }
//        if (i < thisString.length()) {
//            bigrams.add(thisString.substring(i, thisString.length()));
//            System.out.println(thisString.substring(i, thisString.length()));
//        }

        return bigrams;
    }

//    private void getWordVector(String word){
//        if (word.length() <= 4) {
//            getAffixVector.
//        }
//        
//    }
}
