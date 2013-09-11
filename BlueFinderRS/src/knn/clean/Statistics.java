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

import strategies.LastCategoryGeneralization;

import db.WikipediaConnector;

public class Statistics {

	public Set<String> getSetOfRelevantPathQueries(String stringOfPathQueries) {
		//"#from / Cat:#from / #to , #from / Cat:Warner_Music_labels / #to";
		String[] paths = stringOfPathQueries.trim().split(" , ");
		List<String> result = Arrays.asList(paths);
		
		return new HashSet<String>(result);
	}

	public Set<String> getRetrievedPaths(String stringPathQueries) {
		//{#from / #to=1010, #from / * / Cat:ECM_artists / #to=1}
		Set<String> result = new HashSet<String>();
		String paths = stringPathQueries.substring(1, stringPathQueries.length());
		String[] temporal = paths.split(", ");
		for (int i = 0; i < temporal.length; i++) {
			String[] subPaths = temporal[i].split(" / ");
			String[] equalsPart = subPaths[subPaths.length-1].split("=");
			String toAdd=temporal[i].replaceFirst("="+equalsPart[equalsPart.length-1], "");
			result.add(toAdd);
			
		}
		
		return result;
	}
	
	public Set<String> getRetrievedPaths(String stringPathQueries, int limit) {
		//{#from / #to=1010, #from / * / Cat:ECM_artists / #to=1}
		Set<String> result = new HashSet<String>();
		String paths = stringPathQueries.substring(1, stringPathQueries.length());
		String[] temporal = paths.split(", ");
		for (int i = 0; (i < temporal.length && result.size()<limit); i++) {
			String[] subPaths = temporal[i].split(" / ");
			String[] equalsPart = subPaths[subPaths.length-1].split("=");
			String toAdd=temporal[i].replaceFirst("="+equalsPart[equalsPart.length-1], "");
			result.add(toAdd);
			
		}
		
		return result;
	}

	public double simplePresicion(String retrievedPaths, String relevantPaths, int limit) {
		
		Set<String> retrieved = this.getRetrievedPaths(retrievedPaths, limit);
		Set<String> relevant = this.getSetOfRelevantPathQueries(relevantPaths);
		LastCategoryGeneralization cg = new LastCategoryGeneralization();
		
		Set<String> starRelevant = new HashSet<String>();
		for (String path : relevant) {
			starRelevant.add(cg.generalizePathQuery(path));
		}
		
		relevant=starRelevant;
		
		relevant.retainAll(retrieved);
		double rSize = relevant.size();
		double retrievedSize = retrieved.size();
		
		return rSize / retrievedSize;
		
		
		
	}

	/**
	 * Computes all the statistics for a particular scenario results.
	 * @param scenarioResults
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	public void computeStatistics(String scenarioResults) throws SQLException, ClassNotFoundException {
		
		Map<Integer,Double> presicions = this.computeAllPresicionMeans(scenarioResults, 10000);
		Map<Integer,Double> recalls = this.computeAllRecallMeans(scenarioResults, 10000);
		Map<Integer,Double> hitRates = this.computeAllHitRateMeans(scenarioResults, 10000);
		double giniIndex = this.giniIndex();
		
		
		for (int i = 1; i <= 10; i++) {
			WikipediaConnector.insertParticularStatistics(scenarioResults, i, presicions.get(i), 
					recalls.get(i), this.f1(presicions.get(i), recalls.get(i)), hitRates.get(i), 
					giniIndex, 0.0, 0.0, 0);
		}
		
		presicions = this.computeAllPresicionMeans(scenarioResults,1);
		recalls = this.computeAllRecallMeans(scenarioResults,1);
		hitRates = this.computeAllHitRateMeans(scenarioResults,1);
		
		for (int i = 1; i <= 10; i++) {
			WikipediaConnector.insertParticularStatistics(scenarioResults, i, presicions.get(i), 
					recalls.get(i), this.f1(presicions.get(i), recalls.get(i)), hitRates.get(i), 
					giniIndex, 0.0, 0.0, 1);
		}
		
		presicions = this.computeAllPresicionMeans(scenarioResults,3);
		recalls = this.computeAllRecallMeans(scenarioResults,3);
		hitRates = this.computeAllHitRateMeans(scenarioResults,3);
		
		for (int i = 1; i <= 10; i++) {
			WikipediaConnector.insertParticularStatistics(scenarioResults, i, presicions.get(i), 
					recalls.get(i), this.f1(presicions.get(i), recalls.get(i)), hitRates.get(i), 
					giniIndex, 0.0, 0.0, 3);
		}
		
		presicions = this.computeAllPresicionMeans(scenarioResults,5);
		recalls = this.computeAllRecallMeans(scenarioResults,5);
		hitRates = this.computeAllHitRateMeans(scenarioResults,5);
		
		for (int i = 1; i <= 10; i++) {
			WikipediaConnector.insertParticularStatistics(scenarioResults, i, presicions.get(i), 
					recalls.get(i), this.f1(presicions.get(i), recalls.get(i)), hitRates.get(i), 
					giniIndex, 0.0, 0.0, 5);
		}
		
	}

	public Map<Integer, Double> computeAllPresicionMeans(String scenarioName, int limit) throws SQLException, ClassNotFoundException {
		Map<Integer,Double> result = new HashMap<Integer, Double>();
		for (int i = 1; i <= 10; i++) {
			result.put(i, 0.0);
		}
		
		String query = "select * from `"+scenarioName+"`";
		PreparedStatement statement = WikipediaConnector.getResultsConnection().prepareStatement(query);
		
		ResultSet rs = statement.executeQuery();
		double size=0;
		while(rs.next()){
			size++;
			String relevant = rs.getString("relevantPaths");
			for (int i = 4; i <=13 ; i++) {
				double presicion= this.simplePresicion(rs.getString(i), relevant, limit);
				result.put(i-3, result.get(i-3)+presicion);
			}
		}
		
		for (int i = 1; i <= 10; i++) {
			result.put(i, result.get(i)/size);
		}
		statement.close();
		rs.close();
		return result;
	}

	public double simpleRecall(String retrievedPaths, String relevantPaths, int limit) {
		Set<String> retrieved = this.getRetrievedPaths(retrievedPaths, limit);
		Set<String> relevant = this.getSetOfRelevantPathQueries(relevantPaths);
		LastCategoryGeneralization cg = new LastCategoryGeneralization();
		
		Set<String> starRelevant = new HashSet<String>();
		for (String path : relevant) {
			starRelevant.add(cg.generalizePathQuery(path));
		}
		
		
		relevant=starRelevant;
		
		retrieved.retainAll(relevant);
		double intersection = retrieved.size();
		double relevantSize = relevant.size();
		return intersection / relevantSize;
	}

	public Map<Integer, Double> computeAllRecallMeans(String scenarioName, int limit) throws SQLException, ClassNotFoundException {
		Map<Integer,Double> result = new HashMap<Integer, Double>();
		for (int i = 1; i <= 10; i++) {
			result.put(i, 0.0);
		}
		
		String query = "select * from `"+scenarioName+"`";
		PreparedStatement statement = WikipediaConnector.getResultsConnection().prepareStatement(query);
		
		ResultSet rs = statement.executeQuery();
		double size=0;
		while(rs.next()){
			size++;
			String relevant = rs.getString("relevantPaths");
			for (int i = 4; i <=13 ; i++) {
				double recall= this.simpleRecall(rs.getString(i), relevant, limit);
				result.put(i-3, result.get(i-3)+recall);
			}
		}
		
		for (int i = 1; i <= 10; i++) {
			result.put(i, result.get(i)/size);
		}
		statement.close();
		rs.close();
		
		return result;
	}

	public double simpleHitRate(String retrievedPaths, String relevantPaths, int limit) {
		Set<String> retrieved = this.getRetrievedPaths(retrievedPaths, limit);
		Set<String> relevant = this.getSetOfRelevantPathQueries(relevantPaths);
		LastCategoryGeneralization cg = new LastCategoryGeneralization();
		
		Set<String> starRelevant = new HashSet<String>();
		for (String path : relevant) {
			starRelevant.add(cg.generalizePathQuery(path));
		}
		
		relevant=starRelevant;
		
		
		retrieved.retainAll(relevant);
		
		if( retrieved.size()>0 ){
			return 1;
		}
		return 0;
		
	}

	public Map<Integer, Double> computeAllHitRateMeans(String scenarioName, int limit) throws SQLException, ClassNotFoundException {
		Map<Integer,Double> result = new HashMap<Integer, Double>();
		for (int i = 1; i <= 10; i++) {
			result.put(i, 0.0);
		}
		
		String query = "select * from `"+scenarioName+"`";
		PreparedStatement statement = WikipediaConnector.getResultsConnection().prepareStatement(query);
		
		ResultSet rs = statement.executeQuery();
		double size=0;
		while(rs.next()){
			size++;
			String relevant = rs.getString("relevantPaths");
			for (int i = 4; i <=13 ; i++) {
				double hitrate= this.simpleHitRate(rs.getString(i), relevant, limit);
				result.put(i-3, result.get(i-3)+hitrate);
			}
		}
		
		for (int i = 1; i <= 10; i++) {
			result.put(i, result.get(i)/size);
		}
		statement.close();
		rs.close();
		return result;
	}

	

	public double f1(double presicion, double recall) {
		return (2*((presicion * recall) / (presicion + recall)));
	}

	/**This method computes the Gini Index for the current Path index in the ResultsDatabaBase.
	 * 
	 * @return giniIndex
	 * @throws ClassNotFoundException 
	 * @throws SQLException 
	 */
	public double giniIndex() throws SQLException, ClassNotFoundException {
		Statement st = WikipediaConnector.getResultsConnection().createStatement();
		Statement stCount = WikipediaConnector.getResultsConnection().createStatement();
		ResultSet rsCount = stCount.executeQuery("select count(*) as cant from U_page");
		Statement stCountPaths = WikipediaConnector.getResultsConnection().createStatement();
		ResultSet rsCountPaths = stCountPaths.executeQuery("select count(*) as cant from V_Normalized");
		rsCount.first();
		rsCountPaths.first();
		double n = rsCountPaths.getDouble("cant");
		double cant = rsCount.getDouble("cant");
		double total = 0.0;
		double j = 1;
		ResultSet rs = st.executeQuery("SELECT v_to, count(u_from) suma,V.path from UxV, V_Normalized V where v_to=V.id group by v_to order by suma asc" );
		while(rs.next()){
			double p_i= rs.getDouble("suma") / cant;
			double tempSum= 2.0*j-n-1;
			total = total + (tempSum*p_i);
			j++;
		}
		
		total = total * (1.0 / (n-1.0));
		
		return total;
	}

	/**
	 * Computes the support for an item.
	 * @param pair
	 * @param pathQuery
	 * @return
	 * @throws ClassNotFoundException 
	 * @throws SQLException 
	 */
	public double supportForItem(String pair, String pathQuery) throws SQLException, ClassNotFoundException {
		String query = "SELECT v_to, count(u_from) suma,V.path from UxV, V_Normalized V where v_to=V.id and path=? group by v_to order by suma asc";
		PreparedStatement pst = WikipediaConnector.getResultsConnection().prepareStatement(query);
		pst.setString(1, pathQuery);
		ResultSet rs = pst.executeQuery();
		double result = 0.0;
		if(rs.next()){
			result=rs.getDouble("suma");
		}
		return result;
	}

	public double supportForUser(String pair, String pathQuery) throws SQLException, ClassNotFoundException {
		//select u_from, count(v_to) as suma, U_page.page as suma from UxV,U_page where u_from=U_page.id group by u_from order by suma;
		String query = "select u_from, count(v_to) as suma, U_page.page as suma from UxV,U_page where u_from=U_page.id and page=? group by u_from order by suma";
		PreparedStatement pst = WikipediaConnector.getResultsConnection().prepareStatement(query);
		pst.setString(1, pair);
		ResultSet rs = pst.executeQuery();
		double result = 0.0;
		if(rs.next()){
			result=rs.getDouble("suma");
		}
		return result;
	}


}
