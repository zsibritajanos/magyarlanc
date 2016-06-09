package hu.u_szeged.cons.parser;

import hu.u_szeged.config.Config;
import hu.u_szeged.cons.PPReplaceParser;
import hu.u_szeged.pos.purepos.MyPurePos;

public class MyBerkeleyParser {

	/**
	 * singleton
	 */
	private static volatile MyBerkeleyParser instance = null;

	/**
	 * Get singleton.
	 *
	 * @return
	 */
	public static synchronized MyBerkeleyParser getInstance() {
		if (instance == null) {
			Config cfg = Config.getInstance();
			instance = new MyBerkeleyParser(cfg.getConstParserModel(), cfg.getConstNumOfGrammars(), cfg.getConstWriteIncorrectTree());
		}
		return instance;
	}

	private MyBerkeleyParser(String modelFileName) {
		this(modelFileName, 4, false);
	}

	private MyBerkeleyParser(String modelFileName, int numberOfModels, boolean writeIncorrectTree) {
		PPReplaceParser.initReplaceParser(modelFileName, numberOfModels, writeIncorrectTree);
	}

	/**
	 * Constituent parsing of a sentence, using the forms and morphological
	 * analysis.
	 *
	 * @param morph
	 *            two dimensional array of the morphological analysis of the
	 *            forms each row contains two elements, the first is the lemma,
	 *            the second is the full POS (MSD) code e.g.:[alma][Nn-sn]
	 * @return two dimensional array, which contains the constituent parse of a
	 *         sentence on the last coulumn.
	 */
	public String[][] parseSentence(String[][] morph) {
		return PPReplaceParser.parseSentence(morph);
	}

	public String[][] parseSentence(String[] sentence) {
		return MyBerkeleyParser.getInstance().parseSentence(MyPurePos.getInstance().morphParseSentence(sentence));
	}

	public static void main(String[] args) {
		String[] sentence = "A ( - ) * [ ] .".split(" ");

		String[][] cons = MyBerkeleyParser.getInstance().parseSentence(sentence);
		
		System.out.println(cons.length);
		for (String[] t : cons) {
			for (String s : t) {
				System.out.print(s + "\t");
			}
			System.out.println();
		}
	}

}
