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
import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author Behrang QasemiZadeh <zadeh at phil.hhu.de>
 */
public class RandomIndexFileReader {

    private IndexedVectorFileReader ivfr;
    private Map<String, SimpleSparse> mapSimpleSparse;
    private boolean inMemory;
    public RandomIndexFileReader(String file, boolean inMemory) throws IOException {

        this.inMemory  = inMemory;
        ivfr = new IndexedVectorFileReader(new File(file), SimpleSparse.LABEL_DELIMIT);
        System.out.println("loaded index for " + ivfr.getLineCount() + " vectors");
        if (inMemory) {
            mapSimpleSparse = new TreeMap<>();
            for (String s : ivfr.entrySet()) {
                SimpleSparse v = getVectorFromFile(s);
                mapSimpleSparse.put(s, v);
            }

            this.ivfr.close();
        }
        //LinkedHashMap<String,SparseVector> cache  ; future
    }

    public SimpleSparse getVector(String contextKey) throws IOException {
        if (inMemory) {

            if (this.mapSimpleSparse.containsKey(contextKey)) {
                return mapSimpleSparse.get(contextKey);
            } else {
                //empty vec
                return new SimpleSparse();
            }
        }else  if (ivfr.hasEntry(contextKey)) {
           return getVectorFromFile(contextKey);
        } else {
            return new SimpleSparse(); // return an emptye vec;
        }
    }
    
    private SimpleSparse getVectorFromFile(String contextKey) throws IOException {
        if(ivfr.hasEntry(contextKey)){
        String readLine = ivfr.readLine(contextKey);

          //  System.out.println(readLine);
       if(readLine.split(SimpleSparse.LABEL_DELIMIT).length<2 || readLine.split(SimpleSparse.LABEL_DELIMIT)[1].trim().isEmpty()){
           System.out.println(readLine);
            System.out.println("fic " + contextKey);
        }
        SimpleSparse sv = SimpleSparse.fromString(readLine.split(SimpleSparse.LABEL_DELIMIT)[1]);
        sv.setLabel(readLine.split(SimpleSparse.LABEL_DELIMIT)[0]);
        
        return sv;}else{
            return new SimpleSparse(); // this is to be changed
        }
    }

    public static void main(String[] test) throws IOException {
        MenUtils.getRamReport();
        ExpSet expSetBase = new ExpSet("15032015-", 0, 0); // just to load freq profiles once
        RandomIndexFileReader rc = new RandomIndexFileReader(expSetBase.getRndVecStd(400, 4)+"test", false);
        System.out.println(rc.getVector("this"));
        //MenUtils.getRamReport();
        for(String j: rc.ivfr.entrySet()){
            System.err.println(j );
            rc.getVector(j);
        }
    }

    @Override
    protected void finalize() throws Throwable {
       super.finalize();
        this.ivfr.close();
        
    }
    
    

}
