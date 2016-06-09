package hu.u_szeged.magyarlanc;

import hu.u_szeged.converter.univ.Msd2UnivMorph;
import hu.u_szeged.converter.univ.Univ;
import hu.u_szeged.magyarlanc.resource.ResourceHolder;
import hu.u_szeged.pos.guesser.CompoundWord;
import hu.u_szeged.pos.guesser.HyphenicGuesser;
import hu.u_szeged.pos.guesser.HyphenicWord;
import hu.u_szeged.pos.guesser.NumberGuesser;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class HunLemMor {

  /**
   * Addott szo lehetseges morfologiai elemzeseinek megahatarozasa.
   *
   * @param word
   * @return
   */
  public static Set<MorAna> getMorphologicalAnalyses(String word) {

    Set<MorAna> morAnas = new TreeSet<>();

    // irasjelek
    if (hu.u_szeged.magyarlanc.resource.Util.isPunctation(word)) {
      // a legfontosabb irasjelek lemmaja maga az irasjel, POS kodja szinten
      // maga az irasjel lesz
      // . , ; : ! ? - -
      if (ResourceHolder.getPunctations().contains(word)) {
        morAnas.add(new MorAna(word, word));
      }

      // § lemmaja maga az irasjel, POS kodja 'Nn-sn' lesz
      else if (word.equals("§")) {
        morAnas.add(new MorAna(word, "Nc-sn"));
      }

      // egyeb irasjelek lemmaja maga az irasjel, POS kodja 'K' lesz
      else {
        morAnas.add(new MorAna(word, "K"));
      }
      return morAnas;
    }

    // ha benne van a corpus.lex-ben
    if (ResourceHolder.getCorpus().containsKey(word)) {
      morAnas.addAll(ResourceHolder.getCorpus().get(word));
      //return ResourceHolder.getCorpus().get(word);
    }

//    System.out.println(morAnas);

    // ha benne van a corpus.lex-ben kisbetuvel
    if (ResourceHolder.getCorpus().containsKey(word.toLowerCase())) {
      morAnas.addAll(ResourceHolder.getCorpus().get(word.toLowerCase()));
    }

    if (word.equalsIgnoreCase("a") || word.equalsIgnoreCase("az") || word.equalsIgnoreCase("egy")) {
      return morAnas;
    }

    // szam
    morAnas.addAll(NumberGuesser.guess(word));

//    if (morAnas.size() > 0) {
//      return morAnas;
//    }


    // romai szam
    morAnas.addAll(NumberGuesser.guessRomanNumber(word));


    // rfsa
    for (String kr : ResourceHolder.getRFSA().analyse(word)) {
      //System.out.println(word + "\t" + kr + "\t" + ResourceHolder.getKRToMSD().getMSD(kr));
      morAnas.addAll(ResourceHolder.getKRToMSD().getMSD(kr));
    }

    // (kotojeles)osszetett szo
    if (morAnas.size() == 0) {
      // kotojeles
      if (word.contains("-") && word.indexOf("-") > 1) {
        for (String morphCode : HyphenicWord.analyseHyphenicCompoundWord(word)) {
          morAnas.addAll(ResourceHolder.getKRToMSD().getMSD(morphCode));
        }
      } else {
        // osszetett szo
        for (String morphCode : CompoundWord.analyseCompoundWord(word
                .toLowerCase())) {
          morAnas.addAll(ResourceHolder.getKRToMSD().getMSD(morphCode));
        }
      }
    }

    // guess (Bush-nak, Bush-kormanyhoz)
    if (morAnas.size() == 0) {
      int index = word.lastIndexOf("-") > 1 ? word.lastIndexOf("-") : 0;

      if (index > 0) {
        String root = null;
        String suffix = null;

        root = word.substring(0, index);
        suffix = word.substring(index + 1);
        morAnas.addAll(HyphenicGuesser.guess(root, suffix));
      }
    }

    // nepies szavak
    if (morAnas.size() == 0) {

      if (ResourceHolder.getCorrDic().containsKey(word)
              && !word.equals(ResourceHolder.getCorrDic().get(word))) {
        morAnas.addAll(getMorphologicalAnalyses(ResourceHolder.getCorrDic()
                .get(word)));
      } else if (ResourceHolder.getCorrDic().containsKey(word.toLowerCase())
              && !word.equals(ResourceHolder.getCorrDic().get(word.toLowerCase()))) {
        morAnas.addAll(getMorphologicalAnalyses(ResourceHolder.getCorrDic()
                .get(word.toLowerCase())));
      }
    }

    return morAnas;
  }

  /**
   *
   * @param word
   * @return
   */
  public static List<Univ> getUnivMorphologicalAnalyses(String word) {


    Set<MorAna> morAnas = getMorphologicalAnalyses(word);
    List<Univ> univMorAnas = new LinkedList<>();

    for (MorAna morAna : morAnas) {

      Univ univ = Msd2UnivMorph.convert(morAna.getMsd(), morAna.getLemma());

      if (univ.lemma == null) {
        univMorAnas.add(new Univ(morAna.getLemma(), univ.pos, univ.features));
      } else {
        univMorAnas.add(new Univ(univ.lemma, univ.pos, univ.features));
      }
    }

    return univMorAnas;
  }

  public static void main(String[] args) {
//    System.out.println("utól\t" + getMorphologicalAnalyses("utól"));
//    System.out.println("utol\t" + getMorphologicalAnalyses("utol"));
//
//    System.out.println("utól\t" + getUnivMorphologicalAnalyses("utól"));


    System.out.println("hez\t" + getUnivMorphologicalAnalyses("hez"));
    System.out.println("hez\t" + getMorphologicalAnalyses("hez"));
  }
}
