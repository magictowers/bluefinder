package normalization;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import db.DBConnector;
import db.PropertiesFileIsNotFoundException;
import db.WikipediaConnector;

public class TranslatorBasicNormalization extends BasicNormalization implements INormalizator {
		
    private String languageToTranslate;
	private String categoryNamePrefix;
	private Map<String, String> hash;
	private DBConnector connector;
    
    public TranslatorBasicNormalization(DBConnector connector, String languageToTranslate, String categoryNameprefix) {
    	this.connector = connector;
    	this.languageToTranslate = languageToTranslate;
    	this.categoryNamePrefix = categoryNameprefix;
    	this.hash = new HashMap<String, String>();
    }
    
    /**
     * 
     * @param pageName
     * @param pageNamespace
     * @return 
     * @throws PropertiesFileIsNotFoundException 
     */
    private String getTranslatedName(String pageName, int pageNamespace) throws PropertiesFileIsNotFoundException {
    	String result = pageName;
        String key = pageName + " -ns- " + pageNamespace;
    	if (this.hash.containsKey(key)) {
    		result = this.hash.get(key);
    	} else {
            try {
                Connection con = this.connector.getWikiConnection();
                String query = ""
                        + "SELECT CONVERT(ll_title USING utf8) as ll_title "
                        + "FROM langlinks as lan INNER JOIN page ON "
                            + "page_title = ? "
                            + "AND page_namespace = ? "
                            + "AND page_id = lan.ll_from "
                            + "AND ll_lang = ?";

                PreparedStatement st = con.prepareStatement(query);
                st.setString(1, pageName);
                st.setInt(2, pageNamespace);
                st.setString(3, this.languageToTranslate);

                ResultSet res = st.executeQuery();
                if (res.next()) {
                    result = res.getString("ll_title");
                    this.hash.put(key, result);
                }
                    
                st.close();			
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    	return result;
    }
    
	@Override
	public String normalizeCategory(String subCategoryName, String fromName, String toName) throws PropertiesFileIsNotFoundException {
		String name = this.getTranslatedName(subCategoryName, 14);
		String translatedFromName = this.getTranslatedName(fromName, 0);
		String translatedToName = this.getTranslatedName(toName, 0);
		if (translatedFromName == null) {
			translatedFromName = fromName;
		}
		if (translatedToName == null) {
			translatedToName = toName;
		}
		if (name == null) {
			name = subCategoryName;
		}
		name = name.replaceFirst(this.categoryNamePrefix, "");
		name = name.replace(" ", "_");
		name = super.normalizeCategory(name.trim(), translatedFromName.replace(" ", "_"),
                                       translatedToName.replace(" ", "_"));
		return name;
	}

	@Override
	public String normalizePage(String pageName, String fromName, String toName) {
		// TODO Auto-generated method stub
		return null;
	}

}
