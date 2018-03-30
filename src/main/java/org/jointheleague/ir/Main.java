package org.jointheleague.ir;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;

import javax.swing.JDialog;
import javax.swing.JFrame;

public class Main {
	public static JFrame FRAME;

	public static void main() {
		Program.initialize();

		try {
			JDialog dialog = Popup.create(Program.APPLICATION_NAME, "Load");
			dialog.setSize(300, 300);
			dialog.addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent e) {
					System.exit(0);
				}
			});

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
			dialog.dispose();

			tmp.delete();
		} catch (Exception e) {
			Program.exit(e);
		}

		JFrame frame = new JFrame(Program.APPLICATION_NAME);
		frame.setJMenuBar(new Toolbar());
		frame.getContentPane().add(new UIPanel());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		frame.pack();

		FRAME = frame;
	}
}
