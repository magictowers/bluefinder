/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package db;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 *
 * @author dtorres
 */
public class WikipediaConnector {
    
    public final static String HOST = "localhost";
    public final static String SCHEMA = "eswiki";
    public final static String USER = "root";
    public final static String PASS = "root";
    
    public final static String RHOST = "localhost";
    public final static String RSCHEMA = "dbresearch";
    public final static String RUSER = "root";
    public final static String RPASS = "root";
    
    
    private  static Connection wikiConnection;
    private  static Connection researhConnection;
    
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
        researhConnection = DriverManager.getConnection("jdbc:mysql://localhost/dbresearch?user=root&password=root&characterEncoding=utf8");
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
		return getProperties().getProperty("testDatabasePass");

	}

	public static String getTestDatabaseUser() {
		return getProperties().getProperty("testDatabaseUser");
	}
	
	public static String getTestDatabasePass() {
		return getProperties().getProperty("testDatabasePass");
	}

    
//    public static Connection getConnection() throws ClassNotFoundException, SQLException{
//        
//        Class.forName("com.mysql.jdbc.Driver");
//       // Connection con = DriverManager.getConnection("jdbc:mysql://"+WikipediaConnector.HOST+"/"+WikipediaConnector.SCHEMA+"", WikipediaConnector.USER, WikipediaConnector.PASS);
//        Connection con = DriverManager.getConnection("jdbc:mysql://localhost/dbwikipedia?user=root&password=root&useUnicode=true&characterEncoding=utf8");
//                           
//                          
//        return con;
//        
//    }
//    
//    public static Connection getResultsConnection() throws ClassNotFoundException, SQLException{
//        
//        Class.forName("com.mysql.jdbc.Driver");
//        //Connection con = DriverManager.getConnection("jdbc:mysql://"+WikipediaConnector.RHOST+"/"+WikipediaConnector.RSCHEMA, WikipediaConnector.USER, WikipediaConnector.RPASS);
//        Connection con = DriverManager.getConnection("jdbc:mysql://localhost/dbpediaresearch?user=root&password=root&useUnicode=true&characterEncoding=utf8");
//        return con;
//        
//    }
//     
   
    
}
