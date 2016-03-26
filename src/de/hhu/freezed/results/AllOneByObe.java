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
package de.hhu.freezed.results;

import de.hhu.phil.buildvectors.Estim2New2PMIWeightedStanardContextVectorBuilder;
import de.hhu.phil.buildvectors.PMIWeightedStanardContextVectorBuilder;
import de.hhu.phil.buildvectors.PmiRandomIndexingContextBuilder;
import de.hhu.phil.buildvectors.ProbRandomIndexingContextBuilder;
import de.hhu.phil.buildvectors.StdRandomIndexingContextBuilder;

/**
 *
 * @author Behrang QasemiZadeh <zadeh at phil.hhu.de>
 */
public class AllOneByObe {
    
    public static void main(String[] ss) throws Exception{
    StdRandomIndexingContextBuilder.main(ss);
    ProbRandomIndexingContextBuilder.main(null);
    Estim2New2PMIWeightedStanardContextVectorBuilder.main(null); // this one until 29
    EstimNew2PMIWeightedStanardContextVectorBuilder.main(null);
    New2PMIWeightedStanardContextVectorBuilder.main(null);
    PMIWeightedStanardContextVectorBuilder.main(null);
    ProbRandomIndexingContextBuilder.main(null);
    PmiRandomIndexingContextBuilder.main(null);
        

        System.err.println("-8");
    AccPMIWeightedStanardContextVectorBuilder.main(null);
    PMIDivByMaxCYCXWeightedStanardContextVectorBuilder1.main(null);
    
    }
    
}
