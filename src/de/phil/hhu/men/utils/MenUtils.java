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
package de.phil.hhu.men.utils;

import de.phil.hhu.obj.MenEntry;
import de.phil.hhu.obj.MenPair;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 *
 * @author Behrang QasemiZadeh <zadeh at phil.hhu.de>
 */
public class MenUtils {
  
    public static Set<MenEntry> readMenData(String file) throws IOException {
        Set<MenEntry> menEntry = new TreeSet();
        BufferedReader br = new BufferedReader(new FileReader(new File(file)));
        String line = null;
        while ((line = br.readLine()) != null) {
            menEntry.add(new MenEntry(line.split(" ")[0].split("-")[0], line.split(" ")[0].split("-")[1]));
            menEntry.add(new MenEntry(line.split(" ")[1].split("-")[0], line.split(" ")[1].split("-")[1]));
        }
        return menEntry;
    }
    
    public static int countContextFileFreq(File f) throws IOException{
        BufferedReader bf = new BufferedReader(new FileReader(f));
        String line;
        int count = 0;
        while((line=bf.readLine())!=null){
            int parseInt = Integer.parseInt(line.split(" : ")[1]);
            count+=parseInt;
        }
        return count;
    }
    

//      private List<String[]> readMenPair(String file) throws IOException {
//        List<String[]> menEntry = new ArrayList();
//        BufferedReader br = new BufferedReader(new FileReader(new File(file)));
//        String line = null;
//        while ((line = br.readLine()) != null) {
//            String bit[] = new String[2];
//            bit[0] = line.split(" ")[0];
//            bit[1] = line.split(" ")[1];
//            menEntry.add(bit);
//        }
//        br.close();
//        return menEntry;
//
//    }
      
    public static List<MenPair> readMenPair(String file) throws IOException {
        List<MenPair> menEntry = new ArrayList();
        BufferedReader br = new BufferedReader(new FileReader(new File(file)));
        String line = null;

        while ((line = br.readLine()) != null) {

            String bit[] = new String[2];
            bit[0] = line.split(" ")[0];
            bit[1] = line.split(" ")[1];
            MenPair m = new MenPair(bit[0], bit[1]);
            double parseDouble = Double.parseDouble(line.split(" ")[2]);
            m.setSim(parseDouble);
            menEntry.add(m);
        }
        br.close();
        return menEntry;

    }
}
