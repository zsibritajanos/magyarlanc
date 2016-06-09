package splitter.utils;

import java.io.*;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Map utilities.
 */

public class MapUtils {
  /**
   * Load string map from a URL.
   *
   * @param mapURL    URL for map file.
   * @param separator Field separator.
   * @param qualifier Quote character.
   * @param encoding  Character encoding for the file.
   * @return Map with values read from file.
   * @throws FileNotFoundException If input file does not exist.
   * @throws IOException           If input file cannot be opened.
   */

  public static Map<String, String> loadMap
  (
          URL mapURL,
          String separator,
          String qualifier,
          String encoding
  )
          throws IOException, FileNotFoundException {
    Map<String, String> map = MapFactory.createNewMap();

    if (mapURL != null) {
      BufferedReader bufferedReader =
              new BufferedReader
                      (
                              new UnicodeReader
                                      (
                                              mapURL.openStream(),
                                              encoding
                                      )
                      );

      String inputLine = bufferedReader.readLine();
      String[] tokens;

      while (inputLine != null) {
        tokens = inputLine.split(separator);

        if (tokens.length > 1) {
          map.put(tokens[0], tokens[1]);
        }

        inputLine = bufferedReader.readLine();
      }

      bufferedReader.close();
    }

    return map;
  }

  /**
   * Load string map from a file.
   *
   * @param mapFile   Map file.
   * @param separator Field separator.
   * @param qualifier Quote character.
   * @param encoding  Character encoding for the file.
   * @return Map with values read from file.
   * @throws FileNotFoundException If input file does not exist.
   * @throws IOException           If input file cannot be opened.
   */

  public static Map<String, String> loadMap
  (
          File mapFile,
          String separator,
          String qualifier,
          String encoding
  )
          throws IOException, FileNotFoundException {
    return loadMap(
            mapFile.toURI().toURL(), separator, qualifier, encoding);
  }

  /**
   * Load string map from a file name.
   *
   * @param mapFileName Map file name.
   * @param separator   Field separator.
   * @param qualifier   Quote character.
   * @param encoding    Character encoding for the file.
   * @return Map with values read from file name.
   * @throws FileNotFoundException If input file does not exist.
   * @throws IOException           If input file cannot be opened.
   */

  public static Map<String, String> loadMap
  (
          String mapFileName,
          String separator,
          String qualifier,
          String encoding
  )
          throws IOException, FileNotFoundException {
    return loadMap(
            new File(mapFileName), separator, qualifier, encoding);
  }

  /**
   * Load string map from a file name.
   *
   * @param mapFileName Map file name.
   * @return Map with values read from file name.
   * <p>
   * <p>
   * Map is assumed to be utf-8 encoded and have tab-separated values.
   * </p>
   * @throws FileNotFoundException If input file does not exist.
   * @throws IOException           If input file cannot be opened.
   */

  public static Map<String, String> loadMap(String mapFileName)
          throws IOException, FileNotFoundException {
    return loadMap
            (
                    new File(mapFileName), "\t", "", "utf-8"
            );
  }

  /**
   * Save map to a file.
   *
   * @param map       Map to save.
   * @param mapFile   Output file name.
   * @param separator Field separator.
   * @param qualifier Quote character.
   * @param encoding  Character encoding for the file.
   * @throws IOException If output file has error.
   */

  public static void saveMap
  (
          Map<?, ?> map,
          File mapFile,
          String separator,
          String qualifier,
          String encoding
  )
          throws IOException, FileNotFoundException {
    if (map != null) {
      PrintWriter printWriter = new PrintWriter(mapFile, "utf-8");

      Iterator<?> iterator = map.keySet().iterator();

      while (iterator.hasNext()) {
        Object key = iterator.next();
        String value = map.get(key).toString();

        printWriter.println
                (
                        qualifier + key + qualifier +
                                separator +
                                qualifier + value + qualifier
                );
      }

      printWriter.flush();
      printWriter.close();
    }
  }

  /**
   * Save map to a file name.
   *
   * @param map         Map to save.
   * @param mapFileName Output file name.
   * @param separator   Field separator.
   * @param qualifier   Quote character.
   * @param encoding    Character encoding for the file.
   * @throws IOException If output file has error.
   */

  public static void saveMap
  (
          Map<?, ?> map,
          String mapFileName,
          String separator,
          String qualifier,
          String encoding
  )
          throws IOException, FileNotFoundException {
    saveMap
            (
                    map, new File(mapFileName), separator, qualifier,
                    encoding
            );
  }

  /**
   * Save map to a file name.
   *
   * @param map         Map to save.
   * @param mapFileName Output file name.
   * @throws IOException If output file has error.
   *                     <p>
   *                     <p>
   *                     The map is saved with utf-8 encoding and a tab character
   *                     separating the value from the key.
   *                     </p>
   */

  public static void saveMap
  (
          Map<?, ?> map,
          String mapFileName
  )
          throws IOException, FileNotFoundException {
    saveMap
            (
                    map, new File(mapFileName), "\t", "", "utf-8"
            );
  }

  /**
   * Save map to a file in sorted key order.
   *
   * @param map       Map to save.
   * @param mapFile   Output file name.
   * @param separator Field separator.
   * @param qualifier Quote character.
   * @param encoding  Character encoding for the file.
   * @throws IOException If output file has error.
   */

  public static void saveSortedMap
  (
          Map<?, ?> map,
          File mapFile,
          String separator,
          String qualifier,
          String encoding
  )
          throws IOException, FileNotFoundException {
    if (map != null) {
      PrintWriter printWriter = new PrintWriter(mapFile, "utf-8");

      Set<Object> keySet = new TreeSet<Object>(map.keySet());
      Iterator<Object> iterator = keySet.iterator();

      while (iterator.hasNext()) {
        Object key = iterator.next();
        String value = map.get(key).toString();

        printWriter.println
                (
                        qualifier + key + qualifier +
                                separator +
                                qualifier + value + qualifier
                );
      }

      printWriter.flush();
      printWriter.close();
    }
  }

  /**
   * Save map to a file name in sorted key order.
   *
   * @param map         Map to save.
   * @param mapFileName Output file name.
   * @param separator   Field separator.
   * @param qualifier   Quote character.
   * @param encoding    Character encoding for the file.
   * @throws IOException If output file has error.
   */

  public static void saveSortedMap
  (
          Map<?, ?> map,
          String mapFileName,
          String separator,
          String qualifier,
          String encoding
  )
          throws IOException, FileNotFoundException {
    saveSortedMap
            (
                    map, new File(mapFileName), separator, qualifier,
                    encoding
            );
  }

  /**
   * Don't allow instantiation, do allow overrides.
   */

  protected MapUtils() {
  }
}
