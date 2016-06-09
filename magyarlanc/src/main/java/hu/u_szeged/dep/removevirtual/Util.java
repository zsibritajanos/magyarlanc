package hu.u_szeged.dep.removevirtual;

import hu.u_szeged.pos.converter.MSDToCoNLLFeatures;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Util {
  
  /**
   * Reads an CoNLL-2009 format file with the given encoding to a String array
   * of the sentences. All sentence contains the String array os the tokens. All
   * token contais the String array of the ConNLL-2009 values.
   * 
   * @param file
   *          the CoNLL-2009 file
   * @param encoding
   *          the specified charcter encoding
   * @return the CoNLL-2009 sentences
   * @see http://ufal.mff.cuni.cz/conll2009-st/task-description.html
   */
  public static String[][][] readCoNLL2009(String file, String encoding) {
    BufferedReader bufferedReader = null;
    String line = null;
    List<String[]> sentence = null;
    List<String[][]> sentences = null;
    
    sentence = new ArrayList<String[]>();
    sentences = new ArrayList<String[][]>();
    
    try {
      bufferedReader = new BufferedReader(new InputStreamReader(
          new FileInputStream(file), encoding));
      
      while ((line = bufferedReader.readLine()) != null) {
        if (!line.equals("")) {
          sentence.add(line.split("\t"));
        } else {
          sentences.add(sentence.toArray(new String[sentence.size()][]));
          sentence = new LinkedList<String[]>();
        }
      }
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    
    return sentences.toArray(new String[sentences.size()][][]);
  }
  
  public static void writeCoNLL2009(String[][][] sentences, String file,
      String encoding) {
    Writer writer = null;
    
    try {
      writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
          file), encoding));
      
      for (String[][] sentence : sentences) {
        for (String[] token : sentence) {
          for (String s : token) {
            writer.write(s + "\t");
          }
          writer.write("\n");
        }
        writer.write("\n");
      }
      
      writer.flush();
      writer.close();
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  public static String[][][] readCoNLL2009(String file) {
    return readCoNLL2009(file, "UTF-8");
  }
  
  public static void writeCoNLL2009(String[][][] sentences, String file) {
    writeCoNLL2009(sentences, file, "UTF-8");
  }
  
  public static void main(String[] args) {
    String[][][] conll = null;
    String[][][] msd = null;
    
    //conll = readCoNLL2009("newspaper.conll2009");
    
    conll = readCoNLL2009("newspaper.conll2009_test_virtual_1");
    msd = readCoNLL2009("newspaper.pred2");
    
    MSDToCoNLLFeatures msdToCoNLLFeatures = new MSDToCoNLLFeatures();
    
    for (int i = 0; i < conll.length; ++i) {
      for (int j = 0; j < conll[i].length; ++j) {
        
        // plemma
        conll[i][j][3] = msd[i][j][1];
        
        // pposs
        conll[i][j][5] = String.valueOf(msd[i][j][2].charAt(0));
        
        // pfeat
        String pfeat = null;
        pfeat = msdToCoNLLFeatures.convert(msd[i][j][1], msd[i][j][2]);
        if (pfeat == null)
          pfeat = "_";
        conll[i][j][7] = pfeat;
        
      }
    }
    
    writeCoNLL2009(conll, "newspaper.conll2009.pred.2");
    
  }
}
