/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

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

        public static Connection getConnection() throws ClassNotFoundException, SQLException{
        if(wikiConnection==null){
        Class.forName("com.mysql.jdbc.Driver");
       // Connection con = DriverManager.getConnection("jdbc:mysql://"+WikipediaConnector.HOST+"/"+WikipediaConnector.SCHEMA+"", WikipediaConnector.USER, Wiki$
        wikiConnection = DriverManager.getConnection("jdbc:mysql://localhost/"+WikipediaConnector.SCHEMA+"?user=root&password=root&useUnicode=true&characterEncoding=utf8");
       // wikiConnection = DriverManager.getConnection("jdbc:mysql://localhost/"+WikipediaConnector.SCHEMA+"?user=root&password=root");
        //wikiConnection = DriverManager.getConnection("jdbc:mysql://localhost/"+WikipediaConnector.SCHEMA+"?user=root&password=root");
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
