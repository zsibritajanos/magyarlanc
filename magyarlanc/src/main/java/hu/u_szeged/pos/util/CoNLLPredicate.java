package hu.u_szeged.pos.util;

import hu.u_szeged.magyarlanc.Magyarlanc;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class CoNLLPredicate {
  
  private static String[] removeEmpty(String[] form) {
    List<String> cleaned = null;
    cleaned = new ArrayList<String>();
    
    for (String f : form) {
      if (!f.equals("<empty>")) {
        cleaned.add(f);
      }
    }
    
    return cleaned.toArray(new String[cleaned.size()]);
  }
  
  private static void predicate(String[][][] sentences, String out) {
    
    CoNLLSentence coNLLSentence = null;
    String[] form = null;
    String[][] morph = null;
    
    BufferedWriter bufferedWriter = null;
    try {
      bufferedWriter = new BufferedWriter(new OutputStreamWriter(
          new FileOutputStream(out), "UTF-8"));
      
      for (String[][] sentence : sentences) {
        coNLLSentence = new CoNLLSentence(sentence);
        form = removeEmpty(coNLLSentence.getForm());
        
        morph = Magyarlanc.morphParseSentence(form);
        
        for (int i = 0; i < form.length; ++i) {
          bufferedWriter.write((form[i] + "\t" + morph[i][1] + "\t"
              + morph[i][2] + "\n"));
          //  bufferedWriter.write((form[i] + "\t" + morph[i][2] + "\n"));
        }
        bufferedWriter.write("\n");
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
  
  public static int szam = 9;
  
  public static void main(String[] args) {
    predicate(CoNLLUtil
        .read("./data/newspaper/newspaper.conll2009_test" + szam),
        "./data/newspaper/newspaper.conll2009_test" + szam + ".pred2");
  }
}
