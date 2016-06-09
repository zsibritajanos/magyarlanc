package hu.u_szeged.magyarlanc;

import hu.u_szeged.cons.parser.MyBerkeleyParser;
import hu.u_szeged.converter.nooj.Dep2Nooj;
import hu.u_szeged.converter.univ.Univ;
import hu.u_szeged.dep.parser.MyMateParser;
import hu.u_szeged.gui.GUI;
import hu.u_szeged.magyarlanc.resource.ResourceHolder;
import hu.u_szeged.magyarlanc.resource.Util;
import hu.u_szeged.magyarlanc.util.SafeReader;
import hu.u_szeged.pos.purepos.MyPurePos;
import splitter.MySplitter;

import java.io.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Magyarlanc {

  // usage messages
  private static final String USAGE_MESSAGE = "usage: -mode gui|morphparse|depparse|constparse|parse|morana";

  private static final String USAGE_MESSAGE_PARSE = "usage: -mode morphparse|depparse|constparse|parse -input input -output output [-encoding encoding] [-tokenized tokenized]";

  private static final NumberFormat NUMBER_FORMAT = new DecimalFormat("#0.00");

  // default encoding
  private static final String DEFAULT_ENCODING = "utf-8";

  // encoding key
  private static final String KEY_ENCODING = "-encoding";
  // tokenized key
  private static final String KEY_TOKENIZED = "-tokenized";

  // input output keys
  private static final String KEY_INPUT = "-input";
  private static final String KEY_OUTPUT = "-output";

  // mode keys
  private static final String MODE_VALUE_MORPH_PARSE = "morphparse";
  private static final String MODE_VALUE_DEP_PARSE = "depparse";
  private static final String MODE_VALUE_CONST_PARSE = "constparse";
  private static final String MODE_VALUE_PARSE = "parse";

  // verbose
  private static final String MESSAGE_INITIALIZING = "initializing";
  private static final String MESSAGE_INITIALIZED = "initialized";
  private static final String MESSAGE_FINISHED = "finished in ";
  private static final String MESSAGE_TIME = " seconds";

  public static void init() {
    ResourceHolder.initCorpus();
    ResourceHolder.initMSDReducer();
    ResourceHolder.initPunctations();
    ResourceHolder.initRFSA();
    ResourceHolder.initKRToMSD();
    ResourceHolder.initMSDToCoNLLFeatures();
    ResourceHolder.initCorrDic();
    ResourceHolder.initMorPhonDir();
  }

  public static void morphInit() {
    init();
    MyPurePos.getInstance();
  }

  public static void depInit() {
    morphInit();
    MyMateParser.getInstance();
  }

  public static void constInit() {
    morphInit();
    MyBerkeleyParser.getInstance();
  }

  public static void fullInit() {
    morphInit();
    MyPurePos.getInstance();
    MyMateParser.getInstance();
    MyBerkeleyParser.getInstance();
  }


  /**
   * @param form
   * @return
   */
  public static String[][] morphParseSentence(String[] form) {
    return MyPurePos.getInstance().morphParseSentence(form);
  }

  /**
   * @param form
   * @param lemma
   * @param pos
   * @param feature
   * @return
   */
  public static String[][] depParseSentence(String[] form, String[] lemma, String[] pos, String[] feature) {
    return MyMateParser.getInstance().parseSentence(form, lemma, pos, feature);
  }

  /**
   * @param form
   * @return
   */
  public static String[][] morphParseSentence(List<String> form) {
    return MyPurePos.getInstance().morphParseSentence(form.toArray(new String[form.size()]));
  }

  /**
   * @param sentence
   * @return
   */
  public static String[][] morphParseSentence(String sentence) {
    return morphParseSentence(MySplitter.getInstance().tokenize(sentence));
  }

  /**
   * @param text
   * @return
   */
  public static String[][][] morphParse(String text) {
    List<String[][]> morph = new ArrayList<>();

    for (String[] sentence : MySplitter.getInstance().splitToArray(text)) {
      morph.add(morphParseSentence(sentence));
    }
    return morph.toArray(new String[morph.size()][][]);
  }

  /**
   * Line by line.
   *
   * @param lines
   * @return
   */
  public static String[][][] morphParse(List<String> lines) {
    List<String[][]> morph = new ArrayList<>();

    int lineCounter = 0;
    int sentenceCounter = 0;

    for (String line : lines) {

      double progress = (double) ++lineCounter / lines.size();

      String[][] sentences = MySplitter.getInstance().splitToArray(line);
      sentenceCounter += sentences.length;
      if (lineCounter % 100 == 0) {
        System.out.println((sentenceCounter) + " sentences " + NUMBER_FORMAT.format(progress * 100) + "%");
      }

      for (String[] sentence : sentences) {
        morph.add(morphParseSentence(sentence));
      }
    }
    return morph.toArray(new String[morph.size()][][]);
  }

  /**
   * Line by line.
   *
   * @param lines
   * @return
   */
  public static void morphParse(List<String> lines, Writer writer) {

    int lineCounter = 0;
    int sentenceCounter = 0;

    try {
      for (String line : lines) {

        double progress = (double) ++lineCounter / lines.size();

        String[][] sentences = MySplitter.getInstance().splitToArray(line);
        sentenceCounter += sentences.length;
        if (lineCounter % 100 == 0) {
          writer.flush();
          System.out.println((sentenceCounter) + " sentences " + NUMBER_FORMAT.format(progress * 100) + "%");
        }

        for (String[] sentence : sentences) {
          String[][] morphed = morphParseSentence(sentence);

          for (int j = 0; j < morphed.length; ++j) {
            for (int k = 0; k < morphed[j].length; ++k) {
              writer.write(morphed[j][k] + "\t");
            }
            writer.write("\n");
          }
          writer.write("\n");
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * @param sentences
   * @param writer
   * @throws IOException
   */
  public static void morphParse(String[][] sentences, Writer writer) throws IOException {

    int sentenceCounter = 0;

    for (String[] sentence : sentences) {

      double progress = (double) ++sentenceCounter / sentences.length;

      if (sentenceCounter % 100 == 0) {
        System.out.println((sentenceCounter) + " sentences " + NUMBER_FORMAT.format(progress * 100) + "%");
      }

      String[][] morph = MyPurePos.getInstance().morphParseSentence(sentence);

      for (int j = 0; j < morph.length; ++j) {
        for (int k = 0; k < morph[j].length; ++k) {
          writer.write(morph[j][k] + "\t");
        }
        writer.write("\n");
      }
      writer.write("\n");
    }
  }


  /**
   * @param sentences
   * @param writer
   * @throws IOException
   */
  public static void depParse(String[][] sentences, Writer writer) throws IOException {

    int sentenceCounter = 0;

    for (String[] sentence : sentences) {

      double progress = (double) ++sentenceCounter / sentences.length;

      if (sentenceCounter % 100 == 0) {
        System.out.println((sentenceCounter) + " sentences " + NUMBER_FORMAT.format(progress * 100) + "%");
      }

      String[][] parsed = MyMateParser.getInstance().parseSentence(sentence);

      for (int j = 0; j < parsed.length; ++j) {
        for (int k = 0; k < parsed[j].length; ++k) {
          writer.write(parsed[j][k] + "\t");
        }
        writer.write("\n");
      }
      writer.write("\n");
    }
  }


  /**
   * @param sentences
   * @param writer
   * @throws IOException
   */
  public static void constParse(String[][] sentences, Writer writer) throws IOException {

    int sentenceCounter = 0;

    for (String[] sentence : sentences) {

      double progress = (double) ++sentenceCounter / sentences.length;

      if (sentenceCounter % 100 == 0) {
        System.out.println((sentenceCounter) + " sentences " + NUMBER_FORMAT.format(progress * 100) + "%");
      }

      String[][] parsed = MyBerkeleyParser.getInstance().parseSentence(sentence);

      for (int j = 0; j < parsed.length; ++j) {
        for (int k = 0; k < parsed[j].length; ++k) {
          writer.write(parsed[j][k] + "\t");
        }
        writer.write("\n");
      }
      writer.write("\n");
    }
  }

  public static void depParse(List<String> lines, Writer writer) throws IOException {

    int lineCounter = 0;
    int sentenceCounter = 0;

    for (String line : lines) {

      double progress = (double) ++lineCounter / lines.size();

      String[][] sentences = MySplitter.getInstance().splitToArray(line);
      sentenceCounter += sentences.length;
      if (lineCounter % 100 == 0) {
        writer.flush();
        System.out.println((sentenceCounter) + " sentences " + NUMBER_FORMAT.format(progress * 100) + "%");
      }

      for (String[] sentence : sentences) {
        String[][] dep = depParseSentence(sentence);

        for (int j = 0; j < dep.length; ++j) {
          for (int k = 0; k < dep[j].length; ++k) {
            writer.write(dep[j][k] + "\t");
          }
          writer.write("\n");
        }
        writer.write("\n");
      }
    }
  }

  /**
   * @param lines
   * @return
   */
  public static void constParse(List<String> lines, Writer writer) throws IOException {

    int lineCounter = 0;
    int sentenceCounter = 0;

    for (String line : lines) {

      double progress = (double) ++lineCounter / lines.size();
      String[][] sentences = MySplitter.getInstance().splitToArray(line);

      sentenceCounter += sentences.length;

      if (lineCounter % 100 == 0) {
        writer.flush();
        System.out.println((sentenceCounter) + " sentences " + NUMBER_FORMAT.format(progress * 100) + "%");
      }

      for (String[] sentence : MySplitter.getInstance().splitToArray(line)) {
        String[][] con = constParseSentence(sentence);
        for (int j = 0; j < con.length; ++j) {
          for (int k = 0; k < con[j].length; ++k) {
            writer.write(con[j][k] + "\t");
          }
          writer.write("\n");
        }
        writer.write("\n");
      }
    }
  }

  /**
   * @param lines
   * @return
   */
  public static void parse(List<String> lines, Writer writer) throws IOException {

    int lineCounter = 0;
    int sentenceCounter = 0;

    for (String line : lines) {

      double progress = (double) ++lineCounter / lines.size();
      String[][] sentences = MySplitter.getInstance().splitToArray(line);

      sentenceCounter += sentences.length;

      if (lineCounter % 100 == 0) {
        System.out.println((sentenceCounter) + " sentences " + NUMBER_FORMAT.format(progress * 100) + "%");
      }

      for (String[] sentence : MySplitter.getInstance().splitToArray(line)) {

        String[][] morph = MyPurePos.getInstance().morphParseSentence(sentence);
        String[][] cons = MyBerkeleyParser.getInstance().parseSentence(morph);
        String[][] dep = MyMateParser.getInstance().parseSentence(morph);

        String[][] merged = merge(sentence, morph, cons, dep);

        for (int j = 0; j < merged.length; ++j) {
          for (int k = 0; k < merged[j].length; ++k) {
            writer.write(merged[j][k] + "\t");
          }
          writer.write("\n");
        }
        writer.write("\n");
      }
    }
  }


  /**
   * @param sentences
   * @param writer
   * @throws IOException
   */
  public static void parse(String[][] sentences, Writer writer) throws IOException {

    int sentenceCounter = 0;

    for (String[] sentence : sentences) {

      double progress = (double) ++sentenceCounter / sentences.length;

      if (sentenceCounter % 100 == 0) {
        System.out.println((sentenceCounter) + " sentences " + NUMBER_FORMAT.format(progress * 100) + "%");
      }

      String[][] morph = MyPurePos.getInstance().morphParseSentence(sentence);
      String[][] cons = MyBerkeleyParser.getInstance().parseSentence(morph);
      String[][] dep = MyMateParser.getInstance().parseSentence(morph);

      String[][] merged = merge(sentence, morph, cons, dep);

      for (int j = 0; j < merged.length; ++j) {
        for (int k = 0; k < merged[j].length; ++k) {
          writer.write(merged[j][k] + "\t");
        }
        writer.write("\n");
      }
      writer.write("\n");
    }
  }


  /**
   * @param lines
   * @return
   */
  public static String[][][] depParse(List<String> lines) {
    List<String[][]> dep = new ArrayList<>();

    int lineCounter = 0;
    int sentenceCounter = 0;

    for (String line : lines) {

      double progress = (double) ++lineCounter / lines.size();
      String[][] sentences = MySplitter.getInstance().splitToArray(line);

      sentenceCounter += sentences.length;

      if (lineCounter % 100 == 0) {
        System.out.println((sentenceCounter) + " sentences " + NUMBER_FORMAT.format(progress * 100) + "%");
      }

      for (String[] sentence : MySplitter.getInstance().splitToArray(line)) {
        dep.add(depParseSentence(sentence));
      }
    }

    return dep.toArray(new String[dep.size()][][]);
  }

  /**
   * @param lines
   * @return
   */
  public static String[][][] parse(List<String> lines) {
    List<String[][]> res = new ArrayList<>();

    int lineCounter = 0;
    int sentenceCounter = 0;

    for (String line : lines) {

      double progress = (double) ++lineCounter / lines.size();
      String[][] sentences = MySplitter.getInstance().splitToArray(line);

      sentenceCounter += sentences.length;

      if (lineCounter % 100 == 0) {
        System.out.println((sentenceCounter) + " sentences " + NUMBER_FORMAT.format(progress * 100) + "%");
      }

      for (String[] sentence : MySplitter.getInstance().splitToArray(line)) {

        String[][] morph = MyPurePos.getInstance().morphParseSentence(sentence);
        String[][] cons = MyBerkeleyParser.getInstance().parseSentence(morph);
        String[][] dep = MyMateParser.getInstance().parseSentence(morph);

        String[][] merged = merge(sentence, morph, cons, dep);

        res.add(merged);
      }
    }

    return res.toArray(new String[res.size()][][]);
  }


  /**
   * @param lines
   * @return
   */
  public static String[][][] constParse(List<String> lines) {
    List<String[][]> dep = new ArrayList<>();

    int lineCounter = 0;
    int sentenceCounter = 0;

    for (String line : lines) {

      double progress = (double) ++lineCounter / lines.size();
      String[][] sentences = MySplitter.getInstance().splitToArray(line);

      sentenceCounter += sentences.length;

      if (lineCounter % 100 == 0) {
        System.out.println((sentenceCounter) + " sentences " + NUMBER_FORMAT.format(progress * 100) + "%");
      }

      for (String[] sentence : MySplitter.getInstance().splitToArray(line)) {
        dep.add(constParseSentence(sentence));
      }
    }

    return dep.toArray(new String[dep.size()][][]);
  }


  /**
   * @param text
   * @return
   */
  public static String[][][] morphParse(String[][] text) {
    List<String[][]> morph = new ArrayList<>();

    for (String[] sentence : text) {
      morph.add(morphParseSentence(sentence));
    }
    return morph.toArray(new String[morph.size()][][]);
  }


  /**
   * @param text
   * @return
   */
  public static String[][][] parse(String[][] text) {
    List<String[][]> parsed = new ArrayList<>();

    for (String[] sentence : text) {
      parsed.add(parseSentence(sentence));
    }
    return parsed.toArray(new String[parsed.size()][][]);
  }


  /**
   * @param text
   * @return
   */
  public static String[][][] depParse(String text) {
    List<String[][]> dep = new ArrayList<>();

    for (String[] sentence : MySplitter.getInstance().splitToArray(text)) {
      dep.add(depParseSentence(sentence));
    }
    return dep.toArray(new String[dep.size()][][]);
  }


  /**
   * @param sentence
   * @return
   */
  public static String[][] depParseSentence(String sentence) {
    return depParseSentence(MySplitter.getInstance().tokenizeToArray(sentence));
  }

  /**
   * @param form
   * @return
   */
  public static String[][] depParseSentence(String[] form) {
    return MyMateParser.getInstance().parseSentence(MyPurePos.getInstance().morphParseSentence(form));
  }

  /**
   * @param form
   * @return
   */
  public static String[][] parseSentence(String[] form) {

    String[][] morph = MyPurePos.getInstance().morphParseSentence(form);
    String[][] cons = MyBerkeleyParser.getInstance().parseSentence(morph);
    String[][] dep = MyMateParser.getInstance().parseSentence(morph);

    return merge(form, morph, cons, dep);
  }

  /**
   * @param sentence
   * @return
   */
  public static String[][] parseSentence(String sentence) {
    return parseSentence(MySplitter.getInstance().tokenizeToArray(sentence));
  }

  private static String[][] merge(String[] form, String[][] morph, String[][] cons, String[][] dep) {
    String[][] ret = new String[form.length][8];
    for (int i = 0; i < ret.length; ++i) {
      // dep idx
      ret[i][0] = dep[i][0];

      // form
      ret[i][1] = morph[i][0];
      // lemma
      ret[i][2] = morph[i][1];
      // pos
      ret[i][3] = morph[i][2];
      //features
      ret[i][4] = morph[i][3];

      //head
      ret[i][5] = dep[i][5];
      //rel
      ret[i][6] = dep[i][6];

      // cons
      ret[i][7] = cons[i][4];
    }
    return ret;
  }

  /**
   * @param text
   * @return
   */
  public static String[][][] constParse(String text) {
    List<String[][]> dep = new ArrayList<>();

    for (String[] sentence : MySplitter.getInstance().splitToArray(text)) {
      dep.add(constParseSentence(sentence));
    }
    return dep.toArray(new String[dep.size()][][]);
  }


  /**
   * @param sentence
   * @return
   */
  public static String[][] constParseSentence(String sentence) {
    return constParseSentence(MySplitter.getInstance().tokenizeToArray(
            sentence));
  }

  /**
   * @param form
   * @return
   */
  public static String[][] constParseSentence(String[] form) {
    return MyBerkeleyParser.getInstance().parseSentence(MyPurePos.getInstance().morphParseSentence(form));
  }

  /**
   * @param array
   */
  public static void print(String[][][] array) {
    for (int i = 0; i < array.length; ++i) {
      for (int j = 0; j < array[i].length; ++j) {
        for (int k = 0; k < array[i][j].length; ++k) {
          System.out.print(array[i][j][k] + "\t");
        }
        System.out.println();
      }
      System.out.println();
    }
  }

  /**
   * @param array
   * @param out
   * @param encoding
   */
  public static void write(String[][][] array, String out, String encoding) {
    BufferedWriter bufferedWriter = null;
    try {
      bufferedWriter = new BufferedWriter(new OutputStreamWriter(
              new FileOutputStream(out), encoding));
      for (int i = 0; i < array.length; ++i) {
        for (int j = 0; j < array[i].length; ++j) {
          for (int k = 0; k < array[i][j].length; ++k) {
            bufferedWriter.write(array[i][j][k] + "\t");
          }
          bufferedWriter.write("\n");
        }
        bufferedWriter.write("\n");
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        bufferedWriter.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * @param array
   */
  public static void printSentence(String[][] array) {
    for (int i = 0; i < array.length; ++i) {
      for (int j = 0; j < array[i].length; ++j) {
        System.out.print(array[i][j] + "\t");
      }
      System.out.println();
    }
  }

  /**
   * @param array
   * @return
   */
  public static String sentenceAsString(String[][] array) {
    StringBuffer stringBuffer = new StringBuffer();

    for (int i = 0; i < array.length; ++i) {
      for (int j = 0; j < array[i].length; ++j) {
        stringBuffer.append(array[i][j] + "\t");

      }
      stringBuffer.append("\n");
    }

    return stringBuffer.toString();
  }

  public static void main(String[] args) throws IOException {


    if (args.length < 2) {
      System.out.println(USAGE_MESSAGE);
      System.exit(0);
    }

    Map<String, String> params;
    params = new HashMap<>();

    for (int i = 0; i < args.length; i++) {
      try {
        params.put(args[i], args[i + 1]);
        i++;
      } catch (Exception e) {
        System.out.println(USAGE_MESSAGE);
        System.exit(0);
      }
    }

    if (params.containsKey("-mode")) {
      String mode = params.get("-mode");

      switch (mode) {
        /**
         * GUI
         */
        case "gui":
          GUI.init();
          break;

        /**
         * MorAna
         */
        case "morana":
          if (params.containsKey("-spelling")) {
            List<Univ> analyses = HunLemMor.getUnivMorphologicalAnalyses(params.get("-spelling"));
            for (Univ univ : analyses) {
              System.out.println(univ);
            }
          } else {
            System.out.println("usage: -mode morana -spelling spelling");
          }
          break;

        /**
         * Nooj
         */
        case "nooj":
          if (params.containsKey("-input") && params.containsKey("-output")) {
            if (params.containsKey("-encoding")) {
              Dep2Nooj.convert(
                      depParse(Util.readFileToString(params.get("-input"), "UTF-8")),
                      params.get("-output"), params.get("-encoding"));
            } else {
              Dep2Nooj.convert(
                      depParse(Util.readFileToString(params.get("-input"), "UTF-8")),
                      params.get("-output"), "utf-8");
            }
          } else {
            System.out
                    .println("usage: -mode nooj -input input -output output [-encoding encoding]");
          }
          break;

        case MODE_VALUE_MORPH_PARSE:
        case MODE_VALUE_DEP_PARSE:
        case MODE_VALUE_CONST_PARSE:
        case MODE_VALUE_PARSE:
          if (params.containsKey(KEY_INPUT) && params.containsKey(KEY_OUTPUT)) {

            //List<String> lines = null;
            List<String> lines = null;
            String[][] tokenized = null;

            String encoding = params.containsKey(KEY_ENCODING) ? params.get(KEY_ENCODING) : DEFAULT_ENCODING;

            if (params.containsKey(KEY_TOKENIZED) && Boolean.parseBoolean(params.get(KEY_TOKENIZED))) {
              tokenized = Util.readTokenizedFile(params.get(KEY_INPUT), encoding);
            } else {
              lines = SafeReader.read(params.get(KEY_INPUT), encoding);
            }

            /**
             * INIT
             */
            System.out.println(MESSAGE_INITIALIZING);
            switch (mode) {
              case MODE_VALUE_MORPH_PARSE:
                morphInit();
                break;
              case MODE_VALUE_DEP_PARSE:
                depInit();
                break;
              case MODE_VALUE_CONST_PARSE:
                constInit();
                break;
              case MODE_VALUE_PARSE:
                fullInit();
                break;
            }
            System.out.println(MESSAGE_INITIALIZED);

            long start = System.currentTimeMillis();

            BufferedWriter bufferedWriter = null;

            try {
              bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(params.get(KEY_OUTPUT)), encoding));

              if (lines != null) {
                switch (mode) {
                  case MODE_VALUE_MORPH_PARSE:
                    morphParse(lines, bufferedWriter);
                    break;
                  case MODE_VALUE_DEP_PARSE:
                    depParse(lines, bufferedWriter);
                    break;
                  case MODE_VALUE_CONST_PARSE:
                    constParse(lines, bufferedWriter);
                    break;
                  case MODE_VALUE_PARSE:
                    parse(lines, bufferedWriter);
                    break;
                }

              } else if (tokenized != null) {
                switch (mode) {
                  case MODE_VALUE_MORPH_PARSE:
                    morphParse(tokenized, bufferedWriter);
                    break;
                  case MODE_VALUE_DEP_PARSE:
                    depParse(tokenized, bufferedWriter);
                    break;
                  case MODE_VALUE_CONST_PARSE:
                    constParse(tokenized, bufferedWriter);
                    break;
                  case MODE_VALUE_PARSE:
                    parse(tokenized, bufferedWriter);
                    break;
                }
              }
            } catch (IOException e) {
              e.printStackTrace();
            } finally {
              bufferedWriter.close();
            }

            System.out.println(MESSAGE_FINISHED + (System.currentTimeMillis() - start) / 1000 + MESSAGE_TIME);
          } else {
            System.out.println(USAGE_MESSAGE_PARSE);
          }
          break;

        default:
          System.out.println(USAGE_MESSAGE);
      }
    }
  }
}
