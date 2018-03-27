package org.jointheleague.ir;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JOptionPane;

public class Program {
	public static final String APPLICATION_NAME = "Neurosphere Detector";
	public static final String APPLICATION_INTERNAL = "NeurosphereDetector";

	private static File dataFolder;
	private static File cacheFile;

	public static void initialize() {
		dataFolder = new File(System.getenv("AppData"), APPLICATION_INTERNAL);
		cacheFile = new File(dataFolder, ".cache");

		if (!dataFolder.exists()) {
			dataFolder.mkdir();
		}

		if (!cacheFile.exists()) {
			try {
				cacheFile.createNewFile();
			} catch (IOException e) {
				exit(e);
			}
		}

		try {
			Cache.initialize(cacheFile);
		} catch (IOException e) {
			exit(e);
		}
	}

	public static void exit(Throwable ex) {
		ex.printStackTrace();

		JOptionPane.showMessageDialog(null, "Oh no! The application crashed. Please report this to the developer.",
				APPLICATION_NAME, JOptionPane.ERROR_MESSAGE);

		File crashReport = new File(dataFolder,
				"Crash-" + new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date()) + ".txt");
		try {
			crashReport.createNewFile();
		} catch (IOException e1) {
			return;
		}

		PrintStream out = null;
		try {
			out = new PrintStream(crashReport);
		} catch (FileNotFoundException e) {
			return;
		}
		ex.printStackTrace(out);
		out.close();

		System.exit(1);
	}
}
