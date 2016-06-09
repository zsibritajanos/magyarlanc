package hu.u_szeged.converter.univ;

/**
 * Created by zsibritajanos on 2016.01.10..
 */
public class Univ {

  public String lemma;
  public String pos;
  public String features;

  public Univ(String pos, String features) {
    this.pos = pos;
    this.features = features;
  }

  public Univ(String lemma, String pos, String features) {
    this.lemma = lemma;
    this.pos = pos;
    this.features = features;
  }

  public String toString() {
    return toString('\t');
  }

  public String toString(String separator) {
    StringBuffer stringBuffer = new StringBuffer();
    if (this.lemma != null) {
      stringBuffer.append(this.lemma + separator);
    }
    stringBuffer.append(this.pos + separator);
    stringBuffer.append(this.features);

    return stringBuffer.toString().trim();
  }

  public String toString(char separator) {
    return toString(String.valueOf(separator));
  }
}
