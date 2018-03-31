package org.jointheleague.ir;

import java.awt.Dimension;
import java.io.File;

import javax.swing.JPanel;

public class DatabasePanel extends JPanel {
	private static final long serialVersionUID = -9217449720238227058L;

	private DetectionDatabase database;
	private File file;

	public DatabasePanel(Dimension size) {
		this(null, size);
	}

	public DatabasePanel(DetectionDatabase db, Dimension size) {
		if (db != null) {
			this.database = db;
			this.file = db.getDatabaseFile();
		}

		setPreferredSize(size);
	}
}
