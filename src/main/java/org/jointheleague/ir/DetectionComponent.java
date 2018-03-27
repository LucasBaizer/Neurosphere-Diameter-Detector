package org.jointheleague.ir;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.util.Objects;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

public class DetectionComponent extends JComponent {
	private static final long serialVersionUID = 3758238562426778363L;

	private Point selectedPoint;
	private int actualDiameter;
	private boolean selected;

	public DetectionComponent(Detection detection, double scale) {
		Objects.requireNonNull(detection);

		int originalX = (int) ((detection.getCenter().x - detection.getRadius()) * scale);
		int originalY = (int) ((detection.getCenter().y - detection.getRadius()) * scale);
		setBounds(originalX, originalY, (int) (detection.getDiameter() * scale),
				(int) (detection.getDiameter() * scale));

		setFont(new Font("Arial", Font.BOLD, (int) (detection.getRadius() * scale / 2d)));

		actualDiameter = detection.getDiameter();

		MouseAdapter listener = new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				selected = true;
				selectedPoint = e.getPoint();
				repaint();
			}

			@Override
			public void mouseDragged(MouseEvent e) {
				Point pt = e.getPoint();
				SwingUtilities.convertPointToScreen(pt, DetectionComponent.this);
				SwingUtilities.convertPointFromScreen(pt, getParent());

				setLocation(pt.x - selectedPoint.x, pt.y - selectedPoint.y);
				paintImmediately(getBounds());
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				selected = false;
				selectedPoint = null;
				repaint();
			}
		};
		addMouseListener(listener);
		addMouseMotionListener(listener);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		Graphics2D g2d = (Graphics2D) g;
		g2d.setFont(getFont());
		if (selected) {
			g2d.setColor(new Color(50, 220, 50));
		} else {
			g2d.setColor(Color.GREEN);
		}
		g2d.setStroke(new BasicStroke(2f));
		g2d.draw(new Ellipse2D.Double(0, 0, getWidth(), getHeight()));

		FontMetrics metrics = getFontMetrics(getFont());
		String text = Integer.toString(Measurement.PIXELS.to(Measurement.MICROMETERS, actualDiameter)) + "μm";
		g.drawString(text, (getWidth() - metrics.stringWidth(text)) / 2,
				((getHeight() - metrics.getHeight()) / 2) + metrics.getAscent());
	}
}
