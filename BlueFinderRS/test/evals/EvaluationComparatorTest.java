/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package evals;

import db.WikipediaConnector;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Assume;
import utils.FromToPair;
import utils.PathsResolver;

/**
 *
 * @author mkaminose
 */
public class EvaluationComparatorTest {
    
    private EvaluationComparator evalComparator;
    
    public EvaluationComparatorTest() {
    }
    
    @BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Assume.assumeTrue(WikipediaConnector.isTestEnvironment());
        try {
            WikipediaConnector.executeSqlFromFile("test_p06_associatedBand_es.sql");
            WikipediaConnector.executeSqlFromFile("test_p06_associatedBand_fr.sql");
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error while loading required dumps. Cannot execute tests correctly.");
        }
	}
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        this.evalComparator = new EvaluationComparator();
    }
    
    @After
    public void tearDown() {
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}
    
    /**
     * Evaluate to true, they both tuples contain only one path, the direct one.
     * @throws java.sql.SQLException
     * @throws java.lang.ClassNotFoundException
     */
    @Test
    public void testGetConventions1() throws SQLException, ClassNotFoundException {
        String from = "Band_of_Gypsys";
        String to = "Jimi_Hendrix";
        Set<String> expected = new HashSet<String>();
        expected.add(FromToPair.FROM_WILDCARD + PathsResolver.STEP_SEPARATOR + FromToPair.TO_WILDCARD);
        Set<String> actual = this.evalComparator.findConventions(from, to, "conf1", "conf2");
        assertEquals("No tienen la misma cantidad de convenciones", expected, actual);
        for (String strExpected : expected) {
            assertTrue("El path no se encuentra dentro de las convenciones.", actual.contains(strExpected));
        }        
    }
    
}
