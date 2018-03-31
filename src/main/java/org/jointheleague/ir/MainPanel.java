package org.jointheleague.ir;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public class MainPanel extends JPanel {
	private static final long serialVersionUID = 8414775521754491472L;

	public MainPanel() {
		JTabbedPane pane = new JTabbedPane(JTabbedPane.LEFT);
		MeasurePanel panel = new MeasurePanel();
		pane.add("Databases", new DatabaseListPanel(panel.getPreferredSize()));
		pane.add("Measure", panel);
		pane.setSelectedIndex(1);

		add(pane);
	}
}
