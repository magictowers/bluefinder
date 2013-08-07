package knn.clean;

import static org.junit.Assert.*;

import java.io.UnsupportedEncodingException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import pia.BipartiteGraphGenerator;
import pia.PathIndex;

import db.WikipediaConnector;

public class StatisticsTest {

	private Statistics statistics;
	private String scenarioName;
	
	@Before
	public void setUp() throws Exception {
		
		WikipediaConnector.restoreResultIndex();
		this.statistics= new Statistics();
		this.scenarioName="resultsTestKNN";
	}


	
	@Test
	public void testSetOfRelevantPaths(){
		String stringOfPathQueries = "#from / Cat:#from / #to , #from / Cat:Warner_Music_labels / #to";
		Set<String> expected = new HashSet<String>(); 
		expected.add("#from / Cat:#from / #to");
		expected.add("#from / Cat:Warner_Music_labels / #to");
		
		Set<String> actual = this.statistics.getSetOfRelevantPathQueries(stringOfPathQueries);
		
		assertEquals(expected, actual);
	
	}
	
	@Test
	public void testNeighborPaths(){
		String stringPathQueries = "{#from / #to=1010, #from / * / Cat:ECM_artists / #to=1}";
		Set<String> expected = new HashSet<String>(); 
		expected.add("#from / #to");
		expected.add("#from / * / Cat:ECM_artists / #to");
		
		Set<String> actual = this.statistics.getRetrievedPaths(stringPathQueries);
		
		assertEquals(expected, actual);
	}
	
	
	@Test
	public void testSimplePresicion() {
		String retrievedPaths = "{#from / #to=1010, #from / * / Cat:ECM_artists / #to=1}";
		String retrievedNothingInCommon = "{#from / Cat:Bad / #to=1010, #from / * / Cat:ECM_artists / #to=1}";
		String relevantPaths = "#from / #to , #from / Cat:Warner_Music_labels / #to";
				
		double result = this.statistics.simplePresicion(retrievedPaths,relevantPaths);
		
		assertEquals("Presicion bad calculated", 0.5, result, 0.00001);
		
		result = this.statistics.simplePresicion(retrievedNothingInCommon,relevantPaths);
		
		assertEquals("Presicion bad calculated with nothing in common", 0.0, result, 0.0);
		
		
	}
	
	public void testSimpleRecall() {
		String retrievedPaths = "{#from / #to=1010, #from / * / Cat:ECM_artists / #to=1}";
		
		String relevantPaths = "#from / #to , #from / Cat:Warner_Music_labels / #to";
		String relevantNothingInCommon ="#from / Cat:X / #to";
				
		double result = this.statistics.simpleRecall(retrievedPaths,relevantPaths);
		
		assertEquals("Recall bad calculated", 0.5, result, 0.00001);
		
		result = this.statistics.simpleRecall(retrievedPaths,relevantNothingInCommon);
		
		assertEquals("Presicion bad calculated with nothing in common", 0.0, result, 0.0);
		
		
	}
	
	
	
	@Test
	public void testPresicionMean() throws SQLException, ClassNotFoundException{
		
		/**
		 * presicion de 10path es (1/2 + 1/5) / 2 = 0.35 
		 */
		WikipediaConnector.createStatisticsTables();
		
		
		
		
		this.statistics.computeStatistics("resultsTestKNN");
		
		String queryString = "select * from generalStatistics as g inner join particularStatistics as p on g.id=p.id and g.scenario=? and p.kValue=?";
		PreparedStatement pst = WikipediaConnector.getResultsConnection().prepareStatement(queryString);
		
		pst.setString(1,this.scenarioName);
		//KValue
		pst.setLong(2,10);
		
		
		ResultSet rs = pst.executeQuery();
		rs.next();
		assertEquals("presicion value is not correct", 0.35, rs.getDouble("presicion"), 0.00005);
		
		
		
	}
	
	@Test
	public void testComputeAllPresicionMeans() throws SQLException, ClassNotFoundException{
		// 0,75	0,75	0,75	0,75	0,66665	0,625	0,625	0,6	0,35	0,35
		Map<Integer,Double> actual = this.statistics.computeAllPresicionMeans(this.scenarioName);
		Map<Integer,Double> expected = new HashMap<Integer, Double>();
		expected.put(1, 0.75);
		expected.put(2, 0.75);
		expected.put(3, 0.75);
		expected.put(4, 0.75);
		expected.put(5, 0.66665);
		expected.put(6, 0.625);
		expected.put(7, 0.625);
		expected.put(8, 0.6);
		expected.put(9, 0.35);
		expected.put(10, 0.35);
		
		
		assertEquals(expected.get(1),actual.get(1));
		assertEquals(expected.get(2),actual.get(2));
		assertEquals(expected.get(3),actual.get(3));
		assertEquals(expected.get(4),actual.get(4));
		assertEquals(expected.get(5),actual.get(5), 0.00005);
		assertEquals(expected.get(6),actual.get(6));
		assertEquals(expected.get(7),actual.get(7));
		assertEquals(expected.get(8),actual.get(8));
		assertEquals(expected.get(9),actual.get(9));
		assertEquals(expected.get(10),actual.get(10));
		
	}
	
	@Test
	public void testComputeAllRecallMeans() throws SQLException, ClassNotFoundException{
		// 0,75	0,75	0,75	0,75	0,66665	0,625	0,625	0,6	0,35	0,35
		Map<Integer,Double> actual = this.statistics.computeAllRecallMeans(this.scenarioName);
		Map<Integer,Double> expected = new HashMap<Integer, Double>();
		expected.put(1, 0.75);
		expected.put(2, 0.75);
		expected.put(3, 0.75);
		expected.put(4, 0.75);
		expected.put(5, 0.75);
		expected.put(6, 0.75);
		expected.put(7, 0.75);
		expected.put(8, 0.75);
		expected.put(9, 0.75);
		expected.put(10, 0.75);
		
		
		assertEquals(expected.get(1),actual.get(1));
		assertEquals(expected.get(2),actual.get(2));
		assertEquals(expected.get(3),actual.get(3));
		assertEquals(expected.get(4),actual.get(4));
		assertEquals(expected.get(5),actual.get(5), 0.00005);
		assertEquals(expected.get(6),actual.get(6));
		assertEquals(expected.get(7),actual.get(7));
		assertEquals(expected.get(8),actual.get(8));
		assertEquals(expected.get(9),actual.get(9));
		assertEquals(expected.get(10),actual.get(10));
		
	}
	
	
	@Test
	public void testComputeAllHitRateMeans() throws SQLException, ClassNotFoundException{
		// 0,75	0,75	0,75	0,75	0,66665	0,625	0,625	0,6	0,35	0,35
		Map<Integer,Double> actual = this.statistics.computeAllHitRateMeans(this.scenarioName);
		Map<Integer,Double> expected = new HashMap<Integer, Double>();
		expected.put(1, 1.0);
		expected.put(2, 1.0);
		expected.put(3, 1.0);
		expected.put(4, 1.0);
		expected.put(5, 1.0);
		expected.put(6, 1.0);
		expected.put(7, 1.0);
		expected.put(8, 1.0);
		expected.put(9, 1.0);
		expected.put(10, 1.0);
		
		
		
		
		assertEquals(expected.get(1),actual.get(1));
		assertEquals(expected.get(2),actual.get(2));
		assertEquals(expected.get(3),actual.get(3));
		assertEquals(expected.get(4),actual.get(4));
		assertEquals(expected.get(5),actual.get(5), 0.00005);
		assertEquals(expected.get(6),actual.get(6));
		assertEquals(expected.get(7),actual.get(7));
		assertEquals(expected.get(8),actual.get(8));
		assertEquals(expected.get(9),actual.get(9));
		assertEquals(expected.get(10),actual.get(10));
		
	}
	
	public void testSimpleHitRate() {
		String retrievedPaths = "{#from / #to=1010, #from / * / Cat:ECM_artists / #to=1}";
		String retrievedPathsAllIncluded = "{#from / #to=1010, #from / * / Cat:ECM_artists / #to=1, #from / Cat:Warner_Music_labels / #to=1}";
		
		String relevantPaths = "#from / #to , #from / Cat:Warner_Music_labels / #to";
		String relevantNothingInCommon ="#from / Cat:X / #to";
				
		double result = this.statistics.simpleHitRate(retrievedPaths,relevantPaths);
		
		assertEquals("Recall bad calculated", 1.0, result, 0.00001);
		
		result = this.statistics.simpleHitRate(retrievedPaths,relevantNothingInCommon);
		
		assertEquals("Presicion bad calculated with nothing in common", 0.0, result, 0.0);
		
		result = this.statistics.simpleHitRate(retrievedPathsAllIncluded,relevantPaths);
		
		assertEquals("Presicion bad calculated with nothing in common", 1.0, result, 0.0);
		
		
		
	}
	

	
	@Test
	public void testF1Doubles(){
		
		assertEquals(0.4,this.statistics.f1(1.0, 0.25), 0.001);
		assertEquals(0.6666, this.statistics.f1(0.5, 1.0), 0.001);
	}
	
	
	@Test
	public void testComputeAllF1Means() throws SQLException, ClassNotFoundException{
		fail("not implemented yet");
		// 0,75	0,75	0,75	0,75	0,66665	0,625	0,625	0,6	0,35	0,35
		Map<Integer,Double> actual = this.statistics.computeAllHitRateMeans(this.scenarioName);
		Map<Integer,Double> expected = new HashMap<Integer, Double>();
		expected.put(1, 1.0);
		expected.put(2, 1.0);
		expected.put(3, 1.0);
		expected.put(4, 1.0);
		expected.put(5, 1.0);
		expected.put(6, 1.0);
		expected.put(7, 1.0);
		expected.put(8, 1.0);
		expected.put(9, 1.0);
		expected.put(10, 1.0);
		
		
		
		
		assertEquals(expected.get(1),actual.get(1));
		assertEquals(expected.get(2),actual.get(2));
		assertEquals(expected.get(3),actual.get(3));
		assertEquals(expected.get(4),actual.get(4));
		assertEquals(expected.get(5),actual.get(5), 0.00005);
		assertEquals(expected.get(6),actual.get(6));
		assertEquals(expected.get(7),actual.get(7));
		assertEquals(expected.get(8),actual.get(8));
		assertEquals(expected.get(9),actual.get(9));
		assertEquals(expected.get(10),actual.get(10));
		
	}
	
	@Test
	public void testGIndex() throws UnsupportedEncodingException, SQLException, ClassNotFoundException{
		BipartiteGraphGenerator pathIndex;
		pathIndex = new BipartiteGraphGenerator(3);
		pathIndex.generateBiGraph("Rosario,_Santa_Fe", "Lionel_Messi");
		pathIndex.generateBiGraph("Rosario,_Santa_Fe", "List_of_VIP");
		double actual = this.statistics.GIndex();
		
		assertEquals(0.5, actual, 0.0001);
		
	}
	
	@Test
	public void itemSupport() throws UnsupportedEncodingException, SQLException, ClassNotFoundException{
		BipartiteGraphGenerator pathIndex;
		pathIndex = new BipartiteGraphGenerator(3);
		pathIndex.generateBiGraph("Rosario,_Santa_Fe", "Lionel_Messi");
		pathIndex.generateBiGraph("Rosario,_Santa_Fe", "List_of_VIP");
		
		double actual = this.statistics.supportForItem("Rosario,_Santa_Fe , Lionel_Messi", "#from / #to");
		assertEquals(2.0, actual, 0.00001);
		
		actual = this.statistics.supportForItem("Rosario,_Santa_Fe , Lionel_Messi", "#from / Cat:#from / Cat:People_from_#from / #to");
		assertEquals(1.0, actual, 0.00001);
		
		
	}
	
	@Test
	public void testUserSupport() throws UnsupportedEncodingException, SQLException, ClassNotFoundException{
		BipartiteGraphGenerator pathIndex;
		pathIndex = new BipartiteGraphGenerator(3);
		pathIndex.generateBiGraph("Rosario,_Santa_Fe", "Lionel_Messi");
		pathIndex.generateBiGraph("Rosario,_Santa_Fe", "List_of_VIP");
		
		double actual = this.statistics.supportForUser("Rosario,_Santa_Fe , Lionel_Messi", "#from / #to");
		assertEquals(3, actual,0.0001);
		actual = this.statistics.supportForUser("Rosario,_Santa_Fe , Lionel_Messi", "#from / List_of_VIP / #to");
		assertEquals(3, actual,0.0001);
		
		actual = this.statistics.supportForUser("Rosario,_Santa_Fe , List_of_VIP", "#from / #to");
		assertEquals(1,actual,0.0001);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}