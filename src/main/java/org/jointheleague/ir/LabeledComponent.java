package org.jointheleague.ir;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;

import javax.swing.JPanel;

public class LabeledComponent extends JPanel {
	private static final long serialVersionUID = -8531643502533803659L;

	private Component component;
	private FontMetrics metrics;

	public LabeledComponent(Component comp, FontMetrics metrics) {
		this.component = comp;
		this.metrics = metrics;

		add(comp);

		setPreferredSize(
				new Dimension(comp.getPreferredSize().width, comp.getPreferredSize().height + metrics.getHeight()));
	}
}
