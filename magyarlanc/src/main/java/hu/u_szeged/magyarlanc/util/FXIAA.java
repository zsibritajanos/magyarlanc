package hu.u_szeged.magyarlanc.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class FXIAA {
  
  static int TP = 0;
  static int FP = 0;
  static int FN = 0;
  
  public static Integer[][] getPhrases(String[] sentence) {
    boolean inPhrase = false;
    
    ArrayList<Integer> start = null;
    ArrayList<Integer> end = null;
    
    start = new ArrayList<Integer>();
    end = new ArrayList<Integer>();
    
    for (int i = 0; i < sentence.length; ++i) {
      if (!inPhrase) {
        if ((i == 0 && !sentence[i].equals("O"))
            || (i > 0 && !sentence[i].equals("O") && !sentence[i - 1]
                .equals(sentence[i]))) {
          start.add(i);
          inPhrase = true;
        }
      }
      
      if (inPhrase) {
        if ((i == sentence.length - 1)
            || (!sentence[i].equals(sentence[i + 1]))) {
          end.add(i);
          inPhrase = false;
        }
      }
    }
    
    Integer[][] phrases = null;
    phrases = new Integer[start.size()][2];
    
    for (int i = 0; i < start.size(); ++i) {
      phrases[i][0] = start.get(i);
      phrases[i][1] = end.get(i);
    }
    
    return phrases;
  }
  
  public static boolean containsPhrase(Integer[] phrase, Integer[][] phrases) {
    for (int i = 0; i < phrases.length; ++i) {
      if ((phrases[i][0] == phrase[0]) && (phrases[i][1] == phrase[1])) {
        return true;
      }
    }
    
    return false;
  }
  
  public static void evalSentence(String[] e, String[] p) {
    Integer[][] ePhrases = null;
    Integer[][] pPhrases = null;
    
    ePhrases = getPhrases(e);
    pPhrases = getPhrases(p);
    
    for (int i = 0; i < ePhrases.length; ++i) {
      if (containsPhrase(ePhrases[i], pPhrases)) {
        ++TP;
      }
      if (!containsPhrase(ePhrases[i], pPhrases)) {
        ++FN;
      }
    }
    
    for (int i = 0; i < pPhrases.length; ++i) {
      if (!containsPhrase(pPhrases[i], ePhrases)) {
        ++FP;
      }
    }
  }
  
  public static List<List<String>> read(String file) {
    BufferedReader reader = null;
    
    String line = null;
    List<String> sentence = null;
    List<List<String>> sentences = null;
    
    sentence = new ArrayList<String>();
    sentences = new LinkedList<List<String>>();
    
    try {
      reader = new BufferedReader(new InputStreamReader(new FileInputStream(
          file), "UTF-8"));
      
      while ((line = reader.readLine()) != null) {
        if (line.equals("")) {
          sentences.add(sentence);
          sentence = new ArrayList<String>();
        } else {
          sentence.add(line.split("\t")[1]);
        }
      }
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    
    return sentences;
  }
  
  public static void eval(List<List<String>> e, List<List<String>> p) {
    for (int i = 0; i < e.size(); ++i) {
      evalSentence(e.get(i).toArray(new String[e.get(i).size()]), p.get(i)
          .toArray(new String[p.get(i).size()]));
    }
  }
  
  public static void eval(String e, String p) {
    eval(read(e), read(p));
    System.err.print(e + " -> " + p);
    System.err.print("\nTP: " + TP);
    System.err.print("\tFP: " + FP);
    System.err.println("\tFN: " + FN);
    double precision = (TP / (double) (TP + FP));
    double recall = (TP / (double) (TP + FN));
    System.err.println("Prec: " + precision);
    System.err.println("Rec: " + recall);
    System.err.println("F: " + 2 * (precision * recall) / (precision + recall));
    
    TP = 0;
    FP = 0;
    FN = 0;
  }
  
  public static void main(String[] args) {
    eval("./data/fxiaa/vera.fx.iob", "./data/fxiaa/adam.fx.iob");
  }
}
