/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dbpedia.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import org.openrdf.model.Value;
import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryException;
import dbpedia.utils.sesame.QueryRetriever;

/**
 *
 * @author dtorres
 */
public class DBPediaFinder {

    public static void main(String[] args) throws MalformedQueryException, RepositoryException, QueryEvaluationException, ClassNotFoundException, SQLException {

        if (args.length < 6) {
            System.out.println("The number of params is not correct. Please use the following example as template");
            System.out.println("DBPediaFinder: to_translate[true|false] \"select ?from, ?to where {?from a <http://dbpedia.org/ontology/University>. ?from <http://dbpedia.org/ontology/city> ?to }\" uni2city localhost/results user userpass");
            return;
        }

        boolean translate = Boolean.parseBoolean(args[0]);
        String sparqlQuery = args[1];
        String table = args[2];
        String base = args[3];
        String user = args[4];
        String pass = args[5];
        System.out.println(sparqlQuery);

        if (base.equalsIgnoreCase("localhost/dtorres")) {
            System.out.println("ERROR: localhost/dtorres is not a valid name for the database");
            return;
        }

        //String query = "select ?from, ?to where {?uni a <http://dbpedia.org/ontology/University>. ?uni <http://dbpedia.org/ontology/city> ?city }";
        Class.forName("com.mysql.jdbc.Driver");
        Connection conexion = DriverManager.getConnection("jdbc:mysql://" + base + "?useUnicode=true&characterEncoding=utf8", user, pass);

        String query;
        if (translate)
            query = "CREATE TABLE IF NOT EXISTS `" + table + "` (`from` varchar(800) NOT NULL, `to` varchar(800) NOT NULL, `fromTrans` varchar(800) NOT NULL, `toTrans` varchar(800) NOT NULL) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci";
        else
            query = "CREATE TABLE IF NOT EXISTS `" + table + "` (`from` varchar(800) NOT NULL, `to` varchar(800) NOT NULL, `fromTrans` varchar(800), `toTrans` varchar(800)) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci";
       
        Statement createST = conexion.createStatement();
        createST.executeUpdate(query);
        createST.close();
        createST = conexion.createStatement();
        createST.executeUpdate("truncate table `" + table + "`");

        int counter = 0;
        QueryRetriever qr = new QueryRetriever(10000, sparqlQuery, 10000000);
        while (qr.hasNext()) {
            TupleQueryResult queryResult = qr.getNextPage();
            while (queryResult.hasNext()) {
                BindingSet bs = queryResult.next();
                Value from = bs.getValue(qr.from);
                Value to = bs.getValue(qr.to);
                PreparedStatement stmt;
                if (translate) {
                    String strFromTrans = "";
                    String strToTrans = "";
                    Value fromTrans = bs.getValue(qr.fromTrans);
                    Value toTrans = bs.getValue(qr.toTrans);
                    if (fromTrans != null) {
                        try {
                            strFromTrans = URLDecoder.decode(fromTrans.toString(), "UTF-8");
                        } catch (UnsupportedEncodingException ex) {
                            strFromTrans = fromTrans.toString();
                        }
                    }
                    if (toTrans != null) {
                        try {
                            strToTrans = URLDecoder.decode(toTrans.toString(), "UTF-8");
                        } catch (UnsupportedEncodingException ex) {
                            strToTrans = to.toString();
                        }
                    }
                    
                    query = "INSERT INTO " + table + " (`from`, `to`, `fromTrans`, `toTrans`) VALUES (?, ?, ?, ?)";
                    stmt = conexion.prepareStatement(query);
                    stmt.setString(3, strFromTrans);
                    stmt.setString(4, strToTrans);
                } else {
                    query = "INSERT INTO " + table + " (`from`, `to`) VALUES (?, ?)";
                    stmt = conexion.prepareStatement(query);
                }
                
                if (!(from == null || to == null)) {
                    stmt.setString(1, from.toString());
                    stmt.setString(2, to.toString());
                    stmt.executeUpdate();
                    counter++;
                    stmt.close();
                } else {
                    System.out.println("NULL EN ALGUNO DE LOS VALORES");
                }
            }
            System.out.println(counter);
        }
        System.out.println("Number of Inserts: " + counter);
    }

}
