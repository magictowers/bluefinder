package evals;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import pia.PIAConfigurationBuilder;
import strategies.IGeneralization;

import utils.DBInterface;
import utils.PathsResolver;

public class GiniIndex {
	
	private List<String> pathsSample;
	private final String NORMALIZED_STAR_PATH_TABLE;
	private final String STARPATH_SUFFIX = "_starpath";
	private final String separator = ", ";
	private DBInterface dbInterface = new DBInterface();

	public GiniIndex() {
		this.pathsSample = new ArrayList<String>();
		NORMALIZED_STAR_PATH_TABLE = "V_Normalized" + STARPATH_SUFFIX;
	}

	public GiniIndex(String pathsTableName, boolean makeStarPath) throws ClassNotFoundException, SQLException  {
		this.pathsSample = new ArrayList<String>();
		if (makeStarPath) {
			NORMALIZED_STAR_PATH_TABLE = pathsTableName + STARPATH_SUFFIX;
			try {
				int totalStarPaths = this.bulkGeneralizer(pathsTableName);
				System.out.println("Sample table: " + NORMALIZED_STAR_PATH_TABLE + ", with " + totalStarPaths + " paths.");
			} catch (ClassNotFoundException e) {
				System.err.println("Couldn't create the table with star paths.");
				throw new ClassNotFoundException();
			} catch (SQLException e) {
				throw new SQLException();
			}
		} else {
			NORMALIZED_STAR_PATH_TABLE = pathsTableName;
		}
	}
	
	/**
	 * Populate a list with star paths from {@link #NORMALIZED_STAR_PATH_TABLE} to analyze.
	 * (V_Normalized)
	 * 
	 * @param limit to limit number of items to analyze
	 * @param offset from where to analyze
	 * @throws SQLException 
	 * @throws ClassNotFoundException 
	 */
	public void setPathsSample(int limit, int offset) throws SQLException, ClassNotFoundException {
		try {
			Map<Integer, String> paths = this.dbInterface.getNormalizedPaths(NORMALIZED_STAR_PATH_TABLE, limit, offset);
			for (int id : paths.keySet()) {
				String path = paths.get(id);
				this.pathsSample.add(path);
			}
		} catch (SQLException e) {
			throw new SQLException();
		} catch (ClassNotFoundException e) {
			throw new ClassNotFoundException();
		}
	}

	public float getGiniIndexFor(String table, int k, int maxRecomm, int limit, int offset) {
		Map<String, Float> pi = this.getPiFor(table, k, maxRecomm, limit, offset);
		Set<String> piKeys = pi.keySet();
		int n = this.pathsSample.size();
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
	
	public Map<Integer, Float> getGiniIndex(String table, int maxRecomm) {
		Map<Integer, Float> indexes = new HashMap<Integer, Float>();
		for (int k = 1; k <= 10; k++) {
			float index = this.getGiniIndexFor(table, k, maxRecomm, -1, 0);
			indexes.put(k, index);
		}
		return indexes;
	}
	
	public Map<String, Float> getPiFor(String table, int k, int maxRecomm, int limit, int offset) {
		List<String> pathsForK = this.setPathsForNeighbour(table, k, maxRecomm, limit, offset);
		Map<String, Float> pi = new TreeMap<String, Float>(); 
		for (String pathToAnalyze : this.getPathsSample()) {
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
	private int bulkGeneralizer(String normalizedPathTable) throws ClassNotFoundException, SQLException {
		int totalStarPaths = 0;
		try {
			this.dbInterface.createNormalizedPathTable(NORMALIZED_STAR_PATH_TABLE);
			
            IGeneralization generalizator = PIAConfigurationBuilder.getGeneralizator();
			Set<String> starPaths = new HashSet<String>();
			Map<Integer, String> paths = this.dbInterface.getNormalizedPaths(normalizedPathTable, -1, -1);
			for (int id : paths.keySet()) {
				String starPath = generalizator.generalizePathQuery(paths.get(id));
				starPaths.add(starPath);
			}
			this.dbInterface.addToNormalizedPathTable(NORMALIZED_STAR_PATH_TABLE, starPaths);
			totalStarPaths = starPaths.size();
		} catch (SQLException e1) {
			e1.printStackTrace();
			throw new SQLException();
		}
		return totalStarPaths;
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
			Map<Integer, Map<String, String>> evals = this.dbInterface.getEvaluations(table, limit, offset);
			PathsResolver decoupler = new PathsResolver(separator);
			for (int id : evals.keySet()) {
				Map<String, String> eval = evals.get(id);
				String strPaths = eval.get(k + "path");
				List<String> decoupled = decoupler.simpleDecoupledPaths(strPaths);
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
	
	public List<String> getPathsSample() {
		return pathsSample;
	}

	public void setPathsToAnalize(List<String> pathsToAnalize) {
		this.pathsSample = pathsToAnalize;
	}
	
	public static void main(String[] args) {
		if (args.length < 3) {
			System.err.println("Expected parameters: <path's table name> <true if has to be starred, false or anything if not> <evaluation's table name> <number of max recommendations, -1 if all of them>");
			System.exit(255);
		}
		String pathsTable = args[0];
		boolean makeStarPath = Boolean.valueOf(args[1]);
		String evalTable = args[2];
		int maxRecomm;
		try {
			maxRecomm = Integer.parseInt(args[3]);
		} catch (NumberFormatException ex) {
			System.err.println("Invalid number of recommendation, set to default (-1, all of them).");
			maxRecomm = -1;
		} catch (ArrayIndexOutOfBoundsException ex) {
			System.err.println("Number of recommendations not provided, set to default (-1, all of them).");
			maxRecomm = -1;
		}

		System.out.println("Evaluation's table: " + evalTable);
		Long startTime = System.currentTimeMillis();
		Map<Integer, Float> indexes = new HashMap<Integer, Float>();
		try {
			GiniIndex analyzer = new GiniIndex(pathsTable, makeStarPath);
			analyzer.setPathsSample(-1, -1);
			indexes = analyzer.getGiniIndex(evalTable, maxRecomm);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			System.err.println("Unable to create the connection. Maybe mysql lib is missing.");
		} catch (SQLException e) {
			e.printStackTrace();
			System.err.println("Unable to create the connection.");
		}
		if (indexes.size() > 0) {
			String newLineMark = System.getProperty("line.separator");
			String leftAlignFormat = "| %-12s | %-9f | %-9f | %-9f | %-9f | %-9f | %-9f | %-9f | %-9f | %-9f | %-9f |" + newLineMark;
			System.out.format("+--------------+-----------+-----------+-----------+-----------+-----------+-----------+-----------+-----------+-----------+-----------+" + newLineMark);
			System.out.printf("| Gini Index   |   1path   |   2path   |   3path   |   4path   |   5path   |   6path   |   7path   |   8path   |   9path   |  10path   |" + newLineMark);
			System.out.format("+--------------+-----------+-----------+-----------+-----------+-----------+-----------+-----------+-----------+-----------+-----------+" + newLineMark);
			System.out.format(leftAlignFormat, "GI", indexes.get(1), indexes.get(2), indexes.get(3), indexes.get(4), indexes.get(5), indexes.get(6), indexes.get(7), indexes.get(8), indexes.get(9), indexes.get(10));
			System.out.format("+--------------+-----------+-----------+-----------+-----------+-----------+-----------+-----------+-----------+-----------+-----------+" + newLineMark);
		}
		System.out.println(indexes);
		Long endTime = System.currentTimeMillis() - startTime;
		System.out.println("Took " + endTime + " mills.");
	}

}
