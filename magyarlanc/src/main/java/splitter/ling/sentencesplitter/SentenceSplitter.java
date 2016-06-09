package splitter.ling.sentencesplitter;

import splitter.ling.abbreviations.Abbreviations;
import splitter.ling.postagger.guesser.PartOfSpeechGuesser;
import splitter.ling.tokenizer.WordTokenizer;

import java.util.List;

/**
 * Interface for splitting text into sentences.
 */

public interface SentenceSplitter {
  /**
   * Set part of speech guesser.
   *
   * @param partOfSpeechGuesser Part of speech guesser.
   *                            <p>
   *                            <p>
   *                            A sentence MySplitter may use part of speech information
   *                            to disambiguate end-of-sentence boundary conditions.
   *                            The part of speech guesser provides access to the
   *                            lexicons and guessing algorithms for determining the
   *                            possible parts of speech for a word without performing
   *                            a full part of speech tagging operation.
   *                            </p>
   */

  public void setPartOfSpeechGuesser
  (
          PartOfSpeechGuesser partOfSpeechGuesser
  );

  /**
   * Set sentence MySplitter iterator.
   *
   * @param sentenceSplitterIterator Sentence MySplitter iterator.
   */

  public void setSentenceSplitterIterator
  (
          SentenceSplitterIterator sentenceSplitterIterator
  );

  /**
   * Set abbreviations.
   *
   * @param abbreviations Abbreviations.
   */

  public void setAbbreviations(Abbreviations abbreviations);

  /**
   * Break text into sentences and tokens.
   *
   * @param text      Text to break into sentences and tokens.
   * @param tokenizer Tokenizer to use for breaking sentences
   *                  into words.
   * @return List of sentences.  Each sentence
   * is itself a list of word tokens.
   * <p>
   * <p>
   * Word tokens may be words, numbers, punctuation, etc.
   * </p>
   */

  public List<List<String>> extractSentences
  (
          String text,
          WordTokenizer tokenizer
  );

  /**
   * Break text into sentences and tokens.
   *
   * @param text Text to break into sentences and tokens.
   * @return List of sentences.  Each sentence
   * is itself a list of word tokens.
   * <p>
   * <p>
   * Word tokens may be words, numbers, punctuation, etc.  The default
   * word tokenizer is used.
   * </p>
   */

  public List<List<String>> extractSentences(String text);

  /**
   * Find starting offsets of sentences extracted from a text.
   *
   * @param text      Text from which sentences were
   *                  extracted.
   * @param sentences List of sentences (each a list of
   *                  words) extracted from text.
   *                  <p>
   *                  N.B.  If the sentences aren't from
   *                  the specified text, the resulting
   *                  offsets will be meaningless.
   * @return int array of starting offsets in text
   * for each sentence.  The first offset
   * starts at 0.  There is one more offset
   * than the number of sentences -- the
   * last offset is where the sentence
   * after the last sentence would start.
   */

  public int[] findSentenceOffsets
  (
          String text,
          List<List<String>> sentences
  );
}
