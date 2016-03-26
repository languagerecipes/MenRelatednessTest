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
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 *
 * @author Behrang QasemiZadeh <zadeh at phil.hhu.de>
 */
public interface IExpSet {

    public String getRoot();

    public void printConfiguration() throws IOException;

    public List<FCRITInfo> getFCRITContextQueries();



    public String getExpIdentifier();

    public String getSimilarityResultFile(String simMeasureType);

    public String getSparseVectorPresentations()  throws UnsupportedEncodingException ;

    public String getRawContextFilesPath() throws UnsupportedEncodingException;

    public String getRawContextFilesName(IContextQuery cq, FCRITInfo fcrit) throws UnsupportedEncodingException;

    public String getFinalResultReport();

    //public String getSparseVectorPresentations(IContextQuery ctxQuery) throws UnsupportedEncodingException;
    public String getSparseVectorPresentations(IContextQuery ctxQuery, String weightingType) throws UnsupportedEncodingException;
    public String getContextFreqProfileFile();
    
      public String getCorpus() ;
    
    public String getBaseURL();

    public String getRunCGI();

    
  //  public String getSimType();
   // public String weightingType();

}
