package splitter.ling.tokenizer;


import splitter.utils.CharUtils;
import splitter.utils.ListFactory;
import splitter.utils.RomanNumeralUtils;

import java.util.List;
import java.util.StringTokenizer;

/**
 * Default word tokenizer.
 */

public class DefaultWordTokenizer extends AbstractWordTokenizer implements
        WordTokenizer {
  /**
   * Create a simple word tokenizer.
   */

  public DefaultWordTokenizer() {
    super();
  }

  /**
   * Break text into word tokens.
   *
   * @param text Text to break into word tokens.
   * @return List of word tokens.
   * <p>
   * <p>
   * Word tokens may be words, numbers, punctuation, etc.
   * </p>
   */

  public List<String> extractWords(String text) {
    // List of extracted words.

    List<String> result = ListFactory.createNewList();

    // Create string tokenizer which
    // splits preTokenizer text at
    // white space boundaries.

    String pretokenizedText = preTokenizer.pretokenize(text);

    StringTokenizer tokenizer = new StringTokenizer(pretokenizedText);

    // While there are tokens left to get ...

    while (tokenizer.hasMoreTokens()) {
      // Extract next token.

      String token = tokenizer.nextToken();

      // Preprocess token.

      token = preprocessToken(token, result);

      // Check if we have to split off a
      // leading character, Some like a
      // leading percent are
      // always split off. For others,
      // when the token appears in the
      // list of contracted forms, leave
      // the character alone, otherwise split off
      // the leading character.

      boolean doneLeading = false;

      while ((token.length() > 0) && (!doneLeading)) {
        hyphensMatcher.reset(token);

        if (hyphensMatcher.matches()) {
          doneLeading = true;
        } else if (token.charAt(0) == '&') {
          if (contractions.containsString(token)) {
            doneLeading = true;
          } else {
            addWordToSentence(result, "&");
            token = token.substring(1);
          }
        } else if (!apostropheCanBeQuote
                && CharUtils.isApostrophe(token.charAt(0))) {
          doneLeading = true;
        } else if (isSingleOpeningQuote(token.charAt(0))) {
          if (contractions.containsString(token)) {
            doneLeading = true;
          } else if (aposTokens.isKnownAposToken(token)) {
            doneLeading = true;
          } else {
            addWordToSentence(result, token.charAt(0) + "");
            token = token.substring(1);
          }
        } else if (CharUtils.isOpeningQuote(token.charAt(0))) {
          addWordToSentence(result, token.charAt(0) + "");
          token = token.substring(1);
        } else if (token.charAt(0) == '%') {
          addWordToSentence(result, "%");
          token = token.substring(1);
        } else if (token.charAt(0) == '*') {
          addWordToSentence(result, "*");
          token = token.substring(1);
        }
        // else if ( CharUtils.isDash( token.charAt( 0 ) ) )
        else if (CharUtils.isBreakingDash(token.charAt(0))) {
          if (token.length() > 1) {
            if (!CharUtils.isNumber(token.substring(1))) {
              addWordToSentence(result, token.charAt(0) + "");

              token = token.substring(1);
            } else {
              doneLeading = true;
            }
          } else {
            doneLeading = true;
          }
        } else {
          doneLeading = true;
        }
      }

      int l = token.length();
      String endDelims = "";

      // If token is all dashes, leave it
      // intact.

      hyphensMatcher.reset(token);

      if (!hyphensMatcher.matches()) {
        // See if we have to split off
        // trailing delimiter characters.

        boolean doneEnding = false;

        while ((l > 1) && !doneEnding) {
          // If token ends in an apostrophe,
          // see if we should detach it or not.

          if (token.charAt(l - 1) == '\'') {
            if (aposTokens.isKnownAposToken(token)) {
              doneEnding = true;
            } else {
              endDelims = token.charAt(l - 1) + endDelims;
              token = token.substring(0, token.length() - 1);
            }
          }

          // If character is colon, dash, or single
          // quote, assume we should split
          // off that character.

          else if ((token.charAt(l - 1) == ':')
                  ||
                  // ( CharUtils.isDash( token.charAt( l - 1 ) ) ) ||
                  (CharUtils.isBreakingDash(token.charAt(l - 1)))
                  || (isClosingQuote(token.charAt(l - 1)))) {
            endDelims = token.charAt(l - 1) + endDelims;
            token = token.substring(0, token.length() - 1);
          }
          // See if we should split off a
          // trailing single quote.

          // See if we should split off a
          // trailing question or exclamation.

          else if ((token.charAt(l - 1) == '!') || (token.charAt(l - 1) == '?')) {
            if (abbreviations.isAbbreviation(token)) {
              doneEnding = true;
            } else {
              endDelims = token.charAt(l - 1) + endDelims;

              token = token.substring(0, token.length() - 1);
            }
          }
          // See if we should split off a
          // trailing period.

          else if (token.charAt(l - 1) == '.') {
            // If this is currency, split off the
            // trailing period if there is
            // a previous period in the token.

            if (token.charAt(0) == '$') {
              if (token.length() > 2) {
                if (token.substring(1, l - 2).indexOf('.') >= 0) {
                  endDelims = "." + endDelims;
                  token = token.substring(0, token.length() - 1);
                }
              }

              doneEnding = true;
            }
            // If we have an apparent abbreviation,
            // leave the period attached to the
            // token. Split it off otherwise.

            else if (isLetterOrSingleQuote(token.charAt(l - 2))) {
              if (abbreviations.isAbbreviation(token)) {
                doneEnding = true;
              } else {
                endDelims = "." + endDelims;
                token = token.substring(0, token.length() - 1);
              }
            }
            // All periods? Leave it alone.

            else if (CharUtils.isAllPeriods(token)) {
              doneEnding = true;
            }
            // Leave trailing period attached
            // to number.

            else if (CharUtils.isNumber(token)) {
              doneEnding = true;
            }
            // Leave trailing period attached
            // to Roman numeral.

            else if (RomanNumeralUtils.isLooseRomanNumeral(token)) {
              doneEnding = true;
            } else {
              endDelims = "." + endDelims;
              token = token.substring(0, token.length() - 1);
            }
          } else {
            doneEnding = true;
            ;
          }

          l = token.length();
        }
      }
      // If we have just "--" or "---" and the
      // previous token was just a single letter,
      // append the dashes to the previous token.
      /*
       * if ( isMultipleHyphens( token ) ) { if ( result.size() > 0 ) { String
			 * previousToken = (String)result.get( result.size() - 1 );
			 * 
			 * if ( CharUtils.isLetter( previousToken ) ) { previousToken =
			 * previousToken + token; result.set( result.size() - 1 , previousToken );
			 * token = ""; } } }
			 */
      // If we have a 's or 'S, and the previous
      // token was just dashes or asterisks,
      // append the 's to the previous token.

      if (token.equals("'s") || token.equals("'S")) {
        if (result.size() > 0) {
          String previousToken = (String) result.get(result.size() - 1);

          if (previousToken
                  .matches("([A-Za-z]){0,1}(--|---|\u2011\u2011|\u2011\u2011\u2011|(\\*+))")) {
            previousToken = previousToken + token;
            result.set(result.size() - 1, previousToken);
            token = "";
          }
        }
      }
      // If the token is not empty,
      // add it to the sentence.

      if (token.length() > 0) {
        // Check if we need to split a token
        // containing an internal period.

        String[] tokens = splitToken(token);

        for (int k = 0; k < tokens.length; k++) {
          if (tokens[k].length() > 0) {
            addWordToSentence(result, tokens[k]);
          }
        }
      }
      // Add the trailing delimiters.

      for (int k = 0; k < endDelims.length(); k++) {
        String delimToken = endDelims.charAt(k) + "";

        addWordToSentence(result, delimToken);
      }
    }
    // Return list of words to caller.
    return result;
  }

  /**
   * Add word to list of words in sentence.
   *
   * @param sentence Result sentence.
   * @param word     Word to add.
   */

  public void addWordToSentence(List<String> sentence, String word) {
    boolean coalesce = (sentence.size() > 0)
            && ((coalesceHyphens && word.equals("-")) || (coalesceAsterisks && word
            .equals("*")));

    if (coalesce) {
      String previousWord = sentence.get(sentence.size() - 1);

      if (previousWord.endsWith(word)) {
        previousWord = previousWord + word;
        sentence.set(sentence.size() - 1, previousWord);
      } else {
        sentence.add(word);
      }
    } else {
      // $$$PIB$$$ Kluge to handle Roman numerals enclosed in periods.
      //
      // sentence.add( word );
      //
      if (word.equals(".") && (sentence.size() > 0)) {
        String prevWord = sentence.get(sentence.size() - 1);

        if ((prevWord.charAt(0) == '.')
                && (RomanNumeralUtils.isLooseRomanNumeral(prevWord))) {
          prevWord = prevWord + ".";
          sentence.set(sentence.size() - 1, prevWord);
        } else {
          sentence.add(word);
        }
      } else {
        sentence.add(word);
      }
    }
  }
}
