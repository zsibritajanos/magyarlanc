package hu.u_szeged.eval.reader;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by zsibritajanos on 2015.09.29..
 */
public class ColumnCorpusReader extends CorpusReader {

  // token lemma msd separator
  private String columnSeparator;
  private String encoding;

  public ColumnCorpusReader(String separator, String encoding) {
    this.columnSeparator = separator;
    this.encoding = encoding;
  }

  public ColumnCorpusReader() {
    this("\t", "utf-8");
  }


  @Override
  public List<List<List<String>>> read(String file, String encoding) {

    List<String> lines = null;
    //read
    try {
      lines = Files.readAllLines(Paths.get(file), Charset.forName(encoding));
    } catch (IOException e) {
      e.printStackTrace();
    }

    List<List<List<String>>> corpus = new ArrayList<>();
    List<List<String>> sentence = new ArrayList<>();
    for (String line : lines) {

      if (line.trim().length() == 0) {
        corpus.add(sentence);
        sentence = new ArrayList<>();
      } else {
        List<String> token = new LinkedList<>();
        for (String s : line.trim().split(this.columnSeparator)) {
          token.add(s.trim());
        }
        sentence.add(token);
      }
    }
    return corpus;
  }

  public static void main(String[] args) {


    String file = "./data/cikk/out/face_np.morph.szk.out.stanford";
    ColumnCorpusReader c = new ColumnCorpusReader("\t", "utf-8");


    List<List<List<String>>> corpus = c.read(file);

    int ok = 0;
    int cntr = 0;

    for (List<List<String>> sentence : corpus) {
      for (List<String> token : sentence) {
        ++cntr;
        if (/*token.get(1).equals(token.get(3)) &&*/ token.get(2).equals(token.get(4))) {
          //System.out.println(token);
          ++ok;
        }
        else{
          System.out.println(token);
        }
      }
    }

    System.out.println(ok / (double)(cntr));


    //System.out.println(c.read(root + "10elb_replaced.pron").get(1));
  }
}

