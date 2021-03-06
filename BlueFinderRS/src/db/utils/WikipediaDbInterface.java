package db.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import pia.PathFinder;
import db.WikipediaConnector;
import utils.ProjectConfiguration;

public class WikipediaDbInterface {

    public static final List<String> BLACKLIST_CATEGORY;
    private boolean translate;
    static {
        List<String> tmp = new ArrayList<String>();
        try {
            String filename = ProjectConfiguration.blacklistFilename();
            InputStream blackListIS = PathFinder.class.getClassLoader().getResourceAsStream(filename);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(blackListIS));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                tmp.add(line);
            }
            bufferedReader.close();
        } catch (IOException e) {
            System.err.print("Some error ocurred while loading category's blacklist.");
            e.printStackTrace();
        }
        BLACKLIST_CATEGORY = Collections.unmodifiableList(tmp);
    }
    
    public WikipediaDbInterface() {
    	this.translate = ProjectConfiguration.translate();
    }
	
	/**
	 * Get the id for the given category.
	 * 
	 * @param category
	 * @return an ID, or 0.
	 * @throws ClassNotFoundException
	 */
	public Integer getCategoryId(String category) throws ClassNotFoundException {
		return this.getIdFor(category, 14);
	}
	
	/**
	 * Get the id for the given page.
	 * 
	 * @param page
	 * @return an ID, or 0
	 * @throws ClassNotFoundException
	 */
	public Integer getPageId(String page) throws ClassNotFoundException {
		return this.getIdFor(page, 0);
	}
    
    /**
     * Given a page in a language, returns its translated name, the language is determined
     * by the parameter LANGUAGE_CODE in the setup file.
     * 
     * @param page
     * @return translated page or the same one
     * @throws ClassNotFoundException 
     */
    public String getTranslatedPage(String page) throws ClassNotFoundException {
        String transName = page;
        Integer pageId = this.getPageId(page);
        try {
            Connection c = WikipediaConnector.getConnection();
            String query = "SELECT convert(ll_title using utf8) AS ll_title FROM langlinks WHERE ll_lang = ? AND ll_from = ?";
            PreparedStatement stmt = c.prepareStatement(query);
            stmt.setString(1, ProjectConfiguration.languageCode());
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
	
	/**
	 * Get the id for the given resource and namespace.
	 * 
	 * @param resource
	 * @param namespace
	 * @return an ID, or 0.
	 * @throws ClassNotFoundException
	 */
	public Integer getIdFor(String resource, int namespace) throws ClassNotFoundException {
		int id = 0;
		try {
            Connection c = WikipediaConnector.getConnection();
            String query = "SELECT page_id FROM page WHERE page_namespace = ? AND page_title = ?";
            PreparedStatement stmt = c.prepareStatement(query);
            stmt.setInt(1, namespace);
            stmt.setString(2, resource);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                id = rs.getInt("page_id");
            }
            stmt.close();
        } catch (SQLException ex) {
           System.out.println("Error para obtener el id de " + resource);
        } 
		return id;
	}

	public boolean isInCategoryBlackList(String category) {
		boolean isBlackList = false;
		if (BLACKLIST_CATEGORY.contains(category)) {
			isBlackList = true;
		} else {
			for (int i = 0; i < BLACKLIST_CATEGORY.size() && !isBlackList; i++) {
				String black = BLACKLIST_CATEGORY.get(i);
				if (category.startsWith(black)) {
					isBlackList = true;
				}
			}
		}
		return isBlackList;
	}
	
	/**
	 * Get the categories for the given page.
	 * 
	 * @param pageId
	 * @return a list of categories for the given page id.
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	public List<String> getCategories(Integer pageId) throws SQLException, ClassNotFoundException {
		List<String> categories = new ArrayList<String>();
		String query = ""
				+ "SELECT convert(cl_to using utf8) AS cl_to "
				+ "FROM categorylinks c "
				+ "WHERE cl_from = ? and cl_type = \"page\"";
		Connection conn = WikipediaConnector.getConnection();
		PreparedStatement stmt = conn.prepareStatement(query);
		stmt.setInt(1, pageId);
		ResultSet resultSet = stmt.executeQuery();
		
		while (resultSet.next()) {
			String categoryTo = resultSet.getString("cl_to");
			if (!this.isInCategoryBlackList(categoryTo))
				categories.add(categoryTo);
		}
		stmt.close();
		return categories;
	}

	/**
	 * Get the subcategories for the given category.
	 * 
	 * @param category
	 * @return a list of categories for the given category. 
	 * @throws ClassNotFoundException 
	 */
	public List<String> getSubcategories(String category) throws ClassNotFoundException {
		List<String> categories = new ArrayList<String>();
		PreparedStatement stmt;
		try {
			Connection conn = WikipediaConnector.getConnection();
			String query = ""
					+ "SELECT convert(cl_from using utf8) AS cl_from, convert(page_title using utf8) AS page_title "
					+ "FROM categorylinks INNER JOIN page ON cl_from = page_id AND page.page_namespace = 14 AND cl_to = ?";
			stmt = conn.prepareStatement(query);
			stmt.setString(1, category);
			ResultSet resultSet = stmt.executeQuery();
			while (resultSet.next()) {
				String pageTitle = resultSet.getString("page_title");
				categories.add(pageTitle);
			}
			stmt.close();
		} catch (SQLException ex) {
			Logger.getLogger(PathFinder.class.getName()).log(Level.SEVERE, null, ex);
		}
		return categories;
	}

	public List<String> getListOf(Integer pageId) {
		List<String> items = new ArrayList<String>();
		String query;
		if (!this.translate) {
			query = ""
                    + "SELECT page.page_id AS page_id, CONVERT(page.page_title USING utf8) AS page_title "
                    + "FROM pagelinks AS level0 INNER JOIN page ON ("
                        + "level0.pl_from = ? "
                        + "AND level0.pl_namespace = 0 "
                        + "AND page.page_namespace = 0 "
                        + "AND page.page_title = level0.pl_title "
                        + "AND page.page_title LIKE 'List_of_%')";
		} else {
            // este primero es de prueba, para ajustar el segundo
            query = ""
                    + "SELECT CONVERT(ll_title USING utf8) AS page_title, CONVERT(page.page_title USING utf8) AS original_page_title "
                    + "FROM page INNER JOIN pagelinks AS level0 INNER JOIN page AS level1 INNER JOIN langlinks ll ON ("
                        + "page.page_id = level0.pl_from AND "
                        + "page.page_namespace = 0 AND "
                        + "(level0.pl_namespace = 0 OR level0.pl_namespace = 104) AND "
                        + "level0.pl_from = ? AND "
                        + "level0.pl_title = level1.page_title AND "
                        + "ll.ll_lang = 'en' AND "
                        + "ll.ll_title LIKE 'List_of_%' AND "
                        + "level1.page_id = ll.ll_from)";
//			query = ""
//                    + "SELECT page.page_id AS page_id, CONVERT(page.page_title USING utf8) AS page_title "
//                    + "FROM pagelinks AS level0 INNER JOIN page INNER JOIN langlinks ll ON ("
//                        + "level0.pl_from = ? "
//                        + "AND level0.pl_namespace = 0 "
//                        + "AND page.page_namespace = 0 "
//                        + "AND page.page_title = level0.pl_title "
//                        + "AND ll.ll_lang = 'en' "
//                        + "AND ll.ll_title LIKE 'List_of_%')";
		}
		try {
			PreparedStatement stmt = WikipediaConnector.getConnection().prepareStatement(query);
			stmt.setInt(1, pageId);
			ResultSet results = stmt.executeQuery();
			while (results.next()) {
				items.add(results.getString("page_title"));
			}
			stmt.close();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return items;
	}
    
    public boolean areDirectlyLinked(Integer fromId, Integer toId) throws SQLException, ClassNotFoundException {
        boolean areLinked = false;
        String query = ""
				+ "SELECT COUNT(page.page_id) AS page_id "
				+ "FROM pagelinks AS level0 INNER JOIN page ON ("
					+ "level0.pl_from = ? "
					+ "AND level0.pl_namespace = 0 "
					+ "AND page.page_namespace = 0 "
					+ "AND page.page_title = level0.pl_title) "
                    + "AND page.page_id = ?";
        Connection conn = WikipediaConnector.getConnection();
        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setInt(1, fromId);
        stmt.setInt(2, toId);
        ResultSet results = stmt.executeQuery();
        results.next();
        if (results.getInt(1) >= 1) { // si devuelve un resultado, es ese ID, significa que estan relacionados
            areLinked = true;
        }
        return areLinked;
    }
	
	public List<DbResultMap> getDirectNodes(Integer pageId) throws ClassNotFoundException, SQLException {
		List<DbResultMap> nodes = new ArrayList<DbResultMap>();
		Connection conn = WikipediaConnector.getConnection();
		String query = ""
				+ "SELECT page.page_id AS page_id, CONVERT(page.page_title USING utf8) AS page_title "
				+ "FROM pagelinks AS level0 INNER JOIN page ON ("
					+ "level0.pl_from = ? "
					+ "AND level0.pl_namespace = 0 "
					+ "AND page.page_namespace = 0 "
					+ "AND page.page_title = level0.pl_title)";
		PreparedStatement stmt = conn.prepareStatement(query);
		stmt.setInt(1, pageId);
		ResultSet results = stmt.executeQuery();
		while (results.next()) {
			DbResultMap map = new DbResultMap();
			map.put("id", results.getInt("page_id"));
			map.put("page_title", results.getString("page_title"));
			nodes.add(map);
		}
		return nodes;
	}
}
