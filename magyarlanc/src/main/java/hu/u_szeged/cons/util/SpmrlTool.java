package hu.u_szeged.cons.util;

import java.util.ArrayList;
import java.util.List;

import edu.berkeley.nlp.syntax.Tree;

public class SpmrlTool {

	public static String convertPreTerm(String posTag){
		return posTag.replaceAll("/", "~sla~").replaceAll("-", "~hyp~").replaceAll("_", "~hb~").replaceAll("=", "_");
	}
	
	public static String backConvertPreTerm(String posTag){
		return posTag.replaceAll("~sla~", "/").replaceAll("~hyp~", "-").replaceAll("~hb~", "_").replaceAll("_", "=");
	}
	
	public static String removeGramFuncFromNonTerminal(String nonTerm) {
		return nonTerm.replaceAll("-.*", "");
	}
	
	public static String removeGramFuncFromPreTerminal(String preTerm) {
		return preTerm.replaceAll("-[^#]*(##[^ \\(\\)#]*##)", "$1");
	}
	
	public static String removeLemma(String preTerm) {
		return preTerm.replaceAll("#lem[^\\|]*\\|", "#");
	}
	
	public static String convertBrackets(String word) {
		return word.replaceAll("\\(", "*LRB*").replaceAll("\\)", "*RRB*");
	}
	
	public static List<Tree<String>> addRoot(List<Tree<String>> sentences) {
		List<Tree<String>> result = new ArrayList<Tree<String>>();
		
		for (Tree<String> sentence : sentences) {
			Tree<String> root = new Tree<String>("ROOT", new ArrayList<Tree<String>>());
			root.getChildren().add(sentence);
			result.add(root);
		}
		
		return result;
	}
}
