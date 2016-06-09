package splitter.ling.sentencesplitter;

/**
 * Splits text into sentences.
 * <p>
 * <p>
 * Uses the built-in Java BreakIterator class to identify candidate
 * sentences.  Several heuristics are used to correct the sentence
 * identification produced by BreakIterator when a sentence potentially
 * ends with an abbreviation or a bracket character (right parenthesis,
 * right bracket, or right brace).
 * </p>
 */

public class DefaultSentenceSplitter
        extends ICU4JBreakIteratorSentenceSplitter
        implements SentenceSplitter {
  public DefaultSentenceSplitter() {
    super();
  }
}
