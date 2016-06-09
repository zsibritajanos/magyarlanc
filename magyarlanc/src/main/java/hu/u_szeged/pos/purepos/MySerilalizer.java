package hu.u_szeged.pos.purepos;

import hu.ppke.itk.nlpg.purepos.model.internal.RawModel;

import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * Created by zsibritajanos on 2016.01.16..
 */
public class MySerilalizer {

  public static RawModel readModel(String file) {
    ObjectInputStream input = null;
    RawModel model = null;
    try {
      input = new ObjectInputStream(MySerilalizer.class.getClassLoader().getResourceAsStream(file));
      model = (RawModel) input.readObject();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    } finally {
      try {
        input.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    return model;
  }
}
