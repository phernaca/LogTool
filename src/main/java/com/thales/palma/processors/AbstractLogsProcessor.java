package com.thales.palma.processors;

import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOCase;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.JVMRandom;

import au.com.bytecode.opencsv.CSVWriter;

import com.thales.palma.logsmigration.beans.CsvErrorFile;
import com.thales.palma.logsmigration.beans.CsvErrorLine;
import com.thales.palma.logsmigration.exceptions.LogsMigrationException;



public abstract class AbstractLogsProcessor implements LogsProcessor {

	

	protected java.util.logging.Logger actionLoggerGeneric;
	
	public static String F_SEPARATOR = "_";
	
	
	
    /* Declare Configuration objects */
    protected Context context;
    protected Configuration config;
    
    protected String csvSeparator;
	
    protected String csvLoadKey;
    protected Collection<File> csvInputFiles;
    protected File csvFailureFile;
    
    protected String outputCsvFailFileName;
	
    protected boolean consolidateOutputFile = false;
    
	public Context getContext() {
		
		return context;
	}

	public void initialize(Configuration config, Context context) throws LogsMigrationException {
		
		/* And set the parameters for the current load */
		this.context = context;
		this.config = config;

		
		this.csvLoadKey = (String)context.get(Context.LOAD_ACTION_KEY);
		this.csvFailureFile = (File)context.get(Context.FAILURES_FILE_KEY);
		this.csvInputFiles = (Collection<File>)context.get(Context.CSV_INPUT_FILES_KEY);
		this.csvSeparator = (String)context.get(Context.CSV_COLS_SEP_KEY);
		
		/* Set positive boolean value according to the Context   */
		if(LogsProcessor.YES_VALUE.equals((String)context.get(Context.OUTPUT_CONSOLIDATE_KEY))) {
			this.consolidateOutputFile = true;
		}
		
		outputCsvFailFileName = this.config.getString("logs_migration." + getCsvLoadKey() + ".fail_file_name", "objsFailed");
		
		/* And initialize the logger */
		actionLoggerGeneric = java.util.logging.Logger.getLogger(this.csvLoadKey);
		
		
		initActionLoggers();
	}

	
	/**
	 * 
	 */
	public void processFiles() {

		/* Proceed to sort the failure file with the TAS csvmapfile order */
		
		try {
			
			String failDir = (String)getContext().get(Context.FAILED_KEY);
			String oriFailureName = getCsvFailureFile().getName();
			String tasFailureName = StringUtils.replace(oriFailureName, ".csv", "TAS.csv");
			
			/* First of all check that File with Load errors for the current action is there
			 * Otherwise all the lines for targeted files have been successfully loaded */
			String errActionLogFile =(String)context.get(Context.LOG_ERROR_FILE_KEY);
			String logsDirPath = (String)context.get(Context.WT_LOGS_KEY);
			File actionErrorsLogFile = obtainActionErrorFile(errActionLogFile, logsDirPath);
			
			
			
			/* Proceed only if the Errors File for the current action is there */
			if(actionErrorsLogFile != null) {
				
				actionLoggerGeneric.info("Line Errors descriptions at File : " + actionErrorsLogFile.getName());
				
				/* Init Map where to put Csv Files and their Line Errors */
				Map<String,CsvErrorFile> csvErrorsFiles = new HashMap<String,CsvErrorFile>();
				
				/* And the list of lines formatted to TAS */
				List<String> failedFormattedLines = new ArrayList<String>();
				
				findErrorLinesInCsvFiles(actionErrorsLogFile,csvErrorsFiles,failedFormattedLines);
				
				
				
				/* 1st Goal : Proceed to write the Failures in a CSV format */
				String csvJvmOutputFileName = getContext().get(Context.OUTPUT_KEY) + File.separator 
												+ outputCsvFailFileName
												+ "_" + (String)context.get(Context.JVM_ID_KEY)+".csv";
				
				File csvJvmOutputFile = obtainCsvJvmOutputFile(csvErrorsFiles, csvJvmOutputFileName);
				/* Consolidate in one file if requested */
				if(isConsolidateOutputFile()) {
					consolidateCsvFile(csvJvmOutputFile);
				}
				
				
				
				/* 2nd Goal : Put an output CSV file into the TASB csvmapfile format */
				if(CollectionUtils.isNotEmpty(failedFormattedLines)) {
					
					/* Generate the output file with the expected TAS format */
					File newTASFile = new File(failDir + File.separator + tasFailureName);
					FileUtils.touch(newTASFile);
					FileUtils.writeLines(newTASFile, failedFormattedLines);
				
				}
				
			} else {
				
				actionLoggerGeneric.info("No errors found loading action : " + (String)context.get(Context.LOAD_ACTION_KEY) + "for JVM : " + (String)context.get(Context.JVM_ID_KEY) + actionErrorsLogFile.getName());
			}
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
				
				
	}

	/**
	 * @param csvJvmOutputFile
	 * @throws IOException
	 */
	protected void consolidateCsvFile(File csvJvmOutputFile) throws IOException {
		if( csvJvmOutputFile!=null && csvJvmOutputFile.length()>0L ) {
			
			String csvOutConsolidateFileName = getContext().get(Context.OUTPUT_KEY) + File.separator 
										+ outputCsvFailFileName
										+ "_consolidate.csv";
			
			File csvConsolidateFile = new File(csvOutConsolidateFileName);
			FileUtils.writeLines(csvConsolidateFile, FileUtils.readLines(csvJvmOutputFile),true);
			
		}
	}

	/**
	 * 
	 * @param csvErrorsFiles
	 * @param csvJvmOutputFileName
	 * @return
	 * @throws IOException
	 */
	protected File obtainCsvJvmOutputFile(
			Map<String, CsvErrorFile> csvErrorsFiles,
			String csvJvmOutputFileName) throws IOException {
		
		File csvJvmOutputFile = new File(csvJvmOutputFileName);
		
		CSVWriter failsWriter = new CSVWriter(
				new FileWriter(csvJvmOutputFile), ';' );
		
		/* Browse all the CSV Files */ 
		List<String> csvInputFiles = new ArrayList<String>(csvErrorsFiles.keySet());
		Collections.sort(csvInputFiles);
		for(String csvInputFileKey : csvInputFiles) {
			
			CsvErrorFile csvErrFile = csvErrorsFiles.get(csvInputFileKey);
			
			List<CsvErrorLine> csvErrLines = csvErrFile.getCsvLines();
			for(CsvErrorLine csvErrLine : csvErrLines) {
				
				String[] entries = (csvErrFile.getFileName() + CSV_SEP_STRING + csvErrLine.toString()).split(CSV_SEP_STRING);
				failsWriter.writeNext(entries);
			}
		}
		failsWriter.close();
		
		return csvJvmOutputFile;
	}

	/**
	 * @return
	 * @throws IOException
	 */
	protected void findErrorLinesInCsvFiles(File actionErrorsLogFile,
													Map<String,CsvErrorFile> csvErrorsFiles,
													List<String> failedFormattedLines
														) throws IOException {
		
		
		/* Obtain the TAS used col names in an array */
		String[] tasCsvColNames = StringUtils.split((String)context.get(Context.CSV_ACTION_COLS_KEY), CSV_MAP_FILE_SEPARATOR);
		
		/* Clone and Sort the CSV column names */
		String[] alphaSortedCsvColNames = tasCsvColNames.clone();
		Arrays.sort(alphaSortedCsvColNames);
		
		/* Obtain Line Iterator for the Error Line Descriptions File */
		LineIterator logErrFileIter = IOUtils.lineIterator(new FileReader(actionErrorsLogFile));
		
		/* Obtain the lines from the Failed CSV file */
		List<String> failedLines = FileUtils.readLines(getCsvFailureFile());
		
		for(String failedLine : failedLines) {
			
			/* Proceed with one failed line */
			processFailedLine(csvErrorsFiles, tasCsvColNames,
					alphaSortedCsvColNames, logErrFileIter,
					failedFormattedLines, failedLine);
		}
		
		
		
	}

	/**
	 * @param csvErrorsFiles
	 * @param tasCsvColNames
	 * @param alphaSortedCsvColNames
	 * @param logErrFileIter
	 * @param failedFormattedLines
	 * @param failedLine
	 * @throws IOException
	 */
	protected void processFailedLine(Map<String, CsvErrorFile> csvErrorsFiles,
			String[] tasCsvColNames, String[] alphaSortedCsvColNames,
			LineIterator logErrFileIter, List<String> failedFormattedLines,
			String failedLine) throws IOException {
		actionLoggerGeneric.info("Looking for failed : " + failedLine);
		
		/* First obtain the map with cols mapping the TAS csv format */
		Map<String,String> tmpFailedMapLine = obtainSpecifiedLineMap(alphaSortedCsvColNames, failedLine);
		
		 
		/* Proceed obtaining the input csv line and the matching error description */
		boolean foundLine = findLineOnCsv(tasCsvColNames, tmpFailedMapLine, csvErrorsFiles, logErrFileIter);
		
		
		/* Obtain the failed formatted into the TAS one */
		String reformatFailedLine = obtainTASFailedLine(tasCsvColNames, tmpFailedMapLine);
		if(StringUtils.isNotBlank(reformatFailedLine)) {
			failedFormattedLines.add(reformatFailedLine);
		} else {
			
			//TODO Log It!
		}
	}

	/**
	 * 
	 * @param errActionLogFile
	 * @param logsDirPath
	 * @return
	 */
	private File obtainActionErrorFile(String errActionLogFile, String logsDirPath) {
		
		File lastLogErrorFile = null;
		
		FileFilter fileFilter = new WildcardFileFilter(errActionLogFile, IOCase.INSENSITIVE);
		
		File logsDir = new File(logsDirPath);
		File[] failedLogFiles = logsDir.listFiles(fileFilter);
		Arrays.sort(failedLogFiles, LastModifiedFileComparator.LASTMODIFIED_REVERSE);
		
		if(failedLogFiles.length>0) {
						
			lastLogErrorFile = failedLogFiles[0];
		}
		
		return lastLogErrorFile;
	}

	/**
	 * @param tasCsvColNames
	 * @param tmpFailedMapLine
	 * @param foundLine
	 * @throws IOException
	 */
	protected boolean findLineOnCsv(String[] tasCsvColNames,
			Map<String, String> tmpFailedMapLine,
			Map<String,CsvErrorFile> csvErrorFiles,
			LineIterator logFileIter)
			throws IOException {
		
		boolean foundLine = false;
		
		Iterator<File> csvInIter = getCsvInputFiles().iterator();
		while(!foundLine &&  csvInIter.hasNext()) {
			
			File csvInFile = csvInIter.next();
			/* Obtain all the Input lines from a CSV and search for the failed one */
			List<String> inputLines = FileUtils.readLines(csvInFile);
			
			int lineIdx = 0;
			Iterator<String> inputLinesIter = inputLines.iterator();
			while(!foundLine &&  inputLinesIter.hasNext()) {
				
				String inputLine = inputLinesIter.next();
				lineIdx++;
				
				try {
					
					if(!StringUtils.startsWith(inputLine, "#")
							|| StringUtils.containsNone(inputLine, getCsvLoadKey())) {
						actionLoggerGeneric.fine("Looking at File : " + csvInFile.getName() + " and Line #" + lineIdx);
						Map<String,String> tmpInputMapLine = obtainSpecifiedLineMap(tasCsvColNames, inputLine);
						
						boolean sameLine = true;
						List<String> keys = new ArrayList<String>(tmpFailedMapLine.keySet());
						Collections.sort(keys);
						Iterator<String> keysIter = keys.iterator();
						while(sameLine &&  keysIter.hasNext()) {
							String theKey = keysIter.next();
							String stInputValue = tmpInputMapLine.get(theKey);
							String stFailedValue = tmpFailedMapLine.get(theKey);
							
							if(!StringUtils.equals(stInputValue, stFailedValue)) {
								sameLine = false;
							}
						}
						
						if(sameLine) {
							
							foundLine = true;
							actionLoggerGeneric.info("Found at File : " + csvInFile.getName() + " and Line #" + lineIdx);
							
							String objectId = obtainObjectLineId(tmpFailedMapLine);
							actionLoggerGeneric.info("Line Object ID : " + objectId);
							
							/* proceed to update the Map that contains all the informations about the errors in the load */
							updateCsvErrorFiles(csvErrorFiles, logFileIter,
									csvInFile, lineIdx, objectId);
							
						}
						
						
					}
					
				} catch (Exception le) {
					actionLoggerGeneric.warning("Problem in input file : " + csvInFile.getName());
					actionLoggerGeneric.warning("Problem comparing with line : " + inputLine);
				}
			}
			
		}
		
		return foundLine;
	}

	/**
	 * @param csvErrorFiles
	 * @param logFileIter
	 * @param csvInFile
	 * @param lineIdx
	 * @param objectId
	 * @throws IOException
	 */
	protected void updateCsvErrorFiles(Map<String, CsvErrorFile> csvErrorFiles,
			LineIterator logFileIter, File csvInFile, int lineIdx,
			String objectId) throws IOException {
		/* Now try to find the error description for the failed input CSV line */
		boolean errDescFound = false;
		while (!errDescFound && logFileIter.hasNext()) {
			 
			String line = logFileIter.nextLine();
			if(containsObjectLineId(line, objectId)) {
				
				errDescFound = true;
				/* obtain lines containing the description */
				String descErrLogLine = obtainErrorLogDescription(logFileIter, objectId);
				
				CsvErrorFile csvError = null;
				if(csvErrorFiles.containsKey(csvInFile.getName())) {
					csvError = csvErrorFiles.get(csvInFile.getName());
				}
				else {
					csvError = new CsvErrorFile(csvInFile.getName());
				}
				
				/* Update the Map with the CsvErrorFile */
				List<CsvErrorLine> errorLines = csvError.getCsvLines();
				CsvErrorLine errLine = new CsvErrorLine(lineIdx,objectId,descErrLogLine);
				errorLines.add(errLine);
				
				csvErrorFiles.put(csvInFile.getName(), csvError);
			}
			 
		}
	}

	/**
	 * Default  Error Description
	 * @param logFileIter
	 * @param objectId defined by each specific sub processor
	 * @return
	 */
	protected String obtainErrorLogDescription(LineIterator logFileIter, String objectId) {
		String descErrLogLine = logFileIter.nextLine();
		
		actionLoggerGeneric.info("Error Description : " + descErrLogLine);
		return descErrLogLine;
	}

	
	/**
	 * Returns CSV Line with the PALMA TAS Format
	 * @param tasCsvColNames
	 * @param tmpCsvMapLine
	 * @return
	 */
	private String obtainTASFailedLine(String[] tasCsvColNames,
			Map<String, String> tmpCsvMapLine) {
		
		StringBuffer buffer = new StringBuffer();
		buffer.append(getCsvLoadKey());
		for(int i=0; i<tasCsvColNames.length; i++) {
			
			buffer.append(getCsvSeparator());
			buffer.append(tmpCsvMapLine.get(tasCsvColNames[i]));
		}
		
		return buffer.toString();
	}

	/**
	 * @param sortedColNames
	 * @param failedLine
	 * @param tmpCsvMapLine
	 */
	private Map<String,String> obtainSpecifiedLineMap(String[] sortedColNames, String failedLine) {
		
		Map<String,String> tmpCsvMapLine = new HashMap<String,String>();
		
		String[] csvValues = StringUtils.splitPreserveAllTokens(failedLine, getCsvSeparator());
		
		// Treate only if load key is the same as context load action key
		if (context.get(context.LOAD_ACTION_KEY).equals(csvValues[0])) {
			/* Check if number of columns is the same*/
			if(sortedColNames.length+1 !=  csvValues.length) {
				actionLoggerGeneric.warning("Col Names " + sortedColNames.length + " vs Col Values " + csvValues.length);
			}
			//else {
				//split(failedLine, getCsvSeparator());
				for(int i=1; i<csvValues.length; i++) {

					tmpCsvMapLine.put(sortedColNames[i-1], csvValues[i]);
				}
			//}
		}
		
		return tmpCsvMapLine;
	}

	
	/**
	 * 
	 * @param logFileName
	 * @param logDateFormat
	 * @return
	 * @throws SecurityException
	 * @throws IOException
	 */
    protected FileHandler getLogFileHandler(String logFileName, final String logDateFormat) throws SecurityException, IOException {

    	FileHandler fhandler;

    	fhandler = new java.util.logging.FileHandler(
		    								getContext().get(Context.OUTPUT_KEY) + File.separator + logFileName, 
		    								true);
    	fhandler.setFormatter(new Formatter() {
    	    public String format(LogRecord record) {
    		DateFormat dateFormat = new SimpleDateFormat(logDateFormat);
    		Calendar cal = Calendar.getInstance();

    		return dateFormat.format(cal.getTime()) + "  :  " + record.getMessage() + "\n";
    	    }
    	});

    	return fhandler;

    }
	
	
    /**
     * Initialize the Loggers for the current File Processor.
     * 
     * @throws FileMigrationException
     */
    protected void initActionLoggers() throws LogsMigrationException {

	try {

	    /*
	     * Proceed initialize all Folder Loggers (OK, KO and Generic). Assign the right handlers to them
	     */
	    String dateFormatGen = this.config.getString("logs_migration." + getCsvLoadKey() + ".logactiondateformat.gen", "yyyy-MM-dd HH:mm:ss");
	 //   String dateFormatKO = this.config.getString("file_migration." + processorKey + ".logfolderdateformat.ko", "yyyy-MM-dd HH:mm:ss");

/*	    String fileOKName = fileFolder.getParentFile().getName() + F_SEPARATOR + fileFolder.getName() + F_SEPARATOR + OK_TOKEN + ".log";
	    folderLoggerOK.addHandler(getLogFileHandler(fileOKName, dateFormatOK));

	    String fileKOName = fileFolder.getParentFile().getName() + F_SEPARATOR + fileFolder.getName() + F_SEPARATOR + KO_TOKEN + ".log";
	    folderLoggerKO.addHandler(getLogFileHandler(fileKOName, dateFormatKO)); */

	    String fileGenName = getCsvLoadKey() + F_SEPARATOR + System.currentTimeMillis() + ".log";
	    actionLoggerGeneric.addHandler(getLogFileHandler(fileGenName, dateFormatGen));

	} catch (Exception e) {
	    e.printStackTrace();
	    throw new LogsMigrationException("Problem setting the Logs Loggers: ", e);
	}

    }
    
    
    /**
     * 
     * @param tmpCsvMapLine
     * @return
     */
    protected abstract String obtainObjectLineId(Map<String, String>tmpCsvMapLine);
    
    /**
     * 
     * @param currentLine
     * @param objectId
     * @return
     */
    protected abstract boolean containsObjectLineId(String currentLine, String objectId);
    
	protected String getCsvLoadKey() {
		return csvLoadKey;
	}

	protected Collection<File> getCsvInputFiles() {
		return csvInputFiles;
	}

	protected File getCsvFailureFile() {
		return csvFailureFile;
	}

	protected String getCsvSeparator() {
		return csvSeparator;
	}

	public boolean isConsolidateOutputFile() {
		return consolidateOutputFile;
	}

	
	
}
