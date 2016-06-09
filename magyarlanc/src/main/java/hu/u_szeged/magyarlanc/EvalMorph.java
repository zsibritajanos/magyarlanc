package hu.u_szeged.magyarlanc;

import hu.u_szeged.config.Config;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by zsibritajanos on 2016.01.19..
 */
public class EvalMorph {
  public static List<List<String>> read(String file, String encoding) {

    BufferedReader reader = null;

    String line;
    List<String> sentence = new ArrayList<>();
    List<List<String>> sentences = new LinkedList<>();

    try {
      reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), encoding));

      while ((line = reader.readLine()) != null) {
        if (line.trim().equals("")) {
          sentences.add(sentence);
          sentence = new ArrayList<>();
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
    } finally {
      try {
        reader.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    System.out.println(sentences.size() + " sentences found.");

    return sentences;
  }

  /**
   * @param sentence
   * @param separator
   * @param index
   * @return
   */
  public static String[] getColumn(List<String> sentence, String separator, int index) {
    String[] forms = new String[sentence.size()];

    String[] splitted;
    for (int i = 0; i < sentence.size(); ++i) {
      splitted = sentence.get(i).split(separator);
      forms[i] = splitted[index];
    }

    return forms;
  }

  /**
   * @param file
   * @param encoding
   * @param out
   */
  public static void prediateMorph(String file, String encoding, String out) {

    List<List<String>> sentences = read(file, encoding);

    String[] form;
    String[] lemma;
    String[] pos;
    String[] feature;

    String[][] morph;

    Writer writer = null;

    int c = 0;
    try {
      writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(out), encoding));

      for (List<String> sentence : sentences) {

        form = getColumn(sentence, "\t", 0);
        lemma = getColumn(sentence, "\t", 1);
        pos = getColumn(sentence, "\t", 2);
        feature = getColumn(sentence, "\t", 3);

        morph = Magyarlanc.morphParseSentence(form);

        for (int i = 0; i < form.length; ++i) {
          writer.write(form[i]);

          // lemma
          writer.write("\t" + lemma[i]);
          writer.write("\t" + morph[i][1]);

          // pos
          writer.write("\t" + pos[i]);
          writer.write("\t" + morph[i][2]);

          // feature
          writer.write("\t" + feature[i]);
          writer.write("\t" + morph[i][3]);

          writer.write("\n");
        }
        writer.write("\n");

        if (++c % 1000 == 0) {
          writer.flush();
          System.out.println(c + "/" + sentences.size());
        }
      }
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
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

  /**
   * @param file
   * @param encoding
   * @param out
   */
  public static void prediateDep(String file, String encoding, String out) {

    List<List<String>> sentences = read(file, encoding);

    String[] idx;
    String[] form;
    String[] lemma;
    String[] pos;
    String[] feature;
    String[] head;
    String[] rel;

    String[][] dep;

    Writer writer = null;

    int c = 0;
    try {
      writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(out), encoding));


      for (List<String> sentence : sentences) {
        if (sentence.size() > Config.getInstance().getDepMaxSentenceLength()) {
          continue;
        }

        idx = getColumn(sentence, "\t", 0);
        form = getColumn(sentence, "\t", 1);

        lemma = getColumn(sentence, "\t", 2);

        pos = getColumn(sentence, "\t", 4);

        feature = getColumn(sentence, "\t", 6);


        head = getColumn(sentence, "\t", 8);
        rel = getColumn(sentence, "\t", 10);


        //dep = Magyarlanc.depParseSentence(form, lemma, pos, feature);
        dep = Magyarlanc.depParseSentence(form);

        for (int i = 0; i < form.length; ++i) {

          //idx
          writer.write(idx[i]);
          //form
          writer.write("\t" + form[i]);
          // lemma
          writer.write("\t" + lemma[i]);
          // pos
          writer.write("\t" + pos[i]);
          // feature
          writer.write("\t" + feature[i]);

          //head
          writer.write("\t" + head[i]);
          writer.write("\t" + dep[i][5]);

          // rel
          writer.write("\t" + rel[i]);
          writer.write("\t" + dep[i][6]);

          writer.write("\n");
        }
        writer.write("\n");

        if (++c % 1000 == 0) {
          writer.flush();
          System.out.println(c + "/" + sentences.size());
        }

      }

    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
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

  private static double evalMorph(String predFile) {
    List<String> lines = null;
    try {
      lines = Files.readAllLines(Paths.get(predFile), StandardCharsets.UTF_8);
    } catch (IOException e) {
      e.printStackTrace();
    }

    int ok = 0;
    int err = 0;

    for (String line : lines) {
      if (line.trim().length() > 0) {
        String[] split = line.trim().split("\t");

        if (split[1].equalsIgnoreCase(split[2]) && split[3].equalsIgnoreCase(split[4]) && split[5].equalsIgnoreCase(split[6])) {

          ++ok;
        } else {
          //System.out.println(line);
          ++err;
        }
      }
    }

    return (double) ok / ((double) (ok + err));
  }

  private static void evalDep(String predFile) {
    List<String> lines = null;
    try {
      lines = Files.readAllLines(Paths.get(predFile), StandardCharsets.UTF_8);
    } catch (IOException e) {
      e.printStackTrace();
    }

    int lasOk = 0;
    int lasErr = 0;

    int ulaOk = 0;
    int ulaErr = 0;

    for (String line : lines) {
      if (line.trim().length() > 0) {
        String[] split = line.trim().split("\t");

        // LAS
        if (split[5].equals(split[6]) && split[7].equalsIgnoreCase(split[8])) {
          ++lasOk;
        } else {
          //System.err.println(line);
          ++lasErr;
        }
        // UAS
        if (split[5].equalsIgnoreCase(split[6])) {
          ++ulaOk;
        } else {
          //System.err.println(line);
          ++ulaErr;
        }
      }
    }

    double las = (double) lasOk / ((double) (lasOk + lasErr));
    double ula = (double) ulaOk / ((double) (ulaOk + ulaErr));


    System.out.println("LAS: " + las);
    System.out.println("ULA: " + ula);
  }


  public static void main(String[] args) throws IOException {

//    prediateDep("./szk.mate.test", "utf-8", "./szk.mate.pred.pred");

    prediateDep("./face.uddep.virt", "utf-8", "./face.uddep.virt.pred");
    prediateDep("./faq.uddep.virt", "utf-8", "./faq.uddep.virt.pred");
    evalDep("./face.uddep.virt.pred");
    evalDep("./faq.uddep.virt.pred");

//    prediateMorph("./face.test", "utf-8", "./face.out");
//    System.out.println(evalMorph("./face.out"));
//    prediateMorph("./faq.test", "utf-8", "./faq.out");
//    System.out.println(evalMorph("./faq.out"));

//    System.out.println(evalMorph(predFile));

//    evalDep("./faq.uddep.pred");
  }
}
