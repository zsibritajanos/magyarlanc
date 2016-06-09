package hu.u_szeged.eval;

import hu.u_szeged.pos.converter.CoNLLFeaturesToMSD;
import writer.Writer;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by zsibritajanos on 2015.09.28..
 */
public class ConllToFreq {

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
  public static final String EXTENSION_OUT_FREQ = ".freq";
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

    Map<String, Integer> freqs = new TreeMap<>();
    addLinesToFreqs(lines, freqs);

    Writer.writeMapToFile(freqs, outPath + file + outExtension, outEncoding, SEPARATOR_OUT);
  }

  /**
   * @param lines
   * @param freqs
   */
  public static void addLinesToFreqs(List<String> lines, Map<String, Integer> freqs) {
    for (String line : lines) {
      if (line.trim().length() > 0) {
        String[] split = line.split(SEPARATOR_SZK);
        addMsdToFreqs(CONLL_FEATURES_TO_MSD.convert(split[2], split[3]), freqs);
      }
    }
  }

  /**
   * Adds the MSD to the freqs.
   *
   * @param msd
   * @param freqs
   */
  public static void addMsdToFreqs(String msd, Map<String, Integer> freqs) {
    if (!freqs.containsKey(msd)) {
      freqs.put(msd, 0);
    }

    freqs.put(msd, freqs.get(msd) + 1);
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
