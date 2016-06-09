package hu.u_szeged.train;

import hu.u_szeged.magyarlanc.resource.ResourceHolder;
import hu.u_szeged.magyarlanc.resource.Util;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class MultiWordSplitter {

	static {
		PropertyConfigurator.configure("data/log4j/properties");
	}

	public static Logger logger = Logger.getLogger(MultiWordSplitter.class
	    .getName());

	private static final String DEFAULT_ADJECTIVE_MSD = "Afp-sn";
	private static final String DEFAULT_NUMERAL_MSD = "Mc-snd";
	private static final String DEFAULT_NUMERAL_FRACTION_MSD = "Mf-snd";
	private static final String DEFAULT_NOUN_MSD = "Np-sn";

	public static String[][] splitMW(String wordForm, String lemma, String msd) {

		String[][] split = null;

		String[] wordForms = null;
		String[] lemmas = null;

		wordForms = wordForm.split(" ");
		lemmas = lemma.split(" ");

		if (lemmas.length != wordForms.length) {
			logger.debug("Different wordform and lemma length: " + wordForm + "\t"
			    + lemma);
			split = new String[][] { { wordForm.replace(" ", "_"),
			    lemma.replace(" ", "_"), msd } };
		} else {

			switch (msd.charAt(0)) {
			case 'N':
				split = splitN(wordForms, lemmas, msd);
				break;
			case 'M':
				split = splitM(wordForms, lemmas, msd);
				break;
			case 'A':
				split = splitA(wordForms, lemmas, msd);
				break;
			default:
				logger.debug("Can't resolve split: " + wordForm + "\t" + lemma + "\t"
				    + msd);
				split = new String[][] { { wordForm.replace(" ", "_"),
				    lemma.replace(" ", "_"), msd } };
			}
		}

		return changeMsd(split);
	}

	public static String[][] changeMsd(String[][] tokens) {

		for (String[] token : tokens) {
			if (ResourceHolder.getPunctations().contains(token[1])) {
				token[2] = token[1];
			} else if (!token[1].equals("§") && Util.isPunctation(token[0])) {
				token[2] = "K";
			} else if (token[1].equalsIgnoreCase("és")) {
				token[2] = "Ccsw";
			}

			if ("Nc-sn".equals("Nn-sn")) {
				token[2] = token[2].replace("Nc-", "Nn-");
				token[2] = token[2].replace("Np-", "Nn-");
			}
		}

		return tokens;
	}

	public static String[][] splitN(String[] wordForms, String[] lemmas,
	    String msd) {
		String[][] ret = null;
		ret = new String[wordForms.length][3];

		for (int i = 0; i < wordForms.length; ++i) {
			ret[i][0] = wordForms[i];
			ret[i][1] = lemmas[i];

			if (i < wordForms.length - 1) {
				ret[i][2] = DEFAULT_NOUN_MSD;
			} else {
				ret[i][2] = msd;
			}
		}

		return ret;
	}

	public static String[][] splitA(String[] wordForms, String[] lemmas,
	    String msd) {

		String[][] ret = null;
		ret = new String[wordForms.length][3];

		for (int i = 0; i < wordForms.length; ++i) {
			ret[i][0] = wordForms[i];
			ret[i][1] = lemmas[i];

			if (i < wordForms.length - 1) {
				ret[i][2] = DEFAULT_ADJECTIVE_MSD;
			} else {
				ret[i][2] = msd;
			}
		}

		return ret;
	}

	public static String[][] splitM(String[] wordForms, String[] lemmas,
	    String msd) {
		String[][] ret = null;
		ret = new String[wordForms.length][3];

		for (int i = 0; i < wordForms.length; ++i) {
			ret[i][0] = wordForms[i];
			ret[i][1] = lemmas[i];

			if (i < wordForms.length - 1) {
				if (wordForms[i].contains(",")) {
					ret[i][2] = DEFAULT_NUMERAL_FRACTION_MSD;
				} else {
					ret[i][2] = DEFAULT_NUMERAL_MSD;
				}
			} else {
				ret[i][2] = msd;
			}
		}

		return ret;
	}
}
