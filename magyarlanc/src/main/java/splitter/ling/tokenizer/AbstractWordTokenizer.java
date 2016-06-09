package splitter.ling.tokenizer;

import splitter.ling.abbreviations.Abbreviations;
import splitter.ling.apostokens.AposTokens;
import splitter.utils.*;
import splitter.utils.logger.DummyLogger;
import splitter.utils.logger.Logger;
import splitter.utils.logger.UsesLogger;

import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Base class for deriving word tokenizers.
 */

abstract public class AbstractWordTokenizer extends IsCloseableObject implements
        WordTokenizer, IsCloseable, UsesLogger {
  /**
   * The preTokenizer used here,
   */

  protected PreTokenizer preTokenizer;

  /**
   * List of words starting with & or ' which should not be split.
   */

  protected TaggedStrings contractions;

  /**
   * URL for List of words starting with & or ' .
   */

  protected String contractionsURL = "resources/contractions.txt";

  /**
   * Logger used for output.
   */

  protected Logger logger;

  /**
   * Abbreviations.
   */

  protected Abbreviations abbreviations;

  /**
   * Apostrophe tokens.
   */

  protected AposTokens aposTokens;

  /**
   * True to coalesce adjacent hyphens.
   */

  protected boolean coalesceHyphens = false;

  /**
   * True to coalesce adjacent asterisks.
   */

  protected boolean coalesceAsterisks = true;

  /**
   * True if apostrophes can be single quotes.
   */

  protected boolean apostropheCanBeQuote = true;

  /**
   * Pattern for 2 or more hyphens.
   */

  protected Pattern hyphensPattern = Pattern
          .compile("^([-\u2011]{2,})$");

  protected Matcher hyphensMatcher = hyphensPattern.matcher("");

  /**
   * Create a word tokenizer.
   */

  public AbstractWordTokenizer() {
    // Get preTokenizer.

    preTokenizer = PreTokenizerFactory.newPreTokenizer();

    // Load list of contractions.
    loadContractions();

    // Create dummy logger.

    logger = new DummyLogger();

    // Load default abbreviations.

    abbreviations = new Abbreviations();

    // Load default apostrophe tokens.

    aposTokens = new AposTokens();
  }

  /**
   * Get the logger.
   *
   * @return The logger.
   */

  public Logger getLogger() {
    return logger;
  }

  /**
   * Set the logger.
   *
   * @param logger The logger.
   */

  public void setLogger(Logger logger) {
    this.logger = logger;
  }

  /**
   * Set abbreviations.
   *
   * @param abbreviations Abbreviations.
   */

  public void setAbbreviations(Abbreviations abbreviations) {
    this.abbreviations = abbreviations;
  }

  /**
   * Set apostophe tokens.
   *
   * @param aposTokens Apostrophe tokens.
   */

  public void setAposTokens(AposTokens aposTokens) {
    this.aposTokens = aposTokens;
  }

  /**
   * Get the preTokenizer.
   *
   * @return The preTokenizer.
   */

  public PreTokenizer getPreTokenizer() {
    return preTokenizer;
  }

  /**
   * Set the preTokenizer.
   *
   * @param preTokenizer The preTokenizer.
   */

  public void setPreTokenizer(PreTokenizer preTokenizer) {
    this.preTokenizer = preTokenizer;
  }

  /**
   * Load list of non-breakable words and contractions.
   */

  protected void loadContractions() {
    try {
      Set<String> contractionsSet = SetUtils.loadSet(
              DefaultWordTokenizer.class.getResource(contractionsURL), "utf-8");

      contractions = new SingleTagTaggedStrings(
              (String[]) contractionsSet
                      .toArray(new String[contractionsSet.size()]),
              "1");
    } catch (Exception e) {
      // e.printStackTrace();
    }
  }

  /**
   * Preprocess a word token.
   *
   * @param token     Token to preprocess.
   * @param tokenList List of previous tokens already issued.
   * @return Preprocessed token. The token list may also have been modified.
   */

  public String preprocessToken(String token, List<String> tokenList) {
    return token;
  }

  /**
   * True if character is a single opening quote.
   *
   * @param ch Character to check for being a single opening quote.
   * @return true if character is a single opening quote.
   */

  public boolean isSingleOpeningQuote(char ch) {
    return (ch == CharUtils.LSQUOTE) || ((ch == '\'') && apostropheCanBeQuote);
  }

  /**
   * Is character a letter or a single quote?
   *
   * @param ch Character.
   * @return true if character is a letter or a quote.
   */

  protected boolean isLetterOrSingleQuote(char ch) {
    return CharUtils.isLetter(ch) || (ch == CharUtils.LSQUOTE) || (ch == '\'');
  }

  /**
   * Is character a closing quote?
   *
   * @param ch Character.
   * @return true if character is a closing quote.
   */

  protected boolean isClosingQuote(char ch) {
    return (ch == CharUtils.RSQUOTE) || (ch == CharUtils.RDQUOTE)
            || ((ch == '\'') && apostropheCanBeQuote);
  }

  /**
   * Split a token if necessary.
   *
   * @param token Token to split.
   * @return The tokens.
   */

  protected String[] splitToken(String token) {
    String[] result = new String[]{token};

    // Is there is a period in the token?

    int iPos = token.indexOf(".");

    // If there is a period, and the token
    // is not just periods, currency,
    // a number, or a known abbreviation,
    // split the token around the periods.

    if ((iPos >= 0) && !CharUtils.isCurrency(token)
            && !CharUtils.isAllPeriods(token) && !token.endsWith(".")
            && !abbreviations.isKnownAbbreviation(token)
            && !CharUtils.isNumber(token)
            && !RomanNumeralUtils.isLooseRomanNumeral(token)) {
      // Up to and including period.

      String token1 = token.substring(0, iPos + 1);

      // Everything past period.

      String token2 = token.substring(iPos + 1);

      // If first part is known abbreviation,
      // leave period attached. If second
      // part is possessive 's or 'S, leave
      // that attached as well. Otherwise
      // split after the period.

      if (abbreviations.isKnownAbbreviation(token1)) {
        if (!token2.equals("'s") && !token2.equals("'S'")) {
          result = new String[]{token1, token2};
        }
      }
      // First part not known abbreviation.
      // If the part after the period is
      // 's or 'S, assume it is an abbreviation
      // anyway, and leave it as a single
      // token. Otherwise split off period
      // as separate token.
      else {
        if (!token2.equals("'s") && !token2.equals("'S'")) {
          result = new String[]{token1.substring(0, token1.length() - 1),
                  ".", token2};
        }
      }
    }

    // $$$PIB$$$ Kludge for "I." Most of the
    // time we want to split "I." into
    // "I" followed by ".", but there are
    // times when the "I." should remain.
    // We don't have enough information
    // to decide here, so always split, and
    // try to fix up an incorrect split later.
    /*
		 * else if ( token.equals( "I." ) ) { result = new String[]{ "I" , "." }; }
		 */
    return result;
  }

  /**
   * Add word to list of words in sentence.
   *
   * @param sentence Result sentence.
   * @param word     Word to add.
   */

  public void addWordToSentence(List<String> sentence, String word) {
    sentence.add(word);
  }

  /**
   * Find starting offsets of words in a sentence.
   *
   * @param sentenceText Text from which tokens were extracted.
   * @param words        List of words extracted from sentence text.
   *                     <p>
   *                     N.B. If the words aren't from the specified sentence text, the
   *                     resulting offsets will be meaningless.
   * @return int array of starting offsets in sentenceText for each word. The
   * first offset starts at 0. There is one more offset than the number
   * of words -- the last offset is where the word after the last word
   * would start.
   */

  public int[] findWordOffsets(String sentenceText, List<?> words) {
    // Allocate int vector to hold
    // word offsets.

    int wordCount = words.size();

    int[] result = new int[wordCount + 1];

    // Sentence text length.

    int sentenceTextLength = sentenceText.length();

    // Offset of current word.
    int offset = 0;
    // Loop over words.

    for (int i = 0; i < wordCount; i++) {
      // Get next word.

      String word = words.get(i).toString();

      // Skip leading whitespace.

      while (CharUtils.isWhitespace(sentenceText.charAt(offset))) {
        offset++;
      }
      // Store starting offset of word.

      result[i] = offset;

      // Find number of non-blank
      // characters in word =
      // word length.

      int nbCount = word.length();

      // Move forward in text that many
      // non-blank characters, skipping
      // over any whitespace. That will
      // give us the offset of the start of
      // the next word, if any.

      int tNbCount = 0;

      while (tNbCount < nbCount) {
        if (!CharUtils.isWhitespace(sentenceText.charAt(offset))) {
          tNbCount++;
        }

        offset++;
      }
    }
    // Store position of last word + 1.

    result[wordCount] = sentenceText.length();

    // Return word offsets to caller.
    return result;
  }

  /**
   * True if string contains only 2 or more hyphens.
   *
   * @param s String to check for hyphens.
   * @return true if string contains only hyphens and at least two of them.
   */

  public boolean isMultipleHyphens(String s) {
    hyphensMatcher.reset(s);

    return hyphensMatcher.matches();
  }

  /**
   * Break text into word tokens.
   *
   * @param text Text to break into word tokens.
   * @return List of word tokens.
   * <p>
   * <p>
   * Word tokens may be words, numbers, punctuation, etc.
   * </p>
   */

  public abstract List<String> extractWords(String text);
}

