package hu.u_szeged.pos.util;

import hu.u_szeged.magyarlanc.MorAna;
import hu.u_szeged.pos.converter.CoNLLFeaturesToMSD;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Map.Entry;

public class CoNLLToCorpus {
  
  private static CoNLLFeaturesToMSD coNLLFeaturesToMSD = null;
  
  private static Map<String, Set<MorAna>> getCorpus(String[][][] sentences) {
    if (coNLLFeaturesToMSD == null)
      coNLLFeaturesToMSD = new CoNLLFeaturesToMSD();
    
    Map<String, Set<MorAna>> corpus = null;
    corpus = new TreeMap<String, Set<MorAna>>();
    
    for (String[][] sentence : sentences)
      for (String[] token : sentence) {
        if (!corpus.containsKey(token[1])) {
          corpus.put(token[1], new TreeSet<MorAna>());
        }
        
        corpus.get(token[1])
            .add(
                new MorAna(token[2], coNLLFeaturesToMSD.convert(token[4],
                    token[6])));
      }
    
    return corpus;
  }
  
  private static void write(Map<String, Set<MorAna>> corpus, String file) {
    BufferedWriter bufferedWriter = null;
    
    try {
      bufferedWriter = new BufferedWriter(new OutputStreamWriter(
          new FileOutputStream(file), "UTF-8"));
      
      for (Entry<String, Set<MorAna>> entry : corpus.entrySet()) {
        if (!entry.getKey().equals("<empty>")) {
          bufferedWriter.write(entry.getKey());
          for (MorAna morAna : entry.getValue()) {
            bufferedWriter.write("\t" + morAna.getLemma() + "\t"
                + morAna.getMsd());
          }
          bufferedWriter.write("\n");
        }
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
  
  public static void coNLLToCorpus(String corpus, String out) {
    write(getCorpus(CoNLLUtil.read(corpus)), out);
  }
  
  public static void main(String[] args) {
    for (int i = 0; i < 10; ++i)
      coNLLToCorpus("./data/newspaper/newspaper.conll2009_train" + i,
          "./data/newspaper/newspaper.conll2009_train" + i + ".corpus");
  }
}
