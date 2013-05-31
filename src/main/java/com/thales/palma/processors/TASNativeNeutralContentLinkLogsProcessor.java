/**
 * 
 */
package com.thales.palma.processors;

import java.util.Map;

/**
 * @author frup82635
 *
 */
public class TASNativeNeutralContentLinkLogsProcessor extends
		TASDefaultLogsProcessor {

	/* (non-Javadoc)
	 * @see com.thales.palma.processors.AbstractLogsProcessor#obtainObjectLineId(java.util.Map)
	 */
	@Override
	protected String obtainObjectLineId(Map<String, String> tmpCsvMapLine) {

		StringBuilder objectId = new StringBuilder();
		
		objectId.append(tmpCsvMapLine.get("04nativeFileName"));
		objectId.append(SEP_LINE_ID);
		objectId.append(tmpCsvMapLine.get("05neutralFileName"));
		
		return objectId.toString();
	}

}
