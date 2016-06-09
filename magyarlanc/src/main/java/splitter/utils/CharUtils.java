package splitter.utils;

import java.text.Normalizer;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Character utilities.
 * <p>
 * <p>
 * This class provides various static utility methods for manipulating
 * characters.
 * </p>
 */

public class CharUtils {
  /**
   * Set of punctuation values.
   */

  protected static Set<Integer> punctuationSet =
          SetFactory.createNewSet();

  /**
   * Set of symbol values.
   */

  protected static Set<Integer> symbolSet =
          SetFactory.createNewSet();

  /**
   * Set of superscript characters.
   */

  protected static Set<Integer> superscriptSet =
          SetFactory.createNewSet();

  /**
   * Set of subscript characters.
   */

  protected static Set<Integer> subscriptSet =
          SetFactory.createNewSet();

  /**
   * Paragraph.
   */

  public static final char PARAGRAPH = '\u00b6';
  public static final String PARAGRAPH_STRING = "\u00b6";

  /**
   * Section sign.
   */

  public static final char SECTION_SIGN = '\u00a7';
  public static final String SECTION_SIGN_STRING = "\u00a7";

  /**
   * Left single curly quote.
   */

  public static final char LSQUOTE = '\u2018';
  public static final String LSQUOTE_STRING = "\u2018";

  /**
   * Right single curly quote.
   */

  public static final char RSQUOTE = '\u2019';
  public static final String RSQUOTE_STRING = "\u2019";

  /**
   * Right low single curly quote.
   */

  public static final char RLSQUOTE = '\u201A';
  public static final String RLSQUOTE_STRING = "\u201A";

  /**
   * Left double curly quote.
   */

  public static final char LDQUOTE = '\u201C';
  public static final String LDQUOTE_STRING = "\u201C";

  /**
   * Right double curly quote.
   */

  public static final char RDQUOTE = '\u201D';
  public static final String RDQUOTE_STRING = "\u201D";

  /**
   * Unicode filled-in circle/dot.
   */

  public static final char SOLIDCIRCLE = '\u2022';
  public static final String SOLIDCIRCLE_STRING = "\u2022";

  /**
   * Unicode black circle/dot.
   */

  public static final char BLACKCIRCLE = '\u25CF';
  public static final String BLACKCIRCLE_STRING = "\u25CF";

  /**
   * Degrees/Hours.
   */

  public static final char DEGREES_MARK = '\u2032';
  public static final String DEGREES_MARK_STRING = "\u2032";

  public static final char MINUTES_MARK = '\u2033';
  public static final char SECONDS_MARK = '\u2034';

  /**
   * Gap marker inside words.
   */

  public static final char CHAR_GAP_MARKER = BLACKCIRCLE;

  /**
   * Gap marker inside words as string.
   */

  public static final String CHAR_GAP_MARKER_STRING =
          CHAR_GAP_MARKER + "";

  /**
   * End of text section marker character.
   */

  public static final char CHAR_END_OF_TEXT_SECTION =
          '\ue500';

  public static final String CHAR_END_OF_TEXT_SECTION_STRING =
          "\ue500";

  /**
   * Substitute soft hyphen marker.
   */

  public static final char CHAR_FAKE_SOFT_HYPHEN =
          '\ue501';

  public static final String CHAR_FAKE_SOFT_HYPHEN_STRING =
          "\ue501";

  /**
   * Substitute single quote character.
   */

  public static final char CHAR_SUBSTITUTE_SINGLE_QUOTE =
          '\ue502';

  public static final String CHAR_SUBSTITUTE_SINGLE_QUOTE_STRING =
          "\ue502";

  /**
   * <sup> text marker.
   */

  public static final char CHAR_SUP_TEXT_MARKER =
          '\ue503';

  public static final String CHAR_SUP_TEXT_MARKER_STRING =
          "\ue503";

  /**
   * Long dash.
   */

  public static final char LONG_DASH = '\u2014';

  /**
   * Long dash string.
   */

  public static final String LONG_DASH_STRING = LONG_DASH + "";

  /**
   * Long dash.
   */

  public static final char SHORT_DASH = '\u2010';

  /**
   * Short dash string.
   */

  public static final String SHORT_DASH_STRING = SHORT_DASH + "";

  /**
   * Old Euro symbol.
   */

  public static final char OLD_EURO_SIGN = '\u20A0';

  /**
   * New Euro symbol.
   */

  public static final char EURO_SIGN = '\u20AC';

  /**
   * Unknown punctuation marker.
   */

  public static final char UNKNOWN_PUNC = '\u25AA';

  /**
   * Combining macron character.
   */

  public static final char COMBINING_MACRON = '\u0304';

  /**
   * Combining macron string.
   */

  public static final String COMBINING_MACRON_STRING = "\u0304";

  /**
   * Nonbreaking blank character.
   */

  public static final char NONBREAKING_BLANK = '\u00A0';

  /**
   * Nonbreaking blank string.
   */

  public static final String NONBREAKING_BLANK_STRING = "\u00A0";

  /**
   * Nonbreaking hyphen character.
   */

  public static final char NONBREAKING_HYPHEN = '\u2011';

  /**
   * Nonbreaking hyphen string.
   */

  public static final String NONBREAKING_HYPHEN_STRING = "\u2011";

  /**
   * Vertical bar.
   */

  public static final char VERTICAL_BAR = '\u007C';

  /**
   * Vertical bar string.
   */

  public static final String VERTICAL_BAR_STRING = "\u007C";

  /**
   * Broken vertical bar.
   */

  public static final char BROKEN_VERTICAL_BAR = '\u00A6';

  /**
   * Broken vertical bar string.
   */

  public static final String BROKEN_VERTICAL_BAR_STRING = "\u00A6";

  /**
   * Light vertical bar.
   */

  public static final char LIGHT_VERTICAL_BAR = '\u2758';

  /**
   * Light vertical bar string.
   */

  public static final String LIGHT_VERTICAL_BAR_STRING = "\u2758";

  /**
   * Divider vertical bar.
   */

  public static final char DIVIDER_VERTICAL_BAR = '\u2223';

  /**
   * Divider vertical bar string.
   */

  public static final String DIVIDER_VERTICAL_BAR_STRING = "\u2223";

  /**
   * Ellipsis (...) character.
   */

  public static final char ELLIPSIS = '\u2026';
  public static final String ELLIPSIS_STRING = "\u2026";

  /**
   * Solar circle character.
   */

  public static final char SOLAR_CIRCLE = '\u2299';
  public static final String SOLAR_CIRCLE_STRING = "\u2299";

  /**
   * Unicode dotted circle.
   */

  public static final char DOTTEDCIRCLE = '\u25CC';
  public static final String DOTTEDCIRCLE_STRING = "\u25CC";

  /**
   * Unicode left angle bracket.
   */

  public static final char LEFT_ANGLE_BRACKET = '\u3008';
  public static final String LEFT_ANGLE_BRACKET_STRING = "\u3008";

  /**
   * Unicode right angle bracket.
   */

  public static final char RIGHT_ANGLE_BRACKET = '\u3009';
  public static final String RIGHT_ANGLE_BRACKET_STRING = "\u3009";

  /**
   * Unicode lozenge.
   */

  public static final char LOZENGE = '\u25CA';
  public static final String LOZENGE_STRING = "\u25CA";

  /**
   * Unicode asterism.
   */

  public static final char ASTERISM = '\u2042';
  public static final String ASTERISM_STRING = "\u2042";

  /**
   * Unicode dagger.
   */

  public static final char DAGGER = '\u2020';
  public static final String DAGGER_STRING = "\u2020";

  /**
   * Unicode double dagger.
   */

  public static final char DOUBLE_DAGGER = '\u2021';
  public static final String DOUBLE_DAGGER_STRING = "\u2021";

  /**
   * Unicode invisible separator.
   */

  public static final char INVISIBLE_SEPARATOR =
          '\u2063';

  public static final String INVISIBLE_SEPARATOR_STRING =
          "\u2063";

  /**
   * Span gap marker.
   */

  public static final String SPAN_GAP_MARKER =
          CharUtils.LEFT_ANGLE_BRACKET_STRING +
                  CharUtils.ELLIPSIS_STRING +
                  CharUtils.RIGHT_ANGLE_BRACKET_STRING;

  /**
   * Long s.
   */

  public static final char LONG_S = '\u017f';
  public static final String LONG_S_STRING = "\u017f";

  /**
   * Digits pattern.
   */

  public static String digitsPattern = "[0-9]*";

  /**
   * Ordinal number pattern (English only!)
   */

  public static String ordinalNumberPattern =
          "[0-9][0-9,]*(th|TH|st|ST|nd|ND|rd|RD)";

  /**
   * Pattern for 1 or more hyphens.
   */

  protected final static Pattern hyphenPattern =
          Pattern.compile("^([-\u2011]{1,})\u0024");

  protected final static Matcher hyphenMatcher =
          hyphenPattern.matcher("");

  /**
   * Pattern for 1 or more asterisks.
   */

  protected final static Pattern asteriskPattern =
          Pattern.compile("^(\\*{1,})\u0024");

  protected final static Matcher asteriskMatcher =
          asteriskPattern.matcher("");

  /**
   * Pattern for 1 or more asterisks followed by 's.
   */

  protected final static Pattern possessiveAsteriskPattern =
          Pattern.compile("^(\\*{1,})'(s|S)\u0024");

  protected final static Matcher possessiveAsteriskMatcher =
          possessiveAsteriskPattern.matcher("");

  /**
   * Pattern for 2 or more dashes followed by 's.
   */

  protected final static Pattern possessiveDashesPattern =
          Pattern.compile("^([-\u2011]{2,})'(s|S)\u0024");

  protected final static Matcher possessiveDashesMatcher =
          possessiveDashesPattern.matcher("");

  /**
   * Pattern for Unicode word.
   */

  protected final static Pattern wordPattern =
          Pattern.compile
                  (
                          "([\\w]+|([\\w]+-[\\w]+)+|[\\w]*'[\\w]*)"
                  );

  protected final static Matcher wordMatcher =
          wordPattern.matcher("");

  /**
   * Pattern for Unicode letters only.
   */

  protected final static Pattern lettersPattern =
          Pattern.compile("^[\\pL\\pN]+\u0024");

  protected final static Matcher lettersMatcher =
          lettersPattern.matcher("");

  /**
   * Pattern for punctuation only.
   */

  protected final static Pattern punctuationPattern =
          Pattern.compile("^[\\p{Punct}]+\u0024|[\\p{P}]+\u0024");

  protected final static Matcher punctuationMatcher =
          punctuationPattern.matcher("");

  /**
   * Check if character is a letter.
   * <p>
   * \u0040param c   Character to test.
   * <p>
   * \u0040return        true if character is a letter.
   */

  public static boolean isLetter(char c) {
    return Character.isLetter(c);
  }

  /**
   * Check if string is a single letter.
   * <p>
   * \u0040param s   String to test.
   * <p>
   * \u0040return        true if string is a single letter.
   */

  public static boolean isLetter(String s) {
    return
            (s != null) && (s.length() == 1) &&
                    Character.isLetter(s.charAt(0));
  }

  /**
   * Check if string contains only letters.
   * <p>
   * \u0040param s   String to test.
   * <p>
   * \u0040return        true if string contains only letters.
   */

  public static boolean isLetters(String s) {
    boolean result = false;

    if (s != null) {
/*
            result  = true;

            String ts   = s.trim();

            for ( int i = 0 ; i < ts.length() ; i++ )
            {
                if ( !isLetter( ts.charAt( i ) ) )
                {
                    result  = false;
                    break;
                }
            }
*/
      lettersMatcher.reset(s.trim());

      result = lettersMatcher.matches();
    }

    return result;
  }

  /**
   * Check if a string is a word (Unicode letters, digits, hyphen).
   * <p>
   * \u0040param s   String to test.
   * <p>
   * \u0040return        true if string is a Unicode word.
   */

  public static boolean isAWord(String s) {
    boolean result = false;

    if ((s != null) && !s.equals("'")) {
      wordMatcher.reset(s);

      result = wordMatcher.matches();
    }

    return result;
  }

  /**
   * Check if character is a capital letter.
   * <p>
   * \u0040param c   Character to test.
   * <p>
   * \u0040return        true if character is a capital letter.
   */

  public static boolean isCapitalLetter(char c) {
    return Character.isLetter(c) && Character.isUpperCase(c);
  }

  /**
   * True if character is punctuation.
   */

  public static boolean isPunctuation(char ch) {
    return
            punctuationSet.contains
                    (
                            new Integer(Character.getType(ch))
                    ) ||
                    (ch == '\u0060') ||
                    (ch == UNKNOWN_PUNC);
  }

  /**
   * True if all characters in a string are punctuation.
   * <p>
   * \u0040param s   String to check for punctuation.
   * <p>
   * \u0040return        true if all characters are punctuation,
   * including dashes.
   */

  public static boolean isPunctuation(String s) {
/*
        boolean result  = true;

        String ts   = s.trim();

        for ( int i = 0 ; i < ts.length() ; i++ )
        {
            if ( !isPunctuation( ts.charAt( i ) ) )
            {
                result  = false;
                break;
            }
        }
*/
    boolean result = false;

    if (s != null) {
      punctuationMatcher.reset(s.trim());

      result = punctuationMatcher.matches();
    }

    return result;
  }

  /**
   * True if character is symbol.
   */

  public static boolean isSymbol(char ch) {
    return
            symbolSet.contains(new Integer(Character.getType(ch)));
  }

  /**
   * True if all characters in a string are symbols.
   * <p>
   * \u0040param s   String to check for symbols.
   * <p>
   * \u0040return        true if all characters are symbols.
   */

  public static boolean isSymbol(String s) {
    boolean result = true;

    String ts = s.trim();

    for (int i = 0; i < ts.length(); i++) {
      if (!isSymbol(ts.charAt(i))) {
        result = false;
        break;
      }
    }

    return result;
  }

  /**
   * True if character is punctuation or symbol.
   * <p>
   * \u0040param c   Character to check for punctuation or symbol.
   * <p>
   * \u0040return        true if character is punctuation or symbol.
   */

  public static boolean isPunctuationOrSymbol(char c) {
    return isPunctuation(c) || isSymbol(c);
  }

  /**
   * True if all characters in a string are punctuation or symbols.
   * <p>
   * \u0040param s   String to check for punctuation and symbols.
   * <p>
   * \u0040return        true if all characters are punctuation or symbols.
   */

  public static boolean isPunctuationOrSymbol(String s) {
    boolean result = true;

    String ts = s.trim();

    for (int i = 0; i < ts.length(); i++) {
      if (!isPunctuationOrSymbol(ts.charAt(i))) {
        result = false;
        break;
      }
    }

    return result;
  }

  /**
   * True if character is a digit.
   * <p>
   * \u0040param c   Character to check for being a digit.
   * <p>
   * \u0040return        true if character is a digit from 0 through 9.
   */

  public static boolean isDigit(char c) {
    return Character.isDigit(c);
  }

  /**
   * True if all characters in a string are digits.
   * <p>
   * \u0040param s   String to check for digits.
   * <p>
   * \u0040return        true if all characters are digits 0 through 9.
   */

  public static boolean isDigits(String s) {
    if (s == null) return false;

    boolean result = true;

    String ts = s.trim();

    for (int i = 0; i < ts.length(); i++) {
      if (!isDigit(s.charAt(i))) {
        result = false;
        break;
      }
    }

    return result;
  }

  /**
   * True if at least one character in a string is a digit.
   * <p>
   * \u0040param s   String to check for digits.
   * <p>
   * \u0040return        true if at least one character is a digit 0 through 9.
   */

  public static boolean hasDigit(String s) {
    if (s == null) return false;

    boolean result = false;

    String ts = s.trim();

    for (int i = 0; i < ts.length(); i++) {
      if (isDigit(s.charAt(i))) {
        result = true;
        break;
      }
    }

    return result;
  }

  /**
   * True if character is a dash of some kind.
   * <p>
   * \u0040param c   Character to check for being a dash.
   * <p>
   * \u0040return        true if character is a dash.
   */

  public static boolean isDash(char c) {
    return
            Character.getType(c) == Character.DASH_PUNCTUATION;
  }

  /**
   * True if character is a breaking dash of some kind.
   * <p>
   * \u0040param c   Character to check for being a breaking dash.
   * <p>
   * \u0040return        true if character is a breaking dash.
   */

  public static boolean isBreakingDash(char c) {
    return isDash(c) &&
            (c != NONBREAKING_HYPHEN) &&
            (c != LONG_DASH);
  }

  /**
   * True if string contains a dash of some kind.
   * <p>
   * \u0040param s   String to check for containing a dash.
   * <p>
   * \u0040return        true if string contains a dash.
   */

  public static boolean hasDash(String s) {
    boolean result = false;

    for (int i = 0; i < s.length(); i++) {
      result =
              (Character.getType(s.charAt(i)) ==
                      Character.DASH_PUNCTUATION);

      if (result) break;
    }

    return result;
  }

  /**
   * Evict dashes from a string.
   * <p>
   * \u0040param s   String from which to evict dashes.
   * <p>
   * \u0040return        String with dashes evicted.
   */

  public static String evictDashes(String s) {
    StringBuffer result = new StringBuffer();

    for (int i = 0; i < s.length(); i++) {
      if (Character.getType(s.charAt(i)) !=
              Character.DASH_PUNCTUATION) {
        result.append(s.charAt(i));
      }
    }

    return result.toString();
  }

  /**
   * True if a string is a number.
   * <p>
   * \u0040param s   String to check for being a number.
   * <p>
   * \u0040return        true if string is a number.
   */

  public static boolean isNumber(String s) {
    boolean result = false;

    if (s != null) {
      try {
        double x = Double.parseDouble(s);
        result = true;
      } catch (Exception e) {
      }
    }

    return result;
  }

  /**
   * True if a string is an ordinal number.
   * <p>
   * \u0040param s   String to check for being an ordinal number.
   * <p>
   * \u0040return        true if string is an ordinal number.
   */

  public static boolean isOrdinal(String s) {
    boolean result = false;

    if (s != null) {
      result = s.matches(ordinalNumberPattern);
    }

    return result;
  }

  /**
   * True if all letters in a string are uppercase.
   * <p>
   * \u0040param s   String to check for upper case letters.
   * <p>
   * \u0040return        true if all letters are upper case.
   * <p>
   * <p>
   * Note: non-letters are ignored.  The result is false if there
   * are no letters in the string.
   * </p>
   */

  public static boolean allLettersCapital(String s) {
    boolean result = false;
    String ts = s.trim();
    int l = ts.length();

    for (int i = 0; i < l; i++) {
      char ch = ts.charAt(i);

      if (Character.isLetter(ch)) {
        if (!Character.isUpperCase(ch)) {
          result = false;
          break;
        } else {
          result = true;
        }
      }
    }

    return result;
  }

  /**
   * True if all characters in a string are uppercase.
   * <p>
   * \u0040param s   String to check for upper case letters.
   * <p>
   * \u0040return        true if all characters are upper case.
   * <p>
   * <p>
   * All characters are checked, letters and non-letters alike.
   * </p>
   */

  public static boolean isUpperCase(String s) {
    boolean result = true;

    String ts = s.trim();

    for (int i = 0; i < ts.length(); i++) {
      if (!Character.isUpperCase(ts.charAt(i))) {
        result = false;
        break;
      }
    }

    return result;
  }

  /**
   * True if first letter in a string is uppercase.
   * <p>
   * \u0040param s   String to check for initial uppercase letter.
   * <p>
   * \u0040return        true if first letter in string is uppercase.
   * <p>
   * <p>
   * Leading non-letters are ignored.  If none of the characters
   * in the string is a letters, false is returned.
   * </p>
   */

  public static boolean isFirstLetterCapital(String s) {
    boolean result = false;

    String ts = s.trim();

    for (int i = 0; i < ts.length(); i++) {
      if (Character.isLetter(ts.charAt(i))) {
        result = Character.isUpperCase(ts.charAt(i));
        break;
      }
    }

    return result;
  }

  /**
   * True if any characters in a string are punctuation.
   * <p>
   * \u0040param s   String to check for punctuation.
   * <p>
   * \u0040return        true if any characters are punctuation, except
   * that dashes are allowed.
   */

  public static boolean hasPunctuation(String s) {
    boolean result = false;

    String ts = s.trim();

    for (int i = 0; i < ts.length(); i++) {
      char ch = ts.charAt(i);

      if (isPunctuation(ch) && (ch != '-')) {
        result = true;
        break;
      }
    }

    return result;
  }

  /**
   * True if any characters in a string are punctuation.
   * <p>
   * \u0040param s   String to check for punctuation.
   * <p>
   * \u0040return        true if any characters are punctuation, except
   * that dashes and apostrophes are allowed.
   */

  public static boolean hasPunctuationNotApostrophes(String s) {
    boolean result = false;

    String ts = s.trim();

    for (int i = 0; i < ts.length(); i++) {
      char ch = ts.charAt(i);

      if (isPunctuation(ch) && (ch != '-') && (ch != '\'')) {
        result = true;
        break;
      }
    }

    return result;
  }

  /**
   * True if any characters in a string are symbols.
   * <p>
   * \u0040param s   String to check for symbols.
   * <p>
   * \u0040return        true if any characters are symbols.
   */

  public static boolean hasSymbols(String s) {
    boolean result = false;

    String ts = s.trim();

    for (int i = 0; i < ts.length(); i++) {
      char ch = ts.charAt(i);

      if (isSymbol(ch)) {
        result = true;
        break;
      }
    }

    return result;
  }

  /**
   * True if character is a gap marker.
   * <p>
   * \u0040param c   Character to check for being a gap marker.
   * <p>
   * \u0040return        true if character is a gap marker.
   */

  public static boolean isGapMarker(char c) {
    return (c == CHAR_GAP_MARKER);
  }

  /**
   * True if any characters in a string are gap markers.
   * <p>
   * \u0040param s   String to check for gap markers.
   * <p>
   * \u0040return        true if any characters are gap markers.
   */

  public static boolean hasGapMarkers(String s) {
    boolean result = false;

    String ts = s.trim();

    for (int i = 0; i < ts.length(); i++) {
      char ch = ts.charAt(i);

      if (isGapMarker(ch)) {
        result = true;
        break;
      }
    }

    return result;
  }

  /**
   * True if character is a Greek letter.
   * <p>
   * \u0040param c   Character to check for being a Greek letter.
   * <p>
   * \u0040return        true if character is a Greek letter.
   */

  public static boolean isGreekLetter(char c) {
    return
            (
                    ((c >= 0x0370) && (c < 0x0400)) ||
                            ((c >= 0x1f00) && (c < 0x2000))
            );
  }

  /**
   * True if any characters in a string are Greek letters.
   * <p>
   * \u0040param s   String to check for Greek letters.
   * <p>
   * \u0040return        true if any characters are Greek letters.
   */

  public static boolean hasGreekLetters(String s) {
    boolean result = false;

    String ts = s.trim();

    for (int i = 0; i < ts.length(); i++) {
      char ch = ts.charAt(i);

      if (isGreekLetter(ch)) {
        result = true;
        break;
      }
    }

    return result;
  }

  /**
   * Is character an English vowel?
   * <p>
   * \u0040param c   Character to check.
   * <p>
   * \u0040return        true if "c" is a, e, i, o, u.
   */

  public static boolean isEnglishVowel(char c) {
    return
            (c == 'a') ||
                    (c == 'e') ||
                    (c == 'i') ||
                    (c == 'o') ||
                    (c == 'u');
  }

  /**
   * True if character is single quote.
   * <p>
   * \u0040param c   Character to check for being a single quote.
   * <p>
   * \u0040return        true if character is a single quote.
   */

  public static boolean isSingleQuote(char c) {
    return (c == '\'') || (c == LSQUOTE) || (c == RSQUOTE);
  }

  /**
   * True if character is an apostrophe.
   * <p>
   * \u0040param c   Character to check for being an apostrophe.
   * <p>
   * \u0040return        true if character is an apostrophe.
   */

  public static boolean isApostrophe(char c) {
    return (c == '\'');
  }

  /**
   * True if character is a single opening quote.
   * <p>
   * \u0040param c   Character to check for being a single opening quote.
   * <p>
   * \u0040return        true if character is a single opening quote.
   */

  public static boolean isSingleOpeningQuote(char c) {
    return (c == '\'') || (c == LSQUOTE);
  }

  /**
   * True if string ends with "single quote + s".
   * <p>
   * \u0040param s   String to check for ending with single quote + s.
   * <p>
   * \u0040return        true if token ends with single quote + s.
   */

  public static boolean endsWithSingleQuoteS(String s) {
    int l = s.length();

    return
            (l > 1) &&
                    ((s.charAt(l - 1) == 's') ||
                            (s.charAt(l - 1) == 'S')
                    ) &&
                    isSingleQuote(s.charAt(l - 2));
  }

  /**
   * True if string is "single quote + s".
   * <p>
   * \u0040param s   String to check for being single quote + s.
   * <p>
   * \u0040return        true if token is single quote + s.
   */

  public static boolean isSingleQuoteS(String s) {
    int l = s.length();

    return
            (l == 2) &&
                    isSingleQuote(s.charAt(0)) &&
                    ((s.charAt(1) == 's') ||
                            (s.charAt(1) == 'S')
                    );
  }

  /**
   * True if string ends with single quote.
   * <p>
   * \u0040param s   String to check for ending with single quote.
   * <p>
   * \u0040return        true if token ends with single quote.
   */

  public static boolean endsWithSingleQuote(String s) {
    int l = s.length();

    return
            (l > 0) &&
                    isSingleQuote(s.charAt(l - 1));
  }

  /**
   * True if character is any kind of opening quote.
   * <p>
   * \u0040param c   Character to check for being an opening quote.
   * <p>
   * \u0040return        true if character is an opening quote.
   */

  public static boolean isOpeningQuote(char c) {
    return (c == '\'') || (c == LSQUOTE) || (c == LDQUOTE);
  }

  /**
   * True if character is any kind of closing quote.
   * <p>
   * \u0040param c   Character to check for being a closing quote.
   * <p>
   * \u0040return        true if character is a closing quote.
   */

  public static boolean isClosingQuote(char c) {
    return
            (c == '\'') || (c == '\"') || (c == RSQUOTE) ||
                    (c == RDQUOTE);
  }

  /**
   * True if string contains a single quote.
   * <p>
   * \u0040param s   String to check for containing a single quote.
   * <p>
   * \u0040return        true if string contains a single quote.
   */

  public static boolean hasSingleQuote(String s) {
    boolean result = false;

    for (int i = 0; i < s.length(); i++) {
      result = result || isSingleQuote(s.charAt(i));

      if (result) break;
    }

    return result;
  }

  /**
   * True if string is all caps.
   * <p>
   * \u0040param s   String to check for being all capitals.
   * <p>
   * \u0040return        True if string is all capitals.
   */

  public static boolean isAllCaps(String s) {
    boolean result = true;

    for (int i = 0; i < s.length(); i++) {
      char ch = s.charAt(i);

      if (Character.isLetter(ch)) {
        result = result && Character.isUpperCase(ch);
        if (!result) break;
      }
    }

    return result;
  }

  /**
   * True if string is all lower case.
   * <p>
   * \u0040param s   String to check for being all lower case.
   * <p>
   * \u0040return        True if string is all lower case.
   */

  public static boolean isAllLowerCase(String s) {
    boolean result = true;

    for (int i = 0; i < s.length(); i++) {
      char ch = s.charAt(i);

      if (Character.isLetter(ch)) {
        result = result && Character.isLowerCase(ch);
        if (!result) break;
      }
    }

    return result;
  }

  /**
   * True if string contains at least one capital letter.
   * <p>
   * \u0040param s   String to check for having a capital letter.
   * <p>
   * \u0040return        True if string has a capital letter.
   */

  public static boolean hasCapitalLetter(String s) {
    boolean result = false;

    for (int i = 0; i < s.length(); i++) {
      char ch = s.charAt(i);

      result =
              result ||
                      Character.isLetter(ch) && Character.isUpperCase(ch);

      if (result) break;
    }

    return result;
  }

  /**
   * True if string contains at least one apostrophe.
   * <p>
   * \u0040param s   String to check for having an apostrophe.
   * <p>
   * \u0040return        True if string has an apostrophe.
   */

  public static boolean hasApostrophe(String s) {
    return (s.indexOf("'") >= 0);
  }

  /**
   * True if string contains at least one period.
   * <p>
   * \u0040param s   String to check for having a period.
   * <p>
   * \u0040return        True if string has a period.
   */

  public static boolean hasPeriod(String s) {
    return (s.indexOf(".") >= 0);
  }

  /**
   * True if string contains at least one gap marker character.
   * <p>
   * \u0040param s   String to check for having a gap marker character.
   * <p>
   * \u0040return        True if string has a gap marker.
   */

  public static boolean hasGapCharacter(String s) {
    return (s.indexOf(CHAR_GAP_MARKER) >= 0);
  }

  /**
   * True if string contains internal capital letters.
   * <p>
   * \u0040param s   String to check for having internal capitals.
   * <p>
   * \u0040return        True if string has internal capitals.
   */

  public static boolean hasInternalCaps(String s) {
    boolean result = false;

    for (int i = 1; i < s.length(); i++) {
      char ch = s.charAt(i);

      result =
              result ||
                      Character.isLetter(ch) && Character.isUpperCase(ch);

      if (result) break;
    }

    return result;
  }

  /**
   * True if string is all periods.
   * <p>
   * \u0040param s   String to check for being all periods.
   * <p>
   * \u0040return        True if string is all periods.
   */

  public static boolean isAllPeriods(String s) {
    return s.matches("^(\\.{1,})\u0024");
  }

  /**
   * True if string is all hyphens.
   * <p>
   * \u0040param s   String to check for being all hyphens.
   * <p>
   * \u0040return        True if string is all hyphens.
   */

  public synchronized static boolean isAllHyphens(String s) {
    hyphenMatcher.reset(s);

    return hyphenMatcher.matches();
  }

  /**
   * True if string is all asterisks.
   * <p>
   * \u0040param s   String to check for being all asterisks.
   * <p>
   * \u0040return        True if string is all asterisks.
   */

  public synchronized static boolean isAllAsterisks(String s) {
    asteriskMatcher.reset(s);

    return asteriskMatcher.matches();
  }

  /**
   * True if string is asterisks followed by 's.
   * <p>
   * \u0040param s   String to check for being asterisks followed by 's.
   * <p>
   * \u0040return        True if string is asterisks followed by 's.
   */

  public synchronized static boolean isPossessiveAsterisks(String s) {
    possessiveAsteriskMatcher.reset(s);

    return possessiveAsteriskMatcher.matches();
  }

  /**
   * True if string is two or more dashes followed by 's.
   * <p>
   * \u0040param s   String to check for being dashes followed by 's.
   * <p>
   * \u0040return        True if string is dashes followed by 's.
   */

  public synchronized static boolean isPossessiveDashes(String s) {
    possessiveDashesMatcher.reset(s);

    return possessiveDashesMatcher.matches();
  }

  /**
   * True if character is whitespace.
   * <p>
   * \u0040param c   Character to check for being whitespace.
   * <p>
   * \u0040return        True if character c is whitespace.
   */

  public static boolean isWhitespace(char c) {
    return Character.isWhitespace(c);
  }

  /**
   * Make case of string match another string's case.
   * <p>
   * \u0040param s               String whose case should be changed.
   * \u0040param sCaseToMatch    String whose case should be matched.
   * <p>
   * \u0040return                    "s" modified to match case of "sCaseToMatch".
   */

  public static String makeCaseMatch(String s, String sCaseToMatch) {
    String result = s;

    if (isAllCaps(sCaseToMatch)) {
      result = result.toUpperCase();
    } else {
      boolean isCapitalized = false;

      if (sCaseToMatch.length() > 0) {
        if (CharUtils.isSingleQuote(sCaseToMatch.charAt(0))) {
          isCapitalized =
                  (sCaseToMatch.length() > 1) &&
                          CharUtils.isCapitalLetter(
                                  sCaseToMatch.charAt(1));
        } else {
          isCapitalized =
                  CharUtils.isCapitalLetter(
                          sCaseToMatch.charAt(0));
        }
      }

      if (result.length() > 0) {
        if (CharUtils.isSingleQuote(result.charAt(0))) {
          String char0 = result.charAt(0) + "";
          String rest = "";

          if (result.length() > 1) {
            char char1 = result.charAt(1);

            if (result.length() > 2) {
              rest = result.substring(2);
            }

            if (isCapitalized) {
              result =
                      char0 +
                              Character.toUpperCase(char1) +
                              rest;
            } else {
              result =
                      char0 +
                              Character.toLowerCase(char1) +
                              rest;
            }
          }
        } else {
          String rest = "";
          char char0 = result.charAt(0);

          if (result.length() > 1) {
            rest = result.substring(1);
          }

          if (isCapitalized) {
            result = Character.toUpperCase(char0) + rest;
          } else {
            result = Character.toLowerCase(char0) + rest;
          }
        }
      }
    }

    return result;
  }

  /**
   * Capitalize first letter in string.
   * <p>
   * \u0040param s   String to capitalize.
   * <p>
   * \u0040return        "s" with first letter (not first character) capitalized.
   * Remaining characters are set to lower case.
   */

  public static String capitalizeFirstLetter(String s) {
    char[] chars = s.toLowerCase().toCharArray();

    for (int i = 0; i < chars.length; i++) {
      char ch = chars[i];

      if (Character.isLetter(ch)) {
        chars[i] = Character.toUpperCase(ch);
        break;
      }
    }

    return new String(chars);
  }

  /**
   * Check for US currency.
   * <p>
   * \u0040param token   Word to check for currency.
   * <p>
   * \u0040return            true if token is US currency.
   */

  public static boolean isUSCurrency(String token) {
    return
            token.matches
                    (
                            "^\\s*[\u0024]?\\s*((\\d+)|(\\d{1,3}(\\,\\d{3})+))(\\.\\d{2})?\\s*\u0024"
//              "(?n:(^\\\u0024?(?!0,?\\d)\\d{1,3}(?=(?<1>,)|(?<1>))(\\k<1>\\d{3})*(\\.\\d\\d)?)\u0024)"
                    );
  }

  /**
   * Check for US currency with cents.
   * <p>
   * \u0040param token   Word to check for currency.
   * <p>
   * \u0040return            true if token is US currency.
   */

  public static boolean isUSCurrencyCents(String token) {
    return token.matches("([\\+\\-]*)([0-9\\,]+)\u00a2\u0024");
  }

  /**
   * Check for currency.
   * <p>
   * \u0040param token   Word to check for currency.
   * <p>
   * \u0040return            true if token is currency.
   */

  public static boolean isCurrency(String token) {
    return token.matches
            (
                    "^-?[\u00a3\\\u0024L" + CharUtils.EURO_SIGN + " ]*[0-9\\.\\,]+[lL]*\u0024"
            );
  }

  /**
   * Get case value for a string.
   * <p>
   * \u0040param s   The string.
   * <p>
   * \u0040return        Case value.
   * "0" = all lower case
   * "1" = first letter is upper case
   * "2" = first character not upper case, but
   * some characters after first are
   * upper case.
   * "3" = all characters are upper case
   */

  public static String getCaseOld(String s) {
    String result = "0";

    if (CharUtils.allLettersCapital(s)) {
      result = "3";
    } else if (CharUtils.isFirstLetterCapital(s)) {
      result = "1";
    } else if (CharUtils.hasInternalCaps(s)) {
      result = "2";
    }

    return result;
  }

  /**
   * Get case value for a string.
   * <p>
   * \u0040param s   The string.
   * <p>
   * \u0040return        Case value.
   * 0   = all letters are lower case
   * 1   = first letter (only) is upper case
   * 2   = first letter is not upper case, but
   * some letters after first are upper case
   * 3   = all letters are upper case
   */

  public static int getLetterCase(String s) {
    int result = 0;

    int capLetCount = 0;
    int letCount = 0;

    for (int i = 0; i < s.length(); i++) {
      char ch = s.charAt(i);

      if (Character.isLetter(ch)) {
        letCount++;

        if (Character.isUpperCase(ch)) {
          if (letCount == 1) result = 1;
          capLetCount++;
        }
      }
    }

    if (letCount == capLetCount) {
      result = 3;
    } else if ((result == 0) && (capLetCount > 0)) {
      result = 2;
    }

    return result;
  }

  /**
   * Convert super/subscript characters to normal equivalents.
   * <p>
   * \u0040param s   String containing super/subscript characters.
   * <p>
   * \u0040return        String with super/subscript characters converted
   * to normal equivalents.
   */

  public static String normalizeString(String s) {
    return
            (s == null) ?
                    null :
                    Normalizer.normalize(s, Normalizer.Form.NFKD)
                            .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
  }

  /**
   * Is character a superscript character.
   * <p>
   * \u0040param ch  Character to check.
   * <p>
   * \u0040return        true if superscript character, false otherwise.
   */

  public static boolean isSuperscript(char ch) {
    return superscriptSet.contains((int) ch);
  }

  /**
   * Is character a subscript character.
   * <p>
   * \u0040param ch  Character to check.
   * <p>
   * \u0040return        true if subscript character, false otherwise.
   */

  public static boolean isSubscript(char ch) {
    return subscriptSet.contains((int) ch);
  }

  /**
   * Don't allow instantiation, do allow overrides.
   */

  protected CharUtils() {
  }

  /** Static initializer. */

  static {
    punctuationSet.add(
            new Integer(Character.CONNECTOR_PUNCTUATION));
    punctuationSet.add(
            new Integer(Character.DASH_PUNCTUATION));
    punctuationSet.add(
            new Integer(Character.ENCLOSING_MARK));
    punctuationSet.add(
            new Integer(Character.END_PUNCTUATION));
    punctuationSet.add(
            new Integer(Character.FINAL_QUOTE_PUNCTUATION));
    punctuationSet.add(
            new Integer(Character.INITIAL_QUOTE_PUNCTUATION));
    punctuationSet.add(
            new Integer(Character.OTHER_PUNCTUATION));
    punctuationSet.add(
            new Integer(Character.PARAGRAPH_SEPARATOR));
    punctuationSet.add(
            new Integer(Character.START_PUNCTUATION));

    symbolSet.add(
            new Integer(Character.CURRENCY_SYMBOL));
    symbolSet.add(
            new Integer(Character.MATH_SYMBOL));
    symbolSet.add(
            new Integer(Character.MODIFIER_SYMBOL));
    symbolSet.add(
            new Integer(Character.OTHER_SYMBOL));
    symbolSet.add(
            new Integer(Character.PRIVATE_USE));

    for (int i = 0x2070; i <= 0x2079; i++) {
      superscriptSet.add(i);
    }

    for (int i = 0x2080; i <= 0x2089; i++) {
      subscriptSet.add(i);
    }
  }
}