/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package strategies;

import db.WikipediaConnector;
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
public class UnstarredPathGeneralizationTest {
    
    private IGeneralization pathGeneralizator;
    
    public UnstarredPathGeneralizationTest() {
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
        this.pathGeneralizator = new UnstarredPathGeneralization();
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
    public void generalizePathQuery() {
        String path = FromToPair.FROM_WILDCARD + PathsResolver.STEP_SEPARATOR + FromToPair.TO_WILDCARD;
        assertEquals(this.pathGeneralizator.generalizePathQuery(path), path);
        path = FromToPair.FROM_WILDCARD + PathsResolver.STEP_SEPARATOR + 
                PathsResolver.CATEGORY_PREFIX + "First_Category_Name" + PathsResolver.STEP_SEPARATOR + 
                PathsResolver.CATEGORY_PREFIX + "Second_Category_Name" + PathsResolver.STEP_SEPARATOR +
                PathsResolver.CATEGORY_PREFIX + "Third_Category_Name" + PathsResolver.STEP_SEPARATOR +
                FromToPair.TO_WILDCARD;
        assertEquals(this.pathGeneralizator.generalizePathQuery(path), path);        
    }
}
