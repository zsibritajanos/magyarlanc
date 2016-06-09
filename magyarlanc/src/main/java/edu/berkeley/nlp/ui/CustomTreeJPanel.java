package edu.berkeley.nlp.ui;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;

import javax.swing.SwingConstants;

import edu.berkeley.nlp.ui.TreeJPanel;

public class CustomTreeJPanel extends TreeJPanel {
	
	public static Color bgColor = Color.white;
	
	@Override
	public void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		// FontMetrics fM = pickFont(g2, tree, space);

		double width = width(tree, myFont);
		double height = height(tree, myFont);
		preferredX = (int) width;
		preferredY = (int) height;
		setSize(new Dimension(preferredX, preferredY));
		setPreferredSize(new Dimension(preferredX, preferredY));
		setMaximumSize(new Dimension(preferredX, preferredY));
		setMinimumSize(new Dimension(preferredX, preferredY));
		// setSize(new Dimension((int)Math.round(width),
		// (int)Math.round(height)));
		g2.setFont(myFont.getFont());

		Dimension space = getSize();
		double startX = 0.0;
		double startY = 0.0;
		if (HORIZONTAL_ALIGN == SwingConstants.CENTER) {
			startX = (space.getWidth() - width) / 2.0;
		}
		if (HORIZONTAL_ALIGN == SwingConstants.RIGHT) {
			startX = space.getWidth() - width;
		}
		if (VERTICAL_ALIGN == SwingConstants.CENTER) {
			startY = (space.getHeight() - height) / 2.0;
		}
		if (VERTICAL_ALIGN == SwingConstants.BOTTOM) {
			startY = space.getHeight() - height;
		}
		super.paintComponent(g);

		g2.setBackground(bgColor);
		g2.clearRect(0, 0, space.width, space.height);

		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
				1.0f));
		g2.setPaint(Color.black);

		paintTree(tree, new Point2D.Double(startX, startY), g2, myFont);
	}
}
