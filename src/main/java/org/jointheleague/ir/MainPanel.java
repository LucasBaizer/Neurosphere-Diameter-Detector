package org.jointheleague.ir;

import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public class MainPanel extends JPanel {
	private static final long serialVersionUID = 8414775521754491472L;

	private static MainPanel instance;

	public static MainPanel getInstance() {
		return instance;
	}

	private JTabbedPane tabbedPane;

	public MainPanel() {
		instance = this;

		JTabbedPane pane = tabbedPane = new JTabbedPane(JTabbedPane.LEFT);
		MeasurePanel panel = new MeasurePanel();
		pane.add("Databases", new DatabaseListPanel(
				new Dimension(panel.getPreferredSize().width, panel.getPreferredSize().height - 50)));
		pane.add("Measure", panel);
		pane.setSelectedIndex(1);

		add(pane);
	}

	public JTabbedPane getTabbedPane() {
		return tabbedPane;
	}

	public void setTabbedPane(JTabbedPane tabPane) {
		this.tabbedPane = tabPane;
	}
}
