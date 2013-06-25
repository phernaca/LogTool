/**
 * 
 */
package com.thales.palma.processors;

import org.apache.commons.io.LineIterator;
import org.apache.commons.lang.StringUtils;

/**
 * @author frup82635
 *
 */
public class TASDocumentLogsProcessor extends TASDefaultLogsProcessor {

	private static final String SPACE = " ";
	
	@Override
	protected String obtainErrorLogDescription(LineIterator logFileIter, String objectId) {
		// get only object number
		String number = StringUtils.split(objectId,SEP_LINE_ID)[0];
		
		String descErrLogLine = logFileIter.nextLine();
		// if first line does not contain enough info, get also the second
		if (!descErrLogLine.equalsIgnoreCase(number)) {
			descErrLogLine += SPACE + logFileIter.nextLine();
		}
		
		actionLoggerGeneric.info("Error Description : " + descErrLogLine);
		return descErrLogLine;
	}
}
