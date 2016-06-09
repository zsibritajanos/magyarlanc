package hu.u_szeged.pos.util;

import hu.u_szeged.pos.converter.CoNLLFeaturesToMSD;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

public class CoNLLToFreq {
  
  private static CoNLLFeaturesToMSD coNLLFeaturesToMSD = null;
  
  private static Map<String, Integer> getFreq(String[][][] sentences) {
    if (coNLLFeaturesToMSD == null)
      coNLLFeaturesToMSD = new CoNLLFeaturesToMSD();
    
    Map<String, Integer> freq = null;
    freq = new TreeMap<String, Integer>();
    
    String MSD = null;
    
    for (String[][] sentence : sentences)
      for (String[] token : sentence) {
        MSD = coNLLFeaturesToMSD.convert(token[4], token[6]);
        
        if (!freq.containsKey(MSD)) {
          freq.put(MSD, 0);
        }
        
        freq.put(MSD, freq.get(MSD) + 1);
      }
    
    return freq;
  }
  
  private static void write(Map<String, Integer> freq, String file) {
    BufferedWriter bufferedWriter = null;
    
    try {
      bufferedWriter = new BufferedWriter(new OutputStreamWriter(
          new FileOutputStream(file), "UTF-8"));
      
      for (Entry<String, Integer> entry : freq.entrySet()) {
        bufferedWriter.write(entry.getKey() + "\t" + entry.getValue() + "\n");
      }
      
      bufferedWriter.flush();
      bufferedWriter.close();
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  public static void coNLLToFreq(String corpus, String out) {
    write(getFreq(CoNLLUtil.read(corpus)), out);
  }
  
  public static void main(String[] args) {
    for (int i = 0; i < 10; ++i)
      coNLLToFreq("./data/newspaper/newspaper.conll2009_train" + i,
          "./data/newspaper/newspaper.conll2009_train" + i + ".freq");
    
  }
}
