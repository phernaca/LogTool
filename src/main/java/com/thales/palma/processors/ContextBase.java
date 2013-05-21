package com.thales.palma.processors;

import java.util.HashMap;
import java.util.Map;

public class ContextBase implements Context {

	Map<String, Object> valueMap = new HashMap<String, Object>();
	
	public Object get(String key) {
		return valueMap.get(key);
	}

	public void set(String key, Object value) {
		valueMap.put(key, value);
	}

	
}
