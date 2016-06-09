package splitter.utils;

/**
 * Class utilities.
 * <p>
 * <p>
 * This static class provides various utility methods for manipulating
 * class names.
 * </p>
 */

public class ClassUtils {
  /**
   * Extracts the unqualified class name from a fully qualified
   * class name.
   *
   * @param name The fully qualified class name.
   * @return The unqualified class name.
   */

  public static String unqualifiedName(String name) {
    int index = name.lastIndexOf('.');

    return name.substring(index + 1);
  }

  /**
   * Extracts the package name from a fully qualified class name.
   *
   * @param name The fully qualified class name.
   * @return The package name.
   */

  public static String packageName(String name) {
    int index = name.lastIndexOf('.');

    return name.substring(0, index);
  }

  /**
   * Don't allow instantiation, do allow overrides.
   */

  protected ClassUtils() {
  }
}
