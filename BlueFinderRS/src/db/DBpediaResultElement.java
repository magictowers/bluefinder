package db;

import java.util.HashMap;
import java.util.Map;

public class DBpediaResultElement implements ResultElement {

	Map<String,String> values ;
	
	public DBpediaResultElement() {
		this.values=new HashMap<String,String>();
	}
	
	@Override
	public String at(String columnName) {
		return this.values.get(columnName);
	}
	
	public void put(String columnName, String columnValue){
		this.values.put(columnName, columnValue);
	}
	
	@Override
	public boolean equals(Object obj) {
		if ( this == obj ) return true;
		if ( !(obj instanceof ResultElement) ) return false;
		boolean realEquals = true;
		ResultElement other = (ResultElement) obj;
		for (String key : this.values.keySet()) {
			realEquals = realEquals && (this.at(key).equals(other.at(key)));
		}
		return realEquals;
	}
	
	@Override
	public int hashCode() {
		int hash = 1;
		for (String key: this.values.keySet()) {
			hash = hash * 31 + (key.hashCode() + this.at(key).hashCode());
		}
		return hash;
	}
}
