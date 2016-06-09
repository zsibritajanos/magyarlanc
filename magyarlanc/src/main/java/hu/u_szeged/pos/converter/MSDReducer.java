package hu.u_szeged.pos.converter;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class MSDReducer {

	private Map<String, String> cache = null;

	private static final Pattern NOUN_PATTERN_1 = Pattern.compile("N.-..---s3");
	private static final Pattern NOUN_PATTERN_2 = Pattern.compile("N.-..---..s");

	private static final Pattern ADJECTIVE_PATTERN_1 = Pattern
	    .compile("A..-..-.--s3");
	private static final Pattern ADJECTIVE_PATTERN_2 = Pattern
	    .compile("A..-..-.--..s");

	private static final Pattern NUMERAL_PATTERN_1 = Pattern
	    .compile("M.-...-.--s3");
	private static final Pattern NUMERAL_PATTERN_2 = Pattern
	    .compile("M.-...-.--..s");

	private static final Pattern OPEN_PATTERN_1 = Pattern.compile("O..-..---s3");
	private static final Pattern OPEN_PATTERN_2 = Pattern.compile("O..-..---..s");

	private static final Pattern VERB_PATTERN_1 = Pattern
	    .compile("V[^a]cp[12]p---y");
	private static final Pattern VERB_PATTERN_2 = Pattern
	    .compile("V[^a]ip1s---y");
	private static final Pattern VERB_PATTERN_3 = Pattern
	    .compile("V[^a]cp3p---y");

	private static final Pattern VERB_PATTERN_4 = Pattern
	    .compile("V[^a]is1[sp]---y");

	public MSDReducer() {
		this.setCache(new HashMap<String, String>());
	}

	/**
	 * Reduce noun.
	 * 
	 * @param msd
	 *          msd code
	 */

	/**
	 * Reduce noun.
	 * 
	 * @param msd
	 *          msd code
	 * @return reduced code
	 */
	private String reduceN(String msd) {
		StringBuffer result = null;
		result = new StringBuffer("N");

		// dative/genitive
		// superessive/essive
		if (msd.length() > 4
		    && (msd.charAt(4) == 'd' || msd.charAt(4) == 'g' || msd.charAt(4) == 'p')) {
			result.append(msd.charAt(4));
		}

		// N.-..---s3
		if (NOUN_PATTERN_1.matcher(msd).find()) {
			result.append('s');
		}

		// N.-..---..s
		if (NOUN_PATTERN_2.matcher(msd).find()) {
			result.append('z');
		}

		return result.toString();
	}

	/**
	 * Reduce other
	 * 
	 * @param msd
	 *          msd code
	 * @return reduced code
	 */
	private String reduceO(String msd) {
		StringBuffer result = null;
		result = new StringBuffer("O");

		// dative/genitive
		// superessive/essive
		if (msd.length() > 5
		    && (msd.charAt(5) == 'd' || msd.charAt(5) == 'g' || msd.charAt(5) == 'p')) {
			result.append(msd.charAt(5));
		}
		// O..-..---s3
		if (OPEN_PATTERN_1.matcher(msd).find()) {
			result.append('s');
		}

		// O..-..---..s
		if (OPEN_PATTERN_2.matcher(msd).find()) {
			result.append('z');
		}

		return result.toString();
	}

	/**
	 * Reduce verb
	 * 
	 * @param msd
	 *          msd code
	 * @return reduced code
	 */
	private String reduceV(String msd) {
		String result = null;

		// Va
		if (msd.startsWith("Va")) {
			result = "Va";
		}

		// mult ideju muvelteto igealakok
		// Vsis[123][sp]---[yn]
		else if (msd.startsWith("Vsis")) {

			if (msd.endsWith("---y")) {
				// 1
				result = "Vsy";
			}
			// festettek
			if (msd.equals("Vsis3p---y")) {
				result = "Vs3py";
			}

			// festettem
			if (msd.equals("Vsis1s---y")) {
				result = "Vs1y";
			}

			else {
				result = "Vs";
			}
		}

		// festetek
		else if (msd.equals("Vsip1s---n")) {
			result = "Vs";
		}

		// olvasnánk
		// V[^a]cp1p---y
		else if (VERB_PATTERN_1.matcher(msd).find()) {
			result = "Vcp";
		}

		// eszek eszem
		// V[^a]ip1s---y
		else if (VERB_PATTERN_2.matcher(msd).find()) {
			result = "Vip";
		}

		// festetnék
		// V[^a]cp3p---y
		else if (VERB_PATTERN_3.matcher(msd).find()) {
			if (msd.charAt(1) == 's') {
				result = "Vs3p";
			} else {
				result = "V3p";
			}
		}

		// festenék
		// V[^a]cp3p---y
		else if (msd.equals("Vmcp1s---n")) {
			result = "V1";
		}

		// festettem
		// V s is[123][sp]---[yn]
		// V[^a]is 1 [sp]---y
		else if (VERB_PATTERN_4.matcher(msd).find()) {
			if (msd.charAt(1) == 's') {
				result = "Vs1y";
			} else {
				result = "Vy";
			}
		}

		// V-m felszlito mod
		else if (msd.length() > 2 && msd.charAt(2) == 'm') {
			result = "Vm";
		}

		// V--p jelen ido egybeeshet multtal pl.: �rt
		else if (msd.length() > 3 && msd.charAt(3) == 'p') {
			result = "Vp";
		}

		else {
			result = "V";
		}

		return result;
	}

	/**
	 * Reduce adjective.
	 * 
	 * @param msd
	 *          msd code
	 * @return reduced code
	 */
	private String reduceA(String msd) {
		StringBuffer result = null;
		result = new StringBuffer("A");

		// igenevek
		if (msd.charAt(1) != 'f') {
			result.append('r');
		}

		// dative/genitive
		// superessive/essive
		if (msd.length() > 5
		    && (msd.charAt(5) == 'd' || msd.charAt(5) == 'g' || msd.charAt(5) == 'p')) {
			result.append(msd.charAt(5));
		}

		// A..-..-.--s3
		if (ADJECTIVE_PATTERN_1.matcher(msd).find()) {
			result.append('s');
		}

		// A..-..-.--..s
		if (ADJECTIVE_PATTERN_2.matcher(msd).find()) {
			result.append('z');
		}
		return result.toString();
	}

	/**
	 * Reduce pronoun.
	 * 
	 * @param msd
	 *          msd code
	 * @return reduced code
	 */
	private String reduceP(String msd) {
		StringBuffer result = null;
		result = new StringBuffer("P");

		// Pq Pr Pp
		if (msd.length() > 1
		    && (msd.charAt(1) == 'q' || msd.charAt(1) == 'r' || msd.charAt(1) == 'p')) {
			if (msd.charAt(1) == 'p') {
				result.append('e');
			} else {
				result.append(msd.charAt(1));
			}
		}

		// dative/genitive
		// superessive/essive
		if (msd.length() > 5
		    && (msd.charAt(5) == 'd' || msd.charAt(5) == 'g' || msd.charAt(5) == 'p')) {
			result.append(msd.charAt(5));
		}

		return result.toString();
	}

	/**
	 * Reduce adverb.
	 * 
	 * @param msd
	 *          msd code
	 * @return reduced code
	 */
	private String reduceR(String msd) {
		StringBuffer result = null;
		result = new StringBuffer("R");

		// Rq Rr Rp
		if (msd.length() > 1
		    && (msd.charAt(1) == 'q' || msd.charAt(1) == 'r' || msd.charAt(1) == 'p')) {
			result.append(msd.charAt(1));
		}

		return result.toString();
	}

	/**
	 * Reduce numeral.
	 * 
	 * @param msd
	 *          msd code
	 * @return reduced code
	 */
	private String reduceM(String msd) {
		StringBuffer result = null;
		result = new StringBuffer("M");

		// fractal
		if (msd.length() > 1 && msd.charAt(1) == 'f') {
			result.append(msd.charAt(1));
		}

		// dative/genitive
		// superessive/essive
		if (msd.length() > 4
		    && (msd.charAt(4) == 'd' || msd.charAt(4) == 'g' || msd.charAt(4) == 'p')) {
			result.append(msd.charAt(4));
		}

		// M.-...-.--s3
		if (NUMERAL_PATTERN_1.matcher(msd).find()) {
			result.append('s');
		}

		// M.-...-.--..s
		if (NUMERAL_PATTERN_2.matcher(msd).find()) {
			result.append('z');
		}
		return result.toString();
	}

	/**
	 * Reduce.
	 * 
	 * @param msd
	 *          msd code
	 * @return reduced code
	 */
	public String reduce(String msd) {

		String reduced = null;

		if (this.getCache().containsKey(msd)) {
			return this.getCache().get(msd);
		}

		if (msd.length() == 1) {
			return msd;
		}

		switch (msd.charAt(0)) {

		case 'N':
			reduced = reduceN(msd);
			break;

		case 'V':
			reduced = reduceV(msd);
			break;

		case 'A':
			reduced = reduceA(msd);
			break;

		case 'P':
			reduced = reduceP(msd);
			break;

		case 'R':
			reduced = reduceR(msd);
			break;

		case 'M':
			reduced = reduceM(msd);
			break;

		case 'O':
			reduced = reduceO(msd);
			break;

		case 'C':
			reduced = msd;
			break;

		// T, S, I, X, Y, Z
		default:
			reduced = String.valueOf(msd.charAt(0));
		}

		this.getCache().put(msd, reduced);

		return reduced;
	}

	public void setCache(Map<String, String> cache) {
		this.cache = cache;
	}

	public Map<String, String> getCache() {
		return cache;
	}

	public String toString() {
		return this.getClass().getName();
	}
}
