package pia;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import db.TestDatabaseSameThatWikipediaDatabaseException;
import db.WikipediaConnector;
import utils.ProjectSetup;
import utils.ProjectSetupForTest;

public class PathFinderTest extends PathFinder{
    
    private PathFinder pathFinder ;
    private List<String> expectedfromPeoplefromfrom;
    private List<String> expectedTo;
    private List<String> expectedListOf;
    private static ProjectSetup projectSetup;
    
    public PathFinderTest() throws SQLException, ClassNotFoundException {
        super();
        projectSetup = new ProjectSetupForTest();
    }
    
    @BeforeClass
    public static void setUpClass() throws FileNotFoundException, ClassNotFoundException, SQLException, TestDatabaseSameThatWikipediaDatabaseException, IOException {
        projectSetup = new ProjectSetupForTest();
        Assume.assumeTrue(projectSetup.isTestEnvironment());
    	WikipediaConnector.restoreTestDatabase();
    }
    
    
    @Before
    public void setUp() throws SQLException, ClassNotFoundException {
    	
        this.pathFinder = new PathFinder();
        this.expectedfromPeoplefromfrom = new ArrayList<String>();
        expectedfromPeoplefromfrom.add("#from"); expectedfromPeoplefromfrom.add("Cat:#from"); expectedfromPeoplefromfrom.add("Cat:People_from_#from"); expectedfromPeoplefromfrom.add("#to");
        
        this.expectedTo = new ArrayList<String>();
        expectedTo.add("#from");expectedTo.add("#to");
        
        this.expectedListOf = new ArrayList<String>();
        this.expectedListOf.add("#from");this.expectedListOf.add("List_of_VIP");this.expectedListOf.add("#to");
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
     
    @Test
    public void testIncrementRegularGeneratedPaths() {
        int expectedResult = this.pathFinder.getRegularGeneratedPaths() + 1;
        assertEquals(expectedResult, this.pathFinder.getRegularGeneratedPaths());
    }*/

    /**
     * Test of areDirectLinked method, of class PathFinder.
     * @throws SQLException 
     * @throws ClassNotFoundException 
     */
    @Test
    public void testAreDirectLinked() throws ClassNotFoundException, SQLException {
        String from = "Rosario,_Santa_Fe";
        String to = "Lionel_Messi";
            boolean result = this.pathFinder.areDirectLinked(from, to);
            assertTrue(from + " and " + to + " are not directly linked error.", result);
    }
    
    /**
     * Test of areDirectLinked method, of class PathFinder for negative case.
     * @throws SQLException 
     * @throws ClassNotFoundException 
     */
    @Test
    public void testAreDirectLinkedNegativeCase() throws ClassNotFoundException, SQLException {
        String from = "Santa_Fe";
        String to = "Lionel_Messi";
            boolean result = this.pathFinder.areDirectLinked(from, to);
            assertFalse(from + " and " + to + " are detected as directly linked error.", result);
    }

   
    /**
     * Test of getPathsUsingCategories method, of class PathFinder.
     * @throws SQLException 
     * @throws ClassNotFoundException 
     * @throws UnsupportedEncodingException 
     */
    @Test
    public void testGetPathsUsingCategories() throws UnsupportedEncodingException, ClassNotFoundException, SQLException {
        String from = "Rosario,_Santa_Fe";
        String to = "Lionel_Messi";
        PathFinder pathFinder = new PathFinder();
        pathFinder.setCategoryPathIterations(3);
        
        
        List<List<String>> expectedResult = new ArrayList<List<String>>();
        
        expectedResult.add(expectedTo);
        expectedResult.add(expectedfromPeoplefromfrom);
        expectedResult.add(this.expectedListOf);
        
        List<List<String>> paths = pathFinder.getPathsUsingCategories(from, to);
        
        
        
        assertEquals(expectedResult, paths);
        
     
        
    }

    /**
     * Test of getStringValue method, of class PathFinder.
     
    @Test
    public void testGetStringValue() {
        
        byte[] varbinary = null;
        String expResult = "";
        String result = this.pathFinder.getStringValue(varbinary);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }*/

    /**
     * Test of normalizeCategory method, of class PathFinder.
     */
    @Test
    public void testNormalizeCategory() {
        String subCategoryName = "People_from_Rosario,_Santa_Fe";
        String fromPage = "Rosario,_Santa_Fe";
        String toPage = "Lionel_Messi";
        String expResult = "People_from_#from";
        String result = this.pathFinder.normalizeCategory(subCategoryName, fromPage, toPage);
        assertEquals(expResult, result);
        
        
    }

    /**
     * Test of getRelevantDocuments method, of class PathFinder.
     
    @Test
    public void testGetRelevantDocuments() throws Exception {
        String pathQuery = "";
        int expResult = 0;
        int result = this.pathFinder.getRelevantDocuments(pathQuery);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }*/

    /**
     * Test of isReachablePath method, of class PathFinder.
     
    @Test
    public void testIsReachablePath() throws Exception {
        String pathQuery = "";
        String from = "";
        String to = "";
        boolean expResult = false;
        boolean result = this.pathFinder.isReachablePath(pathQuery, from, to);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    */
    
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
    
    
    @Test
    public void testBlackListCategory(){
    	assertTrue(PathFinder.BLACKLIST_CATEGORY.contains("All_articles_to_be_expanded"));
    }
    
    @Test
    public void testIsBlackCategory(){
    	String blackCategory = "Articles_containing_";
    	String blackCategory2 = "All_articles_to_be_expanded";
    	
    	assertTrue(this.pathFinder.isBlackCategory(blackCategory));
    	assertTrue(this.pathFinder.isBlackCategory(blackCategory2));
    	
    	assertFalse(this.pathFinder.isBlackCategory("Paris"));
    }
    
    
    
 
    
}