package org.jointheleague.ir;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import javax.swing.AbstractAction;
import javax.swing.JDialog;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

public class Toolbar extends JMenuBar {
	private static final long serialVersionUID = 4933781209241086901L;

	public Toolbar() {
		super();

		add(new FileMenu());
		add(new DataMenu());
		add(new HelpMenu());
	}

	private static JMenuItem item(String name, Runnable action, KeyStroke hotkey) {
		JMenuItem item = new JMenuItem(new AbstractAction(name) {
			private static final long serialVersionUID = -2879611392094127231L;

			@Override
			public void actionPerformed(ActionEvent e) {
				action.run();
			}
		});
		if (hotkey != null) {
			item.setAccelerator(hotkey);
		}
		return item;
	}

	private static class FileMenu extends JMenu {
		private static final long serialVersionUID = 1902779469429226780L;

		public FileMenu() {
			super("File");

			add(item("Open", () -> {
				UIPanel.getInstance().getInputComponent().selectFile();
			}, KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_MASK)));

			add(item("Open All", () -> {
				JOptionPane.showMessageDialog(null, "Batching files is not implemented yet. Check back in soon!",
						Program.APPLICATION_NAME, JOptionPane.INFORMATION_MESSAGE);
			}, KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_MASK | KeyEvent.SHIFT_MASK)));

			add(item("Import", () -> {
				JOptionPane.showMessageDialog(null,
						"Importing application databases in not implemented yet. Check back in soon!",
						Program.APPLICATION_NAME, JOptionPane.INFORMATION_MESSAGE);
			}, KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_MASK)));
		}
	}

	private static class DataMenu extends JMenu {
		private static final long serialVersionUID = -4512710361648938486L;

		public DataMenu() {
			super("Data");

			add(item("Measurements", () -> {
				JDialog dialog = new JDialog(Main.FRAME, "Measurements");

				MeasurementPanel panel = new MeasurementPanel(dialog);
				panel.onExit().addObserver((source, none) -> {
					BufferedImage img = UIPanel.getInstance().getInputComponent().getImage();

					if (!UIPanel.getInstance().getImageSizeMicrometers().getText().isEmpty()) {
						UIPanel.getInstance().getImageSizeMicrometers()
								.setText((int) Measurement.PIXELS.convert(Measurement.MICROMETERS, img.getWidth()) + "x"
										+ (int) Measurement.PIXELS.convert(Measurement.MICROMETERS, img.getHeight())
										+ " μm");

						if (!UIPanel.getInstance().getOutputAverage().getText().isEmpty()) {
							UIPanel.getInstance().getDetectButton().getActionListeners()[0]
									.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, null));
						}
					}
				});

				dialog.getContentPane().add(panel);
				dialog.setVisible(true);
				dialog.pack();
			}, null));
		}
	}

	private static class HelpMenu extends JMenu {
		private static final long serialVersionUID = 9214071768398863415L;

		public HelpMenu() {
			super("Help");

			add(item("Manual", () -> {
				Popup.create("Manual", "TableOfContents");
			}, null));
			addSeparator();
			add(item("About", () -> {
				Popup.create("About", "About");
			}, null));
			add(item("Licensing", () -> {
				Popup.create("Licensing", "Licensing");
			}, null));
		}
	}
}
