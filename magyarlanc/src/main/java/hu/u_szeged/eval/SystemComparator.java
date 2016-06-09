package hu.u_szeged.eval;

import file.FileUtil;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by zsibritajanos on 2015.09.22..
 */
public class SystemComparator {


  public static void main(String[] args) throws IOException {

    Map<String, Set<String>> disam = new TreeMap<>();

    for (String file : FileUtil.getFileNames("./data/szk25", "split")) {
      List<String> lines = Files.readAllLines(Paths.get(file), Charset.forName("utf-8"));


      String[] split;

      for (String line : lines) {
        split = line.split("\t");
        if (split.length == 3) {

          String wordForm = split[0];
          String msd = split[2];

          String lemma = split[1].toLowerCase().replace("+", "");

          String key = (wordForm + "_" + msd).toLowerCase();

          if (!disam.containsKey(key)) {
            disam.put(key, new TreeSet<>());
          }

          disam.get(key).add(lemma);
        }
      }
    }

    for (Map.Entry<String, Set<String>> entry : disam.entrySet()) {
      if (entry.getValue().size() > 1) {
        System.out.println(entry);
      }
    }
  }
}
