package splitter.ling.abbreviations;

import splitter.utils.StringUtils;
import splitter.utils.UTF8Properties;
import splitter.utils.UTF8PropertyUtils;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Abbreviation lists and pattern matchers.
 * <p>
 * <p>
 * Holds a list of common abbreviations along with information about whether
 * each abbreviation can normally end a sentence or not. Also provides patterns
 * and methods for determining if a string is a possible abbreviation.
 * </p>
 */

public class Abbreviations {
  // Default abbreviation pattern.

  public static String defaultAbbreviationPattern = "([A-Za-z]\\.([A-Za-z0-9]\\.)+|[A-Z]\\.|[A-Z][bcdfghj-np-tvxz]+\\.)";
  // "([A-Za-z]\\.([A-Za-z0-9]\\.)+|[A-Z]\\.|[A-Z][a-z\\-]+\\.)";

  // Compiled regular expression to match
  // an abbreviation.

  protected Pattern abbreviationPattern = Pattern.compile(
          // "([A-Z,a-z]\\.([A-Z,a-z,0-9]\\.)*)|([A-Z][bcdfghj-np-tvxz]+\\.)"
          "^" + defaultAbbreviationPattern + "$");
  // Abbreviation pattern matcher.

  protected Matcher abbreviationMatcher = abbreviationPattern.matcher("");

  // Compiled regular expression to
  // match an initial.

  protected Pattern initialPattern = Pattern.compile("[A-Z][.]");

  // Initial pattern matcher.

  protected Matcher initialMatcher = initialPattern.matcher("");

  // Compiled regular expression to
  // match a possessive initial.

  protected Pattern possessiveInitialPattern = Pattern
          .compile("[A-Z][.]'[s|S]");

  // Initial pattern matcher.

  protected Matcher possessiveInitialMatcher = possessiveInitialPattern
          .matcher("");

  // Defined abbreviations.

  protected UTF8Properties abbreviations = null;

  // Path to abbreviations list resource.

  protected final static String defaultAbbreviationsFileName = "resources/en-abbrevs.properties";

  /**
   * Create abbreviation detector. Assumes English.
   */

  public Abbreviations() {
    abbreviations = loadAbbreviationsFromResource("en");
  }

  /**
   * Create abbreviation detector for specified ISO language code.
   *
   * @param langCode ISO language code.
   */

  public Abbreviations(String langCode) {
    abbreviations = loadAbbreviationsFromResource(langCode);
  }

  /**
   * Load abbreviations list from resource properties file.
   * <p>
   * <p>
   * Each line in the UTF8 abbreviations property file takes the form:
   * </p>
   * <p>
   * <p>
   * <code>abbrev.=n</code>
   * </p>
   * <p>
   * <p>
   * where a value of 1 for n indicates the abbreviation can normally end a
   * sentence and a value of 0 for n indicates the abbreviation normally cannot
   * end a sentence.
   * </p>
   * <p>
   * <p>
   * if there is not a resource file for the given language code, the
   * abbreviations list will be empty.
   * </p>
   */

  public static UTF8Properties loadAbbreviationsFromResource(String langCode) {
    // Create properties object to
    // hold abbreviations.

    UTF8Properties result = new UTF8Properties();

    // Load abbreviations from resource file.
    try {


      result.load(Abbreviations.class.getClassLoader().getResourceAsStream("hu.abbrevs"));
      //+ langCode + "-abbrevs.properties"));
    } catch (IOException ioe) {
      ioe.printStackTrace();
    }

    //System.out.println(result);
    return result;
  }

  /**
   * Load abbreviations list from a properties file.
   *
   * @param abbreviationsURL Abbreviations URL.
   * @return true if abbreviations loaded OK, false if error occurred.
   * <p>
   * <p>
   * Each line in the UTF8 abbreviations property file takes the form:
   * </p>
   * <p>
   * <p>
   * <code>abbrev.=n</code>
   * </p>
   * <p>
   * <p>
   * where a value of 1 for n indicates the abbreviation can normally
   * end a sentence and a value of 0 for n indicates the abbreviation
   * normally cannot end a sentence.
   * </p>
   */

  public boolean loadAbbreviations(String abbreviationsURL) {
    boolean result = false;

    // Create properties object to
    // hold abbreviations if not
    // already created.

    if (abbreviations == null) {
      abbreviations = new UTF8Properties();
    }
    // Load abbreviations from file.
    try {
      UTF8Properties newAbbreviations = UTF8PropertyUtils
              .loadUTF8Properties(new URL(abbreviationsURL));

      for (Enumeration enumeration = newAbbreviations.propertyNames(); enumeration
              .hasMoreElements(); ) {
        String key = (String) enumeration.nextElement();
        abbreviations.put(key, newAbbreviations.getProperty(key));
      }

      result = true;
    } catch (IOException ioe) {
      // ioe.printStackTrace();
    }

    return result;
  }

  /**
   * Checks if string is a known abbreviation.
   *
   * @param str The string to check.
   * @return true if "str" is on the known abbreviations list.
   */

  public boolean isKnownAbbreviation(String str) {
    return abbreviations.containsKey(str);
  }

  /**
   * Checks if string is a probable abbreviation.
   *
   * @param str The string to check.
   * @return true if "str" is probably an abbreviation .
   * <p>
   * <p>
   * A string is declared to be a probable abbreviation if if appears in
   * the abbreviation list or matches the abbreviation pattern.
   * </p>
   */

  public boolean isAbbreviation(String str) {
    boolean result = isKnownAbbreviation(str);

    if (!result) {
      abbreviationMatcher.reset(str);

      result = abbreviationMatcher.matches();
    }

    return result;
  }

  /**
   * Checks if string is an abbreviation on which a sentence can end.
   *
   * @param str The string to check.
   * @return true if "str" is an possible sentence-ending abbreviation .
   * <p>
   * <p>
   * A string is declared to be a probable sentence-ending abbreviation
   * if it appears in the abbreviation list and it has a sentence-ending
   * value of 1.
   * </p>
   */

  public boolean isEOSAbbreviation(String str) {
    return abbreviations.getProperty(str, "0").equals("1");
  }

  /**
   * Checks if string is an initial.
   *
   * @param str The string to check.
   * @return true if "str" is an abbreviation .
   * <p>
   * <p>
   * A string is an initial when it takes the form "L." where L is a
   * capital letter.
   * </p>
   */

  public boolean isInitial(String str) {
    initialMatcher.reset(str);

    return initialMatcher.matches();
  }

  /**
   * Checks if string is a possible possessive initial.
   *
   * @param str The string to check.
   * @return true if "str" is a possible possessive initial .
   * <p>
   * <p>
   * A string is an possible possessive initial when it takes the form
   * "L.'s" where L is a capital letter.
   * </p>
   */

  public boolean isPossessiveInitial(String str) {
    possessiveInitialMatcher.reset(str);

    return possessiveInitialMatcher.matches();
  }

  /**
   * Get count of known abbreviations.
   *
   * @return Count of known abbreviations.
   */

  public int getAbbreviationsCount() {
    int result = 0;

    if (abbreviations != null) {
      result = abbreviations.size();
    }

    return result;
  }

  /**
   * Return current abbreviations.
   *
   * @return Abbreviations.
   */

  public UTF8Properties getAbbreviations() {
    return abbreviations;
  }

  /**
   * Create abbreviations pattern from list of abbreviations.
   *
   * @param abbreviations Abbreviations for which to create pattern.
   * @return Pattern to match input abbreviations.
   */

  public static String createAbbreviationsPattern(UTF8Properties abbreviations) {
    String result = defaultAbbreviationPattern;

    if ((abbreviations != null) && (abbreviations.size() > 0)) {
      StringBuffer sb = new StringBuffer();

      sb.append("(");

      int i = 0;

      for (Enumeration enumeration = abbreviations.propertyNames(); enumeration
              .hasMoreElements(); ) {
        String abbrev = (String) enumeration.nextElement();

        if (i++ > 0) {
          sb.append("|");
        }

        abbrev = StringUtils.replaceAll(abbrev, ".", "\\.");

        sb.append(abbrev);
      }

      sb.append(")");

      result = sb.toString();
    }

    return result;
  }

  public static void main(String[] args) {
    System.out.println(loadAbbreviationsFromResource("hu"));
  }
}
