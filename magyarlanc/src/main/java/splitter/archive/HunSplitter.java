package splitter.archive;

import edu.northwestern.at.morphadorner.corpuslinguistics.sentencesplitter.DefaultSentenceSplitter;
import edu.northwestern.at.morphadorner.corpuslinguistics.sentencesplitter.SentenceSplitter;
import edu.northwestern.at.morphadorner.corpuslinguistics.tokenizer.DefaultWordTokenizer;
import edu.northwestern.at.morphadorner.corpuslinguistics.tokenizer.WordTokenizer;
import hu.u_szeged.magyarlanc.resource.ResourceHolder;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class HunSplitter {

  private boolean lineSentence = false;

  private WordTokenizer tokenizer = null;
  private SentenceSplitter splitter = null;
  private StringCleaner stringCleaner = null;

  private List<List<String>> splittedTemp = null;

  public HunSplitter() {
    this(false);
  }

  public HunSplitter(WordTokenizer wordTokenizer,
                     SentenceSplitter sentenceSplitter, boolean lineSentence) {
    this.setLineSentence(lineSentence);
    this.setStringCleaner(new StringCleaner());

    this.setSplitter(sentenceSplitter);
    this.setTokenizer(wordTokenizer);
  }

  public HunSplitter(boolean lineSentence) {
    this.setLineSentence(lineSentence);
    this.setStringCleaner(new StringCleaner());

    this.setSplitter(new DefaultSentenceSplitter());
    this.setTokenizer(new DefaultWordTokenizer());
  }

  public List<String> tokenize(String sentence) {
    List<String> splitted = null;
    sentence = this.getStringCleaner().cleanString(sentence);

    splitted = tokenizer.extractWords(sentence);

    splitted = reSplit2Sentence(splitted);
    splitted = reTokenizeSentence(splitted);

    return splitted;
  }

  public String[] tokenizeToArray(String sentence) {
    List<String> tokenized = null;
    tokenized = tokenize(sentence);

    return tokenized.toArray(new String[tokenized.size()]);
  }

  /**
   * Insert the specified character, between the tokens.
   *
   * @param tokens splitted tokens
   * @param c      character to instert
   * @return
   */
  private static String[] insertChars(String[] tokens, char c) {
    List<String> ret = null;
    ret = new ArrayList<String>();

    ret.add(tokens[0]);

    for (int i = 1; i < tokens.length; ++i) {
      ret.add(String.valueOf(c));
      ret.add(tokens[i]);
    }

    return ret.toArray(new String[ret.size()]);
  }

  private List<List<String>> simpleSplit(String text) {
    List<List<String>> splitted = null;

    text = this.getStringCleaner().cleanString(text);

    // text = normalizeQuotes(text);
    // text = normalizeHyphans(text);

    // text = addSpaces(text);

    splitted = splitter.extractSentences(text, tokenizer);

    splittedTemp = splitted;

    splitted = reSplit1(splitted, text);
    splittedTemp = splitted;
    splitted = reSplit2(splitted);
    splittedTemp = splitted;
    splitted = reTokenize(splitted);
    splittedTemp = splitted;

    // return splitHyphens(splitted);
    return splitted;
  }

  private List<List<String>> lineSentenceSplit(String text) {
    List<List<String>> splitted = null;
    splitted = new LinkedList<List<String>>();
    for (String line : text.split("\n")) {
      splitted.addAll(simpleSplit(line));
    }
    return splitted;
  }

  public List<List<String>> split(String text)
          throws StringIndexOutOfBoundsException {

    if (this.isLineSentence()) {
      return lineSentenceSplit(text);
    } else {
      return simpleSplit(text);
    }
  }

  public int[] getSentenceOffsets(String text) {
    return this.getSplitter().findSentenceOffsets(text, this.split(text));
  }

  public int[] getSentenceOffsetsTemp(String text) {
    return this.getSplitter().findSentenceOffsets(text, splittedTemp);
  }

  public int[] getTokenOffsets(String text) {
    int[] ret = null;
    List<List<String>> splitted = null;
    int[] sentenceOffsets = null;
    int[] tokenOffsets = null;
    String sentence = null;

    sentenceOffsets = this.getSentenceOffsets(text);
    splitted = this.split(text);
    int counter = 0;

    for (int i = 0; i < splitted.size(); ++i) {
      for (int j = 0; j < splitted.get(i).size(); ++j) {
        ++counter;
      }
    }

    ret = new int[counter + 1];

    counter = 0;

    for (int i = 0; i < splitted.size(); ++i) {
      sentence = text.substring(sentenceOffsets[i], sentenceOffsets[i + 1]);
      tokenOffsets = this.getTokenizer().findWordOffsets(sentence,
              splitted.get(i));
      for (int j = 0; j < splitted.get(i).size(); ++j) {
        ret[counter] = sentenceOffsets[i] + tokenOffsets[j];
        ++counter;
      }
    }

    ret[counter] = text.length();

    return ret;
  }

  public int[] getTokenOffsetsTemp(String text) {
    int[] ret = null;
    int[] sentenceOffsets = null;
    int[] tokenOffsets = null;
    String sentence = null;

    sentenceOffsets = this.getSentenceOffsetsTemp(text);
    int counter = 0;

    for (int i = 0; i < splittedTemp.size(); ++i) {
      for (int j = 0; j < splittedTemp.get(i).size(); ++j) {
        ++counter;
      }
    }

    ret = new int[counter + 1];

    counter = 0;

    for (int i = 0; i < splittedTemp.size(); ++i) {
      sentence = text.substring(sentenceOffsets[i], sentenceOffsets[i + 1]);
      tokenOffsets = this.getTokenizer().findWordOffsets(sentence,
              splittedTemp.get(i));
      for (int j = 0; j < splittedTemp.get(i).size(); ++j) {
        ret[counter] = sentenceOffsets[i] + tokenOffsets[j];
        ++counter;
      }
    }

    ret[counter] = text.length();

    return ret;
  }

  /*
   * Separate ' 'm 's 'd 're 've 'll n't endings into apart tokens.
   */
  private static List<String> reTokenizeSentence(List<String> sentence) {
    String token = null;
    String tokenLowerCase = null;

    for (int i = 0; i < sentence.size(); ++i) {
      token = sentence.get(i);
      tokenLowerCase = token.toLowerCase();
      if (tokenLowerCase.endsWith("'") && tokenLowerCase.length() > 1) {
        sentence.set(i, token.substring(0, token.length() - 1));
        sentence.add(i + 1, token.substring(token.length() - 1));
        ++i;
      }
      if ((tokenLowerCase.endsWith("'m") || tokenLowerCase.endsWith("'s") || tokenLowerCase
              .endsWith("'d")) && tokenLowerCase.length() > 2) {
        sentence.set(i, token.substring(0, token.length() - 2));
        sentence.add(i + 1, token.substring(token.length() - 2));
        ++i;
      }
      if ((tokenLowerCase.endsWith("'re") || tokenLowerCase.endsWith("'ve")
              || tokenLowerCase.endsWith("'ll") || tokenLowerCase.endsWith("n't"))
              && tokenLowerCase.length() > 3) {
        sentence.set(i, token.substring(0, token.length() - 3));
        sentence.add(i + 1, token.substring(token.length() - 3));
        ++i;
      }
    }

    return sentence;
  }

  private static List<List<String>> reTokenize(List<List<String>> sentences) {
    for (List<String> sentence : sentences) {
      reSplit2Sentence(sentence);
    }

    return sentences;
  }

  private List<List<String>> reSplit1(List<List<String>> sentences, String text) {
    String lastToken = null;
    String firstToken = null;
    int tokenNumber = 0;
    int[] tokenOffsets = null;
    List<String> sentence = null;

    tokenOffsets = getTokenOffsetsTemp(text);
    for (int i = 0; i < sentences.size(); i++) {
      sentence = sentences.get(i);

      // nem lehet üres mondat
      if (sentence.size() > 0) {

				/*
         * 1 betűs rövidítés pl.: George W. Bush
				 */

        // utolsó token pl. (W.)
        lastToken = sentence.get(sentence.size() - 1);
        // nem lehet üres token
        if (lastToken.length() > 0) {
          // ha az utolsó karkter '.'
          if (lastToken.charAt(lastToken.length() - 1) == '.') {
            // ha a token hossza 2 (W.)
            if (lastToken.length() == 2) {
              // ha betű nagybetű ('W.', de 'i.' nem)
              if (Character
                      .isUpperCase(lastToken.charAt(lastToken.length() - 2))) {
                // ha nem az utolsó mondat
                if (sentences.size() > i + 1) {
                  sentences.get(i).addAll(sentences.get(i + 1));
                  sentences.remove(i + 1);
                  // ha nem az első mondat
                  if (i > -1) {
                    --i;
                  }
                }
              }
            }
          }

					/*
					 * 2 betűs pl.: Sz.
					 */

          if (lastToken.length() == 3) {
            // az első betű nagybetű (Sz. de 'az.' nem jó)
            if (Character.isUpperCase(lastToken.charAt(lastToken.length() - 3))) {
              // ha nem az utolsó mondat
              if (sentences.size() > i + 1) {
                sentences.get(i).addAll(sentences.get(i + 1));
                sentences.remove(i + 1);
                // ha nem az első mondat
                if (i > -1) {
                  i--;
                }
              }
            }
          }
        }
      }

      tokenNumber += sentence.size();
      if (tokenNumber + 1 < tokenOffsets.length) {
        if (tokenOffsets[tokenNumber] + 1 == tokenOffsets[tokenNumber + 1]) {
          // System.out.println(sentences.get(i) + "i: " +i + " ss: " +
          // sentences.size());
          if ((sentences.size() > i + 1 && (i > -1))) {
            sentences.get(i).addAll(sentences.get(i + 1));
            sentences.remove(i + 1);
            // ha nem az első mondat
            if (i > 0) {
              i--;
            }
          }
        }
      }

      if ((i < sentences.size() - 1) && (i > 0)) {
        firstToken = sentences.get(i + 1).get(0);

        if (ResourceHolder.getHunAbbrev().contains(firstToken.toLowerCase())) {
          if (sentences.size() > i + 1) {
            sentences.get(i).addAll(sentences.get(i + 1));
            sentences.remove(i + 1);
            if (i > 0) {
              i--;
            }
          }
        }
      }

    }

    return sentences;
  }

  private static List<List<String>> reSplit2(List<List<String>> sentences) {
    for (List<String> sentence : sentences) {
      reSplit2Sentence(sentence);
    }
    return sentences;
  }

  private static List<String> reSplit2Sentence(List<String> sentence) {
    String lastToken = null;
    char lastChar;

    // nem lehet üres mondat
    if (sentence.size() > 0) {

			/*
			 * mondtavégi írásjelek külön tokenek legyenek (.?!:;)
			 */
      // utolsó token pl.: '1999.'
      lastToken = sentence.get(sentence.size() - 1);
      // ha hosszabb mint egy karakter '9.'
      if (lastToken.length() > 1) {
        // utolsó karakter
        lastChar = lastToken.charAt(lastToken.length() - 1);
        // írásjelre végződik
        if (!Character.isLetterOrDigit(lastChar)) {
          // írásjel levágása
          lastToken = lastToken.substring(0, lastToken.length() - 1);
          // utolsó token törlése
          sentence.remove(sentence.size() - 1);
          // új utolsó előtti token hozzáadássa '1999'
          sentence.add(sentence.size(), lastToken);
          // új utolsó karaktertoken hozzáadása
          sentence.add(String.valueOf(lastChar));
        }
      }

    }

    return sentence;
  }

  public boolean isLineSentence() {
    return lineSentence;
  }

  public void setLineSentence(boolean lineSentence) {
    this.lineSentence = lineSentence;
  }

  public WordTokenizer getTokenizer() {
    return tokenizer;
  }

  public void setTokenizer(WordTokenizer tokenizer) {
    this.tokenizer = tokenizer;
  }

  public SentenceSplitter getSplitter() {
    return splitter;
  }

  public void setSplitter(SentenceSplitter splitter) {
    this.splitter = splitter;
  }

  public void setStringCleaner(StringCleaner stringCleaner) {
    this.stringCleaner = stringCleaner;
  }

  public StringCleaner getStringCleaner() {
    return stringCleaner;
  }

  public String[][] splitToArray(String text) {
    List<List<String>> splitted = null;
    String[][] sentences = null;

    splitted = this.split(text);
    sentences = new String[splitted.size()][];

    for (int i = 0; i < sentences.length; ++i) {
      sentences[i] = splitted.get(i)
              .toArray(new String[splitted.get(i).size()]);
    }
    return sentences;
  }

  /**
   * Normalizes the quote sings. Replace them to the regular " sign.
   *
   * @param text raw text
   * @return text wiht only regular " quote sings
   */
  private static String normalizeQuotes(String text) {
    for (char c : HunSplitterResources.QUOTES) {
      text = text.replaceAll(String.valueOf(c),
              String.valueOf(HunSplitterResources.DEFAULT_QUOTE));
    }
    return text;
  }

  /**
   * Normalizes the hyphen sings. Replace them to the regular - sign.
   *
   * @param text raw text
   * @return text wiht only regular - hyphen sings
   */
  private static String normalizeHyphans(String text) {
    for (char c : HunSplitterResources.HYPHENS) {
      text = text.replaceAll(String.valueOf(c),
              String.valueOf(HunSplitterResources.DEFAULT_HYPHEN));
    }
    return text;
  }

  /**
   * Add the missing space characters via the defined FORCE_TOKEN_SEPARATORS
   *
   * @param text raw text
   * @return text with added missing space cahracters
   */
  private static String addSpaces(String text) {

    StringBuffer stringBuffer = null;
    stringBuffer = new StringBuffer(String.valueOf(text));

    for (char c : HunSplitterResources.FORCE_TOKEN_SEPARATORS) {
      int index = -1;
      index = stringBuffer.indexOf(String.valueOf(c));

      while (index > 1 && index < stringBuffer.length() - 1) {
        if (stringBuffer.charAt(index - 1) != ' ') {
          stringBuffer.insert(index + 1, ' ');
        }
        index = stringBuffer.indexOf(String.valueOf(c), index + 1);
      }
    }

    return stringBuffer.toString();
  }

  public static void main(String[] args) {

    HunSplitter hunSplitter = null;
    hunSplitter = new HunSplitter(true);
    String text = null;
    text = "A 2014-es választások előtt túl jó lehetőséget adna az ellenzék kezébe a dohányboltok profitját nyirbáló kezdeményezés.";

    for (List<String> sentence : hunSplitter.split(text)) {
      for (String token : sentence) {
        System.err.println(token);
      }
      System.err.println();
    }

    int[] sentenceOffsets = null;
    int[] tokenOffsets = null;
    String sentence = null;
    String token = null;

    sentenceOffsets = hunSplitter.getSentenceOffsets(text);

    for (int i = 0; i < sentenceOffsets.length - 1; ++i) {
      sentence = text.substring(sentenceOffsets[i], sentenceOffsets[i + 1]);

      System.err.println(sentence);

      tokenOffsets = hunSplitter.getTokenOffsets(sentence);
      for (int j = 0; j < tokenOffsets.length - 1; ++j) {
        token = sentence.substring(tokenOffsets[j], tokenOffsets[j + 1]);
        System.err.println(token);
      }
    }
  }
}
