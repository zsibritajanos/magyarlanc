package hu.u_szeged.magyarlanc;

import java.util.Map;
import java.util.TreeMap;

public class Eval {

  private static Map<String, Integer[]> measure = null;

  static {
    measure = new TreeMap<String, Integer[]>();
  }

  private static boolean equals(String etalonLemma, String etalaonPos,
      String predicatedLemma, String predicatedPos) {

    if (/*etalonLemma.equalsIgnoreCase(predicatedLemma)
        &&*/ etalaonPos.equals(predicatedPos)) {
      return true;
    }
    return false;
  }

  private static void evalToken(String etalonLemma, String etalaonPos,
      String predicatedLemma, String predicatedPos) {

    if (!measure.containsKey(etalaonPos)) {
      measure.put(etalaonPos, new Integer[] { 0, 0 });
    }

    // equals
    if (equals(etalonLemma, etalaonPos, predicatedLemma, predicatedPos)) {
      measure.get(etalaonPos)[0]++;
    } else {
      measure.get(etalaonPos)[1]++;
    }
  }

  private static void evalSentence(String[] etalonLemma, String[] etalaonPos,
      String[][] predicated) {

    for (int i = 0; i < etalonLemma.length; ++i) {
      evalToken(etalonLemma[i], etalaonPos[i], predicated[i][1],
          predicated[i][2]);
    }
  }

  public static void addSentence(String[] etalonLemma, String[] etalaonPos,
      String[][] predicated) {
    evalSentence(etalonLemma, etalaonPos, predicated);
  }

  public static void getStat() {

    int correct = 0;
    int error = 0;

    for (Map.Entry<String, Integer[]> entry : measure.entrySet()) {

      if (Character.isLetter(entry.getKey().charAt(0))) {
        System.out.println(entry.getKey() + "\t" + entry.getValue()[0] + "/"
            + entry.getValue()[1] + "/"
            + (entry.getValue()[0] + entry.getValue()[1]) + "\t"
            + (double) entry.getValue()[0]
            / (entry.getValue()[0] + entry.getValue()[1]));

        correct += entry.getValue()[0];
        error += entry.getValue()[1];

      }
    }
    System.out.println(correct + "/" + error + "/" + (correct + error) + "\t"
        + correct / (double) (correct + error));
  }
}
