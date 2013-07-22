package pia;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import normalization.INormalizator;

import org.junit.Test;

import db.TestDatabaseSameThatWikipediaDatabaseException;
import db.WikipediaConnector;

public class PathFinderTest extends PathFinder{
    
    private PathFinder pathFinder ;
    
    @BeforeClass
    public static void setUpClass() throws FileNotFoundException, ClassNotFoundException, SQLException, TestDatabaseSameThatWikipediaDatabaseException, IOException {
    	WikipediaConnector.restoreTestDatabase();
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
    
    
    /**Evalua que retorne en forma correcta el id en la tabla de Wikipedia del titulo de la pagina
     * 
     * @throws ClassNotFoundException
     */
    @Test
	public void testGetPageId() throws ClassNotFoundException{
		assertSame(6,this.getPageId("Rosario,_Santa_Fe"));
		assertSame("No retorna 0 cuando la pagina no existe", 0,this.getPageId("Rosario")); // No existe y por eso retorna 0
	}
    
    @Test
    public void testGetCategoryId() throws ClassNotFoundException{
     assertSame(5, this.getCatPageId("Sportspeople_from_Liverpool"));
     assertSame(0, this.getCatPageId("Sportspeople_from_Live"));
     assertSame(9, this.getCatPageId("Rosario,_Santa_Fe")); // this case is the category.
    }
    
    @Test
    public void testGetCategoriesFromPage() throws UnsupportedEncodingException, ClassNotFoundException, SQLException{
    	List<String> expected = new ArrayList<String>();
    	expected.add("People_from_Rosario,_Santa_Fe");
    	List<String> actual = this.pathFinder.getCategoriesFromPage("Lionel_Messi");
    	assertEquals("Wrong categories from page",expected,actual);
    	
    }
    
}