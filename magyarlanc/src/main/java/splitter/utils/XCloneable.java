package splitter.utils;

/**
 * Interface for a cloneable object.
 */

public interface XCloneable extends Cloneable {
  public Object clone()
          throws CloneNotSupportedException;
}