package knn.clean;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import com.mysql.jdbc.exceptions.jdbc4.MySQLSyntaxErrorException;
import pia.BipartiteGraphGenerator;
import pia.PathIndex;
import knn.Instance;
import knn.distance.SemanticPair;
import strategies.LastCategoryGeneralization;
import db.WikipediaConnector;
import dbpedia.similarityStrategies.ValueComparator;
import utils.ProjectConfiguration;

/**
 * This class compute the evaluation for one PathIndex. The evaluation is the
 * one that is included in the journal article.
 * 
 * @author dtorres
 * 
 */

public class BlueFinderEvaluation {

	private KNN knn;

	public BlueFinderEvaluation(KNN knn) {
		this.knn = knn;
	}
	
	public void runCompleteEvaluation(int proportionOfConnectedPairs, int kValue, String resultTableName) throws ClassNotFoundException, SQLException{
		this.processTest(proportionOfConnectedPairs, kValue, resultTableName);
		this.generateGeneralStatistics(resultTableName);		
	}

	protected void processTest(int proportionOfConnectedPairs, int kValue, String resultTableName)
			throws ClassNotFoundException, SQLException {
				
		this.createResultTable(resultTableName);
		ResultSet resultSet = WikipediaConnector.getRandomProportionOfConnectedPairs(proportionOfConnectedPairs);

		PathIndex pathIndex = new BipartiteGraphGenerator().getPathIndex();
		
		String relatedUFrom = "u_from=0 ";
		String relatedString = "";
		
		int errStmts = 0;
		while (resultSet.next()) {
			long time_start, time_end;
			time_start = System.currentTimeMillis();

			SemanticPair disconnectedPair = this.knn.generateSemanticPair(resultSet.getString("page"), resultSet.getLong("id"), 
					resultSet.getString("subjectTypes"), resultSet.getString("objectTypes"));

			List<Instance> kNearestNeighbors = this.knn.getKNearestNeighbors(kValue, disconnectedPair);

			SemanticPairInstance disconnectedInstance = new SemanticPairInstance(0, disconnectedPair);

			kNearestNeighbors.remove(disconnectedInstance);

			List<String> knnResults = new ArrayList<String>();
			for (Instance neighbor : kNearestNeighbors) {
				relatedUFrom = relatedUFrom + "or u_from = " + neighbor.getId() + " ";
				relatedString = relatedString + "(" + neighbor.getDistance() + ") " + neighbor.getResource() + " ";

				Statement st = WikipediaConnector.getResultsConnection().createStatement();

				String queryFixed = "SELECT v_to, count(v_to) suma,V.path from UxV, V_Normalized V where v_to=V.id and ("
						+ relatedUFrom + ") group by v_to order by suma desc";

				ResultSet paths = st.executeQuery(queryFixed);
				TreeMap<String, Integer> map = this.genericPath(paths, knnResults.size() + 1);

				knnResults.add(map.toString());
			}
			time_end = System.currentTimeMillis();
			
			String insertSentence = "INSERT INTO `"
					+ resultTableName
					+ "` (`resource`, `related_resources`,`1path`, `2path`, `3path`, `4path`, `5path`, `6path`, `7path`, `8path`, `9path`, `10path`,`time`, `relevantPaths`)"
					+ "VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )";
			PreparedStatement statementInsert = WikipediaConnector.getResultsConnection()
					.prepareStatement(insertSentence);
			String firstParam = resultSet.getString("page") +" "+ resultSet.getLong("id"); 
			statementInsert.setString(1, firstParam);
			statementInsert.setString(2, relatedString);
			int i = 3;
			for (String string : knnResults) {
				statementInsert.setString(i, string);
				i++;
			}
			
			List<String> disconnectedPairPathQueries = pathIndex.getPathQueries(disconnectedPair.getSubject(), disconnectedPair.getObject());
			String relevantPathQueries = this.convertToString(disconnectedPairPathQueries);
			
			statementInsert.setLong(13, time_end - time_start);
			statementInsert.setString(14, relevantPathQueries);
			try {
				statementInsert.executeUpdate();
			} catch (MySQLSyntaxErrorException ex) {
				System.out.println("Error while executing the statement...");
//				System.out.println(statementInsert.toString());
				errStmts++;
			}

			relatedUFrom = "u_from=0 ";
			relatedString = "";
		}
		if (errStmts > 0) {
			System.out.println(errStmts + " INSERTs couldn't be executed.");
		}
	}
	
	private String convertToString(List<String> disconnectedPairPathQueries) {
		String result = "";
		for (String pathQuery : disconnectedPairPathQueries) {
			result = result + " , " + pathQuery;
		}
		if(!result.equals("")) {
			result=result.substring(3);
		}
		return result;
	}

	void createResultTable(String resultTableName) throws SQLException, ClassNotFoundException {
		String queryDrop = "DROP TABLE IF EXISTS `"+resultTableName+"`";
		//String query = "CREATE TABLE `"+resultTableName+"` ( `id` int(11) NOT NULL AUTO_INCREMENT, `resource` BLOB, `1path` int(11) DEFAULT NULL, `1pC` int(11) DEFAULT NULL,  `2path` int(11) DEFAULT NULL, `2pC` int(11) DEFAULT NULL,   `3path` int(11) DEFAULT NULL,  `3pC` int(11) DEFAULT NULL,  `4path` int(11) DEFAULT NULL,  `4pC` int(11) DEFAULT NULL,  `5path` int(11) DEFAULT NULL,  `5pC` int(11) DEFAULT NULL,  `6path` int(11) DEFAULT NULL,  `6pC` int(11) DEFAULT NULL,  `7path` int(11) DEFAULT NULL,  `7pC` int(11) DEFAULT NULL,  `8path` int(11) DEFAULT NULL,  `8pC` int(11) DEFAULT NULL,  `9path` int(11) DEFAULT NULL,  `9pC` int(11) DEFAULT NULL,  `10path` int(11) DEFAULT NULL,  `10pC` int(11) DEFAULT NULL,  `resourcePaths` int(11) DEFAULT NULL,  PRIMARY KEY (`id`)) ENGINE=InnoDB DEFAULT CHARSET=utf8";
		String query = "CREATE TABLE `"+resultTableName+"` (`id` int(11) NOT NULL AUTO_INCREMENT, `resource` blob, `related_resources` blob, `1path` text, `2path` text,`3path` text," +
		"`4path` text, `5path` text, `6path` text, `7path` text, `8path` text, `9path` text, `10path` text, `time` bigint(20) DEFAULT NULL, `relevantPaths` text, PRIMARY KEY (`id`)) ENGINE=InnoDB DEFAULT CHARSET=utf8";

		Statement statement = WikipediaConnector.getResultsConnection().createStatement();
		statement.executeUpdate(queryDrop);
		statement.close();
		
		statement = WikipediaConnector.getResultsConnection().createStatement();
		statement.executeUpdate(query);
		statement.close();		
	}

	protected TreeMap<String, Integer> genericPath(ResultSet paths, int kValue)
			throws SQLException {
		HashMap<String, Integer> pathDictionary = new HashMap<String, Integer>();
		ValueComparator bvc = new ValueComparator(pathDictionary);
		TreeMap<String, Integer> sorted_map = new TreeMap<String, Integer>(bvc);
		LastCategoryGeneralization cg = new LastCategoryGeneralization();

		while (paths.next()) {
			String path = paths.getString("path");
			path = cg.generalizePathQuery(path);
			int suma = paths.getInt("suma");
			if ((!path.contains("Articles_") || path.contains("Articles_li�s"))
					&& !path.contains("All_Wikipedia_")
					&& !path.contains("Wikipedia_")
					&& !path.contains("Non-free")
					&& !path.contains("All_pages_")
					&& !path.contains("All_non")) {
				if (pathDictionary.get(path) == null) {
					if (suma == kValue) { // all the cases belongs to this path query
						suma = suma + 1000;
					}
				} else {
					suma += pathDictionary.get(path);
				}
				pathDictionary.put(path, suma);
			}

		}
		// sorted_map.putAll(pathDictionary);
		for (String path : pathDictionary.keySet()) {
			Integer suma = pathDictionary.get(path);
			sorted_map.put(path, suma);
		}
		return sorted_map;
	}

	/**
	 * Creates the general statistic and particular statistic table in the result database.
	 * 
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	public void createStatisticsTables() throws SQLException, ClassNotFoundException {
		WikipediaConnector.createStatisticsTables();		
	}

	public void insertParticularStatistic(String experimentName, long kValue,
			double precision, double recall, double f1, double hit_rate,
			double gindex, double itemSupport, double userSupport, int limit) throws SQLException, ClassNotFoundException {
		
		WikipediaConnector.insertParticularStatistics(experimentName, kValue,
			precision, recall, f1, hit_rate,gindex, itemSupport, userSupport, limit);		
	}
	
	/**
	 * Process the resultsTable and put the general statistics of the computation.
	 * @param resultsTableName
	 * @throws ClassNotFoundException 
	 * @throws SQLException 
	 */
	protected void generateGeneralStatistics(String resultsTableName) throws SQLException, ClassNotFoundException{	
		Statistics statistics = new Statistics();
		this.createStatisticsTables();
		statistics.computeStatistics(resultsTableName);
	}
	
	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		
		if (!(args.length == 2)){
			System.out.println("Help: You have to indicate <scenarioName> <proportionOfExperiment>");
			System.out.println("The evaluation use k from 1 to 10");
			System.out.println("Results are in generalStatistics and particularStatistics");
			System.exit(0);
		}
		String scenarioName;
		int proportion;
		try {
			scenarioName = args[0];
			proportion = Integer.parseInt(args[1]);
		} catch (ArrayIndexOutOfBoundsException ex) {
			scenarioName = "sc1Evaluation";
			proportion = 3;
		}
		KNN knn = new KNN(ProjectConfiguration.enhanceTable());
		BlueFinderEvaluation bfe = new BlueFinderEvaluation(knn);
        
		bfe.runCompleteEvaluation(proportion, 11, scenarioName);
		System.out.println("FINALIZED!!!");
		System.exit(0);
	}


	
}
