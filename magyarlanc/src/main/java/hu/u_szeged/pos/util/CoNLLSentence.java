package hu.u_szeged.pos.util;

public class CoNLLSentence {
  
  private String[][] tokens = null;
  
  public CoNLLSentence(String[][] sentence) {
    this.setTokens(sentence);
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
  
  private String[] getColumn(int index) {
    String[] column = null;
    column = new String[this.getTokens().length];
    
    for (int i = 0; i < this.getTokens().length; ++i) {
      column[i] = this.getTokens()[i][index];
    }
    
    return column;
  }
  
  public String[] getRel() {
    return this.getColumn(5);
  }
  
  public String[] getForm() {
    return this.getColumn(1);
  }
  
  public void setTokens(String[][] tokens) {
    this.tokens = tokens;
  }
  
  public String[][] getTokens() {
    return tokens;
  }
  
}
