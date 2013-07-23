package normalization;

public class BasicNormalization implements INormalizator {

	@Override
	public String normalizeCategory(String subCategoryName, String fromCatName, String toCatName) {
        String normalized;
        normalized = subCategoryName.replaceAll(fromCatName, "#from");
        return normalized.replaceAll(toCatName, "#to");
    }

	@Override
	public String normalizePage(String pageName, String fromName, String toName) {
		// TODO Auto-generated method stub
		return null;
	}

}
