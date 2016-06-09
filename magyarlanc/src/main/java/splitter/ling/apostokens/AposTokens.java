package splitter.ling.apostokens;

import splitter.utils.SetFactory;
import splitter.utils.SetUtils;

import java.io.IOException;
import java.util.Set;

/**
 * Tokens which start or end with an apostrophe.
 */

public class AposTokens {
  //  Set of tokens which begin or end
  //  with an apostrophe.

  protected Set<String> aposTokens = null;

  //  Path to apos tokens list resource.

  protected final static String defaultAposTokensFileName =
          "resources/en-apostokens.txt";

  /**
   * Create AposTokens.  Assumes English.
   */

  public AposTokens() {
    aposTokens = loadAposTokensFromResource("en");
  }

  /**
   * Create AposTokens for specified ISO language code.
   *
   * @param langCode ISO language code.
   */

  public AposTokens(String langCode) {
    aposTokens = loadAposTokensFromResource(langCode);
  }

  /**
   * Load apostrophe tokens set from resource properties file.
   * <p>
   * <p>
   * Each line in the UTF8 apostrophe tokens contains a single
   * tokens which begins or ends with an apostrophe.
   * <p>
   * <p>
   * if there is not a resource file for the given language code,
   * the aposTokens set will be empty.
   * </p>
   */

  public static Set<String> loadAposTokensFromResource
  (
          String langCode
  ) {
    //  Create properties object to
    //  hold apostophe tokens.

    Set<String> result = SetFactory.createNewSet();

    //  Load apostrophe tokens from
    //  resource file.

    try {
      SetUtils.loadIntoSet
              (
                      result,
                      AposTokens.class.getResource
                              (
                                      "resources/" + langCode + "-apostokens.txt"
                              ),
                      "utf-8"
              );
    } catch (IOException ioe) {
//          ioe.printStackTrace();
    }

    return result;
  }

  /**
   * Load aposTokens list from a file.
   *
   * @param aposTokensURL AposTokens URL.
   * @return true if aposTokens loaded OK,
   * false if error occurred.
   */

  public boolean loadAposTokens(String aposTokensURL) {
    boolean result = false;

    //  Create properties object to
    //  hold aposTokens if not
    //  already created.

    if (aposTokens == null) {
      aposTokens = SetFactory.createNewSet();
    }
    //  Load aposTokens from file.
    try {
      aposTokens = SetUtils.loadSet(aposTokensURL, "utf-8");

      result = true;
    } catch (IOException ioe) {
//          ioe.printStackTrace();
    }

    return result;
  }

  /**
   * Checks if string is a known token with apostrophe.
   *
   * @param str The string to check.
   * @return true if "str" is on the known apostrophe tokens list.
   */

  public boolean isKnownAposToken(String str) {
    return aposTokens.contains(str.toLowerCase());
  }

  /**
   * Get count of known aposrophe Tokens.
   *
   * @return Count of known apostrophe tokens.
   */

  public int getAposTokensCount() {
    int result = 0;

    if (aposTokens != null) {
      result = aposTokens.size();
    }

    return result;
  }

  /**
   * Return current aposTokens.
   *
   * @return AposTokens.
   */

  public Set<String> getAposTokens() {
    return aposTokens;
  }
}

