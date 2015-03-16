package pia;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import strategies.LastCategoryGeneralization;
import utils.ProjectSetup;
import db.DBConnector;
import db.TestSetup;

public class BipartiteGraphGeneratorStarPathTest {

	private BipartiteGraphGenerator pathIndex;
	private List<String> expectedfromPeoplefromfrom;
	private DBConnector connector;
	private ProjectSetup projectSetup;
	
	@BeforeClass
	public static void setupclass() throws Exception {
//        PIAConfigurationBuilder.setGeneralizator("starred");
	}
    
    @AfterClass
    public static void tearDown() throws Exception {
//        PIAConfigurationBuilder.unsetGeneralizator();
    }
	
	@Before
	public void setUp() throws Exception {
		this.connector = TestSetup.getDBConnector();
		this.projectSetup = TestSetup.getProjectSetup();
		this.connector.restoreTestDatabase();
		this.connector.restoreResultIndex();
		this.pathIndex= new BipartiteGraphGenerator(this.projectSetup, this.connector, new LastCategoryGeneralization());
		this.expectedfromPeoplefromfrom = new ArrayList<String>();
        expectedfromPeoplefromfrom.add("#from");
        expectedfromPeoplefromfrom.add("*");
        expectedfromPeoplefromfrom.add("Cat:People_from_#from");
        expectedfromPeoplefromfrom.add("#to");
	}

	@Test
	public void testPathIndex1Hop() throws Exception {
		this.pathIndex = new BipartiteGraphGenerator(this.projectSetup,this.connector,new LastCategoryGeneralization(),1);
		this.pathIndex.generateBiGraph("Rosario,_Santa_Fe", "Lionel_Messi");
		PathIndex pathIndex = this.pathIndex.getPathIndex();
		List<String> path_queries = pathIndex.getPathQueries("Rosario,_Santa_Fe", "Lionel_Messi");
		assertEquals(1, path_queries.size());
		assertEquals("#from / #to", path_queries.get(0));
	}
	
	@Test
	public void testPathIndex2Hops() throws Exception {
		this.pathIndex = new BipartiteGraphGenerator(this.projectSetup,this.connector,new LastCategoryGeneralization(),2);
		this.pathIndex.generateBiGraph("Rosario,_Santa_Fe", "Lionel_Messi");
		PathIndex pathIndex = this.pathIndex.getPathIndex();
		List<String> path_queries = pathIndex.getPathQueries("Rosario,_Santa_Fe", "Lionel_Messi");
		assertEquals(2, path_queries.size());
		assertEquals("#from / #to", path_queries.get(0));
		assertEquals("#from / * / List_of_VIP / #to", path_queries.get(1));
	}
	
	@Test
	public void testPathIndex3Hops() throws Exception {
		this.pathIndex = new BipartiteGraphGenerator(this.projectSetup, this.connector,new LastCategoryGeneralization(),3);
		this.pathIndex.generateBiGraph("Rosario,_Santa_Fe", "Lionel_Messi");
		PathIndex pathIndex = this.pathIndex.getPathIndex();
		List<String> path_queries = pathIndex.getPathQueries("Rosario,_Santa_Fe", "Lionel_Messi");
		assertEquals(3, path_queries.size());
        assertEquals("#from / #to", path_queries.get(0));
		assertEquals("#from / * / Cat:People_from_#from / #to", path_queries.get(1));
		assertEquals("#from / * / List_of_VIP / #to", path_queries.get(2));	
	}
	
	
	
	@Test
	public void testGetNormalizedPathIdIntoDB() throws Exception {
		int returned = this.pathIndex.getNormalizedPathIdIntoDB(this.expectedfromPeoplefromfrom);
		assertNotSame("First time have to be different than 0 but returned 0", 0, this.pathIndex.getNormalizedPathIdIntoDB(this.expectedfromPeoplefromfrom));
		int seccond = this.pathIndex.getNormalizedPathIdIntoDB(this.expectedfromPeoplefromfrom);
		assertSame("seccond time have to be the same id as the first time", seccond,this.pathIndex.getNormalizedPathIdIntoDB(this.expectedfromPeoplefromfrom));
		assertSame("Is not always the same id", seccond,returned);
		}
	
	

}
