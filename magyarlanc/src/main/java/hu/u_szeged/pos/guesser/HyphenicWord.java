package hu.u_szeged.pos.guesser;


import hu.u_szeged.magyarlanc.resource.ResourceHolder;
import hu.u_szeged.pos.converter.KRUtils;

import java.util.Collection;
import java.util.LinkedHashSet;


public class HyphenicWord {
  
  public static LinkedHashSet<String> analyseHyphenicCompoundWord(
      String hyphenicCompoundWord) {
    LinkedHashSet<String> analises = null;
    analises = new LinkedHashSet<String>();
    
    if (!hyphenicCompoundWord.contains("-")) {
      return analises;
    }
    
    String firstPart = null;
    String secondPart = null;
    
    int hyphenPosition = 0;
    
    hyphenPosition = hyphenicCompoundWord.indexOf('-');
    firstPart = hyphenicCompoundWord.substring(0, hyphenPosition);
    secondPart = hyphenicCompoundWord.substring(hyphenPosition + 1,
        hyphenicCompoundWord.length());
    
    // a k�t�jel el�tti �s a k�t�jel ut�ni r�sznek is van elemz�se (pl.:
    // adat-kezel�t)
    if (CompoundWord.isBisectable(firstPart + secondPart)) {
      analises = CompoundWord
          .getCompatibleAnalises(firstPart, secondPart, true);
    }

    // a k�t�jel el�tti r�sznek is van elemz�se, a k�t�jel ut�ni r�sz k�t r�szre
    // bonthat�
    else if (ResourceHolder.getRFSA().analyse(firstPart).size() > 0
        && CompoundWord.isBisectable(secondPart)) {
      Collection<String> firstPartAnalises = null;
      firstPartAnalises = ResourceHolder.getRFSA().analyse(firstPart);
      
      String firstPartOfSecondSection = null;
      String secondPartOfSecondSection = null;
      LinkedHashSet<String> secondSectionAnalises = null;
      int bisectIndex = 0;
      bisectIndex = CompoundWord.bisectIndex(secondPart);
      firstPartOfSecondSection = secondPart.substring(0, bisectIndex);
      secondPartOfSecondSection = secondPart.substring(bisectIndex, secondPart
          .length());
      
      secondSectionAnalises = CompoundWord.getCompatibleAnalises(
          firstPartOfSecondSection, secondPartOfSecondSection);
      
      for (String firstAnalyse : firstPartAnalises) {
        for (String secondAnalyse : secondSectionAnalises) {
          if (CompoundWord.isCompatibleAnalyises(KRUtils.getRoot(firstAnalyse),
              KRUtils.getRoot(secondAnalyse))) {
            if (analises == null) {
              analises = new LinkedHashSet<String>();
            }
            analises.add(KRUtils.getRoot(secondAnalyse).replace("$",
                "$" + firstPart + "-"));
          }
        }
      }
    }

    else if (CompoundWord.isBisectable(firstPart)
        && ResourceHolder.getRFSA().analyse(secondPart).size() > 0) {
      Collection<String> secondPartAnalises = null;
      secondPartAnalises = ResourceHolder.getRFSA().analyse(secondPart);
      
      String firstSectionOfFirstPart = null;
      String secondSectionOfFirstPart = null;
      LinkedHashSet<String> firstPartAnalises = null;
      int bisectIndex = 0;
      
      bisectIndex = CompoundWord.bisectIndex(firstPart);
      firstSectionOfFirstPart = firstPart.substring(0, bisectIndex);
      secondSectionOfFirstPart = firstPart.substring(bisectIndex, firstPart
          .length());
      
      firstPartAnalises = CompoundWord.getCompatibleAnalises(
          firstSectionOfFirstPart, secondSectionOfFirstPart);
      
      for (String firstAnalyse : firstPartAnalises) {
        for (String secondAnalyse : secondPartAnalises) {
          if (CompoundWord.isCompatibleAnalyises(KRUtils.getRoot(firstAnalyse),
              KRUtils.getRoot(secondAnalyse))) {
            if (analises == null) {
              analises = new LinkedHashSet<String>();
            }
            analises.add(KRUtils.getRoot(secondAnalyse).replace("$",
                "$" + firstPart + "-"));
          }
        }
      }
    }
    
    return analises;
  }
}
