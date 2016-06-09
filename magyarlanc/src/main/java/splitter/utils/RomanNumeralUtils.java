package splitter.utils;

import java.util.Map;

/**
 * Roman numeral utilities.
 * <p>
 * <p>
 * This static class provides various utility methods for working with
 * Roman numerals.  The range of values supported is 1 through 5000 .
 * </p>
 */

public class RomanNumeralUtils {
  /**
   * Maximum Roman Numeral value handled here.
   */

  protected static final int MAX_ROMAN_NUMERAL = 5000;

  /**
   * Regular expression pattern matching a Roman numeral.
   */

  protected static String romanNumeralPattern =
          "^\\.{0,1}M{0,3}(C[MD]|D?C{0,3})(X[CL]|L?X{0,3})" +
                  "(I[XV]|V?I{0,3}|V?I{0,2}J)\\.{0,1}$";

  /**
   * Regular expression pattern matching a looser Roman numeral.
   */

//  protected static String looseRomanNumeralPattern    =
//      "^\\.{0,1}M{0,3}(C[MD]|D?C{0,3})(X[CL]|L?X{0,3})" +
//      "(I[XV]|V?I{0,4}|V?I{0,3}J)(O|M|ST|US){0,1}\\.{0,1}$"
//      ;

//  protected static String looseRomanNumeralPattern    =
//      "^\\.{0,1}M{0,3}(C[MD]|D?C{0,3})(X[CL]|L?X{0,3})" +
//      "(I[XVU]|[UV]?I{0,4}|[UV]?I{0,3}J)(O|M|ST|US){0,1}\\.{0,1}$"
//      ;

  protected static String looseRomanNumeralPattern =
          "^\\.{0,1}M{0,3}\\.*(C[MD]|D?C{0,3})\\.*(X[CL]|L?X{0,3})" +
                  "(I[XVU]|[UV]?I{0,4}|[UV]?I{0,3}J)(O|M|ST|US){0,1}\\.{0,1}$";

  /**
   * Ordinal Roman numeral pattern.
   */

  public static String looseOrdinalRomanNumeralPattern =
          "^\\.{0,1}M{0,3}\\.*(C[MD]|D?C{0,3})\\.*(X[CL]|L?X{0,3})" +
                  "(I[XVU]|[UV]?I{0,4}|[UV]?I{0,3}J)" +
                  "(th|TH|st|ST|nd|ND|rd|RD)\\.{0,1}$";

  /**
   * Integer to Roman numerals map for 1 - 5000 .
   */

  protected static Map<Integer, String> integerToRomanMap =
          MapFactory.createNewMap(5000);

  /**
   * Roman numerals to integer map for 1 - 5000 .
   */

  protected static Map<String, Integer> romanToIntegerMap =
          MapFactory.createNewMap(5000);

  /**
   * Roman numeral strings.
   */

  protected static String[] romanNumerals =
          new String[]
                  {
                          "M",
                          "CM",
                          "D",
                          "CD",
                          "C",
                          "XC",
                          "L",
                          "XL",
                          "X",
                          "IX",
                          "V",
                          "IV",
                          "I"
                  };

  /**
   * Integer values for entries in "romanNumerals".
   */

  protected static int[] integerValues =
          new int[]
                  {
                          1000,
                          900,
                          500,
                          400,
                          100,
                          90,
                          50,
                          40,
                          10,
                          9,
                          5,
                          4,
                          1
                  };

  /**
   * See if string is a Roman numeral.
   *
   * @param s The string.
   * @return true if string is a valid Roman numeral.
   */

  public static boolean isRomanNumeral(String s) {
    return s.toUpperCase().matches(romanNumeralPattern);
  }

  /**
   * See if string is a Roman numeral using looser (older) definition.
   *
   * @param s The string.
   * @return true if string is a valid Roman numeral using
   * looser (older) definition.
   */

  public static boolean isLooseRomanNumeral(String s) {
    return s.toUpperCase().matches(looseRomanNumeralPattern);
  }

  /**
   * See if string is an ordinal Roman numeral using looser definition.
   *
   * @param s The string.
   * @return true if string is a valid Roman ordinal numeral
   * using looser (older) definition.
   */

  public static boolean isLooseOrdinalRomanNumeral(String s) {
    return s.toUpperCase().matches(
            looseOrdinalRomanNumeralPattern);
  }

  /**
   * Convert Roman numeral to integer.
   *
   * @param s The Roman numeral as a string.
   * @return The corresponding decimal integer.
   * -1 if Roman numeral string is bad.
   */

  public static int romanNumeralsToInteger(String s) {
    int result = -1;

    if (s == null) return result;

    //  Remove embedded blanks.

    String sFixed = s.toUpperCase().replaceAll(" ", "");

    //  Remove leading and trailing dot.

    int l = sFixed.length();

    if (l > 1) {
      if ((sFixed.charAt(0) == '.') &&
              (sFixed.charAt(l - 1) == '.')) {
        sFixed = sFixed.substring(1, l - 1);
      }
    }
    //  Lookup up Roman numerals string
    //  in map.  If there, return
    //  corresponding integer, else
    //  return -1 .

    Integer i = (Integer) romanToIntegerMap.get(sFixed);

    if (i != null) {
      result = i.intValue();
    }

    return result;
  }

  /**
   * Convert integer to a Roman numeral.
   *
   * @param i The integer to convert to a Roman numeral.
   *          Must be 1 < i <= 5000 .
   * @return The corresponding Roman numeral as a string.
   * "" if input value is invalid.
   */

  public static String integerToRomanNumerals(int i) {
    String result = "";

    //  Lookup up integer value in Roman
    //  numerals map.  If there, return
    //  corresponding Roman numerals string,
    //  else return empty string .

    String s =
            (String) integerToRomanMap.get(new Integer(i));

    if (s != null) {
      result = s;
    }

    return result;
  }

  /**
   * Convert integer to a Roman numeral.
   *
   * @param n The integer to convert to a Roman numeral.
   * @return The corresponding Roman numeral as a string.
   */

  protected static String integerToRoman(int n) {
    StringBuffer result = new StringBuffer();

    int i = 0;
    //  Compute Roman numeral string using
    //  successive substraction of
    //  breakpoint values for Roman numerals.
    while (n > 0) {
      while (n >= integerValues[i]) {
        result.append(romanNumerals[i]);
        n -= integerValues[i];
      }

      i++;
    }

    return result.toString();
  }

  /**
   * Don't allow instantiation, do allow overrides.
   */

  protected RomanNumeralUtils() {
  }

  /** Compute and store Roman numerals and associated integers.
   *
   *  <p>
   *  Only values for 1 through 5000 are computed.
   *  </p>
   */

  static {
    for (int i = 1; i <= MAX_ROMAN_NUMERAL; i++) {
      String romanNumeral = integerToRoman(i);

      romanToIntegerMap.put(romanNumeral, new Integer(i));
      integerToRomanMap.put(new Integer(i), romanNumeral);
    }
  }
}
