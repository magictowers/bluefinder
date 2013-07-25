package normalization;

import java.util.regex.Pattern;

public class BasicNormalization implements INormalizator {

	@Override
	public String normalizeCategory(String subCategoryName, String fromCatName, String toCatName) {
        String normalized;
        String toName = Pattern.quote(toCatName);
        String fromName = Pattern.quote(fromCatName);
       
        normalized = subCategoryName.replaceAll(fromName, "#from");
        return normalized.replaceAll(toName, "#to");
    }

	@Override
	public String normalizePage(String pageName, String fromName, String toName) {
		// TODO Auto-generated method stub
		return null;
	}

}
