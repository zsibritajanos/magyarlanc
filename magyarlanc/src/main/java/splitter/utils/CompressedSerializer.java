package splitter.utils;

import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Serialize and unserialize objects with GZIP compression.
 */

public class CompressedSerializer implements Serializer {
  /**
   * Create a compressed serializer.
   */

  public CompressedSerializer() {
  }

  /**
   * Serialize an object to a compressed array of bytes.
   *
   * @param object The object to serialize.
   * @return Serialized object as a compressed array of bytes.
   */

  public byte[] serializeToBytes(Object object)
          throws IOException {
    //  Get byte array output stream.

    ByteArrayOutputStream byteStream = new ByteArrayOutputStream();

    //  Get GZIP output stream over
    //  byte stream.   This will compress
    //  the byte stream written below.

    GZIPOutputStream gzipStream =
            new GZIPOutputStream(byteStream);

    //  Create object output stream over
    //  byte output stream.

    ObjectOutputStream objectStream =
            new ObjectOutputStream(gzipStream);

    //  Write object to output stream, which
    //  serializes the object.

    objectStream.writeObject(object);

    //  Close object stream.
    objectStream.close();
    //  Return serialized object as array
    //  of bytes.

    return byteStream.toByteArray();
  }

  /**
   * Deserialize an object from a compressed array of bytes.
   *
   * @param serializedObject Array of bytes containing a
   *                         compressed serialized object.
   * @return The deserialized object.
   * @throws IOException
   * @throws ClassNotFoundException
   */

  public Object deserializeFromBytes(byte[] serializedObject)
          throws IOException, ClassNotFoundException {
    //  Open byte input stream over
    //  serialized object bytes.

    ByteArrayInputStream byteStream =
            new ByteArrayInputStream(serializedObject);

    //  Get GZIP input stream over
    //  byte stream.   This will decompress
    //  the byte stream read below.

    GZIPInputStream gzipStream =
            new GZIPInputStream(byteStream);

    //  Open object stream over
    //  byte input stream.

    ObjectInputStream objectStream =
            new ObjectInputStream(gzipStream);

    //  Read object, which deserializes the
    //  object.

    Object result = objectStream.readObject();

    //  Close object stream.

    objectStream.close();

    //  Return deserialized object.
    return result;
  }
}
