package hu.u_szeged.pos.converter;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class CoNLLFeaturesToMSD {

  /**
   * Patterns for the MSD attribute positions for ex. the noun patern contains,
   * that the first character of a noun MSD code contains the SubPOS
   * featurevalue the third character contains the Num featurevalue etc. It is
   * important that the second, fifth etc. characters are empty, that means it
   * has no value, the represtation in the MSD is a - sign.
   */
  private final static String NOUN_PATTERN = "SubPOS||Num|Cas||||NumP|PerP|NumPd";
  private final static String VERB_PATTERN = "SubPOS|Mood|Tense|Per|Num||||Def";
  private final static String ADJ_PATTERN = "SubPOS|Deg||Num|Cas|||||NumP|PerP|NumPd";
  private final static String PRONOUN_PATTERN = "SubPOS|Per||Num|Cas|NumP|||||||||PerP|NumPd";
  private final static String ARTICLE_PATTERN = "SubPOS";
  private final static String ADVERB_PATTERN = "SubPOS|Deg|Clitic|Num|Per";
  private final static String ADPOSITION_PATTERN = "SubPOS";
  private final static String CONJUNCTION_PATTERN = "SubPOS|Form|Coord";
  private final static String NUMERAL_PATTERN = "SubPOS||Num|Cas|Form|||||NumP|PerP|NumPd";
  private final static String INTERJECTION_PATTERN = "SubPOS";
  private final static String OTHER_PATTERN = "SubPOS|Type||Num|Cas||||NumP|PerP|NumPd";

  private static Map<String, Integer> nounMap = null;
  private static Map<String, Integer> verbMap = null;
  private static Map<String, Integer> adjMap = null;
  private static Map<String, Integer> pronounMap = null;
  private static Map<String, Integer> articleMap = null;
  private static Map<String, Integer> adverbMap = null;
  private static Map<String, Integer> adpositionMap = null;
  private static Map<String, Integer> conjunctionMap = null;
  private static Map<String, Integer> numeralMap = null;
  private static Map<String, Integer> interjectionMap = null;
  private static Map<String, Integer> otherMap = null;

  private Set<String> possibleFeatures = null;

  public CoNLLFeaturesToMSD() {

    /**
     * possible conll-2009 feature names
     */
    String[] features = new String[] { "SubPOS", "Num", "Cas", "NumP", "PerP",
        "NumPd", "Mood", "Tense", "Per", "Def", "Deg", "Clitic", "Form",
        "Coord", "Type" };
    this.setPossibleFeatures(new TreeSet<String>());

    for (String feature : features) {
      this.getPossibleFeatures().add(feature);
    }

    initMaps();

  }

  private void initMaps() {
    nounMap = patternToMap(NOUN_PATTERN);
    verbMap = patternToMap(VERB_PATTERN);
    adjMap = patternToMap(ADJ_PATTERN);
    pronounMap = patternToMap(PRONOUN_PATTERN);
    articleMap = patternToMap(ARTICLE_PATTERN);
    adverbMap = patternToMap(ADVERB_PATTERN);
    adpositionMap = patternToMap(ADPOSITION_PATTERN);
    conjunctionMap = patternToMap(CONJUNCTION_PATTERN);
    numeralMap = patternToMap(NUMERAL_PATTERN);
    interjectionMap = patternToMap(INTERJECTION_PATTERN);
    otherMap = patternToMap(OTHER_PATTERN);
  }

  private void setPossibleFeatures(Set<String> possibleFeatures) {
    this.possibleFeatures = possibleFeatures;
  }

  private Set<String> getPossibleFeatures() {
    return possibleFeatures;
  }

  /**
   * convert the pattern to map, that contains the position of the feature in
   * the MSD code for ex. the noun map will be {SubPOS=1, Num=3, Cas=4, NumP=8,
   * PerP=9, NumPd=10}
   */
  private Map<String, Integer> patternToMap(String pattern) {
    Map<String, Integer> map = null;
    map = new TreeMap<String, Integer>();

    String[] splitted = null;
    splitted = pattern.split("\\|");

    for (int i = 0; i < splitted.length; ++i) {
      if (!splitted[i].equals(""))
        map.put(splitted[i], i + 1);
    }
    return map;
  }

  /**
   * clean the unnecessary - signs from the end of the MSD code for ex. the
   * Nc-sn------ will be cleandet to Nc-sn.
   */
  private String cleanMsd(String msd) {
    int i = msd.length();

    while (msd.charAt(i - 1) == '-') {
      --i;
    }

    return msd.substring(0, i);
  }

  /**
   * Split the String of the features via the | sing, and put the featurenames
   * and its values to a map.
   */
  private Map<String, String> getFeaturesMap(String features) {
    Map<String, String> featuresMap = null;
    featuresMap = new LinkedHashMap<String, String>();
    String[] pair = null;

    String[] splitted = features.split("\\|");

    for (String feature : splitted) {
      pair = feature.split("=");

      if (pair.length != 2) {
        System.err.println("Incorrect feature: " + feature);
        return null;
      }

      if (!this.getPossibleFeatures().contains(pair[0])) {
        System.err.println("Incorrect featurename: " + pair[0]);
        return null;
      }

      featuresMap.put(pair[0], pair[1]);
    }
    return featuresMap;
  }

  /**
   * Convert the features to MSD code, using the MSD positions and
   * featurevalues, that belongs to the current POS.
   */
  private String convert(Character pos, Map<String, Integer> positionsMap,
      Map<String, String> featuresMap) {

    StringBuffer msd = null;
    msd = new StringBuffer(pos + "----------------");

    for (Map.Entry<String, String> entry : featuresMap.entrySet()) {
      if (!entry.getValue().equals("none"))
        msd.setCharAt(positionsMap.get(entry.getKey()), entry.getValue()
            .charAt(0));
    }

    /**
     * f�n�vi igenvek ha csak sim�n 'n�zni' van, akkor nem kell, de ha n�znie,
     * akkor igen
     */

    if (pos == 'V' && msd.charAt(3) == '-') {
      msd.setCharAt(3, 'p');
      String cleaned = null;
      cleaned = cleanMsd(msd.toString());

      if (cleaned.length() == 4)
        return cleaned.substring(0, 3);
    }

    return cleanMsd(msd.toString());
  }

  public String convert(String pos, String features) {
    if (pos.length() > 1) {
      return "_";
    }

    else
      return convert(pos.charAt(0), features);
  }

  /**
   * convert the POS character and feature String to MSD code for ex. the POS
   * character can be 'N' and the feature String that belongs to the POS
   * character can be "SubPOS=c|Num=s|Cas=n|NumP=none|PerP=none|NumPd=none"
   */
  public String convert(char pos, String features) {

    /**
     * The relevant punctations has no features, its featurestring contain only
     * a _ character. The MSD code of a relevant punctations is the punctation
     * itself.
     */

    /**
     * featuresstring can't be empy or null
     */
    if (features == "" || features == null) {
      System.err.println("Empty (or null) features");
      System.err.println("Unable to convert: " + pos + " " + features);
      return null;
    }

    try {
      /**
       * X, Y, Z has no features relevant punctation has no features it is it's
       * possible that I has no featues
       */
      if (features.equals("_")) {
        return String.valueOf(pos);
      }

      Map<String, String> featuresMap = null;
      featuresMap = getFeaturesMap(features);

      if ((featuresMap == null)
          && (pos != 'X' && pos != 'Y' && pos != 'Z' && pos != 'I')) {

      }

      switch (pos) {
      case 'N':
        return convert(pos, nounMap, featuresMap);
      case 'V':
        return convert(pos, verbMap, featuresMap);
      case 'A':
        return convert(pos, adjMap, featuresMap);
      case 'P':
        return convert(pos, pronounMap, featuresMap);
      case 'T':
        return convert(pos, articleMap, featuresMap);
      case 'R':
        return convert(pos, adverbMap, featuresMap);
      case 'S':
        return convert(pos, adpositionMap, featuresMap);
      case 'C':
        return convert(pos, conjunctionMap, featuresMap);
      case 'M':
        return convert(pos, numeralMap, featuresMap);
      case 'I':
        return convert(pos, interjectionMap, featuresMap);
      case 'O':
        return convert(pos, otherMap, featuresMap);
      case 'X':
        return "X";
      case 'Y':
        return "Y";
      case 'Z':
        return "Z";
      case 'K':
        return "K";
      default:
        System.err.println("Incorrect POS: " + pos);
        return null;

      }
    } catch (NullPointerException e) {
      System.err.println("Unable to convert: " + pos + " " + features);
    }

    return null;
  }

  public static void main(String[] args) {
    System.err.println(new CoNLLFeaturesToMSD().convert("O",
        "SubPOS=e|Type=w|Num=s|Cas=n|NumP=none|PerP=none|NumPd=none"));
  }
}
