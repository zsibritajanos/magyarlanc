package hu.u_szeged.gui;

import java.awt.Component;
import java.awt.Toolkit;

public class GUIUtil {
  public static void moveToCenter(Component component) {
    component
        .setLocation(
            (int) ((Toolkit.getDefaultToolkit().getScreenSize().getWidth() - component
                .getPreferredSize().getWidth()) / 2),
            (int) ((Toolkit.getDefaultToolkit().getScreenSize().getHeight() - component
                .getPreferredSize().getHeight()) / 2));
  }
}
