package com.thales.palma.logsmigration.beans;

import java.util.ArrayList;
import java.util.List;

public class CsvErrorFile {

	private String fileName;
	
	private List<CsvErrorLine> csvLines;

	
	/**
	 * 
	 * @param fileName
	 * @param csvLines
	 */
	public CsvErrorFile(String fileName, List<CsvErrorLine> csvLines) {
		super();
		this.fileName = fileName;
		this.csvLines = csvLines;
	}

	
	/**
	 * 
	 * @param fileName
	 */
	public CsvErrorFile(String fileName) {
		
		/* Call constructor with initalized List of CsvErrorLine elements */
		this(fileName, new ArrayList<CsvErrorLine>());
	}





	public String getFileName() {
		return fileName;
	}

	public List<CsvErrorLine> getCsvLines() {
		return csvLines;
	}


	
	
	
}
