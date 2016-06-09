package splitter.utils;

import java.io.Serializable;

/**
 * Implements an integer object wrapper which allows changing the integer value.
 * <p>
 * <p>
 * The built-in Java Integer class does not allow changing the value
 * of the wrapped integer value once the Integer object is created.
 * MutableInteger provides most of the same methods as Integer but adds
 * extra methods to allow changing the value of the stored integer.
 * This is useful when wrapping integers for storage in collection types
 * since it is much more efficient to update the value of an existing
 * MutableInteger than to create a new Integer every time the value
 * changes.
 * </p>
 */

public class MutableInteger extends Number implements Serializable {
  /**
   * The integer wrapped here.
   */

  protected int mutableInteger;

  /**
   * Create MutableInteger object from an int value.
   *
   * @param i The integer being wrapped.
   */

  public MutableInteger(int i) {
    mutableInteger = i;
  }

  /**
   * Create MutableInteger object from a string.
   *
   * @param s String containing an integer value.
   */

  public MutableInteger(String s)
          throws NumberFormatException {
    mutableInteger = Integer.parseInt(s);
  }

  /**
   * Compares this object to another object.
   *
   * @param obj The other object.
   */

  public int compareTo(Object obj) {
    return compareTo((Number) obj);
  }

  /**
   * Compares this number to another number.
   *
   * @param number The other number.
   */

  public int compareTo(Number number) {
    return doCompare(mutableInteger, number.intValue());
  }

  protected int doCompare(int i, int j) {
    return (i >= j) ? ((int) ((i != j) ? 1 : 0)) : -1;
  }

  public boolean equals(Object obj) {
    if ((obj != null) && (obj instanceof Number))
      return (mutableInteger == ((Number) obj).intValue());
    else
      return false;
  }

  public int hashCode() {
    return mutableInteger;
  }

  public byte byteValue() {
    return (byte) mutableInteger;
  }

  public short shortValue() {
    return (short) mutableInteger;
  }

  public int intValue() {
    return mutableInteger;
  }

  public long longValue() {
    return (long) mutableInteger;
  }

  public float floatValue() {
    return (float) mutableInteger;
  }

  public double doubleValue() {
    return (double) mutableInteger;
  }

  public String toString() {
    return String.valueOf(mutableInteger);
  }

  public void setValue(byte aByte) {
    mutableInteger = aByte;
  }

  public void setValue(short aWord) {
    mutableInteger = aWord;
  }

  public void setValue(int i) {
    mutableInteger = i;
  }

  public void setValue(long l) {
    mutableInteger = (int) l;
  }

  public void setValue(float f) {
    mutableInteger = (int) f;
  }

  public void setValue(double d) {
    mutableInteger = (int) d;
  }

  public Integer toInteger() {
    return new Integer(mutableInteger);
  }
}
