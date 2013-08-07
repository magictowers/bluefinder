
package db;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import db.utils.ScriptRunner;

/**
 * This class control the connections to the different databases. It reads the setup.properties file which has to be placed
 * in the classpath of the application.
 * @author dtorres
 */
public class WikipediaConnector {
    
    private  static Connection wikiConnection;
    private  static Connection researhConnection;
	private static Connection testConnection;
    
    private static Properties getProperties(){
    	Properties prop = new Properties();
    	try {
			prop.load(WikipediaConnector.class.getClassLoader().getResourceAsStream("setup.properties"));
		} catch (IOException e) {	
		}
    	return prop;
    }
    
    

        public static Connection getConnection() throws ClassNotFoundException, SQLException{
        if(wikiConnection==null || wikiConnection.isClosed()){
        Class.forName("com.mysql.jdbc.Driver");
       // Connection con = DriverManager.getConnection("jdbc:mysql://"+WikipediaConnector.HOST+"/"+WikipediaConnector.SCHEMA+"", WikipediaConnector.USER, Wiki$
        //wikiConnection = DriverManager.getConnection("jdbc:mysql://localhost/"+WikipediaConnector.SCHEMA+"?user=root&password=root&useUnicode=true&characterEncoding=utf8");
       // wikiConnection = DriverManager.getConnection("jdbc:mysql://localhost/"+WikipediaConnector.SCHEMA+"?user=root&password=root");
        //wikiConnection = DriverManager.getConnection("jdbc:mysql://localhost/"+WikipediaConnector.SCHEMA+"?user=root&password=root");
        if(getProperties().getProperty("testEnvironment").equalsIgnoreCase("true")){
        	try {
				wikiConnection = getTestConnection();
			} catch (TestDatabaseSameThatWikipediaDatabaseException e) {
				throw new SQLException("TestDatabaseSameThatWikipediaDatabaseException");
			} 	
        }else{
        	System.out.println("WikipediaDatabaseConnector!!");
        wikiConnection = DriverManager.getConnection("jdbc:mysql://"+getWikipediaBase()+"?user="+getWikipediaDatabaseUser()+"&password="+getWikipediaDatabasePass()+"&useUnicode=true&characterEncoding=utf8");
        }}

        return wikiConnection;

    }
    
   
    public static Connection getResultsConnection() throws ClassNotFoundException, SQLException{
        if(researhConnection==null || researhConnection.isClosed()) {
        Class.forName("com.mysql.jdbc.Driver");
        //Connection con = DriverManager.getConnection("jdbc:mysql://"+WikipediaConnector.RHOST+"/"+WikipediaConnector.RSCHEMA, WikipediaConnector.USER, Wikip$
        //researhConnection = DriverManager.getConnection("jdbc:mysql://localhost/dbresearch?user=root&password=root&characterEncoding=utf8");
        if(getProperties().getProperty("testEnvironment").equalsIgnoreCase("true")){
        	try {
				researhConnection = getTestConnection();
			} catch (TestDatabaseSameThatWikipediaDatabaseException e) {
				throw new SQLException("TestDatabaseSameThatWikipediaDatabaseException");
			} 	
        }else{
        researhConnection = DriverManager.getConnection("jdbc:mysql://"+getResultDatabase()+"?user="+getResultDatabaseUser()+"&password="+getResultDatabasePass()+"&characterEncoding=utf8");
        }}
        return researhConnection;

    }
    
	public static Connection getTestConnection() throws ClassNotFoundException, SQLException, TestDatabaseSameThatWikipediaDatabaseException {
		   if(testConnection==null || testConnection.isClosed()){
		        Class.forName("com.mysql.jdbc.Driver");
		        if(getTestDatabase().equalsIgnoreCase(getWikipediaBase())){
		        	throw new TestDatabaseSameThatWikipediaDatabaseException();
		        }
		        testConnection = DriverManager.getConnection("jdbc:mysql://"+getTestDatabase()+"?user="+getTestDatabaseUser()+"&password="+getTestDatabasePass()+"&characterEncoding=utf8");
		        }
		        return testConnection;
	}


    public static void restoreTestDatabase() throws ClassNotFoundException, SQLException, TestDatabaseSameThatWikipediaDatabaseException, FileNotFoundException, IOException{
			Connection con = getTestConnection();
			queryRunner(con,"testBasicWikipedia.sql");
			}



	private static void queryRunner(Connection con, String scriptPathFile) throws IOException,
			SQLException, FileNotFoundException {
		ScriptRunner runner = new ScriptRunner(con,false,true);
		runner.setLogWriter(null);
		InputStream is= WikipediaConnector.class.getClassLoader().getResourceAsStream(scriptPathFile);
		InputStreamReader reader = new InputStreamReader(is);
		//runner.runScript(new BufferedReader(new FileReader(scriptPathFile)));
		runner.runScript(new BufferedReader(reader));
	}
    
    

	public static String getWikipediaBase() {
		return getProperties().getProperty("wikipediaDatabase");
	}



	public static String getWikipediaDatabaseUser() {
		return getProperties().getProperty("wikipediaDatabaseUser");
	}



	public static String getWikipediaDatabasePass() {
		return getProperties().getProperty("wikipediaDatabasePass");
		}



	public static String getResultDatabase() {
		return getProperties().getProperty("resultDatabase");

	}

	public static String getResultDatabaseUser() {
		return getProperties().getProperty("resultDatabaseUser");
	}
	
	public static String getResultDatabasePass() {
		return getProperties().getProperty("resultDatabasePass");
	}

	public static String getTestDatabase() {
		return getProperties().getProperty("testDatabase");

	}

	public static String getTestDatabaseUser() {
		return getProperties().getProperty("testDatabaseUser");
	}
	
	public static String getTestDatabasePass() {
		return getProperties().getProperty("testDatabasePass");
	}



	public static void restoreResultIndex() throws ClassNotFoundException, SQLException, FileNotFoundException, IOException {
		Connection con;
	
			con = getResultsConnection();
		
		queryRunner(con,"bluefinder.sql");
		
	}



	public static List<String> getResourceDBTypes(String resource) throws SQLException, ClassNotFoundException {
		String query = "select type from dbtypes where resource=?";
		PreparedStatement statement;
	
		statement = getResultsConnection().prepareStatement(query);
		
		statement.setString(1, resource);
		ResultSet rs = statement.executeQuery();
		
		List<String> results = new ArrayList<String>();
		while(rs.next()){
			results.add(rs.getString("type"));
		}
		return results;
	}



	public static ResultSet getRandomProportionOfConnectedPairs(int proportion) throws ClassNotFoundException, SQLException {
		Connection con;
		
			con = getResultsConnection();
		
		Statement st = con.createStatement();
		ResultSet rs = st.executeQuery("select count(*) as total from U_pageEnhanced");
		double p = proportion;
		rs.next();
		long rows = rs.getLong("total");
		rs.close();
		int prop;
		if(rows>0){
		prop =(int) (rows*(p/100.0));
		}else{prop=0;}
		
		st = con.createStatement();

		String query = "select convert(page using utf8) as page, id, convert(subjectTypes using utf8) as subjectTypes, convert(objectTypes using utf8) as objectTypes FROM U_pageEnhanced order by RAND() limit "+ prop;
		
		rs = st.executeQuery(query);
		return rs;
		
			
		
		
	}

	public static boolean isTestEnvironment(){
		return getProperties().getProperty("testEnvironment").equalsIgnoreCase("true");
		
	}



	public static void createStatisticsTables() throws SQLException, ClassNotFoundException {
		
    String createSentence = "CREATE TABLE IF NOT EXISTS `generalStatistics` (`id` int(11) NOT NULL AUTO_INCREMENT, `scenario` varchar(45) NOT NULL, PRIMARY KEY (`id`),"+
    						"UNIQUE KEY `scenario_UNIQUE` (`scenario`)) ENGINE=InnoDB DEFAULT CHARSET=utf8";


		Statement statement = WikipediaConnector.getResultsConnection().createStatement();
		statement.executeUpdate(createSentence);
		statement.close();
		
		String createParticular = "CREATE TABLE IF NOT EXISTS `particularStatistics` (`id` int(11) NOT NULL AUTO_INCREMENT, `general_id` int(11) NOT NULL,`kValue` int(11) NOT NULL,`precision` float(15,8) NOT NULL DEFAULT '0',"+
								"`recall` float(15,8) NOT NULL DEFAULT '0', `f1` float(15,8) NOT NULL DEFAULT '0',`hit_rate` float(15,8) NOT NULL DEFAULT '0',"+
								" `GI` float(15,8) NOT NULL DEFAULT '0',`itemSupport` float(15,8) NOT NULL DEFAULT '0', `userSupport` float(15,8) NOT NULL DEFAULT '0', `limit` int(11) NOT NULL, "+
								"PRIMARY KEY (`id`)) ENGINE=InnoDB DEFAULT CHARSET=utf8";

		statement = WikipediaConnector.getResultsConnection().createStatement();
		statement.executeUpdate(createParticular);
		statement.close();
		
	}



	public static void insertParticularStatistics(String experimentName,
			long kValue, double precision, double recall, double f1,
			double hit_rate, double gindex, double itemSupport,
			double userSupport, int limit) throws SQLException, ClassNotFoundException {
		
		
		String generalStatistic = "select * from `generalStatistics` where scenario=?";
		
		PreparedStatement gs = getResultsConnection().prepareStatement(generalStatistic);
		gs.setString(1, experimentName);
		ResultSet rs = gs.executeQuery();
		long general_id=0;
		if(rs.next()){
			general_id=rs.getLong("id");
		}else{
			String insertIntoGeneral = "INSERT INTO `generalStatistics` (`scenario`) VALUES (?)";
			PreparedStatement psInsertGeneral = WikipediaConnector.getResultsConnection().prepareStatement(insertIntoGeneral);
			psInsertGeneral.setString(1, experimentName);
			psInsertGeneral.executeUpdate();
			gs = getResultsConnection().prepareStatement(generalStatistic);
			gs.setString(1, experimentName);
			rs = gs.executeQuery();
			rs.next();
			general_id=rs.getLong("id");
		}
		
		
		
		
		
		
		String insertParticularStatistic= "INSERT INTO `particularStatistics` (`general_id`,`kValue`,`precision`,`recall`, "+
"`f1`,`hit_rate`,`GI`,`itemSupport`,`userSupport`, `limit`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		
		PreparedStatement prepared = WikipediaConnector.getResultsConnection().prepareStatement(insertParticularStatistic);
		
		prepared.setLong(1, general_id);
		prepared.setLong(2,kValue);
		prepared.setDouble(3, precision);
		prepared.setDouble(4, recall);
		prepared.setDouble(5, f1);
		prepared.setDouble(6, hit_rate);
		prepared.setDouble(7, gindex);
		prepared.setDouble(8, itemSupport);
		prepared.setDouble(9, userSupport);
		prepared.setInt(10, limit);
		
		
		prepared.execute();
		}


   
    
}
