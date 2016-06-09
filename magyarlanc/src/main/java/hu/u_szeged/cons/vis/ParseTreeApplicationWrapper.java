package hu.u_szeged.cons.vis;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.imageio.ImageIO;

import hu.u_szeged.cons.util.ConstTool;
import pta.ParseTree;
import pta.ParseTreeApplication;
import pta.ParseTreePanel;

public class ParseTreeApplicationWrapper {

	public static final String IMAGE_FORMAT = "PNG";
	public static final String DEFAULT_ERROR_TREE = "(ROOT (CP (ADVP (I Sajnos)) (NEG (R nem)) (V_ (V0 (V sikerült))) (INF_-NOM:V_ (INF0 (V leelemezni))) (NP (T a) (N mondatot)) (PUNC .)))"; 
	//"(ROOT (- Sajnos) (- nem) (- sikerült) (- leelemezni) (- a) (- mondatot) (- .))";

	public static void init() {

	}

	public static byte[] exportToByteArray(String[][] sentence){
		return exportToByteArray(ConstTool.coloumnFormat2Tree(sentence).toString());
	}

	public static byte[] exportToByteArray(String sentence){
		ParseTreeApplication pta = new ParseTreeApplication(true);

		ByteArrayOutputStream byteArrayOutputStream = null;
		try {
			byteArrayOutputStream = new ByteArrayOutputStream();
			ImageIO.write((RenderedImage) getImage(sentence), IMAGE_FORMAT, byteArrayOutputStream);
			byteArrayOutputStream.flush();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				byteArrayOutputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return byteArrayOutputStream.toByteArray();
	}

	public static Image getImage(String sentence){
		ParseTreePanel ptp = getParseTreePanel(sentence);
		Image image = ptp.createImage((int) ptp.getSize().getWidth(), (int) ptp.getSize().getHeight());

		Graphics imageGraphics = image.getGraphics();

		// store and change the background color
		Color backgroundColor = ptp.getBackground();
		ptp.setBackground(Color.WHITE);

		// draw it
		ptp.paintComponent(imageGraphics);

		// restore the background color
		ptp.setBackground(backgroundColor);

		// imageGraphics.dispose();
		return image;
	}
	
	public static ParseTreePanel getParseTreePanel(String[][] sentence){
		try {
			String tree = ConstTool.coloumnFormat2Tree(sentence).toString();
			return getParseTreePanel(tree);
		} catch (Exception e) {
		}
		return getParseTreePanel(DEFAULT_ERROR_TREE);
	}
	
	public static ParseTreePanel getParseTreePanel(String sentence) {
		ParseTreeApplication pta = new ParseTreeApplication(true);
		
		ParseTreePanel ptp = null;
		try {
			ptp = new ParseTreePanel(ParseTree.build(sentence), ParseTreePanel.TOP_BIASED);
		} catch (Exception e) {
			try {
				ptp = new ParseTreePanel(ParseTree.build("(ROOT (N error))"), ParseTreePanel.TOP_BIASED);
			} catch (Exception e1) {
				e.printStackTrace();
				System.exit(1);
			}
		}
		
		return ptp;
	}

}
