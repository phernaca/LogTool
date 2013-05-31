/**
 * 
 */
package com.thales.palma.processors;

import java.util.Map;

import org.apache.commons.lang.StringUtils;

/**
 * @author frup82635
 *
 */
public class TASDefaultLogsProcessor  extends AbstractLogsProcessor {

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

}
