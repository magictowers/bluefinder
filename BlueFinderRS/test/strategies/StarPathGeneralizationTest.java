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

/**
 *
 * @author mkaminose
 */
public class StarPathGeneralizationTest {
    
    public StarPathGeneralizationTest() {
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
    
    @Before
    public static void setUpBeforeClass() throws Exception {
		Assume.assumeTrue(WikipediaConnector.isTestEnvironment());
	}
}
