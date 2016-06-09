package hu.u_szeged.cons.util;

import java.io.IOException;
import java.util.List;

import edu.berkeley.nlp.syntax.Tree;

public class SpmrlPtbConverter {

	  public static void main(String[] args) {
		    if (args.length < 2) {
		      System.out
		          .println("Usage: java hu.u_szeged.cons.util.SpmrlPtbConverter originalPtbFile convertedPtbFile [removeGramaticalFunctionONPosLevel [addRoot]]");
		      return;
		    }
		    
		    boolean removeGramaticalFunction = true;
		    if (args.length > 2) {
		    	removeGramaticalFunction = !args[2].equals("0");
		    }
		    
		    boolean addRoot = true;
		    if (args.length > 3) {
		    	addRoot = !args[3].equals("0");
		    }
		    
		    try {
		      List<Tree<String>> sentences = ConstIOTool.readConstTree(args[0]);
		      
		      for (Tree<String> sentence : sentences) {
		        for (Tree<String> nonTerm : sentence.getNonTerminals()) {
		        	if (nonTerm.isPreTerminal()) {
		        		if (removeGramaticalFunction) {
		        			nonTerm.setLabel(SpmrlTool.removeGramFuncFromPreTerminal(nonTerm.getLabel()));
		        		}
		        		nonTerm.setLabel(SpmrlTool.removeLemma(nonTerm.getLabel()));
		        		nonTerm.setLabel(SpmrlTool.convertPreTerm(nonTerm.getLabel()));
		        	} else {
		        		//is non-terminal
		        		if (removeGramaticalFunction) {
		        			nonTerm.setLabel(SpmrlTool.removeGramFuncFromNonTerminal(nonTerm.getLabel()));
		        		}
		        	}
		        }
		      }
		      
		      if (addRoot) {
		    	  sentences = SpmrlTool.addRoot(sentences);
		      }
		      
		      ConstIOTool.writeConstTreeSentence(sentences, args[1]);
		    } catch (IOException e) {
		      // TODO Auto-generated catch block
		      e.printStackTrace();
		    }
		  }
}
