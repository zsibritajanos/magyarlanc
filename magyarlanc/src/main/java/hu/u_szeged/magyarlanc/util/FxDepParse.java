package hu.u_szeged.magyarlanc.util;

import is2.data.SentenceData09;
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
import java.util.LinkedList;
import java.util.List;

public class FxDepParse {
  
  static final String WORK_DIR = "./data/fx-dep-parse/";
  static final String MODEL_DIR = "model/";
  static final String REMOVED_EMPTY_NODES = "removed_empty_nodes.";
  static final String HAS_ROOT = "has_root.";
  
  private static Parser parser = null;
  
  static int truePositive = 0;
  static int falseNegative = 0;
  static int falsePositive = 0;
  
  public enum Value {
    TP, FP, FN, FP_FN
  }
  
  public static String[][] parseSentence(String[] form, String[] lemma,
      String[] pos, String[] feature) {
    
    SentenceData09 sentenceData09 = null;
    sentenceData09 = new SentenceData09();
    
    String s[] = null;
    String l[] = null;
    String p[] = null;
    String f[] = null;
    
    s = new String[form.length + 1];
    l = new String[lemma.length + 1];
    p = new String[pos.length + 1];
    f = new String[feature.length + 1];
    
    s[0] = "<root>";
    l[0] = "<root-LEMMA>";
    p[0] = "<root-POS>";
    f[0] = "<no-type>";
    
    for (int i = 0; i < form.length; ++i) {
      s[i + 1] = form[i];
    }
    
    for (int i = 0; i < lemma.length; ++i) {
      l[i + 1] = lemma[i];
    }
    
    for (int i = 0; i < pos.length; ++i) {
      p[i + 1] = pos[i];
    }
    
    for (int i = 0; i < feature.length; ++i) {
      f[i + 1] = feature[i];
    }
    
    sentenceData09.init(s);
    sentenceData09.setLemmas(l);
    sentenceData09.setPPos(p);
    sentenceData09.setFeats(f);
    
    sentenceData09 = parser.apply(sentenceData09);
    
    String[][] result = null;
    result = new String[sentenceData09.plabels.length][2];
    
    for (int i = 0; i < sentenceData09.plabels.length; ++i) {
      result[i][0] = String.valueOf(sentenceData09.pheads[i]);
      result[i][1] = sentenceData09.plabels[i];
    }
    
    return result;
  }
  
  public static void split(String file) {
    List<List<String>> sentences = null;
    sentences = read(WORK_DIR + HAS_ROOT + REMOVED_EMPTY_NODES + file);
    
    // a
    write(sentences.subList(0, sentences.size() / 2), WORK_DIR + "a." + file);
    System.err.println("a\t" + read(WORK_DIR + "a." + file).size());
    
    // b
    write(sentences.subList(sentences.size() / 2, sentences.size()), WORK_DIR
        + "b." + file);
    System.err.println("b\t" + read(WORK_DIR + "b." + file).size());
    
  }
  
  public static List<List<String>> read(String file) {
    
    BufferedReader reader = null;
    String line = null;
    
    List<String> sentence = null;
    List<List<String>> sentences = null;
    
    sentence = new LinkedList<String>();
    sentences = new LinkedList<List<String>>();
    
    try {
      reader = new BufferedReader(new InputStreamReader(new FileInputStream(
          file), "UTF-8"));
      while ((line = reader.readLine()) != null) {
        if (!line.equals("")) {
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
    return sentences;
  }
  
  public static void write(List<List<String>> sentences, String file) {
    
    Writer writer = null;
    
    try {
      writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
          file), "UTF8"));
      
      for (List<String> sentence : sentences) {
        if (sentence.size() < 100) {
          for (String token : sentence) {
            writer.write(token);
            writer.write("\n");
          }
          writer.write("\n");
        } else {
          System.err.println("|" + sentence.size() + "|");
          for (String line : sentence)
            System.err.println(line);
          System.err.println();
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
  
  public static boolean containsRoot(List<String> sentence, int index) {
    for (String line : sentence) {
      if (line.split("\t")[index].equals("ROOT"))
        return true;
    }
    return false;
  }
  
  public static void prepare(String file) {
    System.err.println(file);
    System.err.println(read(WORK_DIR + file).size());
    
    // virtualis node-ok eltavolitasa
    try {
      hu.u_szeged.dep.util.RemoveEmptyNodes.processFile(WORK_DIR + file,
          WORK_DIR + REMOVED_EMPTY_NODES + file);
    } catch (IOException e) {
      e.printStackTrace();
    }
    
    List<List<String>> sentences = null;
    sentences = read(WORK_DIR + REMOVED_EMPTY_NODES + file);
    System.err.println(sentences.size());
    
    // root nelkuli mondatok
    List<List<String>> hasRoot = null;
    hasRoot = new LinkedList<List<String>>();
    
    for (List<String> sentence : sentences) {
      if (containsRoot(sentence, 10)) {
        hasRoot.add(sentence);
      }
    }
    
    System.err.println(hasRoot.size());
    write(hasRoot, WORK_DIR + HAS_ROOT + REMOVED_EMPTY_NODES + file);
    
    sentences = null;
    split(file);
  }
  
  public static String[] getColumn(List<String> sentence, int index) {
    String[] column = null;
    column = new String[sentence.size()];
    
    for (int i = 0; i < column.length; ++i) {
      
      column[i] = sentence.get(i).split("\t")[index];
    }
    
    return column;
  }
  
  public static void predicate(String file, String model, String pred) {
    
    if (parser == null) {
      parser = new Parser(new Options(new String[] { "-model", model, "-cores",
          "1" }));
    }
    
    List<List<String>> sentences = null;
    sentences = read(file);
    
    Writer writer = null;
    try {
      writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
          pred), "UTF8"));
      
      int cntr = 0;
      String[][] parsed = null;
      
      for (List<String> sentence : sentences) {
        if (sentence.size() < 100) {
          parsed = parseSentence(getColumn(sentence, 1),
              getColumn(sentence, 2), getColumn(sentence, 4), getColumn(
                  sentence, 6));
          
          for (int i = 0; i < getColumn(sentence, 10).length; ++i) {
            writer.write(getColumn(sentence, 1)[i]);
            writer.write("\t" + getColumn(sentence, 8)[i]);
            writer.write("\t" + getColumn(sentence, 10)[i]);
            writer.write("\t" + parsed[i][0]);
            writer.write("\t" + parsed[i][1]);
            writer.write("\n");
          }
          writer.write("\n");
          System.err.println(++cntr + "/" + sentences.size());
          
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
  
  public static void reduce(String file) {
    
    List<List<String>> sentences = null;
    sentences = read(WORK_DIR + file);
    
    Writer writer = null;
    try {
      writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
          WORK_DIR + file + ".shorted"), "UTF8"));
      
      for (List<String> sentence : sentences) {
        
        for (int i = 0; i < sentence.size(); ++i) {
          writer.write(getColumn(sentence, 0)[i]);
          writer.write("\t" + getColumn(sentence, 1)[i]);
          writer.write("\t" + getColumn(sentence, 8)[i]);
          writer.write("\t" + getColumn(sentence, 10)[i]);
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
  
  public static Value evalToken(String parent, String pparent, String label,
      String plabel) {
    
    // 2 2 OBJFX OBJFX
    if (parent.equals(pparent) && label.equals(plabel)) {
      return Value.TP;
    }
    
    // O OBJFX
    if (!label.endsWith("FX") && plabel.endsWith("FX")) {
      return Value.FP;
    }
    
    // OBJFX O
    if (label.endsWith("FX") && !plabel.endsWith("FX")) {
      return Value.FN;
    }
    
    // OBJFX ATTFX
    if (label.endsWith("FX") && plabel.endsWith("FX") && !label.equals(plabel)) {
      return Value.FP_FN;
    }
    
    // 2 5 OBJFX OBJFX
    if (label.equals(plabel) && !parent.equals(pparent)) {
      return Value.FP;
    }
    
    return null;
  }
  
  public static void evalSentence(String[] parent, String[] pparent,
      String[] label, String[] plabel, String[] form) {
    
    Value value = null;
    for (int i = 0; i < parent.length; ++i) {
      if (label[i].endsWith("FX") || plabel[i].endsWith("FX")) {
        value = evalToken(parent[i], pparent[i], label[i], plabel[i]);
        
        switch (value) {
          case TP:
            ++truePositive;
          break;
          case FP:
            ++falsePositive;
          break;
          case FN:
            ++falseNegative;
          break;
          case FP_FN:
            ++falseNegative;
            ++falsePositive;
          break;
        }
        
        // System.err.println(parent[i] + "\t" + pparent[i] + "\t" + label[i]
        // + "\t" + plabel[i] + "\t" + value + "\t" + form[i]);
      }
    }
    
  }
  
  public static void eval(String file) {
    List<List<String>> sentences = null;
    sentences = read(file);
    
    truePositive = 0;
    falseNegative = 0;
    falsePositive = 0;
    
    for (List<String> sentence : sentences) {
      evalSentence(getColumn(sentence, 1), getColumn(sentence, 3), getColumn(
          sentence, 2), getColumn(sentence, 4), getColumn(sentence, 0));
    }
    
    System.err.println("TP: " + truePositive + "\tFN: " + falseNegative
        + "\tFP: " + falsePositive);
    
    float precision = 0.0f;
    float recall = 0.0f;
    
    precision = (float) truePositive / (truePositive + falsePositive);
    recall = (float) truePositive / (truePositive + falseNegative);
    
    System.err.print("P: " + precision);
    System.err.print("\tR: " + recall);
    
    System.err.println("\tF: " + 2 * precision
        * (recall / (precision + recall)));
    // System.err.println("F: " + (float) (2 * truePositive)
    // / (2 * truePositive + falsePositive + falseNegative));
    
  }
  
  public static void LAS(String file) {
    List<List<String>> sentences = null;
    sentences = read(file);
    
    String[] splitted = null;
    
    int counter = 0;
    int las = 0;
    for (List<String> sentence : sentences) {
      for (String token : sentence) {
        splitted = token.split("\t");
        if (splitted[1].equals(splitted[3]) && splitted[2].equals(splitted[4])) {
          ++las;
        }
        ++counter;
      }
    }
    System.out.println("LAS:" + (double) las / counter);
  }
  
  public static void UAS(String file) {
    List<List<String>> sentences = null;
    sentences = read(file);
    
    String[] splitted = null;
    
    int counter = 0;
    int uas = 0;
    for (List<String> sentence : sentences) {
      for (String token : sentence) {
        splitted = token.split("\t");
        if (splitted[1].equals(splitted[3])) {
          ++uas;
        }
        ++counter;
      }
    }
    System.out.println("UAS:" + (double) uas / counter);
  }
  
  public static void rewriteAnnotation(String corpusFile, String correctedFile,
      String out) {
    List<List<String>> corpus = null;
    List<List<String>> corrected = null;
    
    corpus = read(WORK_DIR + corpusFile);
    corrected = read(WORK_DIR + correctedFile);
    String[] splittedCorpus = null;
    String[] splittedCorrected = null;
    Writer writer = null;
    
    try {
      writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
          WORK_DIR + out), "UTF8"));
      
      for (int i = 0; i < corpus.size(); ++i) {
        for (int j = 0; j < corpus.get(i).size(); ++j) {
          splittedCorpus = corpus.get(i).get(j).split("\t");
          splittedCorrected = corrected.get(i).get(j).split("\t");
          
          writer.write(splittedCorpus[0]);
          for (int k = 1; k < 10; ++k) {
            writer.write("\t" + splittedCorpus[k]);
          }
          writer.write("\t" + splittedCorrected[3]);
          writer.write("\t" + splittedCorrected[3]);
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
  
  public static int[] getFoldOffsets(String file) {
    BufferedReader bufferedReader = null;
    String line = null;
    
    int offset = 0;
    int sentenceCounter = 0;
    
    try {
      bufferedReader = new BufferedReader(new InputStreamReader(
          new FileInputStream(file), "UTF-8"));
      
      while ((line = bufferedReader.readLine()) != null) {
        sentenceCounter += Integer.parseInt(line.split("\t")[1]);
        if ((++offset % 120) == 0) {
          System.out.println(offset + "\t" + sentenceCounter);
        }
      }
      System.out.println(offset);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    
    return null;
  }
  
  public static boolean containsVirtualNode(List<String> sentence) {
    String[] forms = null;
    forms = getColumn(sentence, 1);
    
    for (String form : forms)
      if (form.equals("_"))
        return true;
    
    return false;
  }
  
  public static void writeFolds(String file) {
    List<List<String>> sentences = null;
    sentences = read(file);
    
    int[] foldOffsets = null;
    
    foldOffsets = new int[] { 0, 688, 1470, 2221, 2848, 4113, 4755, 5666, 6961,
        8190, sentences.size() };
    
    Writer testWriter = null;
    Writer trainWriter = null;
    
    for (int i = 0; i < foldOffsets.length - 1; ++i) {
      try {
        testWriter = new BufferedWriter(new OutputStreamWriter(
            new FileOutputStream("./data/objfx/10fold-removed-virtuals2/law."
                + i + ".test"), "UTF8"));
        trainWriter = new BufferedWriter(new OutputStreamWriter(
            new FileOutputStream("./data/objfx/10fold-removed-virtuals2/law."
                + i + ".train"), "UTF8"));
        
        for (int j = 0; j < sentences.size(); ++j) {
          // virtualokat tart mondatok nekiul
          if (!containsVirtualNode(sentences.get(j))
              && sentences.get(j).size() < 200) {
            if (j >= foldOffsets[i] && j <= (foldOffsets[i + 1] - 1)) {
              for (String s : sentences.get(j)) {
                // testWriter.write(s + "\t_\t_\n");
                testWriter.write(s + "\n");
              }
              testWriter.write("\n");
            } else {
              for (String s : sentences.get(j)) {
                // trainWriter.write(s + "\t_\t_\n");
                trainWriter.write(s + "\n");
              }
              trainWriter.write("\n");
            }
          }
        }
        
        testWriter.flush();
        trainWriter.flush();
        testWriter.close();
        trainWriter.close();
      } catch (UnsupportedEncodingException e) {
        e.printStackTrace();
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
      }
      // System.out.println(foldOffsets[i] + "\t" + ));
    }
  }
  
  public static void main(String[] args) {
    // rewriteAnnotation("gazdtar.dep.fx.out", "gazdtar.dep.fx.out_jav",
    // "gazdtar.dep.fx.out.corrected");
    
    // for (String file : new String[] { /* "gazdtar", */"szerzj" })
    // prepare(file + ".dep.fx.out");
    
    // for (int i = 0; i < 10; ++i) {
    // predicate("./data/objfx/10fold-removed-virtuals/law." + i + ".test",
    // "./data/objfx/10fold-removed-virtuals/law." + i + ".model",
    // "./data/objfx/10fold-removed-virtuals/law." + i + ".pred");
    // }
    
    //    
    // reduce("szerzj.dep.fx.out");
    
    // evalSentence(new String[] { "12", "14", "14", "14", "14" }, new String[]
    // {
    // "12", "15", "14", "14", "14" }, new String[] { "OBJFX", "OBJFX",
    // "OBJFX", "OBJ", "OBJFX" }, new String[] { "OBJFX", "OBJFX", "OBJ",
    // "OBJFX", "ATTFX" });
    
    for (int i = 0; i < 10; ++i) {
      eval("./data/objfx/10fold-removed-virtuals/law." + i + ".pred");
    }
    
    // LAS("./data/objfx/10fold-removed-virtuals/law.0123456789.pred");
    // UAS("./data/objfx/10fold-removed-virtuals/law.0123456789.pred");
    
    // getFoldOffsets("./data/objfx/doc-sentences.txt");
    // writeFolds("./data/objfx/law.out");
  }
}
