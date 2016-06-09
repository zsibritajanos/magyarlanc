package splitter.ling.tokenizer;


import splitter.utils.CharUtils;
import splitter.utils.IsCloseableObject;
import splitter.utils.PatternReplacer;
import splitter.utils.StringUtils;
import splitter.utils.logger.DummyLogger;
import splitter.utils.logger.Logger;
import splitter.utils.logger.UsesLogger;

/**
 * Default pretokenizes which prepares a string for tokenization.
 */

abstract public class AbstractPreTokenizer extends IsCloseableObject implements
        PreTokenizer, UsesLogger {
  /**
   * Pattern to match three or more periods.
   */

  protected String periods = "(\\.{3,})";

  /**
   * Pattern to match one or more asterisk.
   */

  protected String asterisks = "([\\*]+)";

  /**
   * Pattern to match two or more hyphens in a row.
   */

  protected String hyphens = "(-{2,})";

  /**
   * Pattern to match comma as a separator.
   */

  protected String commaSeparator = "(,)([^0-9])";

  /**
   * Logger used for output.
   */

  protected Logger logger;

  /**
   * Pattern to match characters which are always separators.
   * <p>
   * <p>
   * Unicode \u25CF (BLACKCIRCLE) is the dot character which marks character
   * lacunae. This is not a token separator. Neither is Unicode \u2022
   * (SOLIDCIRCLE) which was used in the old EEBO format TCP files to mark
   * character lacunae.
   * </p>
   * <p>
   * <p>
   * Unicode \u2011, the non-breaking hyphen, is not treated as a token
   * separator.
   * </p>
   * <p>
   * <p>
   * Unicode \u2032 (DEGREES_MARK) is degrees quote symbol. Unicode \u2033
   * (MINUTES_MARK) is minutes quote symbol. Unicode \u2034 (SECONDS_MARK) is
   * seconds quote symbol. These are not token separators.
   * </p>
   * <p>
   * <p>
   * Unicode \u2018 (LSQUOTE) is left single curly quote. Unicode \u2019
   * (RSQUOTE) is right single curly quote. These may or may not be token
   * separators. It is up to the word tokenizer to decide.
   * </p>
   * <p>
   * <p>
   * Unicode \u201C (LDQUOTE) is left double curly quote. Unicode \u201D
   * (RDQUOTE) is right double curly quote. These are token separators.
   * </p>
   */

  protected final String alwaysSeparators = "(" + hyphens + "|" + periods + "|"
          + "[\\(\\)\\[\\]\\{\\}\";:/=\u0060\u00b6<>\u00a1\u00bf\u00ab\u00bb_"
          + CharUtils.LDQUOTE + CharUtils.RDQUOTE + CharUtils.LONG_DASH + "\\"
          + CharUtils.VERTICAL_BAR + CharUtils.BROKEN_VERTICAL_BAR
          + CharUtils.LIGHT_VERTICAL_BAR + "[\\p{InGeneralPunctuation}&&[^"
          + CharUtils.SOLIDCIRCLE + CharUtils.DEGREES_MARK + CharUtils.MINUTES_MARK
          + CharUtils.SECONDS_MARK + CharUtils.LSQUOTE + CharUtils.RSQUOTE
          + CharUtils.SHORT_DASH + CharUtils.NONBREAKING_HYPHEN + "]]"
          + "\\p{InLetterlikeSymbols}" + "\\p{InMathematicalOperators}"
          + "\\p{InMiscellaneousTechnical}" + "[\\p{InGeometricShapes}&&[^"
          + CharUtils.BLACKCIRCLE + "]]" + "\\p{InMiscellaneousSymbols}"
          + "\\p{InDingbats}" + "\\p{InAlphabeticPresentationForms}" + "]" + ")";

  /**
   * Always Separators replacer pattern.
   */

  protected PatternReplacer alwaysSeparatorsReplacer = new PatternReplacer(
          alwaysSeparators, " \u00241 ");

  /**
   * Comma separator replacer pattern.
   */

  protected PatternReplacer commaSeparatorReplacer = new PatternReplacer(
          commaSeparator, " \u00241 \u00242");

  /**
   * Create a preTokenizer.
   */

  public AbstractPreTokenizer() {
    logger = new DummyLogger();
  }

  /**
   * Get the logger.
   * <p>
   * \u0040return The logger.
   */

  public Logger getLogger() {
    return logger;
  }

  /**
   * Set the logger.
   * <p>
   * \u0040param logger
   * The logger.
   */

  public void setLogger(Logger logger) {
    this.logger = logger;
  }

  /**
   * Prepare text for tokenization.
   * <p>
   * \u0040param line
   * The text to prepare for tokenization,
   * <p>
   * \u0040return The pretokenized text.
   */

  public String pretokenize(String line) {
    // Replace tabs with single space.

    String result = StringUtils.replaceAll(line, "\t", " ");

    // Put spaces around characters
    // that are always separators.

    result = alwaysSeparatorsReplacer.replace(result);

    // Put spaces around all commas except
    // those appearing before a digit, which
    // presumably are part of a number.

    result = commaSeparatorReplacer.replace(result);

    return result;
  }
}
