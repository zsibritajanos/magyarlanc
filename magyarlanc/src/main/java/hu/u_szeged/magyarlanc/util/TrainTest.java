package hu.u_szeged.magyarlanc.util;

import is2.parser.Options;
import is2.parser.Parser;

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
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class TrainTest {
  
  private static final String WORK_DIR = "./data/fx-dep/";
  private static final String TRAIN_DIR = "train/";
  private static final String TEST_DIR = "test/";
  
  private static final String CORPUS2_DIR = "corpus2/";
  private static final String TEST_2_DIR = "test2/";
  private static final String TRAIN_2_DIR = "train2/";
  
  private static final String MODEL_DIR = "model/";
  private static final String PRED_DIR = "predicated/";
  
  private static final String CORPUS_DIR = "corpus/";
  
  static final String NO_DOCSTART = "no_docstart.";
  static final String REMOVED_EMPTY_NODES = "removed_empty_nodes.";
  static final String HAS_ROOT = "has_root.";
  
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
  
  public static List<List<String>> read(String file) {
    
    BufferedReader reader = null;
    String line = null;
    
    List<String> sentence = null;
    List<List<String>> sentences = null;
    List<List<List<String>>> documents = null;
    
    sentence = new LinkedList<String>();
    sentences = new LinkedList<List<String>>();
    documents = new LinkedList<List<List<String>>>();
    
    try {
      reader = new BufferedReader(new InputStreamReader(new FileInputStream(
          file), "UTF-8"));
      while ((line = reader.readLine()) != null) {
        
        if (line.equals("--DOCSTART--")) {
          // documents.add(sentences);
          // sentences = new LinkedList<List<String>>();
          reader.readLine();
        }

        else if (!line.equals("")) {
          sentence.add(line);
        } else if (line.equals("")) {
          sentences.add(sentence);
          sentence = new LinkedList<String>();
        }
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    // System.out.println(sentences.size());
    return sentences;
  }
  
  public static String[] getColumn(List<String> sentence, int index) {
    String[] column = null;
    column = new String[sentence.size()];
    
    for (int i = 0; i < column.length; ++i) {
      
      column[i] = sentence.get(i).split("\t")[index];
    }
    
    return column;
  }
  
  public static void write(List<List<String>> sentences, Integer[] ids,
      String out, boolean removeColumn) {
    Writer writer = null;
    
    String[] splitted = null;
    try {
      writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
          out), "UTF-8"));
      
      for (int id : ids) {
        for (String token : sentences.get(id)) {
          if (removeColumn) {
            splitted = token.split("\t");
            
            writer.write(splitted[0]);
            for (int i = 1; i < 12; ++i) {
              writer.write("\t" + splitted[i]);
            }
            
            for (int i = 13; i < splitted.length; ++i) {
              writer.write("\t" + splitted[i]);
            }
          } else {
            writer.write(token);
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
  
  // public static void removeDocstart(String file) {
  // write(read(WORK_DIR + file), WORK_DIR + NO_DOCSTART + file);
  //    
  // }
  
  public static void removeLongSentences(String file, int length) {
    List<List<String>> sentences = null;
    sentences = read(WORK_DIR + CORPUS_DIR + file);
    Writer writer = null;
    
    try {
      writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
          WORK_DIR + CORPUS_DIR + length + "_" + file), "UTF-8"));
      
      for (List<String> sentence : sentences) {
        if (sentence.size() < length) {
          for (String token : sentence) {
            writer.write(token);
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
  
  public static void write10Fold(String file) {
    System.err.println(file);
    
    // removeEmptyNodes(file, false);
    // removeLongSentences(HAS_ROOT + REMOVED_EMPTY_NODES + file, 100);
    
    // List<List<String>> sentences = read(WORK_DIR + CORPUS_DIR + 100 + "_"
    // + HAS_ROOT + REMOVED_EMPTY_NODES + file);
    
    // System.err.println("100<\t" + sentences.size());
    
    List<List<String>> sentences = read(WORK_DIR + CORPUS2_DIR
        + REMOVED_EMPTY_NODES + file);
    
    int numberOfSentences = 0;
    numberOfSentences = sentences.size();
    
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
      
      write(sentences, group, (WORK_DIR + TEST_2_DIR + file + ".test." + i),
          false);
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
      
      write(sentences, trainArray,
          (WORK_DIR + TRAIN_2_DIR + file + ".train." + i), true);
    }
    System.err.println("||" + n + "||");
  }
  
  public static boolean containsRoot(List<String> sentence, int index) {
    for (String line : sentence) {
      if (line.split("\t")[index].equals("ROOT"))
        return true;
    }
    return false;
  }
  
  public static void write(List<List<String>> sentences, String file) {
    
    Writer writer = null;
    
    try {
      writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
          file), "UTF8"));
      
      for (List<String> sentence : sentences) {
        for (String token : sentence) {
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
  
  public static void removeEmptyNodes(String file, boolean removeNonRoot) {
    // System.err.println(file);
    System.err.println("\t" + read(WORK_DIR + CORPUS2_DIR + file).size());
    
    // virtualis node-ok eltavolitasa
    try {
      hu.u_szeged.dep.util.RemoveEmptyNodes.processFile(WORK_DIR + CORPUS2_DIR
          + file, WORK_DIR + CORPUS2_DIR + REMOVED_EMPTY_NODES + file);
    } catch (IOException e) {
      e.printStackTrace();
    }
    
    List<List<String>> sentences = null;
    sentences = read(WORK_DIR + CORPUS2_DIR + REMOVED_EMPTY_NODES + file);
    System.err.println("VAN/ELL\t" + sentences.size());
    
    // root nelkuli mondatok
    
    if (removeNonRoot) {
      List<List<String>> hasRoot = null;
      hasRoot = new LinkedList<List<String>>();
      
      for (List<String> sentence : sentences) {
        if (containsRoot(sentence, 10)) {
          hasRoot.add(sentence);
        }
      }
      
      System.err.println("ROOT\t" + hasRoot.size());
      write(hasRoot, WORK_DIR + CORPUS_DIR + HAS_ROOT + REMOVED_EMPTY_NODES
          + file);
    }
  }
  
  public static void preidcate(String testFile, String modelFile, Writer writer)
      throws IOException {
    Parser parser = null;
    List<List<String>> sentences = null;
    sentences = read(testFile);
    List<String> sentence = null;
    
    parser = new Parser(new Options(new String[] { "-model", modelFile,
        "-cores", "1" }));
    String[][] prediction = null;
    
    String[] id = null;
    String[] form = null;
    String[] lemma = null;
    String[] plemma = null;
    String[] pos = null;
    String[] ppos = null;
    String[] feat = null;
    String[] pfeat = null;
    String[] head = null;
    String[] rel = null;
    String[] fx = null;
    
    for (int i = 0; i < sentences.size(); ++i) {
      if ((i % 200) == 0)
        System.out.print(sentences.size() + "/" + i + " ");
      
      sentence = sentences.get(i);
      
      id = getColumn(sentence, 0);
      form = getColumn(sentence, 1);
      lemma = getColumn(sentence, 2);
      plemma = getColumn(sentence, 3);
      pos = getColumn(sentence, 4);
      ppos = getColumn(sentence, 5);
      feat = getColumn(sentence, 6);
      pfeat = getColumn(sentence, 7);
      head = getColumn(sentence, 8);
      // 9
      rel = getColumn(sentence, 10);
      // 11
      fx = getColumn(sentence, 12);
      
      // prediction = DParser.parseSentence(getColumn(sentence, 1), getColumn(
      // sentence, 3), getColumn(sentence, 5), getColumn(sentence, 7), parser);
      
      for (int j = 0; j < id.length; ++j) {
        writer.write(id[j]);
        writer.write("\t" + form[j]);
        writer.write("\t" + lemma[j]);
        writer.write("\t" + plemma[j]);
        writer.write("\t" + pos[j]);
        writer.write("\t" + ppos[j]);
        writer.write("\t" + feat[j]);
        writer.write("\t" + pfeat[j]);
        // HEAD
        writer.write("\t" + head[j]);
        writer.write("\t" + prediction[j][0]);
        // REL
        writer.write("\t" + rel[j]);
        writer.write("\t" + prediction[j][1]);
        // FX
        writer.write("\t" + fx[j]);
        
        writer.write("\n");
      }
      writer.write("\n");
      
    }
    System.out.println();
    
  }
  
  public static void predicateCorpus(String corpus) {
    String testFile = null;
    String modelFile = null;
    String predFile = null;
    
    Writer writer = null;
    predFile = WORK_DIR + PRED_DIR + corpus + ".dep.fx.pred";
    try {
      writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
          predFile), "UTF-8"));
      for (int i = 0; i < 10; ++i) {
        testFile = WORK_DIR + TEST_DIR + corpus + ".dep.fx.test." + i;
        modelFile = WORK_DIR + MODEL_DIR + corpus + ".dep.fx.model." + i;
        System.out.println(testFile + "\t" + modelFile + "\t" + predFile);
        preidcate(testFile, modelFile, writer);
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
  
  public static void mergeSubCorpuses(String[] subCorpuses, String out) {
    Writer writer = null;
    List<List<String>> sentences = null;
    String[] splitted = null;
    try {
      writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
          WORK_DIR + CORPUS_DIR + out), "UTF-8"));
      
      for (String subCorpus : subCorpuses) {
        sentences = read(WORK_DIR + subCorpus);
        System.err.println(subCorpus + "\t" + sentences.size());
        for (List<String> sentence : sentences) {
          for (String token : sentence) {
            splitted = token.split("\t");
            
            writer.write(splitted[0]);
            for (int i = 1; i < 8; ++i) {
              writer.write("\t" + splitted[i]);
            }
            
            for (int i = 10; i < splitted.length; ++i) {
              writer.write("\t" + splitted[i]);
            }
            
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
    
    System.err.println("\n" + out + "\t\t"
        + read(WORK_DIR + CORPUS_DIR + out).size() + "\n\n");
  }
  
  public static double LAS(String file) {
    List<List<String>> sentences = null;
    sentences = read(WORK_DIR + PRED_DIR + file);
    
    String[] splitted = null;
    
    int counter = 0;
    int las = 0;
    int uas = 0;
    for (List<String> sentence : sentences) {
      for (String token : sentence) {
        splitted = token.split("\t");
        if (splitted[2].equals(splitted[3]) && splitted[4].equals(splitted[5])
            && splitted[6].equals(splitted[7])
            && splitted[8].equals(splitted[9])
            && splitted[10].equals(splitted[11])) {
          ++las;
        }
        ++counter;
      }
    }
    
    return ((double) las / counter);
  }
  
  public static double UAS(String file) {
    List<List<String>> sentences = null;
    sentences = read(WORK_DIR + PRED_DIR + file);
    
    String[] splitted = null;
    
    int counter = 0;
    int uas = 0;
    for (List<String> sentence : sentences) {
      for (String token : sentence) {
        splitted = token.split("\t");
        if (splitted[8].equals(splitted[9])) {
          ++uas;
        }
        ++counter;
      }
    }
    return ((double) uas / counter);
  }
  
  public static void main(String args[]) {
    
    // merge
    
    // mergeSubCorpuses(new String[] { "10erv.dep.fx", "10elb.dep.fx",
    // "8oelb.dep.fx" }, "composition.dep.fx");
    //    
    // mergeSubCorpuses(new String[] { "utas.dep.fx", "pfred.dep.fx",
    // "1984.dep.fx" }, "literature.dep.fx");
    //    
    // mergeSubCorpuses(new String[] { "gazdtar.dep.fx", "szerzj.dep.fx" },
    // "law.dep.fx");
    //    
    // mergeSubCorpuses(new String[] { "nv.dep.fx", "np.dep.fx", "hvg.dep.fx",
    // "mh.dep.fx" }, "newspaper.dep.fx");
    //    
    // mergeSubCorpuses(new String[] { "newsml.dep.fx" }, "newsml.dep.fx");
    
    // 10 fold
    
    // for (String corpus : new String[] { "composition", "law", "newsml",
    // "newspaper", "literature" })
    // write10Fold(corpus + ".dep.fx");
    
    // predicate
    
    // for (String corpus : new String[] { "newspaper", "literature",
    // "composition", "newsml", "law" })
    // predicateCorpus(corpus);
    
    // for (String corpus : new String[] { "newspaper", "literature",
    // "composition", "newsml", "law" }) {
    // System.out.println(corpus + "\t" + UAS(corpus + ".dep.fx.pred") + "\t"
    // + LAS(corpus + ".dep.fx.pred"));
    
    // System.out.println(corpus + "\t" + LAS(corpus + ".dep.fx.pred"));
    // }
    
  }
}
