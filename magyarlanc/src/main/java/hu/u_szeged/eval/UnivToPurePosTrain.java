package hu.u_szeged.eval;

import hu.u_szeged.eval.reader.ColumnCorpusReader;
import hu.u_szeged.eval.reader.Corpus;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * Created by zsibritajanos on 2015.09.23..
 */
public class UnivToPurePosTrain {

  public static final String OUT_TOKEN_SEPARATOR = " ";
  public static final String OUT_TAG_SEPARATOR = "#";

  public static List<String> read(String in, String inEnc) {
    List<String> lines = null;

    //read
    try {
      lines = Files.readAllLines(Paths.get(in), Charset.forName(inEnc));
    } catch (IOException e) {
      e.printStackTrace();
    }

    return lines;
  }

  public static void main(String[] args) {




    String root = "./data/pure_mszny/2.5toUM/";
    String f = "newsml_1.ud";
    String encoding = "utf-8";


    String file = root + f;
    Corpus corpus = new Corpus(new ColumnCorpusReader("\t", "utf-8"));
    corpus.addFile(file);


    //System.out.println(corpus.getC);
    /*

    double tres = lines.size() * 0.8;

    List<String> train = lines.subList(0, (int)tres);
    List<String> test = lines.subList((int) tres, lines.size());


    System.out.println(tres);
    */

  }
}

