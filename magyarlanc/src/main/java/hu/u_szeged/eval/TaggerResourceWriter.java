package hu.u_szeged.eval;

import hu.u_szeged.pos.converter.CoNLLFeaturesToMSD;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by zsibritajanos on 2015.09.27..
 */
public abstract class TaggerResourceWriter {

  /**
   * main config
   */

  public static final String SEPARATOR_SZK = "\t";
  public static final String ROOT_PATH = "./data/purepos/";

  /**
   * input config
   */
  public static final String ENCODING_IN = "utf-8";
  public static final String PATH_IN = ROOT_PATH + "ud/";
  public static final String EXTENSION_IN_TRAIN = ".train.ud";
  public static final String EXTENSION_IN_TEST = ".test.ud";

  /**
   * out config
   */
  public static final String ENCODING_OUT = "utf-8";
  public static final String PATH_OUT_TRAIN = ROOT_PATH + "train_stanford/";
  public static final String PATH_OUT_TEST = ROOT_PATH + "test_stanford/";
  public static final String PATH_OUT_GOLD = ROOT_PATH + "gold_stanford/";

  public static final String EXTENSION_OUT_TRAIN = ".train.stanford";
  public static final String EXTENSION_OUT_TEST = ".test.stanford";
  public static final String EXTENSION_OUT_GOLD = ".gold.stanford";

  /**
   * corpus set config
   */
  public static final String[] CORPUS = {"computer", "law", "literature", "newsml", "newspaper", "student"};

  /**
   * init
   */
  public static final CoNLLFeaturesToMSD CONLL_FEATURES_TO_MSD = new CoNLLFeaturesToMSD();

  /**
   * Converts the given Conll file to train format.
   *
   * @param path
   * @param file
   * @param extension
   * @param outPath
   * @param outExtension
   */
  public void convertTrain(String path, String file, String extension, String inEncoding, String outPath, String outExtension, String outEncoding) {
    System.out.println(file);

    // read
    List<String> lines = read(path, file, extension, inEncoding);

    //  convert and write
    try {
      Files.write(Paths.get(outPath + file + outExtension), linesToTrainSentences(lines), Charset.forName(outEncoding));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Reads the lines.
   *
   * @param path
   * @param file
   * @param extension
   * @return
   */
  public static List<String> read(String path, String file, String extension, String encoding) {
    List<String> lines = null;

    //read
    try {
      lines = Files.readAllLines(Paths.get(path + file + extension), Charset.forName(encoding));
    } catch (IOException e) {
      e.printStackTrace();
    }

    return lines;
  }

  public List<String> linesToTrainSentences(List<String> lines) {

    List<String> sentences = new LinkedList<>();

    List<String> wordforms = new LinkedList<>();
    List<String> lemmas = new LinkedList<>();
    List<String> msds = new LinkedList<>();

    String[] split;
    for (String line : lines) {
      if (line.trim().length() == 0) {
        sentences.add(lineToTrainSentence(wordforms, lemmas, msds));
        wordforms.clear();
        lemmas.clear();
        msds.clear();
      } else {
        split = line.split(SEPARATOR_SZK);
        wordforms.add(split[0]);
        lemmas.add(split[1]);
        msds.add(CONLL_FEATURES_TO_MSD.convert(split[2], split[3]));
      }
    }

    return sentences;
  }

  /**
   * Converts the given conll lines to TEST format.
   *
   * @param lines
   * @return
   */
  public List<String> linesToTestSentences(List<String> lines) {

    // to return
    List<String> sentences = new LinkedList<>();

    List<String> wordfForm = new LinkedList<>();

    String[] split;
    for (String line : lines) {
      if (line.trim().length() == 0) {
        sentences.add(lineToTestSentence(wordfForm));
        wordfForm.clear();
      } else {
        split = line.split(SEPARATOR_SZK);
        wordfForm.add(split[0]);
      }
    }

    return sentences;
  }

  /**
   * Converts the given conll lines to GOLD format.
   *
   * @param lines
   * @return
   */
  public List<String> linesToGoldSentences(List<String> lines) {

    List<String> sentences = new LinkedList<>();

    List<String> wordforms = new LinkedList<>();
    List<String> lemmas = new LinkedList<>();
    List<String> msds = new LinkedList<>();

    String[] split;
    for (String line : lines) {
      if (line.trim().length() == 0) {
        sentences.add(lineToGoldSentence(wordforms, lemmas, msds));
        wordforms.clear();
        lemmas.clear();
        msds.clear();
      } else {
        split = line.split(SEPARATOR_SZK);
        wordforms.add(split[0]);
        lemmas.add(split[1]);
        msds.add(CONLL_FEATURES_TO_MSD.convert(split[2], split[3]));
      }
    }
    return sentences;
  }


  /**
   * Converts the given Conll file to test format.
   *
   * @param path
   * @param file
   * @param extension
   * @param outPath
   * @param outExtension
   */
  private void convertTest(String path, String file, String extension, String inEncoding, String outPath, String outExtension, String outEncoding) {
    System.out.println(file);
    List<String> lines = null;

    //read
    try {
      lines = Files.readAllLines(Paths.get(path + file + extension), Charset.forName(inEncoding));
    } catch (IOException e) {
      e.printStackTrace();
    }

    //  convert and write
    try {
      Files.write(Paths.get(outPath + file + outExtension), linesToTestSentences(lines), Charset.forName(outEncoding));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Converts the given Conll file to test format.
   *
   * @param path
   * @param file
   * @param extension
   * @param outPath
   * @param outExtension
   */
  private void convertGold(String path, String file, String extension, String inEncoding, String outPath, String outExtension, String outEncoding) {
    System.out.println(file);
    List<String> lines = null;

    //read
    try {
      lines = Files.readAllLines(Paths.get(path + file + extension), Charset.forName(inEncoding));
    } catch (IOException e) {
      e.printStackTrace();
    }

    //  convert and write
    try {
      Files.write(Paths.get(outPath + file + outExtension), linesToGoldSentences(lines), Charset.forName(outEncoding));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void generate() {
    /**
     * TRAIN FILES
     */
    for (String corpus : CORPUS) {
      convertTrain(PATH_IN, corpus, EXTENSION_IN_TRAIN, ENCODING_IN, PATH_OUT_TRAIN, EXTENSION_OUT_TRAIN, ENCODING_OUT);
    }

    /**
     * TEST FILES
     */
    for (String corpus : CORPUS) {
      //convertTest(PATH_IN, corpus, EXTENSION_IN_TEST, ENCODING_IN, PATH_OUT_TEST, EXTENSION_OUT_TEST, ENCODING_OUT);
    }

    /**
     * GOLD FILES
     */
    for (String corpus : CORPUS) {
      convertGold(PATH_IN, corpus, EXTENSION_IN_TEST, ENCODING_IN, PATH_OUT_GOLD, EXTENSION_OUT_GOLD, ENCODING_OUT);
    }
  }

  public abstract String lineToTrainSentence(List<String> wordforms, List<String> lemmas, List<String> msds);

  public abstract String lineToTestSentence(List<String> wordforms);

  public abstract String lineToGoldSentence(List<String> wordforms, List<String> lemmas, List<String> msds);
}
