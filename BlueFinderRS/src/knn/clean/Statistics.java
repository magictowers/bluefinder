package knn.clean;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import utils.PathsResolver;
import utils.ProjectSetup;
import db.DBConnector;
import db.PropertiesFileIsNotFoundException;
import db.WikipediaConnector;
import db.utils.ResultsDbInterface;
import evals.GiniIndex;
import pia.deprecated.PIAConfigurationContainer;
import strategies.IGeneralization;

public class Statistics {

	private GiniIndex giniIndexCalculator;
    private ResultsDbInterface resultsDb;
    private DBConnector connector;

	public Statistics(DBConnector connector) {
		this.connector = connector;
		this.giniIndexCalculator = new GiniIndex(connector);
	}

	public Statistics(DBConnector connector, GiniIndex giniIndex) {
		this.connector = connector;
		this.giniIndexCalculator = giniIndex;
	}
    
    public Statistics(DBConnector connector, GiniIndex giniIndex, ResultsDbInterface resultsDb) {
        this(connector, giniIndex);
        this.resultsDb = resultsDb;
    }

	public GiniIndex getGiniIndexCalculator() {
		return giniIndexCalculator;
	}

	public void setGiniIndexCalculator(GiniIndex giniIndexCalculator) {
		this.giniIndexCalculator = giniIndexCalculator;
	}

	public Set<String> getSetOfRelevantPathQueries(String stringOfPathQueries) {
		// "#from / Cat:#from / #to , #from / Cat:Warner_Music_labels / #to";
		String[] paths = stringOfPathQueries.trim().split(" , ");
		List<String> result = Arrays.asList(paths);

		return new HashSet<String>(result);
	}

	public Set<String> getRetrievedPaths(String stringPathQueries) {
		// {#from / #to=1010, #from / * / Cat:ECM_artists / #to=1}
		PathsResolver pathsResolver = new PathsResolver(", ");
		List<String> tmpPaths = pathsResolver
				.simpleDecoupledPaths(stringPathQueries);
		Set<String> results = new HashSet<String>(tmpPaths);
		return results;
	}

	public Set<String> getRetrievedPaths(String stringPathQueries, int limit) {
		// {#from / #to=1010, #from / * / Cat:ECM_artists / #to=1}
		PathsResolver pathsResolver = new PathsResolver(", ");
		List<String> tmpPaths = pathsResolver.simpleDecoupledPaths(stringPathQueries);
		Set<String> results = new HashSet<String>();
		for (int i = 0; i < tmpPaths.size() && results.size() < limit; i++) {
			results.add(tmpPaths.get(i));
		}
		return results;
	}

	public double simplePresicion(ProjectSetup projectSetup, String retrievedPaths, String relevantPaths, int limit) {
		Set<String> retrieved = this.getRetrievedPaths(retrievedPaths, limit);
		Set<String> relevant = this.getSetOfRelevantPathQueries(relevantPaths);
        IGeneralization cg = projectSetup.getGeneralizator();

		Set<String> starRelevant = new HashSet<String>();
		for (String path : relevant) {
			starRelevant.add(cg.generalizePathQuery(path));
		}

		relevant = starRelevant;

		relevant.retainAll(retrieved);
		double rSize = relevant.size();
		double retrievedSize = retrieved.size();

		return rSize / retrievedSize;
	}

	/**
	 * Computes all the statistics for a particular scenario results.
	 * 
	 * @param scenarioResults
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 * @throws PropertiesFileIsNotFoundException 
	 */
	public void computeStatistics(ProjectSetup projectSetup, String scenarioResults) throws SQLException, ClassNotFoundException, PropertiesFileIsNotFoundException {
		
		Map<Integer, Double> presicions = this.computeAllPresicionMeans(projectSetup, scenarioResults, 10000);
		Map<Integer, Double> recalls = this.computeAllRecallMeans(projectSetup, scenarioResults, 10000);
		Map<Integer, Double> hitRates = this.computeAllHitRateMeans(projectSetup, scenarioResults, 10000);
		Map<Integer, Float> giniIndexes = this.computeAllGiniIndexes(scenarioResults, 10000);

		for (int i = 1; i <= 10; i++) {
            getResultsDb().insertParticularStatistics(scenarioResults, i,
					presicions.get(i), recalls.get(i),
					this.f1(presicions.get(i), recalls.get(i)),
					hitRates.get(i), giniIndexes.get(i), 0.0, 0.0, 0);
//			WikipediaConnector.insertParticularStatistics(scenarioResults, i,
//					presicions.get(i), recalls.get(i),
//					this.f1(presicions.get(i), recalls.get(i)),
//					hitRates.get(i), giniIndexes.get(i), 0.0, 0.0, 0);
		}

		presicions = this.computeAllPresicionMeans(projectSetup, scenarioResults, 1);
		recalls = this.computeAllRecallMeans(projectSetup, scenarioResults, 1);
		hitRates = this.computeAllHitRateMeans(projectSetup, scenarioResults, 1);
		giniIndexes = this.computeAllGiniIndexes(scenarioResults, 1);

		for (int i = 1; i <= 10; i++) {
            getResultsDb().insertParticularStatistics(scenarioResults, i,
					presicions.get(i), recalls.get(i),
					this.f1(presicions.get(i), recalls.get(i)),
					hitRates.get(i), giniIndexes.get(i), 0.0, 0.0, 1);
//			WikipediaConnector.insertParticularStatistics(scenarioResults, i,
//					presicions.get(i), recalls.get(i),
//					this.f1(presicions.get(i), recalls.get(i)),
//					hitRates.get(i), giniIndexes.get(i), 0.0, 0.0, 1);
		}

		presicions = this.computeAllPresicionMeans(projectSetup, scenarioResults, 3);
		recalls = this.computeAllRecallMeans(projectSetup, scenarioResults, 3);
		hitRates = this.computeAllHitRateMeans(projectSetup, scenarioResults, 3);
		giniIndexes = this.computeAllGiniIndexes(scenarioResults, 3);

		for (int i = 1; i <= 10; i++) {
			WikipediaConnector.insertParticularStatistics(scenarioResults, i,
					presicions.get(i), recalls.get(i),
					this.f1(presicions.get(i), recalls.get(i)),
					hitRates.get(i), giniIndexes.get(i), 0.0, 0.0, 3);
		}

		presicions = this.computeAllPresicionMeans(projectSetup, scenarioResults, 5);
		recalls = this.computeAllRecallMeans(projectSetup, scenarioResults, 5);
		hitRates = this.computeAllHitRateMeans(projectSetup, scenarioResults, 5);
		giniIndexes = this.computeAllGiniIndexes(scenarioResults, 5);

		for (int i = 1; i <= 10; i++) {
			WikipediaConnector.insertParticularStatistics(scenarioResults, i,
					presicions.get(i), recalls.get(i),
					this.f1(presicions.get(i), recalls.get(i)),
					hitRates.get(i), giniIndexes.get(i), 0.0, 0.0, 5);
		}

	}

	public Map<Integer, Float> computeAllGiniIndexes(String scenarioName,
			int limit) throws PropertiesFileIsNotFoundException {
		return this.getGiniIndexCalculator().getGiniIndex(scenarioName, limit);
	}

	public Map<Integer, Double> computeAllPresicionMeans(ProjectSetup projectSetup, String scenarioName, int limit) 
			throws SQLException, ClassNotFoundException, PropertiesFileIsNotFoundException {
		Map<Integer, Double> result = new HashMap<Integer, Double>();
		for (int i = 1; i <= 10; i++) {
			result.put(i, 0.0);
		}

		String query = "select * from `" + scenarioName + "`";
		PreparedStatement statement = this.connector.getResultsConnection().prepareStatement(query);

		ResultSet rs = statement.executeQuery();
		double size = 0;
		while (rs.next()) {
			size++;
			String relevant = rs.getString("relevantPaths");
			for (int i = 4; i <= 13; i++) {
				double presicion = this.simplePresicion(projectSetup, rs.getString(i),
						relevant, limit);
				result.put(i - 3, result.get(i - 3) + presicion);
			}
		}
        
		if (size > 0) {
			for (int i = 1; i <= 10; i++) {
				result.put(i, result.get(i) / size);
			}
		} else {
			for (int i = 1; i <= 10; i++) {
				result.put(i, -1.0);
			}
		}
        
		statement.close();
		rs.close();
		return result;
	}

	public double simpleRecall(ProjectSetup projectSetup, String retrievedPaths, String relevantPaths, int limit) {
		Set<String> retrieved = this.getRetrievedPaths(retrievedPaths, limit);
		Set<String> relevant = this.getSetOfRelevantPathQueries(relevantPaths);
		IGeneralization cg = projectSetup.getGeneralizator();

		Set<String> starRelevant = new HashSet<String>();
		for (String path : relevant) {
			starRelevant.add(cg.generalizePathQuery(path));
		}

		relevant = starRelevant;

		retrieved.retainAll(relevant);
		double intersection = retrieved.size();
		double relevantSize = relevant.size();
		return intersection / relevantSize;
	}

	public Map<Integer, Double> computeAllRecallMeans(ProjectSetup projectSetup, String scenarioName, int limit) 
			throws SQLException, ClassNotFoundException, PropertiesFileIsNotFoundException {
		Map<Integer, Double> result = new HashMap<Integer, Double>();
		for (int i = 1; i <= 10; i++) {
			result.put(i, 0.0);
		}

		String query = "select * from `" + scenarioName + "`";
		PreparedStatement statement = this.connector.getResultsConnection()
				.prepareStatement(query);

		ResultSet rs = statement.executeQuery();
		double size = 0;
		while (rs.next()) {
			size++;
			String relevant = rs.getString("relevantPaths");
			for (int i = 4; i <= 13; i++) {
				double recall = this.simpleRecall(projectSetup, rs.getString(i), relevant,
						limit);
				result.put(i - 3, result.get(i - 3) + recall);
			}
		}

		if (size > 0) {
			for (int i = 1; i <= 10; i++) {
				result.put(i, result.get(i) / size);
			}
		} else {
			for (int i = 1; i <= 10; i++) {
				result.put(i, -1.0);
			}
		}
		statement.close();
		rs.close();

		return result;
	}

	public double simpleHitRate(ProjectSetup projectSetup, String retrievedPaths, String relevantPaths, int limit) {
		Set<String> retrieved = this.getRetrievedPaths(retrievedPaths, limit);
		Set<String> relevant = this.getSetOfRelevantPathQueries(relevantPaths);
		IGeneralization cg = projectSetup.getGeneralizator();

		Set<String> starRelevant = new HashSet<String>();
		for (String path : relevant) {
			starRelevant.add(cg.generalizePathQuery(path));
		}

		relevant = starRelevant;

		retrieved.retainAll(relevant);

		if (retrieved.size() > 0) {
			return 1;
		}else{
			System.out.println("hit-rate fails");
		}
		return 0;
	}

	public Map<Integer, Double> computeAllHitRateMeans(ProjectSetup projectSetup, String scenarioName, int limit) 
			throws SQLException, ClassNotFoundException, PropertiesFileIsNotFoundException {
		Map<Integer, Double> result = new HashMap<Integer, Double>();
		for (int i = 1; i <= 10; i++) {
			result.put(i, 0.0);
		}

		String query = "select * from `" + scenarioName + "`";
		PreparedStatement statement = this.connector.getResultsConnection()
				.prepareStatement(query);

		ResultSet rs = statement.executeQuery();
		double size = 0;
		while (rs.next()) {
			size++;
			String relevant = rs.getString("relevantPaths");
			for (int i = 4; i <= 13; i++) {
				double hitrate = this.simpleHitRate(projectSetup, rs.getString(i), relevant,
						limit);
				result.put(i - 3, result.get(i - 3) + hitrate);
			}
		}

		if (size > 0) {
			for (int i = 1; i <= 10; i++) {
				result.put(i, result.get(i) / size);
			}
		} else {
			for (int i = 1; i <= 10; i++) {
				result.put(i, -1.0);
			}
		}
		statement.close();
		rs.close();
		return result;
	}

	public double f1(double presicion, double recall) {
		return (2 * ((presicion * recall) / (presicion + recall)));
	}

	/**
	 * This method computes the Gini Index for the current Path index in the
	 * ResultsDatabaBase.
	 * 
	 * @return giniIndex
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 * @throws PropertiesFileIsNotFoundException 
	 */
	public double giniIndex() throws SQLException, ClassNotFoundException, PropertiesFileIsNotFoundException {
		Statement st = this.connector.getResultsConnection().createStatement();
		Statement stCount = this.connector.getResultsConnection().createStatement();
		ResultSet rsCount = stCount.executeQuery("select count(*) as cant from U_page");
		Statement stCountPaths = this.connector.getResultsConnection().createStatement();
		ResultSet rsCountPaths = stCountPaths.executeQuery("select count(*) as cant from V_Normalized");
		rsCount.first();
		rsCountPaths.first();
		double n = rsCountPaths.getDouble("cant");
		double cant = rsCount.getDouble("cant");
		double total = 0.0;
		double j = 1;
		ResultSet rs = st
				.executeQuery("SELECT v_to, count(u_from) suma,V.path from UxV, V_Normalized V where v_to=V.id group by v_to order by suma asc");
		while (rs.next()) {
			double p_i = rs.getDouble("suma") / cant;
			double tempSum = 2.0 * j - n - 1;
			total = total + (tempSum * p_i);
			j++;
		}

		total = total * (1.0 / (n - 1.0));
		return total;
	}

	/**
	 * Computes the support for an item.
	 * 
	 * @param pair
	 * @param pathQuery
	 * @return
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 * @throws PropertiesFileIsNotFoundException 
	 */
	public double supportForItem(String pair, String pathQuery)
			throws SQLException, ClassNotFoundException, PropertiesFileIsNotFoundException {
		String query = "SELECT v_to, count(u_from) suma,V.path from UxV, V_Normalized V where v_to=V.id and path=? group by v_to order by suma asc";
		PreparedStatement pst = this.connector.getResultsConnection()
				.prepareStatement(query);
		pst.setString(1, pathQuery);
		ResultSet rs = pst.executeQuery();
		double result = 0.0;
		if (rs.next()) {
			result = rs.getDouble("suma");
		}
		return result;
	}

	public double supportForUser(String pair, String pathQuery)
			throws SQLException, ClassNotFoundException, PropertiesFileIsNotFoundException {
		// select u_from, count(v_to) as suma, U_page.page as suma from
		// UxV,U_page where u_from=U_page.id group by u_from order by suma;
		String query = "select u_from, count(v_to) as suma, U_page.page as suma from UxV,U_page where u_from=U_page.id and page=? group by u_from order by suma";
		PreparedStatement pst = this.connector.getResultsConnection()
				.prepareStatement(query);
		pst.setString(1, pair);
		ResultSet rs = pst.executeQuery();
		double result = 0.0;
		if (rs.next()) {
			result = rs.getDouble("suma");
		}
		return result;
	}

    /**
     * @return the resultsDb
     */
    public ResultsDbInterface getResultsDb() {
        return resultsDb;
    }

    /**
     * @param resultsDb the resultsDb to set
     */
    public void setResultsDb(ResultsDbInterface resultsDb) {
        this.resultsDb = resultsDb;
    }

}
