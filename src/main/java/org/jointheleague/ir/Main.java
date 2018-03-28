package org.jointheleague.ir;

import javax.swing.JFrame;
import javax.swing.UIManager;

public class Main {
	public static JFrame FRAME;

	public static void main(String[] args) {
		Program.initialize();

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			Program.exit(e);
			return;
		}

		JFrame frame = new JFrame(Program.APPLICATION_NAME);
		frame.setJMenuBar(new Toolbar());
		frame.add(new UIPanel());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		frame.pack();

		FRAME = frame;
	}
}
