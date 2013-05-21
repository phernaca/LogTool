package com.thales.palma.logsmigration;

import java.io.File;
import java.io.FileNotFoundException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang.StringUtils;

public class LogsMigrationCommandLineParser {

	public static String CSV_LOAD_ACTION = "cla";
	public static String INPUT_DIRECTORY_OPTION = "id";
	public static String WT_HOME_OPTION = "wt";
	public static String DATE_OPTION = "dt";
	public static String JVM_ID_OPTION = "vm";
	public static String OUTPUT_LOG_DIRECTORY_OPTION = "old";
	
    /** The options. */
    private final Options options = new Options();
	
    /** The Command Line Parser. */
    private final CommandLineParser commandLineParser = new GnuParser();
	
    /** And The Command Line itself. */
    private CommandLine commandLine = null;
    
	private String inputDir;
	private String wtHomeDir;
	private String jvmId;
	private String outputLogDir;
    

	@SuppressWarnings("static-access")
    public LogsMigrationCommandLineParser() {
    	
		Option csvLoadAction = OptionBuilder.withArgName("csvLoadAction")
		.hasArg()
		.withDescription("CSV Load Action (e.g. 'CreateTASInternalHW' to load IHW Items).")
		.create(CSV_LOAD_ACTION);

		Option inputDir = OptionBuilder.withArgName("inputDirectory")
		.hasArg()
		.withDescription("use given main input directory (full name) which contains all csv files")
		.create(INPUT_DIRECTORY_OPTION);

		Option wtDir = OptionBuilder.withArgName("wtHomeDirectory")
		.hasArg()
		.withDescription("use given main input directory (full name) which contains all csv files")
		.create(WT_HOME_OPTION);

		
		Option jvmId = OptionBuilder.withArgName("jvmId")
		.hasArg()
		.withDescription("used MethodServer JVM ID for the Load")
		.create(JVM_ID_OPTION);
		
		Option checkDate = OptionBuilder.withArgName("option")
		.hasArg()
		.withDescription("check for after date (yes/no). By default is 'yes'")
		.create(DATE_OPTION);
		
		Option outputLogDir = OptionBuilder.withArgName("outputLogDirectory")
		.hasArg()
		.withDescription("use given output logs directory (full name)")
		.create(OUTPUT_LOG_DIRECTORY_OPTION);
		

		csvLoadAction.setRequired(true);
		inputDir.setRequired(true);
		wtDir.setRequired(true);
		jvmId.setRequired(false);
		checkDate.setRequired(false);
		outputLogDir.setRequired(true);

		
		options.addOption(csvLoadAction);
		options.addOption(inputDir);
		options.addOption(wtDir);
		options.addOption(checkDate);
		options.addOption(jvmId);
		options.addOption(outputLogDir);		

	}
	
	/**
	 * Parse the given arguments and print out the usage message if parsing fails.
	 * @param args the command line arguments
	 * @return true if the parsing succeed, false otherwise
	 */
	public boolean parse(String[] args) {
		
		boolean hasErrors = false;
		
		/* Then initialize the Parser with the options*/
		// parse the command line arguments
		try {
			// Check command line arguments
			commandLine = commandLineParser.parse( options, args );
			
			// Check and build input directory path
			this.inputDir = getFilePath(commandLine.getOptionValue(INPUT_DIRECTORY_OPTION), true);
			
			// Check and build input directory path
			this.wtHomeDir = getFilePath(commandLine.getOptionValue(WT_HOME_OPTION), true);
			
			// Check only output log directory and build output file path
			this.outputLogDir= getFilePath(commandLine.getOptionValue(OUTPUT_LOG_DIRECTORY_OPTION), true);
			
		} catch (ParseException e) {
			
			// oops, something went wrong
			System.err.println( "Parsing failed. " + e.getMessage() );

			// automatically generate the help/usage statement
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp(" ", options, true );
			
			hasErrors = true;
			
		} catch (FileNotFoundException e) {
			System.err.println(e.getMessage() );
			
			hasErrors = true;
		}
		
		return !hasErrors;
	}

	/**
	 * Build a full path from the given directory and file name and check if it exists. Check only the directory if file name is blank or null. 
	 * @param filePath the file name
	 * @return a File object corresponding to the given file path
	 * @throws FileNotFoundException when directory  does not exist.
	 */
	private String getFilePath(String filePath, boolean checkIfPathExist) throws FileNotFoundException {
		
		File file = null;
		
		// filePath will never be empty or blank since it is declared as required with CLI 
		if(StringUtils.isNotBlank(filePath)) {

			/* Obtain the Path for the Complete Dir Path */
			file = new File(filePath);

			if (checkIfPathExist) {				
				if ( !file.exists()
						|| (file.exists() && file.isFile()) ) {

					/* Throw an Exception as the File does Not exist */
					throw new FileNotFoundException("Invalid path : " + file.getPath());
				}
			}
						
		}		

		return 	filePath;	
	}
	
	public String getLoadAction() {
		return commandLine.getOptionValue(CSV_LOAD_ACTION);
	}
	

	public String getInputDir() {
		return this.inputDir;
	}
	
	
	public String getJvmId() {
		return commandLine.getOptionValue(JVM_ID_OPTION);
	}

	public String getCheckDateOption() {
		return commandLine.getOptionValue(DATE_OPTION);
	}
	
	public String getWtHomeDir() {
		return wtHomeDir;
	}

	public String getOutputLogDir() {
		return this.outputLogDir;
	}
	
	public CommandLine getCommandLine() {
		return commandLine;
	}
}
