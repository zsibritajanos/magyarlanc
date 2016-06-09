package splitter.utils;

import java.util.Arrays;
import java.util.Set;

/**
 * A tagged strings implementation where all strings have the same tag value.
 * <p>
 * <p>
 * Since all the strings have the same tag value, we use a sorted string
 * array to hold the strings.  We use binary search to locate the
 * strings of interest.  While using an array is slower than using
 * a hash map (binary search = o(n*log(n)), hash = o(1)), the array
 * saves lots of memory.
 * </p>
 */

public class SingleTagTaggedStrings implements TaggedStrings {
  /**
   * Array of string values.
   */

  protected String[] strings = null;

  /**
   * The common tag for all the string values.
   */

  protected String tag = null;

  /**
   * Create SingleTagTaggedStrings object.
   *
   * @param strings String array of strings.
   * @param tag     The common tag for all the strings.
   */

  public SingleTagTaggedStrings(String[] strings, String tag) {
    this.strings = strings;
    this.tag = tag;

    if (strings != null) {
      Arrays.sort(strings);
    }
  }

  /**
   * See if specified string exists.
   *
   * @param string The string.
   * @return True if specified string exists.
   */

  public boolean containsString(String string) {
    boolean result = false;

    if (strings != null) {
      result = (Arrays.binarySearch(strings, string) >= 0);
    }

    return result;
  }

  /**
   * Get the tag value associated with a string.
   *
   * @param string The string.
   * @return The tag value associated with the string.
   * May be null.
   */

  public String getTag(String string) {
    String result = null;

    int index = Arrays.binarySearch(strings, string);

    if (index >= 0) result = tag;

    return result;
  }

  /**
   * Get number of strings.
   *
   * @return Number of strings.
   */

  public int getStringCount() {
    int result = 0;

    if (strings != null) result = strings.length;

    return result;
  }

  /**
   * Get set of all unique string values.
   *
   * @return Set of all strings.
   */

  public Set<String> getAllStrings() {
    Set<String> result = SetFactory.createNewSet();

    result.addAll(Arrays.asList(strings));

    return result;
  }

  /**
   * Get set of all unique tag values.
   *
   * @return Set of all unique tag values.
   * <p>
   * <p>
   * The result always contains just one value, the common tag value
   * associated with all the strings.
   * </p>
   */

  public Set<String> getAllTags() {
    Set<String> result = SetFactory.createNewSet();

    if (tag != null) result.add(tag);

    return result;
  }

  /**
   * Set the tag value associated with a string.
   *
   * @param string The string.
   * @param tag    The tag.
   *               <p>
   *               <p>
   *               This is a no-op in this implementation.
   *               </p>
   */

  public void putTag(String string, String tag) {
  }
}