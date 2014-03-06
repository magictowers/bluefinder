package knn;

/**
 * This is the class to run, from a PIA index the BlueFinder algorithm !!!
 */

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import strategies.LastCategoryGeneralization;
import db.MysqlIndexConnection;

public class KNNTestComplete extends KNNComplete {

	public KNNTestComplete(KNN knn) {
		super(knn);
		// TODO Auto-generated constructor stub
	}

	/**
	 * This method compute the accuracy of the strategy using connected pairs as
	 * non connected. The idea is to compare the knn strategy to know if the
	 * recomendations are the same than the actual Wikipedia connected pairs.
	 * 
	 * @param piaIndexBase
	 *            name of the base that contains the piaIndex
	 * @param typesTable
	 *            name of the table with the types to analyse.
	 * @param kValue
	 *            maximun value for K
	 * @param testRowsNumber
	 *            portion taken by the test.
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */

	private void processTest(String piaIndexBase, String typesTable,
			int kValue, int testRowsNumber, String resultTableName)
			throws ClassNotFoundException, SQLException {
		Connection connection = MysqlIndexConnection
				.getConnection(piaIndexBase);
		Statement statement = connection.createStatement();
		ResultSet resultSet = statement.executeQuery("select * from "
				+ typesTable + " where path_query <>'?' order by RAND() limit "
				+ testRowsNumber);
		String relatedVTo = "v_to=0 ";
		String relatedString = "";
		while (resultSet.next()) {
			long time_start, time_end;
			time_start = System.currentTimeMillis();
			Instance instance = new Instance(0,
					resultSet.getString("resource"),
					resultSet.getString("types"), 0);
			// System.out.println("Resource: " +
			// resultSet.getString("resource"));
			
			List<Instance> result = this.getKnn().compute(kValue + 1, instance);
			result.remove(instance);
			List<String> knnResults = new ArrayList<String>();
			for (Instance instance2 : result) {
				relatedVTo = relatedVTo + "or v_to = " + instance2.getId()
						+ " ";
				relatedString = relatedString + "(" + instance2.getDistance()
						+ ") " + instance2.getResource() + " ";
				Statement st = connection.createStatement();
				String queryPaths = "SELECT u_from, count(u_from) suma,V.path from UxV, V_Normalized V where u_from=V.id and ("
						+ relatedVTo + ") group by u_from order by suma desc ";
				ResultSet paths = st.executeQuery(queryPaths);
				TreeMap<String, Integer> map = this.genericPath(paths);
				// for (String pathGen : map.keySet()) {
				// System.out.println(map);

				knnResults.add(map.toString());
				// System.out.println("end ---- k="+kvalue);
			
			}
			time_end = System.currentTimeMillis();
			// Insert statem
			String insertSentence = "INSERT INTO `dbresearch`.`"+ resultTableName + "` (`v_to`, `related_resources`,`1path`, `2path`, `3path`, `4path`, `5path`, `6path`, `7path`, `8path`, `9path`, `10path`,`time`)"
					+ "VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )";
			PreparedStatement st = connection.prepareStatement(insertSentence);
			st.setString(1, resultSet.getString("resource")+" "+resultSet.getInt("path_query"));
			st.setString(2, relatedString);
			int i = 3;
			for (String string : knnResults) {
				st.setString(i, string);
				i++;

			}
			st.setLong(13, time_end - time_start);
			st.executeUpdate();

			relatedVTo = "v_to=0 ";
			relatedString = "";
		}
	}
	
	/**
	 * This method receive a string from the knn results table and 
	 * obtain the starred path queries
	 * @param encodedPaths
	 * @return 
	 */
	
	protected List<String> decodePathsFromRow(String encodedPaths){
		//encodepaths contains a string like {*/Musicians_from_[from]/[to]=8, [to]=1, */Musicians_from_[from],_California/[to]=1}
		String decode = encodedPaths.substring(1, encodedPaths.lastIndexOf("}"));
		List<String> result = new ArrayList<String>();
		String[] pathsWithEquals = decode.split(", ");
		for(String pathWEquals:pathsWithEquals){
			result.add(pathWEquals.split("=")[0]);
		}
		
		return result;
	}
	
	/**
	 * This method analyse the results of knn and determine de number of good cases.
	 * @param string
	 * @throws SQLException 
	 * @throws ClassNotFoundException 
	 */
	private void analyseResults(String piaIndexBase, String resultsTableName, String staticsTableName) throws ClassNotFoundException, SQLException {
		Connection connection = MysqlIndexConnection
				.getConnection(piaIndexBase);
		Statement statement = connection.createStatement();
		ResultSet resultSet = statement.executeQuery("select * from "
				+ resultsTableName);
		//Insert statem
		String insertSentence = "INSERT INTO `dbresearch`.`"+staticsTableName+"` (`resource`,`1path`,`1pC`, `2path`,`2pC`, `3path`,`3pC`, `4path`, `4pC`, `5path`, `5pC`," +
				" `6path`, `6pC`, `7path`, `7pC`, `8path`, `8pC`, `9path`, `9pC`, `10path`, `10pC` ) " + 
				"VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )";
		
		while(resultSet.next()){
			PreparedStatement insertStatement = connection.prepareStatement(insertSentence); 
			int kToAnalyze = 1;
			String[] resource = resultSet.getString("v_to").split(" ");
			Integer id = Integer.parseInt(resource[1]);
			insertStatement.setString(1, resultSet.getString("v_to"));
			List<String> actualPaths = this.getListOfPathQueries(id, connection);
			for (int i = 1; i <= 10; i++) {
				String iPaths = resultSet.getString(i+"path");
				List<String> decodedPaths =  this.decodePathsFromRow(iPaths);
				if(decodedPaths.size()>3){
					decodedPaths=decodedPaths.subList(0, 3);
				}
				int param = i*2;
				insertStatement.setInt(param, decodedPaths.size());
				insertStatement.setInt(param+1, this.countCorrectPaths(actualPaths, decodedPaths));
			}
			insertStatement.execute();
			
			
		}
		
		
	}

	private List<String> getListOfPathQueries(Integer id, Connection connection) throws SQLException {
		String query = "SELECT u_from, count(u_from) suma,V.path from UxV, V_Normalized V where u_from=V.id and v_to="+id+" group by u_from order by suma desc";
		List<String> paths = new ArrayList<String>();
		Statement st = connection.createStatement();
		LastCategoryGeneralization cg = new LastCategoryGeneralization();
		ResultSet resultSet = st.executeQuery(query);
		while(resultSet.next()){
			String pathQuery = resultSet.getString("path");
			String genPath = cg.generalizePathQuery(pathQuery);
			if(((genPath.startsWith("*/Articles_li�s") || !genPath.startsWith("*/Articles"))) && !paths.contains(genPath) ){
				paths.add(genPath);
			}
		}
		return paths;
		
		
	}
	
	public static void resetTables(String tableKNN, String tableStatics ) throws ClassNotFoundException, SQLException{
		
		Connection con = MysqlIndexConnection.getIndexConnection();
		
		Statement delete = con.createStatement();
		delete.executeUpdate("DROP TABLE IF EXISTS `"+tableKNN+"`");
		delete.close();
		Statement create = con.createStatement();
		
		String createKNNResults = "CREATE TABLE `"+tableKNN+"` (" +
		  "`id` int(11) NOT NULL AUTO_INCREMENT,"+
		  "`v_to` text,"+
		  "`related_resources` text,"+
		  "`1path` text,"+
		  "`2path` text,"+
		  "`3path` text,"+
		  "`4path` text,"+
		  "`5path` text,"+
		  "`6path` text,"+
		  "`7path` text,"+
		  "`8path` text,"+
		  "`9path` text,"+
		  "`10path` text,"+
		  "`time` bigint(20) DEFAULT NULL,"+
		  "PRIMARY KEY (`id`)"+
		") ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8";
		
		create.executeUpdate(createKNNResults);
		create.close();
		
		delete = con.createStatement();
		delete.executeUpdate("DROP TABLE IF EXISTS `"+tableStatics+"`");
		delete.close();
		create = con.createStatement();
		String createStatis = "CREATE TABLE `"+tableStatics+"` (`id` int(11) NOT NULL AUTO_INCREMENT," +
				"`resource` text, `1path` int(11) DEFAULT NULL, " +
				"`1pC` int(11) DEFAULT NULL, " +
				"`2path` int(11) DEFAULT NULL, " +
				"`2pC` int(11) DEFAULT NULL, " +
				"`3path` int(11) DEFAULT NULL, `3pC` int(11) DEFAULT NULL, " +
				"`4path` int(11) DEFAULT NULL, `4pC` int(11) DEFAULT NULL, " +
				"`5path` int(11) DEFAULT NULL, `5pC` int(11) DEFAULT NULL, " +
				"`6path` int(11) DEFAULT NULL, `6pC` int(11) DEFAULT NULL, " +
				"`7path` int(11) DEFAULT NULL, `7pC` int(11) DEFAULT NULL, " +
				"`8path` int(11) DEFAULT NULL, `8pC` int(11) DEFAULT NULL, `" +
				"9path` int(11) DEFAULT NULL, `9pC` int(11) DEFAULT NULL, " +
				"`10path` int(11) DEFAULT NULL, `10pC` int(11) DEFAULT NULL, " +
				"`resourcePaths` int(11) DEFAULT NULL, PRIMARY KEY (`id`) ) " +
				"ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8";
		create.executeUpdate(createStatis);
		create.close();
	}

	public static void main(String[] args) throws ClassNotFoundException,
			SQLException {
		
		//Example: dbresearch testEsFoafTypes 9 esbirthplaceknnNew 200 esbirthplacestaticsNew
		String piaIndexBase;
		String typesTable;

		if (args.length != 5) {
			System.out.println("Wrong number of params.");
			System.out
					.println("<piaIndexBase> <typesTable> <kValue> <experimentCode> <rowsNumber>");
			System.out.println("name of the piaIndexBase in localhost.");
			System.out
					.println("Name of table with resource types in piaIndexBase");
			System.out.println("Name of the table to put the resources");
			System.out.println("number of rows to be used by the test");
			System.out.println("Name of the static table");
			System.exit(1);
		}
		long startTime, endTime;
		startTime = System.currentTimeMillis();

		piaIndexBase = args[0];
		typesTable = args[1];
		int kValue = Integer.parseInt(args[2]);
		String resultsName = args[3]+"KNN";
		String staticsName = args[3]+"Statics";
		int rowsNumber = Integer.parseInt(args[4]);
		
		resetTables(resultsName, staticsName);

		KNN knn = new KNN(piaIndexBase, typesTable);
		KNNTestComplete knnTestComplete = new KNNTestComplete(knn);
		knnTestComplete.processTest(piaIndexBase, typesTable, kValue,
				rowsNumber, resultsName);
		knnTestComplete.analyseResults(piaIndexBase, resultsName, staticsName);
		endTime = System.currentTimeMillis();
		System.out.println("the task has taken "
				+ ((endTime - startTime) / 1000) + " seconds");

	}

	public int countCorrectPaths(List<String> actualPaths,
			List<String> comparedPaths) {
		int result = 0;
		for (String string : comparedPaths) {
			if(actualPaths.contains(string)){
				result++;
			}
		}
		
		return result;
	}


}
