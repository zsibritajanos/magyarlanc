package hu.u_szeged.magyarlanc.util;

import splitter.archive.StringCleaner;

import java.io.*;
import java.util.LinkedList;
import java.util.List;

public class SafeReader {

  // https://console.developers.google.com/start

  /**
   * @param file
   * @param encoding
   * @return
   */
  public static List<String> read(String file, String encoding) {

    List<String> lines = new LinkedList<>();

    BufferedReader reader = null;
    String line;
    try {
      reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), encoding));
      while ((line = reader.readLine()) != null) {
        if (line.trim().length() > 0) {
          lines.add(new StringCleaner().cleanString(line.trim()));
        }
      }
    } catch (UnsupportedEncodingException | FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        reader.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    return lines;
  }
}
