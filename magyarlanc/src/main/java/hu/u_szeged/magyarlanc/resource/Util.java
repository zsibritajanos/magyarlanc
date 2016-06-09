package hu.u_szeged.magyarlanc.resource;

import hu.u_szeged.config.Config;
import hu.u_szeged.magyarlanc.MorAna;

import java.io.*;
import java.util.*;

public class Util {

  /**
   * adott szo csak irasjeleket tartalmaz-e
   */
  public static boolean isPunctation(String spelling) {
    for (int i = 0; i < spelling.length(); ++i) {
      if (Character.isLetterOrDigit(spelling.charAt(i))) {
        return false;
      }
    }
    return true;
  }

  /**
   * 16 15-18 minden szam < 32
   */
  public static boolean isDate(String spelling) {
    for (String s : spelling.split("-")) {
      try {
        if (Integer.parseInt(s) > 31) {
          return false;
        }
      } catch (NumberFormatException e) {
        return false;
      }

    }
    return true;
  }

  static Map<String, Set<MorAna>> readCorpus(String file) {
    BufferedReader reader = null;
    String line = null;
    Set<MorAna> morAnas = null;
    String[] splitted = null;

    Map<String, Set<MorAna>> corpus = null;
    corpus = new TreeMap<String, Set<MorAna>>();

    try {
      reader = new BufferedReader(new InputStreamReader(

              Util.class.getClassLoader().getResourceAsStream(file), Config.getInstance().getEncoding()));
      while ((line = reader.readLine()) != null) {
        morAnas = new TreeSet<MorAna>();
        splitted = line.split("\t");
        for (int i = 1; i < splitted.length - 1; i++) {
          morAnas.add(new MorAna(splitted[i], splitted[i + 1]));
          i++;
        }
        corpus.put(splitted[0], morAnas);
      }
      reader.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return corpus;
  }

  static Map<String, Integer> readFrequencies(String file) {
    BufferedReader reader = null;
    String line = null;
    String[] splitted = null;

    Map<String, Integer> frequencies = null;
    frequencies = new TreeMap<String, Integer>();

    try {
      reader = new BufferedReader(new InputStreamReader(
              Util.class.getClassLoader().getResourceAsStream(file), Config.getInstance().getEncoding()));
      while ((line = reader.readLine()) != null) {
        splitted = line.split("\t");
        frequencies.put(splitted[0], Integer.parseInt(splitted[1]));
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    return frequencies;
  }

  static Set<String> readStopwords(String file) {
    BufferedReader reader = null;
    String line = null;

    Set<String> stopwords = null;
    stopwords = new TreeSet<String>();

    try {
      reader = new BufferedReader(new InputStreamReader(
              Util.class.getClassLoader().getResourceAsStream(file), Config.getInstance().getEncoding()));
      while ((line = reader.readLine()) != null) {
        stopwords.add(line);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    return stopwords;
  }

  static Set<String> readList(String file) {
    BufferedReader reader = null;
    String line = null;

    Set<String> lines = null;
    lines = new TreeSet<String>();

    try {
      reader = new BufferedReader(new InputStreamReader(
              Util.class.getClassLoader().getResourceAsStream(file), Config.getInstance().getEncoding()));

      while ((line = reader.readLine()) != null) {
        lines.add(line);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    return lines;
  }

  public static Set<String> loadPunctations() {
    Set<String> punctations = null;
    punctations = new HashSet<String>();

    String[] puncts = {"!", ",", "-", ".", ":", ";", "?", "–"};

    for (String punct : puncts) {
      punctations.add(punct);
    }

    return punctations;
  }

  public static Set<String> loadMorPhonDir() {
    Set<String> morPhonDir = null;
    morPhonDir = new HashSet<String>();

    String[] morPhons = new String[]{"talány", "némber", "sün", "fal",
            "holló", "felhő", "kalap", "hely", "köd"};

    for (String morPhon : morPhons) {
      morPhonDir.add(morPhon);
    }

    return morPhonDir;
  }

  public static void writeMapToFile(Map<?, ?> map, File file, String encoging) {
    Writer writer = null;
    try {
      writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
              file), encoging));
      for (Map.Entry<?, ?> entry : map.entrySet()) {
        writer.write(entry.getKey() + "\t" + entry.getValue() + "\n");
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

  public static Map<String, String> readCorrDic(String file) {
    BufferedReader reader = null;
    String line = null;
    String[] splitted = null;

    Map<String, String> dictionary = null;
    dictionary = new TreeMap<String, String>();

    try {
      reader = new BufferedReader(new InputStreamReader(
              Util.class.getClassLoader().getResourceAsStream(file), Config.getInstance().getEncoding()));
      while ((line = reader.readLine()) != null) {
        splitted = line.split("\t");
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

  public static String[] getAbsoluteLemma(String form) {
    List<String> lemma = null;
    lemma = new ArrayList<String>();

    for (String s : ResourceHolder.getRFSA().analyse(form)) {
      // igekotok levalasztasa
      s = s.substring(s.indexOf("$") + 1);

      if (s.contains("(") && s.indexOf("(") < s.indexOf("/"))
        lemma.add(s.substring(0, s.indexOf("(")));
      else
        lemma.add(s.substring(0, s.indexOf("/")));
    }

    return lemma.toArray(new String[lemma.size()]);
  }

  public static String readFileToString(String filename) {
    return readFileToString(filename, "UTF-8");
  }

  public static String readFileToString(String filePath, String cEncoding) {
    StringBuffer fileData = new StringBuffer(1000);
    try {
      BufferedReader reader = new BufferedReader(new InputStreamReader(
              new FileInputStream(filePath), cEncoding));
      char[] buf = new char[1024];
      int numRead = 0;
      while ((numRead = reader.read(buf)) != -1) {
        String readData = String.valueOf(buf, 0, numRead);
        fileData.append(readData);
        buf = new char[1024];
      }
      reader.close();
    } catch (IOException e) {
      System.err.println("Problem with file: " + filePath);
      return new String();
    }
    return fileData.toString();
  }

  /**
   * @param file
   * @param encoding
   * @return
   */
  public static String[][] readTokenizedFile(String file, String encoding) {

    BufferedReader reader = null;
    String line;

    List<String[]> sentences = new LinkedList<>();
    List<String> tokens = new LinkedList<>();

    try {
      reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), encoding));
      while ((line = reader.readLine()) != null) {
        if (line.trim().length() == 0) {
          // add sentece to senteces
          sentences.add(tokens.toArray(new String[tokens.size()]));
          tokens = new ArrayList<>();
        } else {
          // add token to tokens
          tokens.add(line.trim());
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        reader.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    return sentences.toArray(new String[sentences.size()][]);
  }
}
