package executions;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import pia.BipartiteGraphGenerator;
import pia.BipartiteGraphPathGenerator;
import pia.PIAConfigurationBuilder;
import db.WikipediaConnector;

public class PIATranslatedToEnglish {
	
	public static void main(String[] args) throws UnsupportedEncodingException, ClassNotFoundException, SQLException {
        Connection conReserarch = WikipediaConnector.getResultsConnection();
        Statement st = conReserarch.createStatement();
        int counter = 0;


        if (args.length < 4 || args[0].equalsIgnoreCase("help")) {
            System.out.println("Usage: <inf_limit> <max_limit> <iterations_limit> <from_to_table>");
            System.out.println("Where:");
            System.out.println("\t\t<inf_limit> is a number which represents the min row in from_to_table\n\t\t<max_limit> is a number\n\t\t <iterations_limit> is a number\n\t\t<from_to_table> name of the sources table.");
            return;
        }



        int inf_limit = Integer.parseInt(args[0]);
        int max_limjt = Integer.parseInt(args[1]);
        int iterations = Integer.parseInt(args[2]);
        String from_to_table = args[3];

        long start = System.nanoTime();
        BipartiteGraphGenerator bgg = PIAConfigurationBuilder.interlanguageWikipedia(iterations);
        BipartiteGraphPathGenerator.resetTables();

        ResultSet resultSet = st.executeQuery("SELECT * FROM " + from_to_table + " limit " + inf_limit + " ," + max_limjt);
        while (resultSet.next()) {
            String to = resultSet.getString("to");
            to = URLDecoder.decode(to, "UTF-8");
            String from = resultSet.getString("from");
            from = URLDecoder.decode(from, "UTF-8");
            from = from.substring(31);
            to = to.substring(31);
            System.out.println("Processing paths from " + from + " to " + to + "CASE: " + counter++);
            bgg.generateBiGraph(from, to);
            //System.out.println("Done !");
        }

        long elapsedTimeMillis = System.nanoTime() - start;
        
        //float elapsedTimeSec = elapsedTimeMillis/1000F;

        System.out.println("Regular generated paths = " + bgg.getRegularGeneratedPaths());
        System.out.println("Elapsed time in nanoseconds" + elapsedTimeMillis);

        System.out.println("Finalized !!!!");
        st.close();
        conReserarch.close();


    }

}
