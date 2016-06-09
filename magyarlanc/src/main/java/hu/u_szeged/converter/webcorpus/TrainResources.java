package hu.u_szeged.converter.webcorpus;

import hu.u_szeged.dep.parser.MyMateParser;
import hu.u_szeged.magyarlanc.MorAna;
import hu.u_szeged.magyarlanc.resource.ResourceHolder;
import hu.u_szeged.pos.converter.CoNLLFeaturesToMSD;

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
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class TrainResources {

  public static final String ENCODING = "utf-8";
  public static final CoNLLFeaturesToMSD CFTM = new CoNLLFeaturesToMSD();

  private static final Random RANDOM = new Random();

  public static Set<Integer> getRandom(int size, int max) {

    // random ids
    Set<Integer> randomIDs = null;
    randomIDs = new TreeSet<Integer>();

    while (randomIDs.size() < size) {
      randomIDs.add(RANDOM.nextInt(max));
    }

    return randomIDs;
  }

  /**
   * Reads the sentences.
   *
   * @param file file
   * @return list if the sentences
   */
  public static List<List<String>> readSentences(String file) {
    BufferedReader reader = null;

    String line = null;
    List<String> sentence = null;
    List<List<String>> sentences = null;

    sentence = new ArrayList<String>();
    sentences = new LinkedList<List<String>>();

    try {
      reader = new BufferedReader(new InputStreamReader(new FileInputStream(
              file), ENCODING));

      while ((line = reader.readLine()) != null) {
        if (line.trim().length() == 0) {
          sentences.add(sentence);
          sentence = new ArrayList<String>();
        } else {
          sentence.add(line);
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

    return sentences;
  }

  /**
   * @param sentence
   * @return
   */
  public static boolean isContainsVirtual(List<String> sentence) {

    String[] split = null;

    for (String token : sentence) {
      split = token.split("\t");
      if (split[4].equals("VAN") || split[4].equals("ELL")) {
        return true;
      }
    }
    return false;
  }

  public static List<List<String>> filterSentences(List<List<String>> sentences) {
    List<List<String>> filtered = null;
    filtered = new ArrayList<List<String>>();

    for (List<String> sentence : sentences) {
      if (!isContainsVirtual(sentence)) {
        filtered.add(sentence);
      }
    }

    return filtered;
  }

  public static void writePosTrain(List<List<String>> sentences, String out) {

    String[] split = null;

    Writer writer = null;
    String wordForm = null;
    String msd = null;

    try {
      writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
              out), "UTF-8"));

      for (List<String> sentence : sentences) {
        if (sentence.size() > 0) {
          for (String token : sentence) {
            split = token.split("\t");
            wordForm = split[1];

            msd = CFTM.convert(split[4], split[6]);
            writer.write(wordForm + "@"
                    + ResourceHolder.getMSDReducer().reduce(msd) + " ");
          }
          writer.write("\n");
        }
      }

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

  public static void writeCorpus(List<List<String>> sentences, String out) {

    String[] split = null;

    String wordForm = null;
    String lemma = null;
    String msd = null;

    Map<String, Set<MorAna>> corpus = null;
    corpus = new TreeMap<String, Set<MorAna>>();

    MorAna morAna = null;

    for (List<String> sentence : sentences) {
      for (String token : sentence) {
        split = token.split("\t");
        wordForm = split[1];
        lemma = split[2];
        msd = CFTM.convert(split[4], split[6]);
        morAna = new MorAna(lemma, msd);

        if (!corpus.containsKey(wordForm))
          corpus.put(wordForm, new TreeSet<MorAna>());

        corpus.get(wordForm).add(morAna);
      }
    }

    Writer writer = null;
    try {
      writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
              out), ENCODING));

      for (Entry<String, Set<MorAna>> enrty : corpus.entrySet()) {
        writer.write(enrty.getKey());

        for (MorAna m : enrty.getValue())
          writer.write("\t" + m.getLemma() + "\t" + m.getMsd());

        writer.write("\n");
      }
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

  public static void writeFreq(List<List<String>> sentences, String out) {
    String[] split = null;
    Writer writer = null;

    String msd = null;

    Map<String, Integer> freq = null;
    freq = new TreeMap<String, Integer>();

    for (List<String> sentence : sentences) {
      for (String token : sentence) {
        split = token.split("\t");

        msd = CFTM.convert(split[4], split[6]);

        if (msd.equals("O")) {
          System.err.println(token);
        }

        if (!freq.containsKey(msd)) {
          freq.put(msd, 0);
        }

        freq.put(msd, freq.get(msd) + 1);
      }
    }

    try {
      writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
              out), ENCODING));

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

  public static void writePosTest(List<List<String>> sentences, String out) {
    String[] split = null;

    Writer writer = null;
    String wordForm = null;
    String lemma = null;

    String msd = null;

    try {
      writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
              out), "UTF-8"));

      for (List<String> sentence : sentences) {
        if (sentence.size() > 0) {
          for (String token : sentence) {
            split = token.split("\t");
            wordForm = split[1];
            lemma = split[2];
            msd = CFTM.convert(split[4], split[6]);

            writer.write(wordForm + "\t" + lemma + "\t" + msd + "\n");
          }
          writer.write("\n");
        }
      }

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

  public static void writeDep(List<List<String>> sentences, String out) {
    // 1 A a a T T SubPOS=f SubPOS=f 3 3 DET DET _ _

    String[] split = null;
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

      for (List<String> sentence : sentences) {
        for (String token : sentence) {
          split = token.split("\t");

          id = split[0];
          form = split[1];
          lemma = split[2];
          // splitted[3];
          POS = split[4];
          feature = split[6];

          head = split[8];
          label = split[10];

          writer.write(id + "\t" + form + "\t" + lemma + "\t" + lemma + "\t"
                  + POS + "\t" + POS + "\t" + feature + "\t" + feature + "\t"
                  + head + "\t" + head + "\t" + label + "\t" + label + "\t_\t_\n");
        }
        writer.write("\n");
      }
      writer.flush();
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

  public static void generate() {
    List<List<String>> filteredSentences = null;

    List<List<String>> trainSentences = null;
    List<List<String>> testSentences = null;

    for (String file : new String[]{"web_1222", "face_1222", "faq_1222"}) {

      trainSentences = new ArrayList<List<String>>();
      testSentences = new ArrayList<List<String>>();

      // virtualis node-okat tartalmazo mondatok elhagyasa
      filteredSentences = filterSentences(readSentences("./data/webcorpus_1222/"
              + file + ".conll-2009"));

      Set<Integer> ids = null;
      ids = new TreeSet<Integer>();
      for (int i = 0; i < filteredSentences.size(); ++i) {
        ids.add(i);
      }

      System.err.println(ids.size());
      // full test
      writePosTest(filteredSentences, "./data/webcorpus_1222/" + file
              + ".pos.full");

      Set<Integer> trainIds = null;
      trainIds = getRandom((int) (filteredSentences.size() * 0.8), ids.size());

      // trainSentences = filteredSentences.subList(0,
      // (int) (filteredSentences.size() * 0.8));
      // testSentences = filteredSentences.subList(
      // (int) (filteredSentences.size() * 0.8), filteredSentences.size());

      for (int i : trainIds) {
        trainSentences.add(filteredSentences.get(i));
      }

      ids.removeAll(trainIds);

      for (int i : ids) {
        testSentences.add(filteredSentences.get(i));
      }

      System.err.println(trainIds);
      System.err.println(trainSentences.size());

      System.err.println(ids);
      System.err.println(testSentences.size());

      // 80-20 split

      // writePosTrain(trainSentences, "./data/webcorpus_1222/" + file
      // + ".pos.train");
      // writePosTest(testSentences, "./data/webcorpus_1222/" + file +
      // ".pos.test");
      //
      // writeCorpus(trainSentences, "./data/webcorpus_1222/" + file + ".lex");
      // writeFreq(trainSentences, "./data/webcorpus_1222/" + file + ".freq");
      //
      // writeDep(trainSentences, "./data/webcorpus_1222/" + file +
      // ".dep.train");
      // writeDep(testSentences, "./data/webcorpus_1222/" + file + ".dep.test");
    }
  }

  /**
   * Reads CoNLL-2009 file.
   *
   * @param file
   */
  private static void predictAndEvalConll2009UsingGoldFeatures(String file,
                                                               String out) {

    // 1 De de de C C SubPOS=c... SubPOS=c... 4 4 CONJ CONJ _ _

    int wordFormIndex = 1;
    int lemmaIndex = 2;
    int posIndex = 4;
    int featIndex = 6;
    int headIndex = 8;
    int relIndex = 10;

    // reader
    BufferedReader reader = null;

    Writer writer = null;

    String line = null;
    String[] split = null;

    List<String> wordForm = null;
    List<String> lemma = null;
    List<String> pos = null;
    List<String> feat = null;

    List<String> msd = null;

    List<String> head = null;
    List<String> rel = null;

    wordForm = new ArrayList<String>();
    lemma = new ArrayList<String>();
    pos = new ArrayList<String>();
    feat = new ArrayList<String>();

    msd = new ArrayList<String>();

    head = new ArrayList<String>();
    rel = new ArrayList<String>();

    String[][] pred = null;

    try {
      reader = new BufferedReader(new InputStreamReader(new FileInputStream(
              file), ENCODING));

      writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
              out), ENCODING));

      while ((line = reader.readLine()) != null) {
        if ("".equals(line.trim())) {

          pred = MyMateParser.getInstance().parseSentence(wordForm, lemma, pos,
                  feat);

          if (pred != null) {
            for (int i = 0; i < pred.length; ++i) {
              writer.write(wordForm.get(i) + "\t" + head.get(i) + "\t"
                      + pred[i][6] + "\t" + rel.get(i) + "\t" + pred[i][7] + "\n");
            }

            writer.write("\n");
          }

          // gold values
          wordForm = new ArrayList<String>();
          lemma = new ArrayList<String>();
          pos = new ArrayList<String>();
          feat = new ArrayList<String>();
          msd = new ArrayList<String>();

          head = new ArrayList<String>();
          rel = new ArrayList<String>();

        } else {
          split = line.split("\t");

          wordForm.add(split[wordFormIndex]);
          lemma.add(split[lemmaIndex]);
          pos.add(split[posIndex]);
          feat.add(split[featIndex]);

          msd.add(CFTM.convert(split[posIndex], split[featIndex]));

          head.add(split[headIndex]);
          rel.add(split[relIndex]);
        }
      }

    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        reader.close();
        writer.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    evalPred(out);
  }

  public static void predictPred(List<List<String>> testSentences) {

    // String[][] parseSentence(String[] form, String[] lemma,
    // String[] MSD) {
    //
    // for (int i = 0; i < forms.size(); ++i) {
    // dep.add(MateParserWrapper.parseSentence(
    // );
    // }
  }

  public static void evalPred(String file) {

    List<List<String>> sentences = null;
    sentences = readSentences(file);

    String[] splitted = null;

    int counter = 0;

    int las = 0;
    int uas = 0;

    for (List<String> sentence : sentences) {
      for (String token : sentence) {
        splitted = token.split("\t");
        if (splitted[1].equals(splitted[2])) {
          ++uas;
          if (splitted[3].equals(splitted[4])) {
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

  // leiras
  // https://docs.google.com/document/d/1y9yICnPE-xccrLbkrgDnZOrg972dttECakS0Z2C9T3o/edit

  public static void main(String[] args) {

    // generate();

    // Magyarlanc.eval("./data/webcorpus_1222/faq_1222.pos.full",
    // "./data/webcorpus_1222/faq_1222.pos.full.out");

    // predictAndEvalConll2009UsingGoldFeatures(
    // "./data/webcorpus_1222/face_1222.conll-2009",
    // "./data/webcorpus_1222/face_1222.conll-2009.dep.out");

  }
}
