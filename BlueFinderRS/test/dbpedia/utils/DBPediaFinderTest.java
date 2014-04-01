/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package dbpedia.utils;

import db.WikipediaConnector;
import db.utils.ResultsDbInterface;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Assume;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.RepositoryException;
import utils.ProjectConfiguration;

/**
 *
 * @author mkaminose
 */
public class DBPediaFinderTest {
    
    private String dbpediaPrefix;
    private String dbpediaPrefixEng;
        
    public DBPediaFinderTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
        Assume.assumeTrue(WikipediaConnector.isTestEnvironment());
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        try {
            Connection conn = WikipediaConnector.getResultsConnection();
            Statement stmt = conn.createStatement();
            stmt.execute("DROP TABLE IF EXISTS " + ProjectConfiguration.fromToTable());
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(DBPediaFinderTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(DBPediaFinderTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    @After
    public void tearDown() {
    }

    // @Test
    public void testDBpediaFinder1() {
        String args[] = new String[6];
        args[0] = "true";
        args[1] = "SELECT ?from, ?to, ?fromEng, ?toEng WHERE { ?fromEng a <http://dbpedia.org/ontology/MusicalWork>. ?toEng a <http://dbpedia.org/ontology/Person>. ?toEng <http://dbpedia.org/ontology/associatedBand> ?fromEng. ?fromEng owl:sameAs ?from. ?toEng owl:sameAs ?to. FILTER regex(str(?from), \"^http://es.dbpedia.org/resource/\"). FILTER regex(str(?to), \"^http://es.dbpedia.org/resource/\"). }";
        args[2] = ProjectConfiguration.fromToTable();
        args[3] = ProjectConfiguration.resultDatabase();
        args[4] = ProjectConfiguration.resultDatabaseUser();
        args[5] = ProjectConfiguration.resultDatabasePassword();
        
        try {
            DBPediaFinder.main(args);
            ResultsDbInterface resultDb = new ResultsDbInterface();
            List<Map<String, String>> results = resultDb.getDbpediaTuplesSingle(null, null);
            this.dbpediaPrefix = "http://es.dbpedia.org/resource/";
            this.dbpediaPrefixEng = "http://dbpedia.org/resource/";
            for (Map<String, String> elem : results) {
                assertTrue(String.format("%s doesn't start with %s", elem.get("fromTrans"), dbpediaPrefixEng), elem.get("fromTrans").startsWith(dbpediaPrefixEng));
                assertTrue(String.format("%s doesn't start with %s", elem.get("toTrans"), dbpediaPrefixEng), elem.get("toTrans").startsWith(dbpediaPrefixEng));
                assertTrue(String.format("%s doesn't start with %s", elem.get("from"), dbpediaPrefix), elem.get("from").startsWith(dbpediaPrefix));
                assertTrue(String.format("%s doesn't start with %s", elem.get("to"), dbpediaPrefix), elem.get("to").startsWith(dbpediaPrefix));
            }
        } catch (Exception ex) {
            System.out.println(ex);
            fail(ex.getMessage());
        } 
    }
    
    @Test
    public void testDBpediaFinder2() {
        String args[] = new String[6];
        args[0] = "false";
        args[1] = "SELECT ?from, ?to, ?fromEng, ?toEng WHERE { ?fromEng a <http://dbpedia.org/ontology/MusicalWork>. ?toEng a <http://dbpedia.org/ontology/Person>. ?toEng <http://dbpedia.org/ontology/associatedBand> ?fromEng. ?fromEng owl:sameAs ?from. ?toEng owl:sameAs ?to. FILTER regex(str(?from), \"^http://fr.dbpedia.org/resource/\"). FILTER regex(str(?to), \"^http://fr.dbpedia.org/resource/\"). }";
        args[2] = ProjectConfiguration.fromToTable();
        args[3] = ProjectConfiguration.resultDatabase();
        args[4] = ProjectConfiguration.resultDatabaseUser();
        args[5] = ProjectConfiguration.resultDatabasePassword();
        
        try {
            DBPediaFinder.main(args);
            ResultsDbInterface resultDb = new ResultsDbInterface();
            List<Map<String, String>> results = resultDb.getDbpediaTuplesSingle(null, null);
            this.dbpediaPrefix = "http://fr.dbpedia.org/resource/";
            this.dbpediaPrefixEng = "http://dbpedia.org/resource/";
            for (Map<String, String> elem : results) {
                assertNull(elem.get("fromTrans"));
                assertNull(elem.get("toTrans"));
                assertTrue(String.format("%s doesn't start with %s", elem.get("from"), dbpediaPrefix), elem.get("from").startsWith(dbpediaPrefix));
                assertTrue(String.format("%s doesn't start with %s", elem.get("to"), dbpediaPrefix), elem.get("to").startsWith(dbpediaPrefix));
            }
        } catch (Exception ex) {
            System.out.println(ex);
            fail(ex.getMessage());
        } 
    }
}
