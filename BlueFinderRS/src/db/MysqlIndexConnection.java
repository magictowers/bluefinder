package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author dtorres
 */
public class MysqlIndexConnection {    
    
    private  static Connection indexConnection;
    private final static String indexSchema = "dbresearch";    
   
    public static Connection getIndexConnection() throws ClassNotFoundException, SQLException{
        if(indexConnection==null){
        	Class.forName("com.mysql.jdbc.Driver");
        	indexConnection = DriverManager.getConnection("jdbc:mysql://localhost/"+indexSchema+"?user=root&password=root&useUnicode=true&characterEncoding=utf8");
        }
        return indexConnection;
    }
    
    public static Connection getConnection(String dbName) throws ClassNotFoundException, SQLException{
    	if(indexConnection==null){
    		Class.forName("com.mysql.jdbc.Driver");
    		indexConnection = DriverManager.getConnection("jdbc:mysql://localhost/"+dbName+"?user=root&password=root&useUnicode=true&characterEncoding=utf8");
        }
        return indexConnection;
    }    
   
    public static Connection getResultsConnection(String dbName) throws ClassNotFoundException, SQLException{
        if (indexConnection==null) {
        	Class.forName("com.mysql.jdbc.Driver");
        	indexConnection = DriverManager.getConnection("jdbc:mysql://localhost/"+dbName+"?user=root&password=root&useUnicode=true&characterEncoding=utf8");
        }
        return indexConnection;
    }
}
