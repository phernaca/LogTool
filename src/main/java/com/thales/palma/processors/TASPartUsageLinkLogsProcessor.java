package com.thales.palma.processors;

import java.util.Map;

import org.apache.commons.io.LineIterator;
import org.apache.commons.lang.StringUtils;

public class TASPartUsageLinkLogsProcessor extends AbstractLogsProcessor {

	private static final String UNIQUENESS_CONSTRAINT_VIOLATION = "wt.pom.UniquenessException: A datastore uniqueness constraint violation";

	@Override
	protected boolean containsObjectLineId(String currentLine, String objectId) {
		
		boolean containsObjId = true;
		
		String[] mainTokens = StringUtils.split(objectId, SEP_LINE_ID);
		
		int position = 0;
		for(int idx=0; containsObjId && idx<mainTokens.length; idx++) {
			
			position = StringUtils.indexOf(currentLine, mainTokens[idx], position);
			
			/* once an expected column value is Not found then set the flag to false */
			containsObjId = (position!=-1);
			 
		}
		
		return containsObjId;
		
	}

	
	@Override
	protected String obtainObjectLineId(Map<String, String> tmpCsvMapLine) {
		
		StringBuilder objectId = new StringBuilder();
		
		objectId.append(tmpCsvMapLine.get("01legacyIterationIdentifierUses"));
		objectId.append(SEP_LINE_ID);
		objectId.append(tmpCsvMapLine.get("02numberUses"));
		objectId.append(SEP_LINE_ID);
		objectId.append(tmpCsvMapLine.get("03versionUses"));
		objectId.append(SEP_LINE_ID);
		objectId.append(tmpCsvMapLine.get("04viewUses"));
		objectId.append(SEP_LINE_ID);
		objectId.append(tmpCsvMapLine.get("05legacyMasterIdentifierUsed"));
		objectId.append(SEP_LINE_ID);
		objectId.append(tmpCsvMapLine.get("06numberUsed"));
		
		return objectId.toString();
		
	}
	
	@Override
	protected String obtainErrorLogDescription(LineIterator logFileIter, String objectId) {
		String descErrLogLine = logFileIter.nextLine();
		if(StringUtils.startsWith(descErrLogLine.trim(), UNIQUENESS_CONSTRAINT_VIOLATION)) {
			descErrLogLine +=" : " + logFileIter.nextLine();
		}
		
		actionLoggerGeneric.info("Error Description : " + descErrLogLine);
		return descErrLogLine;
	}
	

}
