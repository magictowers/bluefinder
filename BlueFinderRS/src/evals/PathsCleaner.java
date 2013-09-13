package evals;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import utils.DBInterface;
import utils.FromToPair;
import utils.PathsResolver;
import utils.ProgressCounter;
import utils.Wikipedia;

public class PathsCleaner {

	public final String SUFFIX;
	private Map<Integer, List<String>> pathsToAnalyze;
	private FromToPair pair;
	private DBInterface dbInterface;
	private String relevantPaths;
	private final String separator;

	public PathsCleaner() {
		this.pathsToAnalyze = new HashMap<Integer, List<String>>();
		this.pair = new FromToPair();
		SUFFIX = "_clean";
		this.dbInterface = new DBInterface();
		this.separator = ", ";
	}
	
	public PathsCleaner(String suffix) {
		this.pathsToAnalyze = new HashMap<Integer, List<String>>();
		this.pair = new FromToPair();
		SUFFIX = "_"+suffix;
		this.separator = ", ";
		this.dbInterface = new DBInterface(this.separator);
	}
	
	public PathsCleaner(String suffix, String separator) {
		this.pathsToAnalyze = new HashMap<Integer, List<String>>();
		this.pair = new FromToPair();
		SUFFIX = "_"+suffix;
		this.separator = separator;
		this.dbInterface = new DBInterface(this.separator);
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
	 * Save the valid paths.
	 * 
	 * @param tableName
	 * @param evalId
	 * @param validPaths
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	protected void saveEvaluation(String tableName, int evalId, Map<Integer, List<String>> validPaths) throws SQLException, ClassNotFoundException {
		tableName = tableName+SUFFIX;
		this.dbInterface.createClearedEvaluationTable(tableName);
		this.dbInterface.addToClearedEvaluationTable(tableName, evalId, this.pair, validPaths, this.relevantPaths);
	}
	
	/**
	 * From `tableName`, takes the row `evalRow`, and save the resource in {@link #pair} and kpaths in {@link #pathsToAnalyze}.
	 * 
	 * @param tableName evaluation's table
	 * @param evalId representing a row in tableName
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	public void setAnalysisCase(String tableName, int evalId) throws SQLException, ClassNotFoundException {
		Map<Integer, Map<String, String>> results = this.dbInterface.getEvaluation(tableName, evalId);
		Map<String, String> eval = results.get(evalId);
		if (eval != null) {
			this.pair.setPair(eval.get("resource"));
			PathsResolver decoupler = new PathsResolver(this.separator);
			for (int k = 1; k <= 10; k++) {
				String paths = eval.get(k+"path");
				this.pathsToAnalyze.put(k, decoupler.simpleDecoupledPaths(paths));
			}
			this.relevantPaths = eval.get("relevantPaths");
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
	public void analyzeEvaluations(String tableName) throws ClassNotFoundException, SQLException {
		PathsResolver decoupler = new PathsResolver(this.separator);
		Map<Integer, Map<String, String>> evals = this.dbInterface.getEvaluations(tableName, -1, -1);
		for (int id : evals.keySet()) {
			Map<String, String> eval = evals.get(id);
			this.pair.setPair(eval.get("resource"));
			for (int k = 1; k <= 10; k++) {
				String paths = eval.get(k+"path");
				this.pathsToAnalyze.put(k, decoupler.simpleDecoupledPaths(paths));
			}
			this.relevantPaths = eval.get("relevantPaths");
			this.analyzeEvaluation(tableName, id);
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
	public void analyzeEvaluation(String tableName, int evalId) throws ClassNotFoundException, SQLException {
		if (this.pair != null) {
			System.out.println("Analyzing eval #" + evalId + ", " + this.pair + "...");
			ProgressCounter progressCounter = new ProgressCounter();
			Map<Integer, List<String>> validPaths = new HashMap<Integer, List<String>>();
			for (int k : this.pathsToAnalyze.keySet()) {
				List<String> analyze = this.pathsToAnalyze.get(k);
				validPaths.put(k, this.getValidPaths(analyze));
				progressCounter.increment();
			}
			this.saveEvaluation(tableName, evalId, validPaths);
			int total = 0;
			int subtotal = 0;
			for (int k = 1; k <= 10; k++) {
				total += this.pathsToAnalyze.get(k).size();
			}
			for (int k = 1; k <= 10; k++) {
				subtotal += validPaths.get(k).size();
			}
			System.out.println(total + " paths analyzed, " + subtotal + " saved.\n");
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
		PathsCleaner pathsCleaner = new PathsCleaner();
		String tableName = args[0];		
		long startTime = System.currentTimeMillis();

		if (argsLength >= 2) {
			for (int evalIdPos = 1; evalIdPos < args.length; evalIdPos++) {
				int evalId = 1;
				try {
					evalId = Integer.parseInt(args[evalIdPos]);
				} catch (NumberFormatException e) {				
					System.err.println("Invalid evaluation ID, set to default (1).");
				}
				pathsCleaner.setAnalysisCase(tableName, evalId);
				pathsCleaner.analyzeEvaluation(tableName, evalId);
			}			
		} else {
			pathsCleaner.analyzeEvaluations(tableName);
		}

		long endTime = System.currentTimeMillis();
		System.out.println("Took " + (endTime - startTime) + " millis.");
	}

}
