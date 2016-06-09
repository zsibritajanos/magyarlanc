package splitter.utils;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Defines a pattern replacer.
 * <p>
 * <p>
 * Defines a source pattern (regular expresssion) and its replacement string,
 * along with a method for performing the replacement.
 * </p>
 */

public class PatternReplacer implements Serializable {
  /**
   * Source pattern string.
   */

  protected String sourcePattern;

  /**
   * Compiled source pattern matcher.
   */

  protected Matcher sourcePatternMatcher;

  /**
   * Replacement.
   */

  protected String replacementPattern;

  /**
   * Create a pattern replacer definition.
   *
   * @param sourcePattern      Source pattern string as a regular expression.
   * @param replacementPattern Replacement pattern string as a regular expression replacement
   *                           expression.
   */

  public PatternReplacer(String sourcePattern, String replacementPattern) {
    this.sourcePattern = sourcePattern;
    this.replacementPattern = replacementPattern;

    this.sourcePatternMatcher = Pattern.compile(sourcePattern).matcher("");
  }

  /**
   * Return matched groups.
   *
   * @param s String to match.
   * @return String array of matched groups. Null if match fails.
   */

  public String[] matchGroups(String s) {
    String[] result = null;

    if (sourcePatternMatcher.reset(s).find()) {
      int groupCount = sourcePatternMatcher.groupCount();

      result = new String[groupCount + 1];

      for (int i = 0; i <= groupCount; i++) {
        result[i] = sourcePatternMatcher.group(i);
      }
    }

    return result;
  }

  /**
   * Perform replacement.
   *
   * @param s String in which to perform replacement.
   * @return String with source pattern replaced.
   */

  public String replace(String s) {
    return sourcePatternMatcher.reset(s).replaceAll(replacementPattern);
  }

  /**
   * Get pattern matcher.
   *
   * @return The pattern matcher.
   */

  public Matcher getMatcher() {
    return sourcePatternMatcher;
  }

  /**
   * Display pattern replacer as string.
   *
   * @return Pattern replacer as string.
   */

  public String toString() {
    return sourcePattern + " -> " + replacementPattern;
  }
}