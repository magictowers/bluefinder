package db.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import db.WikipediaConnector;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import utils.FromToPair;
import utils.ProjectConfiguration;

public class ResultsDbInterface {
    
    public ResultsDbInterface() {}
    
    private Connection getConnection() throws ClassNotFoundException, SQLException {
        return WikipediaConnector.getResultsConnection();
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
    
    public FromToPair getTranslatedTuple(String from, String to) throws SQLException, ClassNotFoundException {
        FromToPair pair = null;
        String dbpediaLanguagePrefix = ProjectConfiguration.dbpediaLanguagePrefix();
        String queryStr = ""
                + "SELECT CONVERT(fromto_table.from USING utf8) AS fromPage, CONVERT(fromto_table.to USING utf8) AS toPage "
                + "FROM " + ProjectConfiguration.fromToTable() + " AS fromto_table"
                + " WHERE fromTrans = ? AND toTrans = ?";
        WikipediaConnector.closeConnection();
        Connection conn = this.getConnection();
        PreparedStatement stmt = conn.prepareStatement(queryStr);
        stmt.setString(1, dbpediaLanguagePrefix + from);
        stmt.setString(2, dbpediaLanguagePrefix + to);
        ResultSet result = stmt.executeQuery();
        if (result.next()) {
            from = result.getString("fromPage");
            to = result.getString("toPage");
            String dbpediaPrefix = ProjectConfiguration.dbpediaPrefix();
            pair = new FromToPair(from.replace(dbpediaPrefix, ""), to.replace(dbpediaPrefix, ""), ProjectConfiguration.languageCode());
        }
        return pair;
    }
    
    public List<Map<String, String>> getDbpediaTuples(int limit, int offset) throws ClassNotFoundException, SQLException {
        ProjectConfiguration.useProperties1();
        String db1 = ProjectConfiguration.resultDatabase().split("/")[1];
        String fromtoTable1 = ProjectConfiguration.fromToTable();
        String dbpediaPrefix1 = ProjectConfiguration.dbpediaPrefix();
        ProjectConfiguration.useProperties2();
        String db2 = ProjectConfiguration.resultDatabase().split("/")[1];
        String fromtoTable2 = ProjectConfiguration.fromToTable();
        String dbpediaPrefix2 = ProjectConfiguration.dbpediaPrefix();
        
        Connection conn = WikipediaConnector.getResultsConnection();
        
        String strQuery = ""
                + "SELECT CONVERT(t1.from USING utf8) AS from1, CONVERT(t2.from USING utf8) AS from2, "
                    + "CONVERT(t1.to USING utf8) AS to1, CONVERT(t2.to USING utf8) AS to2 "
                + "FROM " + db1 + "." + fromtoTable1 + " t1 INNER JOIN " + db2 + "." + fromtoTable2 + " t2 ON "
                + "(t1.fromTrans = t2.fromTrans AND t1.toTrans = t2.toTrans)";
        if (limit != 0) {
            strQuery += " LIMIT " + limit + " OFFSET " + offset;
        }
        PreparedStatement stmt = conn.prepareStatement(strQuery);
        ResultSet results = stmt.executeQuery();
        List<Map<String, String>> dbpediaTuples = new ArrayList<Map<String, String>>();
        while (results.next()) {
            Map<String, String> transTuple = new HashMap<String, String>();
            transTuple.put("from1", results.getString("from1").replace(dbpediaPrefix1, ""));
            transTuple.put("to1", results.getString("to1").replace(dbpediaPrefix1, ""));
            
            transTuple.put("from2", results.getString("from2").replace(dbpediaPrefix2, ""));
            transTuple.put("to2", results.getString("to2").replace(dbpediaPrefix2, ""));
            
            dbpediaTuples.add(transTuple);
        }        
        ProjectConfiguration.useDefaultProperties();
        return dbpediaTuples;
    }

    public List<Map<String, String>> getDbpediaTuples() throws ClassNotFoundException, SQLException {
        return this.getDbpediaTuples(0, 0);
    }
}
