package splitter.ling.sentencesplitter;


import com.ibm.icu.text.BreakIterator;

import java.util.Locale;

/**
 * BreakIterator-based sentence MySplitter iterator.
 */

public class ICU4JBreakIteratorSentenceSplitterIterator
        implements SentenceSplitterIterator {
  /**
   * BreakIterator used to iterate over sentences.
   */

  protected BreakIterator sentenceExtractor;

  /**
   * Start of current sentence.
   */

  protected int start = BreakIterator.DONE;

  /**
   * End of current sentence.
   */

  protected int end = BreakIterator.DONE;

  /**
   * Text to break up.
   */

  protected String text;

  /**
   * Create sentence iterator.
   */

  public ICU4JBreakIteratorSentenceSplitterIterator() {
    sentenceExtractor =
            BreakIterator.getSentenceInstance(Locale.US);
  }

  /**
   * Create sentence iterator with specified locale.
   *
   * @param locale The locale.
   */

  public ICU4JBreakIteratorSentenceSplitterIterator(Locale locale) {
    sentenceExtractor =
            BreakIterator.getSentenceInstance(locale);
  }

  /**
   * Create sentence iterator over text.
   *
   * @param text The text from which to extract sentences.
   */

  public ICU4JBreakIteratorSentenceSplitterIterator(String text) {
    sentenceExtractor =
            BreakIterator.getSentenceInstance(Locale.US);

    setText(text);
  }

  /**
   * Create sentence iterator over text with specified locale.
   *
   * @param text   The text from which to extract sentences.
   * @param locale The locale.
   */

  public ICU4JBreakIteratorSentenceSplitterIterator
  (
          String text,
          Locale locale
  ) {
    sentenceExtractor =
            BreakIterator.getSentenceInstance(locale);

    setText(text);
  }

  /**
   * Set the text to split.
   *
   * @param text Text to split.
   */

  public void setText(String text) {
    this.text = text;

    sentenceExtractor.setText(this.text);

    start = sentenceExtractor.first();
    end = sentenceExtractor.next();
  }

  /**
   * Check if there is another sentence available.
   *
   * @return true if another sentence is available.
   */

  public boolean hasNext() {
    return (end != BreakIterator.DONE);
  }

  /**
   * Return next sentence.
   *
   * @return next sentence, or null if none.
   */

  public String next() {
    String result = null;

    if (end != BreakIterator.DONE) {
      result = text.substring(start, end);
      start = end;
      end = sentenceExtractor.next();
    }

    return result;
  }

  /**
   * Return next sentence without advancing sentence pointer.
   *
   * @return next sentence, or null if none.
   */

  public String peek() {
    String result = null;

    if (end != BreakIterator.DONE) {
      result = text.substring(start, end);
    }

    return result;
  }
}

