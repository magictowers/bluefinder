package normalization;

import db.PropertiesFileIsNotFoundException;

public interface INormalizator {
	  
	String normalizeCategory(String subCategoryName, String fromName, String toName) throws PropertiesFileIsNotFoundException;
	
	String normalizePage(String pageName, String fromName, String toName);
	
}
