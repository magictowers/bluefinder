package evals;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import strategies.LastCategoryGeneralization;
import utils.PathsResolver;
import db.WikipediaConnector;

public class AppearancesAnalyzer {
	
	private List<String> pathsToAnalize;
	private final String SAMPLE_TABLE_NAME = "V_Normalized_Generalized";
	private final String TABLE_NAME = "V_Normalized";

	public AppearancesAnalyzer() {
		this.pathsToAnalize = new ArrayList<String>();
	}
	
	/**
	 * Populate a list with star paths from `SAMPLE_TABLE_NAME` to analyze
	 * 
	 * @param limit to limit number of items to analyze
	 * @param offset from where to analyze
	 */
	public void setAnalysisSample(int limit, int offset) {
		try {
			Connection conn = WikipediaConnector.getResultsConnection();
			PreparedStatement stmt = conn.prepareStatement(this.getStrQuery(SAMPLE_TABLE_NAME, limit, offset));
			ResultSet results = stmt.executeQuery();
			while (results.next()) {
				String path = results.getString("path");
				this.pathsToAnalize.add(path);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public float getGiniIndexFor(String table, int k, int maxRecomm, int limit, int offset) {
		Map<String, Float> pi = this.getPiFor(table, k, maxRecomm, limit, offset);
		Set<String> piKeys = pi.keySet();
		int n = this.pathsToAnalize.size();
		float summation = 0;
		int j = 1;
		for (String key : piKeys) {
			float pij = pi.get(key);
			float mathExpression = 2*j - n - 1;
			mathExpression *= pij;
			summation += mathExpression;
			j++;
		}
		return (1.0f / ((float)n - 1.0f)) * summation;
	}
	
	public Map<String, Float> getPiFor(String table, int k, int maxRecomm, int limit, int offset) {
		List<String> pathsForK = this.setPathsForNeighbour(table, k, maxRecomm, limit, offset);
		Map<String, Float> pi = new TreeMap<String, Float>(); 
		for (String pathToAnalyze : this.getPathsToAnalyze()) {
			float frequency = Collections.frequency(pathsForK, pathToAnalyze);
			pi.put(pathToAnalyze, frequency/pathsForK.size());
		}
		pi = this.sortPi(pi);
		return pi;
	}
	
	protected Map<String, Float> sortPi(Map<String, Float> unsortedPi) {
		MapSorter mapSorter = new MapSorter(unsortedPi);
		Map<String, Float> pi = new TreeMap<String, Float>(mapSorter);
		pi.putAll(unsortedPi);
		return pi;
	}

	/**
	 * Take unstarred paths from `TABLE_NAME` and save them in `SAMPLE_TABLE_NAME` as star paths
	 * 
	 * @param limit for `TABLE_NAME`
	 * @param offset for `TABLE_NAME`
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	public void bulkGeneralizer(int limit, int offset) throws SQLException, ClassNotFoundException {
		Connection conn = WikipediaConnector.getResultsConnection();
		conn.createStatement().executeUpdate("DROP TABLE IF EXISTS `"+SAMPLE_TABLE_NAME+"`");
		conn.createStatement().executeUpdate(
				"CREATE TABLE `"+SAMPLE_TABLE_NAME+"` ("
				+ "`id` int(3) NOT NULL AUTO_INCREMENT,"
				+ "`path` longtext NOT NULL,"
				+ "PRIMARY KEY (`id`),"
				+ "KEY `path` (`path`(100)) USING BTREE"
				+ ") ENGINE=MyISAM AUTO_INCREMENT=1 DEFAULT CHARSET=utf8"
		);
		conn.setAutoCommit(false);
		try {
			PreparedStatement stmt = conn.prepareStatement(this.getStrQuery(TABLE_NAME, limit, offset));
			ResultSet results = stmt.executeQuery();
			LastCategoryGeneralization generalizator = new LastCategoryGeneralization();
			Set<String> starPaths = new HashSet<String>();
			while (results.next()) {
				String starPath = generalizator.generalizePathQuery(results.getString("path"));
				starPaths.add(starPath);				
			}
			PreparedStatement insertStmt = conn.prepareStatement("INSERT INTO "+SAMPLE_TABLE_NAME+"(path) VALUES (?)");
			for (String starPath : starPaths) {
				insertStmt.setString(1, starPath);
				insertStmt.addBatch();
			}
			insertStmt.executeBatch();
			conn.commit();
		} catch (SQLException e) {
			conn.rollback();
			e.printStackTrace();
		}
	}
	
	/**
	 * Create a select sentence
	 * 
	 * @param table
	 * @param limit
	 * @param offset
	 * @return String representating a select sentence
	 */
	private String getStrQuery(String table, int limit, int offset) {
		String strQuery = "SELECT * FROM " + table;
		if (limit > 0) {
			strQuery += " LIMIT " + limit;
			if (offset > 0) {
				strQuery += " OFFSET " + offset;
			}
		}
		return strQuery;
	}
	
	/**
	 * Return a list of `k`-path from `table`
	 * 
	 * @param table name for the evaluation's results
	 * @param k representing the path
	 * @param maxRecomm
	 * @param limit
	 * @param offset
	 * @return a list with k-paths
	 */	
	protected List<String> setPathsForNeighbour(String table, int k, int maxRecomm, int limit, int offset) {
		List<String> pathsForK = new ArrayList<String>();
		try {
			Connection conn = WikipediaConnector.getResultsConnection();
			PreparedStatement stmt = conn.prepareStatement(this.getStrQuery(table, limit, offset));
			ResultSet results = stmt.executeQuery();
			PathsResolver decoupler = new PathsResolver(", ");
			while (results.next()) {
				String path = results.getString(k + "path");
				List<String> decoupled = decoupler.simpleDecoupledPaths(path);
				if (maxRecomm >= 0 && decoupled.size() > maxRecomm) {
					decoupled = decoupled.subList(0, maxRecomm);
				}
				pathsForK.addAll(decoupled);				
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return pathsForK;
	}
	
	public List<String> getPathsToAnalyze() {
		return pathsToAnalize;
	}

	public void setPathsToAnalize(List<String> pathsToAnalize) {
		this.pathsToAnalize = pathsToAnalize;
	}
	
	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		if (args.length != 2) {
			System.err.println("Expected parameters: <evaluation's table name> <number of max recommendations, -1 if all of them>");
		}
		String evalTable = args[0];
		int maxRecomm = -1;
		maxRecomm = Integer.parseInt(args[1]);
		
		Long startTime = System.currentTimeMillis();
		AppearancesAnalyzer analyzer = new AppearancesAnalyzer();
		analyzer.bulkGeneralizer(-1, 0);
		analyzer.setAnalysisSample(13, 46);
		List<Float> indexes = new ArrayList<Float>();
		for (int i = 1; i <= 10; i++) {
			float giniIndex = analyzer.getGiniIndexFor(evalTable, i, maxRecomm, -1, 0);
			indexes.add(giniIndex);
		}

		String newLineMark = System.getProperty("line.separator");
		String leftAlignFormat = "| %-12s | %-9f | %-9f | %-9f | %-9f | %-9f | %-9f | %-9f | %-9f | %-9f | %-9f |" + newLineMark;
		System.out.format("+--------------+-----------+-----------+-----------+-----------+-----------+-----------+-----------+-----------+-----------+-----------+" + newLineMark);
		System.out.printf("| Gini Index   |   1path   |   2path   |   3path   |   4path   |   5path   |   6path   |   7path   |   8path   |   9path   |  10path   |" + newLineMark);
		System.out.format("+--------------+-----------+-----------+-----------+-----------+-----------+-----------+-----------+-----------+-----------+-----------+" + newLineMark);
		System.out.format(leftAlignFormat, "GI", indexes.get(0), indexes.get(1), indexes.get(2), indexes.get(3), indexes.get(4), indexes.get(5), indexes.get(6), indexes.get(7), indexes.get(8), indexes.get(9));
		System.out.format("+--------------+-----------+-----------+-----------+-----------+-----------+-----------+-----------+-----------+-----------+-----------+" + newLineMark);
		System.out.println(indexes);
		Long endTime = System.currentTimeMillis() - startTime;
		System.out.println("Took " + endTime + " mills.");
	}

}
