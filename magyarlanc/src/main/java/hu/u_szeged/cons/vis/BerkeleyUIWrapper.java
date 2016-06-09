package hu.u_szeged.cons.vis;

import edu.berkeley.nlp.syntax.Tree;
import edu.berkeley.nlp.syntax.Trees;
import edu.berkeley.nlp.ui.CustomTreeJPanel;
import edu.berkeley.nlp.ui.TreeJPanel;
import hu.u_szeged.cons.util.ConstTool;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

public class BerkeleyUIWrapper {
  private static final String IMAGE_FORMAT = "PNG";

  public static byte[] exportToByteArray(String[][] sentence) {
    try {
      String tree = ConstTool.coloumnFormat2Tree(sentence).toString();
      return exportToByteArray(tree);
    } catch (Exception e) {
    }
    return exportToByteArray(ParseTreeApplicationWrapper.DEFAULT_ERROR_TREE);
  }

  public static byte[] exportToByteArray(String sentence) {

    ByteArrayOutputStream byteArrayOutputStream = null;
    try {
      byteArrayOutputStream = new ByteArrayOutputStream();
      ImageIO.write((BufferedImage) getImage(sentence), IMAGE_FORMAT, byteArrayOutputStream);
      byteArrayOutputStream.flush();

    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        byteArrayOutputStream.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    return byteArrayOutputStream.toByteArray();
  }

  public static byte[] exportToBase64(String[][] parsed) {
    return Base64.getEncoder().encode(exportToByteArray(parsed));
  }

  public static byte[] exportToBase64(String parsed) {
    return exportToBase64(stringToSentence(parsed));
  //  return Base64.getEncoder().encode(exportToByteArray(parsed));
  }

  public static Image getImage(String[][] sentence) {
    try {
      String tree = ConstTool.coloumnFormat2Tree(sentence).toString();
      return getImage(tree);
    } catch (Exception e) {
    }
    return getImage(ParseTreeApplicationWrapper.DEFAULT_ERROR_TREE);
  }

  public static Image getImage(String sentence) {
    TreeJPanel tjp = getTreeJPanel(sentence);

    BufferedImage bi = new BufferedImage(tjp.width(), tjp.height(), BufferedImage.TYPE_INT_ARGB);
    Graphics2D g2 = bi.createGraphics();

    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR, 1.0f));

    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));

    CustomTreeJPanel.bgColor = UIManager.getColor("Panel.background");
    tjp.paintComponent(g2);

    return bi;
  }

  public static void exportToPNG(String[][] sentence, String out) {
    try {
      String tree = ConstTool.coloumnFormat2Tree(sentence).toString();
      exportToPNG(tree, out);
    } catch (Exception e) {
    }
    exportToPNG(ParseTreeApplicationWrapper.DEFAULT_ERROR_TREE, out);
  }

  /**
   * @param sentence
   * @param out
   */
  public static void exportToPNG(String sentence, String out) {
    FileOutputStream fileOutputStream = null;
    try {
      fileOutputStream = new FileOutputStream(out);
      ImageIO.write((BufferedImage) getImage(sentence), IMAGE_FORMAT, fileOutputStream);
      fileOutputStream.flush();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      {
        try {
          fileOutputStream.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }

  public static CustomTreeJPanel getTreeJPanel(String[][] sentence) {
    try {
      String tree = ConstTool.coloumnFormat2Tree(sentence).toString();
      return getTreeJPanel(tree);
    } catch (Exception e) {
    }
    return getTreeJPanel(ParseTreeApplicationWrapper.DEFAULT_ERROR_TREE);

  }

  public static CustomTreeJPanel getTreeJPanel(String sentence) {
    CustomTreeJPanel tjp = new CustomTreeJPanel();
    Tree<String> tree = (new Trees.PennTreeReader(new StringReader(sentence))).next();
    tjp.setTree(tree);
    return tjp;
  }

  /**
   * @param parsedSentence
   * @return
   */
  private static String[][] stringToSentence(String parsedSentence) {
    java.util.List<String[]> sentence = new ArrayList<>();

    for (String line : parsedSentence.split("\n")) {
      String[] split = line.trim().split("\t");
      sentence.add(split);
    }

    return sentence.toArray(new String[sentence.size()][]);
  }

  public static void main(String[] args) {
    /**
     * sorok/tokenek '\t'
     */
    String sentence = "1\tEz\tez\tPRON\tCase=Nom|Number=Sing|Person=3|PronType=Dem\t3\tDET\t(ROOT(CP(NP*)\n" +
            "2\ta\ta\tDET\tDefinite=Def|PronType=Art\t3\tDET\t(NP*\n" +
            "3\tpróba\tpróba\tNOUN\tCase=Nom|Number=Sing\t0\tROOT\t*)\n" +
            "4\t.\t.\tPUNCT\t_\t0\tPUNCT\t*))\n";

    /**
     * base64 stringkent a kep byte array
     */
    System.out.println(exportToBase64(sentence));
  }
}
