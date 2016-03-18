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
package de.phil.hhu.obj;

/**
 *
 * @author Behrang QasemiZadeh <zadeh at phil.hhu.de>
 */
public class MenEntry implements Comparable<MenEntry> {
    String lemma;
    String tag;

    public String getLemma() {
        return lemma;
    }

    public String getTag() {
        return tag;
    }

    public MenEntry(String lemma, String tag) {
        this.lemma = lemma;
        this.tag = tag;
    }

    @Override
    public int compareTo(MenEntry t) {
        int cl = t.lemma.compareTo(this.lemma);
        int ct = t.tag.compareTo(this.tag);
        if(cl==0){
            return ct;
        }else{
            return cl;
        }
    }
    
}
