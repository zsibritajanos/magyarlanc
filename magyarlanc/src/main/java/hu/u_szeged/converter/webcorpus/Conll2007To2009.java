package hu.u_szeged.converter.webcorpus;

import hu.u_szeged.pos.converter.MSDToCoNLLFeatures;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;

public class Conll2007To2009 {

  private static final MSDToCoNLLFeatures msdToConllFeatures = new MSDToCoNLLFeatures();

  public static void convert(String in, String out) {
    BufferedReader reader = null;
    Writer writer = null;
    String line = null;
    String[] split = null;

    String num;
    String wordform;
    String lemma;
    String msd;
    String parent;
    String deprel;

    StringBuffer conll2009 = null;

    try {
      reader = new BufferedReader(new InputStreamReader(
          new FileInputStream(in), "utf-8"));
      writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
          out), "utf-8"));

      while ((line = reader.readLine()) != null) {

        if (line.trim().length() == 0) {
          writer.write("\n");
        } else {

          conll2009 = new StringBuffer();

          split = line.split("\t");
          num = split[0];
          wordform = split[1];
          lemma = split[2];
          msd = split[3];
          parent = split[6];
          deprel = split[7];

          if (msd.equals("VAN") || msd.equals("ELL")) {
            conll2009.append(num + "\t_\t_\t_\t" + msd + "\t" + msd
                + "\t_\t_\t");
          } else {
            if (msd.equals("null")) {
              msd = wordform;
            }
            conll2009.append(num + "\t" + wordform + "\t" + lemma + "\t"
                + lemma + "\t" + msd.charAt(0) + "\t" + msd.charAt(0) + "\t");
            conll2009.append(msdToConllFeatures.convert(lemma, msd) + "\t"
                + msdToConllFeatures.convert(lemma, msd) + "\t");
          }
          conll2009.append(parent + "\t" + parent + "\t" + deprel + "\t"
              + deprel);

          writer.write(conll2009.toString() + "\n");

        }

      }

    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        reader.close();
        writer.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

  }

  public static void main(String[] args) {
    // convert("./data/webcorpus/facebook.conll",
    // "./data/webcorpus/facebook.conll-2009");
    // convert("./data/webcorpus/faq.conll", "./data/webcorpus/faq.conll-2009");
    // convert("./data/webcorpus/web.conll", "./data/webcorpus/web.conll-2009");

    convert("./data/webcorpus_1222/face_1222.conll",
        "./data/webcorpus_1222/face_1222.conll-2009");
    convert("./data/webcorpus_1222/faq_1222.conll",
        "./data/webcorpus_1222/faq_1222.conll-2009");
    convert("./data/webcorpus_1222/web_1222.conll",
        "./data/webcorpus_1222/web_1222.conll-2009");

  }
}
