package hu.u_szeged.cons;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.ObjectInputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import edu.berkeley.nlp.PCFGLA.ArrayParser;
import edu.berkeley.nlp.PCFGLA.CoarseToFineNBestParser;
import edu.berkeley.nlp.util.Numberer;
import hu.u_szeged.cons.util.ConstTool;
import hu.u_szeged.cons.util.SpmrlTool;
import hu.u_szeged.util.SpecialCharacters;
import ims.productParser.Parse;
import ims.productParser.ProductParser;
import ims.productParser.ProductParserData;
import is2.parser.Parser;

public class PPReplaceParser extends ProductParser {

	protected static Map<String, Integer> wordFreqs;
	protected static int threshold;
	protected static boolean writeIncorrectTree;

	public PPReplaceParser(CoarseToFineNBestParser p) {
		super(p);
	}

	public static void initReplaceParser(String modelFileName, int modelNumber, boolean writeIncorrectTree) {
		ArrayParser.SILENT = true;

		ProductParserData ppd = PPReplaceParser.loadModel(modelFileName);
		init(ppd, modelNumber);
		ProductParser.kbest = 1;
		ProductParser.skipParsingErrors = false;

		Map<String, Numberer> nums = new HashMap<String, Numberer>();
		nums.put("tags", ppd.getParsers().get(0).getGrammar().getTagNumberer());
		Numberer.setNumberers(nums);

		PPReplaceModel ppdr = (PPReplaceModel) ppd;

		wordFreqs = ppdr.wordFreqs;
		threshold = ppdr.threshold;
		PPReplaceParser.writeIncorrectTree = writeIncorrectTree;

		// System.out.println("Initializing the product parser...");

	}

	public static ProductParserData loadModel(String fn) {
		// System.out.println("Loading the product parser from "+fn);
		ProductParserData ppd = null;
		try {
			ObjectInputStream ois = null;
			try {
				FileInputStream fis = new FileInputStream(fn);
				GZIPInputStream gs = new GZIPInputStream(fis);
				ois = new ObjectInputStream(gs);
			} catch (FileNotFoundException e) {
				try {
					ois = new ObjectInputStream(
							new GZIPInputStream(Parser.class.getClassLoader().getResourceAsStream(fn)));
				} catch (Exception e2) {
					e.printStackTrace();
				}
			}
			ppd = (ProductParserData) ois.readObject();
			ois.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ppd;
	}

	public static void init(ProductParserData ppd, int modelNumber) {
		threads = new LinkedList<ProductParser>();

		if (ppd.getParsers().size() < modelNumber) {
			modelNumber = ppd.getParsers().size();
		}

		for (int id = 0; id < modelNumber; ++id) {
			ProductParser p = new ProductParser(ppd.getParsers().get(id));
			threads.add(p);
		}
	}

	public static String[][] parseSentence(String[][] morph) {

		if (morph == null) {
			return null;
		}

		String[][] result = new String[morph.length][];
		String constInput = cerateConstInput(morph);

		try {
			Parse parse = parse(constInput);

			String[][] parseArr = ConstTool.tree2ColoumnFormat(parse.tree);

			for (int i = 0; i < morph.length; i++) {
				result[i] = Arrays.copyOf(morph[i], morph[i].length + 1);
				result[i][morph[i].length] = parseArr[i][2];
			}

		} catch (Exception e) {
			if (writeIncorrectTree) {
				String[][] parseArr = ConstTool.tree2ColoumnFormat(new Parse(Arrays.asList(constInput.split(" "))).tree);
				for (int i = 0; i < morph.length; i++) {
					result[i] = Arrays.copyOf(morph[i], morph[i].length + 1);
					result[i][morph[i].length] = parseArr[i][2];
				}
			} else {
				for (int i = 0; i < morph.length; i++) {
					result[i] = Arrays.copyOf(morph[i], morph[i].length + 1);
					result[i][morph[i].length] = "_";
				}
				System.err.println("Can not parse a sentence.");
			}
		}

		return result;
	}
	
	private static String cerateConstInput(String[][] morph){
		StringBuilder sentenceBuilder = new StringBuilder();

		if (morph.length > 0) {
			String word = SpmrlTool.convertBrackets(morph[0][0]);

			StringBuilder morphCodeBuilder = new StringBuilder();
			morphCodeBuilder.append(morph[0][2]);
			morphCodeBuilder.append(SpecialCharacters.POS_SEPARATOR);
			morphCodeBuilder.append(morph[0][3]);
			morphCodeBuilder.append(SpecialCharacters.POS_SEPARATOR);

			String morphCode = SpmrlTool.convertPreTerm(morphCodeBuilder.toString());

			sentenceBuilder.append(replaceWord(word, morphCode));
		}

		for (int i = 1; i < morph.length; i++) {

			String word = SpmrlTool.convertBrackets(morph[i][0]);

			StringBuilder morphCodeBuilder = new StringBuilder();
			morphCodeBuilder.append(morph[i][2]);
			morphCodeBuilder.append(SpecialCharacters.POS_SEPARATOR);
			morphCodeBuilder.append(morph[i][3]);
			morphCodeBuilder.append(SpecialCharacters.POS_SEPARATOR);

			String morphCode = SpmrlTool.convertPreTerm(morphCodeBuilder.toString());

			sentenceBuilder.append(' ');
			sentenceBuilder.append(replaceWord(word, morphCode));
		}

		// String sentence =
		// SpmrlTool.convertPreTerm(sentenceBuilder.toString());
		return sentenceBuilder.toString();
	}

	public static String replaceWord(String word, String morphCode) {
		return ConstTool.replaceWord(word, morphCode, wordFreqs, threshold);
	}

	public static void main(String[] args) {
		// initReplaceParser("", 4);

		// System.out.println(parseSentence);
	}
}
