package hu.u_szeged.pos.purepos;

import hu.ppke.itk.nlpg.docmodel.IToken;
import hu.ppke.itk.nlpg.docmodel.internal.Token;
import hu.ppke.itk.nlpg.purepos.morphology.AbstractMorphologicalAnalyzer;
import hu.u_szeged.converter.univ.Univ;
import hu.u_szeged.magyarlanc.HunLemMor;

import java.util.ArrayList;
import java.util.List;

public class HunMorphAnalyzer extends AbstractMorphologicalAnalyzer {

  @Override
  public List<IToken> analyze(String word) {

    List<Univ> univs = HunLemMor.getUnivMorphologicalAnalyses(word);

    System.out.println(univs);

    ArrayList<IToken> ret = new ArrayList<>();

    for (Univ univ : univs) {

      Token t = new Token(word, univ.lemma, "Pos=" + univ.pos + "|" + univ.features.trim());
      ret.add(t);
    }
    return ret;
  }

  @Override
  public List<String> getTags(String word) {
    ArrayList<String> ret = new ArrayList<>();
    for (IToken t : this.analyze(word)) {
      ret.add(t.getTag());
    }
    return ret;
  }

  public static void main(String[] args) {

  }
}
