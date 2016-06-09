package hu.u_szeged.magyarlanc;

public class MagyarlacTest {

  /**
   *
   * @param args
   */
  public static void main(String[] args) {
    String[] sentence = "Már több mint valahányszor hez megpróbáltam".split(" ");
    //String[][] res = Magyarlanc.depParseSentence(sentence);
    String[][] res = Magyarlanc.morphParseSentence(sentence);
    Magyarlanc.printSentence(res);
  }
}
