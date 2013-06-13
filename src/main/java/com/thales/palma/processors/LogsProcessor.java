package com.thales.palma.processors;


import org.apache.commons.configuration.Configuration;

import com.thales.palma.logsmigration.exceptions.LogsMigrationException;


public interface LogsProcessor {
	
	public static final String CSV_SEP_STRING = ";";
	
	public static final String CSV_SEP_STRING_2 = "~~";
	
	/**
	 * CVS Separator
	 */
	public static String CSV_MAP_FILE_SEPARATOR = "~";
	
	/**
	 * Separator on CSV Error Logs
	 */
	public static final String SEP_LINE_ID = "|";
	
	/**
	 * NO (Negative) value Option in Command Line
	 */
	public static final String NO_VALUE = "no";
	
	/**
	 * YES (Positive) value Option in Command Line
	 */
	public static final String YES_VALUE = "yes";
	
	
	/**
	 * GEN Token identifier
	 */
	public static final  String GEN_TOKEN = "gen";
	
	/**
	 * KO Token identifier
	 */
	public static final  String KO_TOKEN = "ko";
	
	/**
	 * OK Token identifier
	 */
	public static final String OK_TOKEN = "ok";
	
	/**
	 * File TOKEN Separator
	 */
	public static String F_SEPARATOR = "_";
	
	/**
	 * Initializes the implementation class with Objects containing 
	 * all the needed information for processing (logs and input csv)files
	 * @param config
	 * @param context
	 */
	public void initialize(Configuration config, Context context) throws LogsMigrationException;
		
	/**
	 * Process all the files
	 */
	public void processFiles();
	
	/**
	 * Container with main informations to process
	 * @return
	 */
	public Context getContext();

}
