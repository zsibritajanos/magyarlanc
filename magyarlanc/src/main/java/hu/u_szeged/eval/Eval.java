package hu.u_szeged.eval;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Eval {

  public static final String PATH = "./data/purepos/";
  public static final String[] CORPUS = {"computer", "law", "literature", "newsml", "newspaper", "student"};
  public static final String ENCODING = "utf-8";

  /**
   * gold
   */
  //public static final String PATH_GOLD = PATH + "gold_stanford/";
  public static final String PATH_GOLD = PATH + "gold/";
  //public static final String EXTENSION_GOLD = ".gold.stanford";
  public static final String EXTENSION_GOLD = ".gold.purepos";


  /**
   * out
   */
  public static final String PATH_OUT = PATH + "morphed_out/";
  public static final String EXTENSION_OUT_PUREPOS = ".out.purepos.morphed.out";

  private static float evalStnafordLines(List<String> goldLines, List<String> outLines) {
    int OK = 0;
    int ERR = 0;

    for (int i = 0; i < goldLines.size(); ++i) {
      String[] goldSplit = goldLines.get(i).split("\t");
      String[] outSplit = outLines.get(i).split("\t");

      if (goldSplit.length > 1) {
        if (goldSplit[2].equals(outSplit[2])) {
          ++OK;
        } else {
          ++ERR;
        }
      }
    }
    return OK / (float) (ERR + OK);
  }

  private static float evalPureposLines(List<String> goldLines, List<String> outLines) {
    int OK = 0;
    int ERR = 0;

    for (int i = 0; i < goldLines.size(); ++i) {

      //System.out.println(i);
      String[] goldSplit = goldLines.get(i).split(" ");
      String[] outSplit = outLines.get(i).split(" ");

      for (int j = 0; j < goldSplit.length; ++j) {
        //System.out.println(goldSplit[j].split("#")[1]);

        String wordForm = goldSplit[j].split("#")[0];

        String goldLemma = goldSplit[j].split("#")[1];
        String outLemma = outSplit[j].split("#")[1];

        String goldPOS = goldSplit[j].split("#")[2].replace("Pos=PROPN", "Pos=NOUN");
        String outPOS = outSplit[j].split("#")[2].replace("Pos=PROPN", "Pos=NOUN");


        //System.out.println(goldLemma + " " + goldPOS + "\t" + outLemma + " " + outPOS);

        if (goldPOS.equals(outPOS) && goldLemma.equalsIgnoreCase(outLemma)) {
          ++OK;
        } else {
          System.out.println(wordForm + "\t" + goldLemma + "\t" + outLemma + "\t" + goldPOS + "\t" + outPOS);
          ++ERR;
        }
      }
    }

    System.out.println(OK);

    return OK / (float) (ERR + OK);
  }


  private static float evalCorpus(String goldPath, String file, String goldExtension, String outPath, String outExtension) {
    List<String> goldLines = null;

    //read gold
    try {
      goldLines = Files.readAllLines(Paths.get(goldPath + file + goldExtension), Charset.forName(ENCODING));
    } catch (IOException e) {
      e.printStackTrace();
    }

    List<String> outLines = null;
    //read out
    try {
      outLines = Files.readAllLines(Paths.get(outPath + file + outExtension), Charset.forName(ENCODING));
    } catch (IOException e) {
      e.printStackTrace();
    }

    return evalPureposLines(goldLines, outLines);
  }

  private static float evalCorpus(String goldFile, String outFile) {
    List<String> goldLines = null;

    //read gold
    try {
      goldLines = Files.readAllLines(Paths.get(goldFile), Charset.forName(ENCODING));
    } catch (IOException e) {
      e.printStackTrace();
    }

    List<String> outLines = null;
    //read out
    try {
      outLines = Files.readAllLines(Paths.get(outFile), Charset.forName(ENCODING));
    } catch (IOException e) {
      e.printStackTrace();
    }

    return evalPureposLines(goldLines, outLines);
  }

  private static void eval() {

    for (String corpus : CORPUS) {
      try {
        System.out.print(corpus + "\t");
        System.out.println(evalCorpus(PATH_GOLD, corpus, EXTENSION_GOLD, PATH_OUT, EXTENSION_OUT_PUREPOS));
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }


  public static void main(String[] args) {

    String root = "./data/pure_mszny/szk_25/";

    String g = "newsml_1.pron.univ_2.gold";

    //String o = "newsml_1.pron.univ.test.morph.out_2";
    String o = "newsml_1.pron.univ.test.out_2";

    System.out.println(evalCorpus(root + g, root + o));

    //String root = "./data/szk30/web_egyseges/purepos_eval/";
    //   String root = "./data/_______________UJ/";
    //    String goldFile = root + "face_np.morph.pure.gold";


    /*
    for (String c : new String[]{"computer", "law", "literature", "newsml", "newspaper", "student", "szk"})
      for (String r : new String[]{"10", "5", "2", "1"})
        System.out.println(evalCorpus(goldFile, root + "face/face_np.morph.pure." + c + "." + r + ".out"));
     */

//    for (String r : new String[]{"5"})
//      System.out.println(evalCorpus(goldFile, root + "face_np.morph.pure.szkmorpf.szkma." + r + ".out"));

    //System.out.println(evalCorpus(goldFile, root + "web/web_np.morph.pure.szkmorpf.szkma." + r + ".out"));


    //System.out.println(evalCorpus("./data/szk30/web_egyseges/", "web_np.morph", ".pure.gold", "./data/szk30/web_egyseges/", ".pure.gold"));
    //eval();
  }
}