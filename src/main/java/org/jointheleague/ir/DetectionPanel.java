package org.jointheleague.ir;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

public class DetectionPanel extends JPanel {
	private static final long serialVersionUID = 3066409411622985843L;

	private String backgroundText = null;
	private BufferedImage image;

	public DetectionPanel(Dimension size) {
		setLayout(null);
		setPreferredSize(size);
		setBorder(BorderFactory.createLineBorder(Color.BLACK));
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		if (image != null) {
			g.drawImage(image, 0, 0, this);
		}

		if (backgroundText != null) {
			g.setColor(Color.BLACK);
			g.setFont(new Font("Arial", Font.BOLD, getWidth() / 25));

			FontMetrics metrics = getFontMetrics(g.getFont());
			g.drawString(backgroundText, (getWidth() - metrics.stringWidth(backgroundText)) / 2,
					((getHeight() - metrics.getHeight()) / 2) + metrics.getAscent());
		}
	}

	public String getBackgroundText() {
		return backgroundText;
	}

	public void setBackgroundText(String backgroundText) {
		this.backgroundText = backgroundText;
	}

	public BufferedImage getImage() {
		return image;
	}

	public void setImage(BufferedImage image) {
		this.image = image;
	}
}
