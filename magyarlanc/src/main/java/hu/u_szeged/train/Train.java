package hu.u_szeged.train;

import hu.u_szeged.magyarlanc.MorAna;
import hu.u_szeged.magyarlanc.resource.ResourceHolder;
import hu.u_szeged.magyarlanc.resource.Util;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.util.*;

public class Train {

  private static final String[] CORPUSES = { "10elb", "10erv", "1984", "8oelb",
      "cwszt", "gazdtar", "hvg", "mh", "newsml", "np", "nv", "pfred", "szerzj",
      "utas", "win2000" };

  private static final String XML_EXTENSION = ".xml";
  private static final double DIVISION = 0.8;
  private static final String ENCODING = "utf-8";

  private static final String WORDFORM_LEMMA_SEPARATOR = "\t";
  private static final String LEMMA_MSD_SEPARATOR = "\t";

  private static final String STANFORD_TRAIN_WORDFORM_MSD_SEPARATOR = "@";

  private static final String STANFORD_TRAIN_TOKEN_SEPARATOR = " ";

  private static final String CLOSED_TAGS = "! , - . : ; ? Cccp Cccw Ccsp Ccsw Cscp Cssp Cssw S T Z";

  static {
    PropertyConfigurator.configure("data/log4j/properties");
  }

  public static Logger logger = Logger.getLogger(Train.class.getName());

  public static List<Node> getNodes(Document document, String tagName,
      String type) {
    NodeList nodeList = null;
    nodeList = document.getElementsByTagName(tagName);

    List<Node> nodes = null;
    nodes = new LinkedList<Node>();

    Node node = null;
    for (int i = 0; i < nodeList.getLength(); ++i) {
      node = nodeList.item(i);

      if (node.getAttributes().getNamedItem("type") == null) {

      } else {
        if (node.getAttributes().getNamedItem("type").getTextContent()
            .equals(type)) {
          nodes.add(node);
        }
      }
    }

    return nodes;
  }

  public static String getLemma(Node node) {
    return getNodes(getNodes(node, "msd").get(0), "lemma").get(0)
        .getTextContent();
  }

  public static String getMsd(Node node) {
    String msd = null;

    msd = getNodes(getNodes(node, "msd").get(0), "mscat").get(0)
        .getTextContent();
    return msd.substring(1, msd.length() - 1);
  }

  public static String wToTrain(Node node) {

    String spelling = null;
    String lemma = null;
    String msd = null;
    spelling = node.getChildNodes().item(0).getTextContent().trim();

    StringBuffer stringBuffer = null;
    stringBuffer = new StringBuffer();

    NodeList nodes = null;
    nodes = ((Element) node).getElementsByTagName("ana");
    for (int i = 0; i < nodes.getLength(); ++i) {
      lemma = getLemma(nodes.item(i));

      if (spelling.contains(" ")) {
        return splitToTrainString(MultiWordSplitter.splitMW(spelling,
            getLemma(nodes.item(i)), getMsd(nodes.item(i))));
      } else {
        msd = getMsd(nodes.item(i));

        if (ResourceHolder.getPunctations().contains(lemma)) {
          msd = lemma;
        } else if (!lemma.equals("§") && Util.isPunctation(lemma)) {
          msd = "K";
        }

        if ("Nc-sn".equals("Nn-sn")) {
          msd = msd.replace("Nc-", "Nn-");
          msd = msd.replace("Np-", "Nn-");
        }

        stringBuffer.append(spelling + WORDFORM_LEMMA_SEPARATOR + lemma
            + LEMMA_MSD_SEPARATOR + msd);
      }
    }

    return stringBuffer.toString();
  }

  public static String splitToTrainString(String[][] split) {
    StringBuffer stringBuffer = null;
    stringBuffer = new StringBuffer();

    for (String[] s : split) {
      stringBuffer.append(s[0]);
      stringBuffer.append(WORDFORM_LEMMA_SEPARATOR);
      stringBuffer.append(s[1]);
      stringBuffer.append(LEMMA_MSD_SEPARATOR);
      stringBuffer.append(s[2]);
      stringBuffer.append('\n');

    }

    return stringBuffer.toString().trim();
  }

  public static String cToTrain(Node node) {
    StringBuffer stringBuffer = null;
    stringBuffer = new StringBuffer();

    String c = null;
    c = node.getTextContent();

    stringBuffer.append(c);
    stringBuffer.append(WORDFORM_LEMMA_SEPARATOR);
    stringBuffer.append(c);
    stringBuffer.append(LEMMA_MSD_SEPARATOR);

    if (!ResourceHolder.getPunctations().contains(c)) {
      stringBuffer.append("K");
    } else {
      stringBuffer.append(c);
    }

    return stringBuffer.toString();
  }

  public static String choiceToTrain(Node node) {

    for (Node correctedNode : getNodes(
        getNodes(node, new String[] { "corr", "reg" }).get(0), new String[] {
            "w", "c" })) {
      return nodeToTrain(correctedNode);
    }

    return null;
  }

  public static String nodeToTrain(Node node) {
    String nodeName = null;
    nodeName = node.getNodeName();

    if (nodeName.equals("w")) {
      return wToTrain(node);
    } else if (nodeName.equals("c")) {
      return cToTrain(node);
    } else if (nodeName.equals("choice")) {
      return choiceToTrain(node);
    }

    return null;
  }

  private static String sentenceNodeToTrain(Node sentenceNode) {
    StringBuffer stringBuffer = null;
    stringBuffer = new StringBuffer();

    String trainNode = null;

    for (Node node : getNodes(sentenceNode, new String[] { "w", "c", "choice" })) {
      trainNode = nodeToTrain(node);

      if (trainNode != null) {
        stringBuffer.append(trainNode);
        stringBuffer.append("\n");
      }
    }
    return stringBuffer.toString();
  }

  public static List<Node> getNodes(Node node, String... tagNames) {
    NodeList childNodes = null;
    childNodes = ((Element) node).getChildNodes();

    List<Node> nodes = null;
    nodes = new LinkedList<Node>();

    Node tempNode = null;
    String tempNodeName = null;
    for (int i = 0; i < childNodes.getLength(); ++i) {
      tempNode = childNodes.item(i);
      tempNodeName = tempNode.getNodeName();
      for (String tagName : tagNames) {
        if (tempNodeName.equals(tagName)) {
          nodes.add((Element) tempNode);
          break;
        }
      }
    }

    return nodes;
  }

  private static List<Node> readXml(String xml) {
    Document document = null;
    List<Node> divNodes = null;

    try {
      document = DocumentBuilderFactory.newInstance().newDocumentBuilder()
          .parse(new File(xml));

      if (xml.contains("10elb") || xml.contains("10erv")
          || xml.contains("8oelb")) {
        divNodes = getNodes(document, "div", "composition");
      } else if (xml.contains("1984")) {
        divNodes = new ArrayList<Node>();
        for (Node partDivNode : getNodes(document, "div", "part")) {
          divNodes.addAll(getNodes(partDivNode, "div", "chapter"));
        }

      } else if (xml.contains("gazdtar.xml") || xml.contains("szerzj")) {
        divNodes = getNodes(document, "div", "section");
      } else if (xml.contains("cwszt") || xml.contains("hvg")
          || xml.contains("mh") || xml.contains("newsml") || xml.contains("np")
          || xml.contains("nv") || xml.contains("pfred")
          || xml.contains("utas") || xml.contains("win2000")) {
        divNodes = getNodes(document, "div", "article");
      }

    } catch (IOException e) {
      e.printStackTrace();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return divNodes;
  }

  private static void convert(String corpusPath, String trainFile,
      String testFile) {
    StringBuffer xml = null;

    BufferedWriter trainWriter = null;
    BufferedWriter testWriter = null;

    try {
      trainWriter = new BufferedWriter(new OutputStreamWriter(
          new FileOutputStream(trainFile), ENCODING));

      testWriter = new BufferedWriter(new OutputStreamWriter(
          new FileOutputStream(testFile), ENCODING));

    } catch (IOException e) {
      e.printStackTrace();
    }

    int treshold = 0;
    List<Node> divNodes = null;

    for (String corpus : CORPUSES) {
      xml = new StringBuffer(corpusPath);
      xml.append(corpus + XML_EXTENSION);
      divNodes = readXml(xml.toString());
      treshold = (int) (divNodes.size() * DIVISION);

      int sentenceCounter = 0;
      try {
        for (int i = 0; i < divNodes.size(); ++i) {
          ++sentenceCounter;

          for (Node paragraphNode : getNodes(divNodes.get(i), "p")) {
            for (Node sentencNode : getNodes(paragraphNode, "s")) {
              if (i <= treshold) {
                trainWriter.write(sentenceNodeToTrain(sentencNode) + '\n');
              } else {
                testWriter.write(sentenceNodeToTrain(sentencNode) + '\n');
              }
            }
          }
        }
      } catch (IOException e) {
        e.printStackTrace();
      }

      logger.info(xml + "\t" + sentenceCounter);
    }
    try {
      trainWriter.close();
      testWriter.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void stanfordTrain(String in, String out) {
    BufferedReader reader = null;
    BufferedWriter writer = null;

    Set<String> msdCodes = null;
    msdCodes = new TreeSet<String>();

    try {
      reader = new BufferedReader(new InputStreamReader(
          new FileInputStream(in), ENCODING));

      writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
          out), ENCODING));
      String line = null;
      String[] split = null;

      StringBuffer stringBuffer = null;
      stringBuffer = new StringBuffer();

      String reducedMsd = null;
      while ((line = reader.readLine()) != null) {
        split = line.split("\t");
        if (split.length == 3) {
          stringBuffer.append(split[0]);
          stringBuffer.append(STANFORD_TRAIN_WORDFORM_MSD_SEPARATOR);

          reducedMsd = ResourceHolder.getMSDReducer().reduce(split[2]);
          stringBuffer.append(reducedMsd);
          msdCodes.add(reducedMsd);

          stringBuffer.append(STANFORD_TRAIN_TOKEN_SEPARATOR);
        } else {
          writer.write(stringBuffer.toString().trim() + '\n');
          stringBuffer = new StringBuffer();
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        writer.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    for (String msdCode : msdCodes) {
      System.err.print("openClassTags ");

      if (!Arrays.asList(CLOSED_TAGS.split(" ")).contains(msdCode)) {
        System.err.print(msdCode + " ");
      }
    }

  }

  public static void writeLex(Map<String, Set<MorAna>> lexicon, String out) {
    BufferedWriter writer = null;

    try {
      writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
          out), ENCODING));

      for (Map.Entry<String, Set<MorAna>> entry : lexicon.entrySet()) {
        writer.write(entry.getKey());
        for (MorAna morAna : entry.getValue()) {
          writer.write("\t" + morAna.getLemma() + "\t" + morAna.getMsd());
        }
        writer.write('\n');
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        writer.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  public static void writeFreq(Map<String, Integer> frequencies, String out) {
    BufferedWriter writer = null;

    try {
      writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
          out), ENCODING));

      for (Map.Entry<String, Integer> entry : frequencies.entrySet()) {
        writer.write(entry.getKey() + "\t" + entry.getValue() + '\n');
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        writer.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  public static Map<String, Set<MorAna>> getLex(String file) {
    BufferedReader reader = null;

    Map<String, Set<MorAna>> lexicon = null;
    lexicon = new TreeMap<String, Set<MorAna>>();

    try {
      reader = new BufferedReader(new InputStreamReader(new FileInputStream(
          file), ENCODING));

      String line = null;
      String[] split = null;

      while ((line = reader.readLine()) != null) {

        split = line.split(WORDFORM_LEMMA_SEPARATOR);

        if (split.length == 3) {
          if (!lexicon.containsKey(split[0])) {
            lexicon.put(split[0], new TreeSet<MorAna>());
          }
          lexicon.get(split[0]).add(new MorAna(split[1], split[2]));
        }

      }

    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        reader.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    return lexicon;
  }

  public static Map<String, Integer> getFreq(String file) {
    BufferedReader reader = null;

    Map<String, Integer> frequencies = null;
    frequencies = new TreeMap<String, Integer>();

    try {
      reader = new BufferedReader(new InputStreamReader(new FileInputStream(
          file), ENCODING));

      String line = null;
      String[] split = null;

      while ((line = reader.readLine()) != null) {

        split = line.split(WORDFORM_LEMMA_SEPARATOR);

        if (split.length == 3) {
          if (!frequencies.containsKey(split[2])) {

            frequencies.put(split[2], 0);
          }
          frequencies.put(split[2], frequencies.get(split[2]) + 1);
        }

      }

    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        reader.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    return frequencies;
  }

  public static void lex(String input, String output) {
    writeLex(getLex(input), output);
  }

  public static void freq(String input, String output) {
    writeFreq(getFreq(input), output);
  }

  public static void batch() {
    String corpusPath = "./data/Szeged_Korpusz_2.3/";
    String trainFile = "./data/23.train";
    String testFile = "./data/23.test";
    String lexFile = "./data/23.lex";
    String freqFile = "./data/23.freq";
    String stanfordTrainFile = "./data/stanford/23.stanford.train";

    convert(corpusPath, trainFile, testFile);
    stanfordTrain(trainFile, stanfordTrainFile);
    lex(trainFile, lexFile);
    freq(trainFile, freqFile);
  }

  public static void main(String[] args) {
    batch();
  }
}
