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
package ie.pars.experiment.context;

import java.io.IOException;
import java.util.List;

/**
 *
 * @author Behrang QasemiZadeh <zadeh at phil.hhu.de>
 */
public interface IExpSet {

    public String getRoot();

    public void printConfiguration();

    public List<FCRITInfo> getFCRITContextQueries();

    public String getContextQuery(ContextQuery clt);

    public String getExpIdentifier();

    public String getSimilarityResultFile(String simMeasureType);

    public String getSparseVectorPresentations();

    public String getRawContextFilesPath();

    public String getRawContextFilesName(ContextQuery cq, FCRITInfo fcrit);

    public String getFinalResultReport();

    public String getSparseVectorPresentations(ContextQuery ctxQuery);

}
