package org.jointheleague.ir;

import java.io.IOException;

import org.apache.commons.csv.CSVFormat;

@FunctionalInterface
public interface CSVHandler {
	public void handle(String fileName, Appendable out, CSVFormat format) throws IOException;
}
