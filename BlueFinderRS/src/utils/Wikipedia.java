package utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import db.WikipediaConnector;

public class Wikipedia {

	public static boolean categoryExists(String fullCategory) throws ClassNotFoundException, SQLException {
		boolean exists = false;
		String strQuery = "SELECT COUNT(*) FROM page WHERE page_namespace = ? AND page_title = ?";
		Connection wikiConn = WikipediaConnector.getConnection();
		PreparedStatement stmt = wikiConn.prepareStatement(strQuery);
		stmt.setInt(1, 14);
		stmt.setString(2, fullCategory);
		ResultSet results = stmt.executeQuery();
		if (results.next()) {
			int count = results.getInt(1);
			if (count > 0) {
				exists = true;
			}
		}
		return exists;
	}
}
