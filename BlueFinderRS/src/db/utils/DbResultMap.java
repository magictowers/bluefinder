package db.utils;

import java.util.HashMap;

@SuppressWarnings("serial")
public class DbResultMap extends HashMap<String, Object> {

	public Integer getInteger(String key) throws ClassCastException {
		Integer value = null;
		value = (Integer) this.get(key);
		return value;
	}
	
	public String getString(String key) throws ClassCastException {
		String value = null;
		value = (String) this.get(key);
		return value;
	}
	
}