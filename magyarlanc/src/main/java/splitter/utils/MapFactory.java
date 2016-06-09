package splitter.utils;

import java.util.*;

/**
 * Factory for creating maps.
 */

public class MapFactory {
  /**
   * Create a new unsorted map (HashMap).
   */

  public static <K, V> Map<K, V> createNewMap() {
    return new HashMap<K, V>();
  }

  /**
   * Create a new synchronized unsorted map (HashMap).
   */

  public static <K, V> Map<K, V> createNewSynchronizedMap() {
    return Collections.synchronizedMap(new HashMap<K, V>());
  }

  /**
   * Create a new unsorted map (HashMap) with specified initial capacity.
   *
   * @param capacity Initial capacity.
   */

  public static <K, V> Map<K, V> createNewMap(int capacity) {
    return new HashMap<K, V>(capacity);
  }

  /**
   * Create a new compressed unsorted map (CompressedHashMap).
   */

  public static <K, V> Map<K, V> createNewCompressedMap() {
    return new CompressedHashMap<K, V>();
  }

  /**
   * Create a new compressed unsorted map (HashMap) with specified initial capacity.
   *
   * @param capacity Initial capacity.
   */

  public static <K, V> Map<K, V> createNewCompressedMap(int capacity) {
    return new CompressedHashMap<K, V>(capacity);
  }

  /**
   * Create a new  map with keys in insertion order (LinkedHashMap).
   */

  public static <K, V> Map<K, V> createNewLinkedMap() {
    return new LinkedHashMap<K, V>();
  }

  /**
   * Create a new  map with keys in insertion order (LinkedHashMap) with
   * specified initial capacity..
   *
   * @param capacity Initial capacity.
   */

  public static <K, V> Map<K, V> createNewLinkedMap(int capacity) {
    return new LinkedHashMap<K, V>(capacity);
  }

  /**
   * Create a new sorted map (TreeMap).
   */

  public static <K, V> Map<K, V> createNewSortedMap() {
    return new TreeMap<K, V>();
  }

  /**
   * Don't allow instantiation, do allow overrides.
   */

  protected MapFactory() {
  }
}
