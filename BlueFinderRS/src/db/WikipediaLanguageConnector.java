package db;

import static db.WikipediaConnector.getResultDatabase;
import static db.WikipediaConnector.getResultDatabasePass;
import static db.WikipediaConnector.getResultDatabaseUser;
import static db.WikipediaConnector.getTestConnection;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class WikipediaLanguageConnector extends WikipediaConnector {
    
    private static Connection researhConnection;
    private static String suffix = "";
    
    private static Properties getProperties(){
    	Properties prop = new Properties();
    	try {
			prop.load(WikipediaConnector.class.getClassLoader().getResourceAsStream("setup.properties"));
		} catch (IOException e) {	
		}
    	return prop;
    }

    public static Connection getResultsConnection(String langCode) throws ClassNotFoundException, SQLException{
        suffix = "_" + langCode;
        if (researhConnection == null || researhConnection.isClosed()) {
        	Class.forName("com.mysql.jdbc.Driver");
        	if (getProperties().getProperty("testEnvironment").equalsIgnoreCase("true")) {
        		try {
        			researhConnection = getTestConnection();
        		} catch (TestDatabaseSameThatWikipediaDatabaseException e) {
        			throw new SQLException("TestDatabaseSameThatWikipediaDatabaseException");
        		}
        	} else {
        		researhConnection = DriverManager.getConnection("jdbc:mysql://"+getResultDatabase()+"?user="+getResultDatabaseUser()+"&password="+getResultDatabasePass()+"&characterEncoding=utf8");
        	}
		}
        return researhConnection;
    }
    
    public static String getResultDatabase() {
        if (getProperties().getProperty("MULTIPLE_DATABASES").equalsIgnoreCase("true")) {
            return getProperties().getProperty("resultDatabase" + suffix);
        } else {
            return getProperties().getProperty("resultDatabase");
        }        
    }
    
    public static String getResultDatabaseUser() {
        if (getProperties().getProperty("MULTIPLE_DATABASES").equalsIgnoreCase("true")) {
            return getProperties().getProperty("resultDatabaseUser" + suffix);
        } else {
            return getProperties().getProperty("resultDatabaseUser");
        }        
    }
    
    public static String getResultDatabasePass() {
        if (getProperties().getProperty("MULTIPLE_DATABASES").equalsIgnoreCase("true") && suffix.length() != 0) {
            return getProperties().getProperty("resultDatabasePass" + suffix);
        } else {
            return getProperties().getProperty("resultDatabasePass");
        }        
    }
}
