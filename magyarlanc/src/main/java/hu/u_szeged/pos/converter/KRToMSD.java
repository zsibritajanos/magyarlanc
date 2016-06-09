package hu.u_szeged.pos.converter;

import hu.u_szeged.magyarlanc.MorAna;
import hu.u_szeged.magyarlanc.resource.ResourceHolder;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class KRToMSD {

  private Map<String, Set<String>> cache = null;

  public KRToMSD() {
    this.setCache(new TreeMap<String, Set<String>>());
  }

  /**
   * melleknevi igenevek
   */
  public boolean isParticiple(String krAnalysis) {

    int verbIndex = krAnalysis.indexOf("/VERB");
    int adjIndex = krAnalysis.indexOf("/ADJ");

    if (verbIndex > -1 && adjIndex > -1 && adjIndex > verbIndex) {
      return true;
    }
    return false;
  }

  public String getPostPLemma(String analysis) {

    if (analysis.startsWith("$én/NOUN<POSTP<")
            || analysis.startsWith("$te/NOUN<POSTP<")
            || analysis.startsWith("$ők/NOUN<POSTP<")
            || analysis.startsWith("$mi/NOUN<POSTP<")
            || analysis.startsWith("$ti/NOUN<POSTP<")
            || analysis.startsWith("$ők/NOUN<POSTP<")) {

      String post = null;

      if (analysis.startsWith("$én") || analysis.startsWith("$te")) {
        post = analysis.substring(15, analysis.length() - 11).toLowerCase();
      } else if (analysis.startsWith("$ők")) {
        post = analysis.substring(15, analysis.length() - 14).toLowerCase();
      } else if (analysis.startsWith("$ő")) {
        post = analysis.substring(14, analysis.length() - 8).toLowerCase();
      } else if (analysis.startsWith("$mi") || analysis.startsWith("$ti")) {
        post = analysis.substring(15, analysis.length() - 17).toLowerCase();
      }

      if (analysis.startsWith("$ő") && !analysis.startsWith("$ők")) {
        analysis = analysis.substring(2);
      } else {
        analysis = analysis.substring(3);
      }
      return post;

    }

    if (analysis.startsWith("$ez/NOUN<POSTP<")
            || analysis.startsWith("$az/NOUN<POSTP<")) {

      String affix = analysis.substring(15);
      affix = affix.substring(0, affix.indexOf(">")).toLowerCase();

      // alá, alatt, alól, által, elő, előb, ellen, elől, előtt, iránt, után
      // (pl.: ezután)
      if (analysis.contains("(i)")) {
        if (affix.startsWith("a") || affix.startsWith("á")
                || affix.startsWith("e") || affix.startsWith("i")
                || affix.startsWith("u"))
          return analysis.substring(1, 3) + affix + "i";

        return analysis.substring(1, 2) + affix + "i";

      }
      return analysis.substring(1, 3) + affix;
    }

    return analysis.substring(1, analysis.indexOf("/"));
  }

  public String convertNoun(String lemma, String kr) {
    StringBuffer msd = null;
    msd = new StringBuffer("Nc-sn" + "------");

    /*
     * nevmas minden PERS-t tartalmazo NOUN
     */

    // velem
    // /NOUN<PERS<1>><CAS<INS>>

    if (kr.contains("PERS")) {

      msd = new StringBuffer("Pp--sn-----------");
      /*
       * szemely
       */

      // 1
      if (kr.contains("<PERS<1>>")) {
        msd.setCharAt(2, '1');
      }

      // 2
      if (kr.contains("<PERS<2>>")) {
        msd.setCharAt(2, '2');
      }

      // 3
      if (kr.contains("<PERS>")) {
        msd.setCharAt(2, '3');
      }

      /*
       * szam
       */

      if (kr.contains("<PLUR>")) {
        msd.setCharAt(4, 'p');
      }

      /*
       * eset
       */

      // n nincs jelolve alapeset

      // a
      if (kr.contains("<CAS<ACC>>")) {
        msd.setCharAt(5, 'a');
      }

      // g nincs jelolve
      if (kr.contains("<CAS<GEN>>")) {
        msd.setCharAt(5, 'g');
      }

      // d
      if (kr.contains("<CAS<DAT>>")) {
        msd.setCharAt(5, 'd');
      }

      // i
      if (kr.contains("<CAS<INS>>")) {
        msd.setCharAt(5, 'i');
      }

      // x
      if (kr.contains("<CAS<ILL>>")) {
        msd.setCharAt(5, 'x');
      }

      // 2
      if (kr.contains("<CAS<INE>>")) {
        msd.setCharAt(5, '2');
      }

      // e
      if (kr.contains("<CAS<ELA>>")) {
        msd.setCharAt(5, 'e');
      }

      // t
      if (kr.contains("<CAS<ALL>>")) {
        msd.setCharAt(5, 't');
      }

      // 3
      if (kr.contains("<CAS<ADE>>")) {
        msd.setCharAt(5, '3');
      }

      // b
      if (kr.contains("<CAS<ABL>>")) {
        msd.setCharAt(5, 'b');
      }

      // s
      if (kr.contains("<CAS<SBL>>")) {
        msd.setCharAt(5, 's');
      }

      // p
      if (kr.contains("<CAS<SUE>>")) {
        msd.setCharAt(5, 'p');
      }

      // h
      if (kr.contains("<CAS<DEL>>")) {
        msd.setCharAt(5, 'h');
      }

      // 9
      if (kr.contains("<CAS<TER>>")) {
        msd.setCharAt(5, '9');
      }

      // w
      if (kr.contains("[MANNER]")) {
        msd.setCharAt(5, 'w');
      }

      // f
      if (kr.contains("<CAS<FOR>>")) {
        msd.setCharAt(5, 'f');
      }

      // m
      if (kr.contains("<CAS<TEM>>")) {
        msd.setCharAt(5, 'm');
      }

      // c
      if (kr.contains("<CAS<CAU>>")) {
        msd.setCharAt(5, 'c');
      }

      // q
      if (kr.contains("[COM]")) {
        msd.setCharAt(5, 'q');
      }

      // y
      if (kr.contains("<CAS<TRA>>")) {
        msd.setCharAt(5, 'y');
      }

      // u
      if (kr.contains("[PERIOD1]")) {
        msd.setCharAt(5, 'u');
      }

      return cleanMsd(msd.toString());
    }

    /*
     * nevmas minden POSTP-t tartalmazo NOUN
     */

    if (kr.contains("POSTP")) {
      msd = new StringBuffer("Pp3-sn");

      if (lemma.equals("én")) {
        msd.setCharAt(2, '1');
      }

      if (lemma.equals("te")) {
        msd.setCharAt(2, '2');
      }

      if (lemma.equals("ő")) {
        msd.setCharAt(2, '3');
      }

      if (lemma.equals("mi")) {
        msd.setCharAt(2, '1');
        msd.setCharAt(4, 'p');
      }

      if (lemma.equals("ti")) {
        msd.setCharAt(2, '2');
        msd.setCharAt(4, 'p');
      }

      if (lemma.equals("ők")) {
        msd.setCharAt(2, '3');
        msd.setCharAt(4, 'p');
      }

      return cleanMsd(msd.toString());
    }

    /*
     * egyes szam/tobbes szam NOUN<PLUR> NUON<PLUR<FAM>>
     */

    if (kr.contains("NOUN<PLUR")) {
      msd.setCharAt(3, 'p');
    }

    /*
     * eset
     */

    // n nincs jelolve alapeset

    // a
    if (kr.contains("<CAS<ACC>>")) {
      msd.setCharAt(4, 'a');
    }

    // g nincs jelolve
    if (kr.contains("<CAS<GEN>>")) {
      msd.setCharAt(4, 'g');
    }

    // d
    if (kr.contains("<CAS<DAT>>")) {
      msd.setCharAt(4, 'd');
    }

    // i
    if (kr.contains("<CAS<INS>>")) {
      msd.setCharAt(4, 'i');
    }

    // x
    if (kr.contains("<CAS<ILL>>")) {
      msd.setCharAt(4, 'x');
    }

    // 2
    if (kr.contains("<CAS<INE>>")) {
      msd.setCharAt(4, '2');
    }

    // e
    if (kr.contains("<CAS<ELA>>")) {
      msd.setCharAt(4, 'e');
    }

    // t
    if (kr.contains("<CAS<ALL>>")) {
      msd.setCharAt(4, 't');
    }

    // 3
    if (kr.contains("<CAS<ADE>>")) {
      msd.setCharAt(4, '3');
    }

    // b
    if (kr.contains("<CAS<ABL>>")) {
      msd.setCharAt(4, 'b');
    }

    // s
    if (kr.contains("<CAS<SBL>>")) {
      msd.setCharAt(4, 's');
    }

    // p
    if (kr.contains("<CAS<SUE>>")) {
      msd.setCharAt(4, 'p');
    }

    // h
    if (kr.contains("<CAS<DEL>>")) {
      msd.setCharAt(4, 'h');
    }

    // 9
    if (kr.contains("<CAS<TER>>")) {
      msd.setCharAt(4, '9');
    }

    // w
    if (kr.contains("<CAS<ESS>>")) {
      msd.setCharAt(4, 'w');
    }

    // f
    if (kr.contains("<CAS<FOR>>")) {
      msd.setCharAt(4, 'f');
    }

    // m
    if (kr.contains("<CAS<TEM>>")) {
      msd.setCharAt(4, 'm');
    }

    // c
    if (kr.contains("<CAS<CAU>>")) {
      msd.setCharAt(4, 'c');
    }

    // q
    if (kr.contains("[COM]")) {
      msd.setCharAt(4, 'q');
    }

    // y
    if (kr.contains("<CAS<TRA>>")) {
      msd.setCharAt(4, 'y');
    }

    // u
    if (kr.contains("[PERIOD1]")) {
      msd.setCharAt(4, 'u');
    }

    /*
     * birtokos szama/szemelye
     */
    if (kr.contains("<POSS>")) {
      msd.setCharAt(8, 's');
      msd.setCharAt(9, '3');
    }
    if (kr.contains("<POSS<1>>")) {
      msd.setCharAt(8, 's');
      msd.setCharAt(9, '1');
    }
    if (kr.contains("<POSS<2>>")) {
      msd.setCharAt(8, 's');
      msd.setCharAt(9, '2');
    }
    if (kr.contains("<POSS<1><PLUR>>")) {
      msd.setCharAt(8, 'p');
      msd.setCharAt(9, '1');
    }
    if (kr.contains("<POSS<2><PLUR>>")) {
      msd.setCharAt(8, 'p');
      msd.setCharAt(9, '2');
    }
    if (kr.contains("<POSS<PLUR>>")) {
      msd.setCharAt(8, 'p');
      msd.setCharAt(9, '3');
    }

    /*
     * birtok(olt) szama
     */
    if (kr.contains("<ANP>")) {
      msd.setCharAt(10, 's');
    }
    if (kr.contains("<ANP<PLUR>>")) {
      msd.setCharAt(10, 'p');
    }

    return cleanMsd(msd.toString());
  }

  public String convertAdjective(String kr) {
    StringBuffer msd = null;
    msd = new StringBuffer("Afp-sn-------");

    /*
     * tipus (melleknev vagy melleknevi igenev)
     */

    // f (melleknev) nincs jelolve, alapeset

    // p (folyamatos melleknevi igenev)

    if (kr.contains("[IMPERF_PART")) {
      msd.setCharAt(1, 'p');
    }

    // s (befejezett melleknevi igenev)

    if (kr.contains("[PERF_PART")) {
      msd.setCharAt(1, 's');
    }

    // u (beallo melleknevi igenev)

    if (kr.contains("[FUT_PART")) {
      msd.setCharAt(1, 'u');
    }

    /*
     * fok
     */

    // p nincs jelolve alapeset

    // c
    if (kr.contains("[COMPAR")) {
      msd.setCharAt(2, 'c');
    }
    // s
    if (kr.contains("[SUPERLAT")) {
      msd.setCharAt(2, 's');
    }
    // e
    if (kr.contains("[SUPERSUPERLAT")) {
      msd.setCharAt(2, 'e');
    }

    /*
     * szam
     */
    // s nincs jelolve alapeset

    // p
    if (kr.contains("ADJ<PLUR>")) {
      msd.setCharAt(4, 'p');
    }

    /*
     * eset
     */

    // n nincs jelolve alapeset

    // a
    if (kr.contains("<CAS<ACC>>")) {
      msd.setCharAt(5, 'a');
    }

    // g nincs jelolve
    if (kr.contains("<CAS<GEN>>")) {
      msd.setCharAt(5, 'g');
    }

    // d
    if (kr.contains("<CAS<DAT>>")) {
      msd.setCharAt(5, 'd');
    }

    // i
    if (kr.contains("<CAS<INS>>")) {
      msd.setCharAt(5, 'i');
    }

    // x
    if (kr.contains("<CAS<ILL>>")) {
      msd.setCharAt(5, 'x');
    }

    // 2
    if (kr.contains("<CAS<INE>>")) {
      msd.setCharAt(5, '2');
    }

    // e
    if (kr.contains("<CAS<ELA>>")) {
      msd.setCharAt(5, 'e');
    }

    // t
    if (kr.contains("<CAS<ALL>>")) {
      msd.setCharAt(5, 't');
    }

    // 3
    if (kr.contains("<CAS<ADE>>")) {
      msd.setCharAt(5, '3');
    }

    // b
    if (kr.contains("<CAS<ABL>>")) {
      msd.setCharAt(5, 'b');
    }

    // s
    if (kr.contains("<CAS<SBL>>")) {
      msd.setCharAt(5, 's');
    }

    // p
    if (kr.contains("<CAS<SUE>>")) {
      msd.setCharAt(5, 'p');
    }

    // h
    if (kr.contains("<CAS<DEL>>")) {
      msd.setCharAt(5, 'h');
    }

    // 9
    if (kr.contains("<CAS<TER>>")) {
      msd.setCharAt(5, '9');
    }

    // w
    if (kr.contains("[MANNER]")) {
      msd.setCharAt(5, 'w');
    }

    // f
    if (kr.contains("<CAS<FOR>>")) {
      msd.setCharAt(5, 'f');
    }

    // m
    if (kr.contains("<CAS<TEM>>")) {
      msd.setCharAt(5, 'm');
    }

    // c
    if (kr.contains("<CAS<CAU>>")) {
      msd.setCharAt(5, 'c');
    }

    // q
    if (kr.contains("[COM]")) {
      msd.setCharAt(5, 'q');
    }

    // y
    if (kr.contains("<CAS<TRA>>")) {
      msd.setCharAt(5, 'y');
    }

    // u
    if (kr.contains("[PERIOD1]")) {
      msd.setCharAt(5, 'u');
    }

    /*
     * birtokos szama/szemelye
     */
    if (kr.contains("<POSS>")) {
      msd.setCharAt(10, 's');
      msd.setCharAt(11, '3');
    }
    if (kr.contains("<POSS<1>>")) {
      msd.setCharAt(10, 's');
      msd.setCharAt(11, '1');
    }
    if (kr.contains("<POSS<2>>")) {
      msd.setCharAt(10, 's');
      msd.setCharAt(11, '2');
    }
    if (kr.contains("<POSS<1><PLUR>>")) {
      msd.setCharAt(10, 'p');
      msd.setCharAt(11, '1');
    }
    if (kr.contains("<POSS<2><PLUR>>")) {
      msd.setCharAt(10, 'p');
      msd.setCharAt(11, '2');
    }
    if (kr.contains("<POSS<PLUR>>")) {
      msd.setCharAt(10, 'p');
      msd.setCharAt(11, '3');
    }

    /*
     * birtok(olt) szama
     */
    if (kr.contains("<ANP>")) {
      msd.setCharAt(12, 's');
    }
    if (kr.contains("<ANP<PLUR>>")) {
      msd.setCharAt(12, 'p');
    }

    return cleanMsd(msd.toString());
  }

  public String convertVerb(String kr) {
    StringBuffer msd = null;
    msd = new StringBuffer("Vmip3s---n-");

    /*
     * magyarlanc 2.5-től
     */

    // hato
    if (kr.contains("<MODAL>") && !kr.contains("[FREQ]")
            && !kr.contains("[CAUS]")) {
      msd.setCharAt(1, 'o');
    }

    // gyakorito
    if (!kr.contains("<MODAL>") && kr.contains("[FREQ]")
            && !kr.contains("[CAUS]")) {
      msd.setCharAt(1, 'f');
    }

    // muvelteto
    if (!kr.contains("<MODAL>") && !kr.contains("[FREQ]")
            && kr.contains("[CAUS]")) {
      msd.setCharAt(1, 's');
    }

    // gyakorito + hato
    if (kr.contains("<MODAL>") && kr.contains("[FREQ]")
            && !kr.contains("[CAUS]")) {
      msd.setCharAt(1, '1');
    }

    // muvelteto + hato
    if (kr.contains("<MODAL>") && !kr.contains("[FREQ]")
            && kr.contains("[CAUS]")) {
      msd.setCharAt(1, '2');
    }

    // muvelteto + hato
    if (!kr.contains("<MODAL>") && kr.contains("[FREQ]")
            && kr.contains("[CAUS]")) {
      msd.setCharAt(1, '3');
    }

    // muvelteto + gyakorito + hato
    if (kr.contains("<MODAL>") && kr.contains("[FREQ]")
            && kr.contains("[CAUS]")) {
      msd.setCharAt(1, '4');
    }

    if (kr.contains("<COND>")) {
      msd.setCharAt(2, 'c');
    }
    if (kr.contains("<INF>")) {
      msd.setCharAt(2, 'n');
      msd.setCharAt(9, '-');
      if (!kr.contains("<PERS")) {
        msd.setCharAt(3, '-');
        msd.setCharAt(4, '-');
        msd.setCharAt(5, '-');
      }
    }

    if (kr.contains("<SUBJUNC-IMP>")) {
      msd.setCharAt(2, 'm');
    }

    if (kr.contains("<PAST>")) {
      msd.setCharAt(3, 's');
    }

    if (kr.contains("<PERS<1>>")) {
      msd.setCharAt(4, '1');
    }
    if (kr.contains("<PERS<2>>")) {
      msd.setCharAt(4, '2');
    }

    if (kr.contains("<PLUR>")) {
      msd.setCharAt(5, 'p');
    }

    if (kr.contains("<DEF>")) {
      msd.setCharAt(9, 'y');
    }
    if (kr.contains("<PERS<1<OBJ<2>>>>")) {
      msd.setCharAt(4, '1');
      msd.setCharAt(9, '2');
    }
    return cleanMsd(msd.toString());
  }

  public String convertNumber(String kr, String analysis) {
    StringBuffer msd = null;
    msd = new StringBuffer("Mc-snl-------");

    // c alapeset, nincs jelolve

    // o
    if (kr.contains("[ORD")) {
      msd.setCharAt(1, 'o');
    }
    // f
    if (kr.contains("[FRACT")) {
      msd.setCharAt(1, 'f');
    }

    // l nincs a magyarban
    // d nincs KRben

    // s alapeset, nincs jelolve
    // p
    if (kr.contains("NUM<PLUR>")) {
      msd.setCharAt(3, 'p');
    }

    /*
     * eset
     */

    // n nincs jelolve alapeset

    // a
    if (kr.contains("<CAS<ACC>>")) {
      msd.setCharAt(4, 'a');
    }

    // g nincs jelolve
    if (kr.contains("<CAS<GEN>>")) {
      msd.setCharAt(4, 'g');
    }

    // d
    if (kr.contains("<CAS<DAT>>")) {
      msd.setCharAt(4, 'd');
    }

    // i
    if (kr.contains("<CAS<INS>>")) {
      msd.setCharAt(4, 'i');
    }

    // x
    if (kr.contains("<CAS<ILL>>")) {
      msd.setCharAt(4, 'x');
    }

    // 2
    if (kr.contains("<CAS<INE>>")) {
      msd.setCharAt(4, '2');
    }

    // e
    if (kr.contains("<CAS<ELA>>")) {
      msd.setCharAt(4, 'e');
    }

    // t
    if (kr.contains("<CAS<ALL>>")) {
      msd.setCharAt(4, 't');
    }

    // 3
    if (kr.contains("<CAS<ADE>>")) {
      msd.setCharAt(4, '3');
    }

    // b
    if (kr.contains("<CAS<ABL>>")) {
      msd.setCharAt(4, 'b');
    }

    // s
    if (kr.contains("<CAS<SBL>>")) {
      msd.setCharAt(4, 's');
    }

    // p
    if (kr.contains("<CAS<SUE>>")) {
      msd.setCharAt(4, 'p');
    }

    // h
    if (kr.contains("<CAS<DEL>>")) {
      msd.setCharAt(4, 'h');
    }

    // 9
    if (kr.contains("<CAS<TER>>")) {
      msd.setCharAt(4, '9');
    }

    // w
    if (kr.contains("[MANNER]")) {
      msd.setCharAt(4, 'w');
    }

    // f
    if (kr.contains("<CAS<FOR>>")) {
      msd.setCharAt(4, 'f');
    }

    // m
    if (kr.contains("<CAS<TEM>>")) {
      msd.setCharAt(4, 'm');
    }

    // c
    if (kr.contains("<CAS<CAU>>")) {
      msd.setCharAt(4, 'c');
    }

    // q
    if (kr.contains("[COM]")) {
      msd.setCharAt(4, 'q');
    }

    // y
    if (kr.contains("<CAS<TRA>>")) {
      msd.setCharAt(4, 'y');
    }

    // u
    if (kr.contains("[PERIOD1]")) {
      msd.setCharAt(4, 'u');
    }

    // 6
    if (kr.contains("[MULTIPL-ITER]")) {
      msd.setCharAt(4, '6');
    }

    /*
     * birtokos szama/szemelye
     */
    if (analysis.contains("<POSS>")) {
      msd.setCharAt(10, 's');
      msd.setCharAt(11, '3');
    }
    if (analysis.contains("<POSS<1>>")) {
      msd.setCharAt(10, 's');
      msd.setCharAt(11, '1');
    }
    if (analysis.contains("<POSS<2>>")) {
      msd.setCharAt(10, 's');
      msd.setCharAt(11, '2');
    }
    if (analysis.contains("<POSS<1><PLUR>>")) {
      msd.setCharAt(10, 'p');
      msd.setCharAt(11, '1');
    }
    if (analysis.contains("<POSS<2><PLUR>>")) {
      msd.setCharAt(10, 'p');
      msd.setCharAt(11, '2');
    }
    if (analysis.contains("<POSS<PLUR>>")) {
      msd.setCharAt(10, 'p');
      msd.setCharAt(11, '3');
    }

    /*
     * birtok(olt) szama
     */
    if (analysis.contains("<ANP>")) {
      msd.setCharAt(12, 's');
    }
    if (analysis.contains("<ANP<PLUR>>")) {
      msd.setCharAt(12, 'p');
    }

    return cleanMsd(msd.toString());
  }

  public String convertAdverb(String kr) {
    StringBuffer msd = null;
    msd = new StringBuffer("Rx----");

    // c
    if (kr.contains("[COMPAR]")) {
      msd.setCharAt(2, 'c');
    }
    // s
    if (kr.contains("[SUPERLAT]")) {
      msd.setCharAt(2, 's');
    }
    // e
    if (kr.contains("[SUPERSUPERLAT]")) {
      msd.setCharAt(2, 'e');
    }

    return cleanMsd(msd.toString());
  }

  public Set<MorAna> getMSD(String krAnalysis) {

    Set<MorAna> analisis = null;
    String lemma = null;
    String stem = null;
    String krCode = null;
    String krRoot = null;
    String msd = null;

    analisis = new TreeSet<MorAna>();

    krRoot = KRUtils.getRoot(krAnalysis);
    if (krRoot.indexOf("/") > 1) {
      lemma = krRoot.substring(1, krRoot.indexOf("/"));
    } else {
      lemma = krRoot;
    }

    // $forog(-.)/VERB[CAUS](at)/VERB[FREQ](gat)/VERB<PAST><PERS<1>>

    if (krAnalysis.contains("(")
            && krAnalysis.indexOf("(") < krAnalysis.indexOf("/")) {
      stem = krAnalysis.substring(1, krAnalysis.indexOf("("));
    } else if (krAnalysis.contains("+")) {
      stem = lemma;
    } else {
      if (krAnalysis.indexOf("/") > 1) {
        stem = krAnalysis.substring(1, krAnalysis.indexOf("/"));
      } else {
        stem = krAnalysis;
      }
    }

    krCode = krRoot.substring(krRoot.indexOf("/") + 1);

    if (!krAnalysis.contains("[FREQ]") && krAnalysis.contains("[CAUS]")
            & krAnalysis.contains("<MODAL>")) {
      if (this.getCache().containsKey(krCode)) {
        for (String m : this.getCache().get(krCode)) {
          analisis.add(new MorAna(lemma, m));
        }
        return analisis;
      }
    }

    if (krCode.startsWith("NOUN")) {
      msd = convertNoun(lemma, krCode);

      // pronoun
      if (msd.startsWith("P")) {
        lemma = getPostPLemma(krAnalysis);

        // dative
        if (msd.charAt(5) == 'd') {
          analisis.add(new MorAna(lemma, msd.replace('d', 'g')));
        }
      }

      analisis.add(new MorAna(lemma, msd));

      // dative
      if (msd.charAt(4) == 'd') {
        analisis.add(new MorAna(lemma, msd.replace('d', 'g')));
      }

    }

    if (krCode.startsWith("ADJ")) {

      /*
       * magyarlanc 2.5-től
       */

      // melleknevi igenev
      if (isParticiple(krAnalysis)) {
        msd = convertAdjective(krAnalysis);
        analisis.add(new MorAna(lemma, msd));
      } else {
        msd = convertAdjective(krCode);
        analisis.add(new MorAna(lemma, msd));
      }

      // msd = convertAdjective(krCode);
      // analisis.add(new MorAna(lemma, msd));

      // dative
      if (msd.charAt(5) == 'd') {
        analisis.add(new MorAna(lemma, msd.replace('d', 'g')));
      }

    }

    if (krCode.startsWith("VERB")) {
      // határozói igenév
      if (krCode.contains("VERB[PERF_PART]") || krCode.contains("VERB[PART]")) {
        analisis.add(new MorAna(lemma, "Rv"));
      } else if (krAnalysis.contains("[FREQ]") || krAnalysis.contains("[CAUS]")
              || krAnalysis.contains("<MODAL>")) {
        analisis.add(new MorAna(stem, convertVerb(krAnalysis)));
      } else {
        analisis.add(new MorAna(lemma, convertVerb(krCode)));
      }
    }

    if (krCode.startsWith("NUM")) {
      msd = convertNumber(krCode, krAnalysis);
      analisis.add(new MorAna(lemma, msd));

      // dative
      if (msd.charAt(4) == 'd') {
        analisis.add(new MorAna(lemma, msd.replace('d', 'g')));
      }
    }
    if (krCode.startsWith("ART")) {
      /*
       * definite/indefinte
       */
      analisis.add(new MorAna(lemma, "T"));
    }
    if (krCode.startsWith("ADV")) {
      analisis.add(new MorAna(lemma, convertAdverb(krCode)));
    }
    if (krCode.startsWith("POSTP")) {
      analisis.add(new MorAna(lemma, "St"));
    }
    if (krCode.startsWith("CONJ")) {
      analisis.add(new MorAna(lemma, "Ccsp"));
    }
    if (krCode.startsWith("UTT-INT")) {
      analisis.add(new MorAna(lemma, "I"));
    }
    if (krCode.startsWith("PREV")) {
      analisis.add(new MorAna(lemma, "Rp"));
    }
    if (krCode.startsWith("DET")) {
      analisis.add(new MorAna(lemma, "Pd3-sn"));
    }
    if (krCode.startsWith("ONO")) {
      analisis.add(new MorAna(lemma, "X"));
    }

    if (krCode.startsWith("E")) {
      analisis.add(new MorAna(lemma, "Rq-y"));
    }

    if (krCode.startsWith("ABBR")) {
      analisis.add(new MorAna(lemma, "Y"));
    }

    if (krCode.startsWith("TYPO")) {
      analisis.add(new MorAna(lemma, "Z"));
    }

    if (analisis.isEmpty()) {
      analisis.add(new MorAna(lemma, "X"));
    }

    // cache
    if (!this.getCache().containsKey(krCode)) {
      this.getCache().put(krCode, new TreeSet<String>());
    }
    for (MorAna m : analisis) {
      this.getCache().get(krCode).add(m.getMsd());
    }

    return analisis;
  }

  public String cleanMsd(String msd) {
    StringBuffer cleaned = null;
    cleaned = new StringBuffer(msd.trim());

    int index = cleaned.length() - 1;
    while (cleaned.charAt(index) == '-') {
      cleaned.deleteCharAt(index);
      --index;
    }

    return cleaned.toString();
  }

  public void setCache(Map<String, Set<String>> cache) {
    this.cache = cache;
  }

  public Map<String, Set<String>> getCache() {
    return cache;
  }

  public static void main(String args[]) {
    System.err.println(ResourceHolder.getRFSA().analyse("8-ánál"));

//    System.err.println(ResourceHolder.getKRToMSD().getMSD(
//            "$én/NOUN<POSTP<UTÁN>><PERS<1>>"));

  }
}
