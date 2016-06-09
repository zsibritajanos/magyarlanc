package hu.u_szeged.magyarlanc.resource;

import hu.u_szeged.config.Config;
import hu.u_szeged.magyarlanc.HunLemMor;
import hu.u_szeged.magyarlanc.MorAna;
import hu.u_szeged.pos.converter.CoNLLFeaturesToMSD;
import hu.u_szeged.pos.converter.KRToMSD;
import hu.u_szeged.pos.converter.MSDReducer;
import hu.u_szeged.pos.converter.MSDToCoNLLFeatures;
import rfsa.RFSA;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

public class ResourceHolder {

  //private static String data = "./data/";


  // other resources
  private static final String STOPWORDS = "stopword.txt";
  private static final String RFS = "rfsa.txt";
  private static final String CORRDIC = "corrdic.txt";
  private static final String HUN_ABBREV = "hun_abbrev.txt";

  // static objects
  private static Set<String> punctations = null;
  private static Set<String> morPhonDir = null;
  private static MSDToCoNLLFeatures msdToConllFeatures = null;
  private static CoNLLFeaturesToMSD conllFeaturesToMsd = null;

  private static MSDReducer msdReducer = null;

  private static Map<String, Set<MorAna>> corpus = null;
  private static Set<String> stopwords = null;
  private static Map<String, String> corrDic = null;
  private static Set<String> hunAbbrev = null;

  private static RFSA rfsa = null;


  private static KRToMSD krToMsd = null;

  // MorPhonDir
  public static Set<String> getMorPhonDir() {
    if (morPhonDir == null)
      initMorPhonDir();

    return morPhonDir;
  }

  public static void initMorPhonDir() {
    if (morPhonDir == null) {
      morPhonDir = Util.loadMorPhonDir();
    }
  }

  // KRToMSD
  public static KRToMSD getKRToMSD() {
    if (krToMsd == null) {
      initKRToMSD();
    }

    return krToMsd;
  }

  public static void initKRToMSD() {
    if (krToMsd == null) {
      krToMsd = new KRToMSD();
    }
  }

  // RFSA
  public static RFSA getRFSA() {
    if (rfsa == null) {
      initRFSA();
    }

    return rfsa;
  }

  public static void initRFSA() {
    if (rfsa == null) {
      try {
        rfsa = RFSA.read(ResourceHolder.class.getClassLoader().getResourceAsStream(RFS),
                Config.getInstance().getEncoding());
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  // corpus
  public static Map<String, Set<MorAna>> getCorpus() {
    if (corpus == null) {
      initCorpus();
    }

    return corpus;
  }

  public static void initCorpus() {
    if (corpus == null) {
      corpus = Util.readCorpus(Config.getInstance().getCorpus());
    }
  }

  // corrDic
  public static Map<String, String> getCorrDic() {
    if (corrDic == null) {
      initCorrDic();
    }

    return corrDic;
  }

  public static void initCorrDic() {
    if (corrDic == null) {
      corrDic = Util.readCorrDic(CORRDIC);
    }
  }

  // CoNLLFeaturesToMSD
  public static CoNLLFeaturesToMSD getCoNLLFeaturesToMSD() {
    if (conllFeaturesToMsd == null) {
      initCoNLLFeaturesToMSD();
    }

    return conllFeaturesToMsd;
  }

  public static void initCoNLLFeaturesToMSD() {
    if (conllFeaturesToMsd == null) {
      conllFeaturesToMsd = new CoNLLFeaturesToMSD();
    }
  }

  // MsdToCoNLLFeatures
  public static MSDToCoNLLFeatures getMSDToCoNLLFeatures() {
    if (msdToConllFeatures == null) {
      initMSDToCoNLLFeatures();
    }

    return msdToConllFeatures;
  }

  public static void initMSDToCoNLLFeatures() {
    if (msdToConllFeatures == null) {
      msdToConllFeatures = new MSDToCoNLLFeatures();
    }
  }

  // MSDReducer
  public static MSDReducer getMSDReducer() {
    if (msdReducer == null) {
      initMSDReducer();
    }

    return msdReducer;
  }

  public static void initMSDReducer() {
    if (msdReducer == null) {
      msdReducer = new MSDReducer();
    }
  }

  // punctations

  public static Set<String> getPunctations() {
    if (punctations == null) {
      initPunctations();
    }

    return punctations;
  }

  public static void initPunctations() {
    if (punctations == null) {
      punctations = Util.loadPunctations();
    }
  }

  public static Set<String> getStopwords() {
    if (stopwords == null) {
      stopwords = Util.readStopwords(STOPWORDS);
    }

    return stopwords;
  }

  public static Set<String> getHunAbbrev() {
    if (hunAbbrev == null) {
      hunAbbrev = Util.readList(HUN_ABBREV);
    }

    return hunAbbrev;
  }

  public static void main(String[] args) {
    HunLemMor.getMorphologicalAnalyses("8%-ánál");
  }
}
