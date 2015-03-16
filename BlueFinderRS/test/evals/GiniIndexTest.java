package evals;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import utils.ProjectSetup;
import db.DBConnector;
import db.TestSetup;

public class GiniIndexTest {
	
	private GiniIndex analyzer;
	private DBConnector connector;
	private ProjectSetup projectSetup;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
				TestSetup.getDBConnector().executeSqlFromFile("test_GiniIndex_evals.sql");
				TestSetup.getDBConnector().executeSqlFromFile("test_GiniIndex_V_Normalized.sql");
	}

	@Before
	public void setUp() throws Exception {
		this.connector = TestSetup.getDBConnector();
        this.projectSetup = TestSetup.getProjectSetup();
        this.projectSetup.setPathStrategy("unstarred");
		this.analyzer = new GiniIndex(this.connector,"test_V_Normalized_starpath", this.projectSetup);
	}
	
	@Test
	public void testSetAnalysisSample() throws Exception {
		System.out.println("testSetAnalysisSample");
		int limit = 10;
		int offset = 42;
		this.analyzer.setPathsSample(limit, offset);
		List<String> result = this.analyzer.getPathsSample();
		List<String> expected = new ArrayList<String>();
		expected.add("#from / * / List_of_smooth_jazz_musicians / #to");
		expected.add("#from / * / Cat:ABS-CBN_Corporation / #to");
		expected.add("#from / * / Cat:Avex_Group / #to");
		expected.add("#from / * / Cat:African-American_rappers / #to");
		expected.add("#from / #to");
		expected.add("#from / * / Cat:Gordy_family / #to");
		expected.add("#from / * / Cat:Epitaph_Records_artists / #to");
		expected.add("#from / * / Cat:BNA_Records_artists / #to");
		expected.add("#from / * / Cat:Musicians_from_Dallas,_Texas / #to");
		expected.add("#from / * / Cat:Arista_Records_artists / #to");
		assertEquals(expected, result);
	}

	@Test
	public void testSetPathsForNeighbour() throws Exception {
		System.out.println("testSetPathsForNeighbour");
		String table = "test_sc1Evaluation";
		int k = 2;
		int limit = 10;
		int offset = 1;
		int maxRecomm = -1;
		List<String> result = this.analyzer.setPathsForNeighbour(table, k, maxRecomm, limit, offset);
		List<String> expected = new ArrayList<String>();
		expected.add("#from / #to");
		expected.add("#from / * / Cat:#from_artists / #to");
		expected.add("#from / * / Cat:Capitol_Records_artists / #to");
		expected.add("#from / * / Cat:Polydor_Records_artists / #to");
		expected.add("#from / #to");
		expected.add("#from / * / List_of_#from_artists / #to");
		expected.add("#from / #to");
		expected.add("#from / #to");
		assertEquals(expected, result);
	}
	
	@Test
	public void testGetPiFor() throws Exception {
		System.out.println("testGetPiFor");
		String table = "test_sc1Evaluation";
		int k = 2;
		int limit = 10;
		int offset = 1;
		int maxRecomm = -1;
		List<String> analysisSample = new ArrayList<String>();
		analysisSample.add("#from / * / List_of_smooth_jazz_musicians / #to");
		analysisSample.add("#from / * / Cat:ABS-CBN_Corporation / #to");
		analysisSample.add("#from / * / Cat:Avex_Group / #to");
		analysisSample.add("#from / * / Cat:African-American_rappers / #to");
		analysisSample.add("#from / #to");
		analysisSample.add("#from / * / Cat:Gordy_family / #to");
		analysisSample.add("#from / * / Cat:Epitaph_Records_artists / #to");
		analysisSample.add("#from / * / Cat:BNA_Records_artists / #to");
		analysisSample.add("#from / * / Cat:Musicians_from_Dallas,_Texas / #to");
		analysisSample.add("#from / * / Cat:Arista_Records_artists / #to");
		this.analyzer.setPathsToAnalize(analysisSample);
		Map<String, Float> result = this.analyzer.getPiFor(table, k, maxRecomm, limit, offset);
		Map<String, Float> expected = new LinkedHashMap<String, Float>();
		expected.put("#from / * / Cat:ABS-CBN_Corporation / #to", 0f);
		expected.put("#from / * / Cat:African-American_rappers / #to", 0f);
		expected.put("#from / * / Cat:Arista_Records_artists / #to", 0f);
		expected.put("#from / * / Cat:Avex_Group / #to", 0f);
		expected.put("#from / * / Cat:BNA_Records_artists / #to", 0f);
		expected.put("#from / * / Cat:Epitaph_Records_artists / #to", 0f);
		expected.put("#from / * / Cat:Gordy_family / #to", 0f);
		expected.put("#from / * / List_of_smooth_jazz_musicians / #to", 0f);
		expected.put("#from / * / Cat:Musicians_from_Dallas,_Texas / #to", 0f);
		expected.put("#from / #to", 0.5f);
		assertEquals(expected, result);
	}

	@Test
	public void testGetGiniIndexFor() throws Exception {
		System.out.println("testGetGiniIndexFor");
		String table = "test_sc1Evaluation";
		int k = 2;
		int limit = 10;
		int offset = 1;
		int maxRecomm = -1;
		List<String> analysisSample = new ArrayList<String>();
		analysisSample.add("#from / * / List_of_smooth_jazz_musicians / #to");
		analysisSample.add("#from / * / Cat:ABS-CBN_Corporation / #to");
		analysisSample.add("#from / * / Cat:Avex_Group / #to");
		analysisSample.add("#from / * / Cat:African-American_rappers / #to");
		analysisSample.add("#from / #to");
		analysisSample.add("#from / * / Cat:Gordy_family / #to");
		analysisSample.add("#from / * / Cat:Epitaph_Records_artists / #to");
		analysisSample.add("#from / * / Cat:BNA_Records_artists / #to");
		analysisSample.add("#from / * / Cat:Musicians_from_Dallas,_Texas / #to");
		analysisSample.add("#from / * / Cat:Arista_Records_artists / #to");
		this.analyzer.setPathsToAnalize(analysisSample);
		float result = this.analyzer.getGiniIndexFor(table, k, maxRecomm, limit, offset);
		float expected = 0.5f;
		assertEquals(expected, result, 0.00001);
	}
}
