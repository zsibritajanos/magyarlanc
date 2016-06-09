package splitter;

import splitter.ling.sentencesplitter.DefaultSentenceSplitter;
import splitter.ling.tokenizer.DefaultWordTokenizer;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zsibritajanos on 2015.11.30..
 */
public class MySplitter {

  /**
   * URL
   */
  private static final String URL = "(?i)\\b((?:[a-z][\\w-]+:(?:/{1,3}|[a-z0-9%])|www\\d{0,3}[.]|[a-z0-9.\\-]+[.][a-z]{2,4}/)(?:[^\\s()<>]+|\\(([^\\s()<>]+|(\\([^\\s()<>]+\\)))*\\))+(?:\\(([^\\s()<>]+|(\\([^\\s()<>]+\\)))*\\)|[^\\s`!()\\[\\]{};:'\".,<>?������]))";
  private static final Pattern URL_PATTERN = Pattern.compile(URL);

  /**
   * PUNCT
   */
  private static final String PUNCT = "[!?]{2,}";
  private static final Pattern PUNCT_PATTERN = Pattern.compile(PUNCT);


  /**
   * SMILEY
   * https://github.com/twitter/commons/blob/master/src/java/com/twitter/common/text/extractor/EmoticonExtractor.java
   * https://github.com/twitter/commons/blob/master/src/java/com/twitter/common/text/detector/PunctuationDetector.java
   */
  private static final String SPACE_EXCEPTIONS = "\\n\\r";
  private static final String SPACE_CHAR_CLASS = "\\p{C}\\p{Z}&&[^" + SPACE_EXCEPTIONS + "\\p{Cs}]";
  private static final String SPACE_REGEX = "[" + SPACE_CHAR_CLASS + "]";
  private static final String PUNCTUATION_CHAR_CLASS = "\\p{P}\\p{M}\\p{S}" + SPACE_EXCEPTIONS;
  private static final String PUNCTUATION_REGEX = "[" + PUNCTUATION_CHAR_CLASS + "]";
  private static final String EMOTICON_DELIMITER = SPACE_REGEX + "|" + PUNCTUATION_REGEX;

  private static final Pattern SMILEY_REGEX_PATTERN = Pattern.compile("[:;]-?[)DdpP]|:[ -]\\)|<3");
  private static final Pattern FROWNY_REGEX_PATTERN = Pattern.compile(":[(<]|:[ -]\\(");
  private static final Pattern EMOTICON_REGEX_PATTERN = Pattern.compile("(?<=^|" + EMOTICON_DELIMITER + ")(" + SMILEY_REGEX_PATTERN.pattern() + "|" + FROWNY_REGEX_PATTERN.pattern() + ")+(?=$|" + EMOTICON_DELIMITER + ")");

  private static DefaultWordTokenizer tokenizer = new DefaultWordTokenizer();
  private static DefaultSentenceSplitter splitter = new DefaultSentenceSplitter();

  private static final String URL_REPLACE = "URL";
  private static final String EMO_REPLACE = "EMO";
  private static final String PUNCT_REPLACE = "PUNCT";


  /**
   * singleton
   */
  private static volatile MySplitter instance = null;

  /**
   * Get singleton.
   *
   * @return
   */
  public static synchronized MySplitter getInstance() {
    if (instance == null) {
      instance = new MySplitter();
    }
    return instance;
  }

  private MySplitter() {

  }

  /**
   * @param text
   * @return
   */
  public static List<int[]> getMatchIndices(String text, Pattern pattern) {

    Matcher matcher = pattern.matcher(text);

    List<int[]> indices = new ArrayList<>();
    while (matcher.find()) {
      indices.add(new int[]{matcher.start(), matcher.end()});
    }

    return indices;
  }

  public List<String> tokenize(String sentence) {
    return tokenize(sentence, true, true, true);
  }

  public List<String> tokenize(String sentence, boolean isPunct, boolean isUrl, boolean isEmo) {

    String ORI = sentence;


    sentence = sentence.trim();
    if (sentence.length() > 0) {

      List<String> replacedPuncts = new LinkedList<>();

      if (isPunct) {
        // PUNCR indices
        List<int[]> indices = getMatchIndices(sentence, PUNCT_PATTERN);

        for (int[] index : indices) {

          if (index[1] > sentence.length()) {
            index[1] = sentence.length();
          }
          String found = sentence.substring(index[0], index[1]).trim();
          replacedPuncts.add(found);
        }


        if (replacedPuncts.size() > 0) {
          for (String r : replacedPuncts) {
            sentence = sentence.replace(r, " " + PUNCT_REPLACE + " ");
          }
        }
      }

      List<String> replacedEmos = new LinkedList<>();

      if (isEmo) {
        // EMO indices
        List<int[]> emoIndices = getMatchIndices(sentence, EMOTICON_REGEX_PATTERN);

        for (int[] index : emoIndices) {

          if (index[1] > sentence.length()) {
            index[1] = sentence.length();
          }
          String emo = sentence.substring(index[0], index[1]).trim();
          replacedEmos.add(emo);
        }


        if (replacedEmos.size() > 0) {
          for (String r : replacedEmos) {
            sentence = sentence.replace(r, " " + EMO_REPLACE + " ");
          }
        }
      }


      List<String> replacedUrls = new LinkedList<>();
      if (isUrl) {
        // URL indices
        List<int[]> urlIndices = getMatchIndices(sentence, URL_PATTERN);

        for (int[] index : urlIndices) {
          if (index[1] > sentence.length()) {
            index[1] = sentence.length();
          }
          String url = sentence.substring(index[0], index[1]).trim();
          replacedUrls.add(url);
        }

        for (String r : replacedUrls) {
          sentence = sentence.replace(r, " " + URL_REPLACE + " ");
        }
      }


      List<String> tokens = new LinkedList<>();

      for (String token : tokenizer.extractWords(sentence)) {
        if (isUrl) {
          if (token.equals(URL_REPLACE)) {
            if (replacedUrls.size() > 0) {
              token = replacedUrls.get(0);
              replacedUrls.remove(0);
            }
          }
        }

        if (isEmo) {
          if (token.equals(EMO_REPLACE)) {
            if (replacedEmos.size() > 0) {
              token = replacedEmos.get(0);
              replacedEmos.remove(0);
            }
          }
        }

        if (isPunct) {
          if (token.equals(PUNCT_REPLACE)) {
            if (replacedPuncts.size() > 0) {
              token = replacedPuncts.get(0);
              replacedPuncts.remove(0);
            }
          }
        }
        tokens.add(token);
      }

      if (isValid(tokens)) {
        return tokens;
      } else {
        return tokenizer.extractWords(ORI);
      }

    } else {
      return null;
    }
  }

  /**
   *
   * @param tokens
   * @return
   */
  private static boolean isValid(List<String> tokens) {
    boolean isOK = true;
    for (String token : tokens) {
      if (token.contains(URL_REPLACE) || token.contains(EMO_REPLACE) || token.contains(PUNCT_REPLACE)) {
        isOK = false;
        break;
      }
    }
    return isOK;
  }

  /**
   * @param text
   * @return
   */
  public String[][] splitToArray(String text) {


    List<List<String>> splitted = this.split(text);
    String[][] sentences = new String[splitted.size()][];

    for (int i = 0; i < sentences.length; ++i) {
      sentences[i] = splitted.get(i)
              .toArray(new String[splitted.get(i).size()]);
    }
    return sentences;
  }

  /**
   * @param text
   * @return
   */
  public List<List<String>> split(String text) {

    List<List<String>> sentences = splitter.extractSentences(text, tokenizer);

    int[] offsets = splitter.findSentenceOffsets(text, sentences);

    List<List<String>> ret = new LinkedList<>();
    for (int i = 1; i < offsets.length; ++i) {
      String sentence = text.substring(offsets[i - 1], offsets[i]).trim();
      ret.add(tokenize(sentence));
    }

    return ret;
  }

  public String[] tokenizeToArray(String sentence) {
    List<String> tokenized = tokenize(sentence);
    return tokenized.toArray(new String[tokenized.size()]);
  }
}
