package hu.u_szeged.eval.reader;

import java.util.List;

/**
 * Created by zsibritajanos on 2015.09.29..
 */
public abstract class CorpusReader {

  // default encoding
  private String encoding = "utf-8";

  public abstract List<List<List<String>>> read(String file, String encoding);

  public List<List<List<String>>> read(String file) {
    return read(file, encoding);
  }
}
