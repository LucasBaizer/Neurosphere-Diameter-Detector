package org.jointheleague.ir;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class DatabaseListPanel extends JPanel {
	private static final long serialVersionUID = -2960929929599964237L;

	private boolean ignore = false;

	public DatabaseListPanel(Dimension size) {
		DetectionDatabase.CURRENT.clear();

		JTabbedPane databases = new JTabbedPane(JTabbedPane.TOP);
		// databases.setMinimumSize(size);

		String dbs = Cache.get("Databases");
		String[] arr = dbs.split(";");
		for (String item : arr) {
			if (item.trim().isEmpty())
				continue;
			File file = new File(item);

			try {
				DetectionDatabase db = DetectionDatabase.read(file);
				DetectionDatabase.CURRENT.add(db);
				databases.add(db.getName(), new DatabasePanel(db));
			} catch (IOException e) {
				JOptionPane.showMessageDialog(Main.FRAME,
						"There was an error loading the database: " + file.getAbsolutePath() + ".");
				Cache.save("Databases", Cache.get("Databases").replace(file.getAbsolutePath() + ";", ""));
				continue;
			}
		}
		dbs = Cache.get("Databases");

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

					String[] saveDir = new String[1]; // sorry
					saveDir[0] = Cache.get("DatabaseDirectory");

					JPanel panel = new JPanel();
					panel.setLayout(new BorderLayout());
					JPanel l1 = new JPanel();
					JPanel l2 = new JPanel();
					l1.add(new JLabel("Database name: "));
					l1.add(new JTextField(15));
					JLabel label = new JLabel("Database directory: " + saveDir[0]);
					l2.add(label);

					JButton change = new JButton("Change");
					l2.add(change);
					panel.add(l1, BorderLayout.PAGE_START);
					panel.add(l2, BorderLayout.PAGE_END);

					change.addActionListener(event -> {
						JFileChooser chooser = new JFileChooser(Cache.get("DatabaseDirectory"));
						chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
						chooser.setAcceptAllFileFilterUsed(false);

						if (chooser.showOpenDialog(Main.FRAME) == JFileChooser.APPROVE_OPTION) {
							File dir = chooser.getSelectedFile();

							saveDir[0] = dir.getAbsolutePath();

							String path = dir.getAbsolutePath();
							if (dir.getAbsolutePath().length() >= 33) {
								path = dir.getAbsolutePath().substring(0, 30) + "...";
							}
							label.setText("Database directory: " + path);

							Cache.save("DatabaseDirectory", dir.getAbsolutePath());

							SwingUtilities.getWindowAncestor(change).pack();
						}
					});

					int x = JOptionPane.showConfirmDialog(Main.FRAME, panel, Program.APPLICATION_NAME,
							JOptionPane.OK_CANCEL_OPTION);

					ignore = true;
					if (x == JOptionPane.OK_OPTION) {
						String txt = ((JTextField) ((JPanel) panel.getComponent(0)).getComponent(1)).getText();

						File file = new File(saveDir[0], txt + "." + DetectionDatabase.FILE_EXTENSION);
						if (file.exists()) {
							int y = JOptionPane.showConfirmDialog(Main.FRAME,
									"The file already exists. Are you sure you want to overwite it?",
									Program.APPLICATION_NAME, JOptionPane.OK_CANCEL_OPTION);
							if (y != JOptionPane.OK_OPTION) {
								databases.setSelectedIndex(databases.getTabCount() - 2);
								ignore = false;
								return;
							}

							file.delete();
						}
						try {
							file.createNewFile();
							DetectionDatabase db = new DetectionDatabase(txt, file);
							databases.insertTab(txt, null, new DatabasePanel(db), null, databases.getTabCount() - 1);

							DetectionDatabase.CURRENT.add(db);
							Cache.save("Databases", Cache.get("Databases") + file.getAbsolutePath() + ";");
						} catch (IOException ex) {
							Program.exit(ex);
						}
					}
					if (databases.getTabCount() == 1) {
						databases.setSelectedIndex(-1);
					} else {
						databases.setSelectedIndex(databases.getTabCount() - 2);
					}
					ignore = false;
				});

				JOptionPane.showOptionDialog(Main.FRAME, message, Program.APPLICATION_NAME, JOptionPane.DEFAULT_OPTION,
						JOptionPane.PLAIN_MESSAGE, null, new Object[0], null);
			}
		});

		add(databases);
	}
}
