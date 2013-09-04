package evals;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import utils.FromToPair;
import utils.PathsResolver;
import utils.Wikipedia;
import db.WikipediaConnector;

public class PathsCleaner {

	private Map<Integer, List<String>> pathsToAnalyze;
	private FromToPair pair;
	private final String SUFFIX;

	public PathsCleaner() {
		this.pathsToAnalyze = new HashMap<Integer, List<String>>();
		this.pair = new FromToPair();
		SUFFIX = "_clean";
	}
	
	public PathsCleaner(String suffix) {
		this.pathsToAnalyze = new HashMap<Integer, List<String>>();
		this.pair = new FromToPair();
		SUFFIX = "_"+suffix;
	}
	
	public FromToPair getPair() {
		return pair;
	}

	public void setPair(FromToPair pair) {
		this.pair = pair;
	}
	
	public Map<Integer, List<String>> getPathsToAnalyze() {
		return pathsToAnalyze;
	}

	public void setPathsToAnalyze(Map<Integer, List<String>> pathsToAnalyze) {
		this.pathsToAnalyze = pathsToAnalyze;
	}
	
	/**
	 * Save into DB the valid paths.
	 * 
	 * @param tableName
	 * @param evalId
	 * @param separator
	 * @param validPaths
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	protected void saveEvaluation(String tableName, int evalId, String separator, Map<Integer, List<String>> validPaths) throws SQLException, ClassNotFoundException {
		tableName = tableName+SUFFIX;
		Connection conn = WikipediaConnector.getResultsConnection();
		conn.createStatement().executeUpdate(
				"CREATE TABLE IF NOT EXISTS `"+tableName+"` ("
				+ "`id` int(11) NOT NULL AUTO_INCREMENT,"
				+ "`eval_id` int(11),"
				+ "`resource` blob,"
				+ "`1path` mediumtext,"
				+ "`2path` mediumtext,"
				+ "`3path` mediumtext,"
				+ "`4path` mediumtext,"
				+ "`5path` mediumtext,"
				+ "`6path` mediumtext,"
				+ "`7path` mediumtext,"
				+ "`8path` mediumtext,"
				+ "`9path` mediumtext,"
				+ "`10path` mediumtext,"
				+ "PRIMARY KEY (`id`)"
				+ ") ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8"
		);
		PathsResolver pathResolver = new PathsResolver(separator); 
		PreparedStatement stmt = conn.prepareStatement("INSERT INTO "+tableName+" VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
		stmt.setNull(1, java.sql.Types.NULL);
		stmt.setInt(2, evalId);
		stmt.setString(3, this.pair.getConcatPair());
		for (int k : validPaths.keySet()) {
			List<String> path = validPaths.get(k);
			stmt.setString(k + 3, pathResolver.simpleCoupledPaths(path));
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
	public void setAnalysisCase(String tableName, int evalId, String separator) throws SQLException, ClassNotFoundException {
		String strQuery = "SELECT * FROM "+tableName+" WHERE id = ?";
		Connection conn = WikipediaConnector.getResultsConnection();
		PreparedStatement stmt = conn.prepareStatement(strQuery);
		stmt.setInt(1, evalId);
		ResultSet results = stmt.executeQuery();
		if (results.next()) {
			PathsResolver decoupler = new PathsResolver(separator);
			this.pair.setPair(results.getString("resource"));
			for (int k = 1; k <= 10; k++) {
				String paths = results.getString(k+"path");
				this.pathsToAnalyze.put(k, decoupler.simpleDecoupledPaths(paths));
			}
		} else {
			this.pair = null;
		}
	}
	
	/**
	 * Analyze all paths within an evaluation in `tableName`. See {@link #analyzeEvaluation(String, int, String)}.
	 * 
	 * @param tableName
	 * @param separator
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public void analyzeEvaluations(String tableName, String separator) throws ClassNotFoundException, SQLException {
		String strQuery = "SELECT * FROM " + tableName;
		Connection conn = WikipediaConnector.getResultsConnection();
		PreparedStatement stmt = conn.prepareStatement(strQuery);
		ResultSet results = stmt.executeQuery();
		while (results.next()) {
			PathsResolver decoupler = new PathsResolver(separator);
			this.pair.setPair(results.getString("resource"));
			int evalId = results.getInt("id");
			for (int k = 1; k <= 10; k++) {
				String paths = results.getString(k+"path");
				this.pathsToAnalyze.put(k, decoupler.simpleDecoupledPaths(paths));
			}
			this.analyzeEvaluation(tableName, evalId, separator);
		}
	}
		
	/**
	 * Analyze paths in {@link #pathsToAnalyze}, to see if they are existing Wikipedia pages or not.
	 * 
	 * @param validPathsTable
	 * @param resourcesTable
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public void analyzeEvaluation(String tableName, int evalId, String separator) throws ClassNotFoundException, SQLException {
		if (this.pair != null) {
			System.out.println("Analyzing eval #" + evalId + ", " + this.pair + "...");
			Map<Integer, List<String>> validPaths = new HashMap<Integer, List<String>>();
			for (int k : this.pathsToAnalyze.keySet()) {
				List<String> analyze = this.pathsToAnalyze.get(k);
				validPaths.put(k, this.getValidPaths(analyze));				
			}
			this.saveEvaluation(tableName, evalId, separator, validPaths);
			int total = 0;
			int subtotal = 0;
			for (int k = 1; k <= 10; k++) {
				total += this.pathsToAnalyze.get(k).size();
			}
			for (int k = 1; k <= 10; k++) {
				subtotal += validPaths.get(k).size();
			}
			System.out.println(total + " paths analyzed, " + subtotal + " saved.");
		} else {
			System.err.println("The given ID does not exist.");
		}
	}
		
	/**
	 * See which element from `paths` is valid using {@link #pair} as reference.
	 * 
	 * @param paths
	 * @return subset of paths
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
					isValid = Wikipedia.categoryExists(fullCategoryPage);
				}
			}
			if (isValid) {
				validPaths.add(path);
			}
		}		
		return validPaths;
	}
	
	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		int argsLength = args.length;
		if (argsLength < 1) {
			System.err.println("Arguments: <evaluation table> [<evaluation row ID_1> <evaluation row ID_2> <evaluation row ID_N>, default all of them]");
			System.exit(255);
		}
		String separator = ", ";
		PathsCleaner pathsCleaner = new PathsCleaner();
		String tableName = args[0];		
		long startTime = System.currentTimeMillis();

		if (argsLength > 2) {
			for (int evalIdPos = 1; evalIdPos < args.length; evalIdPos++) {
				int evalId = 1;
				try {
					evalId = Integer.parseInt(args[evalIdPos]);
				} catch (NumberFormatException e) {				
					System.err.println("Invalid evaluation ID, set to default (1).");
				}
				pathsCleaner.setAnalysisCase(tableName, evalId, separator);
				pathsCleaner.analyzeEvaluation(tableName, evalId, separator);
			}			
		} else {
			pathsCleaner.analyzeEvaluations(tableName, separator);
		}

		long endTime = System.currentTimeMillis();
		System.out.println("Took " + (endTime - startTime) + " millis.");
	}

}
