package splitter.archive;

import java.util.Set;
import java.util.TreeSet;

public class StringCleaner {
  private static Set<Integer> errorCharacters = null;

  public StringCleaner() {
    errorCharacters = loadErrorCharacters();
  }

  private Set<Integer> loadErrorCharacters() {
    Set<Integer> errorCharacters = new TreeSet<>();

    errorCharacters.add(11);
    errorCharacters.add(12);
    errorCharacters.add(28);
    errorCharacters.add(29);
    errorCharacters.add(30);
    errorCharacters.add(31);
    errorCharacters.add(5760);
    errorCharacters.add(6158);
    errorCharacters.add(8192);
    errorCharacters.add(8193);
    errorCharacters.add(8194);
    errorCharacters.add(8195);
    errorCharacters.add(8196);
    errorCharacters.add(8197);
    errorCharacters.add(8198);
    errorCharacters.add(8200);
    errorCharacters.add(8201);
    errorCharacters.add(8202);
    errorCharacters.add(8203);
    errorCharacters.add(8232);
    errorCharacters.add(8233);
    errorCharacters.add(8287);
    errorCharacters.add(12288);
    errorCharacters.add(65547);
    errorCharacters.add(65564);
    errorCharacters.add(65565);
    errorCharacters.add(65566);
    errorCharacters.add(65567);

    return errorCharacters;
  }

  public String cleanString(String text) {

    StringBuffer sb = new StringBuffer(text);

    for (int i = 0; i < sb.length(); ++i) {

      if (errorCharacters.contains((int) sb.charAt(i))) {
        // System.err.println("unknown character: " + text.charAt(i)
        // + " has been removed");
        sb.setCharAt(i, ' ');
      }

      if ((int) sb.charAt(i) == 733) {
        sb.setCharAt(i, '"');
      }
      if ((int) sb.charAt(i) == 768) {
        sb.setCharAt(i, '\'');
      }
      if ((int) sb.charAt(i) == 769) {
        sb.setCharAt(i, '\'');
      }
      if ((int) sb.charAt(i) == 771) {
        sb.setCharAt(i, '"');
      }
      if ((int) sb.charAt(i) == 803) {
        sb.setCharAt(i, '.');
      }
      if ((int) sb.charAt(i) == 900) {
        sb.setCharAt(i, '\'');
      }
      if ((int) sb.charAt(i) == 1475) {
        sb.setCharAt(i, ':');
      }
      if ((int) sb.charAt(i) == 1523) {
        sb.setCharAt(i, '\'');
      }
      if ((int) sb.charAt(i) == 1524) {
        sb.setCharAt(i, '"');
      }
      if ((int) sb.charAt(i) == 1614) {
        sb.setCharAt(i, '\'');
      }
      if ((int) sb.charAt(i) == 1643) {
        sb.setCharAt(i, ',');
      }
      if ((int) sb.charAt(i) == 1648) {
        sb.setCharAt(i, '\'');
      }
      if ((int) sb.charAt(i) == 1764) {
        sb.setCharAt(i, '"');
      }
      if ((int) sb.charAt(i) == 8211) {
        sb.setCharAt(i, '-');
      }
      if ((int) sb.charAt(i) == 8212) {
        sb.setCharAt(i, '-');
      }
      if ((int) sb.charAt(i) == 8216) {
        sb.setCharAt(i, '\'');
      }
      if ((int) sb.charAt(i) == 8217) {
        sb.setCharAt(i, '\'');
      }
      if ((int) sb.charAt(i) == 8218) {
        sb.setCharAt(i, '\'');
      }
      if ((int) sb.charAt(i) == 8219) {
        sb.setCharAt(i, '\'');
      }
      if ((int) sb.charAt(i) == 8220) {
        sb.setCharAt(i, '"');
      }
      if ((int) sb.charAt(i) == 8221) {
        sb.setCharAt(i, '"');
      }
      if ((int) sb.charAt(i) == 8243) {
        sb.setCharAt(i, '"');
      }
      if ((int) sb.charAt(i) == 8722) {
        sb.setCharAt(i, '-');
      }
      if ((int) sb.charAt(i) == 61448) {
        sb.setCharAt(i, '\'');
      }
      if ((int) sb.charAt(i) == 61449) {
        sb.setCharAt(i, '\'');
      }
      if ((int) sb.charAt(i) == 61472) {
        sb.setCharAt(i, '.');
      }
      if ((int) sb.charAt(i) == 61474) {
        sb.setCharAt(i, '.');
      }
      if ((int) sb.charAt(i) == 61475) {
        sb.setCharAt(i, '.');
      }
      if ((int) sb.charAt(i) == 61476) {
        sb.setCharAt(i, '.');
      }
      if ((int) sb.charAt(i) == 61477) {
        sb.setCharAt(i, '.');
      }
      if ((int) sb.charAt(i) == 61480) {
        sb.setCharAt(i, '.');
      }
      if ((int) sb.charAt(i) == 61481) {
        sb.setCharAt(i, '.');
      }
      if ((int) sb.charAt(i) == 61482) {
        sb.setCharAt(i, '.');
      }
      if ((int) sb.charAt(i) == 61483) {
        sb.setCharAt(i, '.');
      }
      if ((int) sb.charAt(i) == 61484) {
        sb.setCharAt(i, '.');
      }
      if ((int) sb.charAt(i) == 61485) {
        sb.setCharAt(i, '"');
      }
      if ((int) sb.charAt(i) == 61486) {
        sb.setCharAt(i, '"');
      }
      if ((int) sb.charAt(i) == 61487) {
        sb.setCharAt(i, '"');
      }
      if ((int) sb.charAt(i) == 61488) {
        sb.setCharAt(i, '"');
      }
      if ((int) sb.charAt(i) == 65533) {
        sb.setCharAt(i, '-');
      }
    }

    return sb.toString();
  }

}
