package hu.u_szeged.cons.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import edu.berkeley.nlp.syntax.Tree;
import hu.u_szeged.util.SpecialCharacters;

public class ChangePosFromDep {

	public static List<List<String>> readLineSeparatedSentences(String fileName) {
		List<List<String>> sentences = new ArrayList<List<String>>();

		try {
			Scanner sc = new Scanner(new File(fileName));

			String line;
			List<String> sentence = new ArrayList<String>();
			while (sc.hasNextLine()) {
				line = sc.nextLine();
				if (line.length() == 0) {
					sentences.add(sentence);
					sentence = new ArrayList<String>();
				} else {
					sentence.add(line);
				}
			}

			sentences.add(sentence);

			sc.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return sentences;
	}

	public static void main(String[] args) {
		try {
			List<Tree<String>> constSents = ConstIOTool.readConstTree(args[0]);
			List<List<String>> depSents = readLineSeparatedSentences(args[1]);
			
			List<Tree<String>> resultConstSents = new ArrayList<Tree<String>>();
			
			if (depSents.get(depSents.size()-1).size() == 0) {
				depSents.remove(depSents.size() - 1);
			}
			
			if (constSents.size() != depSents.size()) {
				System.out.println("Different number of sentences!");
				System.out.println(constSents.size() + " " + depSents.size());
				System.exit(1);
			}
			
			int wrongSents = 0;
			
			for (int i = 0; i < constSents.size(); i++) {
				Tree<String> constSent = constSents.get(i);
				List<String> depSent = depSents.get(i);
				
				if (constSent.getPreTerminals().size() != depSent.size()) {
					System.out.println("Different number of words!");
					System.out.println(constSent);
					System.out.println(depSent);

					List<Tree<String>> preTerms = constSent.getPreTerminals();
					int max = preTerms.size() > depSent.size() ? depSent.size() : preTerms.size();
					System.out.println("const\tdep");
					for (int j = 0; j < max; j++) {
						System.out.println(preTerms.get(j).getChild(0).getLabel() + "\t" + depSent.get(j).split("\t")[1]);
						//preTerms.get(j).setLabel(depArr[4] + SpecialCharacters.POS_SEPARATOR + depArr[6] + SpecialCharacters.POS_SEPARATOR);
					}					
					
					System.out.println();
					++wrongSents;
				} else {
					List<Tree<String>> preTerms = constSent.getPreTerminals();
					for (int j = 0; j < preTerms.size(); j++) {
						String[] depArr = depSent.get(j).split("\t");
						preTerms.get(j).setLabel(depArr[4] + SpecialCharacters.POS_SEPARATOR + depArr[6] + SpecialCharacters.POS_SEPARATOR);
					}
					
					resultConstSents.add(constSent);
				}
									
				
			}
			
			ConstIOTool.writeConstTreeSentence(resultConstSents, args[2]);
			System.out.println("Different sentences: " + wrongSents);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
