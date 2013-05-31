/**
 * 
 */
package com.thales.palma.processors;

import java.util.Map;

/**
 * @author frup82635
 *
 */
public class TASProblemReportLogsProcessor extends TASDefaultLogsProcessor {
	
	@Override
	protected String obtainObjectLineId(Map<String, String> tmpCsvMapLine) {
		
		return tmpCsvMapLine.get("02number");
	}
}
