package org.jointheleague.ir;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;

import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class Main {
	public static JFrame FRAME;

	public static void main() {
		Program.initialize();

		try {
			int width = 300;
			int height = 250;
			JDialog dialog = Popup.create(Program.APPLICATION_NAME, "Load");
			dialog.setUndecorated(true);
			dialog.setSize(width, height);
			dialog.addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent e) {
					System.exit(0);
				}
			});

			Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
			Point center = new Point(dim.width / 2 - width / 2, dim.height / 2 - height / 2);
			dialog.setLocation(center);
			dialog.setVisible(true);

			InputStream in = Program.class.getResourceAsStream("/img/warmup.jpg");
			byte[] buffer = new byte[in.available()];
			in.read(buffer);
			in.close();

			File tmp = new File(System.getProperty("java.io.tmpdir"), "tmp-warmup.jpg");
			if (tmp.exists()) {
				tmp.delete();
			}
			tmp.createNewFile();
			Files.write(tmp.toPath(), buffer);

			Detector detector = new Detector(tmp, 0.75, 250.0, 300.0);
			detector.initialize();
			detector.blur();
			detector.detect();

			dialog.removeWindowListener(dialog.getWindowListeners()[0]);
			dialog.getContentPane().removeAll();
			dialog.dispose();

			tmp.delete();
		} catch (Exception e) {
			Program.exit(e);
		}

		JFrame frame = new JFrame(Program.APPLICATION_NAME);
		frame.setJMenuBar(new Toolbar());
		frame.getContentPane().add(new MainPanel());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();

		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		Point center = new Point(dim.width / 2 - frame.getWidth() / 2, 100);
		frame.setLocation(center);

		frame.setVisible(true);

		FRAME = frame;

		if (Cache.get("ShowHelp").equals("true")) {
			JCheckBox box = new JCheckBox("Do not show this again");
			Object[] message = {
					"Welcome! For help with the application, navigate to Help -> Manual in the menu bar at the top.",
					box };
			JOptionPane.showMessageDialog(Main.FRAME, message, Program.APPLICATION_NAME,
					JOptionPane.INFORMATION_MESSAGE);
			if (box.isSelected()) {
				Cache.save("ShowHelp", "false");
			}
		}
	}
}
