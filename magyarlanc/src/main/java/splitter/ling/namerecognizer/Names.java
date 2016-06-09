package splitter.ling.namerecognizer;

import splitter.ling.lexicon.Lexicon;
import splitter.ling.tokenizer.DefaultWordTokenizer;
import splitter.ling.tokenizer.WordTokenizer;
import splitter.utils.MapUtils;
import splitter.utils.SetFactory;
import splitter.utils.SetUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Extract person and place names from text.
 * <p>
 * <p>
 * Uses lists of first names, surnames, and geographic locations
 * to extract person names and locations from text.
 * </p>
 * <p>
 * <p>
 * Based in part on code written by Mark Watson.
 * </p>
 */

public class Names {
  /**
   * Default name resource data files.
   */

  protected static String defaultResourcePath = "resources/";

  /**
   * Surname set.
   */

  protected static Set<String> surnameSet = null;

  /**
   * First name set.
   */

  protected static Set<String> firstNameSet = null;

  /**
   * Place name map.
   */

  protected static Map<String, String> placeNameMap = null;

  /**
   * Prefix title set.
   */

  protected static Set<String> prefixSet = null;

  /**
   * Name connectors set.
   */

  protected static Set<String> connectorsSet = null;

  /**
   * Create name extractor.
   */

  public Names() {
    this(defaultResourcePath);
  }

  /**
   * Create name extractor.
   *
   * @param resourcePath Path to resource files.
   */

  public Names(String resourcePath) {
    //  Load static name data if not
    //  already loaded.

    if (surnameSet != null) return;

    try {
      surnameSet =
              SetUtils.loadSet
                      (
                              Names.class.getResource
                                      (
                                              resourcePath + "lastnames.txt"
                                      ),
                              "utf-8"
                      );

      firstNameSet =
              SetUtils.loadSet
                      (
                              Names.class.getResource
                                      (
                                              resourcePath + "firstnames.txt"
                                      ),
                              "utf-8"
                      );

      placeNameMap =
              MapUtils.loadMap
                      (
                              Names.class.getResource
                                      (
                                              resourcePath + "placenames.txt"
                                      ),
                              "\t",
                              "",
                              "utf-8"
                      );

      prefixSet =
              SetUtils.loadSet
                      (
                              Names.class.getResource
                                      (
                                              resourcePath + "prefixes.txt"
                                      ),
                              "utf-8"
                      );

      connectorsSet =
              SetUtils.loadSet
                      (
                              Names.class.getResource
                                      (
                                              resourcePath + "connectors.txt"
                                      ),
                              "utf-8"
                      );
    } catch (Exception e) {
//          e.printStackTrace();
    }
  }

  /**
   * See if string is a name or a place.
   *
   * @param s The string to check.
   * @return true if the string is a name or a place.
   */

  public boolean isNameOrPlace(String s) {
    return
            firstNameSet.contains(s) ||
                    surnameSet.contains(s) ||
                    placeNameMap.containsKey(s);
  }

  /**
   * Accept a name.
   *
   * @param lexicon   Word lexicon.
   * @param name      The text of the name.
   * @param firstWord True if the name starts with the first word
   *                  in a sentence.
   * @param numWords  The number of words in the name.
   * @return true if the name should be accepted as such.
   */

  protected boolean acceptName
  (
          Lexicon lexicon,
          String name,
          boolean firstWord,
          int numWords
  ) {
    boolean result = (name.length() > 0);

    if (result && firstWord && (numWords == 1)) {
      if (lexicon != null) {
        String lowerCaseName = name.toLowerCase().trim();

        result = !lexicon.containsEntry(lowerCaseName);
      }
    }

    return result;
  }

  /**
   * Extract all proper names for people and places from a list of words.
   *
   * @param words   String array of words to search for names.
   *                This should correspond to a single sentence.
   * @param lexicon Lexicon for filtering names.
   * @return Two element array containing two sets.
   * First set is a list of person names.
   * Second set is a list of place names.
   */

  public Set<String>[] getProperNames(String[] words, Lexicon lexicon) {
    //  Create set to hold person names.

    Set<String> personNames = SetFactory.createNewSet();

    //  Create set to hold place names.

    Set<String> placeNames = SetFactory.createNewSet();

    //  If no words, return empty name sets.

    if ((words == null) || (words.length == 0)) {
      @SuppressWarnings("unchecked")
      Set<String>[] result = (Set<String>[]) new Set[2];

      result[0] = personNames;
      result[1] = placeNames;

      return result;
    }
    //  Loop over word list and look
    //  for person and place name patterns.
    int i = 0;

    while (i < words.length) {
      for (int j = 5; j > 0; j--) {
        //  Look for a place name.

        String name;

        if (j <= 3) {
          name = getPlaceName(words, i, j);

          //  We found a name.  Add it to the
          //  list of place names and move past
          //  the name to look for more.

          if ((name.length() > 0) &&
                  acceptName(lexicon, name, (i == 0), j)
                  ) {
            placeNames.add(name);
            i += j - 1;
            break;
          }
        }
        //  Look for a person name.

        name = getPersonName(words, i, j);

        //  We found a name.  Add it to the
        //  list of proper names and move past
        //  the name to look for more.

        if ((name.length() > 0) &&
                acceptName(lexicon, name, (i == 0), j)
                ) {
          personNames.add(name);
          i += j - 1;
          break;
        }
      }

      i++;
    }

    @SuppressWarnings("unchecked")
    Set<String>[] result = (Set<String>[]) new Set[2];

    result[0] = personNames;
    result[1] = placeNames;

    return result;
  }

  /**
   * Extract all proper names for people and places from a sstring.
   *
   * @param s       String to search for names.
   *                This should correspond to a single sentence.
   * @param lexicon Lexicon for filtering names.
   * @return Two element array containing two sets.
   * First set is a list of person names.
   * Second set is a list of place names.
   */

  public Set<String>[] getProperNames(String s, Lexicon lexicon) {
    //  Get a word tokenizer.

    WordTokenizer wordTokenizer = new DefaultWordTokenizer();

    //  Extract list of words from
    //  the input string using the
    //  tokenizer.

    List<String> wordsList = wordTokenizer.extractWords(s);

    //  Convert list of words to
    //  string array of words.

    String[] words =
            (String[]) wordsList.toArray(new String[wordsList.size()]);

    //  Get names from list of words.

    return getProperNames(words, lexicon);
  }

  /**
   * Extract all proper names for people and places from a sstring.
   *
   * @param wordsList List of words to search for names.
   *                  This should correspond to a single sentence.
   * @param lexicon   Lexicon for filtering names.
   * @return Two element array containing two sets.
   * First set is a list of person names.
   * Second set is a list of place names.
   */

  public Set<String>[] getProperNames
  (
          List<String> wordsList,
          Lexicon lexicon
  ) {
    //  string array of words.

    String[] words =
            (String[]) wordsList.toArray(new String[wordsList.size()]);

    //  Get names from list of words.

    return getProperNames(words, lexicon);
  }

  /**
   * Get a place name from a list of words.
   *
   * @param words      String array of words.
   * @param startIndex Start index in words array to check for a name.
   * @param numWords   The number of words to check for a name.
   * @return The place name, if found, or an empty string
   * if not found.
   */

  public String getPlaceName
  (
          String[] words,
          int startIndex,
          int numWords
  ) {
    //  Assume we don't find a name.

    String result = "";

    //  If starting index plus the
    //  number of words to look at runs
    //  past the number of words, we can't
    //  extract a name of the specified
    //  length.

    if ((startIndex + numWords) > words.length) {
      return result;
    }
    //  Concatenate words to form the
    //  potential place name.

    StringBuffer sb = new StringBuffer();

    int endIndex = startIndex + numWords - 1;

    for (int i = startIndex; i <= endIndex; i++) {
      sb.append(words[startIndex]);

      if (i < endIndex) {
        sb.append(" ");
      }
    }
    //  If the concatenated words
    //  form a place name, return that
    //  name, otherwise return an
    //  empty string.

    String s = sb.toString();

    if (isPlaceName(s)) {
      result = s;
    }

    return result.trim();
  }

  /**
   * Get place name type.
   *
   * @param placeName The place name.
   * @return Place name type, or empty string if none.
   */

  public String getPlaceNameType(String placeName) {
    String result = (String) placeNameMap.get(placeName);

    if (result == null) {
      result = "";
    }

    return result.trim();
  }

  /**
   * Check if name is a place name.
   *
   * @param name The name.
   * @return true if it is a place name.
   */

  public boolean isPlaceName(String name) {
    //  See if the name is in the map
    //  of place names.

    return (placeNameMap.get(name) != null);
  }

  /**
   * Check if word is a name prefix (Mr., Mrs., etc.).
   *
   * @param word The word to check.
   * @return true if it is a name prefix.
   */

  public boolean isNamePrefix(String word) {
    //  See if the word is in the map
    //  of name prefixes.

    return ((word != null) && prefixSet.contains(word));
  }

  /**
   * Check if string is a person name.
   *
   * @param s The string.
   * @return true if input string is a person name.
   */

  public boolean isPersonName(String s) {
    //  Get a word tokenizer.

    WordTokenizer wordTokenizer = new DefaultWordTokenizer();

    //  Extract list of words from
    //  the input string using the
    //  tokenizer.

    List<String> wordsList = wordTokenizer.extractWords(s);

    //  Convert list of words to
    //  string array of words.
    String[] words =
            (String[]) wordsList.toArray(new String[wordsList.size()]);

    //  Get names from list of words.

    return isPersonName(words);
  }

  /**
   * Get a person name from a list of words.
   *
   * @param words      String array of words.
   * @param startIndex Start index in words array to check for a name.
   * @param numWords   The number of words to check for a name.
   * @return The person name, if found, or an empty string
   * if not found.
   */

  public String getPersonName
  (
          String[] words,
          int startIndex,
          int numWords
  ) {
    //  Assume we don't find a name.

    String result = "";
    //  If starting index plus the
    //  number of words to look at runs
    //  past the number of words, we can't
    //  extract a name of the specified
    //  length.

    if ((startIndex + numWords) > words.length) {
      return result;
    }
    //  Copy the words forming a potential
    //  name to a new string array.

    String[] sWords = new String[numWords];

    int endIndex = startIndex + numWords - 1;
    int j = 0;

    for (int i = startIndex; i <= endIndex; i++) {
      sWords[j++] = words[i];
    }
    //  If the words form a person name,
    //  create a name string from the words.

    if (isPersonName(sWords)) {
      StringBuffer sb = new StringBuffer();

      for (int i = 0; i < sWords.length; i++) {
        sb.append(sWords[i]);

        if (i < sWords.length) {
          sb.append(" ");
        }
      }

      result = sb.toString();
    }

    return result.trim();
  }

  /**
   * Check if list of words form a person's name.
   *
   * @param words The words.
   * @return true if words form a person's name.
   */

  public boolean isPersonName(String[] words) {
    //  Assume words do not form a name.

    boolean result = false;

    //  Perform different checks depending
    //  upon number of words.

    switch (words.length) {
      case 1:
        result =
                firstNameSet.contains(words[0]) ||
                        surnameSet.contains(words[0])
        ;
        break;

      case 2:
        result =
                firstNameSet.contains(words[0]) &&
                        surnameSet.contains(words[1]);

        result =
                result ||
                        (
                                prefixSet.contains(words[0]) &&
                                        surnameSet.contains(words[1])
                        );
        break;

      case 3:
        result =
                (firstNameSet.contains(words[0])) &&
                        (firstNameSet.contains(words[1])) &&
                        (surnameSet.contains(words[2]));

        result =
                result ||
                        (prefixSet.contains(words[0])) &&
                                (firstNameSet.contains(words[1])) &&
                                (surnameSet.contains(words[2]));

        result =
                result ||
                        (firstNameSet.contains(words[0])) &&
                                (connectorsSet.contains(words[1])) &&
                                (surnameSet.contains(words[2]));

        result =
                result ||
                        (firstNameSet.contains(words[0])) &&
                                (words[1].length() == 2) &&
                                (words[1].endsWith(".")) &&
                                (surnameSet.contains(words[2]));

        break;

      case 4:
        result =
                (firstNameSet.contains(words[0])) &&
                        (firstNameSet.contains(words[1])) &&
                        (firstNameSet.contains(words[2])) &&
                        (surnameSet.contains(words[3]));

        result =
                result ||
                        (prefixSet.contains(words[0])) &&
                                (firstNameSet.contains(words[1])) &&
                                (firstNameSet.contains(words[2])) &&
                                (surnameSet.contains(words[3]));

        result =
                result ||
                        (prefixSet.contains(words[0])) &&
                                (firstNameSet.contains(words[1])) &&
                                (words[2].length() == 1) &&
                                (surnameSet.contains(words[3]));

        result =
                (firstNameSet.contains(words[0])) &&
                        (firstNameSet.contains(words[1])) &&
                        (words[2].length() == 2) &&
                        (words[2].endsWith(".")) &&
                        (surnameSet.contains(words[3]));

        result =
                result ||
                        (prefixSet.contains(words[0])) &&
                                (firstNameSet.contains(words[1])) &&
                                (words[2].length() == 2) &&
                                (words[2].endsWith(".")) &&
                                (surnameSet.contains(words[3]));

        result =
                result ||
                        (firstNameSet.contains(words[0])) &&
                                (firstNameSet.contains(words[1])) &&
                                (firstNameSet.contains(words[2])) &&
                                (surnameSet.contains(words[3]));

        result =
                result ||
                        (firstNameSet.contains(words[0])) &&
                                (connectorsSet.contains(words[1])) &&
                                (connectorsSet.contains(words[2])) &&
                                (surnameSet.contains(words[3]));

        break;

      case 5:
        result =
                result ||
                        (prefixSet.contains(words[0])) &&
                                (firstNameSet.contains(words[1])) &&
                                (connectorsSet.contains(words[2])) &&
                                (connectorsSet.contains(words[3])) &&
                                (surnameSet.contains(words[4]));

        break;

      default:
        break;
    }

    return result;
  }

  /**
   * Return first name set.
   *
   * @return First name set.
   */

  public Set<String> getFirstNames() {
    return firstNameSet;
  }

  /**
   * Return last name set.
   *
   * @return Last name set.
   */

  public Set<String> getSurnames() {
    return surnameSet;
  }

  /**
   * Return place name set.
   *
   * @return Place name set.
   */

  public Map<String, String> getPlaceNames() {
    return placeNameMap;
  }

  /**
   * Return prefix title set.
   *
   * @return Prefix title set.
   */

  public Set<String> getPrefixes() {
    return prefixSet;
  }

  /**
   * Return name connectors set.
   *
   * @return Name connectors set.
   */

  public Set<String> getConnectors() {
    return connectorsSet;
  }
}

