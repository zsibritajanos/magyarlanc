package hu.u_szeged.pos.converter;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utils for KR codes
 * 
 * @author zsjanos
 * 
 */
public class KRUtils {
  
  /**
   * possible KR part of speech tags
   * 
   * @author zsjanos
   * 
   */
  public enum KRPOS {
    VERB, NOUN, ADJ, NUM, ADV, PREV, ART, POSTP, UTT_INT, DET, CONJ, ONO, PREP, X;
  }
  
  /**
   * Search for a pattern in a text by group number.
   * 
   * @param text
   * @param pattern
   * @param group
   * @return
   */
  private static String findPattern(String text, String pattern, int group) {
    Pattern p = null;
    Matcher m = null;
    MatchResult mr = null;
    
    p = Pattern.compile(pattern);
    m = p.matcher(text);
    m.find();
    mr = m.toMatchResult();
    
    return mr.group(group);
  }
  
  /**
   * 
   * Search for the first occurence of the pattern in the text.
   * 
   * @param text
   * @param pattern
   * @return
   */
  private static String findPattern(String text, String pattern) {
    return findPattern(text, pattern, 1);
  }
  
  private static List<String> findPatterns(String text, String pattern) {
    Pattern p = null;
    Matcher m = null;
    MatchResult mr = null;
    boolean find = false;
    List<String> found = null;
    found = new LinkedList<String>();
    
    p = Pattern.compile(pattern);
    m = p.matcher(text);
    find = m.find();
    while (find) {
      mr = m.toMatchResult();
      found.add(mr.group(1));
      find = m.find();
    }
    
    return found;
  }
  
  public static String getRoot(String morph) {
    
    // t�bb
    if (morph.startsWith("$sok/NUM[COMPAR]/NUM<CAS<")) {
      return "$több/NUM<CAS<ACC>>";
    }
    
    // legt�bb
    if (morph.startsWith("$sok/NUM[SUPERLAT]/NUM<CAS<")) {
      return "$legtöbb/NUM<CAS<ACC>>";
    }
    
    // legeslegt�bb
    if (morph.startsWith("$sok/NUM[SUPER-SUPERLAT]/NUM<CAS<")) {
      return "$legeslegtöbb/NUM<CAS<ACC>>";
    }
    
    String root = null;
    String vegsoto = null;
    String igekoto = "";
    String[] tovek;
    boolean ikes = false;
    
    if (!morph.contains("/")) {
      return morph;
    } else {
      
      // igekoto
      if (morph.contains("/PREV+")) {
        igekoto = morph.split("/PREV\\+")[0];
        morph = morph.split("/PREV\\+")[1];
      }
      
      tovek = morph.split("/");
      tovek = preProcess(tovek);
      
      vegsoto = findPatterns(tovek[0], "^([^\\(\\/]*)").get(0);
      
      List<String> feladatok;
      
      if (tovek.length > 2) {
        for (int i = 0; i < tovek.length - 1; i++) {
          
          if (tovek[i].matches(".*\\(.*\\).*")) {
            
            feladatok = findPatterns(tovek[i], "\\((.*?)\\)");
            int backValue = 0;
            for (String feladat : feladatok) {
              
              if (feladat.matches("^\\-\\d.*")) {
                // -1 -2ik
                backValue = Integer.parseInt(findPattern(feladat, "^\\-(\\d)"));
                vegsoto = vegsoto.substring(0, vegsoto.length() - backValue);
                ikes = false;
                if (feladat.matches(".*ik$")) {
                  ikes = true;
                }
                // feladat.matches("^\\-\\.[\\d]"
              } else if (feladat.matches("^\\-\\.")) {
                // -.
                
                String firsPart;
                String secondPart;
                firsPart = findPattern(vegsoto,
                    "^(.*?).([bcdfghjklmnpqrstvwxyz!]*)$", 1);
                secondPart = findPattern(vegsoto,
                    "^(.*?).([bcdfghjklmnpqrstvwxyz!]*)$", 2);
                vegsoto = firsPart + "!" + secondPart;
              } else if (feladat.matches("^\\.(.*)")) {
                // .a .e .i .o .� .u .�
                String csere;
                csere = findPattern(feladat, "^\\.(.*)", 1);
                if (vegsoto.contains("!")) {
                  vegsoto = vegsoto.replace("!", csere);
                } else {
                  // TODO ez mikor van?
                }
                ikes = false;
              } else if (feladat.matches("^\\%.*")) {
                // TODO nem tal�tam ilyet
                
              } else if (feladat.matches("^[^\\.\\-\\%].*")) {
                // a, abb, ad, al, an, ank�nt, anta, askodik, astul, ast�l, at,
                // az, azik, bb, beli, b�li, e, ebb, ed, eget, el, en, enk�nt,
                // ente, eskedik, est�l, est�l, et, ett, ez, ezik, gat, get,
                // hetn�k, i, kedik, k�pp, k�ppen, lag, leg, n, nk�nt, nta, nte,
                // nyi, od, odik, ogat, ol, on, onk�nt, onta, oskodik, ostul,
                // ost�l, ott, ov, oz, ozik, sodik, st�l, st�l, st�l, sul, szer,
                // szerez, szeri, szerte, szor, szori, szoroz, szorta, sz�r,
                // sz�ri, sz�rte, sz�r�z, s�t, s�dik, tat, tet, tt, ul, v, va,
                // ve, v�n, v�n, z, zik, �, �, �t, �, �dik, �dik, �get, �l,
                // �nk�nt, �sk�dik, �st�l, �st�l, �tt, �v, �z, �zik, �l, �dik
                vegsoto = vegsoto
                    + findPattern(feladat, "^([^\\.\\-\\%].*)", 1);
                ikes = false;
                
              } else {
                // System.out.println("HIBA: " + feladat);
              }
            }
          }
        }
      }
      String ikveg = ikes ? "ik" : "";
      root = igekoto + vegsoto + ikveg + "/" + tovek[tovek.length - 1];
      for (String rep : findPatterns(root, "(\\([^\\)]*\\))")) {
        root = root.replace(rep, "");
      }
    }
    root = root.replace("!", "");
    root = root.replace("@", "");
    root = root.replace("$", "");
    root = "$" + root;
    return root;
  }
  
  private static String[] preProcess(String[] tovek) {
    
    for (int i = 0; i < tovek.length; i++) {
      
      // gyorsan -> gyors
      // hallgat�lag -> hallgat�
      if (tovek[i].contains("ADJ[MANNER]")) {
        tovek[tovek.length - 1] = tovek[i];
      }
      
      // mindenk�pp -> minden
      // mindenk�ppen -> minden
      if (tovek[i].contains("NOUN[ESS_FOR]")) {
        tovek[tovek.length - 1] = tovek[i];
      }
      
      // ap�stul -> apa
      if (tovek[i].contains("NOUN") && tovek[i].contains("[COM]")) {
        tovek[tovek.length - 1] = tovek[i];
      }
      
      // fejenk�nt -> fej
      if (tovek[i].contains("NOUN[PERIOD1]")) {
        tovek[tovek.length - 1] = tovek[i];
      }
      
      /*
       * szamnevek, amik KRben /ADV
       */
      if (tovek[i].contains("NUM") && tovek[i].contains("[")) {
        tovek[tovek.length - 1] = tovek[i];
      }
      
      // rosszabb -> rossz
      // legrosszabb -> rossz
      // legeslegrosszabb -> rossz
      // rosszabbik -> rossz
      // legrosszabbik -> rossz
      // legeslegrosszabbik -> rossz
      
      if (tovek[i].contains("ADJ")) {
        if (tovek[i].contains("[COMPAR") || tovek[i].contains("[SUPERLAT")
            || tovek[i].contains("[SUPERSUPERLAT")) {
          tovek[tovek.length - 1] = tovek[i];
        }
      }
      
      // futva, futv�n -> fut
      if (tovek[i].contains("VERB[PART](va)")
          || tovek[i].contains("VERB[PART](ve)")
          || tovek[i].contains("VERB[PERF_PART](v�n)")
          || tovek[i].contains("VERB[PERF_PART](v�n)")) {
        tovek[tovek.length - 1] = tovek[i];
      }
      
    }
    return tovek;
  }
  /*
   * "$fut/VERB[GERUND](�s)/NOUN<PLUR><POSS<1>><CAS<INS>>"
   */
  public static KRPOS getPOS(String code) {
    
    int end1 = Integer.MAX_VALUE;
    int end2 = Integer.MAX_VALUE;
    
    int end = 0;
    
    if (code.contains("@")) {
      end = code.lastIndexOf("@");
    }
    
    int start = code.lastIndexOf("/");
    
    if (code.indexOf("<", start) > 0) {
      end1 = (code.indexOf("<", start));
    }
    
    if (code.indexOf("[", start) > 0) {
      end2 = (code.indexOf("[", start));
    }
    
    end = (end1 < end2) ? end1 : end2;
    
    if ((end > code.length())) {
      end = code.length();
    }
    
    if (code.substring(start, end).equals("VERB")) {
      return KRPOS.VERB;
    }
    if (code.substring(start, end).equals("NOUN")) {
      return KRPOS.NOUN;
    }
    if (code.substring(start + 1, end).equals("ADJ")) {
      return KRPOS.ADJ;
    }
    if (code.substring(start + 1, end).equals("NUM")) {
      return KRPOS.NUM;
    }
    if (code.substring(start + 1, end).equals("ADV")) {
      return KRPOS.ADV;
    }
    if (code.substring(start + 1, end).equals("PREV")) {
      return KRPOS.PREV;
    }
    if (code.substring(start + 1, end).equals("ART")) {
      return KRPOS.ART;
    }
    if (code.substring(start + 1, end).equals("POSTP")) {
      return KRPOS.POSTP;
    }
    if (code.substring(start + 1, end).equals("UTT-INT")) {
      return KRPOS.UTT_INT;
    }
    if (code.substring(start + 1, end).equals("DET")) {
      return KRPOS.DET;
    }
    if (code.substring(start + 1, end).equals("CONJ")) {
      return KRPOS.CONJ;
    }
    if (code.substring(start + 1, end).equals("ONO")) {
      return KRPOS.ONO;
    }
    if (code.substring(start + 1, end).equals("PREP")) {
      return KRPOS.PREP;
    }
    
    return KRPOS.X;
  }
  
  public static void main(String[] args) {
    // System.out
    // .println(getRoot("$fut/VERB[GERUND](�s)/NOUN<PLUR><POSS<1>><CAS<INS>>"));
    
    System.out
        .println(getPOS("$árapály/NOUN"));
  }
}
