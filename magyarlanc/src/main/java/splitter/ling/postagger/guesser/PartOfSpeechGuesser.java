package splitter.ling.postagger.guesser;

import splitter.ling.abbreviations.Abbreviations;
import splitter.ling.lexicon.Lexicon;
import splitter.ling.spellingstandardizer.SpellingStandardizer;
import splitter.utils.MutableInteger;
import splitter.utils.TaggedStrings;

import java.util.List;
import java.util.Map;

/**
 * Interface for a PartOfSpeechGuesser.
 * <p>
 * <p>
 * A part of speech guesser "guesses" the probable part(s) of speech
 * for a word which does not appear in the main lexicon.  Alternate
 * spellings, lexical rules based upon word prefixes or suffixes,
 * and many other approaches may be used to find potential
 * part of speech.
 * </p>
 */

public interface PartOfSpeechGuesser {
  /**
   * Guesses part of speech for a word.
   *
   * @param word The word.
   * @return Map of part of speech tags and counts.
   */

  public Map<String, MutableInteger> guessPartsOfSpeech(String word);

  /**
   * Guesses part of speech for a word.
   *
   * @param word        The word.
   * @param isFirstWord If word is first word in a sentence.
   * @return Map of part of speech tags and counts.
   */

  public Map<String, MutableInteger> guessPartsOfSpeech
  (
          String word,
          boolean isFirstWord
  );

  /**
   * Guesses part of speech for a word in a sentence.
   *
   * @param sentence  Sentence as a list of words.
   * @param wordIndex The word index in the sentence.
   * @return Map of part of speech tags and counts.
   */

  public Map<String, MutableInteger> guessPartsOfSpeech
  (
          List<String> sentence,
          int wordIndex
  );

  /**
   * Get spelling standardizer.
   *
   * @return The spelling standardizer.
   */

  public SpellingStandardizer getSpellingStandardizer();

  /**
   * Set spelling standardizer.
   *
   * @param spellingStandardizer The spelling standardizer.
   */

  public void setSpellingStandardizer
  (
          SpellingStandardizer spellingStandardizer
  );

  /**
   * Get the word lexicon.
   *
   * @return The word lexicon.
   */

  public Lexicon getWordLexicon();

  /**
   * Set the word lexicon.
   *
   * @param wordLexicon The word lexicon.
   */

  public void setWordLexicon(Lexicon wordLexicon);

  /**
   * Get the suffix lexicon.
   *
   * @return The suffix lexicon.
   */

  public Lexicon getSuffixLexicon();

  /**
   * Get cached lexicon for a word.
   *
   * @param word The word whose source lexicon we want.
   * @return The lexicon for the word.
   */

  public Lexicon getCachedLexiconForWord(String word);

  /**
   * Set the suffix lexicon.
   *
   * @param suffixLexicon The suffix lexicon.
   */

  public void setSuffixLexicon(Lexicon suffixLexicon);

  /**
   * Add an auxiliary word list.
   */

  public void addAuxiliaryWordList(TaggedStrings wordList);

  /**
   * Get auxiliary word lists.
   */

  public List getAuxiliaryWordLists();

  /**
   * Try using standardized spellings when guessing parts of speech.
   */

  public void setTryStandardSpellings(boolean tryStandardSpellings);

  /**
   * Check for possessives of known nouns when guessing parts of speech.
   */

  public void setCheckPossessives(boolean checkPossessives);

  /**
   * Set abbreviations.
   *
   * @param abbreviations Abbreviations.
   */

  public void setAbbreviations(Abbreviations abbreviations);
}
