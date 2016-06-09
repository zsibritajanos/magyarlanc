package splitter.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Factory for creating ArrayLists.
 */

public class ListFactory {
  /**
   * Create a new ArrayList.
   */

  public static <E> List<E> createNewList() {
    return new ArrayList<E>();
  }

  /**
   * Create a new ArrayList of a specified size.
   *
   * @param nSize Size of array list to create.
   */

  public static <E> List<E> createNewList(int nSize) {
    return new ArrayList<E>(nSize);
  }

  /**
   * Create a new ArrayList from a collection.
   *
   * @param collection Collection from which to create list.
   */

  public static <E> List<E> createNewList(Collection<E> collection) {
    return new ArrayList<E>(collection);
  }

  /**
   * Create a new sorted list of a specified size.
   *
   * @param nSize Size of array list to create.
   */

  public static <E> List<E> createNewSortedList(int nSize) {
    return new SortedArrayList<E>(nSize);
  }

  /**
   * Create a new sorted list from a collection.
   *
   * @param collection Collection from which to create list.
   */

  public static <E> List<E> createNewSortedList(Collection<E> collection) {
    return new SortedArrayList<E>(collection);
  }

  /**
   * Don't allow instantiation, do allow overrides.
   */

  protected ListFactory() {
  }
}
