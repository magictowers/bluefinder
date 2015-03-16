package normalization;

import java.util.regex.Pattern;

import db.PropertiesFileIsNotFoundException;
import utils.FromToPair;

public class BasicNormalization implements INormalizator {

	@Override
	public String normalizeCategory(String subCategoryName, String fromCatName, String toCatName) throws PropertiesFileIsNotFoundException {
        String normalized;
        String toName = Pattern.quote(toCatName);
        String fromName = Pattern.quote(fromCatName);
       
//        normalized = subCategoryName.replaceAll(fromName, "#from");
//        normalized.replaceAll(toName, "#to");
        normalized = subCategoryName.replaceAll(fromName, FromToPair.FROM_WILDCARD);
        return normalized.replaceAll(toName, FromToPair.TO_WILDCARD);
    }

	@Override
	public String normalizePage(String pageName, String fromName, String toName) {
		// TODO Auto-generated method stub
		return null;
	}

}
