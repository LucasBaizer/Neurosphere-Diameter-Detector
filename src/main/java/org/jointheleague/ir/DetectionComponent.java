package org.jointheleague.ir;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Ellipse2D;
import java.util.Objects;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

public class DetectionComponent extends JComponent {
	private static final long serialVersionUID = 3758238562426778363L;

	private Point selectedPoint;
	private Color color = Color.GREEN;
	private int actualDiameter;
	private boolean selected;
	private boolean destroyed;

	public DetectionComponent(DetectionList list, Detection detection, double scale) {
		Objects.requireNonNull(detection);

		int originalX = (int) ((detection.getCenter().x - detection.getRadius()) * scale);
		int originalY = (int) ((detection.getCenter().y - detection.getRadius()) * scale);
		int originalDiameter = (int) (detection.getDiameter() * scale);
		int deltaScale = (int) (detection.getDiameter() / 50d);

		setBounds(originalX, originalY, originalDiameter, originalDiameter);
		setFont(new Font("Arial", Font.BOLD, (int) (detection.getRadius() * scale / 2d)));

		actualDiameter = detection.getDiameter();

		MouseAdapter listener = new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				selected = true;
				selectedPoint = e.getPoint();
				color = Color.GREEN;
				repaint();

				requestFocus();
			}

			@Override
			public void mouseDragged(MouseEvent e) {
				Point pt = e.getPoint();
				SwingUtilities.convertPointToScreen(pt, DetectionComponent.this);
				SwingUtilities.convertPointFromScreen(pt, getParent());

				setLocation(pt.x - selectedPoint.x, pt.y - selectedPoint.y);
				detection.setCenter(getLocation()); // TODO is this necessary?

				// list.onListChanged().notifyObservers(new
				// ListEvent<Detection>(ListEvent.MANUAL, detection));

				repaint();
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				selected = false;
				selectedPoint = null;
				repaint();
			}

			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				if (selected) {
					Dimension size = getSize();
					size.width += (deltaScale * e.getWheelRotation());
					size.height += (deltaScale * e.getWheelRotation());

					detection.setDiameter((int) (size.width / scale));
					actualDiameter = detection.getDiameter();

					setLocation(getX() - e.getWheelRotation(), getY() - e.getWheelRotation());
					selectedPoint.x += e.getWheelRotation();
					selectedPoint.y += e.getWheelRotation();

					setFont(new Font("Arial", Font.BOLD, (int) (detection.getRadius() * scale / 2d)));
					setSize(size);

					list.onListChanged().markChanged();
					list.onListChanged().notifyObservers(new ListEvent<Detection>(ListEvent.MANUAL, detection));

					repaint();
				}
			}
		};
		addMouseListener(listener);
		addMouseMotionListener(listener);
		addMouseWheelListener(listener);

		getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "delete");
		getActionMap().put("delete", new AbstractAction() {
			private static final long serialVersionUID = 8428486399192334220L;

			@Override
			public void actionPerformed(ActionEvent e) {
				destroyed = true;
				selected = false;
				selectedPoint = null;

				removeMouseListener(listener);
				removeMouseMotionListener(listener);
				removeMouseWheelListener(listener);

				list.remove(detection);

				repaint();

				// we don't remove from the parent because it stops rendering
				// and there's no cleanup... and we can't manually cleanup by
				// calling repaint... just Swing things
			}
		});
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		if (destroyed) {
			return;
		}

		Graphics2D g2d = (Graphics2D) g;
		g2d.setFont(getFont());
		if (selected) {
			g2d.setColor(new Color(50, 220, 50));
		} else {
			g2d.setColor(color);
		}
		g2d.setStroke(new BasicStroke(2f));
		g2d.draw(new Ellipse2D.Double(0, 0, getWidth(), getHeight()));

		FontMetrics metrics = getFontMetrics(getFont());
		String text = Integer.toString((int) Measurement.PIXELS.convert(Measurement.MICROMETERS, actualDiameter))
				+ "μm";
		g.drawString(text, (getWidth() - metrics.stringWidth(text)) / 2,
				((getHeight() - metrics.getHeight()) / 2) + metrics.getAscent());
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}
}
