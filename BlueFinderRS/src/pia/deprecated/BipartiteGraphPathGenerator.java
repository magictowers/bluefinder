
package pia.deprecated;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import db.PropertiesFileIsNotFoundException;
import db.WikipediaConnector;
import utils.ProjectConfigurationReader;


/**
 *
 * @author dtorres
 Main class to execute the Pia Index Algorithm. This class receives from console the basic params to initialize the indexing.
 The PIA Index is represented by means of three Mysql tables: U_Page: pairs of Wikipedia pages, V_Normalized: path queries and
 UxV: the edges set. This main class invokes the BipartiteGraphGenerator class.
 */
@Deprecated
public class BipartiteGraphPathGenerator {      

    /*public static void main(String[] args) throws ClassNotFoundException, SQLException, FileNotFoundException, IOException, PropertiesFileIsNotFoundException {
        Connection conReserarch = WikipediaConnector.getResultsConnection();
        Statement st = conReserarch.createStatement();
        int counter = 0;

        if (args.length < 4 || args[0].equalsIgnoreCase("help")) {
            System.out.println("Usage: <inf_limit> <max_limit> <iterations_limit> <from_to_table> <path generalization strategy> [clean]");
            System.out.println("Where:");
            System.out.println("\t\t<inf_limit> is a number which represents the min row in from_to_table\n\t\t<max_limit> is a number\n\t\t <iterations_limit> is a number\n\t\t<from_to_table> name of the sources table.");
            System.out.println("dbpedia prefix is the prefix to delete");
            System.out.println("write clean as 6 parameter to clean the index results");
            return;
        }
        String params = "";
        
        long inf_limit = Long.parseLong(args[0]);
        params += inf_limit + " ";
        long max_limit = Long.parseLong(args[1]);
        params += max_limit + " ";
        int iterations = Integer.parseInt(args[2]);
        params += iterations + " ";
        String from_to_table = args[3];
        
		String dbpediaPrefix = ProjectConfigurationReader.dbpediaPrefix();
        
        final String pathGeneralizator = args[4];
        PIAConfigurationBuilder.setGeneralizator(pathGeneralizator);
        
		String clean = "tidy";
        if(args.length == 6){
        	clean = args[5];
        	System.out.println("Clean = "+ clean);
            params += "clean ";
        }
        params += from_to_table + " ";
        System.out.println("Params: " + params + "\n");

        long start = System.nanoTime();
        
        BipartiteGraphGenerator bgg = PIAConfigurationBuilder.getBipartiteGraphGenerator(iterations);
        
        if (clean.equalsIgnoreCase("clean")) {
        	WikipediaConnector.restoreResultIndex();
        }
        
        ResultSet resultSet = st.executeQuery("SELECT * FROM " + from_to_table + " LIMIT " + inf_limit + " , " + max_limit);
        long singleCaseElapsedMillis;
        while (resultSet.next()) {
        	String to = resultSet.getString("to");
            to = URLDecoder.decode(to, "UTF-8");
            String from = resultSet.getString("from");
            from = URLDecoder.decode(from, "UTF-8");
            from = from.replace(dbpediaPrefix, "");
            to = to.replace(dbpediaPrefix, "");
            System.out.printf("Case %d: processing paths from %s to %s\n", counter, from, to);
            singleCaseElapsedMillis = System.nanoTime();
            bgg.generateBiGraph(from, to);
            System.out.printf("Elapsed time for case %d: %f seconds.\n\n", counter, 
                              (double)(System.nanoTime() - singleCaseElapsedMillis) / 1000000000.0);
            counter++;
        }

        long elapsedTimeMillis = System.nanoTime() - start;
         
        System.out.println("Regular generated paths = " + bgg.getRegularGeneratedPaths());
        System.out.println("Elapsed time in nanoseconds " + elapsedTimeMillis);
        double seconds = (double)elapsedTimeMillis / 1000000000.0;
        System.out.println("Elapsed time in seconds " + seconds);
        System.out.println("Finished.");
        st.close();
        conReserarch.close();
     }*/
 }