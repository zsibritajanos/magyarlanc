package hu.u_szeged.eval;

import hu.u_szeged.pos.converter.MSDReducer;

import java.util.List;

/**
 * Created by zsibritajanos on 2015.09.27..
 */
public class ConllToStanford extends TaggerResourceWriter {

  public static final String OUT_TOKEN_SEPARATOR = " ";
  public static final String OUT_TAG_SEPARATOR = "@";

  public static final String GOLD_SEPARATOR = "\t";

  private static MSDReducer msdReducer = new MSDReducer();

  @Override
  public String lineToTrainSentence(List<String> wordforms, List<String> lemmas, List<String> msds) {
    StringBuffer stringBuffer = new StringBuffer();
    for (int i = 0; i < wordforms.size(); ++i) {
      //wordform
      stringBuffer.append(wordforms.get(i).trim());
      stringBuffer.append(OUT_TAG_SEPARATOR);

      String reduced = msdReducer.reduce(msds.get(i));

      stringBuffer.append(reduced.trim());
      // token separator
      stringBuffer.append(OUT_TOKEN_SEPARATOR);
    }
    return stringBuffer.toString();
  }

  @Override
  public String lineToTestSentence(List<String> wordforms) {
    StringBuffer stringBuffer = new StringBuffer();
    for (int i = 0; i < wordforms.size(); ++i) {
      stringBuffer.append(wordforms.get(i).trim() + "\n");
    }
    return stringBuffer.toString();
  }

  @Override
  public String lineToGoldSentence(List<String> wordforms, List<String> lemmas, List<String> msds) {
    StringBuffer stringBuffer = new StringBuffer();
    for (int i = 0; i < wordforms.size(); ++i) {
      //wordform
      stringBuffer.append(wordforms.get(i).trim());
      stringBuffer.append(GOLD_SEPARATOR);
      //lemma
      stringBuffer.append(lemmas.get(i).trim());
      stringBuffer.append(GOLD_SEPARATOR);
      // msd
      stringBuffer.append(msds.get(i).trim());
      stringBuffer.append("\n");
    }

    return stringBuffer.toString();
  }

  public static void main(String[] args) {
    new ConllToStanford().generate();
  }
}
