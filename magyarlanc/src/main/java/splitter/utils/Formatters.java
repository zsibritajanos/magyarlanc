package splitter.utils;

import java.text.NumberFormat;

/**
 * Formatting utilties.
 */

public class Formatters {

  /**
   * Number formatter for numbers with commas.
   */

  static private final NumberFormat COMMA_FORMATTER =
          NumberFormat.getInstance();

  static {
    COMMA_FORMATTER.setGroupingUsed(true);
  }

  /**
   * Formats an integer with commas.
   *
   * @param n The number.
   * @return The formatted number with commas.
   */

  public static String formatIntegerWithCommas(int n) {
    return COMMA_FORMATTER.format(n);
  }

  /**
   * Formats a long with commas.
   *
   * @param n The number.
   * @return The formatted number with commas.
   */

  public static String formatLongWithCommas(long n) {
    return COMMA_FORMATTER.format(n);
  }

  /**
   * Number formatter for floating point numbers.
   */

  static private final NumberFormat FLOAT_FORMATTER =
          NumberFormat.getInstance();

  static {
    FLOAT_FORMATTER.setMinimumIntegerDigits(1);
  }

  /**
   * Formats a float.
   * <p>
   * <p>The formatted number always has a minimum of one digit
   * before the decimal point, and a fixed specified number
   * of digits after the decimal point.
   *
   * @param x The number.
   * @param d Number of digits after the decimal point.
   * @return The formatted number.
   */

  public static String formatFloat(float x, int d) {
    FLOAT_FORMATTER.setMinimumFractionDigits(d);
    FLOAT_FORMATTER.setMaximumFractionDigits(d);
    return FLOAT_FORMATTER.format(x);
  }

  /**
   * Formats a double.
   * <p>
   * <p>The formatted number always has a minimum of one digit
   * before the decimal point, and a fixed specified number
   * of digits after the decimal point.
   *
   * @param x The number.
   * @param d Number of digits after the decimal point.
   * @return The formatted number.
   */

  public static String formatDouble(double x, int d) {
    FLOAT_FORMATTER.setMinimumFractionDigits(d);
    FLOAT_FORMATTER.setMaximumFractionDigits(d);
    return FLOAT_FORMATTER.format(x);
  }

  /**
   * Hides the default no-arg constructor.
   */

  private Formatters() {
    throw new UnsupportedOperationException();
  }
}