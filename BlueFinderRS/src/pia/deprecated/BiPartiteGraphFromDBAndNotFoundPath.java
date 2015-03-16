/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pia.deprecated;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import pia.BipartiteGraphGenerator;
import db.PropertiesFileIsNotFoundException;
import db.WikipediaConnector;

/**
 *
 * @author dtorres
 */
@Deprecated
public class BiPartiteGraphFromDBAndNotFoundPath {

  /*  public static void main(String[] args) throws UnsupportedEncodingException, ClassNotFoundException, SQLException, PropertiesFileIsNotFoundException {
        //String uri = "Jos%C3%A9_Mar%C3%ADa_Robles_Hurtado";
        //String decoded = URLDecoder.decode(uri, "UTF-8");
        Connection conReserarch = WikipediaConnector.getResultsConnection();
        Statement st = conReserarch.createStatement();
        int counter = 0;
        
                
        if(args.length < 3 || args[0].equalsIgnoreCase("help")){
            System.out.println("Usage: <inf_limit> <max_limit> <iterations_limit>");
            return;
        }*/
        
        
              
        int inf_limit = Integer.parseInt(args[0]);
        int max_limjt = Integer.parseInt(args[1]);
        int iterations = Integer.parseInt(args[2]);
        
        BipartiteGraphGenerator bgg = new BipartiteGraphGenerator(iterations);
         
        
        ResultSet resultSet = st.executeQuery("SELECT * FROM person_and_birthplace p limit "+inf_limit+" ,"+max_limjt);
        while (resultSet.next()) {
            String person = resultSet.getString("person");
            person = URLDecoder.decode(person, "UTF-8");
            String city = resultSet.getString("place");
            city = URLDecoder.decode(city, "UTF-8");
            city = city.substring(28);
            person = person.substring(28);
            System.out.println("Processing paths from "+city+" to "+person + "CASE: "+ counter++);
            bgg.generateBiGraph(city, person);
            System.out.println("Done !");

        }
        System.out.println("Finalized !!!!");
        st.close();
        conReserarch.close();

        
    }
}
