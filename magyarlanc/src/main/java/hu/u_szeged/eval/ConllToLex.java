package hu.u_szeged.eval;

import hu.u_szeged.magyarlanc.MorAna;
import hu.u_szeged.pos.converter.CoNLLFeaturesToMSD;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.*;

/**
 * Created by zsibritajanos on 2015.09.28..
 */
public class ConllToLex {
  /**
   * input config
   */
  public static final String SEPARATOR_SZK = "\t";
  public static final String ROOT_PATH = "./data/purepos/";
  public static final String ENCODING_IN = "utf-8";
  public static final String PATH_IN = ROOT_PATH + "ud/";
  public static final String EXTENSION_IN_TRAIN = ".train.ud";

  /**
   * out config
   */
  public static final String ENCODING_OUT = "utf-8";
  public static final String PATH_OUT_FREQ = ROOT_PATH + "train_stanford/";
  public static final String EXTENSION_OUT_FREQ = ".lex";
  public static final String SEPARATOR_OUT = "\t";

  /**
   * corpus set config
   */
  public static final String[] CORPUS = {"computer", "law", "literature", "newsml", "newspaper", "student"};

  /**
   * init
   */
  public static final CoNLLFeaturesToMSD CONLL_FEATURES_TO_MSD = new CoNLLFeaturesToMSD();

  /**
   * Generates the freq file.
   *
   * @param path
   * @param file
   * @param extension
   * @param outPath
   * @param outExtension
   */
  public static void generate(String path, String file, String extension, String inEncoding, String outPath, String outExtension, String outEncoding) {
    List<String> lines = TaggerResourceWriter.read(path, file, extension, inEncoding);

    Map<String, Set<MorAna>> lex = new TreeMap<>();
    addLinesToLex(lines, lex);

    System.out.println(lex);
    writeLex(lex, outPath + file + outExtension, ENCODING_OUT, SEPARATOR_OUT);
  }

  /**
   * @param lines
   * @param lex
   */
  public static void addLinesToLex(List<String> lines, Map<String, Set<MorAna>> lex) {
    String[] split;
    for (String line : lines) {
      if (line.trim().length() > 0) {
        split = line.split(SEPARATOR_SZK);
        String wordForm = split[0];
        String lemma = split[1];
        String msd = CONLL_FEATURES_TO_MSD.convert(split[2], split[3]);
        addLineToLex(wordForm, lemma, msd, lex);
      }
    }
  }

  /**
   * Adds the MSD to the freqs.
   *
   * @param msd
   * @param lex
   */
  public static void addLineToLex(String wordForm, String lemma, String msd, Map<String, Set<MorAna>> lex) {
    if (!lex.containsKey(wordForm)) {
      lex.put(wordForm, new TreeSet<>());
    }

    lex.get(wordForm).add(new MorAna(lemma, msd));
  }

  /**
   *
   * @param lex
   * @param file
   * @param encoding
   */
  public static void writeLex(Map<String, Set<MorAna>> lex, String file, String encoding, String separator) {
    BufferedWriter writer = null;

    try {
      writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
              file), encoding));

      for (Map.Entry<String, Set<MorAna>> entry : lex.entrySet()) {
        writer.write(entry.getKey());
        for (MorAna morAna : entry.getValue()) {
          writer.write(separator + morAna.getLemma() + separator + morAna.getMsd());
        }
        writer.write('\n');
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
   * Batch generation.
   */
  public static void generate() {
    for (String corpus : CORPUS) {
      generate(PATH_IN, corpus, EXTENSION_IN_TRAIN, ENCODING_IN, PATH_OUT_FREQ, EXTENSION_OUT_FREQ, ENCODING_OUT);
    }
  }

  public static void main(String[] args) {
    generate();
  }
}
