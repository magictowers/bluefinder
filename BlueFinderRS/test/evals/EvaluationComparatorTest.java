/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package evals;

import db.WikipediaConnector;
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
     */
    @Test
    public void testGetConventions1() {
        FromToPair pairEn = new FromToPair("Abadía_de_Claraval", "Bernardo_de_Claraval", "en");
        FromToPair pairEs = new FromToPair("Abadía_de_Fontevrault", "Isabel_de_Angulema", "es");
        Set<String> expected = new HashSet<String>();
        expected.add(FromToPair.FROM_WILDCARD + PathsResolver.STEP_SEPARATOR + FromToPair.TO_WILDCARD);
        Set<String> actual = this.evalComparator.findConventions(pairEn, pairEs);
        assertEquals("No tienen la misma cantidad de convenciones", expected, actual);
        for (String strExpected : expected) {
            assertTrue("El path no se encuentra dentro de las convenciones.", actual.contains(strExpected));
        }        
    }
    
    /**
     * 
     */
    @Test
    public void testGetConventions2() {
        FromToPair pairEn = new FromToPair("Alberta", "Alexander_Rutherford", "es");
        FromToPair pairEs = new FromToPair("Alberta", "Ralph_Klein", "es");
        Set<String> expected = new HashSet<String>();
        expected.add(FromToPair.FROM_WILDCARD + PathsResolver.STEP_SEPARATOR + "*" + PathsResolver.STEP_SEPARATOR +
                PathsResolver.CATEGORY_PREFIX + "Anglo-albertanos" + PathsResolver.STEP_SEPARATOR + FromToPair.TO_WILDCARD);
        expected.add(FromToPair.FROM_WILDCARD + PathsResolver.STEP_SEPARATOR + "*" + PathsResolver.STEP_SEPARATOR +
                PathsResolver.CATEGORY_PREFIX + "Premiers_of_" + FromToPair.FROM_WILDCARD + 
                PathsResolver.STEP_SEPARATOR + FromToPair.TO_WILDCARD);
        Set<String> actual = this.evalComparator.findConventions(pairEn, pairEs);
        assertEquals("No tienen la misma cantidad de convenciones", expected, actual);
        for (String strExpected : expected) {
            assertTrue("El path no se encuentra dentro de las convenciones.", actual.contains(strExpected));
        }        
    }
}
