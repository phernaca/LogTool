/**
 * 
 */
package com.thales.palma.processors;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.LineIterator;
import org.apache.commons.lang.StringUtils;

/**
 * @author frup82635
 *
 */
public class TASDefaultLogsProcessor  extends AbstractLogsProcessor {

	private static final String VC_VERSION_CONTROL_EXCEPTION = "(wt.vc.vcResource/60) wt.vc.VersionControlException:";


	@Override
	protected boolean containsObjectLineId(String currentLine, String objectId) {
		return StringUtils.containsIgnoreCase(currentLine, objectId);
	}

	@Override
	protected String obtainObjectLineId(Map<String, String> tmpCsvMapLine) {
		
		StringBuilder objectId = new StringBuilder();
		
		objectId.append(tmpCsvMapLine.get("05number"));
		objectId.append(SEP_LINE_ID);
		objectId.append(tmpCsvMapLine.get("11version"));
		
		return objectId.toString();
	}

	
	@Override
	protected String obtainErrorLogDescription(LineIterator logFileIter, String objectId) {
		String descErrLogLine = logFileIter.nextLine();
		if(StringUtils.startsWith(descErrLogLine.trim(), VC_VERSION_CONTROL_EXCEPTION)) {
			descErrLogLine +=" : " + logFileIter.nextLine();
		}
		
		actionLoggerGeneric.info("Error Description : " + descErrLogLine);
		return descErrLogLine;
	}
	
	@Override
	protected String[] filterCsvValues(String[] rawValues) {
	
		
		
		List<String> filteredList = new ArrayList();
		for(int i=0; i<rawValues.length; i++) {
			if(!isValueOmmited(rawValues[i])) {
				filteredList.add(rawValues[i]);
			}
		}
		
	
		
		return  filteredList.toArray(new String[filteredList.size()]);
		
	}
	
	
	protected boolean isValueOmmited(String rawValue) {
		
		boolean isValOmmited = false;
		
		if(StringUtils.equals(rawValue.trim(), "wt.doc.WTDocument") 
				|| (StringUtils.equals(rawValue, "ECP") || (StringUtils.equals(rawValue, "RFD")))
				|| 	StringUtils.startsWith(rawValue, "com.ptc.")	) {
			
			isValOmmited = true;
		}
		
		
		return isValOmmited;
	}
	

}
