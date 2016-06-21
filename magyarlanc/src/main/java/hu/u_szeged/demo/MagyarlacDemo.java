package hu.u_szeged.demo;

import hu.u_szeged.cons.parser.MyBerkeleyParser;
import hu.u_szeged.dep.parser.MyMateParser;
import hu.u_szeged.magyarlanc.Magyarlanc;
import hu.u_szeged.pos.purepos.MyPurePos;
import splitter.MySplitter;

import java.util.List;

public class MagyarlacDemo {

  /**
   * @param args
   */
  public static void main(String[] args) {

    String text = "Két folyó találkozásánál fekszik a város, ami a várakozások ellenére nem egy oroszlánról kapta a nevét. Megmutatjuk a legjobb kocsmákat, és hogy hol lehet a legnagyobb a magyarok öröme.";


    /**
     * Mondatrabontas es tokenizalas
     */
    // (lista)
    List<List<String>> sentencesList = MySplitter.getInstance().split(text);
    //(tomb)
    String[][] sentencesArray = MySplitter.getInstance().splitToArray(text);


    /**
     * Morfologia
     */
    // lita
    for (List<String> sentence : sentencesList) {
      String[][] morph = MyPurePos.getInstance().morphParseSentence(sentence);
    }
    // tomb
    for (String[] sentence : sentencesArray) {
      String[][] morph = MyPurePos.getInstance().morphParseSentence(sentence);
    }

    /**
     * Dependencia
     */
    // tomb
    for (String[] sentence : sentencesArray) {
      String[][] dep = MyMateParser.getInstance().parseSentence(sentence);
    }

    /**
     * Konstituens
     */
    // tomb
    for (String[] sentence : sentencesArray) {
      String[][] kons = MyBerkeleyParser.getInstance().parseSentence(sentence);
    }

    /**
     * Magyarlanc
     */
    // tomb
    for (String[] sentence : sentencesArray) {
      String[][] morph = Magyarlanc.morphParseSentence(sentence);
      //Magyarlanc.printSentence(morph);
      String[][] dep = Magyarlanc.depParseSentence(sentence);
      //Magyarlanc.printSentence(dep);
      String[][] kons = Magyarlanc.constParseSentence(sentence);
      //Magyarlanc.printSentence(kons);
    }
  }
}
