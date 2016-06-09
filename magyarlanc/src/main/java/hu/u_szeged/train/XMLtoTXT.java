package hu.u_szeged.train;

import hu.u_szeged.magyarlanc.MorAna;
import hu.u_szeged.magyarlanc.resource.ResourceHolder;
import hu.u_szeged.magyarlanc.resource.Util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XMLtoTXT {

  private static Writer writer = null;
  private static Writer corpusWriter = null;
  private static Writer freqsWriter = null;

  private static Map<String, Integer> freqs = null;
  private static Map<String, Set<MorAna>> corpus = null;

  public static List<String> splitSentenceNsamedEntites(List<String> sentence) {
    String[] splittedLine = null;
    String[] splittedNamedEntity = null;
    String[] splittedLemma = null;
    StringBuffer stringBuffer = null;

    for (int i = 0; i < sentence.size(); ++i) {

      splittedLine = sentence.get(i).split("\t");

      // nem kivanatos _ elejen/vegen
      if (splittedLine[1].length() > 1
          && (splittedLine[1].startsWith("_") || splittedLine[1].endsWith("_"))) {
        // System.out.println(splittedLine[1]);
      }

      if ((splittedLine[3].startsWith("Np") || splittedLine[3].startsWith("X")
          || splittedLine[3].startsWith("M")
          || splittedLine[3].startsWith("Afp") || splittedLine[3]
            .startsWith("Nc"))
          && splittedLine[1].contains("_")
          && splittedLine[1].length() > 1) {

        splittedNamedEntity = splittedLine[1].split("_");
        splittedLemma = splittedLine[2].split("_");

        stringBuffer = new StringBuffer(splittedLine[0]);
        try {
          stringBuffer.append("\t" + splittedNamedEntity[0]);
        } catch (ArrayIndexOutOfBoundsException e) {
          // System.out.println(sentence.get(i));
        }
        stringBuffer.append("\t" + splittedLemma[0]);

        // System.out.println(splittedLemma[0]);

        // utolso elotti tokenek
        if (splittedLine[3].startsWith("X")) {
          stringBuffer.append("\t" + "Np-sn");
          stringBuffer.append("\t" + "N");
        }

        // N
        else if (splittedLine[3].startsWith("Np")) {
          stringBuffer.append("\t" + "Np-sn");
          stringBuffer.append("\t" + "N");
        }

        // M
        else if (splittedLine[3].startsWith("M")) {

          if (splittedLine[1].contains(",")) {
            if (!splittedNamedEntity[0].contains(",")) {
              stringBuffer.append("\t" + "Mc-snd");
            } else {
              stringBuffer.append("\t" + "Mf-snd");
            }
          } else {
            stringBuffer.append("\t" + "Mc-snd");
          }
          stringBuffer.append("\t" + "M");
        }

        // AFP
        else if (splittedLine[3].startsWith("Afp")) {
          if (Character.isDigit(splittedLine[1].charAt(0))) {
            if (splittedLine[1].contains(",")) {
              stringBuffer.append("\t" + "Mf-snd");
            } else {
              stringBuffer.append("\t" + "Mc-snd");
            }
            stringBuffer.append("\t" + "NUM");
          } else {
            stringBuffer.append("\t" + "Np-sn");
            stringBuffer.append("\t" + "N");

          }
        } else {
          stringBuffer.append("\t" + "Np-sn");
          stringBuffer.append("\t" + splittedLine[4]);
        }

        stringBuffer.append("\t" + splittedLine[5]);
        stringBuffer.append("\t"
            + String.valueOf(Integer.valueOf(splittedLine[0]) + 1));
        if (splittedLine[3].startsWith("M")) {
          stringBuffer.append("\t" + "NUM");
        } else {
          stringBuffer.append("\t" + "NE");
        }

        stringBuffer.append("\t" + splittedLine[8]);
        stringBuffer.append("\t" + splittedLine[9]);

        sentence.set(i, stringBuffer.toString());

        // sentence = renumberOrdinal(sentence, i + 1,
        // splittedNamedEntity.length - 1);
        //
        // sentence = renumberParent(sentence, i + 1,
        // splittedNamedEntity.length - 1);
        //
        int token = 1;

        for (int j = i + 1; j < i + splittedNamedEntity.length; ++j) {
          stringBuffer = new StringBuffer(String.valueOf(j + 1));
          stringBuffer.append("\t" + splittedNamedEntity[token]);
          try {
            stringBuffer.append("\t" + splittedLemma[token]);
          } catch (Exception e) {
            // System.err.println(sentence);
          }

          if (splittedLine[3].startsWith("X")) {
            if (j == (i + splittedNamedEntity.length - 1)) {
              stringBuffer.append("\t" + "X");
              stringBuffer.append("\t" + "X");

            } else {
              stringBuffer.append("\t" + "Np-sn");
              stringBuffer.append("\t" + "N");

            }
          }

          else if (splittedLine[3].startsWith("Afp")) {
            System.err.println(Arrays.toString(splittedNamedEntity));
            if (j == (i + splittedNamedEntity.length - 1)) {
              if (splittedLine[0].equals("és")) {
                stringBuffer.append("\t" + "Ccsw");
                stringBuffer.append("\t" + "Ccsw");
              } else {
                stringBuffer.append("\t" + splittedLine[3]);
                stringBuffer.append("\t" + splittedLine[4]);
              }

            } else {
              stringBuffer.append("\t" + "Np-sn");
              stringBuffer.append("\t" + "N");
            }
          }

          else if (splittedLine[3].startsWith("Np")) {
            System.err.println(Arrays.toString(splittedNamedEntity));
            if (j == (i + splittedNamedEntity.length - 1)) {
              if (splittedLine[0].equals("és")) {
                stringBuffer.append("\t" + "Ccsw");
                stringBuffer.append("\t" + "Ccsw");
              } else {
                stringBuffer.append("\t" + splittedLine[3]);
                stringBuffer.append("\t" + splittedLine[4]);
              }

            } else {
              stringBuffer.append("\t" + "Np-sn");
              stringBuffer.append("\t" + "N");
            }
          } else {
            stringBuffer.append("\t" + splittedLine[3]);
            stringBuffer.append("\t" + splittedLine[4]);
          }

          stringBuffer.append("\t" + splittedLine[5]);

          // utols token
          if (j + 1 == i + splittedNamedEntity.length) {

            if (Integer.parseInt(splittedLine[6]) > j
                - splittedNamedEntity.length + 1) {
              stringBuffer.append("\t"
                  + String.valueOf(Integer.valueOf(splittedLine[6])
                      + splittedNamedEntity.length - 1));
            } else {
              stringBuffer.append("\t"
                  + String.valueOf(Integer.valueOf(splittedLine[6])));

            }

            stringBuffer.append("\t" + splittedLine[7]);
          } else {
            stringBuffer.append("\t" + String.valueOf(j + 2));
            stringBuffer.append("\t" + "NE");
          }
          stringBuffer.append("\t" + splittedLine[8]);
          stringBuffer.append("\t" + splittedLine[9]);
          ++token;
          sentence.add(j, stringBuffer.toString());
        }
      }
    }

    return sentence;
  }

  public static void addTofreq(String msd) {
    if (!freqs.containsKey(msd)) {
      freqs.put(msd, 0);
    }
    freqs.put(msd, (freqs.get(msd) + 1));
  }

  public static void addToCorpus(String spelling, MorAna morAna) {
    if (!corpus.containsKey(spelling)) {
      corpus.put(spelling, new TreeSet<MorAna>());
    }
    corpus.get(spelling).add(morAna);
  }

  public static List<Node> getNodes(Document document, String tagName,
      String type) {
    NodeList nodeList = null;
    nodeList = document.getElementsByTagName(tagName);

    List<Node> nodes = null;
    nodes = new LinkedList<Node>();

    Node node = null;
    for (int i = 0; i < nodeList.getLength(); ++i) {
      node = nodeList.item(i);
      if (node.getAttributes().getNamedItem("type").getTextContent()
          .equals(type)) {
        nodes.add(node);
      }
    }

    return nodes;
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

  public static String getSpelling(Node node) {
    return node.getChildNodes().item(0).getTextContent().trim();
  }

  public static String getLemma(Node node) {
    return getNodes(getNodes(node, "msd").get(0), "lemma").get(0)
        .getTextContent();
  }

  public static String getMsd(Node node, boolean reduce) {
    String msd = null;

    msd = getNodes(getNodes(node, "msd").get(0), "mscat").get(0)
        .getTextContent();

    msd = msd.substring(1, msd.length() - 1);

    if (reduce) {
      return ResourceHolder.getMSDReducer().reduce(msd);
    } else {
      return msd;
    }
  }

  public static void printW(Node node, boolean reduce, boolean train)
      throws IOException {

    String spelling = null;
    spelling = node.getChildNodes().item(0).getTextContent().trim();

    writer.write(spelling.replace(" ", " "));

    NodeList nodes = null;
    nodes = ((Element) node).getElementsByTagName("ana");
    for (int i = 0; i < nodes.getLength(); ++i) {
      if (train) {
        writer.write("@" + getMsd(nodes.item(i), reduce));
        addTofreq(getMsd(nodes.item(i), false));
      }

      else {
        writer.write("\t" + getLemma(nodes.item(i)).replace("+", "") + "\t"
            + getMsd(nodes.item(i), reduce));
      }
    }

    nodes = ((Element) node).getElementsByTagName("anav");

    for (int i = 0; i < nodes.getLength(); ++i) {
      if (train) {
        addToCorpus(spelling,
            new MorAna(getLemma(nodes.item(i)), getMsd(nodes.item(i), false)));
      }

      else {
        writer.write("\t" + getLemma(nodes.item(i)) + "\t"
            + getMsd(nodes.item(i), reduce));
      }
    }

  }

  public static void printC(Node node, boolean train) throws DOMException,
      IOException {

    String c = null;
    c = node.getTextContent();
    writer.write(c);

    if (!ResourceHolder.getPunctations().contains(c)) {
      writer.write("\t" + node.getTextContent() + "\t" + "K" + "\t"
          + node.getTextContent() + "\t" + "K");
    } else {
      writer.write("\t" + node.getTextContent() + "\t" + node.getTextContent()
          + "\t" + node.getTextContent() + "\t" + node.getTextContent());
    }
  }

  public static void printChoice(Node node, boolean reduce, boolean train)
      throws IOException {
    try {
      for (Node correctedNode : getNodes(
          getNodes(node, new String[] { "corr", "reg" }).get(0), new String[] {
              "w", "c" })) {
        printNode(correctedNode, reduce, train);
      }

    } catch (IndexOutOfBoundsException e) {
      System.err.println(node.getTextContent());
    }
  }

  public static void printNode(Node node, boolean reduce, boolean train)
      throws IOException {
    String nodeName = null;
    nodeName = node.getNodeName();
    if (nodeName.equals("w")) {
      printW(node, reduce, train);
    } else if (nodeName.equals("c")) {
      printC(node, train);
    } else if (nodeName.equals("choice")) {
      printChoice(node, reduce, train);
    }
  }

  public static void writeCorpus() {
    try {
      for (Map.Entry<String, Set<MorAna>> entry : corpus.entrySet()) {
        corpusWriter.write(entry.getKey().replace(" ", "_"));
        for (MorAna morAna : entry.getValue()) {
          corpusWriter.write("\t"
              + morAna.getLemma().replace("+", "").replace(" ", "_") + "\t"
              + morAna.getMsd());
        }
        corpusWriter.write("\n");
      }
      corpusWriter.flush();
      corpusWriter.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void writeFreqs() {
    try {
      for (Map.Entry<String, Integer> entry : freqs.entrySet()) {
        freqsWriter.write(entry.getKey() + "\t" + entry.getValue() + "\n");
      }
      freqsWriter.flush();
      freqsWriter.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void printPrefix(Node... nodes) throws DOMException,
      IOException {
    for (Node node : nodes) {
      writer.write(node.getAttributes().getNamedItem("id").getTextContent()
          + "\t");
    }
  }

  public static void write(String XML, String txt, boolean reduce, boolean train) {
    Document document = null;

    if (train) {
      freqs = new TreeMap<String, Integer>();
      corpus = new TreeMap<String, Set<MorAna>>();
    }

    if (writer == null)
      try {
        writer = new BufferedWriter(new FileWriter(txt));
      } catch (IOException e) {
        e.printStackTrace();
      }

    try {
      document = DocumentBuilderFactory.newInstance().newDocumentBuilder()
          .parse(new File(XML));

      // for (Node partDivNode : getNodes(document, "div", "part")) {
      // for (Node chapterDivNode : getNodes(partDivNode, "div", "chapter")) {
      // for (Node divNode : getNodes(document, "div", "composition")) {
      for (Node divNode : getNodes(document, "div", "article")) {
        for (Node pNode : getNodes(divNode, "p")) {
          for (Node sNode : getNodes(pNode, "s")) {
            for (Node node : getNodes(sNode,
                new String[] { "w", "c", "choice" })) {
              try {
                if (!train)
                  printPrefix(divNode, pNode, sNode);
                printNode(node, reduce, train);
                if (!train) {
                  writer.write("\n");
                } else {
                  writer.write(" ");
                }
              } catch (IOException e) {
                e.printStackTrace();
              }
            }
            writer.write("\n");
          }
        }
      }

      writer.flush();
      writer.close();
      writer = null;
    } catch (IOException e) {
      e.printStackTrace();
    } catch (Exception e) {
      e.printStackTrace();
    }

    if (train) {
      try {
        freqsWriter = new BufferedWriter(new FileWriter(txt.replace(".txt",
            "_freqs.txt")));
        corpusWriter = new BufferedWriter(new FileWriter(txt.replace(".txt",
            "_corpus.txt")));
      } catch (IOException e) {
        e.printStackTrace();
      }
      writeCorpus();
      writeFreqs();
    }
  }

  public static String[][] read(String file) {
    List<String> sentence = null;
    sentence = new ArrayList<String>();

    String s = null;
    s = Util.readFileToString(file);

    List<String[]> sentences = null;
    sentences = new ArrayList<String[]>();

    for (String line : s.split("\n")) {

      if (line.trim().equals("")) {
        sentences.add(sentence.toArray(new String[sentence.size()]));
        sentence = new ArrayList<String>();
      } else {
        sentence.add(line);
      }
    }
    return sentences.toArray(new String[sentences.size()][]);
  }

  public static void convertToTrain(String file) {

  }

  public static void main(String args[]) {

    String corpus = null;
    corpus = "./data/Szeged_Korpusz_2.3/newsml.xml";

    write("./data/Szeged_Korpusz_2.3/newsml.xml",
        "./data/Szeged_Korpusz_2.3/newsml.txt", false, false);
    //
    // String c = null;
    // c = Util.readFileToString("./data/szk2.5/txt/newsml_1.txt");
    // Writer writer = null;
    // try {
    // writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
    // "./data/szk2.5/txt/newsml-split.txt"), "UTF-8"));
    // } catch (IOException e) {
    // e.printStackTrace();
    // }
    // String[] split = null;
    // for (String line : c.split("\n")) {
    // if (line.length() > 0) {
    // // System.err.println(line);
    // split = splitMW(line);
    // if (split != null) {
    // for (String s : split) {
    // try {
    // writer.write(s + "\n");
    // } catch (IOException e) {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // }
    // }
    // } else {
    // try {
    // writer.write(line + "\n");
    // } catch (IOException e) {
    // e.printStackTrace();
    // }
    // }
    // } else {
    // try {
    // writer.write("\n");
    // } catch (IOException e) {
    // e.printStackTrace();
    // }
    // }
    //
    // }
    //
    // try {
    // writer.close();
    // } catch (IOException e) {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // }
    //
    // String file = null;
    // file = "./data/szk2.5/txt/newsml-split.txt";
    // String[][] sentences = read(file);
    //
    // int treshold = (int) (sentences.length * 0.8);
    //
    // System.err.println(treshold);
    //
    // Set<String> reducedSet = new TreeSet<String>();
    //
    // int cntr = 0;
    //
    // Writer writer2 = null;
    // if (writer2 == null)
    // try {
    // writer2 = new BufferedWriter(new OutputStreamWriter(
    // new FileOutputStream("./data/szk2.5/txt/newsml-train-80.txt"),
    // "UTF-8"));
    // } catch (IOException e) {
    // e.printStackTrace();
    // }
    //
    // for (String[] sentence : sentences) {
    // if (++cntr < treshold) {
    // for (String token : sentence) {
    // reducedSet.add(ResourceHolder.getMSDReducer().reduce(
    // token.split("\t")[MSD_INDEX]));
    //
    // try {
    // writer2.write(token.split("\t")[WORD_FORM_INDEX]
    // + "@"
    // + ResourceHolder.getMSDReducer().reduce(
    // token.split("\t")[MSD_INDEX]) + " ");
    // } catch (IOException e) {
    // e.printStackTrace();
    // }
    //
    // // System.err.print();
    // }
    // try {
    // writer2.write("\n");
    // } catch (IOException e) {
    // e.printStackTrace();
    // }
    // }
    //
    // }
    //
    // try {
    // writer2.close();
    // } catch (IOException e) {
    // e.printStackTrace();
    // }

    // for (String s : reducedSet)
    // System.err.println(s);
    // System.err.println(reducedSet);
    // System.out.println(freqs);
    // System.out.println(corpus);

    // Document document = null;
    //
    // try {
    // document = DocumentBuilderFactory.newInstance().newDocumentBuilder()
    // .parse(new File("./data/szk2.5/xml/newsml_1.xml"));
    // } catch (SAXException e) {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // } catch (IOException e) {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // } catch (ParserConfigurationException e) {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // }
  }
}
