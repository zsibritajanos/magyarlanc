package hu.u_szeged.converter.univ;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

public class UnivMorphConvert {

	public static void main(String[] args) {
		File file = new File("./data/SZK2.51/");
		listFilesForFolder(file);
	}

	public static void listFilesForFolder(final File folder) {

		for (final File fileEntry : folder.listFiles()) {
			if (fileEntry.isDirectory()) {
				listFilesForFolder(fileEntry);
			} else {
				if (fileEntry.getName().endsWith("pron_2")) {
					convert(fileEntry.toString());
				}
			}
		}
	}

	private static void convert(String file) {
		BufferedReader bufferedReader = null;
		String line = null;
		StringBuffer conversion = new StringBuffer();

		try {
			bufferedReader = new BufferedReader(new InputStreamReader(
					new FileInputStream(file), "UTF-8"));

			while ((line = bufferedReader.readLine()) != null) {

				if (!line.equals("")) {
					//System.out.println(line);

					String[] lineSplit = line.split("\t");
					String word = lineSplit[0];
					String lemma = lineSplit[1];
					//String msd = lineSplit[2];
					String pos = lineSplit[2];
					String feature = lineSplit[3];

					//Twitter-specific annotation
					if(pos.equals("E")) {
						pos = "SYM";
						feature = "_";
					}

					//interjections
					if(pos.equals("I")) {
						pos = "INTJ";
						feature = "_";
					}

					//punctuations
					if(pos.equals("K") || pos.equals(",") || pos.equals(";") ||
							pos.equals("?") || pos.equals(".") || pos.equals(":") ||
							pos.equals("-") || pos.equals("–") || pos.equals("!")) {
						pos = "PUNCT";
						feature = "_";
					}

					//conjunctions
					if(pos.equals("C") && feature.startsWith("SubPOS=c")) {
						pos = "CONJ";
						feature = "_";
					}

					if(pos.equals("C") && feature.startsWith("SubPOS=s")) {
						pos = "SCONJ";
						feature = "_";
					}

					//articles
					if(pos.equals("T") && feature.startsWith("SubPOS=f")) {
						pos = "DET";
						feature = "PronType=Art|Definite=Def";
					}

					if(pos.equals("T") && feature.startsWith("SubPOS=i")) {
						pos = "DET";
						feature = "PronType=Art|Definite=Indef";
					}

					//postpositions
					if(pos.equals("S") && feature.startsWith("SubPOS=t")) {
						pos = "ADP";
						feature = "_";
					}

					//adverbs
					if(pos.equals("R") && !feature.contains("SubPOS=p")) {
						pos = "ADV";
						if(feature.startsWith("SubPOS=x")) {
							feature = "PronType=None|Degree=None";
						} else {
							feature = feature.replace("SubPOS", "PronType");
						}
					}

					//verbal prefixes
					if(pos.equals("R") && feature.contains("SubPOS=p")) {
						pos = prefixPOS(word.toLowerCase());
						feature = prefixFeat(word.toLowerCase());
						lemma = prefixLemma(word.toLowerCase());
					}

					//pronouns
					if(pos.equals("P")) {
						pos = "PRON";
						feature = feature.replace("SubPOS", "PronType");
					}

					//nouns
					if(pos.equals("N") && feature.startsWith("SubPOS=c")) {
						pos = "NOUN";
						feature = feature.replace("SubPOS=c|", "");
					}

					if(pos.equals("N") && feature.startsWith("SubPOS=n")) {
						pos = "NOUN";
						feature = feature.replace("SubPOS=n|", "");
					}

					if(pos.equals("N") && feature.startsWith("SubPOS=p")) {
						pos = "PROPN";
						feature = feature.replace("SubPOS=p|", "");
					}

					//adjectives
					if(pos.equals("A") && feature.startsWith("SubPOS=f")) {
						pos = "ADJ";
						feature = feature.replace("SubPOS=f", "VerbForm=None|NumType=None");
					}

					//participles
					if(pos.equals("A") && !feature.startsWith("SubPOS=f")) {
						pos = "ADJ";
						feature = feature.replace("SubPOS", "VerbForm");
					}

					//auxiliaries
					if(pos.equals("V") && feature.startsWith("SubPOS=a")) {
						pos = "AUX";
						feature = feature.replace("SubPOS=a", "Voice=Act|Aspect=None");
					}

					//more aux to come from dep analysis

					//verbs
					if(pos.equals("V") && !feature.startsWith("SubPOS=a")) {
						pos = "VERB";
						feature = feature.replace("SubPOS=m", "VerbForm=Fin|Voice=Act|Aspect=None");

						//caus voice
						if (feature.contains("SubPOS=s")) {
							feature = feature.replace("SubPOS=s", "VerbForm=Fin|Voice=Cau|Aspect=None");
						}
						//modal aspect
						if (feature.contains("SubPOS=o")) {
							feature = feature.replace("SubPOS=o", "VerbForm=Fin|Voice=Act|Aspect=None");
							feature = feature.replace("Mood=", "Mood=Pot,");
						}
						//freq aspect
						if (feature.contains("SubPOS=f")) {
							feature = feature.replace("SubPOS=f", "VerbForm=Fin|Voice=Act|Aspect=Freq");
						}
						//freq+modal
						if (feature.contains("SubPOS=1")) {
							feature = feature.replace("SubPOS=1", "VerbForm=Fin|Voice=Act|Aspect=Freq");
							feature = feature.replace("Mood=", "Mood=Pot,");
						}
						//caus+modal
						if (feature.contains("SubPOS=2")) {
							feature = feature.replace("SubPOS=2", "VerbForm=Fin|Voice=Cau|Aspect=None");
							feature = feature.replace("Mood=", "Mood=Pot,");
						}
						//caus+freq
						if (feature.contains("SubPOS=3")) {
							feature = feature.replace("SubPOS=3", "VerbForm=Fin|Voice=Cau|Aspect=Freq");
						}
						//caus+modal+freq
						if (feature.contains("SubPOS=4")) {
							feature = feature.replace("SubPOS=4", "VerbForm=Fin|Voice=Cau|Aspect=Freq");
							feature = feature.replace("Mood=", "Mood=Pot,");
						}

						//infinitive
						if (feature.contains("Mood=n")) {
							feature = feature.replace("VerbForm=Fin", "VerbForm=Inf");
							feature = feature.replace("Mood=n|", "");
						}

					}

					//open class
					if(pos.equals("O")) {

						//Oh
						if (feature.contains("SubPOS=h")) {
							pos = "NOUN";
							feature = feature.replace("SubPOS=h", "SubPOS=c");
						}

						//Oe
						if (feature.contains("SubPOS=e")) {
							pos = "SYM";
							feature = feature.replace("SubPOS=e|", "");
						}

						//Oi
						if (feature.contains("SubPOS=i")) {
							pos = "PROPN";
							feature = feature.replace("SubPOS=i|", "");
						}

						//On
						if (feature.contains("SubPOS=n")) {
							pos = "NUM";
							feature = feature.replace("SubPOS=n|", "");
							feature = feature.replace("Type=d", "NumType[sem]=Dot");
							feature = feature.replace("Type=f", "NumType[sem]=Formula");
							feature = feature.replace("Type=q", "NumType[sem]=Quotient");
							feature = feature.replace("Type=r", "NumType[sem]=Result");
							feature = feature.replace("Type=s", "NumType[sem]=Signed");
							feature = feature.replace("Type=t", "NumType[sem]=Time");
							feature = feature.replace("Type=p", "NumType[sem]=Percent");
							feature = feature.replace("Type=g", "NumType[sem]=Grade");
							feature = feature.replace("Type=m", "NumType[sem]=Measure");
							feature = feature.replace("Type=o", "NumType[sem]=Other");
						}
					}

					//multiplication
					if(pos.equals("M") && feature.contains("Cas=6")) {
						pos = "ADV";
						feature = "PronType=Default|Degree=None";
					}

					//ordinal numbers
					if(pos.equals("M") && feature.startsWith("SubPOS=o") && !feature.contains("Cas=6")) {
						pos = "ADJ";
						feature = feature.replace("SubPOS=o", "VerbForm=None|NumType=Ord|Degree=None");
						feature = feature.replaceAll("Form=(l|d|r)\\|", "");
					}

					//cardinal and fraction numbers
					if(pos.equals("M") && feature.startsWith("SubPOS=c") && !feature.contains("Cas=6")) {
						pos = "NUM";
						feature = feature.replace("SubPOS=c", "NumType=Card");
						feature = feature.replaceAll("Form=(l|d|r)\\|", "");
					}

					if(pos.equals("M") && feature.startsWith("SubPOS=f") && !feature.contains("Cas=6")) {
						pos = "NUM";
						feature = feature.replace("SubPOS=f", "NumType=Frac");
						feature = feature.replaceAll("Form=(l|d|r)\\|", "");
					}

					if(pos.equals("M") && feature.startsWith("SubPOS=d") && !feature.contains("Cas=6")) {
						pos = "NUM";
						feature = feature.replace("SubPOS=d", "NumType=Dist");
						feature = feature.replaceAll("Form=(l|d|r)\\|", "");
					}
					//System.out.println(word + "\t" + lemma + "\t" + pos + "\t" + feature);
					conversion.append(word + "\t" + lemma + "\t" + pos + "\t" + feature + "\n");
				} else {
					//System.out.println("\n");
					conversion.append("\n");
				}

			}

			bufferedReader.close();
			replace(conversion, file);

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static String prefixLemma(String origWord) {
		String preverbFile = "./data/igekoto.txt";
		String linePreverb = "";
		String newLemma = "";

		try {
			BufferedReader bufferedReaderPreverb = new BufferedReader(new InputStreamReader(
					new FileInputStream(preverbFile), "UTF-8"));

			while ((linePreverb = bufferedReaderPreverb.readLine()) != null) {

				if (!linePreverb.equals("")) {
					String[] preverbLineSplit = linePreverb.split("\t");

					if (preverbLineSplit.length != 6) {
						continue;
					}

					String form = preverbLineSplit[2];
					String lemma = preverbLineSplit[3];

					if (origWord.equals(form)) {
						newLemma = lemma;
					}
				}
			} bufferedReaderPreverb.close();

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return newLemma;
	}

	private static String prefixFeat(String origWord) {
		String preverbFile = "./data/igekoto.txt";
		String linePreverb = "";
		String newFeat = "";

		try {
			BufferedReader bufferedReaderPreverb = new BufferedReader(new InputStreamReader(
					new FileInputStream(preverbFile), "UTF-8"));

			while ((linePreverb = bufferedReaderPreverb.readLine()) != null) {

				if (!linePreverb.equals("")) {
					String[] preverbLineSplit = linePreverb.split("\t");

					if (preverbLineSplit.length != 6) {
						continue;
					}

					String form = preverbLineSplit[2];
					String feature = preverbLineSplit[5];

					if (origWord.equals(form)) {
						newFeat = feature;
					}
				}
			} bufferedReaderPreverb.close();

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return newFeat;
	}

	private static String prefixPOS(String origWord) {
		String preverbFile = "./data/igekoto.txt";
		String linePreverb = "";
		String newPOS = "";

		try {
			BufferedReader bufferedReaderPreverb = new BufferedReader(new InputStreamReader(
					new FileInputStream(preverbFile), "UTF-8"));

			while ((linePreverb = bufferedReaderPreverb.readLine()) != null) {

				if (!linePreverb.equals("")) {
					String[] preverbLineSplit = linePreverb.split("\t");

					if (preverbLineSplit.length != 6) {
						continue;
					}

					String form = preverbLineSplit[2];
					String pos = preverbLineSplit[4];

					if (origWord.equals(form)) {
						newPOS = pos;
					}
				}
			} bufferedReaderPreverb.close();

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return newPOS;
	}

	private static void replace(StringBuffer conversion, String file) {

		String convertedFile = conversion.toString();

		//nominal features
		//number
		convertedFile = convertedFile.replace("Num=s", "Number=Sing");
		convertedFile = convertedFile.replace("Num=p", "Number=Plur");
		convertedFile = convertedFile.replace("Num=none", "Number=None");

		//definite (conjugation too)
		convertedFile = convertedFile.replace("Def=y", "Definite=Def");
		convertedFile = convertedFile.replace("Def=n", "Definite=Indef");
		convertedFile = convertedFile.replace("Def=2", "Definite=2");

		//degree
		convertedFile = convertedFile.replace("Deg=none", "Degree=None");
		convertedFile = convertedFile.replace("Deg=p", "Degree=Pos");
		convertedFile = convertedFile.replace("Deg=c", "Degree=Cmp");
		convertedFile = convertedFile.replace("Deg=s", "Degree=Sup");
		convertedFile = convertedFile.replace("Deg=e", "Degree=Abs");

		//case
		convertedFile = convertedFile.replace("Cas=n|", "Case=Nom|");
		convertedFile = convertedFile.replace("Cas=a", "Case=Acc");
		convertedFile = convertedFile.replace("Cas=d", "Case=Dat");
		convertedFile = convertedFile.replace("Cas=g", "Case=Gen");
		convertedFile = convertedFile.replace("Cas=i", "Case=Ins");
		convertedFile = convertedFile.replace("Cas=x", "Case=Ill");
		convertedFile = convertedFile.replace("Cas=2", "Case=Ine");
		convertedFile = convertedFile.replace("Cas=e", "Case=Ela");
		convertedFile = convertedFile.replace("Cas=t", "Case=All");
		convertedFile = convertedFile.replace("Cas=3", "Case=Ade");
		convertedFile = convertedFile.replace("Cas=b", "Case=Abl");
		convertedFile = convertedFile.replace("Cas=s", "Case=Sub");
		convertedFile = convertedFile.replace("Cas=p", "Case=Sup");
		convertedFile = convertedFile.replace("Cas=h", "Case=Del");
		convertedFile = convertedFile.replace("Cas=9", "Case=Ter");
		//essive???
		convertedFile = convertedFile.replace("Cas=w", "Case=Ess");
		convertedFile = convertedFile.replace("Cas=f", "Case=Abs"); //!!!!!
		convertedFile = convertedFile.replace("Cas=m", "Case=Tem");
		convertedFile = convertedFile.replace("Cas=c", "Case=Cau");
		convertedFile = convertedFile.replace("Cas=q", "Case=Com");
		convertedFile = convertedFile.replace("Cas=y", "Case=Tra");
		convertedFile = convertedFile.replace("Cas=u", "Case=Dis");
		convertedFile = convertedFile.replace("Cas=l", "Case=Loc");

		//possession
		convertedFile = convertedFile.replace("NumP=s", "Number[psor]=Sing");
		convertedFile = convertedFile.replace("NumP=p", "Number[psor]=Plur");
		convertedFile = convertedFile.replace("NumP=none", "Number[psor]=None");
		convertedFile = convertedFile.replace("PerP=1", "Person[psor]=1");
		convertedFile = convertedFile.replace("PerP=2", "Person[psor]=2");
		convertedFile = convertedFile.replace("PerP=3", "Person[psor]=3");
		convertedFile = convertedFile.replace("PerP=none", "Person[psor]=None");
		convertedFile = convertedFile.replace("NumPd=s", "Number[psed]=Sing");
		convertedFile = convertedFile.replace("NumPd=p", "Number[psed]=Plur");
		convertedFile = convertedFile.replace("NumPd=none", "Number[psed]=None");

		//verbal features
		//verbform
		convertedFile = convertedFile.replace("VerbForm=p", "VerbForm=Part:Pres");
		convertedFile = convertedFile.replace("VerbForm=s", "VerbForm=Part:Past");
		convertedFile = convertedFile.replace("VerbForm=u", "VerbForm=Part:Fut");

		//mood
		convertedFile = convertedFile.replace("Mood=i", "Mood=Ind");
		convertedFile = convertedFile.replace("Mood=c", "Mood=Cnd");
		convertedFile = convertedFile.replace("Mood=m", "Mood=Imp");
		convertedFile = convertedFile.replace("Mood=Pot,i", "Mood=Ind,Pot");
		convertedFile = convertedFile.replace("Mood=Pot,c", "Mood=Cnd,Pot");
		convertedFile = convertedFile.replace("Mood=Pot,m", "Mood=Imp,Pot");

		//tense
		convertedFile = convertedFile.replace("Tense=p", "Tense=Pres");
		convertedFile = convertedFile.replace("Tense=s", "Tense=Past");

		//person
		convertedFile = convertedFile.replace("Per=1", "Person=1");
		convertedFile = convertedFile.replace("Per=2", "Person=2");
		convertedFile = convertedFile.replace("Per=3", "Person=3");
		convertedFile = convertedFile.replace("Per=none", "Person=None");

		//lexical features
		//prontype
		//Rp separately
		convertedFile = convertedFile.replace("PronType=p", "PronType=Prs");
		convertedFile = convertedFile.replace("PronType=d", "PronType=Dem");
		convertedFile = convertedFile.replace("PronType=i", "PronType=Ind");
		convertedFile = convertedFile.replace("PronType=s", "PronType=Prs|Poss=Yes");
		convertedFile = convertedFile.replace("PronType=q", "PronType=Int");
		convertedFile = convertedFile.replace("PronType=r", "PronType=Rel");
		convertedFile = convertedFile.replace("PronType=y", "PronType=Rcp");
		convertedFile = convertedFile.replace("PronType=g", "PronType=Tot");
		convertedFile = convertedFile.replace("PronType=x", "Reflexive=Yes");
		convertedFile = convertedFile.replace("PronType=m", "PronType=Neg");

		writeIntoFile(convertedFile, file);
	}


	private static void writeIntoFile(String text, String fileEntry) {

		BufferedWriter writer = null;
		String file = fileEntry.toString().replace("pron_2", "udfeat");

		try {
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));

			writer.write(text);
			writer.flush();
			writer.close();

		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}


}
