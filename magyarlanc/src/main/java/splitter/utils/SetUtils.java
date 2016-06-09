package splitter.utils;

import java.io.*;
import java.net.URL;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;

/**
 * Set utilities.
 */

public class SetUtils {
  /**
   * Load string set from a URL.
   *
   * @param set      Set into which to read data from URL.
   * @param setURL   URL for set file.
   * @param encoding Character encoding for the file.
   * @return Set with values read from URL.
   * @throws FileNotFoundException If input URL does not exist.
   * @throws IOException           If input URL cannot be opened.
   */

  public static Set<String> loadIntoSet
  (
          Set<String> set,
          URL setURL,
          String encoding
  )
          throws IOException, FileNotFoundException {
    if ((set != null) && (setURL != null)) {
      BufferedReader bufferedReader =
              new BufferedReader
                      (
                              new UnicodeReader
                                      (
                                              setURL.openStream(),
                                              encoding
                                      )
                      );

      String inputLine = bufferedReader.readLine();

      while (inputLine != null) {
        set.add(inputLine);

        inputLine = bufferedReader.readLine();
      }

      bufferedReader.close();
    }

    return set;
  }

  /**
   * Load string set from a URL.
   *
   * @param setURL   URL for set file.
   * @param encoding Character encoding for the file.
   * @return Set with values read from URL.
   * @throws FileNotFoundException If input file does not exist.
   * @throws IOException           If input file cannot be opened.
   */

  public static Set<String> loadSet
  (
          URL setURL,
          String encoding
  )
          throws IOException, FileNotFoundException {
    Set<String> set = SetFactory.createNewSet();

    loadIntoSet(set, setURL, encoding);

    return set;
  }

  /**
   * Load sorted string set from a URL.
   *
   * @param setURL   URL for set file.
   * @param encoding Character encoding for the file.
   * @return Sorted set with values read from URL.
   * @throws FileNotFoundException If input file does not exist.
   * @throws IOException           If input file cannot be opened.
   */

  public static Set<String> loadSortedSet
  (
          URL setURL,
          String encoding
  )
          throws IOException, FileNotFoundException {
    Set<String> set = SetFactory.createNewSortedSet();

    loadIntoSet(set, setURL, encoding);

    return set;
  }

  /**
   * Load string set from a file.
   *
   * @param setFile  File from which to load set.
   * @param encoding Character encoding for the file.
   * @return Set with values read from file.
   * @throws FileNotFoundException If input file does not exist.
   * @throws IOException           If input file cannot be opened.
   */

  public static Set<String> loadSet
  (
          File setFile,
          String encoding
  )
          throws IOException, FileNotFoundException {
    return loadSet(setFile.toURI().toURL(), encoding);
  }

  /**
   * Load sorted string set from a file.
   *
   * @param setFile  File from which to load set.
   * @param encoding Character encoding for the file.
   * @return Sorted set with values read from file.
   * @throws FileNotFoundException If input file does not exist.
   * @throws IOException           If input file cannot be opened.
   */

  public static Set<String> loadSortedSet
  (
          File setFile,
          String encoding
  )
          throws IOException, FileNotFoundException {
    return loadSortedSet(setFile.toURI().toURL(), encoding);
  }

  /**
   * Load string set from a file name.
   *
   * @param setFileName File name from which to load set.
   * @param encoding    Character encoding for the file.
   * @return Set with values read from file name.
   * @throws FileNotFoundException If input file does not exist.
   * @throws IOException           If input file cannot be opened.
   */

  public static Set<String> loadSet
  (
          String setFileName,
          String encoding
  )
          throws IOException, FileNotFoundException {
    return loadSet(new File(setFileName), encoding);
  }

  /**
   * Load sorted string set from a file name.
   *
   * @param setFileName File name from which to load set.
   * @param encoding    Character encoding for the file.
   * @return Sorted set with values read from file name.
   * @throws FileNotFoundException If input file does not exist.
   * @throws IOException           If input file cannot be opened.
   */

  public static Set<String> loadSortedSet
  (
          String setFileName,
          String encoding
  )
          throws IOException, FileNotFoundException {
    return loadSortedSet(new File(setFileName), encoding);
  }

  /**
   * Save set to a file.
   *
   * @param set      Set to save.
   * @param setFile  Output file name.
   * @param encoding Character encoding for the file.
   * @throws IOException If output file has error.
   */

  public static void saveSet
  (
          Set<?> set,
          File setFile,
          String encoding
  )
          throws IOException, FileNotFoundException {
    if (set != null) {
      PrintWriter printWriter = new PrintWriter(setFile, "utf-8");

      Iterator<?> iterator = set.iterator();

      while (iterator.hasNext()) {
        String value = iterator.next().toString();

        printWriter.println(value);
      }

      printWriter.flush();
      printWriter.close();
    }
  }

  /**
   * Save sorted set to a file.
   *
   * @param set      Set to save.  The entries are sorted
   *                 in ascending order for output.
   * @param setFile  Output file name.
   * @param encoding Character encoding for the file.
   * @throws IOException If output file has error.
   */

  public static void saveSortedSet
  (
          Set<?> set,
          File setFile,
          String encoding
  )
          throws IOException, FileNotFoundException {
    if (set != null) {
      if (set instanceof SortedSet) {
        saveSet(set, setFile, encoding);
      } else {
        SortedSet<Object> sortedSet = SetFactory.createNewSortedSet();

        sortedSet.addAll(set);

        saveSet(sortedSet, setFile, encoding);
      }
    }
  }

  /**
   * Save set as string to a file name.
   *
   * @param set         Set to save.
   * @param setFileName Output file name.
   * @param encoding    Character encoding for the file.
   * @throws IOException If output file has error.
   */

  public static void saveSet
  (
          Set<?> set,
          String setFileName,
          String encoding
  )
          throws IOException, FileNotFoundException {
    saveSet(set, new File(setFileName), encoding);
  }

  /**
   * Save sorted set to a file name.
   *
   * @param set         Set to save.
   * @param setFileName Output file name.
   * @param encoding    Character encoding for the file.
   * @throws IOException If output file has error.
   */

  public static void saveSortedSet
  (
          Set<?> set,
          String setFileName,
          String encoding
  )
          throws IOException, FileNotFoundException {
    saveSortedSet(set, new File(setFileName), encoding);
  }

  /**
   * Add array entries to a set.
   *
   * @param set Set to which to add array entries.
   * @param t   Array whose entries are to be added.
   */

  public static <T> void addAll
  (
          Set<T> set,
          T[] t
  )
          throws IOException, FileNotFoundException {
    set.addAll(Arrays.asList(t));
  }

  /**
   * Don't allow instantiation, do allow overrides.
   */

  protected SetUtils() {
  }
}
