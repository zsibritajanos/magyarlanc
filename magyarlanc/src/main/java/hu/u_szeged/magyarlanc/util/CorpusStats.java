package hu.u_szeged.magyarlanc.util;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;


public class CorpusStats {
  
  public static Map<String, Integer> stat(List<List<String>> document,
      int columnIndex) {
    
    Map<String, Integer> stat = null;
    stat = new TreeMap<String, Integer>();
    
    String[] splitted = null;
    for (List<String> sentence : document) {
      for (String token : sentence) {
        splitted = token.split("\t");
        // if (splitted[4].equals("VAN") || splitted[4].equals("ELL")) {
        if (!stat.containsKey(splitted[columnIndex]))
          stat.put(splitted[columnIndex], 0);
        stat.put(splitted[columnIndex], stat.get(splitted[columnIndex]) + 1);
        // }
      }
    }
    
    return stat;
    
  }
  
  public static void main(String[] args) {
    String path = null;
    String[] files = null;
    String extension = null;
    
    path = "./data/conll/corrected_features/";
    
    files = new String[] { "8oelb", "10elb", "10erv", "1984", "cwszt",
        "gazdtar", "hvg", "mh", "newsml", "np", "nv", "pfred", "szerzj",
        "utas", "win2000" };
    
    extension = ".conll-2009-msd";
 
    
  }
  
}
