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
import ie.pars.clac.vector.IndexedVectorFileReader;
import ie.pars.clac.vector.SimpleSparse;

import ie.pars.experiment.context.ExpSet;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author Behrang QasemiZadeh <zadeh at phil.hhu.de>
 */
public class RandomIndexFileReader2 {

    
    private Map<String, SimpleSparse> mapSimpleSparse;
    
    public RandomIndexFileReader2(String file) throws IOException {

       BufferedReader br = new BufferedReader(new FileReader(file));
       String line;
       mapSimpleSparse = new TreeMap<>();
        while ((line = br.readLine()) != null) {

            String label = line.split(SimpleSparse.LABEL_DELIMIT)[0];
            String vec= line.split(SimpleSparse.LABEL_DELIMIT)[1];
            
                SimpleSparse v = SimpleSparse.fromString(vec);
                mapSimpleSparse.put(label, v);
       }
        br.close();
        //LinkedHashMap<String,SparseVector> cache  ; future
    }

    public SimpleSparse getVector(String contextKey) throws IOException {
       

            if (this.mapSimpleSparse.containsKey(contextKey)) {
                return mapSimpleSparse.get(contextKey);
            } else {
                //empty vec
                return new SimpleSparse();
            }
       
    }
    
    

    public static void main(String[] test) throws IOException {
        MenUtils.getRamReport();
        ExpSet expSetBase = new ExpSet("15032015-", 0, 0); // just to load freq profiles once
        RandomIndexFileReader2 rc = new RandomIndexFileReader2(expSetBase.getRndVecStd(400, 4)+"test");
        System.out.println(rc.getVector("this"));
        //MenUtils.getRamReport();
        System.out.println(rc.mapSimpleSparse.size());
       
    }

    public Map<String, SimpleSparse> getMapSimpleSparse() {
        return mapSimpleSparse;
    }


    
    

}
