package com.thales.palma.processors;

public interface Context {

	
	public static final String PROCESSOR_CLASS_KEY = "logs_processor_class";
	public static final String CSV_INPUT_FILES_KEY = "input_csv_files";
	public static final String FAILURES_FILE_KEY = "failures_csv_file";
	public static final String OUTPUT_CONSOLIDATE_KEY = "output_consolidate";
	public static final String JVM_ID_KEY = "jvm_id";
	public static final String INPUT_KEY = "input";
	public static final String FAILED_KEY = "failed";
	public static final String WT_LOGS_KEY = "wtLogs";
	public static final String OUTPUT_KEY = "output";
	public static final String LOAD_ACTION_KEY = "load_action_key";
	public static final String LOG_ERROR_FILE_KEY = "log_error_file_key";
	public static final String CSV_ACTION_COLS_KEY = "csv_action_cols_key";
	public static final String CSV_COLS_SEP_KEY = "csv_cols_separator_key";
	
	

	
	/**
	 * Get a value that has been set into the context with the given key
	 * @param key a key
	 * @return a value
	 */
	public Object get(String key);
	
	/**
	 * Set a value into the context with the given key
	 * @param key a key
	 * @param value a value
	 */
	public void set(String key, Object value);
}
