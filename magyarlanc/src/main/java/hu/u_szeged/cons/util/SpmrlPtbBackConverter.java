package hu.u_szeged.cons.util;

import java.io.IOException;
import java.util.List;

import edu.berkeley.nlp.syntax.Tree;

public class SpmrlPtbBackConverter {

	  public static void main(String[] args) {
		    if (args.length < 2) {
		      System.out
		          .println("Usage: java hu.u_szeged.cons.util.SpmrlPtbBackConverter originalPtbFile convertedPtbFile");
		      return;
		    }
		    
		    try {
		      List<Tree<String>> sentences = ConstIOTool.readConstTree(args[0]);
		      
		      for (Tree<String> sentence : sentences) {
		        for (Tree<String> nonTerm : sentence.getNonTerminals()) {
		        	if (nonTerm.isPreTerminal()) {
		        		nonTerm.setLabel(SpmrlTool.backConvertPreTerm(nonTerm.getLabel()));
		        	}
		        }
		      }
		      
		      ConstIOTool.writeConstTreeSentence(sentences, args[1]);
		    } catch (IOException e) {
		      // TODO Auto-generated catch block
		      e.printStackTrace();
		    }
		  }
}
