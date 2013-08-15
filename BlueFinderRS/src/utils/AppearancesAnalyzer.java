package utils;

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
import db.WikipediaConnector;

public class AppearancesAnalyzer {
	
	private List<String> pathsToAnalize;

	public AppearancesAnalyzer() {
		this.pathsToAnalize = new ArrayList<String>();
	}
	
	public void setAnalysisSample(String table, int limit, int offset) {
		try {
			Connection conn = WikipediaConnector.getResultsConnection();
			PreparedStatement stmt = conn.prepareStatement(this.getStrQuery(table, limit, offset));
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

	public float getGiniIndexFor(String table, int k, int limit, int offset) {
		Map<String, Float> pi = this.getPiFor(table, k, limit, offset);
		Set<String> piKeys = pi.keySet();
		int n = this.pathsToAnalize.size();
		float summation = 0;
		int j = 1;
		for (String key : piKeys) {
			float pij = pi.get(key);
			float mathExpression = 2*j - n - 1;
			mathExpression *= pij;
			summation += mathExpression;
//			System.out.println(key + " -> at j=" + j + "   pij="+pij + "   mathExpression="+mathExpression + "   summation="+summation);
			j++;
		}
		return (1.0f / ((float)n - 1.0f)) * summation;
	}
	
	public Map<String, Float> getPiFor(String table, int k, int limit, int offset) {
		List<String> pathsForK = this.setPathsForNeighbour(table, k, limit, offset);
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

	public void bulkGeneralizer(int limit, int offset) throws SQLException, ClassNotFoundException {
		Connection conn = WikipediaConnector.getResultsConnection();
		conn.setAutoCommit(false);
		try {
			System.out.println(this.getStrQuery("V_Normalized", limit, offset));
			PreparedStatement stmt = conn.prepareStatement(this.getStrQuery("V_Normalized", limit, offset));
			ResultSet results = stmt.executeQuery();
			LastCategoryGeneralization generalizator = new LastCategoryGeneralization();
			Set<String> starPaths = new HashSet<String>();
			while (results.next()) {
				String starPath = generalizator.generalizePathQuery(results.getString("path"));
				starPaths.add(starPath);				
			}
			PreparedStatement insertStmt = conn.prepareStatement("INSERT INTO V_Normalized_Generalized(path) VALUES (?)");
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
	
	protected List<String> setPathsForNeighbour(String table, int k, int limit, int offset) {
		List<String> pathsForK = new ArrayList<String>();
		try {
			Connection conn = WikipediaConnector.getResultsConnection();
			PreparedStatement stmt = conn.prepareStatement(this.getStrQuery(table, limit, offset));
			ResultSet results = stmt.executeQuery();
			PathsDecoupler decoupler = new PathsDecoupler(", ");
			while (results.next()) {
				String path = results.getString(k + "path");
				List<String> decoupled = decoupler.simpleDecoupledPaths(path);
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
		Long startTime = System.currentTimeMillis();
		AppearancesAnalyzer analyzer = new AppearancesAnalyzer();
//		analyzer.bulkGeneralizer(-1, 0);
		analyzer.setAnalysisSample("V_Normalized_Generalized", 13, 46);
		float giniIndex = analyzer.getGiniIndexFor("sc1Evaluation_firstpart", 4, -1, 1);
		System.out.println("Gini index: " + giniIndex);
		Long endTime = System.currentTimeMillis() - startTime;
		System.out.println("Took " + endTime + " mills.");
	}

}
