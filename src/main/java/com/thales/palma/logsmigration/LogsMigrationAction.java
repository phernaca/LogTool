package com.thales.palma.logsmigration;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.TreeMap;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationFactory;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOCase;
import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.PropertyConfigurator;

import com.thales.palma.logsmigration.exceptions.LogsMigrationException;
import com.thales.palma.processors.Context;
import com.thales.palma.processors.ContextBase;
import com.thales.palma.processors.LogsProcessor;


public class LogsMigrationAction {

	
	private static final String PALMA_LOGS_PATH = "/logs";

	private static final String FAILURES_CSV = ".failures.csv";

	public static final String PALMA_FAILURES_PATH = "/loadFiles/ext/thales/palma/failures";

	public final static Logger fileMigrLog = java.util.logging.Logger.getLogger(LogsMigrationAction.class.getName());
	
	public final static String LOG_KO_FILENAME = "files_ko.log";
	public final static String LOG_OK_FILENAME = "files_ok.log";
	public final static String LOG_GEN_FILENAME = "files_migration.log";
	
	
	Configuration configuration;
	Context context;
	List<TreeMap<File,String>> allFileFolders;
	File fileWorkspaceDir;

	
	
	/**
	 * Default constructor
	 */
	public LogsMigrationAction() {

		
	}


	public Configuration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}

	

	/*
	 * Take into account all generated Log Files from every processed folder and merge them into one file.
	 * It will be one file for each type of Log document (KO, OK and Generic one).
	 * 
	 * */	
//	private boolean processLogFiles( String outputLogDir ) throws FileMigrationException {	
//		
//		boolean allLogFiles = false;
//		
//		/* Define all the LOG Files (KO, OK and Generic) */
//		File logKO = new File(outputLogDir + File.separator + LOG_KO_FILENAME);
//		File logOK = new File(outputLogDir + File.separator + LOG_OK_FILENAME);
//		File logGen= new File(outputLogDir + File.separator + LOG_GEN_FILENAME);
//		
//		try {
//			
//			/* Proceed to create all resulting Log files in the Disk */
//			FileUtils.touch(logKO);
//			FileUtils.touch(logOK);
//			FileUtils.touch(logGen);			
//			
//			List<String> koFileLines = new ArrayList<String>();
//			List<String> okFileLines = new ArrayList<String>();
//			List<String> genFileLines = new ArrayList<String>();
//			
//			/* Then proceed to deal with every folder (and its log files) */
//			List<TreeMap<File,String>> allLogFolders =  new ArrayList<TreeMap<File,String>>();
//				//obtainAllFileFolders(this.fileWorkspaceDir.getCanonicalPath());						
//			for (TreeMap<File,String> aFolderMap : allLogFolders)
//			{
//				File aFolder = aFolderMap.firstKey();
//				String fileType = aFolderMap.get(aFolder);
//				
//				try
//				{
//					
//					if(fileMigrLog.isLoggable(Level.FINE)) {
//						fileMigrLog.fine("Dealing with Log Folder: " + aFolder.getCanonicalPath());
//					}						
//					
//					/* Obtain the File object of every type (LOG, KO, GEN) */
//					File koFile = new File (aFolder.getCanonicalPath() + File.separator + fileType.toUpperCase() 
//												+ AbstractFileProcessor.F_SEPARATOR + aFolder.getName() 
//												+ AbstractFileProcessor.F_SEPARATOR + AbstractFileProcessor.KO_TOKEN + ".log");					
//					
//					File okFile = new File (aFolder.getCanonicalPath() + File.separator + fileType.toUpperCase() 
//												+ AbstractFileProcessor.F_SEPARATOR + aFolder.getName() 
//												+ AbstractFileProcessor.F_SEPARATOR + AbstractFileProcessor.OK_TOKEN + ".log");										
//					
//					File genFile = new File (aFolder.getCanonicalPath() + File.separator + fileType.toUpperCase() 
//												+ AbstractFileProcessor.F_SEPARATOR + aFolder.getName() 
//												+ AbstractFileProcessor.F_SEPARATOR + AbstractFileProcessor.GEN_TOKEN + ".log");
//					
//					
//					/* Proceed to insert the contents on each file */					
//					/* Take KO Log files */
//					if(koFile.isFile()) {
//						koFileLines.addAll(FileUtils.readLines(koFile));
//					}
//					
//					/* Take OK Log files */					
//					if(okFile.isFile()) {
//						okFileLines.addAll(FileUtils.readLines(okFile));						
//					}					
//										
//					/* Take Generic Log files */					
//					if(genFile.isFile()) {
//						genFileLines.addAll(FileUtils.readLines(genFile));
//					}						
//									
//					
//				} catch (IOException ioe) {
//					
//					/* Trace that there was a problem when dealing with one Folder Logs */
//					fileMigrLog.warning("Problem getting logs from folder: " + aFolder.getCanonicalPath() + ". Exception: " + ioe.getMessage());
//				}
//				
//			} /* end for */
//			
//			/* Finally proceed to write all the contents of the different Log files in one for each type */
//			FileUtils.writeLines(logKO, koFileLines);
//			FileUtils.writeLines(logOK, okFileLines);
//			FileUtils.writeLines(logGen, genFileLines);
//									
//		} catch (IOException ie) {
//			
//			throw new FileMigrationException("Problem when processing the Folder Log Files: " + ie.getMessage());
//		}
//			
//		
//		return allLogFiles;
//	}	
	
	
	/**
	 * Setup LogsMigrationAction Logger (use java.util.logging.Logger)
	 * @throws IOException 
	 * @throws SecurityException 
	 */
	public void setLogsMigrationLogger(String outputLogDir) throws SecurityException, IOException {
				
		FileHandler fhandler = new FileHandler(outputLogDir + File.separator 
				+ getConfiguration().getString("logs_migration.action.logging.filename", "LogsImport.log"), true);		
				fhandler.setFormatter(new Formatter() {
				public String format(LogRecord record) {
				DateFormat dateFormat = 
					new SimpleDateFormat(
								getConfiguration().getString("logs_migration.action.logging.dateformat", "yyyy-MM-dd HH:mm:ss") );
				Calendar cal = Calendar.getInstance();
				
				return dateFormat.format(cal.getTime()) + "  :  "			          		             
				+ record.getMessage() + "\n";
				}
				});

		fileMigrLog.addHandler(fhandler);
		
		String level = getConfiguration().getString("logs_migration.action.logging.level", "INFO");
		fileMigrLog.setLevel(
				java.util.logging.Level.parse(level) );
		
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {		

		try {
			System.out.println("Begin the Begin!");
			LogsMigrationCommandLineParser parser = new LogsMigrationCommandLineParser();

			for(int i=0; i<args.length; i++) {
				System.out.println(args[i]);	/**/
			}
				
			// parse the command line arguments
			if (parser.parse(args)) {
								
				System.out.println("Begin the File Import Process");
				
				String configFile = "/config.xml";
				String log4jFile = "/options_migration.properties";
					//"/log4j.properties";

				// Get options from parser
				String loadAction = parser.getLoadAction();
				String inputDir= parser.getInputDir();
				String logsDir = parser.getWtHomeDir() + PALMA_LOGS_PATH;
				String failedDir= parser.getWtHomeDir() + PALMA_FAILURES_PATH;
				String jvmId= parser.getJvmId();
				String checkDate = parser.getCheckDateOption();
				String outputConsolidate = parser.getOutputConsolidateOption();
				String outputLogDir = parser.getOutputLogDir();

				// Create a new MigrationAction object
				LogsMigrationAction mAction = new LogsMigrationAction();

				// Configure log4j
				URL log4jURL = mAction.getClass().getResource(log4jFile);	
				PropertyConfigurator.configure(log4jURL); 

				/**
				 * Setup configuration
				 */
				ConfigurationFactory factory = new ConfigurationFactory();

				URL configURL = mAction.getClass().getResource(configFile);	
				factory.setConfigurationURL(configURL);				

				Configuration configuration = factory.getConfiguration();
			
				
				mAction.setConfiguration(configuration);

				/* Set the Logger for the current main class */				
				mAction.setLogsMigrationLogger(outputLogDir);
				
				Collection<File> csvFiles = new ArrayList<File>();
				File   csvsDir  = new File(inputDir); 
				if(csvsDir.exists() && csvsDir.isDirectory()) {
					String[]   exts   = new String[] {"csv"};  
		            csvFiles = FileUtils.listFiles(csvsDir, exts, true);
		            
				}
				
				/* And obtain as well the log file containing the error lines of the Load process */
				if(StringUtils.isBlank(outputConsolidate)) {
					outputConsolidate = LogsProcessor.NO_VALUE;
				}
				
				/* Obtain boolean value for flag to ignore older files than given date 'logs_migration.failure.cutoffDelta' */
				boolean checkDateAfter =  true;
				if(StringUtils.isNotBlank(checkDate) && StringUtils.containsIgnoreCase(checkDate, LogsProcessor.NO_VALUE)) {
					checkDateAfter = false;
				}
				
				/* obtain the last failed file, containing the failed lines on csv */
				File lastFailFile = mAction.obtainLastFailedFile(loadAction, failedDir, jvmId, checkDateAfter, mAction);
				
				if(lastFailFile != null && csvFiles.size()>0) {
					/* Then obtain the right implementation Logs Processor class 
					 * First get the Load Type Name and then the Processor class Name */
					String loadActionTASCols = mAction.getConfiguration().getString("csvmapfile." + loadAction,"");
					
					String loadCsvColsSeparator = mAction.getConfiguration().getString("csvmapfile.separator.column","~");
					
					/* And obtain as well the log file containing the error lines of the Load process */
					if(StringUtils.isEmpty(jvmId)) {
						jvmId = StringUtils.substringBetween(lastFailFile.getName(), loadAction+"-", ".failures");
					}
					
					/* Obtain current Action Error Log File */
					String actionErrLogFile = loadAction + "-" + jvmId + ".log";
					
					/**
					 * Create a new context for each Processor
					 */
					Context context = new ContextBase();
					
					context.set(Context.LOAD_ACTION_KEY, loadAction);
					context.set(Context.CSV_ACTION_COLS_KEY, loadActionTASCols);
					context.set(Context.CSV_INPUT_FILES_KEY,csvFiles);
					context.set(Context.CSV_COLS_SEP_KEY,loadCsvColsSeparator);
					context.set(Context.FAILURES_FILE_KEY,lastFailFile);
					context.set(Context.JVM_ID_KEY,jvmId);
					context.set(Context.OUTPUT_CONSOLIDATE_KEY, outputConsolidate);
					context.set(Context.LOG_ERROR_FILE_KEY,actionErrLogFile);
					context.set(Context.INPUT_KEY, inputDir);
					context.set(Context.WT_LOGS_KEY, logsDir);
					context.set(Context.FAILED_KEY, failedDir);
					context.set(Context.OUTPUT_KEY, outputLogDir);
					
					mAction.launchProcessor(loadAction,context);				
				}
				
				// TODO
				/* Finally proceed to reorganize all the Log Files */				
				//		mAction.processLogFiles(outputLogDir);
				
				

				
				
				System.out.println("End of the Logs Process");
		
								
			}
		} catch (Exception e) {
			
			System.out.println("Exception in the main process: " + e.getMessage());
		}
	}


	/**
	 * @param loadAction
	 * 
	 */
	protected  void launchProcessor(String loadAction, Context context) {
		/* Obtain the instance of the right Processor */
		try {
			
			fileMigrLog.info("Proceed with action: " + loadAction);
			
			LogsProcessor lprocessor = getLogsProcessor(loadAction);
							
			/* Then proceed to launch the work(on matching folder) with this processor ...if it has been well instantiated. */		
			if(lprocessor != null) {
						
				context.set(Context.PROCESSOR_CLASS_KEY, lprocessor.getClass());
				
				fileMigrLog.info("Instantiated Processor: " + lprocessor.getClass());
				/* Configure it to be ready to work */
				lprocessor
						.initialize(getConfiguration(),
									context);
				
				
				fileMigrLog.info("Process files!");
				/* Start importing the files */
				lprocessor.processFiles();
				
			}
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}



	/**
	 * Obtains the last Failed File (csv lines) for a given Load Action
	 * @param loadAction
	 * @param failedDir
	 * @param jvmId
	 * @param checkDate
	 * @param mAction
	 * @return
	 */
	protected File obtainLastFailedFile(String loadAction,
		String failedDir,
		String jvmId, boolean checkDate,
		LogsMigrationAction mAction ) {
		File lastFailFile = null;
		
		File failDir = new File(failedDir);
		if(failDir.exists() && failDir.isDirectory()) {
			String findPattern = "*" + (StringUtils.isNotBlank(jvmId)?jvmId:"") + FAILURES_CSV;
			FileFilter fileFilter = new WildcardFileFilter(loadAction+findPattern, IOCase.INSENSITIVE);
			
			File[] failedFiles = failDir.listFiles(fileFilter);
			Arrays.sort(failedFiles, LastModifiedFileComparator.LASTMODIFIED_REVERSE);
			
			if(failedFiles.length>0) {
				
				lastFailFile = failedFiles[0];
			}
			
		}
		
		if(lastFailFile !=null && ( checkDate && mAction.isOlderThanExpected(lastFailFile) ) ) {
			
			lastFailFile = null;
		}
		
		return lastFailFile;
	}

	/**
	 *  Checks if the last found file is older than the cutoff date for processing failures
	 * @param lastFailFile
	 * @return
	 */
	protected  boolean isOlderThanExpected(File lastFailFile) {
		boolean isOlderThanExpected = false;
		long cutoffDelta = NumberUtils.createLong(this.configuration.getString("logs_migration.failure.cutoffDelta","86400000").trim());
			
			//this.configuration.getString("csv_migration.failure.cutoffDelta","24 * 60 * 60 * 1000").trim();
		long cutoff = System.currentTimeMillis() - (cutoffDelta);
		if(lastFailFile !=null) {
			
			isOlderThanExpected = lastFailFile.lastModified()<cutoff;
			
			System.out.println("File : " + lastFailFile.getName() + " and older : " + isOlderThanExpected);
		}
		
		return isOlderThanExpected;
	}
	
	
	/**
	 * Create an LogsProcessor instance from the class name that must have been previously set into context.
	 * @return an InputDataHandler implementation instance
	 * @throws MigrationException
	 * @see com.airbus.sugecom.migration.handlers.Context
	 */
	protected LogsProcessor getLogsProcessor(String loadAction) throws LogsMigrationException {

		/**
		 * Load Processor dynamically
		 */
		/* Then obtain the right implementation Logs Processor class 
		 * First get the Load Type Name and then the Processor class Name */
		String implProcessorClassName = getConfiguration().getString("logs_migration." + loadAction + ".processorclassname","");

		// FileProcessor reference
		LogsProcessor logsProcessor = null;
		
		// Create an instance of the handler implementation 
		try {
				
			if(StringUtils.isNotBlank(implProcessorClassName)) {
				logsProcessor = (LogsProcessor)Class.forName(implProcessorClassName).newInstance();
			}	

		} catch (Exception e) {
			throw new LogsMigrationException("Failed to load class " + implProcessorClassName, e);
		}
		
		return logsProcessor;
	}	
	

}
