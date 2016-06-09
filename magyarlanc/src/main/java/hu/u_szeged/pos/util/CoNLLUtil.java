package hu.u_szeged.pos.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class CoNLLUtil {
  
  static String readToString(String file) {
    BufferedReader bufferedReader = null;
    String line = null;
    StringBuffer stringBuffer = null;
    
    stringBuffer = new StringBuffer();
    try {
      bufferedReader = new BufferedReader(new InputStreamReader(
          new FileInputStream(file), "UTF-8"));
      
      while ((line = bufferedReader.readLine()) != null) {
        stringBuffer.append(line + "\n");
      }
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    
    return stringBuffer.toString();
  }
  
  public static void merge(String out) {
    BufferedWriter bufferedWriter = null;
    
    try {
      bufferedWriter = new BufferedWriter(new OutputStreamWriter(
          new FileOutputStream(out), "UTF-8"));
      
      for (int i = 0; i < 10; ++i) {
        bufferedWriter
            .write(readToString("./data/newspaper/newspaper.conll2009_test" + i
                + ".pred2"));
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
  
  static String[][][] read(String file) {
    BufferedReader bufferedReader = null;
    String line = null;
    
    List<String[]> sentence = null;
    List<String[][]> sentences = null;
    sentence = new ArrayList<String[]>();
    sentences = new ArrayList<String[][]>();
    
    try {
      bufferedReader = new BufferedReader(new InputStreamReader(
          new FileInputStream(file), "UTF-8"));
      
      while ((line = bufferedReader.readLine()) != null) {
        if (line.trim().equals("")) {
          sentences.add(sentence.toArray(new String[sentence.size()][]));
          sentence = new ArrayList<String[]>();
        } else {
          sentence.add(line.split("\t"));
        }
      }
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    
    return sentences.toArray(new String[sentence.size()][][]);
  }
  
  public static void main(String[] args) {
    merge("./newspaper.pred2");
  }
}
