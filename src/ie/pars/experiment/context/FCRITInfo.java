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

/**
 * Object that encapsulates information about the way freqs are collected.
 * @author Behrang QasemiZadeh <zadeh at phil.hhu.de>
 */
public class FCRITInfo {

    private final String fcritQuery;
    private final String fcritFileIdentifier;

    public String getFcritFileIdentifier() {
        return fcritFileIdentifier;
    }

    public String getFcritQuery() {
        return fcritQuery;
    }

    public FCRITInfo(String fcritQuery, String fcritFileName) {
        this.fcritQuery = fcritQuery;
        this.fcritFileIdentifier = fcritFileName;
    }

    @Override
    public String toString() {
        return fcritQuery + "\t" + fcritFileIdentifier;
    }

}
