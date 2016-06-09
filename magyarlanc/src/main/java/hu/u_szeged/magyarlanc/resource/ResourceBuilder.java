package hu.u_szeged.magyarlanc.resource;

import hu.u_szeged.magyarlanc.MorAna;

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
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class ResourceBuilder {

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
    } finally{
      try {
        reader.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    return sentences;
  }

  public static void minimalize(List<List<String>> sentences, String out) {
    String id = null;
    String form = null;
    String lemma = null;
    String POS = null;
    String feature = null;
    String head = null;
    String label = null;

    String MSD = null;

    Writer writer = null;

    String[] splitted = null;
    StringBuilder stringBuilder = null;
    try {
      writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
          out), "UTF-8"));

      for (List<String> sentence : sentences) {
        for (String token : sentence) {
          splitted = token.split("\t");
          id = splitted[0];
          form = splitted[1];
          lemma = splitted[2];
          POS = splitted[4];
          feature = splitted[6];
          head = splitted[8];
          label = splitted[10];
          MSD = ResourceHolder.getCoNLLFeaturesToMSD().convert(POS, feature);

          if (lemma.equals("-e"))
            MSD = MSD + "-y";

          // Nn-
          if (MSD.startsWith("N")) {
            stringBuilder = new StringBuilder(MSD);
            stringBuilder.setCharAt(1, 'n');
            MSD = stringBuilder.toString();

            stringBuilder = new StringBuilder(feature);
            stringBuilder.setCharAt(7, 'n');
            feature = stringBuilder.toString();
          }

          writer.write(id + "\t" + form + "\t" + lemma + "\t" + MSD + "\t"
              + POS + "\t" + feature + "\t" + head + "\t" + label + "\n");

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

  public static boolean containsZ(List<String> sentence) {
    String[] splitted = null;
    for (String token : sentence) {
      splitted = token.split("\t");
      if (splitted[4].equals("Z")) {
        // System.err.println(token);
        return true;
      }
    }
    return false;
  }

  public static void removeEmptyNodes(String file) {
    // System.err.println(file);
    System.err.println("\t" + read(file).size());

    // virtualis node-ok eltavolitasa
    try {
      hu.u_szeged.dep.util.RemoveEmptyNodes.processFile(file, file
          + ".removed-virtuals");
    } catch (IOException e) {
      e.printStackTrace();
    }

    List<List<String>> sentences = null;
    sentences = read(file + ".removed-virtuals");
    System.err.println("VAN/ELL\t" + sentences.size());

    // root nelkuli mondatok

    List<List<String>> hasRoot = null;
    hasRoot = new LinkedList<List<String>>();

    for (List<String> sentence : sentences) {
      if (containsRoot(sentence, 10)) {
        hasRoot.add(sentence);
      }
    }

    System.err.println("ROOT\t" + hasRoot.size());
    write(hasRoot, file + ".removed-virtuals" + ".has-root");
  }

  public static void removeZ(String file) {
    List<List<String>> sentences = null;
    sentences = read(file);
    // System.err.println(sentences.size());

    // root nelkuli mondatok

    List<List<String>> noZ = null;
    noZ = new LinkedList<List<String>>();

    for (List<String> sentence : sentences) {
      if (!containsZ(sentence)) {
        noZ.add(sentence);
      }
    }

    System.err.println("no Z\t" + noZ.size());
    write(noZ, file + ".no-z");
  }

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
          writer.write(form + "@" + ResourceHolder.getMSDReducer().reduce(MSD)
              + " ");
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

  public static void writeCorpus(String file, String out) {

    String[] splitted = null;
    String form = null;
    String lemma = null;
    String MSD = null;

    Map<String, Set<MorAna>> corpus = null;
    corpus = new TreeMap<String, Set<MorAna>>();

    MorAna morAna = null;

    for (List<String> sentence : read(file)) {
      for (String token : sentence) {
        splitted = token.split("\t");
        form = splitted[1];
        lemma = splitted[2];
        MSD = splitted[3];
        morAna = new MorAna(lemma, MSD);

        if (!corpus.containsKey(form))
          corpus.put(form, new TreeSet<MorAna>());

        corpus.get(form).add(morAna);
      }
    }

    Writer writer = null;
    try {
      writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
          out), "UTF-8"));

      for (Entry<String, Set<MorAna>> enrty : corpus.entrySet()) {
        writer.write(enrty.getKey());

        for (MorAna m : enrty.getValue())
          writer.write("\t" + m.getLemma() + "\t" + m.getMsd());

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

  public static void writeFreq(String file, String out) {
    String[] splitted = null;
    Writer writer = null;

    String msd = null;

    Map<String, Integer> freq = null;
    freq = new TreeMap<String, Integer>();

    for (List<String> sentence : read(file)) {
      for (String token : sentence) {
        splitted = token.split("\t");
        msd = splitted[3];

        if (!freq.containsKey(msd))
          freq.put(msd, 0);

        freq.put(msd, freq.get(msd) + 1);
      }
    }

    try {
      writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
          out), "UTF-8"));

      for (Entry<String, Integer> enrty : freq.entrySet()) {
        writer.write(enrty.getKey() + "\t" + enrty.getValue() + "\n");
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

  public static void merge(String[] files, String out) {
    List<List<String>> merged = null;
    merged = new LinkedList<List<String>>();

    for (String file : files) {
      for (List<String> sentence : read("c:/mszny2012/minimalized/" + file
          + ".minimalized")) {
        merged.add(sentence);
      }
    }

    write(merged, "c:/mszny2012/minimalized-merged/" + out + ".minimalized");
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

  public static void split(String file) {
    List<List<String>> sentences = read(file);

    int numberOfSentences = 0;
    numberOfSentences = sentences.size();

    System.out.println(numberOfSentences);

    Integer[] ids = null;
    ids = randomArray(numberOfSentences);

    int treshold = 0;

    treshold = (int) (numberOfSentences * 0.2);

    System.out.println(treshold);

    List<List<String>> test = null;
    test = new LinkedList<List<String>>();

    List<List<String>> train = null;
    train = new LinkedList<List<String>>();

    // test
    for (int i = 0; i < treshold; ++i) {
      test.add(sentences.get(ids[i]));
    }
    write(test, "c:/mszny2012/huge/szeged.test");

    // train
    for (int i = treshold; i < sentences.size(); ++i) {
      train.add(sentences.get(ids[i]));
    }
    write(train, "c:/mszny2012/huge/szeged.train");

    // group = aparts[i];
    // n += group.length;
    // System.err.println("|" + group.length + "|" + "\t"
    // + Arrays.toString(group));
    //
    // write(sentences, group, (WORK_DIR + TEST_2_DIR + file + ".test." + i),
    // false);
    // }

    // train
    // Integer[] trainArray = null;
    // ArrayList<Integer> train = null;
    //
    // for (int i = 0; i < aparts.length; ++i) {
    // train = new ArrayList<Integer>();
    // for (int j = 0; j < aparts.length; ++j) {
    // if (i != j) {
    // train.addAll(Arrays.asList(aparts[j]));
    // }
    // }
    //
    // trainArray = train.toArray(new Integer[train.size()]);
    // Arrays.sort(trainArray);
    // System.err.println("|" + trainArray.length + "|" + "\t"
    // + Arrays.toString(trainArray));
    //
    // write(sentences, trainArray,
    // (WORK_DIR + TRAIN_2_DIR + file + ".train." + i), true);
    // }
    // System.err.println("||" + n + "||");
  }

  public static void main(String args[]) {

    // merge(new String[] { "10erv", "10elb", "8oelb" }, "composition");
    // merge(new String[] { "utas", "pfred", "1984" }, "literature");
    // merge(new String[] { "gazdtar", "szerzj" }, "law");
    // merge(new String[] { "nv", "np", "hvg", "mh" }, "newspaper");
    // merge(new String[] { "newsml" }, "newsml");
    // merge(new String[] { "win2000", "cwszt" }, "computer");

    String[] corpuses = null;
    corpuses = new String[] { "10erv", "10elb", "1984", "8oelb", "cwszt",
        "gazdtar", "hvg", "mh", "newsml", "np", "nv", "pfred", "szerzj",
        "utas", "win2000" };

    merge(corpuses, "szeged");

    split("c:/mszny2012/huge/szeged.minimalized");

    // corpuses = new String[] { "composition", "literature", "law",
    // "newspaper",
    // "newsml", "computer" };

    // for (String corpus : corpuses)
    // removeEmptyNodes("c:/mszny2012/" + corpus + ".corpus");

    // for (String corpus : corpuses)
    // removeZ("c:/mszny2012/removed-virtuals-has-root/" + corpus
    // + ".corpus.removed-virtuals.has-root");

    // for (String corpus : corpuses)
    // minimalize(read("c:/mszny2012/removed-virtuals-has-root-no-z/" + corpus
    // + ".corpus.removed-virtuals.has-root.no-z"),
    // "c:/mszny2012/minimalized/" + corpus + ".minimalized");

    // // train
    // for (String corpus : corpuses)
    // writeTrain("c:/mszny2012/minimalized-merged/" + corpus + ".minimalized",
    // "c:/mszny2012/merged-train/" + corpus + ".train");

    // // lex
    // for (String corpus : corpuses)
    // writeCorpus("c:/mszny2012/minimalized-merged/" + corpus + ".minimalized",
    // "c:/mszny2012/merged-lex/" + corpus + ".lex");

    // // freq
    // for (String corpus : corpuses)
    // writeFreq("c:/mszny2012/minimalized-merged/" + corpus + ".minimalized",
    // "c:/mszny2012/merged-freq/" + corpus + ".freq");
    //

    // dep-train
    // for (String corpus : corpuses)
    // writeDepTrain("c:/mszny2012/minimalized-merged/" + corpus
    // + ".minimalized", "c:/mszny2012/merged-dep-train/" + corpus
    // + ".dep.train");

    // pos train
    writePosTrain("c:/mszny2012/huge/szeged.train",
        "c:/mszny2012/huge/szeged.pos.train");

    // pos lex
    writeCorpus("c:/mszny2012/huge/szeged.train",
        "c:/mszny2012/huge/szeged.lex");

    // pos freq
    writeFreq("c:/mszny2012/huge/szeged.train", "c:/mszny2012/huge/szeged.freq");

    // dep-train
    writeDepTrain("c:/mszny2012/huge/szeged.train",
        "c:/mszny2012/hugews/szeged.dep.train");
  }
}
