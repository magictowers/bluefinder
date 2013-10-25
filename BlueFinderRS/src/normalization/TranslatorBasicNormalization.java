package normalization;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import db.WikipediaConnector;

public class TranslatorBasicNormalization extends BasicNormalization implements INormalizator {
		
    private String languageToTranslate;
	private String categoryNamePrefix;
    
    public TranslatorBasicNormalization(String languageToTranslate, String categoryNameprefix) {
    	this.languageToTranslate = languageToTranslate;
    	this.categoryNamePrefix = categoryNameprefix;
    }
    
    private String getTranslatedName(String pageName, int pageNamespace) {    	
    	// SELECT * FROM eswikiquote.langlinks as lan inner join page on page_title="Temas" and page_namespace=14 and page_id=lan.ll_from and ll_lang='en';
    	String result = null;
    	try {
			Connection con = WikipediaConnector.getConnection();
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
			if(res.next()){
				 //byte[]  var = (byte[]) res.getObject("ll_title");
		         //result = new String(var);
				result = res.getString("ll_title");
			}
			st.close();			
		}  catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
    	return result;    	
    }

	@Override
	public String normalizeCategory(String subCategoryName, String fromName, String toName) {
		String name = this.getTranslatedName(subCategoryName, 14);
		String translatedFromName = this.getTranslatedName(fromName, 0);
		String translatedtoName = this.getTranslatedName(toName, 0);
		if (translatedFromName == null) {
			translatedFromName = fromName;
		}
		if (translatedtoName == null) {
			translatedtoName = toName;
		}
		if (name == null) {
			name = subCategoryName;
		}
		name = name.replaceFirst(this.categoryNamePrefix, "");
		name = name.replace(" ", "_");
		name = super.normalizeCategory(name.trim(), translatedFromName, translatedtoName);
		return name;
	}

	@Override
	public String normalizePage(String pageName, String fromName, String toName) {
		// TODO Auto-generated method stub
		return null;
	}

}
