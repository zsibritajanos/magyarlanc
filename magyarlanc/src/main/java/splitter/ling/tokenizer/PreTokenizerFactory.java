package splitter.ling.tokenizer;


import splitter.utils.ClassUtils;
import splitter.utils.UTF8Properties;

/**
 * PreTokenizer factory.
 */

public class PreTokenizerFactory {
  /**
   * Get a preTokenizer.
   *
   * @return The preTokenizer.
   */

  public static PreTokenizer newPreTokenizer() {
    String className =
            System.getProperty("pretokenizer.class");

    if (className == null) {
      className = "DefaultPreTokenizer";
    }

    return newPreTokenizer(className);
  }

  /**
   * Get a preTokenizer.
   *
   * @param properties MorphAdorner properties.
   * @return The preTokenizer.
   */

  public static PreTokenizer newPreTokenizer
  (
          UTF8Properties properties
  ) {
    String className = null;

    if (properties != null) {
      className = properties.getProperty("pretokenizer.class");
    }

    if (className == null) {
      className = "DefaultPreTokenizer";
    }

    return newPreTokenizer(className);
  }

  /**
   * Get a preTokenizer of a specified class name.
   *
   * @param className Class name for the preTokenizer.
   * @return The preTokenizer.
   */

  public static PreTokenizer newPreTokenizer(String className) {
    PreTokenizer preTokenizer = null;

    try {
      preTokenizer =
              (PreTokenizer) Class.forName(className).newInstance();
    } catch (Exception e) {
      String fixedClassName =
              ClassUtils.packageName
                      (
                              PreTokenizerFactory.class.getName()
                      ) +
                      "." + className;

      try {
        preTokenizer =
                (PreTokenizer) Class.forName(
                        fixedClassName).newInstance();
      } catch (Exception e2) {
        System.err.println(
                "Unable to create pretokenizer of class " +
                        fixedClassName + ", using default.");

        preTokenizer = new DefaultPreTokenizer();
      }
    }

    return preTokenizer;
  }
}

