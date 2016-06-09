package hu.u_szeged.dep.removevirtual;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CoNLL2009Sentence {
  
  private String[][] tokens = null;
  
  public CoNLL2009Sentence(String[][] sentence) {
    this.setTokens(sentence);
  }
  
  public void setTokens(String[][] tokens) {
    this.tokens = tokens;
  }
  
  public String[][] getTokens() {
    return tokens;
  }
  
  public String toString() {
    StringBuffer stringBuffer = null;
    stringBuffer = new StringBuffer();
    
    for (int i = 0; i < this.getTokens().length; ++i) {
      stringBuffer.append(tokens[i][0]);
      for (int j = 1; j < tokens[i].length; ++j) {
        stringBuffer.append("\t" + tokens[i][j]);
      }
      stringBuffer.append("\n");
    }
    
    return stringBuffer.toString();
  }
  
  public void removeVirtuals() {
    // VAN ROOT
    if (this.containsVANRoot()) {
      this.removeVANRoot();
    }

    // ELL ROOT
    else if (this.containsELLRoot()) {
      this.removeELLRoot();
    }
    
    // VAN/ELL
    if (this.containsVirtual()) {
      this.removeVirtualNodes();
    }
  }
  
  /**
   * Get the String column from the sentence with the specified index.
   * 
   * @param index
   * @return
   */
  private String[] getColumn(int index) {
    String[] column = null;
    column = new String[this.getTokens().length];
    
    for (int i = 0; i < this.getTokens().length; ++i) {
      column[i] = this.getTokens()[i][index];
    }
    
    return column;
  }
  
  /**
   * Get the POS array of the sentence
   * 
   * @return the array of the POS
   */
  private String[] getPOS() {
    return this.getColumn(4);
  }
  
  /**
   * Get the rel. array of the sentence
   * 
   * @return the array of the rel.
   */
  private String[] getRel() {
    return this.getColumn(10);
  }
  
  /**
   * 
   * @return
   */
  private boolean containsVirtual() {
    String[] POS = null;
    POS = this.getPOS();
    
    for (String s : POS) {
      if (s.equals("VAN") || s.equals("ELL")) {
        return true;
      }
    }
    
    return false;
  }
  
  private Integer[] getVirtualIndexes() {
    String[] POS = null;
    POS = this.getPOS();
    
    List<Integer> virtualId = null;
    virtualId = new ArrayList<Integer>();
    
    for (int i = 0; i < POS.length; ++i) {
      if (POS[i].equals("VAN") || POS[i].equals("ELL")) {
        virtualId.add(i);
      }
    }
    
    // ez igy 'itt' nem elegans, de gyors es mukodik
    // ha egy mondatban tobb virtualis node szerepel, es kivesszuk az elsot
    // akkor a tobbi virtualis id-ja is csokkenni fog!!!
    for (int i = 0; i < virtualId.size(); ++i) {
      virtualId.set(i, virtualId.get(i) - i);
    }
    
    return virtualId.toArray(new Integer[virtualId.size()]);
  }
  
  /**
   * 
   * @return
   */
  private boolean containsVirtualRoot(String rootType) {
    if (this.getVirtualRootIndex(rootType) > -1)
      return true;
    
    return false;
  }
  
  private boolean containsELLRoot() {
    return containsVirtualRoot("ELL");
  }
  
  private boolean containsVANRoot() {
    return containsVirtualRoot("VAN");
  }
  
  private int getVirtualRootIndex(String rootType) {
    String[] POS = null;
    String[] rel = null;
    POS = this.getPOS();
    rel = this.getRel();
    
    for (int i = 0; i < POS.length; ++i) {
      if (POS[i].equals(rootType) && rel[i].equals("ROOT")) {
        return i;
      }
    }
    
    return -1;
  }
  
  private Integer[] getChildrenIndexes(int parentIndex) {
    List<Integer> childrenId = null;
    childrenId = new ArrayList<Integer>();
    
    for (int i = 0; i < this.getTokens().length; ++i) {
      if (getNodeParentId(tokens[i]) == getNodeId(tokens[parentIndex])) {
        childrenId.add(i);
      }
    }
    return childrenId.toArray(new Integer[childrenId.size()]);
  }
  
  private int getNodeId(String[] node) {
    return Integer.parseInt(node[0]);
  }
  
  private String getNodePOS(String[] node) {
    return node[4];
  }
  
  private int getNodeParentId(String[] node) {
    return Integer.parseInt(node[8]);
  }
  
  private String getNodeRel(String[] node) {
    return node[10];
  }
  
  private void setNodeRel(String[] node, String rel) {
    node[10] = rel;
    node[11] = rel;
  }
  
  private void setNodeId(String[] node, int id) {
    node[0] = String.valueOf(id);
  }
  
  private void setNodeParentId(String[] node, int parentId) {
    node[8] = String.valueOf(parentId);
    node[9] = String.valueOf(parentId);
  }
  
  private void renumberIds(int index) {
    for (int i = index + 1; i < this.getTokens().length; ++i) {
      setNodeId(this.getTokens()[i], i);
    }
  }
  
  private void renumberParents(int deletedId) {
    for (int i = 0; i < this.getTokens().length; ++i) {
      if (getNodeParentId(this.getTokens()[i]) > deletedId) {
        setNodeParentId(this.getTokens()[i],
            getNodeParentId(this.getTokens()[i]) - 1);
      }
    }
  }
  
  private int countLabel(String[] labels, String relLabel) {
    int counter = 0;
    for (String l : labels) {
      if (l.equals(relLabel)) {
        ++counter;
      }
    }
    return counter;
  }
  
  private void removeNode(int removeIndex) {
    String[][] array = null;
    array = new String[this.getTokens().length - 1][];
    
    System.arraycopy(this.getTokens(), 0, array, 0, removeIndex);
    System.arraycopy(this.getTokens(), removeIndex + 1, array, removeIndex,
        this.getTokens().length - removeIndex - 1);
    
    this.setTokens(array);
  }
  
  private String[] getRelLabel(Integer... index) {
    String[] rels = null;
    rels = new String[index.length];
    
    for (int i = 0; i < index.length; ++i) {
      rels[i] = getNodeRel(tokens[index[i]]);
    }
    
    return rels;
  }
  
  private void removeVirtualNodes() {
    StringBuffer stringBuffer = null;
    
    // fontos, hogy ezek nem a valodi id-k, ha tobb virtualis van!!!
    for (int i : this.getVirtualIndexes()) {
      for (int j : getChildrenIndexes(i)) {
        stringBuffer = new StringBuffer(getNodeRel(tokens[i])).append(
            "-" + getNodePOS(tokens[i])).append("-" + getNodeRel(tokens[j]));
        setNodeRel(tokens[j], stringBuffer.toString());
        setNodeParentId(tokens[j], getNodeParentId(tokens[i]));
      }
      renumberIds(i);
      renumberParents(getNodeId(this.getTokens()[i]));
      removeNode(i);
    }
  }
  
  /**
   * Virtualis virtualis VAN/ELL ROOT eltavoltasa.
   */
  private void removeVirtualRoot(String rootType, String[] rels) {
    
    int rootIndex = 0;
    Integer[] childrenIndexes = null;
    String[] childrenRelLabels = null;
    
    // virtualis ROOT indexe
    rootIndex = this.getVirtualRootIndex(rootType);
    // virtualis ROOT child node-jainan idexei
    childrenIndexes = this.getChildrenIndexes(rootIndex);
    // virtualis ROOT child node-jainak relacioi
    childrenRelLabels = getRelLabel(childrenIndexes);
    
    // a virtualis ROOT lehetseges child relacioi, fontos a sorrend
    for (String rel : rels)
      
      // ha pontosan egy van az adott relaciobol
      if (countLabel(childrenRelLabels, rel) == 1) {
        // System.out.println(this);
        // System.out.println(rootIndex);
        // System.out.println(Arrays.toString(childrenIndexes));
        // System.out.println(Arrays.toString(childrenRelLabels));
        
        // az uj root node indexe
        int index = Arrays.asList(childrenIndexes).get(
            Arrays.asList(childrenRelLabels).indexOf(rel));
        
        // a virtualis ROOT-hoz kapcsolodo adott relacioju child lesz az uj ROOT
        // az uj relacio pl. ROOT-VAN-PRED lesz
        setNodeRel(this.getTokens()[index], "ROOT-" + rootType + "-" + rel);
        // az uj ROOT parentID-je 0 lesz
        setNodeParentId(this.getTokens()[index], 0);
        
        // a korábban a virtualis ROOT-hoz kapcsolt childok az uj ROOT gyermekei
        // lesznek pl. ELL-PUNCT relacioval
        for (int i : childrenIndexes) {
          if (i != index) {
            setNodeParentId(this.getTokens()[i],
                getNodeId(this.getTokens()[index]));
            
            setNodeRel(this.getTokens()[i], "ROOT-" + rootType + "-"
                + getNodeRel(this.getTokens()[i]));
          }
        }
        
        // atszamozas (id-k csokentese)
        renumberIds(rootIndex);
        // szulo azonositok atszamozasa
        renumberParents(getNodeId(this.getTokens()[rootIndex]));
        // virtualis ROOT eltavolitasa
        removeNode(rootIndex);
        break;
      }
  }
  
  /**
   * ELL
   */
  private void removeELLRoot() {
    this
        .removeVirtualRoot("ELL", new String[] { "PRED", "SUBJ", "OBJ", "OBL" });
  }
  
  /**
   * VAN
   */
  private void removeVANRoot() {
    this.removeVirtualRoot("VAN", new String[] { "PRED", "SUBJ", "ATT" });
  }
}
