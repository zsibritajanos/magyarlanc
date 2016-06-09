
package hu.u_szeged.converter.univ;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


public class Msd2UnivMorph {

  public static final String PREVERB_FILE = "./data/igekoto.txt";
  public static String PREVERB_FILE_JAR = "igekoto.txt";

  private static final List<String> PREVERBS = readPreverbs();

  private static List<String> readPreverbs() {
    BufferedReader bufferedReader = null;

    try {
      bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(PREVERB_FILE), "utf-8"));
    } catch (FileNotFoundException e) {
      try {
        bufferedReader = new BufferedReader(new InputStreamReader(Msd2UnivMorph.class.getClassLoader().getResourceAsStream(PREVERB_FILE_JAR), "utf-8"));
      } catch (Exception e2) {
        e.printStackTrace();
      }
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }

    List<String> lines = new LinkedList<>();
    String line;
    try {
      while ((line = bufferedReader.readLine()) != null) {
        if (!line.trim().equals("")) {
          lines.add(line);
        }
      }
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        bufferedReader.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    return lines;
  }


  public static Univ convert(String msd, String preverb) {

    Univ univ = null;

    //noun
    if (msd.charAt(0) == 'N') {
      univ = nounConvert(msd);
    }

    //adjective
    else if (msd.charAt(0) == 'A') {
      univ = adjConvert(msd);
    }

    //number
    else if (msd.charAt(0) == 'M') {
      univ = numConvert(msd);
    }

    //pronoun
    else if (msd.charAt(0) == 'P') {
      univ = pronConvert(msd);
    }

    //adverb
    else if (msd.charAt(0) == 'R') {
      univ = advConvert(msd, preverb);
    }

    //verb
    else if (msd.charAt(0) == 'V') {
      univ = verbConvert(msd);
    }

    //determiner
    else if (msd.charAt(0) == 'T') {
      univ = detConvert(msd);
    }

    //open class
    else if (msd.charAt(0) == 'O') {
      univ = openConvert(msd);
    }

    //other
    else if (msd.charAt(0) == 'S' || msd.charAt(0) == 'X' || msd.charAt(0) == 'C' || msd.charAt(0) == 'I'
            || msd.charAt(0) == 'E') {
      univ = otherConvert(msd);
    }

    //punctuation
    else if (msd.equals("K") || msd.equals(",") || msd.equals(";") ||
            msd.equals("?") || msd.equals(".") || msd.equals(":") ||
            msd.equals("-") || msd.equals("–") || msd.equals("!")) {
      univ = punctConvert(msd);
    }

    return univ;
  }

  private static Univ openConvert(String msd) {

    String pos = "";
    List<String> featlist = new ArrayList<String>();

    //Oh simplified to common noun
    if (msd.equals("Oh")) {
      msd = "Nc-sn";
      return nounConvert(msd);
    } else {
      char type = msd.charAt(1);

      //Oe
      if (type == 'e') {
        pos = "SYM";
        if (msd.length() >= 3) {
          char semtype = msd.charAt(2);
          featlist.add(semanticType(semtype));
        }
      }

      //Oi
      if (type == 'i') {
        pos = "PROPN";
      }

      //On
      if (type == 'n') {
        pos = "NUM";

        char semtype = msd.charAt(2);
        featlist.add(semanticType(semtype));
      }

      if (msd.length() >= 6) {
        char num = msd.charAt(4);
        featlist.add(number(num));

        char cas = msd.charAt(5);
        featlist.add(grammCase(cas));
      }

      if (msd.length() >= 11) {
        char numPsor = msd.charAt(9);
        char perPsor = msd.charAt(10);

        if (numPsor != '-' && perPsor != '-') {
          featlist.add(numberPsor(numPsor));
          featlist.add(personPsor(perPsor));
        }
      }

      if (msd.length() == 12) {
        char numPsed = msd.charAt(11);
        featlist.add(numberPsed(numPsed));
      }

      return sorting(pos, featlist);
    }
  }

  private static String semanticType(char semtype) {
    String feature = "";

    if (semtype == 'd') {
      feature = "NumType[sem]=Dot";
    }

    if (semtype == 'f') {
      feature = "NumType[sem]=Formula";
    }

    if (semtype == 'q') {
      feature = "NumType[sem]=Quotient";
    }

    if (semtype == 'r') {
      feature = "NumType[sem]=Result";
    }

    if (semtype == 's') {
      feature = "NumType[sem]=Signed";
    }

    if (semtype == 't') {
      feature = "NumType[sem]=Time";
    }

    if (semtype == 'p') {
      feature = "NumType[sem]=Percent";
    }

    if (semtype == 'g') {
      feature = "NumType[sem]=Grade";
    }

    if (semtype == 'm') {
      feature = "NumType[sem]=Measure";
    }

    if (semtype == 'o') {
      feature = "NumType[sem]=Other";
    }

    return feature;
  }

  private static Univ pronConvert(String msd) {
    String pos = "PRON";
    List<String> featlist = new ArrayList<>();

    //multiplication
    if (msd.charAt(5) == '6') {
      pos = "ADV";
      featlist.add("_");
    } else {
      char prontype = msd.charAt(1);
      featlist.add(pronType(prontype));

      if (prontype == 's') {
        featlist.add("Poss=Yes");
      }

      char pers = msd.charAt(2);
      featlist.add(person(pers));

      char num = msd.charAt(4);
      featlist.add(number(num));

      char cas = msd.charAt(5);
      featlist.add(grammCase(cas));

      if (msd.length() >= 16) {
        char numPsor = msd.charAt(6);
        char perPsor = msd.charAt(15);

        if (numPsor != '-' && perPsor != '-') {
          featlist.add(numberPsor(numPsor));
          featlist.add(personPsor(perPsor));
        }
      }

      if (msd.length() == 17) {
        char numPsed = msd.charAt(16);
        featlist.add(numberPsed(numPsed));
      }
    }


    return sorting(pos, featlist);
  }

  private static Univ advConvert(String msd, String preverb) {

    if (msd.equals("Rp")) {
      return preverbConvert(preverb);
    } else {
      String pos = "ADV";
      List<String> featlist = new ArrayList<String>();

      char advtype = msd.charAt(1);

      if (msd.length() == 2) {
        if (advtype == 'x') {
          featlist.add("_");
        } else {
          featlist.add(pronType(advtype));
        }
      }

      if (msd.length() >= 3) {
        char deg = msd.charAt(2);
        if (deg != '-') {
          featlist.add(degree(deg));
        }
        if (advtype != 'x') {
          featlist.add(pronType(advtype));
        }
      }

      return sorting(pos, featlist);
    }
  }

  private static Univ preverbConvert(String preverb) {

    String pos = prefixPOS(preverb.toLowerCase());

    List<String> featlist = prefixFeat(preverb.toLowerCase());
    String lemma = prefixLemma(preverb.toLowerCase());

    return sortingPreverb(pos, featlist, lemma);
  }

  private static String pronType(char type) {
    String feature = "";

    //Rp treated separately
    if (type == 'p') {
      feature = "PronType=Prs";
    }

    if (type == 'd') {
      feature = "PronType=Dem";
    }

    if (type == 'i') {
      feature = "PronType=Ind";
    }

    if (type == 's') {
      feature = "PronType=Prs";
    }

    if (type == 'q') {
      feature = "PronType=Int";
    }

    if (type == 'r') {
      feature = "PronType=Rel";
    }

    if (type == 'y') {
      feature = "PronType=Rcp";
    }

    if (type == 'g') {
      feature = "PronType=Tot";
    }

    //Rx treated separately
    if (type == 'x') {
      feature = "Reflex=Yes";
    }

    if (type == 'm') {
      feature = "PronType=Neg";
    }

    if (type == 'v') {
      feature = "VerbForm=Trans";
    }

    return feature;
  }

  private static Univ punctConvert(String msd) {
    String pos = "PUNCT";
    List<String> featlist = new ArrayList<String>();

    featlist.add("_");

    return sorting(pos, featlist);
  }

  private static Univ verbConvert(String msd) {
    String pos = "VERB";
    List<String> featlist = new ArrayList<String>();

    if (msd.charAt(1) == 'a') {
      pos = "AUX";
    }

    char asp = msd.charAt(1);
    char moodChar = msd.charAt(2);

    //mood, aspect, voice
    featlist = aspectMood(asp, moodChar, featlist);

    //tense
    if (msd.length() >= 4) {
      char tenseChar = msd.charAt(3);
      featlist.add(tense(tenseChar));
    }

    //number, person
    if (msd.length() >= 5) {
      char per = msd.charAt(4);
      featlist.add(person(per));

      char num = msd.charAt(5);
      featlist.add(number(num));
    }

    //definiteness
    if (msd.length() == 10) {
      char def = msd.charAt(9);
      featlist.add(definite(def));
    }

    return sorting(pos, featlist);
  }

  private static String tense(char tenseChar) {
    String feature = "";
    if (tenseChar == 'p') {
      feature = "Tense=Pres";
    }

    if (tenseChar == 's') {
      feature = "Tense=Past";
    }

    return feature;
  }

  private static String person(char per) {
    String feature = "";
    if (per == '1') {
      feature = "Person=1";
    }

    if (per == '2') {
      feature = "Person=2";
    }

    if (per == '3') {
      feature = "Person=3";
    }

    return feature;
  }

  private static List<String> aspectMood(char asp, char moodChar, List<String> featlist) {

    //infinitives
    if (moodChar == 'n') {
      featlist.add("VerbForm=Inf");
    } else {
      featlist.add("VerbForm=Fin");
    }

    //main verbs
    if (asp == 'a' || asp == 'm') {
      featlist.add("Voice=Act");

      if (moodChar == 'i') {
        featlist.add("Mood=Ind");
      }

      if (moodChar == 'm') {
        featlist.add("Mood=Imp");
      }

      if (moodChar == 'c') {
        featlist.add("Mood=Cnd");
      }
    }

    //frequentative
    if (asp == 'f') {

      featlist.add("Aspect=Freq");
      featlist.add("Voice=Act");

      if (moodChar == 'i') {
        featlist.add("Mood=Ind");
      }

      if (moodChar == 'm') {
        featlist.add("Mood=Imp");
      }

      if (moodChar == 'c') {
        featlist.add("Mood=Cnd");
      }
    }

    //causative
    if (asp == 's') {

      featlist.add("Voice=Cau");

      if (moodChar == 'i') {
        featlist.add("Mood=Ind");
      }

      if (moodChar == 'm') {
        featlist.add("Mood=Imp");
      }

      if (moodChar == 'c') {
        featlist.add("Mood=Cnd");
      }
    }

    //modal
    if (asp == 'o') {

      featlist.add("Voice=Act");

      if (moodChar == 'i') {
        featlist.add("Mood=Pot");
      }

      if (moodChar == 'm') {
        featlist.add("Mood=Imp,Pot");
      }

      if (moodChar == 'c') {
        featlist.add("Mood=Cnd,Pot");
      }

      if (moodChar == 'n') {
        featlist.add("Mood=Pot");
      }
    }

    //combinations
    //modal + frequentative
    if (asp == '1') {

      featlist.add("Voice=Act");
      featlist.add("Aspect=Freq");

      if (moodChar == 'i') {
        featlist.add("Mood=Pot");
      }

      if (moodChar == 'm') {
        featlist.add("Mood=Imp,Pot");
      }

      if (moodChar == 'c') {
        featlist.add("Mood=Cnd,Pot");
      }

      if (moodChar == 'n') {
        featlist.add("Mood=Pot");
      }
    }

    //modal + causative
    if (asp == '2') {

      featlist.add("Voice=Cau");

      if (moodChar == 'i') {
        featlist.add("Mood=Pot");
      }

      if (moodChar == 'm') {
        featlist.add("Mood=Imp,Pot");
      }

      if (moodChar == 'c') {
        featlist.add("Mood=Cnd,Pot");
      }

      if (moodChar == 'n') {
        featlist.add("Mood=Pot");
      }
    }

    //frequentative + causative
    if (asp == '3') {

      featlist.add("Voice=Cau");
      featlist.add("Aspect=Freq");

      if (moodChar == 'i') {
        featlist.add("Mood=Ind");
      }

      if (moodChar == 'm') {
        featlist.add("Mood=Imp");
      }

      if (moodChar == 'c') {
        featlist.add("Mood=Cnd");
      }
    }

    //modal + causative + frequentative
    if (asp == '4') {

      featlist.add("Voice=Cau");
      featlist.add("Aspect=Freq");

      if (moodChar == 'i') {
        featlist.add("Mood=Pot");
      }

      if (moodChar == 'm') {
        featlist.add("Mood=Imp,Pot");
      }

      if (moodChar == 'c') {
        featlist.add("Mood=Cnd,Pot");
      }

      if (moodChar == 'n') {
        featlist.add("Mood=Pot");
      }
    }

    return featlist;
  }

  private static Univ detConvert(String msd) {
    String pos = "DET";
    List<String> featlist = new ArrayList<String>();

    char def = msd.charAt(1);

    featlist.add(definite(def));
    featlist.add("PronType=Art");

    return sorting(pos, featlist);
  }

  private static String definite(char def) {
    String feature = "";
    if (def == 'i' || def == 'n') {
      feature = "Definite=Ind";
    }

    if (def == 'f' || def == 'y') {
      feature = "Definite=Def";
    }

    if (def == '2') {
      feature = "Definite=2";
    }

    return feature;
  }

  private static Univ otherConvert(String msd) {
    String pos = "";

    if (msd.charAt(0) == 'S') {
      pos = "ADP";
    }

    if (msd.charAt(0) == 'X') {
      pos = "X";
    }

    if (msd.charAt(0) == 'I') {
      pos = "INTJ";
    }

    if (msd.charAt(0) == 'E') {
      pos = "SYM";
    }

    if (msd.startsWith("Cc")) {
      pos = "CONJ";
    }

    if (msd.startsWith("Cs")) {
      pos = "SCONJ";
    }

    List<String> featlist = new ArrayList<String>();
    featlist.add("_");

    return sorting(pos, featlist);
  }

  private static Univ nounConvert(String msd) {
    String pos = "NOUN";
    List<String> featlist = new ArrayList<String>();

    //multiplication
    if (msd.charAt(4) == '6') {
      pos = "ADV";
      featlist.add("_");
    } else {
      if (msd.charAt(1) == 'p') {
        pos = "PROPN";
      }

      //number
      char num = msd.charAt(3);
      featlist.add(number(num));

      //case
      char cas = msd.charAt(4);
      featlist.add(grammCase(cas));

      //number and person of possessor
      if (msd.length() >= 10) {
        char numPsor = msd.charAt(8);
        char perPsor = msd.charAt(9);

        if (numPsor != '-' && perPsor != '-') {
          featlist.add(numberPsor(numPsor));
          featlist.add(personPsor(perPsor));
        }
      }

      //number of possessed
      if (msd.length() == 11) {
        char numPsed = msd.charAt(10);
        featlist.add(numberPsed(numPsed));
      }
    }

    return sorting(pos, featlist);
  }

  private static Univ adjConvert(String msd) {
    String pos = "ADJ";
    List<String> featlist = new ArrayList<String>();

    //participles
    char part = msd.charAt(1);
    if (part != 'f') {
      featlist.add(participle(part));
    }

    //degree
    char deg = msd.charAt(2);
    featlist.add(degree(deg));

    //number
    char num = msd.charAt(4);
    featlist.add(number(num));

    //case
    char cas = msd.charAt(5);
    featlist.add(grammCase(cas));

    //number and person of possessor
    if (msd.length() >= 12) {
      char numPsor = msd.charAt(10);
      char perPsor = msd.charAt(11);

      if (numPsor != '-' && perPsor != '-') {
        featlist.add(numberPsor(numPsor));
        featlist.add(personPsor(perPsor));
      }
    }

    //number of possessed
    if (msd.length() == 13) {
      char numPsed = msd.charAt(12);
      featlist.add(numberPsed(numPsed));
    }

    return sorting(pos, featlist);
  }

  private static Univ numConvert(String msd) {
    String pos = "NUM";
    List<String> featlist = new ArrayList<String>();

    //multiplication
    if (msd.charAt(4) == '6') {
      pos = "ADV";
      featlist.add("_");
    } else {

      char numtype = msd.charAt(1);

      //ordinal numbers
      if (numtype == 'o') {
        pos = "ADJ";
        featlist.add("NumType=Ord");
      }

      //cardinal numbers
      if (numtype == 'c') {
        featlist.add("NumType=Card");
      }

      //fraction numbers
      if (numtype == 'f') {
        featlist.add("NumType=Frac");
      }

      //distributives
      if (numtype == 'd') {
        featlist.add("NumType=Dist");
      }

      //number
      char num = msd.charAt(3);
      featlist.add(number(num));

      //case
      char cas = msd.charAt(4);
      featlist.add(grammCase(cas));

      //number and person of possessor
      if (msd.length() >= 12) {
        char numPsor = msd.charAt(10);
        char perPsor = msd.charAt(11);

        if (numPsor != '-' && perPsor != '-') {
          featlist.add(numberPsor(numPsor));
          featlist.add(personPsor(perPsor));
        }
      }

      //number of possessed
      if (msd.length() == 13) {
        char numPsed = msd.charAt(12);
        featlist.add(numberPsed(numPsed));
      }
    }

    return sorting(pos, featlist);
  }


  private static String degree(char deg) {
    String feature = "";
    if (deg == 'p') {
      feature = "Degree=Pos";
    }

    if (deg == 'c') {
      feature = "Degree=Cmp";
    }

    if (deg == 's') {
      feature = "Degree=Sup";
    }

    if (deg == 'e') {
      feature = "Degree=Abs";
    }

    return feature;
  }

  private static String participle(char part) {
    String feature = "";
    if (part == 'p') {
      feature = "VerbForm=PartPres";
    }

    if (part == 's') {
      feature = "VerbForm=PartPast";
    }

    if (part == 'u') {
      feature = "VerbForm=PartFut";
    }

    return feature;
  }

  private static String numberPsed(char numPsed) {
    String feature = "";
    if (numPsed == 's') {
      feature = "Number[psed]=Sing";
    }

    if (numPsed == 'p') {
      feature = "Number[psed]=Plur";
    }

    return feature;
  }

  private static String personPsor(char perPsor) {
    String feature = "";
    if (perPsor == '1') {
      feature = "Person[psor]=1";
    }

    if (perPsor == '2') {
      feature = "Person[psor]=2";
    }

    if (perPsor == '3') {
      feature = "Person[psor]=3";
    }

    return feature;
  }

  private static String numberPsor(char numPsor) {
    String feature = "";
    if (numPsor == 's') {
      feature = "Number[psor]=Sing";
    }

    if (numPsor == 'p') {
      feature = "Number[psor]=Plur";
    }
    return feature;
  }

  private static Univ sorting(String pos, List<String> featlist) {

    StringBuffer features = new StringBuffer();
    Collections.sort(featlist, String.CASE_INSENSITIVE_ORDER);

    for (int i = 0; i < featlist.size() - 1; i++) {
      features.append(featlist.get(i) + "|");
    }

    features.append(featlist.get(featlist.size() - 1) + "\n");

    return new Univ(pos, features.toString());
  }


  private static Univ sortingPreverb(String pos, List<String> featlist, String lemma) {

    StringBuffer features = new StringBuffer();
    Collections.sort(featlist);

    for (int i = 0; i < featlist.size() - 1; i++) {
      features.append(featlist.get(i) + "|");
    }
    features.append(featlist.get(featlist.size() - 1) + "\n");

    return new Univ(lemma, pos, features.toString());
  }

  private static String grammCase(char cas) {
    String feature = "";
    if (cas == 'n') {
      feature = "Case=Nom";
    }

    if (cas == 'a') {
      feature = "Case=Acc";
    }

    if (cas == 'd') {
      feature = "Case=Dat";
    }

    if (cas == 'g') {
      feature = "Case=Gen";
    }

    if (cas == 'i') {
      feature = "Case=Ins";
    }

    if (cas == 'x') {
      feature = "Case=Ill";
    }

    if (cas == '2') {
      feature = "Case=Ine";
    }

    if (cas == 'e') {
      feature = "Case=Ela";
    }

    if (cas == 't') {
      feature = "Case=All";
    }

    if (cas == '3') {
      feature = "Case=Ade";
    }

    if (cas == 'b') {
      feature = "Case=Abl";
    }

    if (cas == 's') {
      feature = "Case=Sub";
    }

    if (cas == 'p') {
      feature = "Case=Sup";
    }

    if (cas == 'h') {
      feature = "Case=Del";
    }

    if (cas == '9') {
      feature = "Case=Ter";
    }

    if (cas == 'w') {
      feature = "Case=Ess";
    }

    if (cas == 'f') {
      feature = "Case=Abs";
    }

    if (cas == 'm') {
      feature = "Case=Tem";
    }

    if (cas == 'c') {
      feature = "Case=Cau";
    }

    if (cas == 'q') {
      feature = "Case=Com";
    }

    if (cas == 'y') {
      feature = "Case=Tra";
    }

    if (cas == 'u') {
      feature = "Case=Dis";
    }

    if (cas == 'l') {
      feature = "Case=Loc";
    }

    return feature;
  }

  private static String number(char num) {
    String feature = "";
    if (num == 's') {
      feature = "Number=Sing";
    }

    if (num == 'p') {
      feature = "Number=Plur";
    }
    return feature;
  }

  private static String prefixLemma(String origWord) {
    String newLemma = "";
    for (String linePreverb : PREVERBS) {
      if (!linePreverb.equals("")) {
        String[] preverbLineSplit = linePreverb.split("\t");

        if (preverbLineSplit.length != 6) {
          continue;
        }

        String form = preverbLineSplit[2];
        String lemma = preverbLineSplit[3];

        if (origWord.equals(form)) {
          newLemma = lemma;
        }
      }
    }
    return newLemma;
  }

  private static List<String> prefixFeat(String origWord) {

    String newFeat = "";

    for (String preverb : PREVERBS) {

      if (!preverb.equals("")) {
        String[] preverbLineSplit = preverb.split("\t");

        if (preverbLineSplit.length != 6) {
          continue;
        }

        String form = preverbLineSplit[2];
        String feature = preverbLineSplit[5];

        if (origWord.equals(form)) {
          newFeat = feature;
        }
      }
    }

    List<String> featlist = new ArrayList<>();

    if (newFeat.equals("_") || newFeat.equals("PronType=Default|Degree=None")) {
      featlist.add("_");
    } else {
      String[] feats = newFeat.split("\\|");

      for (int i = 0; i < feats.length; i++) {
        if (!feats[i].endsWith("None") && !feats[i].endsWith("Default")) {
          featlist.add(feats[i]);
        }
      }
    }

    return featlist;
  }

  private static String prefixPOS(String origWord) {

    String newPOS = "";


    for (String preverb : PREVERBS) {
      if (!preverb.equals("")) {
        String[] preverbLineSplit = preverb.split("\t");

        if (preverbLineSplit.length != 6) {
          continue;
        }

        String form = preverbLineSplit[2];
        String pos = preverbLineSplit[4];

        if (origWord.equals(form)) {
          newPOS = pos;
        }
      }
    }
    return newPOS;
  }

  public static void main(String[] args) {


    String line = "nyolc nyolc Mc-snl";

//    for (MorAna morAna : HunLemMor.getMorphologicalAnalyses("valahányszor")) {
//      System.out.println(morAna + "\t" + convert(morAna.getMsd(), ""));
//    }

//    String msd = "Rp";
//    String preverb = "alá";



    // non valid : NUM Case=Nom|NumType=Card|Number=Sing
    // valid :     NUM Case=Nom|Number=Sing|NumType=Card

    System.out.println(convert("Mc-snl", ""));
  }
}
