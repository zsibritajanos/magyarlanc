package hu.u_szeged.dep.removevirtual;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

public class RemoveVirtualNodes {
  
  public static void main(String[] args) {
    String file = null;
    CoNLL2009Sentence coNLL2009Sentence = null;
    
    file = "./data/objfx/10fold/law.0.test";
    
    Writer writer = null;
    
    try {
      writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
          "./data/objfx/10fold/law.0.test.virtual"), "UTF-8"));
      
			// for (String[][] sentence : Util.readCoNLL2009(file)) {
			// coNLL2009Sentence = new CoNLL2009Sentence(sentence);
			// coNLL2009Sentence.removeVirtuals();
			// writer.write(coNLL2009Sentence.toString() + "\n");
			// }
      
      writer.flush();
      writer.close();
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    
  }
}
