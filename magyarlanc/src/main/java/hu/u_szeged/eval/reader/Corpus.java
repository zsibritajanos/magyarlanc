package hu.u_szeged.eval.reader;

import hu.u_szeged.magyarlanc.MorAna;
import hu.u_szeged.pos.converter.MSDReducer;
import hu.u_szeged.pos.converter.MSDToCoNLLFeatures;
import writer.Writer;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.*;

/**
 * newsml        newsml
 * newspaper        hvg        mh        np        nv
 * law        gazdtar        szerzj
 * student        10erv        10elb        8oelb
 * computer        cwszt        win2000
 * literature        1984        pfred        utas
 */
public class Corpus {

  private List<List<List<String>>> corpus;
  private CorpusReader corpusReader;
  private MSDReducer msdReducer;
  private MSDToCoNLLFeatures msdToCoNLLFeatures;

  private static final String[] CORPUS = new String[]{"newsml", "hvg", "mh", "np", "nv", "gazdtar", "szerzj", "10erv", "10elb", "8oelb", "cwszt", "win2000", "1984", "pfred", "utas"};


  public Corpus(CorpusReader corpusReader) {
    this.corpusReader = corpusReader;
    this.corpus = new LinkedList<>();
    this.msdReducer = new MSDReducer();
    this.msdToCoNLLFeatures = new MSDToCoNLLFeatures();
  }

  public void addTrain(String file) {
    // all sentences
    List<List<List<String>>> sentences = this.corpusReader.read(file);
    //train sublist
    corpus.addAll(sentences.subList(0, (int) (sentences.size() * 0.8)));
  }

  public void addTTest(String file) {
    // all sentences
    List<List<List<String>>> sentences = this.corpusReader.read(file);
    //test sublist
    corpus.addAll(sentences.subList((int) (sentences.size() * 0.8), sentences.size()));
  }


  public void addFile(String file) {
    corpus.addAll(this.corpusReader.read(file));
    System.out.println(file + " " + corpus.size());
  }

  /**
   * @param file
   * @param encoding
   * @param tokenSeparator
   */
  public void writePurePosTrain(String file, String encoding, String tokenSeparator) {
    StringBuffer stringBuffer = new StringBuffer();

    for (List<List<String>> sentence : this.corpus) {
      for (List<String> token : sentence) {
        // wordform
        stringBuffer.append(token.get(0));
        stringBuffer.append("#");
        // lemma
        stringBuffer.append(token.get(1).replace("+", ""));

        if (token.get(1).contains(" ")) {
          System.out.println(token.get(1));
        }

        stringBuffer.append("#");
        stringBuffer.append("Pos=" + token.get(2) + "|" + token.get(3));
        // lemma
        stringBuffer.append(tokenSeparator);
      }

      stringBuffer.append("\n");
    }

    System.out.println(file);
    Writer.writeStringToFile(stringBuffer.toString(), file, encoding);
  }

  /**
   * @return
   */
  public Map<String, Set<MorAna>> getLex() {

    Map<String, Set<MorAna>> lexicon = new TreeMap<>();

    for (List<List<String>> sentence : this.corpus) {
      for (List<String> token : sentence) {

        String word = token.get(0);
        if (!lexicon.containsKey(word)) {
          lexicon.put(word, new TreeSet<>());
        }
        lexicon.get(word).add(new MorAna(token.get(1), token.get(2)));
      }
    }

    return lexicon;
  }

  /**
   * @param file
   * @param encoding
   * @param separator
   */
  public void writeLex(String file, String encoding, String separator) {
    BufferedWriter writer = null;
    try {
      writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), encoding));

      for (Map.Entry<String, Set<MorAna>> entry : getLex().entrySet()) {
        writer.write(entry.getKey());
        for (MorAna morAna : entry.getValue()) {
          writer.write(separator + morAna.getLemma() + separator + morAna.getMsd());
        }
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

  /**
   * @param file
   * @param encoding
   * @param separator
   */
  private void writeToUnivMorphCorpus(String file, String encoding, String separator) {
    StringBuffer stringBuffer = new StringBuffer();

    System.out.println(file);
    for (List<List<String>> sentence : this.corpus) {
      for (List<String> token : sentence) {

        // szoalak
        stringBuffer.append(token.get(0));
        stringBuffer.append(separator);

        // lemma
        stringBuffer.append(token.get(1).replace("+", ""));
        stringBuffer.append(separator);

        // POS
        stringBuffer.append(token.get(2));
        stringBuffer.append(separator);

        // features
        stringBuffer.append(token.get(3));
        stringBuffer.append("\n");
      }
      stringBuffer.append("\n");
    }

    Writer.writeStringToFile(stringBuffer.toString(), file, encoding);
  }

  private void writeToUnivDepCorpus(String file, String encoding, String separator) {
    StringBuffer stringBuffer = new StringBuffer();

    System.out.println(file);
    for (List<List<String>> sentence : this.corpus) {

      if (containsVirtual(sentence, 4)) {

        System.out.println(sentence);
      } else {
        for (List<String> token : sentence) {
          for (String t : token) {
            stringBuffer.append(t);
            stringBuffer.append(separator);
          }
          stringBuffer.append("\n");
        }
        stringBuffer.append("\n");
      }
    }

    Writer.writeStringToFile(stringBuffer.toString(), file, encoding);
  }

  /**
   * @param file
   * @param encoding
   * @param separator
   */
  private void writeMateTrain(String file, String encoding, String separator) {

    System.out.println(this.corpus.size());
    StringBuffer stringBuffer = new StringBuffer();

    for (List<List<String>> sentence : this.corpus) {
      if (containsVirtual(sentence, 4)) {
        //System.out.println(sentence);
      } else {
        for (List<String> token : sentence) {

          for (String s : token) {
            stringBuffer.append(s + separator);
          }
          stringBuffer.append("\n");
        }
        stringBuffer.append("\n");
      }
    }

    Writer.writeStringToFile(stringBuffer.toString(), file, encoding);
  }

  /**
   * @param sentence
   * @param index
   * @return
   */
  private static boolean containsVirtual(List<List<String>> sentence, int index) {

    boolean containsVirtual = false;
    for (List<String> token : sentence) {
      if (token.get(index).equals("VAN") || token.get(index).equals("ELL")) {
        containsVirtual = true;
        break;
      }
    }

    return containsVirtual;
  }

  /**
   * @param root
   * @param extension
   * @param encoding
   */
  private static void generateDep(String root, String extension, String encoding, String trainFile, String testFile) {

    // SZK 80% TRAIN in mate train format
//    Corpus trainCorpus = new Corpus(new ColumnCorpusReader("\t", encoding));
//
//    for (String c : CORPUS) {
//      System.out.println(c);
//      trainCorpus.addTrain(root + c + extension);
//    }
//
//    trainCorpus.writeMateTrain(trainFile, encoding, "\t");


    // SZK 20% TEST
    Corpus testCorpus = new Corpus(new ColumnCorpusReader("\t", encoding));

    for (String c : CORPUS) {
      testCorpus.addTTest(root + c + extension);
    }

    testCorpus.writeToUnivDepCorpus(testFile, encoding, "\t");
  }

  /**
   * Generates full SzK corpus for lex.
   *
   * @param root
   * @param extension
   * @param encoding
   */
  private static void generateCorpus(String root, String extension, String encoding, String out) {
    // full SZK
    Corpus corpus = new Corpus(new ColumnCorpusReader("\t", encoding));
    for (String c : CORPUS) {
      //corpus.addTrain(root + c + extension);
      corpus.addFile(root + c + extension);
    }
    corpus.writeLex(out, encoding, "\t");
  }

  /**
   * @param root
   * @param extension
   * @param encoding
   */
  private static void generatePurePos(String root, String extension, String encoding, String trainFile, String testFile) {

    // SZK 80% TRAIN in purepos train format
    Corpus trainCorpus = new Corpus(new ColumnCorpusReader("\t", encoding));

    for (String c : CORPUS) {
      trainCorpus.addTrain(root + c + extension);
      System.out.println(trainCorpus.corpus.size());
    }

    trainCorpus.writePurePosTrain(trainFile, encoding, " ");

    // SZK 20% TEST
    Corpus testCorpus = new Corpus(new ColumnCorpusReader("\t", encoding));

    for (String c : CORPUS) {
      testCorpus.addTTest(root + c + extension);
      System.out.println(testCorpus.corpus.size());
    }

    testCorpus.writeToUnivMorphCorpus(testFile, encoding, "\t");
  }


  public static void main(String[] args) {

    Corpus face = new Corpus(new ColumnCorpusReader("\t", "utf-8"));
    face.addFile("./data/web_univ_dep/faq.uddep");
    face.writeToUnivDepCorpus("./data/faq.uddep.virt", "utf-8", "\t");






//    generatePurePos("./data/szk_univ_morph_2.5/", ".ud", "utf-8", "./szk.purepos.train", "./szk.purepos.test");
//    generateDep("./data/szk_univ_dep_2.0/", ".ud", "utf-8", "./szk.mate.train", "./szk.mate.test");

//    generateCorpus("./data/szk_msd_2.5/", ".pron", "utf-8", "./szk.corpus.train.100");
  }
}
