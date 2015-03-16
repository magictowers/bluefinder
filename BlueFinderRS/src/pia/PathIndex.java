package pia;

import java.util.List;

import db.PropertiesFileIsNotFoundException;

public interface PathIndex {
	
	public List<String> getPathQueries(String x,String y) throws PropertiesFileIsNotFoundException;

}
