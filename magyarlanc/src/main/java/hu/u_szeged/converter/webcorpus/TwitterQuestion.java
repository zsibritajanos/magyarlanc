package hu.u_szeged.converter.webcorpus;

import hu.u_szeged.magyarlanc.Magyarlanc;
import hu.u_szeged.magyarlanc.resource.ResourceHolder;
import splitter.MySplitter;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Class that tries to answer simple questions via its morphological analysis.
 * 
 * @author zsibritajanos
 * 
 */
public class TwitterQuestion {

  /**
   * Decides if the sentence is question.
   * 
   * @param sentence
   *          tokenized sentence
   * @return true is the sentence is question, false otherwise
   */
  private static boolean isQuestion(List<String> sentence) {

    if (sentence.get(sentence.size() - 1).equals("?")) {
      return true;
    }

    return false;
  }

  /**
   * Searches for the given lemma and msd (prefix) pair in the given
   * morphologically analyzed sentence.
   * 
   * @param morphSentence
   *          morphologically analyzed sentence
   * @param lemma
   *          lemma
   * @param msd
   *          msd (prefix)
   * @return index of the first occurrence of the given lemma and msd (prefix)
   *         pair, of -1 if the sentence doesn't contains it
   */
  private static int getMorAnaIndex(String[][] morphSentence, String lemma,
      String msd) {

    for (int i = 0; i < morphSentence.length; ++i) {
      if ("vagy".equals(morphSentence[i][1])
          && morphSentence[i][2].startsWith(msd)) {
        return i;
      }
    }
    return -1;
  }

  /**
   * Get the msd code at the given index in the morphologically analyzed
   * sentence.
   * 
   * @param morphSentence
   *          morphologically analyzed sentence
   * @param index
   *          msd index
   * @return msd code at the index position
   */
  private static String getMsd(String[][] morphSentence, int index) {
    return morphSentence[index][2];
  }

  /**
   * Searches forward for the given msd (prefix) from the given index in the
   * morphologically analyzed sentence.
   * 
   * @param morphSentence
   *          morphologically analyzed sentence
   * @param fromIndex
   *          index
   * @param msd
   *          msd (prefix)
   * @return the index of the msd or -1 if the sentence does not contains the
   *         given msd
   * 
   */
  private static int getMsdIndexForward(String[][] morphSentence,
      int fromIndex, String msd) {

    for (int i = fromIndex; i < morphSentence.length; ++i) {
      if (morphSentence[i][2].startsWith(msd)) {
        return i;
      }
    }

    return -1;
  }

  /**
   * Get the possible (simple) answers for the given sentence.
   * 
   * @param tokenized
   *          sentence
   * @return set of the possible answers for the given sentence, or empty set if
   *         we couldn't predict any answers
   */
  private static List<String> getAnswers(List<String> sentence) {
    List<String> answers = null;
    answers = new LinkedList<String>();

    String[][] morphSentence = null;
    int morAnaIndex = -1;
    String prevMsd = null;

    if (isQuestion(sentence)) {

      morphSentence = Magyarlanc.morphParseSentence(sentence);

      // YES/NO questions
      if (getMsd(morphSentence, 0).startsWith("V")) {
        answers.add("igen");
        answers.add("nem");
      }

      // 'vagy' questions
      morAnaIndex = getMorAnaIndex(morphSentence, "vagy", "Ccs");
      if (morAnaIndex > 0) {
        prevMsd = morphSentence[morAnaIndex - 1][2];
        // Nn Q
        if (prevMsd.startsWith("Nn")) {

          int msdForward = -1;
          msdForward = getMsdIndexForward(morphSentence, morAnaIndex, "Nn");

          if (msdForward > 1) {
            // phrase
            if ((morAnaIndex > 1)
                && (morphSentence[morAnaIndex - 2][2].startsWith("Nn") || morphSentence[morAnaIndex - 2][2]
                    .startsWith("Afp"))) {
              answers.add(morphSentence[morAnaIndex - 2][0] + " "
                  + morphSentence[morAnaIndex - 1][0]);
            } else {
              answers.add(morphSentence[morAnaIndex - 1][0]);
            }

            // phrase
            if (morphSentence[msdForward - 1][2].startsWith("Nn")
                || morphSentence[msdForward - 1][2].startsWith("Afp")) {
              answers.add(morphSentence[msdForward - 1][0] + " "
                  + morphSentence[msdForward][0]);
            } else {
              answers.add(morphSentence[msdForward][0]);
            }

          }
        } else {
          // Afp Q
          if (prevMsd.startsWith("Afp")) {
            int msdForward = -1;
            msdForward = getMsdIndexForward(morphSentence, morAnaIndex, "Afp");

            if (msdForward > 1) {
              answers.add(morphSentence[morAnaIndex - 1][0]);
              answers.add(morphSentence[msdForward][0]);
            }
          }
        }
      }
    }

    return answers;
  }

  /**
   * Get the possible (simple) answers for the given raw text.
   * 
   * @param text
   *          raw text
   * @return set of the possible answers of the last answerable sentence in the
   *         given text, or NULL if we couldn't predict any answers
   */
  public static List<String> getAnswers(String text) {

    List<List<String>> sentences = null;
    sentences = MySplitter.getInstance().split(text);

    List<String> sentenceAnswers = null;

    // reverse list
    Collections.reverse(sentences);

    for (List<String> sentence : sentences) {
      sentenceAnswers = getAnswers(sentence);
      if (sentenceAnswers.size() > 0) {
        return sentenceAnswers;
      }
    }

    return null;
  }

  public static void main(String[] args) {

    System.err
        .println(getAnswers("A Kínai Nagy Fal étterembe vagy a sarki kifőzdébe menjünk enni?"));

  }
}
