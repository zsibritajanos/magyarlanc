/**
 * Developed by:
 * Research Group on Artificial Intelligence of the Hungarian Academy of Sciences
 * http://www.inf.u-szeged.hu/rgai/
 * <p>
 * Contact:
 * Janos Zsibrita
 * zsibrita@inf.u-szeged.hu
 * <p>
 * Licensed by Creative Commons Attribution Share Alike
 * <p>
 * http://creativecommons.org/licenses/by-sa/3.0/legalcode
 */

package hu.u_szeged.pos.guesser;

import hu.u_szeged.magyarlanc.MorAna;
import hu.u_szeged.magyarlanc.resource.ResourceHolder;

import java.util.Set;
import java.util.TreeSet;

/**
 * A MorPhonGuesser osztaly egy ismeretlen (nem elemezheto) fonevi szoto es
 * tetszoleges suffix guesselesere szolgal. A guesseles soran az adott suffixet
 * a rendszer morPhonDir szotaranak elemeire illesztve probajuk elemezni. A
 * szotar reprezentalja a magyar nyelv minden (nem hasonulo) illeszkedesi
 * szabalyat, igy biztosak lehetenk benne, hogy egy valos toldalek mindenkepp
 * illeszkedni fog legalabb egy szotarelemre. Peldaul egy 'hoz'rag eseten,
 * eleszor a kod elemre probalunk illeszteni, majd elemezni. A kapott szoalak
 * igy a kodhez lesz, melyre a KR elemzonk nem ad elemzest. A kovetkezo
 * szotarelem a talany, a szoalak a talanyhoz lesz, melyre megkapjuk az Nc-st
 * (kulso kozelito/allative) fonevi elemzest.
 */
public class MorPhonGuesser {

  public static Set<MorAna> guess(String root, String suffix) {

    Set<MorAna> stems = new TreeSet<>();

    if (root.length() > 0) {
      for (String guess : ResourceHolder.getMorPhonDir()) {

        if (ResourceHolder.getRFSA().analyse(guess + suffix).size() > 0) {
          for (String kr : ResourceHolder.getRFSA().analyse(guess + suffix)) {
            for (MorAna stem : ResourceHolder.getKRToMSD().getMSD(kr)) {
              if (stem.getMsd().startsWith("N")) {
                stems.add(new MorAna(root, stem.getMsd()));
              }
            }
          }
        }
      }
    }

    return stems;
  }

  public static void main(String[] args) {
    System.out.println(guess("", "ban"));
  }
}
