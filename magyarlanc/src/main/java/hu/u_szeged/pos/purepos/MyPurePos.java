package hu.u_szeged.pos.purepos;

import hu.ppke.itk.nlpg.docmodel.ISentence;
import hu.ppke.itk.nlpg.purepos.ITagger;
import hu.ppke.itk.nlpg.purepos.MorphTagger;
import hu.ppke.itk.nlpg.purepos.cli.configuration.Configuration;
import hu.ppke.itk.nlpg.purepos.model.internal.CompiledModel;
import hu.ppke.itk.nlpg.purepos.model.internal.RawModel;
import hu.ppke.itk.nlpg.purepos.morphology.IMorphologicalAnalyzer;
import hu.ppke.itk.nlpg.purepos.morphology.NullAnalyzer;
import hu.u_szeged.config.Config;

import java.util.Arrays;
import java.util.List;

/**
 * Created by zsibritajanos on 2016.01.16..
 */
public class MyPurePos {
  private static final double BEAM_LOG_THETA = Math.log(1000);
  private double SUFFIX_LOG_THETA = Math.log(10);
  private int MAX_GUESSED = 5;
  private boolean USE_BEAM_SEARCH = false;

  private IMorphologicalAnalyzer analyzer;
  private ITagger iTagger;

  /**
   * singleton
   */
  private static volatile MyPurePos instance = null;

  /**
   * Get singleton.
   *
   * @return
   */
  public static synchronized MyPurePos getInstance() {
    if (instance == null) {
      instance = new MyPurePos(Config.getInstance().getPurePosModel());
    }
    return instance;
  }

  /**
   * @param modelFileName
   */
  private void init(String modelFileName) {
    if (Config.getInstance().getPurePosUseMorph()) {
      analyzer = new HunMorphAnalyzer();
    } else {
      analyzer = new NullAnalyzer();
    }

    RawModel rawmodel = null;
    try {
      rawmodel = MySerilalizer.readModel(modelFileName);
    } catch (Exception e) {
      e.printStackTrace();
    }

    CompiledModel<String, Integer> model = rawmodel.compile(new Configuration());
    this.iTagger = new MorphTagger(model, analyzer, BEAM_LOG_THETA, SUFFIX_LOG_THETA, MAX_GUESSED, USE_BEAM_SEARCH);
  }

  /**
   * @param modelFileName
   */
  public MyPurePos(String modelFileName) {
    init(modelFileName);
  }

  /**
   * @param tag
   * @return
   */
  private static String getPos(String tag) {
    return tag.substring(tag.indexOf("=") + 1, tag.indexOf("|"));
  }


  /**
   * @param tag
   * @return
   */
  private static String getFeatures(String tag) {
    return tag.substring(tag.indexOf("|") + 1);
  }

  /**
   * @param sentence
   * @return
   */
  public String[][] morphParseSentence(List<String> sentence) {


    String[][] morph = new String[sentence.size()][4];

    ISentence tagged = this.iTagger.tagSentence(sentence);

    for (int i = 0; i < tagged.size(); ++i) {
      //token
      morph[i][0] = tagged.get(i).getToken();

      // lemma
      morph[i][1] = tagged.get(i).getStem();

      // pos
      morph[i][2] = getPos(tagged.get(i).getTag());

      // features
      morph[i][3] = getFeatures(tagged.get(i).getTag());
    }

    return morph;
  }

  /**
   * @param sentence
   * @return
   */
  public String[][] morphParseSentence(String[] sentence) {
    return morphParseSentence(Arrays.asList(sentence));
  }

  public static void main(String[] args) {
    String[] sentence = "hez hez van xxxyyyyzzz hez itt hez".split(" ");
    String[][] morph = MyPurePos.getInstance().morphParseSentence(sentence);

    for (String[] m : morph) {
      for (String s : m) {
        System.out.print(s + "\t");
      }
      System.out.println("\t");
    }
  }
}
