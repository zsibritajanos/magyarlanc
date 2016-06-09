package hu.u_szeged.magyarlanc.util;

import hu.u_szeged.magyarlanc.HunLemMor;
import hu.u_szeged.magyarlanc.MorAna;
import hu.u_szeged.pos.converter.CoNLLFeaturesToMSD;
import hu.u_szeged.pos.converter.MSDReducer;
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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class Tools {
  
  static int tokens = 0;
  static int sentences = 0;
  
  public static String readFileToString(String file, String encoding) {
    
    StringBuffer stringBuffer = null;
    BufferedReader reader = null;
    stringBuffer = new StringBuffer(1000);
    
    try {
      reader = new BufferedReader(new InputStreamReader(new FileInputStream(
          file), encoding));
      char[] buf = new char[1024];
      int numRead = 0;
      while ((numRead = reader.read(buf)) != -1) {
        String readData = String.valueOf(buf, 0, numRead);
        stringBuffer.append(readData);
        buf = new char[1024];
      }
      reader.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return stringBuffer.toString();
  }
  
  public static List<List<String>> readFile(String file) {
    List<String> sentence = null;
    List<List<String>> document = null;
    BufferedReader reader = null;
    String line = null;
    
    sentence = new LinkedList<String>();
    document = new LinkedList<List<String>>();
    
    int tokenCounter = 0;
    try {
      reader = new BufferedReader(new InputStreamReader(new FileInputStream(
          file), "UTF8"));
      
      while ((line = reader.readLine()) != null) {
        if (line.equals("")) {
          document.add(sentence);
          tokenCounter += sentence.size();
          sentence = new LinkedList<String>();
        } else {
          sentence.add(line);
        }
      }
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    
    System.err.println(file + "\t" + tokenCounter + " tokens\t"
        + document.size() + " sentences");
    
    tokens += tokenCounter;
    sentences += document.size();
    
    return document;
  }
  
  public static List<List<List<String>>> readFiles(String[] files) {
    List<List<List<String>>> documents = null;
    documents = new LinkedList<List<List<String>>>();
    
    for (String file : files) {
      documents.add(readFile(file));
    }
    
    System.err.println(documents.size() + " documents\t" + tokens + " tokens\t"
        + sentences + " sentences");
    return documents;
  }
  
  public static String[] fileList(String path, String[] files, String extension) {
    String[] fileList = null;
    fileList = new String[files.length];
    
    for (int i = 0; i < files.length; ++i) {
      fileList[i] = path + files[i] + extension;
    }
    
    return fileList;
  }
  
  public static void write(String out, List<List<List<String>>> documents) {
    Writer writer = null;
    String[] splitted = null;
    try {
      writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
          out), "UTF8"));
      
      for (List<List<String>> document : documents) {
        for (List<String> sentence : document) {
          for (String line : sentence) {
            splitted = line.split("\t");
            
            writer.write(splitted[0]);
            for (int i = 1; i < 12; ++i)
              writer.write("\t" + splitted[i]);
            writer.write("\n");
          }
          writer.write("\n");
        }
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
  
  public static Map<String, Integer> verbStat(List<List<List<String>>> documents) {
    Map<String, Integer> verbStat = null;
    String[] splitted = null;
    
    verbStat = new TreeMap<String, Integer>();
    
    for (List<List<String>> document : documents) {
      for (List<String> sentence : document) {
        for (String token : sentence) {
          splitted = token.split("\t");
          if (splitted[4].equals("V") && splitted[6].startsWith("SubPOS=m")) {
            if (!verbStat.containsKey(splitted[2])) {
              verbStat.put(splitted[2], 0);
            }
            verbStat.put(splitted[2], verbStat.get(splitted[2]) + 1);
          }
        }
      }
    }
    
    
    for (Map.Entry<String, Integer> entry : verbStat.entrySet()) {
      System.out.println(entry.getKey() + "\t" + entry.getValue());
    }
    
    return verbStat;
  }
  
  // public static void writePosTrain(List<List<List<String>>> documents,
  // String file) {
  // String[] splitted = null;
  // Writer writer = null;
  //    
  // Set<String> reduced = null;
  // reduced = new TreeSet<String>();
  //    
  // try {
  // writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
  // file), "UTF8"));
  // for (List<List<String>> document : documents) {
  // for (List<String> sentence : document) {
  // for (String line : sentence) {
  // splitted = line.split("\t");
  // if (!splitted[4].equals("ELL") && !splitted[4].equals("VAN")) {
  // writer.write(splitted[1] + "@" + splitted[13] + " ");
  // reduced.add(splitted[13]);
  // }
  // }
  // writer.write("\n");
  // }
  // }
  // writer.flush();
  // writer.close();
  // } catch (UnsupportedEncodingException e) {
  // e.printStackTrace();
  // } catch (FileNotFoundException e) {
  // e.printStackTrace();
  // } catch (IOException e) {
  // e.printStackTrace();
  // }
  //    
  // for (String r : reduced) {
  // System.out.print(r + " ");
  // }
  // }
  //  
  public static void writePosTrain(List<List<String>> sentences, String file) {
    String[] splitted = null;
    Writer writer = null;
    
    Set<String> reduced = null;
    reduced = new TreeSet<String>();
    
    CoNLLFeaturesToMSD coNLLFeaturesToMSD = new CoNLLFeaturesToMSD();
    MSDReducer msdReducer = new MSDReducer();
    
    String MSD = null;
    try {
      writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
          file), "UTF8"));
      for (List<String> sentence : sentences) {
        for (String line : sentence) {
          splitted = line.split("\t");
          if (!splitted[4].equals("ELL") && !splitted[4].equals("VAN")) {
            MSD = coNLLFeaturesToMSD.convert(splitted[4], splitted[6]);
            
            writer.write(splitted[1] + "@" + msdReducer.reduce(MSD) + " ");
            reduced.add(splitted[13]);
          }
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
    
    for (String r : reduced) {
      System.out.print(r + " ");
    }
  }
  
  public static void validate() {
    String path = null;
    String[] files = null;
    String extension = null;
    
    MSDToCoNLLFeatures msdToCoNLLFeatures = null;
    CoNLLFeaturesToMSD coNLLFeaturesToMSD = null;
    
    msdToCoNLLFeatures = new MSDToCoNLLFeatures();
    coNLLFeaturesToMSD = new CoNLLFeaturesToMSD();
    
    MSDReducer msdReducer = null;
    msdReducer = new MSDReducer();
    
    Writer writer = null;
    try {
      writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
          "./new_features.txt"), "UTF-8"));
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    
    path = "./data/conll/msd/";
    
    files = new String[] { "8oelb", /*
                                     * "10elb", "10erv", "1984", "cwszt",
                                     * "gazdtar", "hvg", "mh", "newsml", "np",
                                     * "nv", "pfred", "szerzj", "utas",
                                     * "win2000"
                                     */};
    
    extension = ".conll-2009-msd";
    
    List<List<List<String>>> documents = readFiles(fileList(path, files,
        extension));
    
    String oldFetures = null;
    String newFeatures = null;
    String[] splitted = null;
    
    Set<String> ftrs = null;
    ftrs = new TreeSet<String>();
    
    Map<String, Map<String, Integer>> rdcds = null;
    rdcds = new TreeMap<String, Map<String, Integer>>();
    
    Map<String, Integer> msds = null;
    msds = new TreeMap<String, Integer>();
    
    for (List<List<String>> document : documents) {
      for (List<String> sentence : document) {
        for (String token : sentence) {
          
          splitted = token.split("\t");
          oldFetures = splitted[6];
          newFeatures = msdToCoNLLFeatures.convert(splitted[2], splitted[12]);
          
          // new features
          if (!oldFetures.equals(newFeatures)) {
            // try {
            // ftrs.add(splitted[12] + "\t" + oldFetures + "\t" + newFeatures);
            // writer.write(splitted[2] + "\t" + splitted[12] + "\t"
            // + oldFetures + "\t" + newFeatures + "\n");
            // } catch (IOException e) {
            // e.printStackTrace();
            // }
          }
          
          try {
            // if (!coNLLFeaturesToMSD.getMsd(splitted[4], oldFetures).equals(
            // splitted[12])) {
            // System.out.println("SDM : " + splitted[12]);
            // }
          } catch (Exception e) {
            // TODO: handle exception
          }
          
          try {
            if (!msdReducer.reduce(splitted[12]).equals(splitted[13])) {
              if (!Character.isLetterOrDigit(splitted[12].charAt(0))) {
                // System.out.println(token);
              }
              // rdcds.add(splitted[12] + "\t" + splitted[13] + "\t"
              // + msdReducer.reduce(splitted[12]));
            }
            
          } catch (Exception e) {
            // TODO: handle exception
          }
          
          if (!msds.containsKey(splitted[12] + "\t"
              + msdReducer.reduce(splitted[12]))) {
            
            if (!msds.containsKey(splitted[12] + "\t"
                + msdReducer.reduce(splitted[12]))) {
              msds
                  .put(splitted[12] + "\t" + msdReducer.reduce(splitted[12]), 0);
            }
            msds
                .put(splitted[12] + "\t" + msdReducer.reduce(splitted[12]),
                    msds.get(splitted[12] + "\t"
                        + msdReducer.reduce(splitted[12])) + 1);
          }
        }
      }
      
      try {
        writer.flush();
        writer.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
      
      for (String ftr : ftrs) {
        // System.out.println(ftr);
      }
      
      // for (String rdcd : rdcds) {
      // // System.out.println(rdcd);
      // }
      //    
      for (Map.Entry entry : msds.entrySet()) {
        System.out.println(entry.getKey() + " \t" + entry.getValue());
      }
    }
  }
  
  public static void correct(String file, String out) {
    List<List<String>> document = null;
    document = readFile(file);
    Writer writer = null;
    
    try {
      writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
          out), "UTF-8"));
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    
    MSDToCoNLLFeatures msdToCoNLLFeatures = null;
    msdToCoNLLFeatures = new MSDToCoNLLFeatures();
    
    MSDReducer msdReducer = null;
    msdReducer = new MSDReducer();
    
    String[] splitted = null;
    String oldFetures = null;
    String oldReduced = null;
    String newFeatures = null;
    String newReduced = null;
    
    String oldPFetures = null;
    String oldPReduced = null;
    String newPFeatures = null;
    String newPReduced = null;
    
    for (List<String> sentence : document) {
      for (String token : sentence) {
        
        splitted = token.split("\t");
        
        oldFetures = splitted[6];
        oldPFetures = splitted[7];
        
        newFeatures = msdToCoNLLFeatures.convert(splitted[2], splitted[12]);
        newPFeatures = msdToCoNLLFeatures.convert(splitted[3], splitted[14]);
        
        oldReduced = splitted[13];
        oldPReduced = splitted[15];
        newReduced = msdReducer.reduce(splitted[12]);
        newPReduced = msdReducer.reduce(splitted[14]);
        
        if (!oldFetures.equals(newFeatures)) {
          System.out.println(oldFetures + "\t" + newFeatures);
          splitted[6] = newFeatures;
        }
        
        if (!oldPFetures.equals(newPFeatures)) {
          System.out.println(oldPFetures + "\t" + newPFeatures);
          splitted[7] = newPFeatures;
        }
        
        if (!oldReduced.equals(newReduced)) {
          System.out.println(oldReduced + "\t" + newReduced);
          splitted[13] = newReduced;
        }
        
        if (!oldPReduced.equals(newPReduced)) {
          System.out.println(oldPReduced + "\t" + newPReduced);
          splitted[15] = newPReduced;
        }
        
        try {
          writer.write(splitted[0]);
          for (int i = 1; i < splitted.length; ++i)
            writer.write("\t" + splitted[i]);
          writer.write("\n");
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
      try {
        writer.write("\n");
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    try {
      writer.flush();
      writer.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  public static Map<String, Integer> edgeStat(List<List<String>> document) {
    Map<String, Integer> edgeStat = null;
    edgeStat = new TreeMap<String, Integer>();
    String[] splitted = null;
    for (List<String> sentence : document) {
      for (String token : sentence) {
        splitted = token.split("\t");
        
        if (!edgeStat.containsKey(splitted[10]))
          edgeStat.put(splitted[10], 0);
        
        edgeStat.put(splitted[10], edgeStat.get(splitted[10]) + 1);
      }
    }
    
    return edgeStat;
  }
  
  static String getCas(String features) {
    
    String[] splittedFeatures = null;
    splittedFeatures = features.split("\\|");
    
    for (String s : splittedFeatures) {
      if (s.startsWith("Cas")) {
        return s.split("=")[1];
      }
    }
    
    return null;
  }
  
  public static void buildFreqs(List<List<List<String>>> documents, String file) {
    
    Map<String, Integer> freqs = null;
    String[] splitted = null;
    freqs = new TreeMap<String, Integer>();
    
    for (List<List<String>> document : documents)
      for (List<String> sentence : document)
        for (String token : sentence) {
          splitted = token.split("\t");
          if (!splitted[4].equals("ELL") && !splitted[4].equals("VAN")) {
            if (!freqs.containsKey(splitted[12]))
              freqs.put(splitted[12], 0);
            
            freqs.put(splitted[12], freqs.get(splitted[12]) + 1);
          }
        }
    
    Writer writer = null;
    try {
      writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
          file), "UTF-8"));
      for (Map.Entry<String, Integer> entry : freqs.entrySet()) {
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
  
  public static void buildLexicon(List<List<List<String>>> documents,
      String file) {
    Map<String, Set<MorAna>> lexicon = null;
    String[] splitted = null;
    lexicon = new TreeMap<String, Set<MorAna>>();
    
    MorAna morAna = null;
    
    for (List<List<String>> document : documents)
      for (List<String> sentence : document)
        for (String token : sentence) {
          splitted = token.split("\t");
          if (!splitted[4].equals("ELL") && !splitted[4].equals("VAN")) {
            if (!lexicon.containsKey(splitted[1]))
              lexicon.put(splitted[1], new TreeSet<MorAna>());
            
            morAna = new MorAna(splitted[2], splitted[12]);
            lexicon.get(splitted[1]).add(morAna);
          }
        }
    
    Writer writer = null;
    try {
      writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
          file), "UTF-8"));
      for (Map.Entry<String, Set<MorAna>> entry : lexicon.entrySet()) {
        writer.write(entry.getKey());
        for (MorAna m : entry.getValue()) {
          writer.write("\t" + m.getLemma() + "\t" + m.getMsd());
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
  
  public static Map<String, Integer> casStat(List<List<String>> document) {
    Map<String, Integer> casStat = null;
    casStat = new TreeMap<String, Integer>();
    
    String[] splitted = null;
    String cas = null;
    for (List<String> sentence : document) {
      for (String token : sentence) {
        splitted = token.split("\t");
        if (splitted[4].equals("N")) {
          cas = getCas(splitted[6]);
          if (!casStat.containsKey(cas)) {
            casStat.put(cas, 0);
          }
          casStat.put(cas, casStat.get(cas) + 1);
        }
        
      }
    }
    
    return casStat;
  }
  
  public static void possibles(String file) {
    BufferedReader reader = null;
    String line = null;
    
    try {
      reader = new BufferedReader(new InputStreamReader(new FileInputStream(
          file), "UTF-8"));
      while ((line = reader.readLine()) != null) {
        System.err.println(line + "\t"
            + HunLemMor.getMorphologicalAnalyses(line));
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  public static void main(String args[]) {
    
    // possibles("c:/jav2_elemezni.txt");
    String[] files = null;
    String path = "./data/conll/corrected_features/";
    String extension = null;
    files = new String[] { /*
                            * "8oelb", "10elb", "10erv", "1984", "cwszt",
                            * "gazdtar", "hvg", "mh",
                            */"newsml"/*
                                       * , "np", "nv", "pfred", "szerzj",
                                       * "utas", "win2000"
                                       */};
    //    
    extension = ".conll-2009-msd";
    
    // for (String file : files) {
    // for (Map.Entry<String, Integer> entry : edgeStat(
    // readFile(path + file + extension)).entrySet())
    // System.out.println(entry.getKey() + "\t" + entry.getValue());
    // }
    //    
    // for (String file : files) {
    // for (Map.Entry<String, Integer> entry : casStat(
    // readFile(path + file + extension)).entrySet())
    // System.out.println(entry.getKey() + "\t" + entry.getValue());
    // }
    
    // correct("./data/conll/msd/" + file + extension,
    // "./data/conll/corrected_features/" + file + extension);
    
    // verbStat(documents);
    
    //    
    
    // List<List<List<String>>> documents = readFiles(fileList(path, files,
    // extension));
    // //
    // buildLexicon(documents, "./corrected.lex");
    // buildFreqs(documents, "./corrected.freqs");
    //    
    // write("./corrected_features_temp.dep", documents);
    // // documents = null;
    // //
    // try {
    // RemoveEmptyNodes.processFile("./corrected_features_temp.dep",
    // "./corrected_features_temp.dep.train");
    // } catch (IOException e) {
    // e.printStackTrace();
    // }
    //    
    
    // List<List<String>> sentences = readFile("./corrected_features_temp.dep");
    
    for (int i = 0; i < 10; ++i) {
      List<List<String>> sentences = null;
      sentences = readFile("./data/newspaper/newspaper.conll2009_train" + i);
      
      writePosTrain(sentences, "./data/newspaper/newspaper.conll2009_train" + i
          + ".stanford");
    }
  }
}
