/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pia.deprecated;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import db.PropertiesFileIsNotFoundException;
import db.WikipediaConnector;

/**
 *
 * @author dtorres
 */
@Deprecated
public class FixDirectLinked {

 /*   public static void main(String[] args) throws ClassNotFoundException, SQLException, PropertiesFileIsNotFoundException {
        Connection conReserarch = WikipediaConnector.getResultsConnection();
        Statement st = conReserarch.createStatement();
        BipartiteGraphGenerator bgg = new BipartiteGraphGenerator();


        int counter = 0;
        String query = "select id, v_from, u_to from NFPC";
        ResultSet result = st.executeQuery(query);
        List<String> normalized = new ArrayList<String>();
        normalized.add("[to]");
        while (result.next()) {
            String from = result.getString("v_from");
            String to = result.getString("u_to");
            int id = result.getInt("id");

            if (bgg.areDirectLinked(from, to)) {
                // add normalized path to V_normalized
                int path = bgg.getNormalizedPathIdIntoDB(normalized);
                //add U_page
                int dbPageId = bgg.getTupleIdIntoDB("(" + from + ", " + to + ")");
                if (!(dbPageId == 0 || path == 0)) {
                    bgg.addEdge(path, dbPageId);
                    counter++;
                    bgg.removeNotFound(id);
                    System.out.println("Found domain: " + from + " target: " + to);

                }
            }
        }
        System.out.println("Number of fixed: " + counter);
    }*/
}
