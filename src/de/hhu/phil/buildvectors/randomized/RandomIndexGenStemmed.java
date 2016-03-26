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
import de.phil.hhu.men.utils.Stemmer;
import de.phil.hhu.randomized.NormalRandomSimpleVector;
import ie.pars.clac.vector.SimpleSparse;
import ie.pars.experiment.context.ExpSet;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.TreeMap;

/**
 *
 * @author Behrang QasemiZadeh <zadeh at phil.hhu.de>
 */
public class RandomIndexGenStemmed {

    NormalRandomSimpleVector randomVecBaseGen;
    NormalRandomSimpleVector randomVectorSuffix;
    //TreeMap<String, SimpleSparse> prefixMap;
    TreeMap<String, SimpleSparse> affixMap;
    TreeMap<String, SimpleSparse> stemMap;
//    private int totalDim = 1000;
//    private int affixDim = 100; //100+100 each

    Stemmer stem;

    public RandomIndexGenStemmed() {
        init();
        stem = new Stemmer();

    }

    public final static String NO_SUFFIX = "NO_SUFFIX";

    // I do not need to save this but for the sake of debugging and replication do
    // all I need to save is the tree map for the begin and last
    private void init() {
        //  randomVectorAffixGen = new NormalRandomSimpleVector(0, 100, 2);
        randomVecBaseGen = new NormalRandomSimpleVector(0, 400, 12);
        randomVectorSuffix = new NormalRandomSimpleVector(0, 200, 2);
        SimpleSparse randomVectorForNoSuffix =  new SimpleSparse();// randomVectorSuffix.getRandomVector();

        //  completeVector = new NormalRandomSimpleVector(0, 1000, 2);
        //prefixMap = new TreeMap<>();
        affixMap = new TreeMap<>();
        affixMap.put(NO_SUFFIX, randomVectorForNoSuffix);
        stemMap = new TreeMap<>();

    }

    private SimpleSparse concatVector(SimpleSparse simple1, SimpleSparse simple2) {
        int offsetDim = simple1.getDimensionality();
        SimpleSparse ssConcat = new SimpleSparse(simple1);
        for (int oldDim : simple2.getIndices()) {
            ssConcat.setValue(oldDim + offsetDim, simple2.getValue(oldDim));
        }
        return ssConcat;

    }

    private SimpleSparse getStemVector(String base) {
        if (stemMap.containsKey(base)) {
            return stemMap.get(base);
        } else {
            SimpleSparse randomVector = randomVecBaseGen.getRandomVector();
            stemMap.put(base, randomVector);
            return randomVector;
        }

    }

    private SimpleSparse getAffixVector(String hypoAffix) {
        if (affixMap.containsKey(hypoAffix)) {
            return affixMap.get(hypoAffix);
        } else {
            SimpleSparse randomVector = randomVectorSuffix.getRandomVector();
            affixMap.put(hypoAffix, randomVector);
            return randomVector;
        }

    }

    public SimpleSparse getRandomVectorFF(String word) {
        stem.add(word);
        stem.stem();
        String base = stem.toString();
       
        String suffix = word.substring(stem.getI_end());
        SimpleSparse suffixVector;
        if (suffix.isEmpty()) {
            suffixVector = getAffixVector(NO_SUFFIX);
        } else {
            suffixVector = getAffixVector(suffix);
        }
        

        SimpleSparse stemVector = getStemVector(base);
        

        SimpleSparse concatVector = concatVector(stemVector, suffixVector);
        //ss.normalizeToL2Unity();
        return concatVector;

    }

    public static void main(String[] ss) throws IOException, Exception {

        RandomIndexGenStemmed risg = new RandomIndexGenStemmed();
        SimpleSparse randomVectorFF = risg.getRandomVectorFF("entertainment");
        SimpleSparse randomVectorFF2 = risg.getRandomVectorFF("entertain");
        System.out.println(randomVectorFF);
        System.out.println(randomVectorFF2);
        System.out.println(randomVectorFF.cosine(randomVectorFF2));


//        ExpSet expSetBase = new ExpSet("15032015-", 0, 0); // just to load freq profiles once
//        PrintWriter pw = new PrintWriter(new FileWriter(new File(expSetBase.getRndVecStd(500, 6) + "sa")));
////
//        ContextFrequencyLoader cfl = new ContextFrequencyLoader(expSetBase); // cetainly this can go out of this lopp for this set of experiments .. that is when only context size changes!
//        int i = 0;
//        for (String context : cfl.getContextElements()) {
//
////         //  if (cfl.getContextFrequency0(context) > 1) {
//            pw.println(context + SimpleSparse.LABEL_DELIMIT + risg.getRandomVectorFF(context).toStringShorten());
//        //}
//        }
//        pw.close();
    }
}
