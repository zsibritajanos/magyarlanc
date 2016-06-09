package hu.u_szeged.dep.whatswrong;

import com.googlecode.whatswrong.NLPInstance;
import com.googlecode.whatswrong.SingleSentenceRenderer;
import com.googlecode.whatswrong.io.CoNLL2009;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

public class WhatsWrongWrapper {

  private static SingleSentenceRenderer renderer;
  private static CoNLL2009 coNLL2009;

  private static final String IMAGE_FORMAT = "PNG";

  /**
   * @param array
   * @return
   */
  private static List<List<String>> arrayToList(String[][] array) {
    List<List<String>> list = new ArrayList<>();

    for (String[] a : array) {
      String[] s = new String[14];
      // id
      s[0] = a[0]; // id
      // form
      s[1] = a[1]; // form
      // lemma
      s[2] = a[2]; // lemma
      s[4] = "_"; // plemma

      // pos
      s[3] = a[3]; // POS
      s[5] = "_"; // pPOS

      //s[6] = a[4]; // feat
      s[6] = "_"; // feat
      s[7] = "_"; // pfeat

      // head
      s[8] = a[5]; // head
      s[9] = "_"; // phead

      // rel
      s[10] = a[6]; // rel
      s[11] = "_"; // prel
      s[12] = "_";
      s[13] = "_";

      list.add(Arrays.asList(s));
    }

    return list;
  }

  /**
   * @param instance
   * @return
   */
  private static BufferedImage getImage(NLPInstance instance) {

    if (renderer == null) {
      renderer = new SingleSentenceRenderer();
    }

    BufferedImage bufferedImage = new BufferedImage(1, 1, BufferedImage.TYPE_4BYTE_ABGR);
    Graphics2D graphics = bufferedImage.createGraphics();
    Dimension dimension = renderer.render(instance, graphics);

    bufferedImage = new BufferedImage((int) dimension.getWidth() + 5,
            (int) dimension.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);

    BufferedImage subImgage = bufferedImage.getSubimage(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight() - 50);
    bufferedImage = subImgage;

    graphics = bufferedImage.createGraphics();

    renderer.render(instance, graphics);

    return bufferedImage;
  }

  /**
   * @param sentence
   * @param out
   */
  public static void exportToPNG(String[][] sentence, String out) {

    if (coNLL2009 == null) {
      coNLL2009 = new CoNLL2009();
    }


    FileOutputStream fileOutputStream = null;
    try {
      fileOutputStream = new FileOutputStream(out);
      ImageIO.write(getImage(coNLL2009.create(arrayToList(sentence))), IMAGE_FORMAT,
              fileOutputStream);
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

  /**
   * @param sentence
   * @return
   */
  public static byte[] exportToByteArray(String[][] sentence) {

    if (coNLL2009 == null) {
      coNLL2009 = new CoNLL2009();
    }

    ByteArrayOutputStream byteArrayOutputStream = null;
    try {
      byteArrayOutputStream = new ByteArrayOutputStream();
      ImageIO.write(getImage(coNLL2009.create(arrayToList(sentence))), IMAGE_FORMAT,
              byteArrayOutputStream);
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

  public static byte[] exportToBase64(String parsed) {
    return Base64.getEncoder().encode(exportToByteArray(stringToSentence(parsed)));
  }

  /**
   * @param parsedSentence
   * @return
   */
  private static String[][] stringToSentence(String parsedSentence) {
    List<String[]> sentence = new ArrayList<>();

    for (String line : parsedSentence.split("\n")) {
      String[] split = line.trim().split("\t");
      sentence.add(split);
    }

    return sentence.toArray(new String[sentence.size()][]);
  }

  public static void main(String[] args){

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
    exportToBase64(sentence);
  }
}
