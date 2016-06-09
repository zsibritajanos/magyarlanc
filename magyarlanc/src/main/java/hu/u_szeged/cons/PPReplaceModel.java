package hu.u_szeged.cons;

import java.util.Map;

import ims.productParser.ProductParserData;

public class PPReplaceModel extends ProductParserData {
	
	private static final long serialVersionUID = -3268918617190943304L;
	
	protected Map<String, Integer> wordFreqs;
	protected int threshold;

	public Map<String, Integer> getWordFreqs() {
		return wordFreqs;
	}

	public void setWordFreqs(Map<String, Integer> wordFreqs) {
		this.wordFreqs = wordFreqs;
	}

	public int getThreshold() {
		return threshold;
	}

	public void setThreshold(int threshold) {
		this.threshold = threshold;
	}
	
}
