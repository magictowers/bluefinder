package normalization;

public interface INormalizator {
	  
	String normalizeCategory(String subCategoryName, String fromName, String toName);
	
	String normalizePage(String pageName, String fromName, String toName);
	
}
