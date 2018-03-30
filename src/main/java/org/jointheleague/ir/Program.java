package org.jointheleague.ir;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JOptionPane;

public class Program {
	public static final String APPLICATION_NAME = "Neurosphere Detector";
	public static final String APPLICATION_INTERNAL = "NeurosphereDetector";

	private static File dataFolder;
	private static File cacheFile;

	public static File getDataFolder() {
		return dataFolder;
	}

	public static File getAsset(String asset) {
		return new File(dataFolder.getAbsolutePath() + File.separator + asset);
	}

	public static void initialize() {
		try {
			dataFolder = new File(System.getenv("AppData"), APPLICATION_INTERNAL);
			cacheFile = new File(dataFolder, ".cache");

			if (!dataFolder.exists()) {
				dataFolder.mkdir();
			}

			File web = new File(dataFolder, "web");
			if (!web.exists()) {
				web.mkdir();
			}

			File webResources = new File(web, "resources");
			if (!webResources.exists()) {
				webResources.mkdir();
			}

			File webContent = new File(web, "content");
			if (!webContent.exists()) {
				webContent.mkdir();
			}

			copyWebContent("Load.html");

			download("web/resources/bootstrap.min.css",
					"http://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/css/bootstrap.min.css");

			// in the future, this will happen from the web
			InputStream in = Program.class.getResourceAsStream("/web/content/WebContent.txt");
			byte[] buffer = new byte[in.available()];
			in.read(buffer);
			in.close();

			Map<String, String> builtinMap = webContentToMap(new String(buffer, StandardCharsets.UTF_8));

			File currentWebContent = getAsset("web/content/WebContent.txt");
			if (currentWebContent.exists()) {
				Map<String, String> currentMap = webContentToMap(
						new String(Files.readAllBytes(currentWebContent.toPath()), StandardCharsets.UTF_8));

				for (Map.Entry<String, String> builtinEntry : builtinMap.entrySet()) {
					if (!currentMap.containsKey(builtinEntry.getKey())) {
						copyWebContent(builtinEntry.getKey() + ".md");
					} else {
						String currentVersion = currentMap.get(builtinEntry.getKey());
						if (currentVersion.compareTo(builtinEntry.getValue()) < 0) {
							copyWebContent(builtinEntry.getKey() + ".md");
						}
					}
				}

				Files.write(currentWebContent.toPath(), buffer);
			} else {
				currentWebContent.createNewFile();
				Files.write(currentWebContent.toPath(), buffer);

				for (String key : builtinMap.keySet()) {
					copyWebContent(key + ".md");
				}
			}

			if (!cacheFile.exists()) {
				cacheFile.createNewFile();
			}

			Cache.initialize(cacheFile);
		} catch (IOException | URISyntaxException e) {
			exit(e);
		}
	}

	private static Map<String, String> webContentToMap(String webContent) {
		Map<String, String> map = new HashMap<>();

		String[] split = webContent.split("\n");
		for (String line : split) {
			String[] parts = line.split(":");
			map.put(parts[0], parts[1]);
		}

		return map;
	}

	private static void copyWebContent(String name) throws IOException {
		File targetFile = getAsset("web/content/" + name);

		InputStream in = Program.class.getResourceAsStream("/web/content/" + name);
		byte[] buffer = new byte[in.available()];
		in.read(buffer);
		in.close();

		OutputStream out = new FileOutputStream(targetFile);
		out.write(buffer);
		out.close();
	}

	private static void download(String asset, String url) throws IOException, URISyntaxException {
		File a = getAsset(asset);
		if (!a.exists()) {
			a.createNewFile();

			ByteArrayOutputStream out = new ByteArrayOutputStream();

			try {
				byte[] buffer = new byte[4096];
				InputStream stream = new URL(url).openStream();

				int read;
				while ((read = stream.read(buffer)) > 0) {
					out.write(buffer, 0, read);
				}

			} catch (IOException e) {
				exit(e);
			}

			Files.write(a.toPath(), out.toByteArray());
		}
	}

	public static void exit(Throwable ex) {
		ex.printStackTrace();

		JOptionPane.showMessageDialog(Main.FRAME,
				"Oh no! The application crashed. Please report this to the developer.", APPLICATION_NAME,
				JOptionPane.ERROR_MESSAGE);

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
