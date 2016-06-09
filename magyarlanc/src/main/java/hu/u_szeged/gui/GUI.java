package hu.u_szeged.gui;

import hu.u_szeged.config.Config;
import hu.u_szeged.cons.vis.BerkeleyUIWrapper;
import hu.u_szeged.cons.vis.ParseTreeApplicationWrapper;
import hu.u_szeged.dep.whatswrong.WhatsWrongWrapper;
import hu.u_szeged.magyarlanc.Magyarlanc;
import pta.ParseTreePanel;
import splitter.MySplitter;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public class GUI {

  private static Dimension dimension = null;
  private static JFrame frame = null;
  private static JTextField inputField = null;
  private static JButton sendButton = null;
  private static JTextArea textarea = null;
  private static JLabel imageLabel = null;
  private static JLabel constImageLabel = null;
  private static JPanel parsePanel = null;
  private static ParseTreePanel ptp = null;

  private final static String BUTTON_TEXT = "OK";

  private static String[] sentence = null;
  private static String[][] parsed = null;

  public static void init() {

    Magyarlanc.fullInit();

    frame = new JFrame("magyarlanc 3.0");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.getContentPane().setLayout(new BorderLayout());

    JPanel inputPanel = new JPanel();
    inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.X_AXIS));
    inputPanel.setBorder(new EmptyBorder(15, 15, 15, 15));


    parsePanel = new JPanel();
    //parsePanel.setLayout(new BoxLayout(parsePanel, BoxLayout.PAGE_AXIS));
    frame.getContentPane().add(parsePanel, "Center");

    inputField = new JTextField(
            Config.getInstance().getGuiInitSentences());

    sendButton = new JButton(BUTTON_TEXT);
    sendButton.addActionListener(new ActionListener() {

      public void actionPerformed(ActionEvent actionEvent) {
        if (inputField.getText() != null && !inputField.getText().equals("")) {

          sentence = MySplitter.getInstance().splitToArray(
                  inputField.getText())[0];

          parsed = Magyarlanc.parseSentence(sentence);

          //depParsed = Magyarlanc.depParseSentence(sentence);
          //constParsed = Magyarlanc.constParseSentence(sentence);

          parsePanel.removeAll();

          // image
          BufferedImage bufferedImage = null;
          try {
            bufferedImage = ImageIO.read(new ByteArrayInputStream(
                    WhatsWrongWrapper.exportToByteArray(parsed)));
          } catch (IOException e) {
            e.printStackTrace();
          }

          if (imageLabel != null)
            imageLabel.setVisible(false);

          imageLabel = new JLabel(new ImageIcon(bufferedImage));

          parsePanel.add(imageLabel);

          //const
          if (Config.getInstance().getConstVisualizer().equals("berkeley")) {
        	BufferedImage constBufferedImage = (BufferedImage) BerkeleyUIWrapper.getImage(parsed);
			if (constImageLabel != null)
				constImageLabel.setVisible(false);

			constImageLabel = new JLabel(new ImageIcon(constBufferedImage));

			parsePanel.add(constImageLabel);
        	  
          } else {
	          ParseTreePanel.ERROR_COLOR = Color.BLACK;
	          ptp = ParseTreeApplicationWrapper.getParseTreePanel(parsed);
	          // set swing default bg color
	          ptp.setBackground(UIManager.getColor("Panel.background"));
	          parsePanel.add(ptp);
          }
          // textarea
          if (textarea != null)
            textarea.setVisible(false);

          textarea = new JTextArea();
          textarea.setText(Magyarlanc.sentenceAsString(parsed));
          textarea.setMargin(new Insets(10, 10, 10, 10));
          frame.getContentPane().add(textarea, "South");

          GUIUtil.moveToCenter(frame);
          frame.pack();
          frame.setVisible(true);
        }
      }
    });

    inputPanel.add(inputField);
    inputPanel.add(sendButton);

    frame.getContentPane().add(inputPanel, "North");
    dimension = Toolkit.getDefaultToolkit().getScreenSize();

    frame.setPreferredSize(new Dimension((int) dimension.getWidth() - 150,
            (int) dimension.getHeight() - 150));
    frame.setResizable(false);

    GUIUtil.moveToCenter(frame);

    frame.pack();
    frame.setVisible(true);
  }

  public static void main(String[] args) {
    init();
  }
}
