package com.thales.palma.processors;

import java.util.Map;

public class TASPartReferenceLinkLogsProcessor extends TASDefaultLogsProcessor {

	/* (non-Javadoc)
	 * @see com.thales.palma.processors.AbstractLogsProcessor#obtainObjectLineId(java.util.Map)
	 */
	@Override
	protected String obtainObjectLineId(Map<String, String> tmpCsvMapLine) {

		StringBuilder objectId = new StringBuilder();
		
		objectId.append(tmpCsvMapLine.get("01legacyIterationIdentifierPart"));
		objectId.append(SEP_LINE_ID);
		objectId.append(tmpCsvMapLine.get("02numberPart"));
		objectId.append(SEP_LINE_ID);
		objectId.append(tmpCsvMapLine.get("03versionPart"));
		objectId.append(SEP_LINE_ID);
		objectId.append(tmpCsvMapLine.get("05legacyIterationIdentifierDocument"));
		objectId.append(SEP_LINE_ID);
		objectId.append(tmpCsvMapLine.get("06numberDocument"));
		
		return objectId.toString();
	}
	
}
