package hu.u_szeged.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class IOTool {

  public static List<String> readStringList(String fileName) throws IOException {
    BufferedReader reader = new BufferedReader(new FileReader(fileName));
    
    List<String> sentences = new ArrayList<String>();
    
    String line;
    while ((line = reader.readLine()) != null) {
      sentences.add(line);
    }
    
    reader.close();
    return sentences;
  }
  
  public static void writeStringList(List<String> sentences, String fileName) throws IOException {
    BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
    
    for (String line : sentences) {
      writer.write(line + "\n");
    }
    
    writer.flush();
    writer.close();
  }
}
