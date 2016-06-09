package splitter.ling.partsofspeech;


import java.util.*;

/**
 * PartOfSpeechTags: stores information about a set of part of speech tags.
 * <p>
 * <p>
 * Each entry in the part of speech properties file takes the following form:
 * </p>
 * <p>
 * <blockquote>
 * <p>
 * tagn.name=postag
 * tagn.generalwordclass=general word class
 * tagn.lemmawordclass=lemma word class
 * tagn.majorwordclass=major word class
 * tagn.wordclass=word class
 * tagn.description=extended description
 * </p>
 * </blockquote>
 * <p>
 * <p>
 * where <strong>name</strong> is the part of speech tag,
 * <strong>wordclass</strong> is the word class for the part of speech,
 * <strong>majorwordclass</strong> is the major word class for the
 * part of speech, <strong>lemmawordclass</strong> is the word class
 * for lemmatization purposes, and <strong>generalwordclass</strong>
 * is the associated general word class, if any (see below).  The postag,
 * wordclass, majorWordclass, and lemmawordclass fields must be provided.
 * The generalwordclass and description are optional.
 * </p>
 * <p>
 * <p>
 * The tag properties file must be encoded using the utf-8 character set.
 * </p>
 * <p>
 * <p>
 * Example:  the singular proper noun definition for the NUPOS tag set.
 * </p>
 * <p>
 * <p>
 * <code>
 * tag104.name=np1<br />
 * tag104.generalwordclass=noun-proper-singular<br />
 * tag104.lemmawordclass=none<br />
 * tag104.majorwordclass=noun<br />
 * tag104.wordclass=proper noun<br />
 * </code>
 * </p>
 * <p>
 * <p>
 * The following general word class names allow references to
 * commonly used tags in a tag set independent fashion.  A tag set
 * need not define all of these, but it should define as many as
 * possible.
 * </p>
 * <p>
 * <table>
 * <tr>
 * <th>General tag name</th>
 * <th>Meaning</th>
 * </tr>
 * <p>
 * <tr>
 * <td>
 * adjective
 * </td>
 * <td>
 * Tag for an adjective
 * </td>
 * </tr>
 * <p>
 * <tr>
 * <td>
 * adverb
 * </td>
 * <td>
 * Tag for an adverb
 * </td>
 * </tr>
 * <p>
 * <tr>
 * <td>
 * foreign-word
 * </td>
 * <td>
 * Tag for a foreign word.
 * </td>
 * <p>
 * </tr>
 * <tr>
 * <td>
 * foreign-french
 * </td>
 * <td>
 * Tag for a French word.
 * </td>
 * </tr>
 * <p>
 * <tr>
 * <td>
 * foreign-german
 * </td>
 * <td>
 * Tag for a German word.
 * </td>
 * </tr>
 * <p>
 * <tr>
 * <td>
 * foreign-greek
 * </td>
 * <td>
 * Tag for a Greek word.
 * </td>
 * </tr>
 * <p>
 * <tr>
 * <td>
 * foreign-hebrew
 * </td>
 * <td>
 * Tag for a Hebrew word.
 * </td>
 * </tr>
 * <p>
 * <tr>
 * <td>
 * foreign-italian
 * </td>
 * <td>
 * Tag for an Italian word.
 * </td>
 * </tr>
 * <p>
 * <tr>
 * <td>
 * foreign-spanish
 * </td>
 * <td>
 * Tag for a Spanish word.
 * </td>
 * </tr>
 * <p>
 * <tr>
 * <td>
 * foreign-english
 * </td>
 * <td>
 * Tag for an English word.
 * </td>
 * </tr>
 * <p>
 * <tr>
 * <td>
 * foreign-latin
 * </td>
 * <td>
 * Tag for a Latin word.
 * </td>
 * </tr>
 * <p>
 * <tr>
 * <td>
 * interjection
 * </td>
 * <td>
 * Tag for an interjection.
 * </td>
 * </tr>
 * <p>
 * <tr>
 * <td>
 * noun-singular
 * </td>
 * <td>
 * Tag for a singular noun.
 * </td>
 * </tr>
 * <p>
 * <tr>
 * <td>
 * noun-singular-possessive
 * </td>
 * <td>
 * Tag for a singular possessive noun.
 * </td>
 * </tr>
 * <p>
 * <tr>
 * <td>
 * noun-plural
 * </td>
 * <td>
 * Tag for a plural noun.
 * </td>
 * </tr>
 * <p>
 * <tr>
 * <td>
 * noun-plural-possessive
 * </td>
 * <td>
 * Tag for a plural possessive noun.
 * </td>
 * </tr>
 * <p>
 * <tr>
 * <td>
 * noun-proper-singular
 * </td>
 * <td>
 * Tag for a singular proper noun.
 * </td>
 * </tr>
 * <p>
 * <tr>
 * <td>
 * noun-proper-singular-possessive
 * </td>
 * <td>
 * Tag for a singular proper possessive noun.
 * </td>
 * </tr>
 * <p>
 * <tr>
 * <td>
 * noun-proper-plural
 * </td>
 * <td>
 * Tag for a plural proper noun.
 * </td>
 * </tr>
 * <p>
 * <tr>
 * <td>
 * noun-proper-plural-possessive
 * </td>
 * <td>
 * Tag for a plural proper possessive noun.
 * </td>
 * </tr>
 * <p>
 * <tr>
 * <td>
 * numeral-cardinal
 * </td>
 * <td>
 * Tag for a cardinal number.
 * </td>
 * </tr>
 * <p>
 * <tr>
 * <td>
 * numeral-ordinal
 * </td>
 * <td>
 * Tag for an ordinal number.
 * </td>
 * </tr>
 * <p>
 * <tr>
 * <td>
 * symbol
 * </td>
 * <td>
 * Tag for a symbol.
 * </td>
 * </tr>
 * <p>
 * <tr>
 * <td>
 * undetermined
 * </td>
 * <td>
 * Tag for an undetermined part of speech.
 * </td>
 * </tr>
 * <p>
 * <tr>
 * <td>
 * verb
 * </td>
 * <td>
 * Tag for a verb.
 * </td>
 * </tr>
 * <p>
 * <tr>
 * <td>
 * verb-past
 * </td>
 * <td>
 * Tag for a past verb.
 * </td>
 * </tr>
 * <p>
 * <tr>
 * <td>
 * verb-past-participle
 * </td>
 * <td>
 * Tag for a past participle verb.
 * </td>
 * </tr>
 * <p>
 * <tr>
 * <td>
 * verb-present-participle
 * </td>
 * <td>
 * Tag for a present participle verb.
 * </td>
 * </tr>
 * <p>
 * </table>
 */

public interface PartOfSpeechTags {
  /**
   * General tag names.
   */

  public static final String FOREIGN_WORD =
          "foreign-word";

  public static final String ENGLISH_WORD =
          "foreign-english";

  public static final String FRENCH_WORD =
          "foreign-french";

  public static final String GERMAN_WORD =
          "foreign-german";

  public static final String GREEK_WORD =
          "foreign-greek";

  public static final String ITALIAN_WORD =
          "foreign-italian";

  public static final String HEBREW_WORD =
          "foreign-hebrew";

  public static final String LATIN_WORD =
          "foreign-latin";

  public static final String SPANISH_WORD =
          "foreign-spanish";

  public static final String SINGULAR_NOUN =
          "noun-singular";

  public static final String PLURAL_NOUN =
          "noun-plural";

  public static final String POSSESSIVE_NOUN =
          "noun-possessive";

  public static final String POSSESSIVE_SINGULAR_NOUN =
          "noun-singular-possessive";

  public static final String POSSESSIVE_PLURAL_NOUN =
          "noun-plural-possessive";

  public static final String SINGULAR_PROPER_NOUN =
          "noun-proper-singular";

  public static final String PROPER_NOUN =
          "noun-proper";

  public static final String PLURAL_PROPER_NOUN =
          "noun-proper-plural";

  public static final String POSSESSIVE_SINGULAR_PROPER_NOUN =
          "noun-proper-singular-possessive";

  public static final String POSSESSIVE_PLURAL_PROPER_NOUN =
          "noun-proper-plural-possessive";

  public static final String CARDINAL_NUMERAL =
          "numeral-cardinal";

  public static final String ORDINAL_NUMERAL =
          "numeral-ordinal";

  public static final String ADVERB =
          "adverb";

  public static final String ADJECTIVE =
          "adjective";

  public static final String INTERJECTION =
          "interjection";

  public static final String UNDETERMINED =
          "undetermined";

  public static final String VERB =
          "verb";

  public static final String VERB_PAST =
          "verb-past";

  public static final String VERB_PAST_PARTICIPLE =
          "verb-past-participle";

  public static final String VERB_PRESENT_PARTICIPLE =
          "verb-present-participle";

  public static final String SYMBOL =
          "symbol";

  public static final String PUNCTUATION =
          "punctuation";

  public static final String NONE =
          "none";

  /**
   * Indices for part of speech data values.
   */

  public static final int TAG_INDEX = 0;
  public static final int WORDCLASS_INDEX = 1;
  public static final int MAJOR_WORDCLASS_INDEX = 2;
  public static final int GENERAL_TAG_NAME_INDEX = 3;
  public static final int DESCRIPTION_INDEX = 4;

  /**
   * Add a part of speech tag.
   *
   * @param tag            Tag name.
   * @param wordClass      The word class.
   * @param majorWordClass The major word class.
   * @param lemmaWordClass The lemma word class.
   * @param generalTagName The general tag name.
   * @param description    The description.
   */

  public void addTag
  (
          String tag,
          String wordClass,
          String majorWordClass,
          String lemmaWordClass,
          String generalTagName,
          String description
  );

  /**
   * Add a part of speech.
   *
   * @param partOfSpeech The part of speech to add.
   */

  public void addPartOfSpeech
  (
          PartOfSpeech partOfSpeech
  );

  /**
   * Get the part of speech tag for a singular noun.
   *
   * @return The part of speech tag for a singular noun.
   */

  public String getSingularNounTag();

  /**
   * Get the part of speech tag for a plural noun.
   *
   * @return The part of speech tag for a plural noun.
   */

  public String getPluralNounTag();

  /**
   * Get the part of speech tag for a possessive singular noun.
   *
   * @return The part of speech tag for a possessive singular noun.
   */

  public String getPossessiveSingularNounTag();

  /**
   * Get the part of speech tag for a possessive plural noun.
   *
   * @return The part of speech tag for a possessive plural noun.
   */

  public String getPossessivePluralNounTag();

  /**
   * Get the part of speech tag for a singular proper noun.
   *
   * @return The part of speech tag for a singular proper noun.
   */

  public String getSingularProperNounTag();

  /**
   * Get the part of speech tag for a plural proper noun.
   *
   * @return The part of speech tag for a plural proper noun.
   */

  public String getPluralProperNounTag();

  /**
   * Get the part of speech tag for a possessive singular proper noun.
   *
   * @return The part of speech tag for a possessive singular
   * proper noun.
   */

  public String getPossessiveSingularProperNounTag();

  /**
   * Get the part of speech tag for a possessive plural proper noun.
   *
   * @return The part of speech tag for a possessive plural
   * proper noun.
   */

  public String getPossessivePluralProperNounTag();

  /**
   * Get the part of speech tag for a cardinal number.
   *
   * @return The part of speech tag for a cadinal number.
   */

  public String getCardinalNumberTag();

  /**
   * Get the part of speech tag for an ordinal number.
   *
   * @return The part of speech tag for an ordinal number.
   */

  public String getOrdinalNumberTag();

  /**
   * Get the part of speech tag for an adverb.
   *
   * @return The part of speech tag for an adverb.
   */

  public String getAdverbTag();

  /**
   * Get the part of speech tag for an adjective.
   *
   * @return The part of speech tag for an adjective.
   */

  public String getAdjectiveTag();

  /**
   * Get the part of speech tag for an interjection.
   *
   * @return The part of speech tag for an interjection.
   */

  public String getInterjectionTag();

  /**
   * Get the part of speech tag for a verb.
   *
   * @return The part of speech tag for a verb.
   */

  public String getVerbTag();

  /**
   * Get the part of speech tag for a verb past tense.
   *
   * @return The part of speech tag for a verb past tense.
   */

  public String getVerbPastTag();

  /**
   * Get the part of speech tag for a verbal past participle
   *
   * @return The part of speech tag for a verbal past participle.
   */

  public String getPastParticipleTag();

  /**
   * Get the part of speech tag for a verbal present participle
   *
   * @return The part of speech tag for a verbal present participle.
   */

  public String getPresentParticipleTag();

  /**
   * Get the part of speech tag for a symbol.
   *
   * @return The part of speech tag for a symbol.
   */

  public String getSymbolTag();

  /**
   * Get undetermined part of speech tag.
   *
   * @return Undetermined part of speech tag.
   */

  public String getUndeterminedTag();

  /**
   * Get the part of speech tag for a specified foreign language
   *
   * @param language The foreign language.
   * @return The part of speech tag for the specified foreign language.
   */

  public String getForeignWordTag(String language);

  /**
   * Get the description for the part of speech.
   *
   * @param tag The part of speech tag.
   * @return The description of the part of speech.
   */

  public String getDescription(String tag);

  /**
   * Get word class for a tag.
   *
   * @param tag The part of speech tag.
   * @return The word class for a tag.
   */

  public String getWordClass(String tag);

  /**
   * Get major word class for a tag.
   *
   * @param tag The part of speech tag.
   * @return The major word class for a tag.
   */

  public String getMajorWordClass(String tag);

  /**
   * Get lemma class for a tag.
   *
   * @param tag The part of speech tag.
   * @return The lemma class for a tag.
   */

  public String getLemmaWordClass(String tag);

  /**
   * Convert proper noun tag to common noun tag.
   *
   * @param tag The part of speech tag.
   * @return Input tag, or common noun tag if input is proper noun tag.
   */

  public String getCorrespondingCommonNounTag(String tag);

  /**
   * Is tag for a determiner.
   *
   * @param tag The part of speech tag.
   * @return true if the tag is for a determiner.
   */

  public boolean isDeterminerTag(String tag);

  /**
   * Is tag for a noun.
   *
   * @param tag The part of speech tag.
   * @return true if the tag is for a noun form.
   */

  public boolean isNounTag(String tag);

  /**
   * Is tag for a singular noun.
   *
   * @param tag The part of speech tag.
   * @return true if the tag is for a singular noun form.
   */

  public boolean isSingularNounTag(String tag);

  /**
   * Is tag for a proper noun.
   *
   * @param tag The part of speech tag.
   * @return true if the tag is for a proper noun.
   */

  public boolean isProperNounTag(String tag);

  /**
   * Is tag for a proper adjective.
   *
   * @param tag The part of speech tag.
   * @return true if the tag is for a proper adjective.
   */

  public boolean isProperAdjectiveTag(String tag);

  /**
   * Is tag for a verb.
   *
   * @param tag The part of speech tag.
   * @return true if the tag is for a verb form.
   */

  public boolean isVerbTag(String tag);

  /**
   * Is tag for a pronoun.
   *
   * @param tag The part of speech tag.
   * @return true if the tag is for a pronoun.
   */

  public boolean isPronounTag(String tag);

  /**
   * Is tag for a personal pronoun.
   *
   * @param tag The part of speech tag.
   * @return true if the tag is for a personal pronoun.
   */

  public boolean isPersonalPronounTag(String tag);

  /**
   * Is tag for a foreign word.
   *
   * @param tag The part of speech tag.
   * @return true if the tag is for a foreign word.
   */

  public boolean isForeignWordTag(String tag);

  /**
   * Is tag for a number.
   *
   * @param tag The part of speech tag.
   * @return true if the tag is for a number.
   */

  public boolean isNumberTag(String tag);

  /**
   * Is tag for a symbol.
   *
   * @param tag The part of speech tag.
   * @return true if the tag is for a symbol.
   */

  public boolean isSymbolTag(String tag);

  /**
   * Is tag for punctuation.
   *
   * @param tag The part of speech tag.
   * @return true if the tag is for punctuation.
   */

  public boolean isPunctuationTag(String tag);

  /**
   * Is part of speech tag undetermined.
   *
   * @param tag Tag to check for being undetermined.
   * @return True if given tag is undetermined part of speech tag.
   */

  public boolean isUndeterminedTag(String tag);

  /**
   * Check if specified tag appears in the tag list.
   *
   * @param tag The part of speech tag.
   * @return true if specified tag in the tag list.
   */

  public boolean isTag(String tag);

  /**
   * Check if specified tag contains more than one part of speech.
   *
   * @param tag The part of speech tag.
   * @return true if specified tag contains more than one
   * part of speech tag.
   */

  public boolean isCompoundTag(String tag);

  /**
   * Check if specified tag is an interjection.
   *
   * @param tag The part of speech tag
   * @return true if the tag is for an interjection.
   */

  public boolean isInterjectionTag(String tag);

  /**
   * Get part of speech separator.
   *
   * @return Part of speech separator string.
   */

  public String getTagSeparator();

  /**
   * Join separate tags into a compound tag.
   *
   * @param tags      String array of part of speech tags.
   * @param separator String to separate tags.
   * @return String containing joined tags.
   * The tags are separated by the
   * specified separator character.
   */

  public String joinTags(String[] tags, String separator);

  /**
   * Join separate tags into a compound tag.
   *
   * @param tags String array of part of speech tags.
   * @return String containing joined tags.
   * The tags are separated by the
   * default separator character.
   */

  public String joinTags(String[] tags);

  /**
   * Split compound tag into separate tags.
   *
   * @param tag The part of speech tag.
   * @return String array of tags.  Only one entry if
   * tag is not a compound tag.
   */

  public String[] splitTag(String tag);

  /**
   * Get number of tags comprising this tag.
   *
   * @param tag The part of speech tag.
   * @return Count of individual part of speech tags
   * comprising this tag.
   */

  public int countTags(String tag);

  /**
   * Get data for a tag.
   *
   * @param tag The tag name.
   * @return Tag data.
   */

  public PartOfSpeech getTag(String tag);

  /**
   * Get list of tag entries in PartOfSpeech format.
   *
   * @return List of tag entries in PartOfSpeech format
   * sorted by tag name.
   */

  public List<PartOfSpeech> getTags();
}

