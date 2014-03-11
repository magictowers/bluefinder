package db.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import db.WikipediaConnector;
import db.WikipediaLanguageConnector;
import java.util.HashSet;
import java.util.Set;

public class ResultsDbInterface {
    
    private String langCode = "";
    
    public ResultsDbInterface() {}
    
    public ResultsDbInterface(String langCode) {
        this.langCode = langCode;
    }
    
    public void setLangCode(String langCode) {
        this.langCode = langCode;
    }
    
    private Connection getConnection() throws ClassNotFoundException, SQLException {
        return WikipediaLanguageConnector.getResultsConnection(this.langCode);
    }

	public List<DbResultMap> getNotFoundPaths() throws SQLException, ClassNotFoundException {
		String query = "SELECT id, v_from, u_to FROM NFPC";
		Connection conn = WikipediaConnector.getResultsConnection();
		PreparedStatement stmt = conn.prepareStatement(query);
		ResultSet results = stmt.executeQuery();
		List<DbResultMap> dbmaps = new ArrayList<DbResultMap>();
		while (results.next()) {
			DbResultMap map = new DbResultMap();
			map.put("id", results.getInt("id"));
			map.put("v_from", results.getString("v_from"));
			map.put("u_to", results.getString("u_to"));
			dbmaps.add(map);
		}
		stmt.close();
		return dbmaps;
	}

	public List<DbResultMap> getNotFoundPaths(int limit, int offset) throws SQLException, ClassNotFoundException {
		String query = "SELECT id, v_from, u_to FROM NFPC LIMIT " + String.valueOf(limit) + " OFFSET " + String.valueOf(offset);
		Connection conn = WikipediaConnector.getResultsConnection();
		PreparedStatement stmt = conn.prepareStatement(query);
		ResultSet results = stmt.executeQuery();
		List<DbResultMap> dbmaps = new ArrayList<DbResultMap>();
		while (results.next()) {
			DbResultMap map = new DbResultMap();
			map.put("id", results.getInt("id"));
			map.put("v_from", results.getString("v_from"));
			map.put("u_to", results.getString("u_to"));
			dbmaps.add(map);
		}
		stmt.close();
		return dbmaps;
	}
	
	public Integer getNormalizedPathId(String path) throws ClassNotFoundException, SQLException {
		int id = 0;
		Connection conn = WikipediaConnector.getResultsConnection();
		String query = "SELECT id FROM V_Normalized where path = ?";
		PreparedStatement stmt = conn.prepareStatement(query);
		stmt.setString(1, path);
		ResultSet results = stmt.executeQuery();
		if (results.next()) {
			id = results.getInt("id");
		}
		stmt.close();
		return id;
	}
	
	public void saveNormalizedPath(String path) throws SQLException, ClassNotFoundException {
		Connection conn = WikipediaConnector.getResultsConnection();
		String query = "INSERT INTO V_Normalized (path) VALUES (?)";
		PreparedStatement stmt = conn.prepareStatement(query);
		stmt.setString(1, path);
		stmt.executeUpdate();
		stmt.close();
	}

	public Integer getTupleId(String tuple) throws ClassNotFoundException, SQLException {
		int id = 0;
		Connection c = WikipediaConnector.getResultsConnection();
        String query = "SELECT id FROM U_page where page = ?";
        PreparedStatement pst = c.prepareStatement(query);
        pst.setString(1, tuple);
        ResultSet rs = pst.executeQuery();
        if (rs.next()) {
            id = rs.getInt("id");
        }
		return id;
	}
	
	public void saveTuple(String tuple) throws ClassNotFoundException, SQLException {
		Connection conn = WikipediaConnector.getResultsConnection();
        String query = "INSERT INTO U_page (page) VALUES (?)";
        PreparedStatement preparedStatement = conn.prepareStatement(query);
        preparedStatement.setString(1, tuple);
        preparedStatement.executeUpdate();
        preparedStatement.close();
	}
	
	public List<DbResultMap> getTuples() throws ClassNotFoundException, SQLException {
		List<DbResultMap> tuples = new ArrayList<DbResultMap>();
		String query = "SELECT distinct up.page FROM U_page up";        
        Connection conn = WikipediaConnector.getResultsConnection();
        Statement st = conn.createStatement();
        ResultSet results = st.executeQuery(query);
        while (results.next()) {
        	DbResultMap map = new DbResultMap();
        	map.put("id", results.getInt("id"));
        	map.put("tuple", results.getString("page"));
        	tuples.add(map);
        }
        
		return tuples;
	}
	
	public void saveEdge(int pathId, int tupleId) throws ClassNotFoundException, SQLException {
		Connection c = WikipediaConnector.getResultsConnection();
        String query = "INSERT INTO UxV (u_from, v_to) VALUES (?, ?)";
        PreparedStatement st = c.prepareStatement(query);
        st.setInt(1, tupleId);
        st.setInt(2, pathId);
        st.executeUpdate();
        st.close();
	}
	
	public void saveEdge(int pathId, int tupleId, String description) throws ClassNotFoundException, SQLException {
		Connection c = WikipediaConnector.getResultsConnection();
        String query = "INSERT INTO UxV (u_from, v_to, description) VALUES (?, ?, ?)";
        PreparedStatement st = c.prepareStatement(query);
        st.setInt(1, tupleId);
        st.setInt(2, pathId);
        st.setString(3, description);
        st.executeUpdate();
        st.close();
	}
	
	public void saveNotFoundPath(String from, String to) throws ClassNotFoundException, SQLException {
		String query = "INSERT INTO NFPC (v_from,u_to) VALUES (?, ?)";
        PreparedStatement pre = WikipediaConnector.getResultsConnection().prepareStatement(query);
        pre.setString(1, from);
        pre.setString(2, to);
        pre.executeUpdate();
        pre.close();
	}
	
	public void removeNotFoundPath(int id) throws ClassNotFoundException, SQLException {
		Connection c = WikipediaConnector.getResultsConnection();
		String query = "DELETE FROM NFPC where id = ?";
		PreparedStatement st = c.prepareStatement(query);
		st.setInt(1, id);
		st.executeUpdate(query);
	}

    public Set<String> getNormalizedPaths(String tuple) throws ClassNotFoundException, SQLException {
        Set<String> paths = new HashSet<String>();
        Integer tupleId = this.getTupleId(tuple);
        Connection conn = this.getConnection();
        String query = ""
                + "SELECT p.path AS path FROM UxV uv INNER JOIN V_Normalized p ON uv.v_to = p.id"
                + " WHERE uv.u_from = ?";
        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setInt(1, tupleId);
        
        ResultSet results = stmt.executeQuery();
        while (results.next()) {
        	paths.add(results.getString("path"));
        }
        stmt.close();
        return paths;
    }
}
