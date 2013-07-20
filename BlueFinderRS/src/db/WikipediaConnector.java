
package db;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

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
        if(wikiConnection==null){
        Class.forName("com.mysql.jdbc.Driver");
       // Connection con = DriverManager.getConnection("jdbc:mysql://"+WikipediaConnector.HOST+"/"+WikipediaConnector.SCHEMA+"", WikipediaConnector.USER, Wiki$
        //wikiConnection = DriverManager.getConnection("jdbc:mysql://localhost/"+WikipediaConnector.SCHEMA+"?user=root&password=root&useUnicode=true&characterEncoding=utf8");
       // wikiConnection = DriverManager.getConnection("jdbc:mysql://localhost/"+WikipediaConnector.SCHEMA+"?user=root&password=root");
        //wikiConnection = DriverManager.getConnection("jdbc:mysql://localhost/"+WikipediaConnector.SCHEMA+"?user=root&password=root");
        wikiConnection = DriverManager.getConnection("jdbc:mysql://"+getWikipediaBase()+"?user="+getWikipediaDatabaseUser()+"&password="+getWikipediaDatabasePass()+"&useUnicode=true&characterEncoding=utf8");
        }

        return wikiConnection;

    }
    
   
    public static Connection getResultsConnection() throws ClassNotFoundException, SQLException{
        if(researhConnection==null){
        Class.forName("com.mysql.jdbc.Driver");
        //Connection con = DriverManager.getConnection("jdbc:mysql://"+WikipediaConnector.RHOST+"/"+WikipediaConnector.RSCHEMA, WikipediaConnector.USER, Wikip$
        //researhConnection = DriverManager.getConnection("jdbc:mysql://localhost/dbresearch?user=root&password=root&characterEncoding=utf8");
        researhConnection = DriverManager.getConnection("jdbc:mysql://"+getResultDatabase()+"?user="+getResultDatabaseUser()+"&password="+getResultDatabasePass()+"&characterEncoding=utf8");
        }
        return researhConnection;

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



	public static Connection getTestConnection() throws ClassNotFoundException, SQLException, TestDatabaseSameThatWikipediaDatabaseException {
		   if(testConnection==null){
		        Class.forName("com.mysql.jdbc.Driver");
		        if(getTestDatabase().equalsIgnoreCase(getWikipediaBase())){
		        	throw new TestDatabaseSameThatWikipediaDatabaseException();
		        }
		        testConnection = DriverManager.getConnection("jdbc:mysql://"+getTestDatabase()+"?user="+getTestDatabaseUser()+"&password="+getTestDatabasePass()+"&characterEncoding=utf8");
		        }
		        return testConnection;
	}
   
    
}
