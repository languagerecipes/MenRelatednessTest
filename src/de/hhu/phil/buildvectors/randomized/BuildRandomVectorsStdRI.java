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
import de.phil.hhu.randomized.NormalRandomSimpleVector;
import ie.pars.clac.vector.SimpleSparse;
import ie.pars.experiment.context.ExpSet;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

/**
 *
 * @author Behrang QasemiZadeh <zadeh at phil.hhu.de>
 */
public class BuildRandomVectorsStdRI {

    public static void main(String[] ss) throws Exception {
        ExpSet expSetBase = new ExpSet("15032015-", 0, 0); // just to load freq profiles once
        ContextFrequencyLoader cfl = new ContextFrequencyLoader(expSetBase); // cetainly this can go out of this lopp for this set of experiments .. that is when only context size changes!
        NormalRandomSimpleVector rsvp = new NormalRandomSimpleVector(0, 400, 4);
        PrintWriter pw = new PrintWriter(new FileWriter(new File(expSetBase.getRndVecStd(400, 4))));
        for (String context : cfl.getContextElements()) {
            pw.println(context + SimpleSparse.LABEL_DELIMIT + rsvp.getRandomVector().toString());
            
        }
        pw.close();
    }
    
}
