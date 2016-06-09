package splitter.ling.tokenizer;

import splitter.ling.abbreviations.Abbreviations;
import splitter.ling.apostokens.AposTokens;

import java.util.List;

/**
 * Interface for tokenizing a string into "words".
 */

public interface WordTokenizer {
  /**
   * Get the preTokenizer.
   *
   * @return The preTokenizer.
   */

  public PreTokenizer getPreTokenizer();

  /**
   * Set the preTokenizer.
   *
   * @param preTokenizer The preTokenizer.
   */

  public void setPreTokenizer(PreTokenizer preTokenizer);

  /**
   * Set abbreviations.
   *
   * @param abbreviations Abbreviations.
   */

  public void setAbbreviations(Abbreviations abbreviations);

  /**    Add word to list of words in sentence.
   *
   * @param  sentence    Result sentence.
   * @param  word        Word to add.
   */

  /**
   * Set apostophe tokens.
   *
   * @param aposTokens Apostrophe tokens.
   */

  public void setAposTokens(AposTokens aposTokens);

  /**
   * Add word to list of words in sentence.
   *
   * @param sentence Result sentence.
   * @param word     Word to add.
   */

  public void addWordToSentence(List<String> sentence, String word);

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

  public List<String> extractWords(String text);

  /**
   * Find starting offsets of words in a sentence.
   *
   * @param sentenceText Text from which tokens were
   *                     extracted.
   * @param words        List of words extracted from
   *                     sentence text.
   *                     <p>
   *                     N.B.  If the words aren't from
   *                     the specified sentence text,
   *                     the resulting offsets will be
   *                     meaningless.
   * @return int array of starting offsets in
   * sentenceText for each word.
   * The first offset starts at 0.
   * There is one more offset
   * than the number of words -- the
   * last offset is where the word
   * after the last word would start.
   */

  public int[] findWordOffsets(String sentenceText, List<?> words);

  /**
   * Preprocess a word token.
   *
   * @param token     Token to preprocess.
   * @param tokenList List of previous tokens already issued.
   * @return Preprocessed token.
   * The token list may also have been modified.
   */

  public String preprocessToken(String token, List<String> tokenList);

  /**
   * Close down the word tokenizer.
   */

  public void close();
}

