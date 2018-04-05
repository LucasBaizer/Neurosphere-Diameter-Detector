package org.jointheleague.ir;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;

import org.apache.commons.csv.CSVFormat;

public class ExportPanel extends JPanel {
	private static final long serialVersionUID = -2007241054369013959L;

	private File saveDir;
	private String fileName;
	private char delimiter = Cache.get("Delimiter").charAt(0);

	public ExportPanel(JDialog container, String fileName, DetectionList db, CSVHandler handler) {
		setLayout(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_START,
				GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0);

		String fileWithoutExt = fileName.substring(0, fileName.indexOf('.'));

		this.saveDir = new File(Cache.get("SaveDirectory"));
		if(!this.saveDir.exists()) {
			this.saveDir = new File(System.getProperty("user.dir"));
		}
		this.fileName = fileWithoutExt + ".csv";

		JPanel directoryPanel = new JPanel();
		directoryPanel.setLayout(new BoxLayout(directoryPanel, BoxLayout.X_AXIS));

		JLabel saveDirLabel = new JLabel("Save Directory: " + Cache.get("SaveDirectory"));
		JButton saveDirButton = new JButton("Change");
		saveDirButton.addActionListener(e -> {
			JFileChooser chooser = new JFileChooser(Cache.get("SaveDirectory"));
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			chooser.setAcceptAllFileFilterUsed(false);

			if (chooser.showOpenDialog(Main.FRAME) == JFileChooser.APPROVE_OPTION) {
				File dir = chooser.getSelectedFile();

				this.saveDir = dir;

				String path = dir.getAbsolutePath();
				if (dir.getAbsolutePath().length() >= 33) {
					path = dir.getAbsolutePath().substring(0, 30) + "...";
				}
				saveDirLabel.setText("Save Directory: " + path);

				Cache.save("SaveDirectory", dir.getAbsolutePath());
			}
		});

		directoryPanel.add(saveDirLabel);
		directoryPanel.add(Box.createRigidArea(new Dimension(10, 0)));
		directoryPanel.add(saveDirButton);

		JPanel dbPanel = new JPanel();
		dbPanel.setLayout(new BoxLayout(dbPanel, BoxLayout.X_AXIS));

		JLabel dbLabel = new JLabel("Database: ");
		Object[] itemStart = DetectionDatabase.CURRENT.stream().map(e -> e.getName()).toArray();
		String[] items = Arrays.copyOf(itemStart, itemStart.length, String[].class);
		String[] finalItems = new String[items.length + 1];
		finalItems[0] = "None";
		System.arraycopy(items, 0, finalItems, 1, items.length);
		JComboBox<String> dbBox = new JComboBox<>(finalItems);
		dbBox.setSelectedItem(Cache.get("DefaultDatabase"));

		dbBox.addItemListener(e -> {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				Cache.save("DefaultDatabase", (String) e.getItem());
			}
		});

		dbPanel.add(dbLabel);
		dbPanel.add(dbBox);

		JPanel filePanel = new JPanel();
		filePanel.setLayout(new BoxLayout(filePanel, BoxLayout.X_AXIS));

		JLabel fileLabel = new JLabel("File Name: ");
		JTextField fileField = new JTextField(fileWithoutExt, fileWithoutExt.length());
		JLabel csvLabel = new JLabel(".csv");

		filePanel.add(fileLabel);
		filePanel.add(fileField);
		filePanel.add(csvLabel);

		fileField.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) {
				update(e);
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				update(e);
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				update(e);
			}

			private void update(DocumentEvent e) {
				try {
					ExportPanel.this.fileName = e.getDocument().getText(0, e.getDocument().getLength()) + ".csv";
				} catch (BadLocationException ex) {
					Program.exit(ex);
				}
			}
		});

		JPanel delimiterPanel = new JPanel();
		delimiterPanel.setLayout(new BoxLayout(delimiterPanel, BoxLayout.X_AXIS));

		JLabel delimiterLabel = new JLabel("Delimiter: ");
		JComboBox<String> delimiterBox = new JComboBox<>(new String[] { "Comma", "Tab", "Pipe" });
		delimiterBox.addItemListener(e -> {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				String item = (String) e.getItem();
				if (item.equals("Tab")) {
					delimiter = '\t';
				} else if (item.equals("Pipe")) {
					delimiter = '|';
				} else if (item.equals("Comma")) {
					delimiter = ',';
				}

				Cache.save("Delimiter", Character.toString(delimiter));
			}
		});

		delimiterPanel.add(delimiterLabel);
		delimiterPanel.add(Box.createRigidArea(new Dimension(10, 0)));
		delimiterPanel.add(delimiterBox);

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));

		JButton exportButton = new JButton("Export");
		exportButton.addActionListener(e -> {
			File csvFile = new File(saveDir, ExportPanel.this.fileName);

			if (csvFile.exists()) {
				if (JOptionPane.showConfirmDialog(Main.FRAME,
						"The file already exists. Are you sure you want to overwrite it?", Program.APPLICATION_NAME,
						JOptionPane.WARNING_MESSAGE) != JOptionPane.OK_OPTION) {
					return;
				}
			}

			try {
				csvFile.delete();
				csvFile.createNewFile();

				BufferedWriter out = new BufferedWriter(new FileWriter(csvFile));
				CSVFormat format = CSVFormat.newFormat(delimiter).withQuote('"')
						.withRecordSeparator(System.lineSeparator()).withIgnoreEmptyLines(true);
				handler.handle(fileWithoutExt, out, format);

				if (db != null) {
					String cacheDb = Cache.get("DefaultDatabase");
					if (!cacheDb.equals("None")) {
						DetectionDatabase database = DetectionDatabase.forName(cacheDb);
						database.insert(MeasurePanel.getInstance().getInputComponent().getSelectedFile(), db);
						database.save();
					}
				}

				container.dispose();
			} catch (IOException ex) {
				Program.exit(ex);
			}
		});
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(e -> {
			container.dispose();
		});

		buttonPanel.add(cancelButton);
		buttonPanel.add(exportButton);

		c.gridy++;
		add(directoryPanel, c);

		c.gridy++;
		add(filePanel, c);

		if (db != null) {
			c.gridy++;
			add(dbPanel, c);
		}

		c.gridy++;
		add(delimiterPanel, c);

		c.gridy++;
		add(buttonPanel, c);

		exportButton.requestFocus();
	}
}
