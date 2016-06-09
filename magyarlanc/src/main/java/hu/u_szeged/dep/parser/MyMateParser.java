package hu.u_szeged.dep.parser;

import hu.u_szeged.config.Config;
import hu.u_szeged.pos.purepos.MyPurePos;
import is2.data.Cluster;
import is2.data.Long2Int;
import is2.data.SentenceData09;
import is2.parser.*;
import is2.util.DB;
import is2.util.OptionsSuper;

import java.io.*;
import java.util.List;
import java.util.zip.ZipInputStream;

/**
 * Created by zsibritajanos on 2016.01.14..
 */
public class MyMateParser extends is2.parser.Parser {

  /**
   * singleton
   */
  private static volatile MyMateParser instance = null;

  /**
   * Get singleton.
   *
   * @return
   */
  public static synchronized MyMateParser getInstance() {
    if (instance == null) {
      instance = new MyMateParser(Config.getInstance().getDepParserModel());
    }
    return instance;
  }

  private MyMateParser(String modelFileName) {
    super(modelFileName);
  }

  /**
   * Read the models and mapping
   *
   * @param options
   * @param pipe
   * @param params
   * @throws IOException
   */
  public void readModel(OptionsSuper options, Pipe pipe, Parameters params) throws IOException {

    ZipInputStream zis = null;

    try {
      zis = new ZipInputStream(new BufferedInputStream(new FileInputStream(options.modelName)));
    } catch (FileNotFoundException e) {
      try {
        zis = new ZipInputStream(new BufferedInputStream(Parser.class.getClassLoader().getResourceAsStream(options.modelName)));
      } catch (Exception e2) {
        e.printStackTrace();
      }
    }

    zis.getNextEntry();
    DataInputStream dis = new DataInputStream(new BufferedInputStream(zis));

    pipe.mf.read(dis);

    pipe.cl = new Cluster(dis);

    params.read(dis);
    this.l2i = new Long2Int(params.size());

    pipe.extractor = new Extractor[THREADS];

    boolean stack = dis.readBoolean();

    options.featureCreation = dis.readInt();

    for (int t = 0; t < THREADS; t++) pipe.extractor[t] = new Extractor(l2i, stack, options.featureCreation);

    Extractor.initFeatures();
    Extractor.initStat(options.featureCreation);

    for (int t = 0; t < THREADS; t++) pipe.extractor[t].init();

    Edges.read(dis);

    options.decodeProjective = dis.readBoolean();

    Extractor.maxForm = dis.readInt();

    boolean foundInfo = false;
    try {
      String info;
      int icnt = dis.readInt();
      for (int i = 0; i < icnt; i++) {
        info = dis.readUTF();
        //System.out.println(info);
      }
    } catch (Exception e) {
      if (!foundInfo) System.out.println("no info about training");
    }


    dis.close();

    DB.println("Reading data finished");

    Decoder.NON_PROJECTIVITY_THRESHOLD = (float) options.decodeTH;

    Extractor.initStat(options.featureCreation);
  }

  /**
   * Dependency parsing of a sentence, using the forms and morphological
   * analysis.
   *
   * @param morph two dimensional array of the morphological analysis of the forms
   *              each row contains two elements, the first is the lemma, the second
   *              is the full POS (MSD) code e.g.:[alma][Nn-sn]
   * @return two dimensional array, which contains the dependency parsed values,
   * all of the rows contain two elements, the first element is the
   * parent, the second is the relation type, e.g.: [8][ATT]
   */
  public String[][] parseSentence(String[][] morph) {

    String[] form = new String[morph.length];
    String[] lemma = new String[morph.length];
    String[] pos = new String[morph.length];
    String[] feature = new String[morph.length];

    for (int i = 0; i < morph.length; ++i) {
      form[i] = morph[i][0];
      lemma[i] = morph[i][1];
      pos[i] = morph[i][2];
      feature[i] = morph[i][3];
    }

    return parseSentence(form, lemma, pos, feature);
  }

  /**
   * Dependency parsing of a sentence, using the forms, the lemmas, the POS
   * (first character of the MSD code) and the CoNLL2009 formated featurtes
   *
   * @param form
   * @param lemma
   * @param pos
   * @param feature
   * @return
   */
  public String[][] parseSentence(String[] form, String[] lemma, String[] pos, String[] feature) {

    if (form.length > Config.getInstance().getDepMaxSentenceLength()) {
      System.out.println("Too long sentence.");
    }

    SentenceData09 sentenceData09 = new SentenceData09();

    String s[] = new String[form.length + 1];
    String l[] = new String[lemma.length + 1];
    String p[] = new String[pos.length + 1];
    String f[] = new String[feature.length + 1];

    s[0] = "<root>";
    l[0] = "<root-LEMMA>";
    p[0] = "<root-POS>";
    f[0] = "<no-type>";

    for (int i = 0; i < form.length; ++i) {
      s[i + 1] = form[i];
    }

    for (int i = 0; i < lemma.length; ++i) {
      l[i + 1] = lemma[i];
    }

    for (int i = 0; i < pos.length; ++i) {
      p[i + 1] = pos[i];
    }

    for (int i = 0; i < feature.length; ++i) {
      f[i + 1] = feature[i];
    }

    sentenceData09.init(s);
    sentenceData09.setLemmas(l);
    sentenceData09.setPPos(p);
    sentenceData09.setFeats(f);

    if (sentenceData09.length() < 2) {
      return null;
    }

    sentenceData09 = MyMateParser.getInstance().apply(sentenceData09);

    String[][] result = new String[sentenceData09.length()][7];

    for (int i = 0; i < sentenceData09.length(); ++i) {
      result[i][0] = String.valueOf(i + 1);
      result[i][1] = form[i];
      result[i][2] = lemma[i];

      result[i][3] = pos[i];
      result[i][4] = feature[i];

      result[i][5] = String.valueOf(sentenceData09.pheads[i]);
      result[i][6] = sentenceData09.plabels[i];
    }

    return result;
  }

  /**
   * @param form
   * @param lemma
   * @param pos
   * @param feature
   * @return
   */
  public String[][] parseSentence(List<String> form, List<String> lemma, List<String> pos, List<String> feature) {
    return parseSentence(form.toArray(new String[form.size()]),
            lemma.toArray(new String[lemma.size()]),
            pos.toArray(new String[pos.size()]),
            feature.toArray(new String[feature.size()]));
  }

  /**
   * @param sentence
   * @return
   */
  public String[][] parseSentence(String[] sentence) {
    return MyMateParser.getInstance().parseSentence(MyPurePos.getInstance().morphParseSentence(sentence));
  }
}
