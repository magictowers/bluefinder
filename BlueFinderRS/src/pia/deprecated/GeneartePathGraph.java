/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pia.deprecated;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import db.PropertiesFileIsNotFoundException;
import db.WikipediaConnector;

/**
 *
 * @author dtorres
 */
@Deprecated
public class GeneartePathGraph {
/**
    public static void main(String[] args) throws IOException, ClassNotFoundException, SQLException, PropertiesFileIsNotFoundException {
        File file = new File("Paths.txt");
        BufferedWriter output = new BufferedWriter(new FileWriter(file));

        String text = "digraph G { \n"
                + "compound=true;\n"
                + "concentrate=true;\n"
                + "ranksep=1.25;\n"
                + "node [shape=plaintext, fontsize=16];\n"
                + "bgcolor=white;\n"
                + "edge [arrowsize=1, color=black];\n";

        output.write(text);

        Connection c = WikipediaConnector.getResultsConnection();
        Statement st = c.createStatement();
        String query_text = "SELECT path,id FROM V_Normalized";
        System.out.println(query_text);

        ResultSet rs = st.executeQuery(query_text);
        while (rs.next()) {
            text = rs.getString("path");
            String toWrite = text.replaceAll("/", "\" -> \"");
            
            output.write("\""+toWrite+"\"\n" );
        }
        output.close ();
    }    
*/}
