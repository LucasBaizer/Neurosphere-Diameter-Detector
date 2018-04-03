package org.jointheleague.ir;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Map;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.apache.commons.csv.CSVPrinter;

public class DatabasePanel extends JPanel {
	private static final long serialVersionUID = -9217449720238227058L;

	private DetectionDatabase database;
	private JLabel countLabel = new JLabel();
	private JLabel emptyLabel = new JLabel("This database is empty!");
	private JPanel vertical;
	private SizedPanel current;
	private int i = 0;

	public DatabasePanel() {
		this(null);
	}

	public DatabasePanel(DetectionDatabase db) {
		vertical = new JPanel();
		vertical.setLayout(new BoxLayout(vertical, BoxLayout.Y_AXIS));
		current = new SizedPanel(new Dimension());
		current.setLayout(new BoxLayout(current, BoxLayout.X_AXIS));

		JPanel databaseInfo = new JPanel();
		databaseInfo.setLayout(new BoxLayout(databaseInfo, BoxLayout.X_AXIS));

		JButton exportButton = new JButton("Export");
		exportButton.addActionListener(e -> {
			JDialog dialog = new JDialog(Main.FRAME, "Export");
			dialog.getContentPane().add(
					new ExportPanel(dialog, database.getDatabaseFile().getName(), null, (fileName, out, format) -> {
						CSVPrinter csvPrinter = new CSVPrinter(out, format.withHeader("Plate Name", "Cell Size (µm)"));

						for (Map.Entry<File, DetectionList> entry : database.getStorage()) {
							for (Detection detection : entry.getValue()) {
								csvPrinter.printRecord(entry.getKey().getName(), detection.getDiameter());
							}
						}

						csvPrinter.flush();
						csvPrinter.close();
					}));

			Dimension size = dialog.getContentPane().getPreferredSize();
			size.width *= 1.5;
			size.height *= 1.5;
			dialog.setSize(size);
			dialog.setLocationRelativeTo(Main.FRAME);

			dialog.setVisible(true);
		});
		databaseInfo.add(countLabel);
		databaseInfo.add(Box.createRigidArea(new Dimension(20, 0)));
		databaseInfo.add(exportButton);
		vertical.add(databaseInfo);
		vertical.add(current);
		current.add(Box.createRigidArea(new Dimension(20, 0)));

		if (db != null) {
			this.database = db;

			if (database.size() == 0) {
				add(emptyLabel);
			} else {
				emptyLabel = null;

				for (Map.Entry<File, DetectionList> content : db.getStorage()) {
					addContent(content.getKey());
				}

				add(new JScrollPane(vertical));
			}

			db.onInsert().addObserver((source, event) -> {
				if (event.getType() != DatabaseEvent.ADD) {
					return;
				}

				if (emptyLabel != null) {
					remove(emptyLabel);
					emptyLabel = null;

					add(new JScrollPane(vertical));
				}

				addContent(event.getImageFile());
				countLabel.setText(this.database.size() + " images");
			});
		} else {
			add(emptyLabel);
		}

		countLabel.setText(this.database.size() + " images");
	}

	private void addContent(File file) {
		if (i++ == 5) {
			current = new SizedPanel(new Dimension());
			current.setLayout(new BoxLayout(current, BoxLayout.X_AXIS));
			current.add(Box.createRigidArea(new Dimension(20, 0)));
			vertical.add(current);
			i = 0;
		}

		SizedPanel entry = new SizedPanel(new Dimension());
		entry.setLayout(new BoxLayout(entry, BoxLayout.Y_AXIS));

		ImageComponent comp = new ImageComponent(file, 300, 215);
		entry.add(comp);

		JLabel label = new JLabel(file.getAbsolutePath()) {
			private static final long serialVersionUID = -6848602986052433277L;

			@Override
			public Point getLocation() {
				return new Point(300, 0);
			}

			@Override
			public Point getLocation(Point rv) {
				rv.x = 300;
				rv.y = 0;
				return rv;
			}

			@Override
			public Rectangle getBounds() {
				return new Rectangle(getLocation().x, getLocation().y, getPreferredSize().width,
						getPreferredSize().height);
			}

			@Override
			public Rectangle getBounds(Rectangle rv) {
				rv.x = getLocation().x;
				rv.y = getLocation().y;
				rv.width = getPreferredSize().width;
				rv.height = getPreferredSize().height;
				return rv;
			}
		};
		entry.add(label);
		entry.setForcedSize(new Dimension(300, entry.getTypicalSize().height));

		current.add(entry);
		current.add(Box.createRigidArea(new Dimension(20, 0)));
		current.setForcedSize(new Dimension(320 * i, current.getTypicalSize().height + 20));

		comp.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) {
					MeasurePanel mp = MeasurePanel.getInstance();
					DetectionPanel output = mp.getOutputComponent();

					MainPanel.getInstance().getTabbedPane().setSelectedIndex(1);

					if (output.getImage() != null) {
						int x = JOptionPane.showConfirmDialog(Main.FRAME,
								"Are you sure you want to discard changes to the current output?",
								Program.APPLICATION_NAME, JOptionPane.OK_CANCEL_OPTION);
						if (x != JOptionPane.OK_OPTION) {
							MainPanel.getInstance().getTabbedPane().setSelectedIndex(0);
							return;
						}
					}

					output.removeAll();
					mp.getInputComponent().setImage(file);
					output.setImage(mp.getInputComponent().getRenderImage());

					mp.onFinish(database.getDetections(file), mp.getInputComponent().getScale(), 0);
				}
			}
		});
	}
}
