package splitter.ling.partsofspeech;

import splitter.utils.Compare;

import java.io.Serializable;

/**
 * A part of speech.
 * <p>
 * <p>This is a base class for parts of speech.</p>.
 * <p>
 * <p>All part of speech entries have the following attributes:</p>
 * <p>
 * <ul>
 * <li>A tag, e.g., "np1" for an singular proper name.</li>
 * <li>A word class for the part of speech.</li>
 * <li>A major word class for the part of speech.</li>
 * <li>An optional general tag name for the part of speech.</li>
 * <li>An optional description of the part of speech.</li>
 * <li>An optional lemmatization class for the part of speech.</li>
 * </ul>
 * <p>
 * <p>
 * The general tag name allows reference to a few important tags
 * in a tag set independent fashion.  General tag names are
 * defined as follows.
 * </p>
 * <p>
 * <table>
 * <tr>
 * <th>General tag name</th>
 * <th>Meaning</th>
 * </tr>
 * <tr>
 * <td>
 * noun-singular
 * </td>
 * <td>
 * Tag for a singular noun.
 * </td>
 * </tr>
 * <tr>
 * <td>
 * noun-plural
 * </td>
 * <td>
 * Tag for a plural noun.
 * </td>
 * <td>
 * noun-proper-singular
 * </td>
 * <td>
 * Tag for a singular proper noun.
 * </td>
 * </tr>
 * <tr>
 * <td>
 * noun-proper-plural
 * </td>
 * <td>
 * Tag for a plural proper noun.
 * </td>
 * </tr>
 * <tr>
 * <td>
 * numeral-cardinal
 * </td>
 * <td>
 * Tag for a cardinal number.
 * </td>
 * </tr>
 * </table>
 */

public class PartOfSpeech implements Comparable, Serializable {
  /**
   * Tag.
   */

  protected String tag = "";

  /**
   * Word class.
   */

  protected String wordClass = "";

  /**
   * Major word class.
   */

  protected String majorWordClass = "";

  /**
   * General tag name.
   */

  protected String generalTagName = "";

  /**
   * Description.
   */

  protected String description = "";

  /**
   * Lemma word class.
   */

  protected String lemmaWordClass = "";

  /**
   * Creates a new part of speech.
   */

  public PartOfSpeech() {
  }

  /**
   * Creates a new part of speech.
   *
   * @param tag            Tag name.
   * @param wordClass      The word class.
   * @param majorWordClass The major word class.
   * @param lemmaWordClass The lemma word class.
   * @param generalTagName The general tag name.
   * @param description    The description.
   */

  public PartOfSpeech
  (
          String tag,
          String wordClass,
          String majorWordClass,
          String lemmaWordClass,
          String generalTagName,
          String description
  ) {
    this.tag = tag;
    this.wordClass = wordClass;
    this.majorWordClass = majorWordClass;
    this.lemmaWordClass = lemmaWordClass;
    this.generalTagName = generalTagName;
    this.description = description;
  }

  /**
   * Gets the tag.
   *
   * @return The tag.
   */

  public String getTag() {
    return tag;
  }

  /**
   * Sets the tag.
   *
   * @param tag The tag.
   */

  public void setTag(String tag) {
    this.tag = tag;
  }

  /**
   * Gets the description.
   *
   * @return The description, or null if none.
   */

  public String getDescription() {
    return description;
  }

  /**
   * Sets the description.
   *
   * @param description Description.  May be empty.
   */

  public void setDescription(String description) {
    this.description = (description != null) ? description : "";
  }

  /**
   * Gets the word class.
   *
   * @return The word class.  May be empty.
   */

  public String getWordClass() {
    return wordClass;
  }

  /**
   * Sets the word class.
   *
   * @param wordClass The word class.
   */

  public void setWordClass(String wordClass) {
    this.wordClass = (wordClass != null) ? wordClass : "";
  }

  /**
   * Gets the major word class.
   *
   * @return The major word class.  May be empty.
   */

  public String getMajorWordClass() {
    return majorWordClass;
  }

  /**
   * Sets the major word class.
   *
   * @param majorWordClass The major word class.
   */

  public void setMajorWordClass(String majorWordClass) {
    this.majorWordClass =
            (majorWordClass != null) ? majorWordClass : "";
  }

  /**
   * Gets the general tag name.
   *
   * @return The general tag name.  May be empty.
   */

  public String getGeneralTagName() {
    return generalTagName;
  }

  /**
   * Sets the general tag name.
   *
   * @param generalTagName The general tag name.
   */

  public void setGeneralTagName(String generalTagName) {
    this.generalTagName =
            (generalTagName != null) ? generalTagName : "";
  }

  /**
   * Gets the lemma word class.
   *
   * @return The lemma word class.  May be empty.
   */

  public String getLemmaWordClass() {
    return lemmaWordClass;
  }

  /**
   * Sets the lemma word class.
   *
   * @param lemmaWordClass The lemma word class.
   */

  public void setLemmaWordClass(String lemmaWordClass) {
    this.lemmaWordClass =
            (lemmaWordClass != null) ? lemmaWordClass : "";
  }

  /**
   * Gets a string representation of the part of speech.
   *
   * @return The tag information as a string.
   */

  public String toString() {
    return
            "tag=" + tag +
                    ", word class=" + wordClass +
                    ", major word class=" + majorWordClass +
                    ", general tag name=" + generalTagName +
                    ", description=" + description +
                    ", lemma word class=" + lemmaWordClass;
  }

  /**
   * Returns true if some other object is equal to this one.
   * <p>
   * <p>The two parts of speech are equal if their classes and
   * tags are equal.</p>
   *
   * @param obj The other object.
   * @return True if this object equals the other object.
   */

  public boolean equals(Object obj) {
    if ((obj == null) || !(obj instanceof PartOfSpeech)) {
      return false;
    }

    PartOfSpeech other = (PartOfSpeech) obj;

    return Compare.equals(getClass(), other.getClass()) &&
            Compare.equals(tag, other.getTag());
  }

  /**
   * Compare this object to another.
   *
   * @param object Other object.
   *               <p>
   *               <p>
   *               We only compare the tags.
   *               </p>
   */

  public int compareTo(Object object) {
    int result = Integer.MIN_VALUE;

    if ((object != null) && (object instanceof PartOfSpeech)) {
      result =
              Compare.compare(tag, ((PartOfSpeech) object).getTag());
    }

    return result;
  }

  /**
   * Returns a hash code for the object.
   *
   * @return The hash code.
   */

  public int hashCode() {
    return tag.hashCode();
  }
}
