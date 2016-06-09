package hu.u_szeged.converter.nooj;

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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class Dep2Nooj {

  private static Set<String> getGovs(int index, String[][] sentence) {
    Set<String> govs = null;
    govs = new TreeSet<String>();

    for (String[] token : sentence) {
      if (Integer.parseInt(token[6]) == index) {
        if (!token[7].equals("ROOT")) {
          govs.add(token[7]);
        }
      }
    }
    return govs;
  }

  private static String convertSentence(String[][] sentence) {

    Map<String, Integer> map = null;
    map = new TreeMap<String, Integer>();

    StringBuffer stringBuffer = null;
    stringBuffer = new StringBuffer();

    String token[] = null;
    String templabel = null;
    for (int i = 0; i < sentence.length; ++i) {
      token = sentence[i];
      stringBuffer.append("<LU LEMMA=\"");
      stringBuffer.append(token[2] + "\" ");
      stringBuffer.append("CAT=\"");
      stringBuffer.append(token[4] + "\" ");
      stringBuffer.append("POS" + token[3]);
      for (String gov : getGovs(i + 1, sentence)) {
        templabel = gov + "GOV";
        if (!map.containsKey(templabel)) {
          map.put(templabel, 0);
        }
        map.put(templabel, map.get(templabel) + 1);
        int counter = map.get(templabel);
        stringBuffer.append(" " + gov + counter + "GOV");
      }

      if (!token[7].equals("ROOT")) {
        templabel = token[7] + "DEP";
        if (!map.containsKey(templabel)) {
          map.put(templabel, 0);
        }
        map.put(templabel, map.get(templabel) + 1);
        int counter = map.get(templabel);
        stringBuffer.append(" " + token[7] + counter + "DEP");
      }

      stringBuffer.append(">" + token[1] + "</LU> ");
    }

    return stringBuffer.toString();
  }

  private static String[][][] read(String file) {

    BufferedReader reader = null;
    String line = null;

    List<String[]> sentence = null;
    List<String[][]> document = null;

    document = new ArrayList<String[][]>();
    try {
      reader = new BufferedReader(new InputStreamReader(new FileInputStream(
          file), "UTF8"));

      sentence = new ArrayList<String[]>();
      while ((line = reader.readLine()) != null) {
        if (line.trim().length() == 0) {
          document.add(sentence.toArray(new String[sentence.size()][]));
          sentence = new ArrayList<String[]>();
        } else {
          sentence.add(line.split("\t"));
        }

      }
      reader.close();
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return document.toArray(new String[document.size()][][]);
  }

  public static void convert(String[][][] sentences, String outFile,
      String encoding) {
    Writer writer = null;

    try {
      writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
          outFile), encoding));
      writer.write("<doc>\n");
      for (String[][] sentence : sentences) {
        writer.write(convertSentence(sentence) + "\n");
      }
      writer.write("</doc>");
      writer.close();
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        writer.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  public static void convert(String file, String outFile) {
    Writer writer = null;

    try {
      writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
          outFile), "UTF-8"));
      for (String[][] s : read(file)) {
        writer.write(convertSentence(s) + "\n");
      }
      writer.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
