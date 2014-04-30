package pia;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import db.WikipediaConnector;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URISyntaxException;
import java.util.Properties;
import org.junit.AfterClass;
import strategies.LastCategoryGeneralization;
import utils.FromToPair;
import utils.PathsResolver;
import utils.ProjectConfiguration;

public class BipartiteGraphGeneratorStarPathTest {

	private BipartiteGraphGenerator pathIndex;
	private List<String> expectedfromPeoplefromfrom;
    private static String originalValue;
	
	@BeforeClass
	public static void setupclass() throws IOException, URISyntaxException{
        Assume.assumeTrue(WikipediaConnector.isTestEnvironment());
        PIAConfigurationBuilder.setGeneralizator(new LastCategoryGeneralization());
	}
    
    @AfterClass
    public static void tearDown() throws IOException, URISyntaxException {
        PIAConfigurationBuilder.unsetGeneralizator();
    }
	
	@Before
	public void setUp() throws Exception {
		WikipediaConnector.restoreTestDatabase();
		WikipediaConnector.restoreResultIndex();
		this.pathIndex= new BipartiteGraphGenerator();
		this.expectedfromPeoplefromfrom = new ArrayList<String>();
        expectedfromPeoplefromfrom.add("#from");
        expectedfromPeoplefromfrom.add("*");
        expectedfromPeoplefromfrom.add("Cat:People_from_#from");
        expectedfromPeoplefromfrom.add("#to");
	}

	@Test
	public void testPathIndex1Hop() throws UnsupportedEncodingException, SQLException, ClassNotFoundException {
		this.pathIndex = new BipartiteGraphGenerator(1);
		this.pathIndex.generateBiGraph("Rosario,_Santa_Fe", "Lionel_Messi");
		PathIndex pathIndex = this.pathIndex.getPathIndex();
		List<String> path_queries = pathIndex.getPathQueries("Rosario,_Santa_Fe", "Lionel_Messi");
		assertEquals(1, path_queries.size());
		assertEquals("#from / #to", path_queries.get(0));
	}
	
	@Test
	public void testPathIndex2Hops() throws UnsupportedEncodingException, SQLException, ClassNotFoundException {
		this.pathIndex = new BipartiteGraphGenerator(2);
		this.pathIndex.generateBiGraph("Rosario,_Santa_Fe", "Lionel_Messi");
		PathIndex pathIndex = this.pathIndex.getPathIndex();
		List<String> path_queries = pathIndex.getPathQueries("Rosario,_Santa_Fe", "Lionel_Messi");
		assertEquals(2, path_queries.size());
		assertEquals("#from / #to", path_queries.get(0));
		assertEquals("#from / * / List_of_VIP / #to", path_queries.get(1));
	}
	
	@Test
	public void testPathIndex3Hops() throws UnsupportedEncodingException, SQLException, ClassNotFoundException {
		this.pathIndex = new BipartiteGraphGenerator(3);
		this.pathIndex.generateBiGraph("Rosario,_Santa_Fe", "Lionel_Messi");
		PathIndex pathIndex = this.pathIndex.getPathIndex();
		List<String> path_queries = pathIndex.getPathQueries("Rosario,_Santa_Fe", "Lionel_Messi");
		assertEquals(3, path_queries.size());
        assertEquals("#from / #to", path_queries.get(0));
		assertEquals("#from / * / Cat:People_from_#from / #to", path_queries.get(1));
		assertEquals("#from / * / List_of_VIP / #to", path_queries.get(2));	
	}
	
	
	
	@Test
	public void testGetNormalizedPathIdIntoDB() throws FileNotFoundException, ClassNotFoundException, SQLException, IOException {
		int returned = this.pathIndex.getNormalizedPathIdIntoDB(this.expectedfromPeoplefromfrom);
		assertNotSame("First time have to be different than 0 but returned 0", 0, this.pathIndex.getNormalizedPathIdIntoDB(this.expectedfromPeoplefromfrom));
		int seccond = this.pathIndex.getNormalizedPathIdIntoDB(this.expectedfromPeoplefromfrom);
		assertSame("seccond time have to be the same id as the first time", seccond,this.pathIndex.getNormalizedPathIdIntoDB(this.expectedfromPeoplefromfrom));
		assertSame("Is not always the same id", seccond,returned);
		}
	
	

}
