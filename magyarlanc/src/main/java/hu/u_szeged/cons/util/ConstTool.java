package hu.u_szeged.cons.util;

import hu.u_szeged.util.SpecialCharacters;

import java.io.StringReader;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import edu.berkeley.nlp.syntax.Tree;
import edu.berkeley.nlp.syntax.Trees.PennTreeReader;

public class ConstTool {

	public static String replaceWord(String word, String morphCode, Map<String, Integer> wordFreqs, int threshold) {
		if (wordFreqs.containsKey(word) && wordFreqs.get(word) > threshold) {
			return word;
		}

		return morphCode;
	}

	public static String removeMorphCode(String fullMorph) {
		String[] fullMoprhArr = fullMorph.split(SpecialCharacters.POS_SEPARATOR);
		if (fullMoprhArr.length == 0) {
			System.err.println("Invalid morphological code.");
			return "INVALID";
		}

		return fullMoprhArr[0];
	}
	
	public static void replaceAllPreTerm(List<Tree<String>> trees, Map<String, Integer> wordFreqs, int threshold) {
		for (Tree<String> tree : trees) {
			replaceAllPreTerm(tree, wordFreqs, threshold);
		}
	}

	public static void replaceAllPreTerm(Tree<String> tree, Map<String, Integer> wordFreqs, int threshold) {
		for (Tree<String> preTerm : tree.getPreTerminals()) {
			replace(preTerm, wordFreqs, threshold);
		}
	}

	public static void replace(Tree<String> preTerm, Map<String, Integer> wordFreqs, int threshold) {
		if (preTerm.isPreTerminal()) {
			String preTermLbl = preTerm.getLabel();
			String termLbl = preTerm.getChild(0).getLabel();

			String replacedWord = replaceWord(termLbl, preTermLbl, wordFreqs, threshold);
			preTerm.getChild(0).setLabel(replacedWord);

			preTerm.setLabel(removeMorphCode(preTermLbl));
		}
	}

	public static void convertTree2TrainFormat(Tree<String> tree, Map<String, Integer> wordFreqs, int threshold) {

		if (tree.isPreTerminal()) {
			String label = tree.getLabel();
			label = SpmrlTool.removeLemma(label);
			label = SpmrlTool.convertPreTerm(label);
			label = SpmrlTool.removeGramFuncFromPreTerminal(label);
			tree.setLabel(label);

			replace(tree, wordFreqs, threshold);

		} else if (!tree.isLeaf()) {
			// is nonterminal
			String label = tree.getLabel();
			label = SpmrlTool.removeGramFuncFromNonTerminal(label);
			tree.setLabel(label);
		}

		for (Tree<String> child : tree.getChildren()) {
			convertTree2TrainFormat(child, wordFreqs, threshold);
		}
	}

	public static void convertTrees2TrainFormat(List<Tree<String>> trees, Map<String, Integer> wordFreqs,
			int threshold) {
		for (Tree<String> tree : trees) {
			convertTree2TrainFormat(tree, wordFreqs, threshold);
		}
	}

	/*
	 * Return with [word, full morphological code, constituent] format.
	 */
	public static String[][] tree2ColoumnFormat(Tree<String> tree) {
		String[] chunks = tree.toString().split("\\([^()]*\\)");

		List<String> words = tree.getTerminalYield();
		List<String> preTerms = tree.getPreTerminalYield();

		String[][] sent = new String[words.size()][3];

		for (int i = 0; i < words.size(); i++) {

			sent[i][0] = words.get(i);
			sent[i][1] = preTerms.get(i);

			String chunk = chunks[i] + "*";
			int pos = 0;
			while (pos < chunks[i + 1].length() && chunks[i + 1].charAt(pos) == ')') {
				++pos;
			}
			chunk += chunks[i + 1].substring(0, pos);
			chunks[i + 1] = chunks[i + 1].substring(pos);
			sent[i][2] = chunk.replaceAll("-[^ ]*", "").replaceAll(" ", "");
		}

		return sent;
	}

	/**
	 * @param sent A contituent parsed sentence. The 2. coloumn contains the words, the 3. the pos codes and the last contains the contituent data.
	 * @return 
	 */
	public static Tree<String> coloumnFormat2Tree(String[][] sent) {
		StringBuilder sentStr = new StringBuilder();

		for (int i = 0; i < sent.length; i++) {
			StringBuilder word = new StringBuilder();
			word.append('(');
			word.append(SpmrlTool.convertBrackets(sent[i][3]));
			word.append(' ');
			word.append(SpmrlTool.convertBrackets(sent[i][1]));
			word.append(')');
			String cons = sent[i][sent[i].length-1].replace("*", word.toString());
			sentStr.append(cons);
		}

		PennTreeReader r = new PennTreeReader(new StringReader(sentStr.toString()));
		return r.next();
	}

	public static void main(String[] args) {
		String str = "(ROOT (CP (CP (NP (T##SubPOS_f## A) (ADJP (NP (N##SubPOS_c|Num_s/p|Cas_e/2/n/p/i/x/d/a/f/s/g/y/c/h/3/9/t/b/u/m/l/w/q|NumP_none/s/p|PerP_none/3/1/2|NumPd_none/s## hétfőn)) (A##SubPOS_f|Deg_p/s/c|Num_s|Cas_n/y/w/c/d/f/2/a/s/t/3/i/b/p|NumP_none/s|PerP_none/3|NumPd_none/s## kezdődő)) (ADJP (A##SubPOS_f|Deg_p/s/c|Num_s|Cas_n/y/w/c/d/f/2/a/s/t/3/i/b/p|NumP_none/s|PerP_none/3|NumPd_none/s## őszi)) (ADJP (A##SubPOS_f|Deg_p/s/c|Num_s|Cas_n/y/w/c/d/f/2/a/s/t/3/i/b/p|NumP_none/s|PerP_none/3|NumPd_none/s## parlamenti)) (N##SubPOS_c|Num_s/p|Cas_e/2/n/p/i/x/d/a/f/s/g/y/c/h/3/9/t/b/u/m/l/w/q|NumP_none/s/p|PerP_none/3/1/2|NumPd_none/s## ciklusban)) (XP (PUNC —) (ADVP (R##SubPOS_r|Deg_none## mint)) (CP (NP (T##SubPOS_f## a) (N##SubPOS_c|Num_s/p|Cas_e/2/n/p/i/x/d/a/f/s/g/y/c/h/3/9/t/b/u/m/l/w/q|NumP_none/s/p|PerP_none/3/1/2|NumPd_none/s## kormánykoalíció)) (ADVP (R##SubPOS_x|Deg_p## előre)) (V (V0 (V##SubPOS_m|Mood_i/n/m/c|Tense_s/p|Per_3/none/2/1|Num_s/none/p|Def_n/y## jelezte)))) (PUNC —)) (ADVP (R##SubPOS_x|Deg_c## továbbra)) (C0 (C##SubPOS_c|Form_s/c|Coord_p/w## is)) (ADVP (R##SubPOS_x|Deg_none## csak)) (NP (N##SubPOS_c|Num_s/p|Cas_e/2/n/p/i/x/d/a/f/s/g/y/c/h/3/9/t/b/u/m/l/w/q|NumP_none/s/p|PerP_none/3/1/2|NumPd_none/s## háromhetenként)) (V (V0 (V##SubPOS_m|Mood_i/n/m/c|Tense_s/p|Per_3/none/2/1|Num_s/none/p|Def_n/y## lesz))) (NP (ADJP (A##SubPOS_f|Deg_p/s/c|Num_s|Cas_n/y/w/c/d/f/2/a/s/t/3/i/b/p|NumP_none/s|PerP_none/3|NumPd_none/s## plenáris)) (N##SubPOS_c|Num_s/p|Cas_e/2/n/p/i/x/d/a/f/s/g/y/c/h/3/9/t/b/u/m/l/w/q|NumP_none/s/p|PerP_none/3/1/2|NumPd_none/s## ülés))) (PUNC ,) (C0 (C##SubPOS_c|Form_s/c|Coord_p/w## pedig)) (CP (PP (NP (T##SubPOS_f## a) (M##SubPOS_c|Num_s/p|Cas_n|Form_d/l/r|NumP_none/s/p|PerP_none/3/1|NumPd_none/s## hat) (ADJP (A##SubPOS_f|Deg_p/s/c|Num_s|Cas_n/y/w/c/d/f/2/a/s/t/3/i/b/p|NumP_none/s|PerP_none/3|NumPd_none/s## parlamenti)) (N##SubPOS_c|Num_s/p|Cas_e/2/n/p/i/x/d/a/f/s/g/y/c/h/3/9/t/b/u/m/l/w/q|NumP_none/s/p|PerP_none/3/1/2|NumPd_none/s## frakció)) (S##SubPOS_t## közül)) (NP (M##SubPOS_c|Num_s/p|Cas_n|Form_d/l/r|NumP_none/s/p|PerP_none/3/1|NumPd_none/s## öt)) (ADVP (R##SubPOS_x|Deg_none## ma)) (ADVP (R##SubPOS_x|Deg_none## már)) (NEG (R##SubPOS_m|Deg_none## nem)) (V (V0 (V##SubPOS_m|Mood_i/n/m/c|Tense_s/p|Per_3/none/2/1|Num_s/none/p|Def_n/y## ért))) (PREVERB (R##SubPOS_p|Deg_none## egyet)) (NP (T##SubPOS_f## a) (ADJP (NP (N##SubPOS_c|Num_s/p|Cas_e/2/n/p/i/x/d/a/f/s/g/y/c/h/3/9/t/b/u/m/l/w/q|NumP_none/s/p|PerP_none/3/1/2|NumPd_none/s## februárban)) (A##SubPOS_f|Deg_p/s/c|Num_s|Cas_n/y/w/c/d/f/2/a/s/t/3/i/b/p|NumP_none/s|PerP_none/3|NumPd_none/s## bevezetett)) (N##SubPOS_c|Num_s/p|Cas_e/2/n/p/i/x/d/a/f/s/g/y/c/h/3/9/t/b/u/m/l/w/q|NumP_none/s/p|PerP_none/3/1/2|NumPd_none/s## munkarenddel))) (PUNC .)))";
		System.out.println(str);
		PennTreeReader r = new PennTreeReader(new StringReader(str));
		String[][] arr = tree2ColoumnFormat(r.next());
		System.out.println(Arrays.deepToString(arr));
		System.out.println(coloumnFormat2Tree(arr));
	}

}
