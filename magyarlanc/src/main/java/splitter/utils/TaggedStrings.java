package splitter.utils;

import java.util.Set;

/**
 * Interface for a bunch of strings with associated values.
 * <p>
 * <p>
 * This is an interface for wrapping various types
 * of string lists.  Some string lists have one or more different
 * values for each string, while others have the same value for
 * each string.  The underlying implementation can be a map,
 * an array, a trie, a properties list, etc.
 * </p>
 */

public interface TaggedStrings {
  /**
   * See if specified string exists.
   *
   * @param string The string.
   * @return True if specified string exists.
   */

  public boolean containsString(String string);

  /**
   * Get the tag value associated with a string.
   *
   * @param string The string.
   * @return The tag value associated with the string.
   * May be null.
   */

  public String getTag(String string);

  /**
   * Set the tag value associated with a string.
   *
   * @param string The string.
   * @param tag    The tag.
   */

  public void putTag(String string, String tag);

  /**
   * Get number of strings.
   *
   * @return Number of strings.
   */

  public int getStringCount();

  /**
   * Get set of all unique tag values.
   *
   * @return List of all tag values.
   */

  public Set<String> getAllTags();

  /**
   * Get set of all unique string values.
   *
   * @return List of all strings.
   */

  public Set<String> getAllStrings();
}
