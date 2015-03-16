package db.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import pia.PathFinder;
import utils.ProjectSetup;
import db.DBConnector;

/**
 * Clase encargada de interactuar con la base de Wikipedia.
 * La idea es que todas las consultas estén encapsuladas acá.
 * 
 * @author mkaminose
 */
public class WikipediaDbInterface {
    
    private Connection connection;
    private DBConnector connector;
    private ProjectSetup projectSetup;

    private List<String> blacklistCategory;
    protected boolean translate;

    
    public WikipediaDbInterface(ProjectSetup projectSetup, DBConnector connector){
    	this.setProjectSetup(projectSetup);
    	this.blacklistCategory = projectSetup.getBlacklist();
    	this.translate = projectSetup.isTranslate();
    	this.connector = connector;
    }
	
	/**
	 * Get the id for the given category.
	 * 
	 * @param category
	 * @return an ID, or 0.
	 * @throws ClassNotFoundException
	 * @throws SQLException 
	 */
	public Integer getCategoryId(String category) throws ClassNotFoundException, SQLException {
		return this.getIdFor(category, 14);
	}
	
	/**
	 * Get the id for the given page.
	 * 
	 * @param page
	 * @return an ID, or 0
	 * @throws ClassNotFoundException
	 * @throws SQLException 
	 */
	public Integer getPageId(String page) throws ClassNotFoundException, SQLException {
		return this.getIdFor(page, 0);
	}
    
    /**
     * Given a page in a language, returns its translated name, the language is determined
     * by the parameter LANGUAGE_CODE in the setup file.
     * 
     * @param page
     * @return translated page or the same one
     * @throws ClassNotFoundException 
     * @throws SQLException 
     */
    public String getTranslatedPage(String page) throws ClassNotFoundException, SQLException {
        return page;
    }
	
	/**
	 * Get the id for the given resource and namespace.
	 * 
	 * @param resource
	 * @param namespace
	 * @return an ID, or 0.
	 * @throws ClassNotFoundException
	 * @throws SQLException 
	 */
	public Integer getIdFor(String resource, int namespace) throws ClassNotFoundException, SQLException {
		int id = 0;
        Connection c = getConnection();
        String query = "SELECT page_id FROM page WHERE page_namespace = ? AND page_title = ?";
        PreparedStatement stmt = c.prepareStatement(query);
        stmt.setInt(1, namespace);
        stmt.setString(2, resource);
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            id = rs.getInt("page_id");
        }
        stmt.close();
		return id;
	}

	public boolean isInCategoryBlackList(String category) {
		boolean isBlackList = false;
		if (this.blacklistCategory.contains(category)) {
			isBlackList = true;
		} else {
			for (int i = 0; i < this.blacklistCategory.size() && !isBlackList; i++) {
				String black = this.blacklistCategory.get(i);
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
		Connection conn = getConnection();
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
			Connection conn = getConnection();
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

	public List<String> getListOf(Integer pageId) throws ClassNotFoundException {
		List<String> items = new ArrayList<String>();
		String query;
        query = ""
                + "SELECT page.page_id AS page_id, CONVERT(page.page_title USING utf8) AS page_title "
                + "FROM pagelinks AS level0 INNER JOIN page ON ("
                    + "level0.pl_from = ? "
                    + "AND level0.pl_namespace = 0 "
                    + "AND page.page_namespace = 0 "
                    + "AND page.page_title = level0.pl_title "
                    + "AND page.page_title LIKE 'List_of_%')";
		try {
			PreparedStatement stmt = getConnection().prepareStatement(query);
			stmt.setInt(1, pageId);
			ResultSet results = stmt.executeQuery();
			while (results.next()) {
				items.add(results.getString("page_title"));
			}
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
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
        Connection conn = getConnection();
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
		Connection conn = getConnection();
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

    /**
     * @return the connection
     * @throws SQLException 
     * @throws ClassNotFoundException 
     */
    public Connection getConnection() throws ClassNotFoundException, SQLException {
        return this.connector.getWikiConnection();
    }

    /**
     * @param connection the connection to set
     */
    public void setConnection(Connection connection) {
        this.connection = connection;
    }

	/**
	 * @return the projectSetup
	 */
	protected ProjectSetup getProjectSetup() {
		return projectSetup;
	}

	/**
	 * @param projectSetup the projectSetup to set
	 */
	protected void setProjectSetup(ProjectSetup projectSetup) {
		this.projectSetup = projectSetup;
	}
}
