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
    
    private EvaluationComparatorTest evalComparator;
    
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
    }
    
    @After
    public void tearDown() {
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}
    
    @Test
    public void testGetConventions() {
        FromToPair pairEn = new FromToPair("", "", "en");
        FromToPair pairEs = new FromToPair("", "", "es");
        Set<String> expected = new HashSet<String>();
        expected.add(FromToPair.FROM_WILDCARD + PathsResolver.STEP_SEPARATOR + FromToPair.TO_WILDCARD);
        expected.add(FromToPair.FROM_WILDCARD + PathsResolver.STEP_SEPARATOR + 
                PathsResolver.CATEGORY_PREFIX + FromToPair.FROM_WILDCARD +
                PathsResolver.STEP_SEPARATOR + FromToPair.TO_WILDCARD);
        expected.add(FromToPair.FROM_WILDCARD + PathsResolver.STEP_SEPARATOR + 
                PathsResolver.CATEGORY_PREFIX + "adsfasdfasdf" + PathsResolver.STEP_SEPARATOR + FromToPair.TO_WILDCARD);
        Set<Object> actual = this.evalComparator.getConventions(pairEn, pairEs);
        assertEquals("No tienen la misma cantidad de convenciones", expected, actual);
        for (String strExpected : expected) {
            assertTrue("El path no se encuentra dentro de las convenciones.", actual.contains(strExpected));
        }
        
    }
}
