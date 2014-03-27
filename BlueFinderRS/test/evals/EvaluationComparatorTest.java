/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package evals;

import db.WikipediaConnector;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
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
import utils.ProjectConfiguration;

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
            ProjectConfiguration.useProperties1();
            WikipediaConnector.executeSqlFromFile("test_p06_associatedBand_es.sql");
            ProjectConfiguration.useProperties2();
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
    
    /**
     * Evaluate to true, they both tuples contain only one path, the direct one.
     * @throws java.sql.SQLException
     * @throws java.lang.ClassNotFoundException
     */
//    @Test
    public void testFindConventionsEnglishPairs1() throws SQLException, ClassNotFoundException {
        String from = "Band_of_Gypsys";
        String to = "Jimi_Hendrix";
        Set<String> expected = new HashSet<String>();
        expected.add(FromToPair.FROM_WILDCARD + PathsResolver.STEP_SEPARATOR + FromToPair.TO_WILDCARD);
        Set<String> actual = this.evalComparator.findConventions(from, to, "conf1", "conf2");
        assertEquals(String.format("%s y %s no tienen la misma cantidad de convenciones", from, to), expected, actual);
        for (String strExpected : expected) {
            assertTrue(String.format("Entre %s y %s, el path no se encuentra dentro de las convenciones.", from, to), 
                    actual.contains(strExpected));
        }        
    }
    
    /**
     * Any convention between English tuples.
     * 
     * @throws SQLException
     * @throws ClassNotFoundException 
     */
//    @Test
    public void testFindConventionsEnglishPairs2() throws SQLException, ClassNotFoundException {
        String from = "Ringo_Starr_and_His_All-Starr_Band";
        String to = "Roger_Hodgson";
        Set<String> actual = this.evalComparator.findConventions(from, to, "conf1", "conf2");
        assertTrue(String.format("Entre %s y %s no debería tener convenciones", from, to), actual.isEmpty());
        
        from = "Probot";
        to = "Jack_Black";
        actual = this.evalComparator.findConventions(from, to, "conf1", "conf2");
        assertTrue(String.format("Entre %s y %s no debería tener convenciones", from, to), actual.isEmpty());
    }
 
//    @Test
    public void testFindConventionsMultilangPairs1() {
        String from1 = "Killers_(álbum)";
        String to1 = "Paul_Di'Anno";
        String from2 = "Killers_(album)";
        String to2 = "Paul_Di'Anno";
        Set<String> expected = new HashSet<String>();
        expected.add(FromToPair.FROM_WILDCARD + PathsResolver.STEP_SEPARATOR + FromToPair.TO_WILDCARD);
        Set<String> actual = this.evalComparator.findConventions(from1, to1, "conf1", from2, to2, "conf2");
        assertEquals(String.format("%s-%s y %s-%s no tienen la misma cantidad de convenciones", from1, to1, from2, to2), 
                expected, actual);
        for (String strExpected : expected) {
            assertTrue(String.format("Entre %s-%s y %s-%s, el path no se encuentra dentro de las convenciones.", from1, to1, from2, to2), 
                    actual.contains(strExpected));
        }
        
        from1 = "Déjà_Vu_(canción_de_Inna)";
        to1 = "Inna";
        from2 = "Déjà_Vu_(chanson_d'Inna)";
        to2 = "Inna";
        expected = new HashSet<String>();
        expected.add(FromToPair.FROM_WILDCARD + PathsResolver.STEP_SEPARATOR + FromToPair.TO_WILDCARD);
        actual = this.evalComparator.findConventions(from1, to1, "conf1", from2, to2, "conf2");
        assertEquals(String.format("%s-%s y %s-%s no tienen la misma cantidad de convenciones", from1, to1, from2, to2), 
                expected, actual);
        for (String strExpected : expected) {
            assertTrue(String.format("Entre %s-%s y %s-%s, el path no se encuentra dentro de las convenciones.", from1, to1, from2, to2), 
                    actual.contains(strExpected));
        } 
    }

    /**
     * Any convention between languages.
     */
//    @Test
    public void testFindConventionsMultilangPairs2() {        
        String from1 = "Original_Soundtracks_1";
        String to1 = "Larry_Mullen_Jr.";
        String from2 = "Original_Soundtracks_1";
        String to2 = "Larry_Mullen_Junior";
        Set<String> actual = this.evalComparator.findConventions(from1, to1, "conf1", from2, to2, "conf2");
        assertTrue(String.format("%s-%s y %s-%s no deberían tener convenciones", from1, to1, from2, to2), actual.isEmpty());
               
        from1 = "Identity_(álbum)";
        to1 = "Richard_Wright_(músico)";
        from2 = "Identity_(album)";
        to2 = "Richard_Wright_(musicien)";
        actual = this.evalComparator.findConventions(from1, to1, "conf1", from2, to2, "conf2");
        assertTrue(String.format("%s-%s y %s-%s no deberían tener convenciones", from1, to1, from2, to2), actual.isEmpty());
    }

    @Test
    public void testFindConventionsWholePairs1() throws ClassNotFoundException, SQLException {
        Map<String, Set<String>> expected = new HashMap<String, Set<String>>();
        Set<String> paths = new HashSet<String>();
        paths.add("#from / #to");
        expected.put(String.format("%s%s%s", FromToPair.concatPair("The_Good,_the_Bad_and_the_Queen", "Paul_Simonon"), PathsResolver.STEP_SEPARATOR, 
                FromToPair.concatPair("The_Good,_the_Bad_and_the_Queen_(album)", "Paul_Simonon")), paths);
        expected.put(String.format("%s%s%s", FromToPair.concatPair("Rocket_juice_and_The_Moon", "Flea"), PathsResolver.STEP_SEPARATOR, 
                FromToPair.concatPair("Rocket_Juice_and_The_Moon", "Michael_Balzary")), paths);
        expected.put(String.format("%s%s%s", FromToPair.concatPair("Rocket_juice_and_The_Moon", "Damon_Albarn"), PathsResolver.STEP_SEPARATOR, 
                FromToPair.concatPair("Rocket_Juice_and_The_Moon", "Damon_Albarn")), paths);
        expected.put(String.format("%s%s%s", FromToPair.concatPair("Original_Soundtracks_1", "Larry_Mullen_Jr."), PathsResolver.STEP_SEPARATOR, 
                FromToPair.concatPair("Original_Soundtracks_1", "Larry_Mullen_Junior")), new HashSet<String>());
        expected.put(String.format("%s%s%s", FromToPair.concatPair("Original_Soundtracks_1", "Bono_(músico)"), PathsResolver.STEP_SEPARATOR, 
                FromToPair.concatPair("Original_Soundtracks_1", "Bono")), paths);
        
        Map<String, Set<String>> actual = this.evalComparator.findConventions("conf1", "conf2", 5, 26);
        assertEquals("La cantidad de elementos analizados no es la misma", expected.size(), actual.size());
        
        assertEquals("No compararon las mismas cosas", expected.keySet(), actual.keySet());
        for (String key : actual.keySet()) {
            assertEquals(String.format("Diferencia de convenciones con %s", key), expected.get(key), actual.get(key));
        }
        
    }
}
