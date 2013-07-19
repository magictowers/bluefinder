package pia;

import static org.junit.Assert.*;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import normalization.INormalizator;

import org.junit.Test;

public class PathFinderTest {
    
    private PathFinder pathFinder ;
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        this.pathFinder = new PathFinder();
    }
    
    @After
    public void tearDown() {
    }
	/*public void test() throws UnsupportedEncodingException, ClassNotFoundException, SQLException {
		BipartiteGraphGenerator bgg = PIAConfigurationBuilder.interlanguageWikipedia(5);
		BipartiteGraphPathGenerator.resetTables();
		bgg.generateBiGraph("Abeja", "Queen");
		bgg.generateBiGraph("Abeja", "Charles_Darwin");
		bgg.generateBiGraph("Abeja", "Atanasio");
	}
	
	public static void main(String[] args) throws UnsupportedEncodingException, ClassNotFoundException, SQLException {
		System.out.println("Running...");
		BipartiteGraphGenerator bgg = PIAConfigurationBuilder.interlanguageWikipedia(5);
		BipartiteGraphPathGenerator.resetTables();
		//bgg.generateBiGraph("Abeja", "Queen");
		//bgg.generateBiGraph("Abeja", "Charles_Darwin");
		//bgg.generateBiGraph("Abeja", "Atanasio");
		//bgg.generateBiGraph("Abeja", "Mayo_franc�s");
		bgg.generateBiGraph("Mayo_franc�s", "Fran�ois_Mitterrand");
		}
*/ 

    /**
     * Test of incrementRegularGeneratedPaths method, of class PathFinder.
     */
    @Test
    public void testIncrementRegularGeneratedPaths() {
        System.out.println("incrementRegularGeneratedPaths");
        int expectedResult = this.pathFinder.getRegularGeneratedPaths() + 1;
        assertEquals(expectedResult, this.pathFinder.getRegularGeneratedPaths());
    }

    /**
     * Test of areDirectLinked method, of class PathFinder.
     */
    @Test
    public void testAreDirectLinked() {
        String from = "Liverpool";
        String to = "Chris_Lawler";
        try {
            boolean result = this.pathFinder.areDirectLinked(from, to);
            assertTrue(from + " and " + to + " are not directly linked error.", result);
        } catch (ClassNotFoundException ex) {
            fail("ClassNotFoundException");
            Logger.getLogger(PathFinderTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            fail("SQLException");
            Logger.getLogger(PathFinderTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Test of findPathBFS method, of class PathFinder.
     */
    @Test
    public void testFindPathBFS() throws Exception {
        System.out.println("findPathBFS");
        String from = "Liverpool";
        String to = "Chris_Lawler";
        boolean result = this.pathFinder.findPathBFS(from, to);
        assertTrue(result);
        fail("The test case is a prototype.");
    }

    /**
     * Test of getPathsUsingCategories method, of class PathFinder.
     */
    @Test
    public void testGetPathsUsingCategories() {
        String from = "Liverpool";
        String to = "Chris_Lawler";
        PathFinder pathFinder = new PathFinder();
        try {
            List<List<String>> paths = pathFinder.getPathsUsingCategories(from, to);
        } catch (ClassNotFoundException ex) {
            fail("ClassNotFoundException");
            Logger.getLogger(PathFinderTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            fail("SQLException");
            Logger.getLogger(PathFinderTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            fail("UnsupportedEncodingException");
            Logger.getLogger(PathFinderTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Test of getStringValue method, of class PathFinder.
     */
    @Test
    public void testGetStringValue() {
        System.out.println("getStringValue");
        byte[] varbinary = null;
        String expResult = "";
        String result = this.pathFinder.getStringValue(varbinary);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of normalizeCategory method, of class PathFinder.
     */
    @Test
    public void testNormalizeCategory() {
        System.out.println("normalizeCategory");
        String subCategoryName = "";
        String fromPage = "";
        String toPage = "";
        String expResult = "";
        String result = this.pathFinder.normalizeCategory(subCategoryName, fromPage, toPage);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getRelevantDocuments method, of class PathFinder.
     */
    @Test
    public void testGetRelevantDocuments() throws Exception {
        System.out.println("getRelevantDocuments");
        String pathQuery = "";
        int expResult = 0;
        int result = this.pathFinder.getRelevantDocuments(pathQuery);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of isReachablePath method, of class PathFinder.
     */
    @Test
    public void testIsReachablePath() throws Exception {
        System.out.println("isReachablePath");
        String pathQuery = "";
        String from = "";
        String to = "";
        boolean expResult = false;
        boolean result = this.pathFinder.isReachablePath(pathQuery, from, to);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
}