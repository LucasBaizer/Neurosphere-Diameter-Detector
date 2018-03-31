package org.jointheleague.ir;

import java.awt.Dimension;
import java.io.File;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

public class DatabaseListPanel extends JPanel {
	private static final long serialVersionUID = -2960929929599964237L;

	private int counter = 1;
	private boolean ignore = false;

	public DatabaseListPanel(Dimension size) {
		DetectionDatabase.CURRENT.clear();

		JTabbedPane databases = new JTabbedPane(JTabbedPane.TOP);

		String dbs = Cache.get("Databases");
		if (dbs.isEmpty()) {
			databases.add("Database " + (counter++), new DatabasePanel(size));
		} else {
			String[] arr = dbs.split(";");
			for (String item : arr) {
				File file = new File(item);

				try {
					DetectionDatabase db = DetectionDatabase.read(file);
					DetectionDatabase.CURRENT.add(db);
					databases.add(db.getName(), new DatabasePanel(db, size));
				} catch (IOException e) {
					Program.exit(e);
				}
			}
		}

		databases.addTab(null, new ImageIcon(Program.class.getResource("/img/add-icon.png")), new JPanel());

		databases.addChangeListener(e -> {
			if (ignore) {
				return;
			}

			int idx = databases.getSelectedIndex();
			if (idx == databases.getTabCount() - 1) {

				JButton newDb = new JButton("New Database");
				JButton imprt = new JButton("Import Database");
				Object[] message = { newDb, imprt };

				newDb.addActionListener(evt -> {
					SwingUtilities.getWindowAncestor(newDb).dispose();

					ignore = true;
					databases.insertTab("Database " + (counter++), null, new DatabasePanel(size), null,
							databases.getTabCount() - 1);
					databases.setSelectedIndex(databases.getTabCount() - 2);
					ignore = false;
				});

				JOptionPane.showOptionDialog(Main.FRAME, message, Program.APPLICATION_NAME, JOptionPane.DEFAULT_OPTION,
						JOptionPane.PLAIN_MESSAGE, null, new Object[0], null);
			}
		});

		add(databases);
	}
}
