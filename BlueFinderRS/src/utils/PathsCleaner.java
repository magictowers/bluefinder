package utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import db.WikipediaConnector;

public class PathsCleaner {

	private List<String> oldPathsToAnalyze;
	private Map<Integer, List<String>> pathsToAnalyze;
	private FromToPair pair;
	private final String SUFFIX;

	public PathsCleaner() {
		this.oldPathsToAnalyze = new ArrayList<String>();
		this.pathsToAnalyze = new HashMap<Integer, List<String>>();
		this.pair = new FromToPair();
		SUFFIX = "_clean";
	}
	
	public PathsCleaner(String suffix) {
		this.oldPathsToAnalyze = new ArrayList<String>();
		this.pathsToAnalyze = new HashMap<Integer, List<String>>();
		this.pair = new FromToPair();
		SUFFIX = "_"+suffix;
	}
	
	private void saveValidPaths(String tableName, int evalId, Map<Integer, List<String>> validPaths) throws SQLException, ClassNotFoundException {
		tableName = tableName+SUFFIX;
		Connection conn = WikipediaConnector.getResultsConnection();
		conn.createStatement().executeUpdate("DROP TABLE IF EXISTS `"+tableName+"`");
		conn.createStatement().executeUpdate(
				"CREATE TABLE `"+tableName+"` ("
				+ "`id` int(11) NOT NULL AUTO_INCREMENT,"
				+ "`resource` blob,"
				+ "`1path` text,"
				+ "`2path` text,"
				+ "`3path` text,"
				+ "`4path` text,"
				+ "`5path` text,"
				+ "`6path` text,"
				+ "`7path` text,"
				+ "`8path` text,"
				+ "`9path` text,"
				+ "`10path` text,"
				+ "PRIMARY KEY (`id`)"
				+ ") ENGINE=InnoDB AUTO_INCREMENT=102 DEFAULT CHARSET=utf8"
		);
		PathsResolver pathResolver = new PathsResolver(); 
		PreparedStatement stmt = conn.prepareStatement("INSERT INTO "+tableName+" VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
		stmt.setInt(1, evalId);
		stmt.setString(2, this.pair.getConcatPair());
		for (int k : validPaths.keySet()) {
			List<String> path = validPaths.get(k);
			stmt.setString(k + 2, pathResolver.simpleDoupledPaths(path));
		}
		stmt.execute();
	}
	
	/**
	 * From `tableName`, takes the row `evalRow`, and save the resource in {@link #pair} and kpaths in {@link #pathsToAnalyze}.
	 * 
	 * @param tableName evaluation's table
	 * @param evalId representing a row in tableName
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	private void setAnalysisCase(String tableName, int evalId, String separator) throws SQLException, ClassNotFoundException {
		String strQuery = "SELECT * FROM "+tableName+" WHERE id = ?";
		Connection conn = WikipediaConnector.getResultsConnection();
		PreparedStatement stmt = conn.prepareStatement(strQuery);
		stmt.setInt(1, evalId);
		ResultSet results = stmt.executeQuery();
		if (results.next()) {
			PathsResolver decoupler = new PathsResolver(separator);
			this.pair.split(results.getString("resource"));
			for (int k = 1; k <= 10; k++) {
				String paths = results.getString(k+"path");
				this.pathsToAnalyze.put(k, decoupler.simpleDecoupledPaths(paths));
			}
		} else {
			this.pair = null;
		}
	}
	
	/**
	 * Save valid paths in `validPathsTable`. Valid paths references existing Wikipedia categories.
	 * 
	 * @param validPathsTable
	 * @param resourcesTable
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public void saveValidPaths(String tableName, int evalId, String separator) throws ClassNotFoundException, SQLException {
		this.setAnalysisCase(tableName, evalId, separator);
		if (this.pair != null) {
			Map<Integer, List<String>> validPaths = new HashMap<Integer, List<String>>();
			for (int k : this.pathsToAnalyze.keySet()) {
				List<String> analyze = this.pathsToAnalyze.get(k);
				validPaths.put(k, this.getValidPaths(analyze));
				
			}
			this.saveValidPaths(tableName, evalId, validPaths);
		} else {
			System.err.println("The given ID does not exist.");
		}
	}
	
	/**
	 * See which element from {@link #oldPathsToAnalyze} is valid according to `pair`.
	 * 
	 * @param pair
	 * @return a sublist from {@link #oldPathsToAnalyze}
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	private List<String> getValidPaths(List<String> paths) throws ClassNotFoundException, SQLException {
		List<String> validPaths = new ArrayList<String>();
		for (String path : paths) {
			boolean isValid = true;
			int categoryIndex = path.indexOf("Cat:");
			if (categoryIndex > -1) {
				String categoryPage = path.substring(categoryIndex);
				categoryPage = categoryPage.substring(0, categoryPage.indexOf(" / "));
				if (this.pair.pathHasWildCards(categoryPage)) {
					String fullCategoryPage = this.pair.generateFullPath(categoryPage).replace("Cat:", "");
					isValid = this.categoryExists(fullCategoryPage);
				}
			}
			if (isValid) {
				validPaths.add(path);
			}
		}		
		return validPaths;
	}
	
	private boolean categoryExists(String fullCategory) throws ClassNotFoundException, SQLException {
		boolean exists = false;
		String strQuery = "SELECT COUNT(*) FROM page WHERE page_namespace = ? AND page_title = ?";
		Connection wikiConn = WikipediaConnector.getConnection();
		PreparedStatement stmt = wikiConn.prepareStatement(strQuery);
		stmt.setInt(1, 14);
		stmt.setString(2, fullCategory);
		ResultSet results = stmt.executeQuery();
		while (results.next()) {
			int count = results.getInt(1);
			if (count > 0) {
				System.out.println(count + "  " +stmt.toString());
				exists = true;
			}
		}
		stmt.toString();
		return exists;
	}
	
	/**
	 * Insert into `page` table, previously created, real full pages to analyze (reduced set).
	 * 
	 * @param limit
	 * @param offset
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	public void partialLoader(int limit, int offset) throws SQLException, ClassNotFoundException {
		Connection connWiki = WikipediaConnector.getConnection();
		Connection connDbresearch = WikipediaConnector.getResultsConnection();
		connDbresearch.setAutoCommit(false);
		try {
			String strQuery = "SELECT * FROM page WHERE page_namespace IN (14)";
			if (limit > 0) {
				strQuery += " LIMIT " + limit;
				if (offset > 0) {
					strQuery += " OFFSET " + offset;
				}
			}
			PreparedStatement stmt = connWiki.prepareStatement(strQuery);
			PreparedStatement insertStmt = connDbresearch.prepareStatement("INSERT INTO page(page_namespace, page_title, page_restrictions) VALUES(?, ?, 0)");
			ResultSet results = stmt.executeQuery();
			while (results.next()) {
				String namespace = results.getString("page_namespace");
				String title = results.getString("page_title");
				insertStmt.setString(1, namespace);
				insertStmt.setString(2, title);
				insertStmt.addBatch();
			}
			insertStmt.executeBatch();
			connDbresearch.commit();
		} catch (SQLException e) {
			connDbresearch.rollback();
			e.printStackTrace();
		}
	}
		
	public List<String> getPathsToAnalyze() {
		return oldPathsToAnalyze;
	}

	public void setPathsToAnalyze(List<String> pathsToAnalyze) {
		this.oldPathsToAnalyze = pathsToAnalyze;
	}
	
	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		int argsLength = args.length;
		if (argsLength < 2) {
			System.err.println("Arguments: <evaluation table> <evaluation row ID> [<path separator>]");
			System.exit(255);
		}
		PathsCleaner pathsCleaner = new PathsCleaner();
//		pathsCleaner.partialLoader(1000, 2000);
		String tableName = args[0];
		String strEvalId = args[1];
		String separator = ", ";
		if (argsLength > 2) {
			separator = args[2];
		}
		int evalId = 1;
		try {
			evalId = Integer.parseInt(strEvalId);
		} catch (NumberFormatException ex) {
			System.err.println("Invalid evaluation ID, set to default (1).");
		}
		long startTime = System.currentTimeMillis();
		pathsCleaner.saveValidPaths(tableName, evalId, separator);
		long endTime = System.currentTimeMillis();
		System.out.println("Took " + (endTime - startTime) + " millis.");
	}

}
