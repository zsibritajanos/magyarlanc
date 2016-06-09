package splitter.utils;

import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Factory for creating a HashSet.
 */

public class SetFactory {
  /**
   * Create a new HashSet.
   */

  public static <E> Set<E> createNewSet() {
    return new HashSet<E>();
  }

  /**
   * Create a new HashSet.
   *
   * @param capacity Initial capacity.
   */

  public static <E> Set<E> createNewSet(int capacity) {
    return new HashSet<E>(capacity);
  }

  /**
   * Create a new TreeSet.
   */

  public static <E> SortedSet<E> createNewSortedSet() {
    return new TreeSet<E>();
  }

  /**
   * Don't allow instantiation, do allow overrides.
   */

  protected SetFactory() {
  }
}