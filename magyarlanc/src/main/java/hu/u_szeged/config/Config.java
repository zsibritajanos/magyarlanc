package hu.u_szeged.config;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * Created by zsibritajanos on 2016.01.04..
 */
public class Config {

  /**
   * config file
   */
  public static String configFile = "./properties/magyarlanc.props";
  public static String configFileJar = "magyarlanc.props";

  /**
   * keys in the config file
   */
  private static final String CONFIG_KEY_ENCODING = "encoding";
  private static final String CONFIG_KEY_DEP_PARSER_MODEL = "dep_parser_model";
  private static final String CONFIG_KEY_PUREPOS_MODEL = "purepos_model";
  private static final String CONFIG_KEY_PUREPOS_USE_MORPH = "purepos_use_morph";
  private static final String CONFIG_KEY_CONST_PARSER_MODEL = "const_parser_model";
  private static final String CONFIG_KEY_CONST_NUM_OF_GRAMMARS = "const_num_of_grammars";
  private static final String CONFIG_KEY_CONST_WRITE_INCORRECT_TREE = "const_write_incorrect_tree";
  private static final String CONFIG_KEY_CORPUS = "corpus";
  private static final String CONFIG_KEY_GUI_INIT_SENTENCE = "gui_init_sentence";
  private static final String CONFIG_KEY_DEP_MAX_SENTENCE_LENGTH = "dep_max_sentence_length";

  private static final String CONFIG_KEY_WSDL = "wsdl";

  private static final String CONFIG_KEY_CONST_VISUALIZER = "const_visualizer";

  /**
   * singleton
   */
  private static volatile Config instance = null;

  /**
   * properties
   */
  private Properties properties;

  private Config() {
    init();
  }

  /**
   * Reads the config file.
   */
  private void init() {

    this.properties = new Properties();

    InputStream input = null;

    try {
      input = new FileInputStream(configFile);
    } catch (FileNotFoundException e) {
      try {
        input = Config.class.getClassLoader().getResourceAsStream(configFileJar);
      } catch (Exception e2) {
        e.printStackTrace();
      }
    }

    try {
      InputStreamReader reader = new InputStreamReader(input, "UTF-8");
      properties.load(reader);
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (input != null) {
        try {
          input.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }

  /**
   * Get singleton.
   *
   * @return
   */
  public static synchronized Config getInstance() {
    if (instance == null) {
      instance = new Config();
    }
    return instance;
  }

  /**
   * @return
   */
  public String getEncoding() {
    return this.properties.getProperty(CONFIG_KEY_ENCODING);
  }

  public String getPurePosModel() {
    return this.properties.getProperty(CONFIG_KEY_PUREPOS_MODEL);
  }

  public String getDepParserModel() {
    return this.properties.getProperty(CONFIG_KEY_DEP_PARSER_MODEL);
  }

  public String getConstParserModel() {
    return this.properties.getProperty(CONFIG_KEY_CONST_PARSER_MODEL);
  }

  public int getConstNumOfGrammars() {
    return Integer.parseInt(this.properties.getProperty(CONFIG_KEY_CONST_NUM_OF_GRAMMARS, "4"));
  }

  public boolean getConstWriteIncorrectTree() {
    return Boolean.parseBoolean(this.properties.getProperty(CONFIG_KEY_CONST_WRITE_INCORRECT_TREE, "false"));
  }

  public boolean getPurePosUseMorph() {
    return Boolean.parseBoolean(this.properties.getProperty(CONFIG_KEY_PUREPOS_USE_MORPH));
  }

  public String getCorpus() {
    return this.properties.getProperty(CONFIG_KEY_CORPUS);
  }

  public String getGuiInitSentences() {
    return this.properties.getProperty(CONFIG_KEY_GUI_INIT_SENTENCE);
  }

  public int getDepMaxSentenceLength() {
    return Integer.parseInt(this.properties.getProperty(CONFIG_KEY_DEP_MAX_SENTENCE_LENGTH));
  }
  
  public String getConstVisualizer() {
    return this.properties.getProperty(CONFIG_KEY_CONST_VISUALIZER, "pta");
  }

  public String getWsdl() {
    return this.properties.getProperty(CONFIG_KEY_WSDL);
  }


  public static void main(String[] args) {

    System.out.println(Config.getInstance().getEncoding());
    System.out.println(Config.getInstance().getDepParserModel());
    System.out.println(Config.getInstance().getPurePosModel());
  }
}

