package hu.u_szeged.dep.removevirtual;

import hu.u_szeged.pos.converter.MSDToCoNLLFeatures;

import java.util.Arrays;
import java.util.List;

public class CorrectPUNCT {

	private final static List<String> relevant = Arrays.asList(new String[] {
	    "!", ",", "-", ".", ":", ";", "?", "�" });
	private static MSDToCoNLLFeatures msdToCoNLLFeatures = null;

	public static boolean isPunctation(String form) {
		for (int i = 0; i < form.length(); ++i) {
			if (Character.isLetterOrDigit(form.charAt(i))) {
				return false;
			}
		}
		return true;
	}

	public static String getPunctLemma(String form) {
		return form;
	}

	public static String getPunctMSD(String form) {

		// a legfontosabb irasjelek MSD kodja maga az irasjel
		if (relevant.contains(form))
			return form;

		// § MSD kodja Nc-sn
		if (form.equals("§"))
			return "Nc-sn";

		// egyeb irasjelek MSD k�dja 'K' lesz
		else
			return "K";
	}

	public static String getPunctFeature(String lemma, String MSDCode) {
		if (msdToCoNLLFeatures == null)
			msdToCoNLLFeatures = new MSDToCoNLLFeatures();

		return msdToCoNLLFeatures.convert(lemma, MSDCode);
	}

	public static void correctCoNLL2009(String in, String out) {
		String[][][] coNLL2009 = null;
		coNLL2009 = Util.readCoNLL2009(in);

		for (String[][] sentence : coNLL2009) {
			for (String[] token : sentence) {
				if (isPunctation(token[1])) {
					token[2] = getPunctLemma(token[1]);
					token[3] = getPunctLemma(token[1]);
					token[4] = getPunctMSD(token[1]);
					token[5] = getPunctMSD(token[1]);
					token[6] = getPunctFeature(getPunctLemma(token[1]),
					    getPunctMSD(token[1]));
					token[7] = getPunctFeature(getPunctLemma(token[1]),
					    getPunctMSD(token[1]));
				}
			}
		}
		Util.writeCoNLL2009(coNLL2009, out);
	}
}
