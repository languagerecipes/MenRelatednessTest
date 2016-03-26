/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.phil.hhu.obj;

import java.util.Comparator;

/**
 *
 * @author bq
 */
public class MenPair {

    private String keyOne;
    private String keyTwo;
    private double sim;

    public MenPair(String keyOne, String keyTwo) {
        this.keyOne = keyOne;
        this.keyTwo = keyTwo;

    }

    public MenEntry getMenEntry1() {
        return new MenEntry(keyOne.split("-")[0], keyOne.split("-")[1]);
    }

    public MenEntry getMenEntry2() {
        return new MenEntry(keyTwo.split("-")[0], keyTwo.split("-")[1]);
    }

    public MenPair(String keyOne, String keyTwo, double sim) {
        this.keyOne = keyOne;
        this.keyTwo = keyTwo;
        this.sim = sim;
    }

    public void setKeyOne(String keyOne) {
        this.keyOne = keyOne;
    }

    public void setKeyTwo(String keyTwo) {
        this.keyTwo = keyTwo;
    }

    public String getKeyOne() {
        return keyOne;
    }

    public String getKeyTwo() {
        return keyTwo;
    }

    
    public boolean isThisTheSamePair(MenPair m) {
        if (this.keyOne.equals(m.keyOne) && this.keyTwo.equals(m.keyTwo)) {
            return true;
        } else if (this.keyOne.equals(m.keyTwo) && this.keyTwo.equals(m.keyOne)) {
            return true;
        }
        return false;
    }

    public void setSim(double sim) {
        this.sim = sim;
    }

    public double getSim() {
        return sim;
    }

    public static Comparator<MenPair> similarityComparator() {
        return new Comparator<MenPair>() {
            @Override
            public int compare(MenPair t, MenPair t1) {
                return Double.compare(t.sim, t1.sim);
            }
        };
    }

    public static Comparator<MenPair> nameComparator() {
        return new Comparator<MenPair>() {
            @Override
            public int compare(MenPair a, MenPair b) {
                if (a.isThisTheSamePair(b)) {
                    return 0;
                } else {
                    return (a.keyOne + "-" + a.keyTwo).compareTo(b.keyOne + "-" + b.keyTwo);
                }
            }
        };
    }

}
