/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package utils;

import db.WikipediaConnector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
public class PathsResolverTest {
    
    private PathsResolver pathsResolver;
    
    @BeforeClass
    public static void setUpClass() throws Exception {
        Assume.assumeTrue(WikipediaConnector.isTestEnvironment());
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        this.pathsResolver = new PathsResolver();
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void testPathToString() {
        List<String> chunks = new ArrayList<String>();
        chunks.add(FromToPair.FROM_WILDCARD);
        chunks.add(FromToPair.TO_WILDCARD);
        String expected = FromToPair.FROM_WILDCARD + PathsResolver.STEP_SEPARATOR + FromToPair.TO_WILDCARD;
        assertEquals(expected, PathsResolver.pathToString(chunks));
        chunks.clear();
        
        chunks.add(FromToPair.FROM_WILDCARD);
        chunks.add("First_Level");
        chunks.add(PathsResolver.CATEGORY_PREFIX + "Second_Level");
        chunks.add(FromToPair.TO_WILDCARD);
        expected = FromToPair.FROM_WILDCARD + PathsResolver.STEP_SEPARATOR + "First_Level" + 
                PathsResolver.STEP_SEPARATOR + PathsResolver.CATEGORY_PREFIX + "Second_Level" + 
                PathsResolver.STEP_SEPARATOR + FromToPair.TO_WILDCARD;
        assertEquals(expected, PathsResolver.pathToString(chunks));
    }

    @Test
    public void testDecouple() {
        String strMap = "{#from / * / Cat:#from_politicians / #to=8, "
                + "#from / #to=4, "
                + "#from / * / Cat:German_women_in_politics / #to=2, "
                + "#from / * / Cat:German_communists / #to=2, "
                + "#from / * / Cat:German_Revolution_of_1918–19 / #to=1}";
        Map<String, Integer> expected = new HashMap<String, Integer>();
        expected.put("#from / * / Cat:#from_politicians / #to", 8);
        expected.put("#from / #to", 4);
        expected.put("#from / * / Cat:German_women_in_politics / #to", 2);
        expected.put("#from / * / Cat:German_communists / #to", 2);
        expected.put("#from / * / Cat:German_Revolution_of_1918–19 / #to", 1);
        assertEquals(expected, this.pathsResolver.decouple(strMap));
    }
    
    @Test
    public void testSimpleDecoupledPaths() {
        String strMap = "{#from / * / Cat:#from_politicians / #to=8, "
                + "#from / #to=4, "
                + "#from / * / Cat:German_women_in_politics / #to=2, "
                + "#from / * / Cat:German_communists / #to=2, "
                + "#from / * / Cat:German_Revolution_of_1918–19 / #to=1}";
        List<String> expected = new ArrayList<String>();
        expected.add("#from / * / Cat:#from_politicians / #to");
        expected.add("#from / #to");
        expected.add("#from / * / Cat:German_women_in_politics / #to");
        expected.add("#from / * / Cat:German_communists / #to");
        expected.add("#from / * / Cat:German_Revolution_of_1918–19 / #to");
        assertEquals(expected, this.pathsResolver.simpleDecoupledPaths(strMap));
    }
}
