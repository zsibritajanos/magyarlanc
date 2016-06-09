package hu.u_szeged.dep.util;

import java.io.*;
import java.util.*;

public class RemoveEmptyNodes {
  
  public static boolean useLabelConcatForCollapsedEdges = true;
  
  public static void processFile(String in, String out) throws IOException {
    BufferedReader input = new BufferedReader(new InputStreamReader(
        new FileInputStream(in), "UTF-8"));
    BufferedWriter output = new BufferedWriter(new OutputStreamWriter(
        new FileOutputStream(out), "UTF-8"));
    String line;
    List<String[]> sentence = new LinkedList<String[]>();
    while ((line = input.readLine()) != null) {
      if (line.isEmpty()) {
        removeEmptyNodes(sentence, true);
        writeSentence(sentence, output);
        // restoreEmptyNodes(sentence, true);
        // writeSentence(sentence, output);
        sentence = new LinkedList<String[]>();
      } else
        sentence.add(line.split("\t"));
    }
    output.flush();
    output.close();
  }
  
  public static void processStd() throws IOException {
    BufferedReader input = new BufferedReader(new InputStreamReader(System.in,
        "UTF-8"));
    BufferedWriter output = new BufferedWriter(new OutputStreamWriter(
        System.out, "UTF-8"));
    String line;
    List<String[]> sentence = new LinkedList<String[]>();
    while ((line = input.readLine()) != null) {
      if (line.isEmpty()) {
        removeEmptyNodes(sentence, true);
        writeSentence(sentence, output);
        // restoreEmptyNodes(sentence);
        // writeSentence(sentence, output);
        sentence = new LinkedList<String[]>();
      } else
        sentence.add(line.split("\t"));
    }
    output.flush();
    output.close();
  }
  
  public static void removeEmptyNodes(List<String[]> sentence,
      boolean usePredicted) {
    int parentcolumn = usePredicted ? EmptyNodeEvaluator.PRED_PARENT_COLUMN
        : EmptyNodeEvaluator.GS_PARENT_COLUMN;
    int labelcolumn = usePredicted ? EmptyNodeEvaluator.PRED_LABEL_COLUMN
        : EmptyNodeEvaluator.GS_LABEL_COLUMN;
    int e;
    while ((e = firstEmptyNode(sentence)) > 0) {
      String parent = sentence.get(e - 1)[parentcolumn];
      for (String[] w : sentence)
        
        if (Integer.parseInt(w[parentcolumn]) == e) {
          w[8] = w[9] = parent;
          String parentLabel = sentence.get(e - 1)[labelcolumn]; // the label
          // from the
          // empty node
          // to its
          // parent
          String dtrLabel = w[labelcolumn]; // the label form the node to the
          // empty node
          String nodeLabel = sentence.get(e - 1)[usePredicted ? 5 : 4]; // the
          // type
          // of
          // the
          // empty
          // node
          String labelConcat = /*
                                * parentLabel + EmptyNodeEvaluator.SEPARATOR +
                                * nodeLabel + EmptyNodeEvaluator.SEPARATOR +
                                */dtrLabel;
          
          w[10] = w[11] = useLabelConcatForCollapsedEdges ? labelConcat : "Exd";
        }
      sentence.remove(e - 1);
      for (int i = e - 1; i < sentence.size(); ++i) {
        String[] w = sentence.get(i);
        w[0] = Integer.toString(Integer.parseInt(w[0]) - 1);
      }
      for (String[] w : sentence)
        if (Integer.parseInt(w[parentcolumn]) > e)
          w[8] = w[9] = Integer.toString(Integer.parseInt(w[parentcolumn]) - 1);
    }
  }
  
  private static int firstEmptyNode(List<String[]> sentence) {
    for (int i = 0; i < sentence.size(); ++i)
      if (sentence.get(i)[1].equals(EmptyNodeEvaluator.EMPTY_LABEL)) {
        // if(Integer.parseInt(sentence.get(i)[0]) != i+1)
        // System.err.println("Problem with token index!!");
        return i + 1;
      }
    return -1;
  }
  
  private static void restoreEmptyNodes(List<String[]> sentence,
      boolean usePredicted) {
    int parentcolumn = usePredicted ? EmptyNodeEvaluator.PRED_PARENT_COLUMN
        : EmptyNodeEvaluator.GS_PARENT_COLUMN;
    int labelcolumn = usePredicted ? EmptyNodeEvaluator.PRED_LABEL_COLUMN
        : EmptyNodeEvaluator.GS_LABEL_COLUMN;
    if (!useLabelConcatForCollapsedEdges) {
      System.err
          .println("You have to use labelconcats for restoring empty nodes!");
      System.exit(1);
    }
    List<Integer> e;
    while (!(e = firstRestoreNode(sentence, parentcolumn, labelcolumn))
        .isEmpty()) {
      Integer emptyNodePosition = calcEmptyNodePosition(sentence, e);
      Integer emptyParent = Integer
          .parseInt(sentence.get(e.get(0))[parentcolumn]);
      String emptyLabel = sentence.get(e.get(0))[labelcolumn]
          .split(EmptyNodeEvaluator.SEPARATOR)[1];
      String emptyEdgeLabel = sentence.get(e.get(0))[labelcolumn]
          .split(EmptyNodeEvaluator.SEPARATOR)[0];
      for (String[] w : sentence)
        if (Integer.parseInt(w[parentcolumn]) > emptyNodePosition)
          w[8] = w[9] = Integer.toString(Integer.parseInt(w[parentcolumn]) + 1);
      
      for (Integer en : e) {
        String[] w = sentence.get(en);
        w[8] = w[9] = Integer.toString(emptyNodePosition + 1);
        w[10] = w[11] = w[labelcolumn].substring(w[labelcolumn].indexOf(
            EmptyNodeEvaluator.SEPARATOR, w[labelcolumn]
                .indexOf(EmptyNodeEvaluator.SEPARATOR) + 1) + 1);
      }
      String[] empty = new String[sentence.get(0).length];
      for (int i = 0; i < empty.length; ++i)
        empty[i] = EmptyNodeEvaluator.EMPTY_LABEL;
      empty[0] = Integer.toString(emptyNodePosition + 1);
      empty[4] = empty[5] = emptyLabel;
      empty[8] = empty[9] = emptyParent <= emptyNodePosition ? emptyParent
          .toString() : Integer.toString(emptyParent + 1);
      empty[10] = empty[11] = emptyEdgeLabel;
      sentence.add(emptyNodePosition, empty);
      for (int i = emptyNodePosition + 1; i < sentence.size(); ++i) {
        String[] w = sentence.get(i);
        w[0] = Integer.toString(Integer.parseInt(w[0]) + 1);
      }
    }
  }
  
  private static int calcEmptyNodePosition(List<String[]> sentence,
      List<Integer> e) {
    return Collections.min(e);
  }
  
  private static List<Integer> firstRestoreNode(List<String[]> sentence,
      int parentcolumn, int labelcolumn) {
    List<Integer> set = new LinkedList<Integer>();
    int i = 0;
    while (i < sentence.size()
        && !sentence.get(i)[labelcolumn].contains(EmptyNodeEvaluator.SEPARATOR))
      ++i;
    if (i < sentence.size()) {
      String e = sentence.get(i)[labelcolumn]
          .split(EmptyNodeEvaluator.SEPARATOR)[0]
          + sentence.get(i)[labelcolumn].split(EmptyNodeEvaluator.SEPARATOR)[1]
          + sentence.get(i)[parentcolumn]; // parentLabel+nodeLabel+parent
      while (i < sentence.size()) {
        if (sentence.get(i)[labelcolumn].contains(EmptyNodeEvaluator.SEPARATOR)) {
          String o = sentence.get(i)[labelcolumn]
              .split(EmptyNodeEvaluator.SEPARATOR)[0]
              + sentence.get(i)[labelcolumn]
                  .split(EmptyNodeEvaluator.SEPARATOR)[1]
              + sentence.get(i)[parentcolumn];
          if (e.equals(o))
            set.add(i);
        }
        ++i;
      }
    }
    return set;
  }
  
 
  
  public static void writeSentence(List<String[]> sentence,
      BufferedWriter output) throws IOException {
    for (String[] w : sentence) {
      for (int i = 0; i < w.length - 1; ++i)
        output.write(w[i] + "\t");
      // output.write(w[w.length - 1] + "\n");
      output.write(w[w.length - 1] + "\t_\t_" + "\n");
    }
    output.write("\n");
  }
  
  public static void main(String[] args) throws IOException {
    
    // processFile("./data/newsml.dep.fx.noDS", "./newsml.dep.fx.noDS.uot");
    
    // processFile("/home/users0/farkas/c12/Hungarian/Dep/jav/SzegedDependencyTreebank_dev.conll2009",
    // "/home/users0/farkas/c12/Hungarian/Dep/jav/SzegedDependencyTreebank_dev.restored");
    // processStd();
  }
  
}
