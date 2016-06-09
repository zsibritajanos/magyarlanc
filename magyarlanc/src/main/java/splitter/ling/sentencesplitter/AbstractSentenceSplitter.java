package splitter.ling.sentencesplitter;

import splitter.ling.abbreviations.Abbreviations;
import splitter.ling.namerecognizer.Names;
import splitter.ling.partsofspeech.PartOfSpeechTags;
import splitter.ling.postagger.guesser.PartOfSpeechGuesser;
import splitter.ling.tokenizer.DefaultWordTokenizer;
import splitter.ling.tokenizer.WordTokenizer;
import splitter.utils.*;
import splitter.utils.logger.DummyLogger;
import splitter.utils.logger.Logger;
import splitter.utils.logger.UsesLogger;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Abstract sentence MySplitter.
 * <p>
 * <p>
 * The base class for sentence splitters.
 * </p>
 */

abstract public class AbstractSentenceSplitter extends IsCloseableObject
        implements SentenceSplitter, IsCloseable, UsesLogger {
  /**
   * Default word tokenizer used if none specified.
   */

  protected WordTokenizer wordTokenizer = new DefaultWordTokenizer();

  /**
   * Part of speech guesser used by some sentence splitters.
   */

  protected PartOfSpeechGuesser partOfSpeechGuesser;

  /**
   * Sentence iterator.
   */

  protected SentenceSplitterIterator sentenceSplitterIterator;

  /**
   * Name recognizer.
   */

  protected Names names = new Names();

  /**
   * Abbreviations.
   */

  protected Abbreviations abbreviations = new Abbreviations();

  /**
   * Logger used for output.
   */

  protected Logger logger = new DummyLogger();

  /**
   * Characters not allowed to start a sentence.
   */

  protected static final String disallowedSentenceStarters = ",%.";

  /**
   * Set the part of speech guesser.
   *
   * @param partOfSpeechGuesser The part of speech guesser.
   */

  public void setPartOfSpeechGuesser(PartOfSpeechGuesser partOfSpeechGuesser) {
    this.partOfSpeechGuesser = partOfSpeechGuesser;
  }

  /**
   * Set abbreviations.
   *
   * @param abbreviations Abbreviations.
   */

  public void setAbbreviations(Abbreviations abbreviations) {
    this.abbreviations = abbreviations;
  }

  /**
   * Set sentence MySplitter iterator.
   *
   * @param sentenceSplitterIterator Sentence MySplitter iterator.
   */

  public void setSentenceSplitterIterator(
          SentenceSplitterIterator sentenceSplitterIterator) {
    this.sentenceSplitterIterator = sentenceSplitterIterator;
  }

  /**
   * Fix up a sentence.
   *
   * @param sentenceWords         Sentence to fix up.
   * @param previousSentenceWords Previous sentence.
   * @return true if end of sentence found.
   */

  protected boolean fixUpSentence(List<String> sentenceWords,
                                  List<String> previousSentenceWords) {
    // See if the sentence boundary
    // identified by BreakIterator needs
    // correction. Start by assuming
    // the BreakIterator end-of-sentence
    // detection is correct.

    boolean eosSeen = true;
    boolean done = (sentenceWords.size() == 0);

    // Loop until we've finished
    // adjusting the sentences.
    while (!done) {
      done = true;
      // If we have a previous sentence ...

      if ((previousSentenceWords != null) && (previousSentenceWords.size() > 0)) {
        // If the current sentence consists
        // of closing punctuation only,
        // append it to the previous sentence.

        if (isClosingPunctuationOnly(sentenceWords)) {
          previousSentenceWords.addAll(sentenceWords);
          sentenceWords.clear();

          return eosSeen;
        }
        // Get the last token in the previous
        // sentence...

        String lastToken = previousSentenceWords.get(previousSentenceWords
                .size() - 1);

        // If the last token was a right
        // bracket, right parenthesis, right brace,
        // or long dash, join the current sentence
        // to the previous sentence if the
        // first word in the current sentence
        // does not begin with a capital letter.
        // This heuristic does not always work,
        // but it works much more often than not.

        if (lastToken.equals(")") || lastToken.equals("]")
                || lastToken.equals("}")
                || lastToken.equals(CharUtils.LONG_DASH_STRING)) {
          String nextToken = sentenceWords.get(0);

          eosSeen = CharUtils.isCapitalLetter(nextToken.charAt(0));
        }
        // Do not allow a sentence to
        // begin with a long dash, unless the
        // preceding character was a sentence
        // ending character.
        //
        // Add the long dash to the previous
        // sentence.

        else if (sentenceWords.get(0).equals(CharUtils.LONG_DASH_STRING)) {
          String prevSentenceLastWord = "";

          int lastWordIndex = previousSentenceWords.size() - 1;

          if (lastWordIndex >= 0) {
            prevSentenceLastWord = previousSentenceWords.get(lastWordIndex);
          }

          if (prevSentenceLastWord.endsWith(".")
                  || prevSentenceLastWord.endsWith("!")
                  || prevSentenceLastWord.endsWith("?")) {
          } else {
            previousSentenceWords.add(CharUtils.LONG_DASH_STRING);

            sentenceWords.remove(0);

            // Need to recheck the start of the
            // sentence in this case.

            done = (sentenceWords.size() == 0);
          }
        }
        // Do not allow a sentence to
        // begin with a comma. Add the
        // comma to the previous sentence.

        else if (sentenceWords.get(0).equals(",")) {
          previousSentenceWords.add(",");

          sentenceWords.remove(0);

          // Need to recheck the start of the
          // sentence in this case, unless
          // the sentence is now empty.

          done = (sentenceWords.size() == 0);
        }
        // If sentence starts with a number
        // and the previous sentence ended with
        // a number, append this number
        // to the previous sentence.

        else if (CharUtils.isNumber(sentenceWords.get(0))
                && CharUtils.isNumber(lastToken)) {
          previousSentenceWords.add(sentenceWords.get(0));

          sentenceWords.remove(0);

          // Need to recheck the start of the
          // sentence in this case, unless
          // the sentence is now empty.

          done = (sentenceWords.size() == 0);
        }
        // Do not allow s to start a new
        // sentence if previous token ended
        // with a single quote.

        else if (sentenceWords.get(0).equals("s")) {
          // Did previous token end with a
          // single quote?

          if (CharUtils.endsWithSingleQuote(lastToken)) {
            // Append the "s" to the previous
            // token.

            lastToken = lastToken + sentenceWords.get(0);

            // Remove the spurious "s".

            sentenceWords.remove(0);

            // If the new last token is now
            // single quote + s, it is probably
            // a possessive/plural/contraction
            // marker.

            if (CharUtils.isSingleQuoteS(lastToken)) {
              // Get the word prior to the
              // previous word.

              String lastTokenM1 = previousSentenceWords
                      .get(previousSentenceWords.size() - 2);

              // If the word ends with a period
              // or anything but another punctuation
              // mark, append the single quote + s
              // to it.

              if (lastTokenM1.endsWith(".")
                      || !CharUtils.isPunctuation(lastTokenM1)) {
                lastToken = lastTokenM1 + lastToken;

                previousSentenceWords.remove(previousSentenceWords.size() - 1);
              }
            }
            // Replace last token (and possibly
            // token prior to last token) with
            // the new token ending in
            // single quote + s.

            previousSentenceWords.remove(previousSentenceWords.size() - 1);

            previousSentenceWords.add(lastToken);

            // We have not seen the end of the
            // sentence yet.

            eosSeen = false;
          }
        }
        // Initials usually do not end a sentence.
        //
        // $$$PIB$$$ These heuristics need
        // improvement.

        else if (abbreviations.isInitial(lastToken)) {
          // Assume initial does not end the
          // sentence.

          eosSeen = false;

          // Look at the first word in the
          // current sentence. If it starts
          // with a capital letter, and is not a
          // possible noun, the initial is probably
          // the end of the sentence.

          String nextToken = sentenceWords.get(0);

          // First word of current sentence
          // starts with capital letter?

          if (CharUtils.isCapitalLetter(nextToken.charAt(0))) {
            // Is first word probably something
            // other than a noun? If so, the
            // initial is probably the end of
            // the sentence.

            if (!isNoun(nextToken)) {
              eosSeen = true;
            }
          }
        }
        // If the last token in the previous
        // sentence appears to be an abbreviation,
        // check if the current sentence should be
        // appended to the previous sentence.

        else if (abbreviations.isAbbreviation(lastToken)) {
          if (abbreviations.isEOSAbbreviation(lastToken)) {
            // We have a possible sentence-ending
            // abbreviation. Check if we have
            // seen a potential verb so far in
            // this sentence. If not, this cannot
            // be the end of a sentence.

            if (verbSeen(previousSentenceWords)) {
              // Look at the first word in the
              // current sentence. If it starts
              // with a capital letter, and
              // could be a noun or pronoun scan the
              // current sentence for a potential verb.
              // If none is found, the abbreviation
              // is unlikely to be the end of the
              // sentence, so join the current
              // sentence to the previous one.

              String nextToken = sentenceWords.get(0);

              // First word of current sentence
              // starts with capital letter?

              if (CharUtils.isCapitalLetter(nextToken.charAt(0))) {
                // Could first word be a proper noun
                // or pronoun?

                if (isProperNoun(nextToken) || isPronoun(nextToken)) {

                  // Scan for possible verb in current
                  // sentence.

                  boolean nextVerbSeen = false;
                  String prevToken = "";

                  for (int j = 1; ((j < sentenceWords.size()) && !nextVerbSeen); j++) {
                    nextToken = sentenceWords.get(j);

                    nextVerbSeen = (isVerb(nextToken) && !prevToken
                            .equals("to"));

                    prevToken = nextToken.toLowerCase();

                    if (nextVerbSeen)
                      break;
                  }

                  // If verb found, the abbreviation
                  // is probably the end of the sentence.

                  eosSeen = nextVerbSeen;
                }

                // First word of current sentence
                // probably not a proper noun.
                // The abbreviation probably is the
                // end of the sentence.

                else {
                  eosSeen = true;
                }
              }
              // First word in new sentence is
              // capitalized but not a potential
              // noun. The abbreviation is probably
              // the end of the sentence.
              else {
                eosSeen = true;
              }
            }
            // No probable verb in previous
            // sentence. The abbreviation
            // is probably not the end of the
            // sentence.
            else {
              eosSeen = false;
            }
          }
          // Not a likely sentence-ending
          // abbreviation.
          else {
            eosSeen = false;
          }
        }
        // Check for characters which are not
        // allowed to start a sentence.

        else if (sentenceWords.get(0).length() == 1) {
          char ch = sentenceWords.get(0).charAt(0);

          int j = disallowedSentenceStarters.indexOf(ch);

          if (disallowedSentenceStarters.indexOf(ch) >= 0) {
            eosSeen = false;
          }
        }
        // Do not allow a lower case word
        // to start a sentence following
        // a ? or !.
        else if (Character.isLowerCase(sentenceWords.get(0).charAt(0))) {
          eosSeen = false;
        }
      }
    }

    return eosSeen;
  }

  /**
   * Check if sentence contains only closing punctuation.
   *
   * @param sentenceWords Words in sentence.
   * @return true if all words are closing punctuation, false otherwise.
   */

  public boolean isClosingPunctuationOnly(List<String> sentenceWords) {
    boolean result = false;

    if ((sentenceWords != null) && (sentenceWords.size() > 0)) {
      for (int i = 0; i < sentenceWords.size(); i++) {
        String token = sentenceWords.get(i);
        result = true;

        if (token.equals(".") || token.equals(")") || token.equals("]")
                || token.equals("}")) {
        } else {
          result = false;
          break;
        }
      }
    }

    return result;
  }

  /**
   * Break text into sentences and tokens.
   *
   * @param text      Text to break into sentences and tokens.
   * @param tokenizer Word tokenizer to use for breaking sentences into words.
   * @return List of sentences. Each sentence is itself a list of word tokens.
   * <p>
   * <p>
   * Word tokens may be words, numbers, punctuation, etc.
   * </p>
   */

  public List<List<String>> extractSentences(String text,
                                             WordTokenizer tokenizer) {
    // Create a list of tokenized
    // sentences.

    List<List<String>> result = ListFactory.createNewList();

    // Find initial sentences.

    sentenceSplitterIterator.setText(text);

    // Extract each sentence in order
    // of appearance.

    List<String> previousSentenceWords = null;

    while (sentenceSplitterIterator.hasNext()) {
      // Get text of next sentence.

      String sentenceText = sentenceSplitterIterator.next();

      // Get text of sentence past next sentence,
      // if any.

      String nextSentenceText = sentenceSplitterIterator.peek();

      // If following sentence consists solely
      // of a period, join its text to the
      // current sentence text.

      if ((nextSentenceText != null) && nextSentenceText.equals(".")) {
        sentenceText = sentenceText + nextSentenceText;
        sentenceSplitterIterator.next();
      }
      // Split sentence into words.

      List<String> sentenceWords = tokenizer.extractWords(sentenceText);

      // Ignore empty sentence.

      if (sentenceWords.size() == 0) {
        continue;
      }
      // Split sentence into apparent
      // subsentences at surround text markers,
      // if any.

      List<List<String>> subSentences = splitSentenceWordList(sentenceWords);

      // Process each subsentence.

      for (int i = 0; i < subSentences.size(); i++) {
        // Get next subsentence.

        sentenceWords = subSentences.get(i);

        // See if an end of sentence was seen in
        // this subsentence.

        boolean eosSeen = fixUpSentence(sentenceWords, previousSentenceWords);
        // If we've seen the end of the
        // sentence, add the sentence to
        // the list of sentences unless it's empty.
        // If we have not seen the end of the
        // sentence, append the current
        // sentence to the previous sentence.

        if (sentenceWords.size() > 0) {
          if (eosSeen && !quoteOnlySentence(sentenceWords)) {
            addSentence(sentenceWords, result);
            previousSentenceWords = sentenceWords;
          } else {
            if (previousSentenceWords != null) {
              previousSentenceWords.addAll(sentenceWords);
            } else {
              addSentence(sentenceWords, result);
              previousSentenceWords = sentenceWords;
            }
          }
        }
      }
    }
    // Return list of sentences to caller.
    return result;
  }

  /**
   * Check if sentence contains only a double quote.
   *
   * @param sentenceWords List of sentence words.
   * @return true if sentence starts with quote and contains only XML section
   * marker characters.
   */

  public boolean quoteOnlySentence(List<String> sentenceWords) {
    boolean result = false;

    if (sentenceWords.size() == 0)
      return result;

    String word = sentenceWords.get(0);

    result = word.equals(CharUtils.CHAR_END_OF_TEXT_SECTION_STRING)
            || ((word.length() == 1) && (CharUtils.isClosingQuote(word.charAt(0))));

    for (int i = 1; i < sentenceWords.size(); i++) {
      word = sentenceWords.get(i);

      result = word.equals(CharUtils.CHAR_END_OF_TEXT_SECTION_STRING)
              || ((word.length() == 1) && (CharUtils.isClosingQuote(word.charAt(0))));

      if (!result)
        break;
    }

    return result;
  }

  /**
   * Break text into sentences and tokens.
   *
   * @param text Text to break into sentences and tokens.
   * @return List of sentences. Each sentence is itself a list of word tokens.
   * <p>
   * <p>
   * Word tokens may be words, numbers, punctuation, etc. The default
   * word tokenizer is used.
   * </p>
   */

  public List<List<String>> extractSentences(String text) {
    return extractSentences(text, wordTokenizer);
  }

  /**
   * Find starting offsets of sentences extracted from a text.
   *
   * @param text      Text from which sentences were extracted.
   * @param sentences List of sentences (each a list of words) extracted from text.
   *                  <p>
   *                  N.B. If the sentences aren't from the specified text, the
   *                  resulting offsets will be meaningless.
   * @return int array of starting offsets in text for each sentence. The first
   * offset starts at 0. There is one more offset than the number of
   * sentences -- the last offset is where the sentence after the last
   * sentence would start.
   */

  public int[] findSentenceOffsets(String text,
                                   List<List<String>> sentences) {
    // Allocate int vector to hold
    // sentence offsets.

    int sentenceCount = sentences.size();

    int[] result = new int[sentenceCount + 1];

    // Offset of current sentence.

    int offset = 0;

    // Loop over sentences.

    for (int i = 0; i < sentenceCount; i++) {
      // Get next sentence.

      List sentence = sentences.get(i);

      // Store its starting offset.

      result[i] = offset;

      // Find number of non-blank
      // characters in tokens in
      // sentence.

      int nbCount = 0;

      for (int j = 0; j < sentence.size(); j++) {
        nbCount += sentence.get(j).toString().length();
      }
      // Move forward in text that many
      // non-blank characters, skipping
      // over any whitespace. That will
      // give us the offset of the start of
      // the next sentence, if any.

      int tNbCount = 0;

      while (tNbCount < nbCount) {
        if (!CharUtils.isWhitespace(text.charAt(offset))) {
          tNbCount++;
        }

        offset++;
      }
    }
    // Store position of last sentence + 1.

    result[sentenceCount] = text.length();

    // Return sentence offsets to caller.
    return result;
  }

  /**
   * Add sentence to sentence list.
   *
   * @param sentence     List of words in sentence.
   * @param sentenceList List of sentences.
   *                     <p>
   *                     <p>
   *                     The sentence is added to the sentence list after performing any
   *                     further sentence splitting.
   *                     </p>
   */

  protected void addSentenceBad(List<String> sentence,
                                List<List<String>> sentenceList) {
    // $$$PIB$$$ The following code
    // needs review before being
    // reenabled. It sometimes results
    // in the XGTagger code getting
    // lost and generating bad output.
    // However, without it, the BreakIterator
    // based sentence MySplitter sometimes
    // screws up when encountering a
    // right single curly quote.

    boolean quoteTokenFound = false;

    for (int i = 0; i < sentence.size(); i++) {
      String token = (String) sentence.get(i);

      if ((token.length() > 1) && (token.charAt(0) == CharUtils.RSQUOTE)) {
        quoteTokenFound = true;
        break;
      }
    }

    if (quoteTokenFound) {
      StringBuffer sb = new StringBuffer();
      String token;

      for (int i = 0; i < sentence.size(); i++) {
        token = (String) sentence.get(i);

        if ((token.length() > 1) && (token.charAt(0) == CharUtils.RSQUOTE)) {
          token = CharUtils.CHAR_SUBSTITUTE_SINGLE_QUOTE + token.substring(1);
        }

        if (i > 0) {
          sb.append(" ");
        }

        sb.append(token);
      }
      // Set text to break into sentences.

      String newText = sb.toString();

      // Extract sentences.

      SentenceSplitterIterator subSentenceIterator = new ICU4JBreakIteratorSentenceSplitterIterator(
              newText);

      // Extract each sentence in order
      // of appearance.

      while (subSentenceIterator.hasNext()) {
        // Get text of next sentence.

        StringTokenizer stringTokenizer = new StringTokenizer(
                subSentenceIterator.next());

        List<String> newSentence = ListFactory.createNewList();

        while (stringTokenizer.hasMoreTokens()) {
          token = stringTokenizer.nextToken();

          if ((token.length() > 1)
                  && (token.charAt(0) == CharUtils.CHAR_SUBSTITUTE_SINGLE_QUOTE)) {
            token = CharUtils.RSQUOTE + token.substring(1);
          }

          newSentence.add(token);
        }

        sentenceList.add(newSentence);
      }
    } else {
      sentenceList.add(sentence);
    }
  }

  /**
   * Add sentence to sentence list.
   *
   * @param sentence     List of words in sentence.
   * @param sentenceList List of sentences.
   */

  protected void addSentence(List<String> sentence,
                             List<List<String>> sentenceList) {
    if ((sentence != null) && (sentence.size() > 0)) {
      sentenceList.add(sentence);
    }
  }

  /**
   * Check if word is a possible verb.
   *
   * @param word The word to check.
   *             <p>
   *             <p>
   *             The check uses the part of speech guesser to get the parts of
   *             speech for word. The list of parts of speech is checked for an
   *             entry with a major word class of verb.
   *             </p>
   */

  protected boolean isVerb(String word) {
    boolean result = false;

    if (partOfSpeechGuesser != null) {
      Map<String, MutableInteger> guessedTags = partOfSpeechGuesser
              .guessPartsOfSpeech(word);

      PartOfSpeechTags partOfSpeechTags = partOfSpeechGuesser.getWordLexicon()
              .getPartOfSpeechTags();

      Iterator<String> iterator = guessedTags.keySet().iterator();

      while (iterator.hasNext() && !result) {
        String tag = iterator.next();

        result = result || partOfSpeechTags.isVerbTag(tag);
      }
    }

    return result;
  }

  /**
   * Check if word is a possible proper noun.
   *
   * @param word The word to check.
   *             <p>
   *             <p>
   *             The check first looks up the word in the MorphAdorner lists of
   *             proper names and places. If the word is not found there, the part
   *             of speech guesser is used to get the parts of speech for the word.
   *             If the word can be a proper noun, or at least a noun and it begins
   *             with a capital letter, the word is assumed to be a possible proper
   *             noun.
   *             </p>
   */

  protected boolean isProperNoun(String word) {
    // Is the word a known name or place?
    // It is a proper noun if so.

    boolean result = names.isNameOrPlace(word);

    // Not a known name or place.
    // Ask the part of speech guesser
    // if the word can be a proper noun.
    // If the word can be a noun and begins
    // with a capital letter, we assume
    // it is a possible proper noun as well.

    if (!result && (partOfSpeechGuesser != null)) {
      boolean startsWithCapital = CharUtils.isCapitalLetter(word.charAt(0));

      Map<String, MutableInteger> guessedTags = partOfSpeechGuesser
              .guessPartsOfSpeech(word);

      PartOfSpeechTags partOfSpeechTags = partOfSpeechGuesser.getWordLexicon()
              .getPartOfSpeechTags();

      Iterator<String> iterator = guessedTags.keySet().iterator();

      while (iterator.hasNext() && !result) {
        String tag = iterator.next();

        result = partOfSpeechTags.isProperNounTag(tag)
                || (partOfSpeechTags.isNounTag(tag) && startsWithCapital);
      }
    }

    return result;
  }

  /**
   * Check if word is a possible pronoun.
   *
   * @param word The word to check.
   */

  protected boolean isPronoun(String word) {
    boolean result = false;

    if (partOfSpeechGuesser != null) {
      Map<String, MutableInteger> guessedTags = partOfSpeechGuesser
              .guessPartsOfSpeech(word);

      PartOfSpeechTags partOfSpeechTags = partOfSpeechGuesser.getWordLexicon()
              .getPartOfSpeechTags();

      Iterator<String> iterator = guessedTags.keySet().iterator();

      while (iterator.hasNext() && !result) {
        String tag = iterator.next();

        result = partOfSpeechTags.isPronounTag(tag);
      }
    }

    return result;
  }

  /**
   * Check if word is a possible noun.
   *
   * @param word The word to check.
   */

  protected boolean isNoun(String word) {
    boolean result = false;

    if (partOfSpeechGuesser != null) {
      Map<String, MutableInteger> guessedTags = partOfSpeechGuesser
              .guessPartsOfSpeech(word);

      PartOfSpeechTags partOfSpeechTags = partOfSpeechGuesser.getWordLexicon()
              .getPartOfSpeechTags();

      Iterator<String> iterator = guessedTags.keySet().iterator();

      while (iterator.hasNext() && !result) {
        String tag = iterator.next();

        result = partOfSpeechTags.isNounTag(tag);
      }
    }

    return result;
  }

  /**
   * Break sentence word list into subsentences on special marker.
   *
   * @param sentenceWords Sentence words as a list.
   * @return List of lists of sentence words.
   * <p>
   * <p>
   * Breaks the word list for a sentence into subsentences based upon
   * the occurrence of the XML surround text marker character. This
   * special character always indicates where a list of words should be
   * split. Also split at a bare period assuming it ends a sentence. We
   * may rejoin some of the split subsentences later into longer
   * sentences.
   * </p>
   */

  protected List<List<String>> splitSentenceWordList(List<String> sentenceWords) {
    List<List<String>> result = ListFactory.createNewList();

    List<String> subSentence = ListFactory.createNewList();

    for (int i = 0; i < sentenceWords.size(); i++) {
      String word = sentenceWords.get(i);

      subSentence.add(word);

      if (word.equals(CharUtils.CHAR_END_OF_TEXT_SECTION_STRING)) {
        result.add(subSentence);
        subSentence = ListFactory.createNewList();
      } else if (word.equals(".")) {
        result.add(subSentence);
        subSentence = ListFactory.createNewList();
      }
    }

    if (subSentence.size() > 0) {
      result.add(subSentence);
    }

    return result;
  }

  /**
   * See if potential verb found in token list.
   *
   * @param tokenList The token list.
   * @return True if at least one of the tokens in the token list is a potential
   * verb.
   */

  protected boolean verbSeen(List<String> tokenList) {
    boolean result = false;

    if (tokenList != null) {
      for (int i = 0; i < (tokenList.size() - 1); i++) {
        result = result || isVerb(tokenList.get(i));

        if (result)
          break;
      }
    }

    return result;
  }

  /**
   * Get the logger.
   *
   * @return The logger.
   */

  public Logger getLogger() {
    return logger;
  }

  /**
   * Set the logger.
   *
   * @param logger The logger.
   */

  public void setLogger(Logger logger) {
    this.logger = logger;
  }
}
