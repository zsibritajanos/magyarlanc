package splitter.utils;

import java.io.*;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Set;

/**
 * UTF8Properties -- A UTF8 compatible version of Java Properties.
 * <p>
 * <p>
 * Regular Java Properties must be read and written using the
 * ISO8859-1 character encoding.  UTF8Properties reads and writes
 * the properties using the utf-8 character encoding instead.
 * </p>
 */

public class UTF8Properties
        extends Properties
        implements TaggedStrings, Serializable {
  /**
   * Allowed separators between key and value of a property.
   */

  protected static final String keyValueSeparators = "=: \t\r\n\f";

  /**
   * Create empty property list with no default values.
   */

  public UTF8Properties() {
    this(null);
  }

  /**
   * Create empty property list with specified defaults.
   *
   * @param defaults Default property values.
   */

  public UTF8Properties(Properties defaults) {
    this.defaults = defaults;
  }

  /**
   * Reads a property list (key and element pairs) from a utf-8 encoded
   * input stream.
   *
   * @param inputStream  The input stream.
   * @param defaultValue The default value when none specified.
   * @throws IOException              When an error occurs while reading
   *                                  from the input stream.
   * @throws IllegalArgumentException When the input stream contains an invalid Unicode
   *                                  escape sequence.
   *                                  <p>
   *                                  <p>
   *                                  The default value is assigned to any key which does not
   *                                  have a value.  Assume the default value is the string "missing".
   *                                  Then the input line
   *                                  </p>
   *                                  <p>
   *                                  <p><code>x=1</code></p>
   *                                  <p>
   *                                  <p>
   *                                  returns a key of "x" with value of "1", while the input line
   *                                  </p>
   *                                  <p>
   *                                  <p><code>x</code></p>
   *                                  <p>
   *                                  <p>
   *                                  returns a key of "x" with value of "missing" .
   *                                  </p>
   */

  public synchronized void load
  (
          InputStream inputStream,
          String defaultValue
  )
          throws IOException {
    //  Wrap provided input stream
    //  with a buffered reader.
    BufferedReader in =
            new BufferedReader
                    (
                            new UnicodeReader(inputStream, "utf-8")
                    );
    //  Read first input line.

    String line = in.readLine();

    //  While there are still input lines ...

    while (line != null) {
      //  Remove leading whitespace from
      //  this line.

      line = removeLeadingWhiteSpace(line);

      //  Ignore empty lines and comments,

      if (!line.equals("") && (line.charAt(0) != '#')) {
        //  First or only line of property.

        String property = line;

        //  Pick up all continuation lines and
        //  merge them together into one big
        //  string.

        while (isContinued(line)) {
          property =
                  property.substring(0, property.length() - 1);

          line = in.readLine();
          property += line;
        }
        //  If the property line isn't empty,
        //  split it into the key and the value.

        if (!property.equals("")) {
          int endOfKey = 0;

          //  Find end of key.

          while ((endOfKey < property.length()) &&
                  (keyValueSeparators.indexOf(
                          property.charAt(endOfKey)) == -1)) {
            endOfKey++;
          }
          //  Extract key and value.

          String key = property.substring(0, endOfKey);
          String value = defaultValue;

          //  Assign default value if none
          //  found in the property line.

          if ((endOfKey + 1) <= property.length()) {
            value =
                    property.substring(
                            endOfKey + 1, property.length());
          }
          //  Convert any escaped characters to
          //  regular characters in key and value.

          key = convertInputString(key);
          value =
                  convertInputString(
                          removeLeadingWhiteSpace(value));

          //  Store the processed key and value.

          put(key, value);
        }
      }
      //  Read next input line if any.

      line = in.readLine();
    }
  }

  /**
   * Reads a property list (key and element pairs) from a utf-8 encoded
   * input stream.
   *
   * @param inputStream The input stream.
   * @throws IOException              When an error occurs while reading
   *                                  from the input stream.
   * @throws IllegalArgumentException When the input stream contains an invalid Unicode
   *                                  escape sequence.
   *                                  <p>
   *                                  <p>
   *                                  Null is used as the default value for any key without a
   *                                  specified value.
   *                                  </p>
   */

  public synchronized void load(InputStream inputStream)
          throws IOException {
    load(inputStream, null);
  }

  /**
   * Remove leading white space from a string.
   *
   * @param line The string from which to remove leading
   *             white space.
   * @return String with leading while space removed.
   */

  public static String removeLeadingWhiteSpace(String line) {
    int index = 0;

    while ((index < line.length()) &&
            (keyValueSeparators.indexOf(
                    line.charAt(index)) != -1)
            ) {
      index++;
    }

    return line.substring(index, line.length());
  }

  /**
   * Replace all characters preceded by a '\' with the corresponding
   * special character and convert unicode escape sequences to
   * normal characterS.
   *
   * @param line String for which to process escape sequences.
   * @return The processed string.
   */

  public static String convertInputString(String line) {
    //  Accumulates decoded input characters.

    StringBuffer sb = new StringBuffer(line.length());

    //  Loop over each character in the
    //  input string.

    for (int index = 0; index < line.length(); index++) {
      //  Get next character in input string.

      char currentChar = line.charAt(index);

      //  Decode escaped characters.  These
      //  start with a "\".

      if (currentChar == '\\') {
        //  Skip past leading "\".
        index++;
        //  Get the escaped character.

        currentChar = line.charAt(index);

        //  Convert escaped character to
        //  its internal representation.

        switch (currentChar) {
          //  Form feed.

          case 'f':
            currentChar = '\f';
            break;

          //  Line feed.

          case 'n':
            currentChar = '\n';
            break;

          //  Carriage return
          case 'r':
            currentChar = '\r';
            break;

          //  Tab.
          case 't':
            currentChar = '\t';
            break;

          //  Unicode value of form udddd where
          //  "dddd" is a four-digit hexadecimal
          //  value.

          case 'u':
            //  Skip past initial "u" character.

            index++;

            //  Accumulates Unicode character value.

            int unicodeValue = 0;

            //  Pick up each of the four characters
            //  of the unicode value.

            for (int i = 0; i < 4; i++) {
              //  Get next unicode character,
              //  converting letters to lower case.

              currentChar =
                      Character.toLowerCase
                              (
                                      line.charAt(index++)
                              );

              //  Handle digit ('0' through '9')

              if ((currentChar >= '0') &&
                      (currentChar <= '9')) {
                unicodeValue =
                        (unicodeValue << 4) +
                                currentChar - '0';
              }

              //  Handle letters 'a' through 'f'.

              else if ((currentChar >= 'a') &&
                      (currentChar <= 'f')) {
                unicodeValue =
                        (unicodeValue << 4) + 10 +
                                currentChar - 'a';
              }

              //  Anything other than a letter or
              //  a digit is invalid in the
              //  unicode value.

              else {
                throw new IllegalArgumentException
                        (
                                "Invalid \\uxxxx encoding."
                        );
              }
            }
            //  Set current character to accumulated
            //  Unicode value.

            currentChar = (char) unicodeValue;

            //  Back up one character in input string
            //  to continue parsing.

            index--;
            break;

          //  Accept any other character as-is.
          default:
            break;
        }
      }
      //  Append character to output string.

      sb.append(currentChar);
    }
    //  Return output string.

    return sb.toString();
  }

  /**
   * Format string for output.
   *
   * @param line String to convert to output format.
   * @return Output formatted string .
   */

  protected String formatStringForOutput(String line) {
    int length = line.length();

    StringBuffer outBuffer = new StringBuffer(length * 2);

    for (int i = 0; i < length; i++) {
      char currentChar = line.charAt(i);

      switch (currentChar) {
        case '\\':
          outBuffer.append('\\');
          outBuffer.append('\\');
          break;

        case '\f':
          outBuffer.append('\\');
          outBuffer.append('f');
          break;

        case '\n':
          outBuffer.append('\\');
          outBuffer.append('n');
          break;

        case '\r':
          outBuffer.append('\\');
          outBuffer.append('r');
          break;

        case '\t':
          outBuffer.append('\\');
          outBuffer.append('t');
          break;

        default:
          outBuffer.append(currentChar);
          break;
      }
    }

    return outBuffer.toString();
  }

  /**
   * Check if property continues on the next line.
   *
   * @param line Beginning of the property that might be continued
   *             on the next line.
   * @return true if property continues on the following line.
   * <p>
   * <p>
   * A trailing "\" character indicates a property value is continued
   * on the next line.
   * </p>
   */

  protected boolean isContinued(String line) {
    if ((line != null) && !line.equals("")) {
      return line.charAt(line.length() - 1) == '\\';
    }

    return false;
  }

  /**
   * Store properties to a file ignoring any I/O errors.
   *
   * @param outputStream Stream to which to save properties.
   * @param header       Optionl description of the property list.
   * @throws ClassCastException If any key or value is not a String.
   * @deprecated Use {@link #store(OutputStream, String) }
   * instead.
   */

  public void save(OutputStream outputStream, String header) {
    try {
      store(outputStream, header);
    } catch (IOException e) {
    }
  }

  /**
   * Store properties to a file.
   *
   * @param outputStream   Output stream.
   * @param header         Optional description of property list.
   * @param outputOnlyKeys True to output only the keys,
   *                       false to output both the keys
   *                       and the values.
   * @throws IOException When an error occurs while writing
   *                     to the output stream.
   */

  public void store
  (
          OutputStream outputStream,
          String header,
          boolean outputOnlyKeys
  )
          throws IOException {
    BufferedWriter output;

    output =
            new BufferedWriter
                    (
                            new OutputStreamWriter(outputStream, "utf-8")
                    );
    //  Disallow modification by other
    //  threads while we are writing the
    //  properties to the output stream.
    synchronized (this) {
      //  Write header if provided.

      if (header != null) {
        output.write("#" + header);
        output.newLine();
      }
      //  Output date/time stamp.

      output.write("#" + Calendar.getInstance().getTime());
      output.newLine();

      //  Enumerate all properties and
      //  write each (key,value)pair
      //  to the output stream.

      Enumeration enumeration = keys();

      while (enumeration.hasMoreElements()) {
        //  Next key.

        String key =
                formatStringForOutput(
                        (String) enumeration.nextElement());

        //  Write key to output stream.

        output.write(key);

        //  Output value if unless we're
        //  only writing keys.

        if (!outputOnlyKeys) {
          //  Value for key.

          String value =
                  formatStringForOutput((String) get(key));

          output.write("=" + value);
        }

        output.newLine();
      }
    }
    //  Make sure all output is flushed
    //  from the output buffer.
    output.flush();
  }

  /**
   * Store properties to a file.
   *
   * @param outputStream Output stream.
   * @param header       Optional description of the property list.
   * @throws IOException When an error occurs while writing
   *                     to the output stream.
   */

  public void store(OutputStream outputStream, String header)
          throws IOException {
    store(outputStream, header, false);
  }

  /**
   * See if list contains specified string.
   *
   * @param string The string.
   * @return True if list contains specified string.
   */

  public boolean containsString(String string) {
    return (getProperty(string) != null);
  }

  /**
   * Get the tag value associated with a string.
   *
   * @param string The string.
   * @return The tag value associated with the string.
   * May be null.
   */

  public String getTag(String string) {
    return getProperty(string);
  }

  /**
   * Set the tag value associated with a string.
   *
   * @param string The string.
   * @param tag    The tag.
   */

  public void putTag(String string, String tag) {
    setProperty(string, tag);
  }

  /**
   * Get number of strings.
   *
   * @return Number of strings.
   */

  public int getStringCount() {
    return size();
  }

  /**
   * Get set of all unique strings.
   *
   * @return Set of all unique strings.
   */

  public Set<String> getAllStrings() {
    Set<String> result = SetFactory.createNewSet();

    for (Enumeration enumeration = propertyNames();
         enumeration.hasMoreElements();
            ) {
      result.add(enumeration.nextElement().toString());
    }

    return result;
  }

  /**
   * Get set of all unique tags.
   *
   * @return Set of all unique tags
   */

  public Set<String> getAllTags() {
    Set<String> result = SetFactory.createNewSet();

    for (Enumeration enumeration = propertyNames();
         enumeration.hasMoreElements();
            ) {
      String key = (String) enumeration.nextElement();
      result.add(getProperty(key));
    }

    return result;
  }
}
