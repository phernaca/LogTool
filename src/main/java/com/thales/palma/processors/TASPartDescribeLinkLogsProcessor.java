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
public class TASPartDescribeLinkLogsProcessor extends TASDefaultLogsProcessor {

	private static final String NULL = "null";
	
	/* (non-Javadoc)
	 * @see com.thales.palma.processors.AbstractLogsProcessor#obtainObjectLineId(java.util.Map)
	 */
	@Override
	protected String obtainObjectLineId(Map<String, String> tmpCsvMapLine) {

		StringBuilder objectId = new StringBuilder();
		
		objectId.append(getValue(tmpCsvMapLine, "01legacyIterationIdentifierPart"));
		objectId.append(SEP_LINE_ID);
		objectId.append(getValue(tmpCsvMapLine, "02numberPart"));
		objectId.append(SEP_LINE_ID);
		objectId.append(getValue(tmpCsvMapLine, "03versionPart"));
		objectId.append(SEP_LINE_ID);
		objectId.append(getValue(tmpCsvMapLine, "04viewPart"));
		objectId.append(SEP_LINE_ID);
		objectId.append(getValue(tmpCsvMapLine, "05legacyIterationIdentifierDocument"));
		objectId.append(SEP_LINE_ID);
		objectId.append(getValue(tmpCsvMapLine, "06numberDocument"));
		objectId.append(SEP_LINE_ID);
		objectId.append(getValue(tmpCsvMapLine, "07versionDocument"));
		
		return objectId.toString();
	}
	
	@Override
	protected boolean containsObjectLineId(String currentLine, String objectId) {
		return StringUtils.containsIgnoreCase(currentLine, objectId);
	}
	
	private String getValue (Map<String, String> tmpCsvMapLine, String key) {
		String value = tmpCsvMapLine.get(key);
		
		if (value == null || StringUtils.isEmpty(value)) {
			return NULL;
		}
		
		return value;
	}
}
