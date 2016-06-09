package hu.u_szeged.eval;

import java.util.List;

/**
 * Created by zsibritajanos on 2015.09.23..
 */
public class ConllToPureposWriter extends TaggerResourceWriter {

  public static final String OUT_TOKEN_SEPARATOR = " ";
  public static final String OUT_TAG_SEPARATOR = "#";

  public ConllToPureposWriter() {

  }

  public String lineToTrainSentence(List<String> wordforms, List<String> lemmas, List<String> msds) {
    StringBuffer stringBuffer = new StringBuffer();
    for (int i = 0; i < wordforms.size(); ++i) {
      stringBuffer.append(wordforms.get(i).trim());
      stringBuffer.append(OUT_TAG_SEPARATOR);
      // remove '+' from lemma
      // remove ' ' to '_' from lemma
      stringBuffer.append(lemmas.get(i).replace("+", "").replace(" ", "_").trim());
      stringBuffer.append(OUT_TAG_SEPARATOR);
      // add []
      stringBuffer.append(('[' + msds.get(i) + ']').trim());
      // token separator
      stringBuffer.append(OUT_TOKEN_SEPARATOR);
    }
    return stringBuffer.toString();
  }

  public String lineToTestSentence(List<String> wordforms) {
    StringBuffer stringBuffer = new StringBuffer();
    for (int i = 0; i < wordforms.size(); ++i) {
      stringBuffer.append(wordforms.get(i).trim());
      stringBuffer.append(OUT_TOKEN_SEPARATOR);
    }
    return stringBuffer.toString();
  }

  @Override
  public String lineToGoldSentence(List<String> wordforms, List<String> lemmas, List<String> msds) {
    return null;
  }


  public static void main(String[] args) {
    new ConllToPureposWriter().generate();
  }
}

