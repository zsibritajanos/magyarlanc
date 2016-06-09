package hu.u_szeged.pos.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Objfx {

	static String[][][] read(String file) {
		BufferedReader bufferedReader = null;
		String line = null;

		List<String[]> sentence = null;
		List<String[][]> sentences = null;
		sentence = new ArrayList<String[]>();
		sentences = new ArrayList<String[][]>();

		try {
			bufferedReader = new BufferedReader(new InputStreamReader(
			    new FileInputStream(file), "UTF-8"));

			while ((line = bufferedReader.readLine()) != null) {
				if (line.trim().equals("")) {
					sentences.add(sentence.toArray(new String[sentence.size()][]));
					sentence = new ArrayList<String[]>();
				} else {
					sentence.add(line.split("\t"));
				}
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return sentences.toArray(new String[sentence.size()][][]);
	}

	public static void countSentences(String file) {
		System.err.println(read(file).length);
	}

	public static void countTokens(String file) {
		int tokens = 0;

		for (String[][] s : read(file)) {
			tokens += s.length;
		}
		System.err.println(tokens);
	}

	public static void listEtalonFX(String file) {
		String[][][] document = null;
		document = read(file);

		for (String[][] sentence : document) {
			for (String[] token : sentence) {
				if (token[2].endsWith("FX"))
					System.err.println(Arrays.toString(token) + "\t"
					    + Arrays.toString(sentence[Integer.parseInt(token[1]) - 1]));
			}
		}
	}

	public static void listPredFX(String file) {
		String[][][] document = null;
		document = read(file);

		for (String[][] sentence : document) {
			for (String[] token : sentence) {
				if (token[4].endsWith("FX"))
					System.err.println(Arrays.toString(token) + "\t"
					    + Arrays.toString(sentence[Integer.parseInt(token[3]) - 1]));
			}
		}
	}

	public static void listFN(String file) {
		String[][][] document = null;
		document = read(file);

		for (String[][] sentence : document) {
			for (String[] token : sentence) {
				// az etalon fx
				if (token[2].endsWith("FX"))
					// parent vagy rel nem egyezik
					if (!token[1].equals(token[3]) || !token[2].equals(token[4])) {
						System.err.println(token[0] + " "
						    + sentence[Integer.parseInt(token[1]) - 1][0]);
					}
			}
		}
	}

	public static void listTP(String file) {
		String[][][] document = null;
		document = read(file);

		for (String[][] sentence : document) {
			for (String[] token : sentence) {
				// fx
				if (token[2].endsWith("FX"))
					if ((token[1].equals(token[3])) && (token[2].equals(token[4])))
						System.err.println(token[0] + " "
						    + sentence[Integer.parseInt(token[1]) - 1][0]);
			}
		}
	}

	public static void listFP(String file) {
		String[][][] document = null;
		document = read(file);

		for (String[][] sentence : document) {
			for (String[] token : sentence) {
				// a predikalt FX
				if (token[4].endsWith("FX"))
					// parent vagy rel nem egyezik
					if (!token[1].equals(token[3]) || !token[2].equals(token[4])) {
						System.err.println(token[0] + " "
						    + sentence[Integer.parseInt(token[3]) - 1][0]);
					}
			}
		}
	}

	public static void etalonStat(String file) {
		String[][][] document = null;
		document = read(file);

		Map<String, Integer> stat = null;
		stat = new TreeMap<String, Integer>();

		for (String[][] sentence : document) {
			for (String[] token : sentence) {
				// a etalon FX
				if (token[2].endsWith("FX")) {
					if (!stat.containsKey(token[2])) {
						stat.put(token[2], 0);
					}
					stat.put(token[2], stat.get(token[2]) + 1);
				}
			}
		}

		for (Map.Entry<String, Integer> entry : stat.entrySet()) {
			System.err.println(entry.getKey() + "\t" + entry.getValue());
		}
	}

	public static void predStat(String file) {
		String[][][] document = null;
		document = read(file);

		Map<String, Integer> stat = null;
		stat = new TreeMap<String, Integer>();

		for (String[][] sentence : document) {
			for (String[] token : sentence) {
				if (token[4].endsWith("FX")) {
					if (!stat.containsKey(token[4])) {
						stat.put(token[4], 0);
					}
					stat.put(token[4], stat.get(token[4]) + 1);
				}
			}
		}

		for (Map.Entry<String, Integer> entry : stat.entrySet()) {
			System.err.println(entry.getKey() + "\t" + entry.getValue());
		}
	}

	public static void LAS(String file) {
		String[][][] document = null;
		document = read(file);

		int cntr = 0;
		int correct = 0;

		for (String[][] sentence : document) {
			for (String[] token : sentence) {
				if (token[1].equals(token[3])
				    && token[2].replace("FX", "").equals(token[4].replace("FX", ""))) {
					++correct;
				}
				++cntr;
			}
		}
		System.err.println((float) correct / cntr);
	}

	public static void ULA(String file) {
		String[][][] document = null;
		document = read(file);

		int cntr = 0;
		int correct = 0;

		for (String[][] sentence : document) {
			for (String[] token : sentence) {
				if (token[1].equals(token[3])) {
					++correct;
				}
				++cntr;
			}
		}
		System.err.println((float) correct / cntr);
	}
}
