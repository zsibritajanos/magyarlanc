/**
 * Developed by:
 *   Research Group on Artificial Intelligence of the Hungarian Academy of Sciences
 *   http://www.inf.u-szeged.hu/rgai/
 *
 * Contact:
 *  J�nos Zsibrita
 *  zsibrita@inf.u-szeged.hu
 *  
 * Licensed by Creative Commons Attribution Share Alike
 *  
 * http://creativecommons.org/licenses/by-sa/3.0/legalcode
 */

package hu.u_szeged.pos.guesser;

import hu.u_szeged.magyarlanc.MorAna;
import hu.u_szeged.magyarlanc.resource.ResourceHolder;

import java.util.Set;
import java.util.TreeSet;


/**
 * 
 * @author zsjanos
 */

public class HyphenicGuesser {
  
  public static Set<MorAna> guess(String root, String suffix) {
    
    Set<MorAna> morAnas = null;
    morAnas = new TreeSet<MorAna>();
    
    // k�t�leles suffix (pl.: Bush-hoz)
    morAnas.addAll(MorPhonGuesser.guess(root, suffix));
    
    // suffix f�n�v (pl.: Bush-kormannyal)
    for (String kr : ResourceHolder.getRFSA().analyse(suffix)) {
      for (MorAna morAna : ResourceHolder.getKRToMSD().getMSD(kr)) {
        // csak fonevi elemzesek
        if (morAna.getMsd().startsWith("N")) {
          morAnas.add(new MorAna(root + "-" + morAna.getLemma(), morAna
              .getMsd()));
        }
      }
    }
    return morAnas;
  }
  
  public static void main(String[] args) {
    System.out.println(HyphenicGuesser.guess("Bush", "hoz"));
    System.out.println(HyphenicGuesser.guess("Bush", "kormánynak"));
  }
}
