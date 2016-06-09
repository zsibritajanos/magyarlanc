package hu.u_szeged.magyarlanc.util;

import hu.u_szeged.magyarlanc.Magyarlanc;
import hu.u_szeged.magyarlanc.MorAna;
import hu.u_szeged.magyarlanc.resource.ResourceHolder;
import hu.u_szeged.magyarlanc.resource.Util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class SzK25 {

	private static Writer writer = null;

	private static final int WORD_FORM_INDEX = 3;
	private static final int LEMMA_INDEX = 4;
	private static final int MSD_INDEX = 5;

	private static final String DEFAULT_ADJECTIVE_MSD = "Afp-sn";
	private static final String DEFAULT_NUMERAL_MSD = "Mc-snd";
	private static final String DEFAULT_NUMERAL_FRACTION_MSD = "Mf-snd";
	private static final String DEFAULT_NOUN_MSD = "Np-sn";

	private static final float DEFAULR_TRAIN_TRESHOLD = 0.8f;

	static Map<String, Set<MorAna>> readLexicon(String file) {
		BufferedReader reader = null;
		String line = null;
		Set<MorAna> morAnas = null;
		String[] splitted = null;

		Map<String, Set<MorAna>> lexicon = null;
		lexicon = new TreeMap<String, Set<MorAna>>();

		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(
			    file), "UTF8"));
			while ((line = reader.readLine()) != null) {
				morAnas = new TreeSet<MorAna>();
				splitted = line.split("\t");
				for (int i = 1; i < splitted.length - 1; i++) {
					morAnas.add(new MorAna(splitted[i], splitted[i + 1]));
					i++;
				}
				lexicon.put(splitted[0], morAnas);
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return lexicon;
	}

	public static Map<String, Integer> readIntMap(String file, String encoding,
	    String separator, boolean isCaseSensitive) {

		BufferedReader bufferedReader = null;
		Map<String, Integer> map = null;
		map = new HashMap<String, Integer>();

		String line = null;
		String[] splitted = null;

		try {
			bufferedReader = new BufferedReader(new InputStreamReader(
			    new FileInputStream(file), encoding));
			while ((line = bufferedReader.readLine()) != null) {

				line = line.trim();

				if (!isCaseSensitive) {
					line = line.toLowerCase();
				}

				splitted = line.split(separator);

				if (splitted.length > 1) {
					if (!map.containsKey(splitted[0])) {
						map.put(splitted[0], 0);
					}

					try {
						map.put(splitted[0], Integer.parseInt(splitted[1]));
					} catch (Exception e) {

					}
				}
			}
			bufferedReader.close();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return map;
	}

	/**
	 * Builds a String from the given String array and separator.
	 * 
	 * @param parts
	 *          array of String parts
	 * @param separator
	 *          separator between the parts
	 * @return
	 */
	private static String sallow(String[] parts, String separator) {
		StringBuffer stringBuffer = null;
		stringBuffer = new StringBuffer();

		for (String part : parts) {
			stringBuffer.append(part + separator);
		}

		return stringBuffer.toString().trim();
	}

	/**
	 * Post processes a line from the corpus.
	 * 
	 * @param line
	 *          line from the corpus
	 * @return post processed line
	 */
	private static String postProcess(String line) {
		String[] split = null;
		split = line.split("\t");

		// és
		if (split[LEMMA_INDEX].equals("és")) {
			split[MSD_INDEX] = "Ccsw";
		}

		// &
		if (split[LEMMA_INDEX].equals("&")) {
			split[MSD_INDEX] = "K";
		}

		// sallow and replace spaces
		return sallow(split, "\t");
	}

	/**
	 * Splits an MW adjective.
	 * 
	 * @param line
	 * @return MW split
	 */
	private static String[] splitA(String line) {

		String[] lines = null;

		String[] splittedLine = null;
		String[] splittedWordForm = null;
		String[] splittedLemma = null;

		splittedLine = line.split("\t");
		// word form
		splittedWordForm = splittedLine[WORD_FORM_INDEX].split(" ");
		// lemma
		splittedLemma = splittedLine[LEMMA_INDEX].split(" ");

		lines = new String[splittedWordForm.length];

		StringBuffer stringBuffer = null;

		for (int i = 0; i < splittedWordForm.length; ++i) {
			stringBuffer = new StringBuffer();
			for (int j = 0; j < WORD_FORM_INDEX; ++j) {
				stringBuffer.append(splittedLine[j] + "\t");
			}
			stringBuffer.append(splittedWordForm[i] + "\t");
			stringBuffer.append(splittedLemma[i] + "\t");
			if (i < splittedWordForm.length - 1) {
				// default MSD for the last token
				stringBuffer.append(DEFAULT_ADJECTIVE_MSD);
			} else {
				// original MSD for the last token
				stringBuffer.append(splittedLine[MSD_INDEX]);
			}
			lines[i] = stringBuffer.toString();
		}

		return lines;
	}

	/**
	 * Splits an MW numerals.
	 * 
	 * @param line
	 * @return MW split
	 */
	private static String[] splitM(String line) {

		String[] lines = null;

		String[] splittedLine = null;
		String[] splittedWordForm = null;
		String[] splittedLemma = null;

		splittedLine = line.split("\t");
		// word form
		splittedWordForm = splittedLine[WORD_FORM_INDEX].split(" ");
		// lemma
		splittedLemma = splittedLine[LEMMA_INDEX].split(" ");

		lines = new String[splittedWordForm.length];

		StringBuffer stringBuffer = null;

		for (int i = 0; i < splittedWordForm.length; ++i) {
			stringBuffer = new StringBuffer();
			for (int j = 0; j < WORD_FORM_INDEX; ++j) {
				stringBuffer.append(splittedLine[j] + "\t");
			}
			stringBuffer.append(splittedWordForm[i] + "\t");
			stringBuffer.append(splittedLemma[i] + "\t");
			if (i < splittedWordForm.length - 1) {
				if (splittedLine[WORD_FORM_INDEX].contains(",")) {
					// fraction
					stringBuffer.append(DEFAULT_NUMERAL_FRACTION_MSD);
				} else {
					stringBuffer.append(DEFAULT_NUMERAL_MSD);
				}
			} else {
				// original MSD for the last token
				stringBuffer.append(splittedLine[MSD_INDEX]);
			}
			lines[i] = stringBuffer.toString();
		}

		return lines;
	}

	/**
	 * Splits an MW noun.
	 * 
	 * @param line
	 * @return MW split
	 */
	private static String[] splitN(String line) {
		// result split
		String[] lines = null;

		String[] splittedLine = null;
		String[] splittedWordForm = null;
		String[] splittedLemma = null;

		splittedLine = line.split("\t");
		// word form
		splittedWordForm = splittedLine[WORD_FORM_INDEX].split(" ");
		// lemma
		splittedLemma = splittedLine[LEMMA_INDEX].split(" ");

		lines = new String[splittedWordForm.length];

		StringBuffer stringBuffer = null;
		// System.err.println(line);
		for (int i = 0; i < splittedWordForm.length; ++i) {
			stringBuffer = new StringBuffer();
			for (int j = 0; j < WORD_FORM_INDEX; ++j) {
				stringBuffer.append(splittedLine[j] + "\t");
			}
			stringBuffer.append(splittedWordForm[i] + "\t");
			stringBuffer.append(splittedLemma[i] + "\t");
			if (i < splittedWordForm.length - 1) {
				stringBuffer.append(DEFAULT_NOUN_MSD + "\t");
			} else {
				// original MSD for the last token
				stringBuffer.append(splittedLine[MSD_INDEX]);
			}
			lines[i] = stringBuffer.toString();
		}

		return lines;
	}

	/**
	 * Splits an MW.
	 * 
	 * @param line
	 * @return MW split
	 */
	private static String[] splitMW(String line) {

		String[] split = null;

		if (line.split("\t")[WORD_FORM_INDEX].contains(" ")) {

			// System.err.println(line.split("\t")[MSD_INDEX]);
			System.err.println(line);

			switch (line.split("\t")[MSD_INDEX].charAt(0)) {
			case 'N':
				split = splitN(line);
				break;
			case 'M':
				split = splitM(line);
				break;
			case 'A':
				split = splitA(line);
				break;
			}

			if (split != null) {
				for (int i = 0; i < split.length; ++i) {
					split[i] = postProcess(split[i]);
				}
			}

			return split;
		}

		return null;
	}

	/**
	 * 
	 * @param document
	 * @param tagName
	 * @param type
	 * @return
	 */
	private static List<Node> getNodes(Document document, String tagName,
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

	/**
	 * 
	 * @param node
	 * @param tagNames
	 * @return
	 */
	private static List<Node> getNodes(Node node, String... tagNames) {
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

	/**
	 * 
	 * @param node
	 * @return
	 */
	private static String getSpelling(Node node) {
		return node.getChildNodes().item(0).getTextContent().trim();
	}

	/**
	 * 
	 * @param node
	 * @return
	 */
	private static String getLemma(Node node) {
		return getNodes(getNodes(node, "msd").get(0), "lemma").get(0)
		    .getTextContent();
	}

	/**
	 * 
	 * @param node
	 * @return
	 */
	private static String getMsd(Node node) {
		String msd = null;

		msd = getNodes(getNodes(node, "msd").get(0), "mscat").get(0)
		    .getTextContent();

		msd = msd.substring(1, msd.length() - 1);

		return msd;
	}

	/**
	 * 
	 * @param node
	 * @throws IOException
	 */
	private static void printW(Node node) throws IOException {

		String spelling = null;
		spelling = node.getChildNodes().item(0).getTextContent().trim();

		writer.write(spelling);

		NodeList nodes = null;
		nodes = ((Element) node).getElementsByTagName("ana");
		for (int i = 0; i < nodes.getLength(); ++i) {
			writer.write("\t" + getLemma(nodes.item(i)).replace("+", "") + "\t"
			    + getMsd(nodes.item(i)));
		}

		nodes = ((Element) node).getElementsByTagName("anav");

		for (int i = 0; i < nodes.getLength(); ++i) {
			writer.write("\t" + getLemma(nodes.item(i)) + "\t"
			    + getMsd(nodes.item(i)));
		}
	}

	/**
	 * 
	 * @param node
	 * @throws DOMException
	 * @throws IOException
	 */
	private static void printC(Node node) throws DOMException, IOException {

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

	/**
	 * 
	 * @param node
	 * @throws IOException
	 */
	private static void printChoice(Node node) throws IOException {
		try {
			for (Node correctedNode : getNodes(
			    getNodes(node, new String[] { "corr", "reg" }).get(0), new String[] {
			        "w", "c" })) {
				printNode(correctedNode);
			}

		} catch (IndexOutOfBoundsException e) {
			System.err.println(node.getTextContent());
		}
	}

	/**
	 * 
	 * @param node
	 * @throws IOException
	 */
	private static void printNode(Node node) throws IOException {
		String nodeName = null;
		nodeName = node.getNodeName();
		if (nodeName.equals("w")) {
			printW(node);
		} else if (nodeName.equals("c")) {
			printC(node);
		} else if (nodeName.equals("choice")) {
			printChoice(node);
		}
	}

	/**
	 * 
	 * @param nodes
	 * @throws DOMException
	 * @throws IOException
	 */
	private static void printPrefix(Node... nodes) throws DOMException,
	    IOException {
		for (Node node : nodes) {
			writer.write(node.getAttributes().getNamedItem("id").getTextContent()
			    + "\t");
		}
	}

	private static void convertXMLtoTXT(String XML, String txt) {
		Document document = null;

		if (writer == null)
			try {
				writer = new BufferedWriter(new FileWriter(txt));
			} catch (IOException e) {
				e.printStackTrace();
			}

		try {
			document = DocumentBuilderFactory.newInstance().newDocumentBuilder()
			    .parse(new File(XML));

			// "1984"
			// for (Node partDivNode : getNodes(document, "div", "part")) {
			// for (Node divNode : getNodes(partDivNode, "div", "chapter")) {

			// "10elb", "10erv", "8oelb"
			// for (Node divNode : getNodes(document, "div", "composition")) {

			// "nv", "mh", "hvg", "np", "cwszt", "win2000", "utas", "pfred"
			// for (Node divNode : getNodes(document, "div", "article")) {

			// "gazdtar", "szerzj"
			for (Node divNode : getNodes(document, "div", "section")) {
				for (Node pNode : getNodes(divNode, "p")) {
					for (Node sNode : getNodes(pNode, "s")) {
						for (Node node : getNodes(sNode,
						    new String[] { "w", "c", "choice" })) {
							try {
								printPrefix(divNode, pNode, sNode);
								printNode(node);
								writer.write("\n");
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
	}

	private static String[][] read(String file) {
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

	private static void splitMWs(String file, String out) {
		String corpus = null;
		corpus = Util.readFileToString(file);

		Writer writer = null;
		try {
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
			    out), "UTF-8"));

			String[] split = null;
			for (String line : corpus.split("\n")) {
				if (line.length() > 0) {
					// split MW
					split = splitMW(line);
					if (split != null) {
						// split lines
						for (String s : split) {
							writer.write(s + "\n");
						}
					} else {
						writer.write(line + "\n");
					}
				} else {
					writer.write("\n");
				}
			}
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void writeTrain(String[][] sentences, String out) {
		Writer writer = null;

		System.err.println(out);
		try {
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
			    out), "UTF-8"));

			for (String[] sentence : sentences) {
				// train
				for (String token : sentence) {
					writer.write(token.split("\t")[WORD_FORM_INDEX].replace(" ", "_")
					    + "@"
					    + ResourceHolder.getMSDReducer().reduce(
					        token.split("\t")[MSD_INDEX]) + " ");
				}
				writer.write("\n");
			}

			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void writeTest(String[][] sentences, String out) {
		Writer writer = null;
		String msd = null;
		try {
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
			    out), "UTF-8"));

			for (String[] sentence : sentences) {
				for (String token : sentence) {

					msd = token.split("\t")[MSD_INDEX].replace("Np", "Nn");
					msd = msd.replace("Nc", "Nn");

					writer.write(token.split("\t")[WORD_FORM_INDEX].replace(" ", "_")
					    + "\t" + token.split("\t")[LEMMA_INDEX].replace(" ", "_") + "\t"
					    + msd + "\n");
				}
				writer.write("\n");
			}

			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void writeLexicon(String[][] sentences, String out) {
		Map<String, Set<MorAna>> lexicon = null;
		lexicon = new TreeMap<String, Set<MorAna>>();

		String msd = null;
		String[] split = null;
		for (String[] sentence : sentences) {
			for (String token : sentence) {
				split = token.split("\t");

				if (!lexicon.containsKey(split[WORD_FORM_INDEX])) {
					lexicon.put(split[WORD_FORM_INDEX], new TreeSet<MorAna>());
				}

				msd = split[MSD_INDEX].replace("Np", "Nn");
				msd = msd.replace("Nc", "Nn");

				lexicon.get(split[WORD_FORM_INDEX]).add(
				    new MorAna(split[LEMMA_INDEX], msd));
			}
		}

		writeLexiconToFile(lexicon, out);
	}

	public static void writeLexiconToFile(Map<String, Set<MorAna>> lexicon,
	    String out) {
		Writer writer = null;

		try {
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
			    out), "UTF-8"));
			for (Map.Entry<String, Set<MorAna>> entry : lexicon.entrySet()) {
				writer.write(entry.getKey());

				for (MorAna morAna : entry.getValue()) {
					writer.write("\t" + morAna.getLemma() + "\t" + morAna.getMsd());
				}
				writer.write("\n");
			}

			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void writeFreqs(String[][] sentences, String out) {
		Map<String, Integer> freqs = null;
		freqs = new TreeMap<String, Integer>();

		String[] split = null;
		String msd = null;
		for (String[] sentence : sentences) {
			for (String token : sentence) {
				split = token.split("\t");

				msd = split[MSD_INDEX].replace("Np", "Nn");
				msd = msd.replace("Nc", "Nn");

				if (!freqs.containsKey(msd)) {
					freqs.put(msd, 0);
				}

				freqs.put(msd, freqs.get(msd) + 1);
			}
		}

		Writer writer = null;

		try {
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
			    out), "UTF-8"));
			for (Map.Entry<String, Integer> entry : freqs.entrySet()) {
				writer.write(entry.getKey() + "\t" + entry.getValue());
				writer.write("\n");
			}

			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void writeResources(String file) {
		String[][] sentences = null;
		sentences = read(file);

		int treshold = (int) (sentences.length * DEFAULR_TRAIN_TRESHOLD);

		writeTrain(Arrays.copyOfRange(sentences, 0, treshold), file + ".train");
		writeLexicon(Arrays.copyOfRange(sentences, 0, treshold), file + ".lexicon");
		writeFreqs(Arrays.copyOfRange(sentences, 0, treshold), file + ".freqs");

		writeTest(Arrays.copyOfRange(sentences, treshold, sentences.length), file
		    + ".test");

	}

	public static String[] getColumn(String[] sentence, int index) {
		String[] column = null;
		column = new String[sentence.length];

		for (int i = 0; i < column.length; ++i) {
			column[i] = sentence[i].split("\t")[index];
		}

		return column;
	}

	public static void predicate(String file, String out) {
		String[][] sentences = read(file);

		String[] wordform = null;
		String[] lemma = null;
		String[] msd = null;

		String[][] pred = null;

		int correct = 0;
		int tokenCounter = 0;

		Writer writer = null;
		try {
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
			    out), "UTF-8"));

			for (String[] sentence : sentences) {
				wordform = getColumn(sentence, 0);
				lemma = getColumn(sentence, 1);
				msd = getColumn(sentence, 2);

				pred = Magyarlanc.morphParseSentence(wordform);

				for (int i = 0; i < pred.length; ++i) {
					writer.write(wordform[i] + "\t");
					writer.write(lemma[i] + "\t");
					writer.write(msd[i] + "\t");
					writer.write(pred[i][1] + "\t");
					writer.write(pred[i][2] + "\n");

					// if (!lemma[i].equals(pred[i][1]) || !msd[i].equals(pred[i][2])) {
					// System.err.println(lemma[i] + "\t" + pred[i][1] + "\t" + msd[i]
					// + "\t" + (pred[i][2]));
					// }

					if (lemma[i].equalsIgnoreCase(pred[i][1])
					    && msd[i].equals(pred[i][2])) {
						++correct;
					} else {
						// System.err.println(wordform[i] + "\t" + lemma[i] + "\t" + msd[i]
						// + "\t" + pred[i][1] + "\t" + pred[i][2]);
					}
					++tokenCounter;

				}
				writer.write("\n");
			}
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.err.println(tokenCounter);

		System.err.println((float) correct / tokenCounter);
	}

	public static void buildResources(String xml) {
		// convert XML to TXT
		convertXMLtoTXT(xml, xml + ".txt");

		// split MWs
		splitMWs(xml + ".txt", xml + ".txt.split");

		// write train
		writeResources(xml + ".txt.split");
	}

	public static void merge(String path, String[] files, String extension,
	    String out) {
		StringBuffer stringBuffer = null;
		stringBuffer = new StringBuffer();
		for (String file : files) {
			stringBuffer.append(readFileToString(path + file + extension, "UTF-8"));
		}

		writeStringToFile(stringBuffer.toString(), out, "UTF-8");
	}

	public static void mergeFrequencies(String path, String[] files,
	    String extension, String out) {

		Map<String, Integer> map = null;
		map = new TreeMap<String, Integer>();

		for (String file : files) {
			for (Map.Entry<String, Integer> entry : readIntMap(
			    path + file + extension, "UTF-8", "\t", true).entrySet()) {
				if (!map.containsKey(entry.getKey())) {
					map.put(entry.getKey(), 0);
				}
				map.put(entry.getKey(), map.get(entry.getKey()) + entry.getValue());
			}
		}

		writeMapToFile(map, out, "\t", "utf-8");
	}

	public static void writeMapToFile(Map<String, Integer> map, String file,
	    String separator, String encoding) {
		writeMapToFile(map, new File(file), separator, encoding);
	}

	public static void writeMapToFile(Map<String, Integer> map, File file,
	    String separator, String encoding) {
		Writer writer = null;

		try {
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
			    file), encoding));
			for (Entry<String, Integer> entry : map.entrySet()) {
				writer.write(entry.getKey() + separator + entry.getValue() + "\n");
			}
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void writeStringToFile(String s, File file, String encoding) {
		Writer writer = null;

		try {
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
			    file), encoding));
			writer.write(s);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void writeStringToFile(String s, String file, String encoding) {
		writeStringToFile(s, new File(file), encoding);
	}

	/**
	 * Reads the specified file to a String with the specified encoding.
	 * 
	 * @param file
	 *          name of the file
	 * @param encoding
	 *          encoding the file
	 * @return content of the file
	 */
	public static String readFileToString(File file, String encoding) {

		BufferedReader reader = null;

		StringBuffer stringBuffer = null;
		stringBuffer = new StringBuffer();

		String line = null;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(
			    file), encoding));

			while ((line = reader.readLine()) != null) {
				stringBuffer.append(line + "\n");
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return stringBuffer.toString();
	}

	public static void mergeLexicons(String path, String[] files,
	    String extension, String out) {

		Map<String, Set<MorAna>> lexicon = null;
		lexicon = new TreeMap<String, Set<MorAna>>();

		for (String file : files) {
			for (Map.Entry<String, Set<MorAna>> entry : readLexicon(
			    path + file + extension).entrySet()) {
				if (!lexicon.containsKey(entry.getKey())) {
					lexicon.put(entry.getKey(), new TreeSet<MorAna>());
				}
				lexicon.get(entry.getKey()).addAll(entry.getValue());
			}
		}

		writeLexiconToFile(lexicon, out);
	}

	public static String readFileToString(File file) {
		return readFileToString(file, "UTF-8");
	}

	public static String readFileToString(String file, String encoding) {
		return readFileToString(new File(file), encoding);
	}

	public static String readFileToString(String file) {
		return readFileToString(new File(file), "UTF-8");
	}

	public static void mergeResources(String path, String[] files) {
		// merge train files
		merge(path, files, ".xml.txt.split.train", path
		    + "szeged.xml.txt.split.train");

		// merge test files
		merge(path, files, ".xml.txt.split.test", path
		    + "szeged.xml.txt.split.test");

		// merge frequencies
		mergeFrequencies(path, files, ".xml.txt.split.freqs", path
		    + "szeged.xml.txt.split.freqs");

		// merge corpuses
		mergeLexicons(path, files, ".xml.txt.split.lexicon", path
		    + "szeged.xml.txt.split.lexicon");

	}

	public static void main(String args[]) {

		// String[] courpus = new String[] { "nv", "mh", "hvg", "np", "cwszt",
		// "win2000", "utas", "pfred", "newsml" };

		// String[] courpus = new String[] { "1984" };

		// String[] courpus = new String[] { "10elb", "10erv", "8oelb" };

		// String[] courpus = new String[] { "gazdtar", "szerzj" };

		// for (String c : courpus) {
		// buildResources("./data/szk2.5/xml/" + c + ".xml");
		// }

		// String[] corpus = new String[] { "gazdtar", "szerzj", "10elb", "10erv",
		// "8oelb", "1984", "nv", "mh", "hvg", "np", "cwszt", "win2000", "utas",
		// "pfred", "newsml" };
		//
		// mergeResources("./data/szk2.5/xml/", corpus);

		long start = System.currentTimeMillis();
		predicate("./data/szk2.5/xml/szeged.xml.txt.split.test",
		    "./data/szk2.5/xml/szeged.xml.txt.split.test.pred.full");
		System.err.println(System.currentTimeMillis() - start);
 
		// predicate("./25.test",
		// "./data/szk2.5/xml/newsml_1.xml.txt.split.test.pred");

	}
}
