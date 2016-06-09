package hu.u_szeged.cons.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.input.BOMInputStream;

import edu.berkeley.nlp.syntax.Tree;
import edu.berkeley.nlp.syntax.Trees;
import hu.u_szeged.util.IOTool;

public class ConstIOTool {
	public static List<String> readConstSentenceString(String fileName)
			throws IOException {
		return IOTool.readStringList(fileName);
	}

	public static void writeConstSentenceString(List<String> sentences,
			String fileName) throws IOException {
		IOTool.writeStringList(sentences, fileName);
	}

	public static void writeConstTreeSentence(List<Tree<String>> sentences,
			String fileName) throws IOException {
		List<String> stringList = new ArrayList<String>();
		for (Tree<String> tree : sentences) {
			stringList.add(tree.toString());
		}
		IOTool.writeStringList(stringList, fileName);
	}

	public static List<Tree<String>> readConstTree(String fileName)
			throws IOException {
		BufferedReader bfReader = new BufferedReader(new InputStreamReader(
				new BOMInputStream(new FileInputStream(fileName))));

		List<Tree<String>> result = new ArrayList<Tree<String>>();

		Trees.PennTreeReader reader = new Trees.PennTreeReader(bfReader);

		while (reader.hasNext()) {
			try {
				result.add(reader.next());
			} catch (Exception ex) {
				System.out.println("HIBA");
			}
		}

		bfReader.close();

		return result;
	}

	public static List<List<Tree<String>>> readNBestConstTree(String fileName)
			throws IOException {
		BufferedReader bfReader = new BufferedReader(new InputStreamReader(
				new BOMInputStream(new FileInputStream(fileName))));

		List<List<Tree<String>>> result = new ArrayList<List<Tree<String>>>();
		List<Tree<String>> nBestSentence = new ArrayList<Tree<String>>();
		result.add(nBestSentence);

		Trees.PennTreeReader reader;

		String line;
		while ((line = bfReader.readLine()) != null) {
			if (line.length() > 0) {
				try {
					reader = new Trees.PennTreeReader(new StringReader(line));
					nBestSentence.add(reader.next());
				} catch (Exception e) {
					nBestSentence.add(new Tree<String>("ERROR"));
				}
			} else {
				nBestSentence = new ArrayList<Tree<String>>();
				result.add(nBestSentence);
			}
		}

		bfReader.close();

		return result;
	}

	public static void writeNBestConstTree(List<List<Tree<String>>> sentences,
			String fileName) throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));

		for (List<Tree<String>> nBestSentence : sentences) {
			for (Tree<String> line : nBestSentence) {
				writer.write(line + "\n");
			}
			writer.write("\n");
		}

		writer.flush();
		writer.close();
	}
	
	public static List<List<String>> readRawFile(String fileName) throws IOException {
		List<List<String>> result = new ArrayList<List<String>>();
		List<String> sentences = IOTool.readStringList(fileName);
		
		for (String line : sentences) {
			String[] lineArr = line.split(" ");
			result.add(Arrays.asList(lineArr));
		}
		
		return result;
	}
	
	public static void WriteRawFile(List<List<String>> sentences, String fileName) throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));

		for (List<String> line : sentences) {
			if (line.size() > 0) {
				writer.write(line.get(0));
			}
			for (int i = 1; i < line.size(); i++) {
				writer.write(" " + line.get(i));
			}
			writer.write("\n");
		}

		writer.flush();
		writer.close();
	}
}
