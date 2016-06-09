package splitter.ling.sentencesplitter;

import java.util.*;

/**
 * Splits text into sentences using regular expressions.
 * <p>
 * <p>
 * Uses a the ICU4J BreakIterator to identify candidate sentences.
 * Several heuristics are used to correct the initial sentence
 * identification.
 * </p>
 */

public class ICU4JBreakIteratorSentenceSplitter
        extends AbstractSentenceSplitter
        implements SentenceSplitter {
  /**
   * Create regular expression sentence extractor.
   */

  public ICU4JBreakIteratorSentenceSplitter() {
    sentenceSplitterIterator =
            new ICU4JBreakIteratorSentenceSplitterIterator();
  }

  /**
   * Create regular expression sentence extractor for locale.
   */

  public ICU4JBreakIteratorSentenceSplitter(Locale locale) {
    sentenceSplitterIterator =
            new ICU4JBreakIteratorSentenceSplitterIterator(locale);
  }
}
