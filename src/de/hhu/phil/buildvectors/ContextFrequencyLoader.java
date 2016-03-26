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
package de.hhu.phil.buildvectors;

import ie.pars.experiment.context.ExpSet;
import ie.pars.experiment.context.IExpSet;
import ie.pars.nlp.sketchengine.interactions.WordlistSKEInteraction;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * just to get me through an experiment based on PMI
 *
 * @author Behrang QasemiZadeh <zadeh at phil.hhu.de>
 */
public class ContextFrequencyLoader {

    private double totalFreq;
    private int totalItems;
    private IExpSet expSet;
    private Map<String, Integer> freqProfileMap;

    public ContextFrequencyLoader(IExpSet expSet) throws Exception {
        this.expSet = expSet;
        String contextFreqProfilePath = expSet.getContextFreqProfileFile();
       
         File contextProfl = new File(contextFreqProfilePath);
        if (!contextProfl.exists()) {
            makeFreqProfile();
        } else {
             System.out.println("Using cached frequency profile in file: " + expSet.getContextFreqProfileFile());
            loadFreqProfile();
        }
    }
    

    private void makeFreqProfile() throws Exception {
        System.out.println("Caching frequencies in machine...");
        System.out.println("Cache file: " + expSet.getContextFreqProfileFile());
        WordlistSKEInteraction wle = new WordlistSKEInteraction(
                new File(expSet.getContextFreqProfileFile()),
                expSet.getBaseURL(),
                expSet.getRunCGI(),
                expSet.getCorpus(),
                "lemma", "frq", 0);
        wle.dumpItemFrequencyList();
        totalFreq = wle.getTotalFreq();
        loadFreqProfile();

    }

    private void loadFreqProfile() throws FileNotFoundException, IOException {
        TreeMap<String, Integer> freqProfileMap = new TreeMap<>();
        totalFreq = 0.0;
        totalItems = 0;
        BufferedReader br = new BufferedReader(new FileReader(expSet.getContextFreqProfileFile()));
        String line;
        while ((line = br.readLine()) != null) {
            String[] split = line.split(" : ");
            int parseInt = Integer.parseInt(split[1]);
            totalItems++;
            totalFreq += parseInt;
            freqProfileMap.put(split[0], parseInt);
        }
        br.close();
        this.freqProfileMap = Collections.unmodifiableMap(freqProfileMap);
    }

//    public Map<String, Integer> getFreqProfileMap() {
//        return freqProfileMap;
//    }
    public IExpSet getExpSet() {
        return expSet;
    }

    public double getTotalFreq() {
        return totalFreq;
    }

    public int getTotalItems() {
        return totalItems;
    }

    public double getContextProbability(String context) {
        Integer contFreq = this.freqProfileMap.get(context);
        return contFreq / totalFreq;
    }

    public double getContextProbability0(String context) {

        Integer contFreq = getContextFrequency0(context);
        return contFreq * 1.0 / totalFreq;
    }
    public Integer getContextFrequency(String context) {
        return this.freqProfileMap.get(context);
    }

    // to fix the problem with some wiered stirngs in the output
    public Integer getContextFrequency0(String context) {
        if (this.freqProfileMap.containsKey(context)) {
            return this.freqProfileMap.get(context);
        } else {
            return 0;
        }
    }
    public static void main(String[] test) throws Exception{
         ExpSet expSet = new ExpSet("15032015-", -1, 1);
        ContextFrequencyLoader cfload = new ContextFrequencyLoader(expSet);
        double contextProbability = cfload.getContextProbability("this");
        System.out.println(contextProbability);
    }

    public boolean hasKey(String key){
        return this.freqProfileMap.containsKey(key);
    }
    
    public Set<String> getContextElements(){
        return this.freqProfileMap.keySet();
        
    }
    
}
