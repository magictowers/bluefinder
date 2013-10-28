package db.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import db.WikipediaConnector;

public class ResultsDbInterface {

	public List<DbResultMap> getNfpc() throws SQLException, ClassNotFoundException {
		String query = "SELECT id, v_from, u_to FROM NFPC";
		Connection conn = WikipediaConnector.getResultsConnection();
		PreparedStatement stmt = conn.prepareStatement(query);
		ResultSet results = stmt.executeQuery();
		List<DbResultMap> dbmaps = new ArrayList<DbResultMap>();
		while (results.next()) {
			DbResultMap map = new DbResultMap();
			map.put("id", results.getInt("id"));
			map.put("v_from", results.getString("v_from"));
			map.put("v_to", results.getString("v_to"));
			dbmaps.add(map);
		}
		return dbmaps;
	}
	
	
}
