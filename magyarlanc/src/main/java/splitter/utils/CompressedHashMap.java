package splitter.utils;

import java.util.*;

/**
 * A hash map which stores its values in compressed form.
 */

public class CompressedHashMap<K, V>
        implements Map<K, V> {
  /**
   * The object compressor for the values in the hash map.
   */

  protected static CompressedSerializer serializer =
          new CompressedSerializer();

  /**
   * The delagated-to hash map used to store the compressed values.
   */

  protected HashMap<K, Object> delegateMap;

  /**
   * Create hash map.
   */

  public CompressedHashMap() {
    delegateMap = new HashMap<K, Object>();
  }

  /**
   * Create hash map with specified initial capacity.
   *
   * @param initialCapacity The initial capacity.
   */

  public CompressedHashMap(int initialCapacity) {
    delegateMap = new HashMap<K, Object>(initialCapacity);
  }

  /**
   * Create hash map with specified initial capacity and load factor.
   *
   * @param initialCapacity The initial capacity.
   * @param loadFactor      The load factor.
   */

  public CompressedHashMap(int initialCapacity, float loadFactor) {
    delegateMap =
            new HashMap<K, Object>(initialCapacity, loadFactor);
  }

  /**
   * Create hash map from another map.
   *
   * @param map The other map from which to load entries.
   */

  public CompressedHashMap(Map<? extends K, ? extends V> map) {
    delegateMap = new HashMap<K, Object>(map.size());
    putAll(map);
  }

  @SuppressWarnings("unchecked")
  protected V decompress(Object o) {
    V result = null;

    try {
      result = (V) (serializer.deserializeFromBytes((byte[]) o));
    } catch (Exception e) {
    }

    return result;
  }

  protected Object compress(Object o) {
    Object result = null;

    try {
      result = serializer.serializeToBytes(o);
    } catch (Exception e) {
    }

    return result;
  }

  public void clear()
          throws UnsupportedOperationException {
    delegateMap.clear();
  }

  public Object clone() {
    return delegateMap.clone();
  }

  public boolean containsKey(Object key)
          throws ClassCastException,
          NullPointerException {
    return delegateMap.containsKey(key);
  }

  public boolean containsValue(Object value)
          throws ClassCastException,
          NullPointerException {
    return delegateMap.containsValue(compress(value));
  }

  public Set<Entry<K, V>> entrySet() {
    Set<Entry<K, V>> result =
            new HashSet<Entry<K, V>>();

    Iterator<Entry<K, Object>> iterator =
            delegateMap.entrySet().iterator();

    while (iterator.hasNext()) {
      Entry<K, Object> entry = iterator.next();

      Entry<K, V> resultEntry =
              new AbstractMap.SimpleEntry<K, V>
                      (
                              entry.getKey(),
                              decompress(entry.getValue())
                      );

      result.add(resultEntry);
    }

    return result;
  }

  public boolean isEmpty() {
    return delegateMap.isEmpty();
  }

  public Set<K> keySet() {
    return delegateMap.keySet();
  }

  public int size() {
    return delegateMap.size();
  }

  public void putAll(Map<? extends K, ? extends V> m)
          throws UnsupportedOperationException,
          ClassCastException,
          NullPointerException,
          IllegalArgumentException {
    Iterator<? extends K> iterator = m.keySet().iterator();

    while (iterator.hasNext()) {
      K key = iterator.next();
      V value = m.get(key);

      put(key, value);
    }
  }

  public Collection<V> values() {
    Collection<V> result = new ArrayList<V>();
    Iterator<Object> iterator = delegateMap.values().iterator();

    while (iterator.hasNext()) {
      result.add(decompress(iterator.next()));
    }

    return result;
  }

  public V remove(Object key)
          throws
          UnsupportedOperationException,
          ClassCastException,
          NullPointerException {
    Object o = delegateMap.remove(key);
    V result = null;

    if (o != null) {
      result = decompress(o);
    }

    return result;
  }

  public V get(Object key)
          throws ClassCastException,
          NullPointerException {
    Object o = delegateMap.get(key);
    V result = null;

    if (o != null) {
      result = decompress(o);
    }

    return result;
  }

  public V put(K key, V value)
          throws
          UnsupportedOperationException,
          ClassCastException,
          NullPointerException,
          IllegalArgumentException {
    V result = get(key);

    delegateMap.put(key, compress(value));

    return result;
  }
}
