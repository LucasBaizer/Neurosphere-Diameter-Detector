package org.jointheleague.ir;

import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// not a javadoc because eclipse auto-format :(
/*
 * Database format:
 * 
 * [int] database name length
 * [string] database name
 * [int] amount of files
 * [list] file names:
 *   [int] file name length
 *   [string] file name
 *   [int] amount of detections
 *   [list] detections:
 *     [int] x
 *     [int] y
 *     [int] radius
 */
public class DetectionDatabase {
	public static final String FILE_EXTENSION = "dsd";
	public static final List<DetectionDatabase> CURRENT = new ArrayList<>();

	private String name;
	private File databaseFile;
	private Map<File, DetectionList> storage = new HashMap<>();

	public DetectionDatabase(String name, File databaseFile) {
		this.name = name;
		this.databaseFile = databaseFile;
	}

	public File getDatabaseFile() {
		return databaseFile;
	}

	public String getName() {
		return name;
	}

	public void insert(File input, DetectionList list) {
		storage.put(input, list);
	}

	public void remove(File input) {
		storage.remove(input);
	}

	public void save() throws IOException {
		int size = 8; // database name length + amount of files
		size += name.length(); // database name bytes
		for (File file : storage.keySet()) {
			size += 4; // file name length
			size += file.getAbsolutePath().length(); // file name bytes

			DetectionList list = storage.get(file);
			size += 4; // amount of detections
			for (int i = 0; i < list.size(); i++) {
				size += 12; // x + y + radius
			}
		}

		ByteBuffer buf = ByteBuffer.allocate(size);
		buf.putInt(name.length());
		for (char c : name.toCharArray()) {
			buf.put((byte) c);
		}
		for (File file : storage.keySet()) {
			buf.putInt(file.getName().length());
			for (char c : file.getAbsolutePath().toCharArray()) {
				buf.put((byte) c);
			}

			DetectionList list = storage.get(file);
			buf.putInt(list.size());
			for (Detection detection : list) {
				buf.putInt(detection.getCenter().x);
				buf.putInt(detection.getCenter().y);
				buf.putInt(detection.getRadius());
			}
		}

		Files.write(databaseFile.toPath(), buf.array());
	}

	public static DetectionDatabase read(File file) throws IOException {
		DetectionDatabase database = new DetectionDatabase(null, file);

		byte[] bytes = Files.readAllBytes(file.toPath());
		ByteBuffer buf = ByteBuffer.wrap(bytes);

		int nameLength = buf.getInt();
		String name = getString(buf, nameLength);
		database.name = name;

		int files = buf.getInt();
		for (int i = 0; i < files; i++) {
			int strlen = buf.getInt();
			String fileName = getString(buf, strlen);
			File sub = new File(fileName);

			int detectCount = buf.getInt();
			DetectionList list = new DetectionList(detectCount);
			for (int j = 0; j < detectCount; j++) {
				int x = buf.getInt();
				int y = buf.getInt();
				int radius = buf.getInt();

				Detection detection = new Detection(new Point(x, y), radius);
				list.add(detection);
			}

			database.insert(sub, list);
		}

		return database;
	}

	private static String getString(ByteBuffer buf, int len) {
		String str = "";
		for (int i = 0; i < len; i++) {
			str += buf.get();
		}
		return str;
	}
}
