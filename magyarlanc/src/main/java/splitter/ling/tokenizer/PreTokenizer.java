package splitter.ling.tokenizer;

/**
 * Interface for preparing a string for tokenization.
 */

public interface PreTokenizer {
  /**
   * Prepares a string for tokenization.
   *
   * @param s The string to prepare for tokenization.
   * @return The modified string ready for tokenization.
   */

  public String pretokenize(String s);

  /**
   * Close down the preTokenizer.
   */

  public void close();
}

