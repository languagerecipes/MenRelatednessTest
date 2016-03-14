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
package de.phil.hhu.men.test;

import ie.pars.experiment.context.ExpSet;
import java.io.File;
import javax.xml.parsers.ParserConfigurationException;

/**
 *
 * @author Behrang QasemiZadeh <zadeh at phil.hhu.de>
 */
public class ProcessChain {
    
    public static void main(String[] sugar) throws Exception {

        if (sugar.length != 5) {
            throw new Exception("Provide arguments for setting");
        }
        ExpSet expSet = new ExpSet(sugar[0], Integer.parseInt(sugar[1]), Integer.parseInt(sugar[2]));

        new Step_1_CreateRawContextFrequencyFiles(expSet);
        new STEP_2_ContextFilesToVector(expSet);
        new STEP_3_ComputePairwiseSimilarity(expSet, "cos");
        new STEP_4_CompareResults(expSet, "cos");

        expSet.printConfiguration(new File(expSet.getRoot() + sugar[4]));

    }
}
