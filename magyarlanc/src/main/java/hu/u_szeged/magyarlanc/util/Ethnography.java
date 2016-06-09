package hu.u_szeged.magyarlanc.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Map;
//import java.util.TreeMap;

public class Ethnography {
  
  private Map<String, String> dictionary = null;
  
  public void setDictionary(Map<String, String> dictionary) {
    this.dictionary = dictionary;
  }
  
  public Map<String, String> getDictionary() {
    return dictionary;
  }
  
  private Map<String, String> readDictionary(String dictionaryFile) {
    BufferedReader reader = null;
    String line = null;
    String[] splitted = null;
    
    Map<String, String> dictionary = null;
    //dictionary = new TreeMap<String, String>();
    
    try {
      reader = new BufferedReader(new InputStreamReader(new FileInputStream(
          dictionaryFile), "UTF-8"));
      while ((line = reader.readLine()) != null) {
        splitted = line.split("\t");
        if (!splitted[0].equalsIgnoreCase(splitted[1]))
          dictionary.put(splitted[0], splitted[1]);
        
      }
      
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    
    return dictionary;
  }
  
  public Ethnography(String dictionaryFile) {
    this.setDictionary(this.readDictionary(dictionaryFile));
  }
  
  public String getStandardForm(String nonStandardForm) {
    try {
      return this.getDictionary().get(nonStandardForm);
    } catch (Exception e) {
      return null;
    }
  }
  
  public static void main(String[] args) {
    Ethnography ethnography = null;
    ethnography = new Ethnography("./data/ethno.lex");
    System.out.println(ethnography.getStandardForm("acskó"));
  }
  
}
