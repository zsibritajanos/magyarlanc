package splitter.ling.sentencesplitter;

/**
 * Abstract sentence iterator.
 */

public interface SentenceSplitterIterator {
  /**
   * Check if there is another sentence available.
   *
   * @return true if another sentence is available.
   */

  public boolean hasNext();

  /**
   * Return next sentence.
   *
   * @return next sentence as a string, or null if none.
   */

  public String next();

  /**
   * Peek ahead at text of next sentence.
   *
   * @return next sentence as a string, or null if none.
   */

  public String peek();

  /**
   * Set the text to split.
   *
   * @param text Text with sentences over which to iterate.
   */

  public void setText(String text);
}
