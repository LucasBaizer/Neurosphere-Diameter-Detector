package org.jointheleague.ir;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class Cache {
	private static File file;
	private static Map<String, String> cachedData = new HashMap<String, String>();

	public static void initialize(File file) throws IOException {
		Cache.file = file;

		String cacheData = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);

		String[] split = cacheData.split("\n");
		for (int i = 0; i < split.length - 1; i += 2) {
			String name = split[i];
			String data = split[i + 1];
			cachedData.put(name.substring(1, name.length() - 1), data);
		}

		ensure("ImageDirectory", System.getProperty("user.home"));
		ensure("SaveDirectory", System.getProperty("user.home"));
		ensure("BlurFactor", "1.0");
		ensure("MinimumSize", "100");
		ensure("MinimumDistance", "200");
		ensure("Delimiter", ",");
		ensure("pixels->micrometers", "100/55");
		ensure("micrometers->pixels", "55/100");
		ensure("ShowHelp", "true");
	}

	private static void ensure(String key, String value) {
		if (!cachedData.containsKey(key)) {
			cachedData.put(key, value);
		}
	}

	public static void save(String name, String data) {
		cachedData.put(name, data);
		save();
	}

	public static String get(String name) {
		return cachedData.get(name);
	}

	public static void save() {
		String out = "";
		for (Entry<String, String> entry : cachedData.entrySet()) {
			out += "[" + entry.getKey() + "]\n" + entry.getValue() + "\n";
		}
		out = out.trim();
		try {
			Files.write(file.toPath(), out.getBytes(StandardCharsets.UTF_8));
		} catch (IOException e) {
			Program.exit(e);
		}
	}
}
