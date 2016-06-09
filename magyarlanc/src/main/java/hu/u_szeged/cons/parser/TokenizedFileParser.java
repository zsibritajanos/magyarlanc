package hu.u_szeged.cons.parser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import edu.berkeley.nlp.syntax.Tree;
import hu.u_szeged.cons.util.ConstTool;

public class TokenizedFileParser {

	public static void main(String[] args) {
		if (args.length < 2) {
			System.out.println("Usage: java hu.u_szeged.cons.parser.TokenizedFileParser inFile outFile");
			System.exit(1);
		}
		try {
			BufferedReader reader = new BufferedReader(new FileReader(args[0]));
			BufferedWriter writer = new BufferedWriter(new FileWriter(args[1]));
			
			String line;
			while ((line = reader.readLine())!= null) {
				String[] lineArr = line.split(" ");
				String[][] parsedSent = MyBerkeleyParser.getInstance().parseSentence(lineArr);
				Tree<String> tree = ConstTool.coloumnFormat2Tree(parsedSent);
				writer.write(tree.toString() + '\n');
			}
			
			reader.close();
			writer.flush();
			writer.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
