package hu.u_szeged.magyarlanc.util;

import hu.u_szeged.magyarlanc.Magyarlanc;
import hu.u_szeged.magyarlanc.resource.ResourceHolder;
import is2.parser.Parser;

import java.io.*;
import java.text.DecimalFormat;
import java.util.*;



public class Eval {
  
  public static void writePosTrain(String file, String out) {
    
    String[] splitted = null;
    Writer writer = null;
    String form = null;
    String MSD = null;
    
    try {
      writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
          out), "UTF-8"));
      
      for (List<String> sentence : read(file)) {
        for (String token : sentence) {
          splitted = token.split("\t");
          form = splitted[1];
          MSD = splitted[3];
          writer.write(form + "@"
              + ResourceHolder.getMSDReducer().reduce(MSD) + " ");
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
  
  public static void writeDepTrain(String file, String out) {
    // 1 A a a T T SubPOS=f SubPOS=f 3 3 DET DET _ _
    
    String[] splitted = null;
    Writer writer = null;
    
    String id = null;
    String form = null;
    String lemma = null;
    String POS = null;
    String feature = null;
    
    String head = null;
    String label = null;
    
    try {
      writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
          out), "UTF-8"));
      
      for (List<String> sentence : read(file)) {
        for (String token : sentence) {
          splitted = token.split("\t");
          
          id = splitted[0];
          form = splitted[1];
          lemma = splitted[2];
          // splitted[3];
          POS = splitted[4];
          feature = splitted[5];
          
          head = splitted[6];
          label = splitted[7];
          
          writer.write(id + "\t" + form + "\t" + lemma + "\t" + lemma + "\t"
              + POS + "\t" + POS + "\t" + feature + "\t" + feature + "\t"
              + head + "\t" + head + "\t" + label + "\t" + label + "\t_\t_\n");
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
  
  public static int[] getOffsets(Integer[] ids, int n) {
    
    if (n < 1) {
      System.err.println("N must be grater than 0.");
      System.exit(1);
    }
    
    if (n > ids.length) {
      System.err.println("N must be less than the length of the array.");
      System.exit(1);
    }
    
    int[] offsets = null;
    offsets = new int[n + 1];
    
    double length = 0;
    length = (double) ids.length / n;
    
    for (int i = 0; i < n; ++i) {
      offsets[i] = (int) Math.round(i * length);
    }
    offsets[n] = ids.length;
    
    return offsets;
  }
  
  public static Integer[][] apart(Integer[] ids, int n) {
    Integer[][] aparted = null;
    
    int[] offsets = null;
    offsets = getOffsets(ids, n);
    
    aparted = new Integer[n][];
    int index = 0;
    
    for (int i = 0; i < offsets.length - 1; ++i) {
      aparted[i] = new Integer[offsets[i + 1] - offsets[i]];
      index = 0;
      for (int j = offsets[i]; j < offsets[i + 1]; ++j) {
        aparted[i][index] = ids[j];
        ++index;
      }
      Arrays.sort(aparted[i]);
    }
    
    return aparted;
  }
  
  public static Integer[] randomArray(int n) {
    Integer[] random = null;
    random = new Integer[n];
    
    for (int i = 0; i < n; ++i) {
      random[i] = i;
    }
    
    Collections.shuffle(Arrays.asList(random));
    
    return random;
  }
  
  public static void write(List<List<String>> sentences, Integer[] ids,
      String out) {
    Writer writer = null;
    
    try {
      writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
          out), "UTF-8"));
      
      for (int id : ids) {
        for (String token : sentences.get(id)) {
          writer.write(token);
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
  
  public static void write10Fold(String file) {
    List<List<String>> sentences = null;
    int numberOfSentences = 0;
    
    sentences = read(file);
    numberOfSentences = sentences.size();
    
    System.err.println(file + "\t" + sentences.size() + " sentences");
    
    Integer[] ids = null;
    ids = randomArray(numberOfSentences);
    //    
    int n = 0;
    Integer[][] aparts = null;
    aparts = apart(ids, 10);
    Integer[] group = null;
    
    // test
    for (int i = 0; i < aparts.length; ++i) {
      group = aparts[i];
      n += group.length;
      System.err.println("|" + group.length + "|" + "\t"
          + Arrays.toString(group));
      
      write(sentences, group, (file + ".test." + i));
    }
    
    // train
    Integer[] trainArray = null;
    ArrayList<Integer> train = null;
    
    for (int i = 0; i < aparts.length; ++i) {
      train = new ArrayList<Integer>();
      for (int j = 0; j < aparts.length; ++j) {
        if (i != j) {
          train.addAll(Arrays.asList(aparts[j]));
        }
      }
      
      trainArray = train.toArray(new Integer[train.size()]);
      Arrays.sort(trainArray);
      System.err.println("|" + trainArray.length + "|" + "\t"
          + Arrays.toString(trainArray));
      
      write(sentences, trainArray, (file + ".train." + i));
    }
    System.err.println("||" + n + "||");
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
    
    return sentences;
  }
  
  public static String[] getColumn(List<String> sentence, int index) {
    String[] forms = null;
    forms = new String[sentence.size()];
    
    String[] splitted = null;
    for (int i = 0; i < sentence.size(); ++i) {
      splitted = sentence.get(i).split("\t");
      forms[i] = splitted[index];
    }
    
    return forms;
  }
  
  public static void prediatePOS(String file, String out) {
    List<List<String>> sentences = read(file);
    System.out.println(sentences.size());
    
    String[] form = null;
    String[] lemma = null;
    String[] MSD = null;
    String[][] morph = null;
    
    String[] head = null;
    String[] label = null;
    
    Writer writer = null;
    
    try {
      writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
          out), "UTF-8"));
      
      for (List<String> sentence : sentences) {
        form = getColumn(sentence, 1);
        lemma = getColumn(sentence, 2);
        MSD = getColumn(sentence, 3);
        
        morph = Magyarlanc.morphParseSentence(form);
        
        head = getColumn(sentence, 6);
        label = getColumn(sentence, 7);
        
        for (int i = 0; i < form.length; ++i) {
          writer.write(form[i]);
          writer.write("\t" + lemma[i]);
          writer.write("\t" + MSD[i]);
          writer.write("\t" + morph[i][0]);
          writer.write("\t" + morph[i][1]);
          
          writer.write("\t" + head[i]);
          writer.write("\t" + label[i]);
          
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
  
  public static void eval(String file) {
    BufferedReader reader = null;
    
    String line = null;
    String[] splitted = null;
    
    int tp = 0;
    int cntr = 0;
    
    try {
      reader = new BufferedReader(new InputStreamReader(new FileInputStream(
          file), "UTF-8"));
      
      while ((line = reader.readLine()) != null) {
        if (!line.equals("")) {
          splitted = line.split("\t");
          //System.out.println(line);
          if (splitted[1].equalsIgnoreCase(splitted[3])
              && splitted[2].equals(splitted[4])) {
            ++tp;
          } else {
            // System.out.println(line);
          }
          ++cntr;
        }
        System.out.println((double) tp / cntr);
      }
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    
    System.out.println((double) tp / cntr);
  }
  
  public static void predicateDep(Parser parser, String file, String out) {
    String[] form = null;
    String[] lemma = null;
    String[] POS = null;
    String[] MSD = null;
    String[] feature = null;
    
    String[] head = null;
    String[] label = null;
    
    String[][] parsed = null;
    
    Writer writer = null;
    
    try {
      writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
          out), "UTF-8"));
      int cntr = 0;
      for (List<String> sentence : read(file)) {
        if (sentence.size() < 100) {
          form = getColumn(sentence, 0);
          
          // lemma = getColumn(sentence, 2);
          lemma = getColumn(sentence, 3);
          
          // POS = getColumn(sentence, 4);
          MSD = getColumn(sentence, 4);
          
          head = getColumn(sentence, 5);
          label = getColumn(sentence, 6);
          
          parsed = null;
          
          for (int i = 0; i < form.length; ++i) {
            writer.write(form[i]);
            writer.write("\t" + head[i]);
            writer.write("\t" + label[i]);
            writer.write("\t" + parsed[i][0]);
            writer.write("\t" + parsed[i][1]);
            writer.write("\n");
          }
          writer.write("\n");
          System.out.println(++cntr);
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
  
  public static void evalPred(String file) {
    List<List<String>> sentences = null;
    sentences = read(file);
    
    String[] splitted = null;
    
    int counter = 0;
    
    int las = 0;
    int uas = 0;
    
    for (List<String> sentence : sentences) {
      for (String token : sentence) {
        splitted = token.split("\t");
        if (splitted[1].equals(splitted[3])) {
          ++uas;
          if (splitted[2].equals(splitted[4])) {
            ++las;
          }
        }
        ++counter;
      }
    }
    
    DecimalFormat df = new DecimalFormat("#.####");
    
    System.out.print("\t" + df.format((double) las / counter) + "/"
        + df.format((double) uas / counter));
  }
  
  public static void main(String[] args) {
    // for (String corpus : new String[] { "composition", "computer", "law",
    // "literature", "newsml", "newspaper" }) {
    //      
    // // write10Fold("c:/mszny2012/10fold/" + corpus + ".corpus");
    //      
    // for (int i = 0; i < 10; ++i) {
    // // writeDepTrain("c:/mszny2012/10fold/" + corpus + ".corpus.train." + i,
    // // "c:/mszny2012/10fold/" + corpus + ".dep.train." + i);
    //        
    // writePosTrain("c:/mszny2012/10fold/" + corpus + ".corpus.train." + i,
    // "c:/mszny2012/10fold/" + corpus + ".pos.train." + i);
    //        
    // }
    // }
    // Magyarlanc.init();
    //    
    
    // String model = "newspaper";
    
    // for (String test : new String[] { /* "composition", "computer", */"law"/*
    // * ,
    // * "literature"
    // * ,
    // * "newsml"
    // * ,
    // * "newspaper"
    // */}) {
    //      
    // prediatePOS("./data/resource/mszny/test/" + test + ".corpus",
    // "./data/resource/mszny/predicated-pos/" + test + "2." + model);
    //      
    // eval("./data/resource/mszny/predicated-pos/" + test + "2." + model);
    // }
    
    //    
    // String test = "szeged";
    //    
    // String model = "szeged";
    
    // long start = System.currentTimeMillis();
    // prediatePOS("./data/resource/mszny/test/" + test + ".corpus",
    // "./data/resource/mszny/predicated-pos/" + test + "2." + model);
    // System.err.println((System.currentTimeMillis() - start) / 1000 +
    // " secs.");
    
    // eval("./data/resource/mszny/predicated-pos/" + test + "." + model);
    
    // for (String model : new String[] { "composition", "computer",
    // "literature",
    // "law", "newsml", "newspaper" }) {
    //      
    // for (String test : new String[] { "composition", "computer",
    // "literature", "law", "newsml", "newspaper" }) {
    // evalPred("./data/resource/mszny/predicated-dep/" + test + "." + model);
    // }
    // System.out.println();
    // }
    //    
    // System.exit(0);
    // Parser parser = null;
    //    
    // for (String model : new String[] { "composition", "computer",
    // "literature",
    // "law", "newsml", "newspaper" }) {
    // parser = new Parser(new Options(new String[] { "-model",
    // "./data/resource/mszny/" + model + ".dep.model", "-cores", "8" }));
    //      
    // for (String test : new String[] { "composition", "computer",
    // "literature", "law", "newsml", "newspaper" }) {
    //        
    // predicateDep(parser, "./data/resource/mszny/test/" + test + ".corpus",
    // "./data/resource/mszny/predicated-dep/" + test + "." + model);
    //        
    // }
    // parser = null;
    //      
    // }
    
    // Parser parser = null;
    //    
    // parser = new Parser(new Options(new String[] { "-model",
    // "./data/resource/mszny/szeged.dep.model", "-cores", "8" }));
    //    
    // predicateDep(parser, "./data/resource/mszny/test/szeged.szeged",
    // "./data/resource/mszny/predicated-dep/szeged.szeged.szeged");
    
    // evalPred("./data/resource/mszny/predicated-dep/szeged.szeged.szeged");
    
    //eval("./data/resource/mszny/predicated-pos/szeged.szeged");
    
    eval("c:/p2.ppp");
    
  }
}
