package db.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import utils.ProjectConfigurationReader;

/**
 *
 * @author mkaminose
 */
public class WikipediaDbTranslation extends WikipediaDbInterface {
    
    public WikipediaDbTranslation() throws ClassNotFoundException, SQLException {
        super();
    }
    
    /**
     * Given a page in a language, returns its translated name, the language is determined
     * by the parameter LANGUAGE_CODE in the setup file.
     * 
     * @param page
     * @return translated page or the same one
     * @throws ClassNotFoundException 
     */
    @Override
    public String getTranslatedPage(String page) throws ClassNotFoundException {
        String transName = page;
        Integer pageId = this.getPageId(page);
        try {
            Connection c = getConnection();
            String query = "SELECT convert(ll_title using utf8) AS ll_title FROM langlinks WHERE ll_lang = ? AND ll_from = ?";
            PreparedStatement stmt = c.prepareStatement(query);
            stmt.setString(1, ProjectConfigurationReader.languageCode());
            stmt.setInt(2, pageId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                transName = rs.getString("ll_title");
                System.out.println("Translated: " + transName);
            }else{
            	System.out.println(page+ " was not translated");
            }
            stmt.close();
        } catch (SQLException ex) {
           System.out.println("Error al buscar la traducción para " + page);
        } 
        return transName;
    }

    @Override
	public List<String> getListOf(Integer pageId) {
		List<String> items = new ArrayList<String>();
		String transQuery = ""
                    + "SELECT CONVERT(ll_title USING utf8) AS page_title "
                    + "FROM pagelinks AS level0 "
                        + "INNER JOIN page AS level1 "
                        + "INNER JOIN langlinks ll ON ("
                        + "(level0.pl_namespace = 0 OR level0.pl_namespace = 104) AND "
                        + "level0.pl_from = ? AND "
                        + "level0.pl_title = level1.page_title AND "
                        + "ll.ll_lang = 'en' AND "
                        + "ll.ll_title LIKE 'List_of_%' AND "
                        + "level1.page_id = ll.ll_from)";
		try {
			PreparedStatement stmt = getConnection().prepareStatement(transQuery);
			stmt.setInt(1, pageId);
			ResultSet results = stmt.executeQuery();
			while (results.next()) {
				items.add(results.getString("page_title"));
			}
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return items;
	}
}
