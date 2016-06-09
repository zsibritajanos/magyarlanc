package hu.u_szeged.pos.converter;

import hu.u_szeged.config.Config;
import hu.u_szeged.magyarlanc.resource.ResourceHolder;
import hu.u_szeged.magyarlanc.resource.Util;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class MSDToCoNLLFeatures {
  /**
   * cache for the extracted features
   */
  private Map<String, String> cache = null;

  public MSDToCoNLLFeatures() {
    this.setCache(new HashMap<String, String>());
  }

  private void setCache(Map<String, String> cache) {
    this.cache = cache;
  }

  public Map<String, String> getCache() {
    return cache;
  }

  /**
   * extract noun
   */
  private String parseN(String MSDCode) {

    int length = MSDCode.length();
    StringBuffer features = new StringBuffer();

    // 1 SubPOS
    if (MSDCode.charAt(1) == '-') {
      features.append("SubPOS=none");
    } else {
      features.append("SubPOS=" + MSDCode.charAt(1));
    }

    // 2 (not used)

    // 3 Num
    if (MSDCode.charAt(3) == '-') {
      features.append("|Num=none");
    } else {
      features.append("|Num=" + MSDCode.charAt(3));
    }

    // 4 Cas
    if (MSDCode.charAt(4) == '-') {
      features.append("|Cas=none");
    } else {
      features.append("|Cas=" + MSDCode.charAt(4));
    }
    if (length == 5) {
      features.append("|NumP=none|PerP=none|NumPd=none");
      return features.toString();
    }

    // 5 (not used)

    // 6 (not used)

    // 7 (not used)

    // 8 NumP
    if (MSDCode.charAt(8) == '-') {
      features.append("|NumP=none");
    } else {
      features.append("|NumP=" + MSDCode.charAt(8));
    }
    if (length == 9) {
      features.append("|PerP=none|NumPd=none");
      return features.toString();
    }

    // 9 PerP
    if (MSDCode.charAt(9) == '-') {
      features.append("|PerP=none");
    } else {
      features.append("|PerP=" + MSDCode.charAt(9));
    }
    if (length == 10) {
      features.append("|NumPd=none");
      return features.toString();
    }

    // 10 NumPd
    if (MSDCode.charAt(10) == '-') {
      features.append("|NumPd=none");
    } else {
      features.append("|NumPd=" + MSDCode.charAt(10));
    }

    return features.toString();
  }

  /**
   * extract verb
   */
  private String parseV(String MSDCode) {
    int length = MSDCode.length();
    StringBuffer features = new StringBuffer();

    // 1 SubPOS
    if (MSDCode.charAt(1) == '-') {
      features.append("SubPOS=none");
    } else {
      features.append("SubPOS=" + MSDCode.charAt(1));
    }

    // 2 Mood
    if (MSDCode.charAt(2) == '-') {
      features.append("|Mood=none");
    } else {
      features.append("|Mood=" + MSDCode.charAt(2));
    }
    if (length == 3) {
      if (MSDCode.charAt(2) != 'n') {
        features.append("|Tense=none");
      }
      features.append("|Per=none|Num=none");
      if (MSDCode.charAt(2) != 'n') {
        features.append("|Def=none");
      }
      return features.toString();
    }

    // 3 Tense (if Mood != n)
    if (MSDCode.charAt(2) != 'n') {
      if (MSDCode.charAt(3) == '-') {
        features.append("|Tense=none");
      } else {
        features.append("|Tense=" + MSDCode.charAt(3));
      }
    }
    if (length == 4) {
      features.append("|Per=none|Num=none");
      if (MSDCode.charAt(2) != 'n') {
        features.append("|Def=none");
      }
      return features.toString();
    }

    // 4 Per
    if (MSDCode.charAt(4) == '-') {
      features.append("|Per=none");
    } else {
      features.append("|Per=" + MSDCode.charAt(4));
    }
    if (length == 5) {
      features.append("|Num=none");
      if (MSDCode.charAt(2) != 'n') {
        features.append("|Def=none");
      }
      return features.toString();
    }

    // 5 Num
    if (MSDCode.charAt(5) == '-') {
      features.append("|Num=none");
    } else {
      features.append("|Num=" + MSDCode.charAt(5));
    }
    if (length == 6) {
      if (MSDCode.charAt(2) != 'n') {
        features.append("|Def=none");
      }
      return features.toString();
    }

    // 6 Def
    if (length == 7) {
      if (MSDCode.charAt(2) != 'n') {
        features.append("|Def=none");
      }
      return features.toString();
    }

    // 7 (not used)

    // 8 (not used)

    // 9 Def
    if (MSDCode.charAt(2) != 'n') {
      if (MSDCode.charAt(9) == '-') {
        features.append("|Def=none");
      } else {
        features.append("|Def=" + MSDCode.charAt(9));
      }
    }
    if (length == 10) {
      return features.toString();
    }

    // 10 (not used)
    if (length == 11)
      return features.toString();

    return features.toString();
  }

  /**
   * extract adjective
   */
  private String parseA(String MSDCode) {
    int length = MSDCode.length();
    StringBuffer features = new StringBuffer();

    // 1 SubPOS
    if (MSDCode.charAt(1) == '-') {
      features.append("SubPOS=none");
    } else {
      features.append("SubPOS=" + MSDCode.charAt(1));
    }

    // 2 Deg
    if (MSDCode.charAt(2) == '-') {
      features.append("|Deg=none");
    } else {
      features.append("|Deg=" + MSDCode.charAt(2));
    }

    // 3 (not used)

    // 4 Num
    if (MSDCode.charAt(4) == '-') {
      features.append("|Num=none");
    } else {
      features.append("|Num=" + MSDCode.charAt(4));
    }

    // 5 Cas
    if (MSDCode.charAt(5) == '-') {
      features.append("|Cas=none");
    } else {
      features.append("|Cas=" + MSDCode.charAt(5));
    }
    if (length == 6) {
      features.append("|NumP=none|PerP=none|NumPd=none");
      return features.toString();
    }

    // 6 (not used)

    // 7 (not used)

    // 8 (not used)

    // 9 (not used)

    // 10 NumP
    if (MSDCode.charAt(10) == '-') {
      features.append("|NumP=none");
    } else {
      features.append("|NumP=" + MSDCode.charAt(10));
    }
    if (length == 11) {
      features.append("|PerP=none|NumPd=none");
      return features.toString();
    }

    // 11 PerP
    if (MSDCode.charAt(11) == '-') {
      features.append("|PerP=none");
    } else {
      features.append("|PerP=" + MSDCode.charAt(11));
    }
    if (length == 12) {
      features.append("|NumPd=none");
      return features.toString();
    }

    // 12 NumPd
    if (MSDCode.charAt(12) == '-') {
      features.append("|NumPd=none");
    } else {
      features.append("|NumPd=" + MSDCode.charAt(12));
    }

    return features.toString();
  }

  /**
   * extract pronoun
   */
  private String parseP(String MSDCode) {
    int length = MSDCode.length();
    StringBuffer features = new StringBuffer();

    // 1 SubPOS
    if (MSDCode.charAt(1) == '-') {
      features.append("SubPOS=none");
    } else {
      features.append("SubPOS=" + MSDCode.charAt(1));
    }

    // 2 Per
    if (MSDCode.charAt(2) == '-') {
      features.append("|Per=none");
    } else {
      features.append("|Per=" + MSDCode.charAt(2));
    }

    // 3 (not used)

    // 4 Num
    if (MSDCode.charAt(4) == '-') {
      features.append("|Num=none");
    } else {
      features.append("|Num=" + MSDCode.charAt(4));
    }

    // 5 Cas
    if (MSDCode.charAt(5) == '-') {
      features.append("|Cas=none");
    } else {
      features.append("|Cas=" + MSDCode.charAt(5));
    }
    if (length == 6) {
      features.append("|NumP=none|PerP=none|NumPd=none");
      return features.toString();
    }

    // 6 NumP
    if (MSDCode.charAt(6) == '-') {
      features.append("|NumP=none");
    } else {
      features.append("|NumP=" + MSDCode.charAt(6));
    }
    if (length == 7) {
      features.append("|PerP=none|NumPd=none");
      return features.toString();
    }

    // 7 (not used)

    // 8 (not used)

    // 9 (not used)

    // 10 (not used)

    // 11 (not used)

    // 12 (not used)

    // 13 (not used)

    // 14 (not used)

    // 15 PerP
    if (MSDCode.charAt(15) == '-') {
      features.append("|PerP=none");
    } else {
      features.append("|PerP=" + MSDCode.charAt(15));
    }
    if (length == 16) {
      features.append("|NumPd=none");
      return features.toString();
    }

    // 16 NumPd
    if (MSDCode.charAt(16) == '-') {
      features.append("|NumPd=none");
    } else {
      features.append("|NumPd=" + MSDCode.charAt(16));
    }

    return features.toString();
  }

  /**
   * extract article
   */
  private String parseT(String MSDCode) {

    // 1 SubPOS
    if (MSDCode.charAt(1) == '-') {
      return "SubPOS=none";
    } else {
      return "SubPOS=" + MSDCode.charAt(1);
    }
  }

  /**
   * extract adverb
   */
  private String parseR(String MSDCode) {
    int length = MSDCode.length();
    StringBuffer features = new StringBuffer();

    // 1 SubPOS
    if (MSDCode.charAt(1) == '-') {
      features.append("SubPOS=none");
    } else {
      features.append("SubPOS=" + MSDCode.charAt(1));
    }
    if (length == 2) {
      features.append("|Deg=none");
      if (MSDCode.charAt(1) == 'l') {
        features.append("|Num=none|Per=none");
      }
      return features.toString();
    }

    // 2 Deg
    if (MSDCode.charAt(2) == '-') {
      features.append("|Deg=none");
    } else {
      features.append("|Deg=" + MSDCode.charAt(2));
    }
    if (length == 3) {
      if (MSDCode.charAt(1) == 'l') {
        features.append("|Num=none|Per=none");
      }
      return features.toString();
    }

    // 3 (not used)

    // 4 Num
    if (MSDCode.charAt(1) == 'l') {

      if (MSDCode.charAt(4) == '-') {
        features.append("|Num=none");
      } else {
        features.append("|Num=" + MSDCode.charAt(4));
      }
    }
    if (length == 5) {
      if (MSDCode.charAt(1) == 'l') {
        features.append("|Per=none");
      }
      return features.toString();
    }

    // 5 Per
    if (MSDCode.charAt(1) == 'l') {
      if (MSDCode.charAt(5) == '-') {
        features.append("|Per=none");
      } else {
        features.append("|Per=" + MSDCode.charAt(5));
      }
    }

    return features.toString();
  }

  /**
   * extract adposition
   */
  private String parseS(String MSDCode) {

    // 1 SubPOS
    if (MSDCode.charAt(1) == '-') {
      return "SubPOS=none";
    } else {
      return "SubPOS=" + MSDCode.charAt(1);
    }
  }

  /**
   * extract conjucion
   */
  private String parseC(String MSDCode) {

    StringBuffer features = new StringBuffer();

    // 1 SubPOS
    if (MSDCode.charAt(1) == '-') {
      features.append("SubPOS=none");
    } else {
      features.append("SubPOS=" + MSDCode.charAt(1));
    }

    // 2 Form
    if (MSDCode.charAt(2) == '-') {
      features.append("|Form=none");
    } else {
      features.append("|Form=" + MSDCode.charAt(2));
    }

    // 3 Coord
    if (MSDCode.charAt(3) == '-') {
      features.append("|Coord=none");
    } else {
      features.append("|Coord=" + MSDCode.charAt(3));
    }
    return features.toString();
  }

  /**
   * extract numeral
   */
  private String parseM(String MSDCode) {
    int length = MSDCode.length();
    StringBuffer features = new StringBuffer();

    // 1 SubPOS
    if (MSDCode.charAt(1) == '-') {
      features.append("SubPOS=none");
    } else {
      features.append("SubPOS=" + MSDCode.charAt(1));
    }

    // 2 (not used)

    // 3 Num
    if (MSDCode.charAt(3) == '-') {
      features.append("|Num=none");
    } else {
      features.append("|Num=" + MSDCode.charAt(3));
    }

    // 4 Cas
    if (MSDCode.charAt(4) == '-') {
      features.append("|Cas=none");
    } else {
      features.append("|Cas=" + MSDCode.charAt(4));
    }

    // 5 Form
    if (MSDCode.charAt(5) == '-') {
      features.append("|Form=none");
    } else {
      features.append("|Form=" + MSDCode.charAt(5));
    }
    if (length == 6) {
      features.append("|NumP=none|PerP=none|NumPd=none");
      return features.toString();
    }

    // 6 (not used)

    // 7 (not used)

    // 8 (not used)

    // 9 (not used)

    // 10 NumP
    if (MSDCode.charAt(10) == '-') {
      features.append("|NumP=none");
    } else {
      features.append("|NumP=" + MSDCode.charAt(10));
    }
    if (length == 11) {
      features.append("|PerP=none|NumPd=none");
      return features.toString();
    }

    // 11 PerP
    if (MSDCode.charAt(11) == '-') {
      features.append("|PerP=none");
    } else {
      features.append("|PerP=" + MSDCode.charAt(11));
    }
    if (length == 12) {
      features.append("|NumPd=none");
      return features.toString();
    }

    // 12 NumPd
    if (MSDCode.charAt(12) == '-') {
      features.append("|NumPd=none");
    } else {
      features.append("|NumPd=" + MSDCode.charAt(12));
    }

    return features.toString();
  }

  /**
   * extract interjection
   */
  private String parseI(String msdCode) {
    int length = msdCode.length();

    if (length == 1) {
      return "_";
    }
    // 1 SubPOS
    return "SubPOS=" + msdCode.charAt(1);
  }

  /**
   * extract other/open
   */
  private String parseO(String MSDCode) {
    int length = MSDCode.length();
    StringBuffer features = new StringBuffer();

    // 1 SubPOS
    if (MSDCode.charAt(1) == '-') {
      features.append("SubPOS=none");
    } else {
      features.append("SubPOS=" + MSDCode.charAt(1));
    }
    if (length == 2) {
      features.append("|Num=none|Cas=none|NumP=none|PerP=none|NumPd=none");
      return features.toString();
    }

    // 2 Type (if SubPOS=e|d|n)
    if (MSDCode.charAt(1) == 'e' || MSDCode.charAt(1) == 'd'
        || MSDCode.charAt(1) == 'n') {
      if (MSDCode.charAt(1) == '-') {
        features.append("|Type=none");
      } else {
        features.append("|Type=" + MSDCode.charAt(2));
      }
    }
    if (length == 3) {
      features.append("|Num=none|Cas=none|NumP=none|PerP=none|NumPd=none");
      return features.toString();
    }

    // 3 (not used)

    // 4 Num
    if (MSDCode.charAt(4) == '-') {
      features.append("|Num=none");
    } else {
      features.append("|Num=" + MSDCode.charAt(4));
    }
    if (length == 5) {
      features.append("|Cas=none|NumP=none|PerP=none|NumPd=none");
      return features.toString();
    }

    // 5 Cas
    if (MSDCode.charAt(5) == '-') {
      features.append("|Cas=none");
    } else {
      features.append("|Cas=" + MSDCode.charAt(5));
    }
    if (length == 6) {
      features.append("|NumP=none|PerP=none|NumPd=none");
      return features.toString();
    }

    // 6 (not used)

    // 7 (not used)

    // 8 (not used)

    // 9 NumP
    if (MSDCode.charAt(9) == '-') {
      features.append("|NumP=none");
    } else {
      features.append("|NumP=" + MSDCode.charAt(9));
    }
    if (length == 10) {
      features.append("|PerP=none|NumPd=none");
      return features.toString();
    }

    // 10 PerP
    if (MSDCode.charAt(10) == '-') {
      features.append("|PerP=none");
    } else {
      features.append("|PerP=" + MSDCode.charAt(10));
    }
    if (length == 11) {
      features.append("|NumPd=none");
      return features.toString();
    }

    // 11 NumPd
    if (MSDCode.charAt(11) == '-') {
      features.append("|NumPd=none");
    } else {
      features.append("|NumPd=" + MSDCode.charAt(11));
    }

    return features.toString();
  }

  /**
   * get the features of the given lemma/MSD pair
   */
  public String convert(String lemma, String MSDCode) {

    if (lemma.equals("_"))
      return "_";

    // relevant punctation
    if (ResourceHolder.getPunctations().contains(lemma)) {
      return "_";
    }

    // non relevant punctation
    if (MSDCode.equals("K")) {
      return "SubPOS=" + lemma;
    }

    // cache
    if (this.getCache().containsKey(MSDCode)) {
      return this.getCache().get(MSDCode);
    }

    String features = null;

    if (MSDCode.length() == 1) {

    }

    switch (MSDCode.charAt(0)) {
    // noun
    case 'N':
      features = parseN(MSDCode);
      break;
    // verb
    case 'V':
      features = parseV(MSDCode);
      break;
    // adjective
    case 'A':
      features = parseA(MSDCode);
      break;
    // pronoun
    case 'P':
      features = parseP(MSDCode);
      break;
    // article
    case 'T':
      features = parseT(MSDCode);
      break;
    // adverb
    case 'R':
      features = parseR(MSDCode);
      break;
    // adposition
    case 'S':
      features = parseS(MSDCode);
      break;
    // conjuction
    case 'C':
      features = parseC(MSDCode);
      break;
    // numeral
    case 'M':
      features = parseM(MSDCode);
      break;
    // interjection
    case 'I':
      features = parseI(MSDCode);
      break;
    // open/other
    case 'O':
      features = parseO(MSDCode);
      break;
    // residual
    case 'X':
      features = "_";
      break;
    // abbrevation
    case 'Y':
      features = "_";
      break;
    //
    case 'Z':
      features = "_";
      break;
    // punctation
    case 'K':
      features = "SubPOS=" + lemma;
      break;

    // punctation
    case 'E':
      features = "_";
      break;
    }

    this.getCache().put(MSDCode, features);

    if (features == null)
      return "_";

    return features;
  }

  /*
   * write the cache to file
   */
  public void writeCacheToFile(String file) {
    Map<String, String> sorted = null;
    sorted = new TreeMap<String, String>(this.getCache());

    File f = new File(file);
    try {
      System.err.print("\nWriting MSDToCoNLLFeatures cache to "
          + f.getCanonicalPath());
    } catch (IOException e) {
      e.printStackTrace();
    }

    Util.writeMapToFile(sorted, f, Config.getInstance().getEncoding());
  }

  public String[] convertArray(String[] forms, String[] MSDs) {
    String features[] = null;
    features = new String[forms.length];

    for (int i = 0; i < forms.length; ++i) {
      features[i] = convert(forms[i], MSDs[i]);
    }

    return features;
  }

  public static void main(String[] args) {
    StringBuffer sb = new StringBuffer();
    Path p = Paths.get("d:/data/tweet-hu/tweet_hu_1.txt");
    try {
      List<String> lines = Files.readAllLines(p, Charset.forName("utf-8"));
      for (String l : lines) {
        sb.append(l.trim() + "\t");
        String[] split = l.split("\t");
        if (split.length > 2) {
          sb.append(split[2].charAt(0) + "\t");
          // conll
          sb.append(new MSDToCoNLLFeatures().convert(split[1],
              split[2]));
        }
          sb.append("\n");          
        
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    
    try {
      Files.write(Paths.get("d:/data/tweet-hu/tweet_hu_1.txt.conll"), sb.toString().getBytes());
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
}
