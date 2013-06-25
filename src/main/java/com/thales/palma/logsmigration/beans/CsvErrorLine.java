package com.thales.palma.logsmigration.beans;

import com.thales.palma.processors.LogsProcessor;

public class CsvErrorLine {

	//private static final String CSV_SEPARATOR_STR = ";";

	/**
	 * The line Number in the CSV File
	 */
	private int lineNumber;
	
	/**
	 * The Identifier for the CSV Input Line
	 */
	private String objectId;
	
	/**
	 * The Erro Message for the file
	 */
	private String errorMsg;

	
	
	/**
	 * Constructor with needed attributes
	 * @param lineNumber
	 * @param objectId
	 * @param errorMsg
	 */
	public CsvErrorLine(int lineNumber, String objectId, String errorMsg) {
		
		this.lineNumber = lineNumber;
		this.objectId = objectId;
		this.errorMsg = errorMsg;
	}

	
	protected int getLineNumber() {
		return lineNumber;
	}

	protected String getObjectId() {
		return objectId;
	}

	protected String getErrorMsg() {
		return errorMsg;
	}


	@Override
	public String toString() {
		
		StringBuilder stBuild = new StringBuilder();
		
		stBuild.append(getLineNumber());
		stBuild.append(LogsProcessor.CSV_SEP_STRING);
		stBuild.append(getObjectId());
		stBuild.append(LogsProcessor.CSV_SEP_STRING);
		stBuild.append(getErrorMsg());
		stBuild.append(LogsProcessor.CSV_SEP_STRING);
		
		return stBuild.toString();
	}
	
	
	
	
	
}
