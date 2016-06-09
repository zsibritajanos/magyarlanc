/**
 * Developed by:
 * Research Group on Artificial Intelligence of the Hungarian Academy of Sciences
 * http://www.inf.u-szeged.hu/rgai/
 * <p>
 * Contact:
 * Janos Zsibrita
 * zsibrita@inf.u-szeged.hu
 * <p>
 * Licensed by Creative Commons Attribution Share Alike
 * <p>
 * http://creativecommons.org/licenses/by-sa/3.0/legalcode
 */

package hu.u_szeged.pos.guesser;

import hu.u_szeged.magyarlanc.MorAna;
import hu.u_szeged.magyarlanc.resource.ResourceHolder;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Minden szammal kezdodo token elemzesét a NumberGuesser osztaly végzi,
 * regularis kifejezések segitsegevel. Egy szolakhoz tobb elemzes is tartozhat.
 * Egy szammal kezdodo token lehet fonev (N) (pl.: 386-os@Nn-sn), melleknev
 * (pl.: 16-ai@Afp-sn), szamnev (pl. 5.@Mo-snd) vagy nyilt tokenosztalyba
 * tartozo (pl.: 20%@Onp-sn).
 */
public class NumberGuesser {

  // main number pattern
  private final static Pattern PATTERN_0 = Pattern.compile("[0-9]+.*");

  // 1-es 1.3-as 1,5-ös 1/6-os 16-17-es [Afp-sn, Nn-sn]
  private static Pattern PATTERN_1 = Pattern
          .compile("([0-9]+[0-9\\.,%-/]*-(as|ás|es|os|ös)+)([a-zA-ZáéíóöőúüűÁÉÍÓÖŐÚÜŰ]*)");

  // 16-i
  private static Pattern PATTERN_2 = Pattern.compile("[0-9]+[0-9\\.,-/]*-i");

  // 16-(ai/ei/jei)
  private static Pattern PATTERN_3 = Pattern
          .compile("([0-9]+-(ai|ei|jei)+)([a-zA-ZáéíóöőúüűÁÉÍÓÖŐÚÜŰ]*)");

  // +12345
  private static Pattern PATTERN_4 = Pattern
          .compile("([\\+|\\-]{1}[0-9]+[0-9\\.,-/]*)-??([a-zA-ZáéíóöőúüűÁÉÍÓÖŐÚÜŰ]*)");

  // 12345-12345
  private static Pattern PATTERN_5 = Pattern
          .compile("([0-9]+-[0-9]+)-??([a-zA-ZáéíóöőúüűÁÉÍÓÖŐÚÜŰ]*)");

  // 12:30 12.30 Ont-sn
  private static Pattern PATTERN_6 = Pattern
          .compile("(([0-9]{1,2})[\\.:]([0-9]{2}))-??([a-zA-ZáéíóöőúüűÁÉÍÓÖŐÚÜŰ]*)");

  // 123,45-12345
  private static Pattern PATTERN_7 = Pattern
          .compile("([0-9]+,[0-9]+-[0-9]+)-??([a-zA-ZáéíóöőúüűÁÉÍÓÖŐÚÜŰ]*)");

  // 12345-12345,12345
  private static final Pattern PATTERN_8 = Pattern
          .compile("([0-9]+-[0-9]+,[0-9]+)-??([a-zA-ZáéíóöőúüűÁÉÍÓÖŐÚÜŰ]*)");

  // 12345,12345-12345,12345
  private static final Pattern PATTERN_9 = Pattern
          .compile("([0-9]+,[0-9]+-[0-9]+,[0-9]+)-??([a-zA-ZáéíóöőúüűÁÉÍÓÖŐÚÜŰ]*)");

  // 12345.12345,12345
  private static final Pattern PATTERN_10 = Pattern
          .compile("([0-9]+\\.[0-9]+,[0-9]+)-??([a-zA-ZáéíóöőúüűÁÉÍÓÖŐÚÜŰ]*)");

  // 10:30
  private static final Pattern PATTERN_11 = Pattern
          .compile("([0-9]+:[0-9]+)-??([a-zA-ZáéíóöőúüűÁÉÍÓÖŐÚÜŰ]*)");

  // 12345.12345.1234-.
  private static final Pattern PATTERN_12 = Pattern
          .compile("([0-9]+\\.[0-9]+[0-9\\.]*)-??([a-zA-ZáéíóöőúüűÁÉÍÓÖŐÚÜŰ]*)");

  // 12,3-nak
  private static final Pattern PATTERN_13 = Pattern
          .compile("([0-9]+,[0-9]+)-??([a-zA-ZáéíóöőúüűÁÉÍÓÖŐÚÜŰ]*)");

  // 20-nak
  private static final Pattern PATTERN_14 = Pattern
          .compile("([0-9]+)-??([a-zA-ZáéíóöőúüűÁÉÍÓÖŐÚÜŰ]*)");

  // 20.
  private static final Pattern PATTERN_15 = Pattern
          .compile("(([0-9]+-??[0-9]*)\\.)-??([a-zA-ZáéíóöőúüűÁÉÍÓÖŐÚÜŰ]*)");

  // 16-áig
  private static final Pattern PATTERN_16 = Pattern
          .compile("(([0-9]{1,2})-(á|é|jé))([a-zA-ZáéíóöőúüűÁÉÍÓÖŐÚÜŰ]*)");

  // 16-a
  private static final Pattern PATTERN_17 = Pattern
          .compile("(([0-9]{1,2})-(a|e|je))()");

  // 50%
  private static final Pattern PATTERN_18 = Pattern
          .compile("([0-9]+,??[0-9]*%)-??([a-zA-ZáéíóöőúüűÁÉÍÓÖŐÚÜŰ]*)");

  private static String nounToNumeral(String nounMsd, String numeralMsd) {
    StringBuffer msd = null;
    msd = new StringBuffer(numeralMsd);

    // szam
    if (nounMsd.length() > 3)
      msd.setCharAt(3, nounMsd.charAt(3));

    // eset
    if (nounMsd.length() > 4)
      msd.setCharAt(4, nounMsd.charAt(4));

    // birtokos szama
    if (nounMsd.length() > 8)
      msd.setCharAt(10, nounMsd.charAt(8));

    // birtokos szemelye
    if (nounMsd.length() > 9)
      msd.setCharAt(11, nounMsd.charAt(9));

    // birtok(olt) szama
    if (nounMsd.length() > 10)
      msd.setCharAt(12, nounMsd.charAt(10));

    return ResourceHolder.getKRToMSD().cleanMsd(msd.toString());
  }

  private static String nounToOther(String nounMsd, String otherMsd) {
    StringBuffer msd = null;
    msd = new StringBuffer(otherMsd);

    // szam
    if (nounMsd.length() > 3)
      msd.setCharAt(4, nounMsd.charAt(3));

    // eset
    if (nounMsd.length() > 4)
      msd.setCharAt(5, nounMsd.charAt(4));

    // birtokos szama
    if (nounMsd.length() > 8)
      msd.setCharAt(9, nounMsd.charAt(8));

    // birtokos szemelye
    if (nounMsd.length() > 9)
      msd.setCharAt(10, nounMsd.charAt(9));

    // birtok(olt) szama
    if (nounMsd.length() > 10)
      msd.setCharAt(11, nounMsd.charAt(10));

    return ResourceHolder.getKRToMSD().cleanMsd(msd.toString());
  }

  private static String nounToNoun(String nounMsd, String otherMsd) {
    StringBuffer msd = null;
    msd = new StringBuffer(otherMsd);

    // szam
    if (nounMsd.length() > 3)
      msd.setCharAt(3, nounMsd.charAt(3));

    // eset
    if (nounMsd.length() > 4)
      msd.setCharAt(4, nounMsd.charAt(4));

    return ResourceHolder.getKRToMSD().cleanMsd(msd.toString());
  }

  private static int romanToArabic(String romanNumber) {
    char romanChars[] = {'I', 'V', 'X', 'L', 'C', 'D', 'M'};
    int arabicNumbers[] = {1, 5, 10, 50, 100, 500, 1000};
    int temp[] = new int[20];
    int sum = 0;

    for (int i = 0; i < romanNumber.toCharArray().length; i++) {
      for (int j = 0; j < romanChars.length; j++) {
        if (romanNumber.charAt(i) == romanChars[j]) {
          temp[i] = arabicNumbers[j];
        }
      }
    }

    for (int i = 0; i < temp.length; i++) {
      if (i == temp.length - 1) {
        sum += temp[i];
      } else {
        if (temp[i] < temp[i + 1]) {
          sum += (temp[i + 1] - temp[i]);
          i++;
        } else {
          sum += temp[i];
        }
      }
    }
    return sum;
  }

  /**
   * sz�mmal kezd�d� token elemz�se
   *
   * @param number egy (sz�mmal kezd�d�) String
   * @return lehets�ges elemz�seket (lemma-msd p�rok)
   */
  public static Set<MorAna> guess(String number) {
    Matcher matcher = null;

    Set<MorAna> stemSet = null;
    stemSet = new TreeSet<MorAna>();

    String root = null;
    String suffix = null;

    // base number pattern
    matcher = PATTERN_0.matcher(number);
    if (!matcher.matches()) {
      return stemSet;
    }

    matcher = PATTERN_1.matcher(number);

    if (matcher.matches()) {
      root = matcher.group(1);
      // group 3!!!
      // 386-osok (386-(os))(ok)
      suffix = matcher.group(3);
      if (suffix.length() > 0)
        for (MorAna stem : MorPhonGuesser.guess(root, suffix)) {
          stemSet.add(new MorAna(root, stem.getMsd()));
          stemSet.add(new MorAna(root, stem.getMsd().replace(
                  "Nc-sn".substring(0, 2), "Afp")));
        }

      if (stemSet.size() == 0) {
        stemSet.add(new MorAna(matcher.group(1), "Afp-sn"));
        stemSet.add(new MorAna(matcher.group(1), "Nc-sn"));
      }

      return stemSet;
    }

    // 16-i
    matcher = PATTERN_2.matcher(number);
    if (matcher.matches()) {
      stemSet.add(new MorAna(number, "Afp-sn"));
      stemSet.add(new MorAna(number, "Onf-sn"));
      return stemSet;
    }

    // 16-(ai/ei/1-jei)
    matcher = PATTERN_3.matcher(number);
    if (matcher.matches()) {
      root = matcher.group(1);
      suffix = matcher.group(3);
      if (suffix.length() > 0)
        for (MorAna stem : MorPhonGuesser.guess(root, suffix)) {
          stemSet.add(new MorAna(root, "Afp-" + stem.getMsd().substring(3)));
        }

      if (stemSet.size() == 0) {
        stemSet.add(new MorAna(matcher.group(1), "Afp-sn"));
      }

      return stemSet;
    }

    // +/-12345
    matcher = PATTERN_4.matcher(number);
    if (matcher.matches()) {
      root = matcher.group(1);
      suffix = matcher.group(2);
      if (suffix.length() > 0)
        for (MorAna stem : MorPhonGuesser.guess(root, suffix)) {
          stemSet.add(new MorAna(root, nounToOther(stem.getMsd(),
                  "Ons----------")));
        }

      if (stemSet.size() == 0) {
        stemSet.add(new MorAna(number, "Ons-sn"));
      }
      return stemSet;
    }

    // 12:30 12.30 Ont-sn
    matcher = PATTERN_6.matcher(number);
    if (matcher.matches()) {

      if (Integer.parseInt(matcher.group(2)) < 24
              && Integer.parseInt(matcher.group(3)) < 60) {

        root = matcher.group(1);
        suffix = matcher.group(4);
        if (suffix.length() > 0)
          for (MorAna stem : MorPhonGuesser.guess(root, suffix)) {
            stemSet.add(new MorAna(root, nounToOther(stem.getMsd(),
                    "Ont---------")));
          }

        if (stemSet.size() == 0) {
          stemSet.add(new MorAna(number, "Ont-sn"));
        }
      }
    }

    // 12345-12345-*
    matcher = PATTERN_5.matcher(number);
    if (matcher.matches()) {

      root = matcher.group(1);
      suffix = matcher.group(2);
      if (suffix.length() > 0)
        for (MorAna stem : MorPhonGuesser.guess(root, suffix)) {
          stemSet.add(new MorAna(root, nounToOther(stem.getMsd(),
                  "Onr---------")));
          stemSet.add(new MorAna(root, nounToOther(stem.getMsd(),
                  "Onf----------")));
          stemSet.add(new MorAna(root, nounToNumeral(stem.getMsd(),
                  "Mc---d-------")));
        }

      if (stemSet.size() == 0) {
        stemSet.add(new MorAna(number, "Onr-sn"));
        stemSet.add(new MorAna(number, "Onf-sn"));
        stemSet.add(new MorAna(number, "Mc-snd"));
      }

      return stemSet;
    }

    // 12345,12345-12345,12345-*
    // 12345-12345,12345-*
    // 12345,12345-12345-*

    matcher = PATTERN_7.matcher(number);

    if (!matcher.matches()) {
      matcher = PATTERN_8.matcher(number);
    }
    if (!matcher.matches()) {
      matcher = PATTERN_9.matcher(number);
    }

    if (matcher.matches()) {

      root = matcher.group(1);
      suffix = matcher.group(2);
      if (suffix.length() > 0)
        for (MorAna stem : MorPhonGuesser.guess(root, suffix)) {
          stemSet.add(new MorAna(root, nounToNumeral(stem.getMsd(),
                  "Mf---d-------")));
        }

      if (stemSet.size() == 0) {
        stemSet.add(new MorAna(number, "Mf-snd"));
      }

      return stemSet;
    }

    // 12345.12345,12345
    matcher = PATTERN_10.matcher(number);
    if (matcher.matches()) {
      root = matcher.group(1);
      suffix = matcher.group(2);
      if (suffix.length() > 0)
        for (MorAna stem : MorPhonGuesser.guess(root, suffix)) {
          stemSet.add(new MorAna(root, nounToOther(stem.getMsd(),
                  "Ond---------")));
        }

      if (stemSet.size() == 0) {
        stemSet.add(new MorAna(number, "Ond-sn"));
      }

      return stemSet;
    }

    // 10:30-*
    matcher = PATTERN_11.matcher(number);
    if (matcher.matches()) {

      root = matcher.group(1);
      suffix = matcher.group(2);
      if (suffix.length() > 0) {
        for (MorAna stem : MorPhonGuesser.guess(root, suffix)) {
          stemSet.add(new MorAna(root, nounToOther(stem.getMsd(),
                  "Onf---------")));
          stemSet.add(new MorAna(root, nounToOther(stem.getMsd(),
                  "Onq---------")));
          stemSet.add(new MorAna(root, nounToOther(stem.getMsd(),
                  "Onr---------")));
        }
      }

      if (stemSet.size() == 0) {
        stemSet.add(new MorAna(number, "Onf-sn"));
        stemSet.add(new MorAna(number, "Onq-sn"));
        stemSet.add(new MorAna(number, "Onr-sn"));
      }

      return stemSet;
    }

    // 12345.12345.1234-.
    matcher = PATTERN_12.matcher(number);
    if (matcher.matches()) {
      root = matcher.group(1);
      suffix = matcher.group(2);
      if (suffix.length() > 0) {
        for (MorAna stem : MorPhonGuesser.guess(root, suffix)) {
          stemSet.add(new MorAna(root, nounToOther(stem.getMsd(),
                  "Oi----------")));
          stemSet.add(new MorAna(root, nounToOther(stem.getMsd(),
                  "Ond---------")));
        }
      }

      if (stemSet.size() == 0) {
        stemSet.add(new MorAna(number, "Oi--sn"));
        stemSet.add(new MorAna(number, "Ond-sn"));
      }

      return stemSet;
    }

    // 16-a 17-e 16-áig 17-éig 1-je 1-jéig

    matcher = PATTERN_16.matcher(number);

    if (!matcher.matches()) {
      matcher = PATTERN_17.matcher(number);
    }

    if (matcher.matches()) {

      root = matcher.group(2);
      suffix = matcher.group(4);

      if (suffix.length() > 0) {
        for (MorAna stem : MorPhonGuesser.guess(root, suffix)) {
          stemSet.add(new MorAna(root, nounToNumeral(stem.getMsd(),
                  "Mc---d----s3-")));
          if (hu.u_szeged.magyarlanc.resource.Util.isDate(matcher.group(2))) {
            stemSet.add(new MorAna(root + ".", nounToNoun(stem.getMsd(),
                    "Nc-sn".substring(0, 2) + "------s3-")));
          }

          if (matcher.group(3).equals("�")) {
            stemSet.add(new MorAna(root, nounToNumeral(stem.getMsd(),
                    "Mc---d------s")));
          }
        }
      }

      if (stemSet.size() == 0) {
        stemSet.add(new MorAna(matcher.group(2), "Mc-snd----s3"));
        if (hu.u_szeged.magyarlanc.resource.Util.isDate(matcher.group(2))) {
          stemSet.add(new MorAna(matcher.group(2) + ".", "Nc-sn"
                  + "---s3"));
        }
      }

      return stemSet;
    }

    // 50%
    matcher = PATTERN_18.matcher(number);

    if (matcher.matches()) {

      root = matcher.group(1);
      suffix = matcher.group(2);

      if (suffix.length() > 0)
        for (MorAna stem : MorPhonGuesser.guess(root, suffix)) {
          stemSet.add(new MorAna(root, nounToOther(stem.getMsd(),
                  "Onp---------")));
        }

      if (stemSet.size() == 0) {
        stemSet.add(new MorAna(root, "Onp-sn"));
      }
      return stemSet;

    }

    // 12,3-nak
    matcher = PATTERN_13.matcher(number);
    if (matcher.matches()) {

      root = matcher.group(1);
      suffix = matcher.group(2);
      if (suffix.length() > 0)
        for (MorAna stem : MorPhonGuesser.guess(root, suffix)) {
          stemSet.add(new MorAna(root, nounToNumeral(stem.getMsd(),
                  "Mf---d-------")));
        }

      if (stemSet.size() == 0) {
        stemSet.add(new MorAna(number, "Mf-snd"));
      }

      return stemSet;
    }

    // 20-nak
    matcher = PATTERN_14.matcher(number);
    if (matcher.matches()) {

      root = matcher.group(1);
      suffix = matcher.group(2);
      if (suffix.length() > 0)
        for (MorAna stem : MorPhonGuesser.guess(root, suffix)) {
          stemSet.add(new MorAna(root, nounToNumeral(stem.getMsd(),
                  "Mc---d-------")));
        }

      if (stemSet.size() == 0) {
        stemSet.add(new MorAna(number, "Mc-snd"));
      }

      return stemSet;
    }

    // 15.
    matcher = PATTERN_15.matcher(number);
    if (matcher.matches()) {

      root = matcher.group(1);
      suffix = matcher.group(3);

      if (suffix.length() > 0)
        for (MorAna stem : MorPhonGuesser.guess(root, suffix)) {

          stemSet.add(new MorAna(root, nounToNumeral(stem.getMsd(),
                  "Mo---d-------")));

          if (hu.u_szeged.magyarlanc.resource.Util.isDate(matcher.group(2))) {
            stemSet.add(new MorAna(root, stem.getMsd()));
          }
        }

      if (stemSet.size() == 0) {
        stemSet.add(new MorAna(number, "Mo-snd"));
        if (hu.u_szeged.magyarlanc.resource.Util.isDate(matcher.group(2))) {
          stemSet.add(new MorAna(number, "Nc-sn"));
          stemSet.add(new MorAna(number, "Nc-sn" + "---s3"));
        }
      }

      return stemSet;
    }

    if (stemSet.size() == 0) {
      stemSet.add(new MorAna(number, "Oi--sn"));
    }

    return stemSet;
  }

  public static Set<MorAna> guessRomanNumber(String word) {

    Set<MorAna> stemSet = null;
    stemSet = new HashSet<MorAna>();

    // MCMLXXXIV
    if (word
            .matches("^M{0,4}(CM|CD|D?C{0,3})(XC|XL|L?X{0,3})(IX|IV|V?I{0,3})$")) {
      stemSet.add(new MorAna(String.valueOf(romanToArabic(word)), "Mc-snr"));
    }

    // MCMLXXXIV.
    else if (word
            .matches("^M{0,4}(CM|CD|D?C{0,3})(XC|XL|L?X{0,3})(IX|IV|V?I{0,3})\\.$")) {
      stemSet.add(new MorAna(String.valueOf(romanToArabic(word.substring(0,
              word.length() - 1))) + ".", "Mo-snr"));
    }

    // MCMLXXXIV-MMIX
    else if (word
            .matches("^M{0,4}(CM|CD|D?C{0,3})(XC|XL|L?X{0,3})(IX|IV|V?I{0,3})-M{0,4}(CM|CD|D?C{0,3})(XC|XL|L?X{0,3})(IX|IV|V?I{0,3})$")) {
      stemSet.add(new MorAna(String.valueOf(romanToArabic(word.substring(0,
              word.indexOf("-"))))
              + "-"
              + String.valueOf(romanToArabic(word.substring(word.indexOf("-") + 1,
              word.length()))), "Mc-snr"));
    }

    // MCMLXXXIV-MMIX.
    else if (word
            .matches("^M{0,4}(CM|CD|D?C{0,3})(XC|XL|L?X{0,3})(IX|IV|V?I{0,3})-M{0,4}(CM|CD|D?C{0,3})(XC|XL|L?X{0,3})(IX|IV|V?I{0,3})\\.$")) {
      stemSet.add(new MorAna(String.valueOf(romanToArabic(word.substring(0,
              word.indexOf("-"))))
              + "-"
              + String.valueOf(romanToArabic(word.substring(word.indexOf("-") + 1,
              word.length()))) + ".", "Mo-snr"));
    }

    return stemSet;
  }

  public static void main(String[] args) {
    System.out.println(guess("0100101110101101"));
  }
}
