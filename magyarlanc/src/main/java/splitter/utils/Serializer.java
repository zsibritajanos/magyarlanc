package splitter.utils;

import java.io.IOException;

/**
 * Interface for object serialization to and from an array of bytes.
 */

public interface Serializer {
  /**
   * Serialize an object to an array of bytes.
   *
   * @param object The object to serialize.
   * @return Serialized object as an array of bytes.
   */

  public byte[] serializeToBytes(Object object) throws IOException;

  /**
   * Deserialize an object from an array of bytes.
   *
   * @param serializedObject Array of bytes containing a serialized object.
   * @return The deserialized object.
   * @throws IOException
   * @throws ClassNotFoundException
   */

  public Object deserializeFromBytes(byte[] serializedObject)
          throws IOException, ClassNotFoundException;
}
