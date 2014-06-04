package knn.clean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import db.WikipediaConnector;
import db.utils.ResultsDbInterface;
import org.junit.Assert;
import pia.PIAConfigurationBuilder;
import utils.PathsResolver;

public class BlueFinderPathsFinderTest {

	private BlueFinderPathsFinder bfPathsFinder;
    private ResultsDbInterface resultsDb;
	
	@BeforeClass
	public static void setupclass() {
		Assume.assumeTrue(WikipediaConnector.isTestEnvironment());
		if (WikipediaConnector.isTestEnvironment()) {
            PIAConfigurationBuilder.setGeneralizator("starred");
			try {
                WikipediaConnector.restoreResultIndex();
				WikipediaConnector.executeSqlFromFile("dump_U_pageEnhanced.sql");
				WikipediaConnector.executeSqlFromFile("test_BlueFinderRecommender.sql");
				WikipediaConnector.executeSqlFromFile("test_BlueFinderEvaluationAndRecommender.sql");
				WikipediaConnector.executeSqlFromFile("test_dbtypes.sql");
			} catch (Exception ex) {
				ex.printStackTrace();
				fail("Error while loading required dumps. Cannot execute tests correctly.");
			}
		}
	}

	@Before
	public void setUp() throws Exception {
		KNN knn = new KNN(false);
		this.bfPathsFinder = new BlueFinderPathsFinder(knn);
        this.resultsDb = new ResultsDbInterface();
	}

	@Test
	public void testGetEvaluation() {
		String subject = "France";
		String object = "William_Kissam_Vanderbilt";
        Integer id = -1; // this ID is supposed to be the tuple's
		this.bfPathsFinder.setK(6); //TODO: was this fixed???? check!
        this.bfPathsFinder.setK(5);
		this.bfPathsFinder.setMaxRecomm(3);
		try {
			List<String> actualStrResult = this.bfPathsFinder.getEvaluation(object, subject, id);
            List<Map<String, Integer>> expected = new ArrayList<Map<String, Integer>>();
			Map<String, Integer> map = new LinkedHashMap<String, Integer>();
			
			map.put("#from / * / Cat:French_businesspeople / #to", 1015);
			map.put("#from / * / Cat:French_racehorse_owners_and_breeders / #to", 1001);
            expected.add(map);
			
			map.clear();
			map.put("#from / * / Cat:French_racehorse_owners_and_breeders / #to", 1002);
			map.put("#from / * / Cat:French_businesspeople / #to", 15);
            expected.add(map);
			
			map.clear();
			map.put("#from / * / Cat:French_businesspeople / #to", 15);
			map.put("#from / * / Cat:French_racehorse_owners_and_breeders / #to", 2);
			map.put("#from / * / Cat:Drug-related_deaths_in_#from / #to", 1);
            expected.add(map);
			
			map.clear();
			map.put("#from / * / Cat:French_businesspeople / #to", 15);
			map.put("#from / * / Cat:French_racehorse_owners_and_breeders / #to", 2);
			map.put("#from / * / Cat:Kingdom_of_#from_stubs / #to", 2);
            expected.add(map);
			
			map.clear();
			map.put("#from / * / Cat:French_businesspeople / #to", 15);
			map.put("#from / * / Cat:Ephrussi_family / #to", 7);
			map.put("#from / * / Cat:French_racehorse_owners_and_breeders / #to", 3);
            expected.add(map);

            PathsResolver pathsResolver = new PathsResolver();
            List<Map<String, Integer>> actual = new ArrayList<Map<String, Integer>>();
            for (String str : actualStrResult) {
                actual.add(pathsResolver.decouple(str));
            }
            
            Assert.assertFalse("No puede no dar recomendaciones", actual.isEmpty());
			assertEquals("No tienen la misma cantidad de recomendaciones.", expected.size(), actual.size());
            
            for (Map<String, Integer> inner : expected) {
                Assert.assertTrue("No tienen los mismos elementos", actual.contains(inner));
            }
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			fail("ClassNotFoundException");
		} catch (SQLException e) {
			e.printStackTrace();
			fail("SQLException");
		}
	}
	
	@Test
	public void testGetEvaluation2() {
		String subject = "New_York_City";
		String object = "William_Kissam_Vanderbilt_II";
        Integer id = -1;
		this.bfPathsFinder.setK(10); //TODO: was this fixed???? check!
        this.bfPathsFinder.setK(9);
		this.bfPathsFinder.setMaxRecomm(10000);
		try {
			List<String> actualStrResult = this.bfPathsFinder.getEvaluation(object, subject, id);
            List<Map<String, Integer>> expected = new ArrayList<Map<String, Integer>>();
			Map<String, Integer> map = new LinkedHashMap<String, Integer>();
			
			// 1k
			map.put("#from / * / Cat:#from_Ballet / #to", 1001);
			expected.add(map);
			
			map.clear(); // 2k
			map.put("#from / * / Cat:People_from_#from / #to", 4);
			map.put("#from / * / Cat:#from_Ballet / #to", 1);
			expected.add(map);
			
			map.clear(); // 3k
			map.put("#from / * / Cat:People_from_#from / #to", 4);
			map.put("#from / * / Cat:People_from_Brooklyn / #to", 2);
			map.put("#from / * / Cat:#from_Ballet / #to", 1);
			expected.add(map);
			
			map.clear(); // 4k
			map.put("#from / * / Cat:People_from_#from / #to", 4);
			map.put("#from / * / Cat:People_from_Brooklyn / #to", 2);
			map.put("#from / * / Cat:#from_Ballet / #to", 1);
			map.put("#from / * / Cat:Good_articles / #to", 1);
			expected.add(map);
			
			map.clear(); // 5k
			map.put("#from / * / Cat:People_from_#from / #to", 4);
			map.put("#from / * / Cat:People_from_Brooklyn / #to", 2);
			map.put("#from / * / Cat:Shubert_Organization / #to", 1);
			map.put("#from / * / Cat:#from_Ballet / #to", 1);
			map.put("#from / * / Cat:Good_articles / #to", 1);
			expected.add(map);
			
			map.clear(); // 6k
			map.put("#from / * / Cat:History_of_#from / #to", 4);
			map.put("#from / * / Cat:People_from_#from / #to", 4);
			map.put("#from / * / Cat:People_from_Brooklyn / #to", 2);
			map.put("#from / * / Cat:Shubert_Organization / #to", 1);
			map.put("#from / * / Cat:#from_Ballet / #to", 1);
			map.put("#from / * / Cat:Good_articles / #to", 1);
			expected.add(map);
			
			map.clear(); // 7k
			map.put("#from / * / Cat:History_of_#from / #to", 4);
			map.put("#from / * / Cat:People_from_#from / #to", 4);
			map.put("#from / * / Cat:Architects_from_#from / #to", 3);
			map.put("#from / * / Cat:People_from_Brooklyn / #to", 2);
			map.put("#from / * / Cat:Shubert_Organization / #to", 1);
			map.put("#from / * / Cat:#from_Ballet / #to", 1);
			map.put("#from / * / Cat:Artists_from_Philadelphia,_Pennsylvania / #to", 1);
			map.put("#from / * / Cat:Good_articles / #to", 1);
			expected.add(map);
			
			map.clear(); // 8k
			map.put("#from / * / Cat:History_of_#from / #to", 4);
			map.put("#from / * / Cat:People_from_#from / #to", 4);
			map.put("#from / * / Cat:Architects_from_#from / #to", 3);
			map.put("#from / * / Cat:People_from_Brooklyn / #to", 2);
			map.put("#from / * / Cat:Shubert_Organization / #to", 1);
			map.put("#from / * / Cat:#from_Ballet / #to", 1);
			map.put("#from / * / Cat:Artists_from_Philadelphia,_Pennsylvania / #to", 1);
			map.put("#from / * / Cat:Good_articles / #to", 1);
			map.put("#from / * / Cat:American_clowns / #to", 1);
			expected.add(map);
			
			map.clear(); // 9k
			map.put("#from / * / Cat:History_of_#from / #to", 4);
			map.put("#from / * / Cat:People_from_#from / #to", 4);
			map.put("#from / * / Cat:Architects_from_#from / #to", 3);
			map.put("#from / * / Cat:People_from_Brooklyn / #to", 2);
			map.put("#from / * / Cat:Shubert_Organization / #to", 1);
			map.put("#from / * / Cat:#from_Ballet / #to", 1);
			map.put("#from / * / Cat:The_King's_College_(New_York) / #to", 1);
			map.put("#from / * / Cat:Artists_from_Philadelphia,_Pennsylvania / #to", 1);
			map.put("#from / * / Cat:Good_articles / #to", 1);
			map.put("#from / * / Cat:American_clowns / #to", 1);
			expected.add(map);

            PathsResolver pathsResolver = new PathsResolver();
            List<Map<String, Integer>> actual = new ArrayList<Map<String, Integer>>();
            for (String str : actualStrResult) {
                actual.add(pathsResolver.decouple(str));
            }
            
            Assert.assertFalse("No puede no dar recomendaciones", actual.isEmpty());
			assertEquals("No tienen la misma cantidad de recomendaciones.", expected.size(), actual.size());
            
            for (Map<String, Integer> inner : expected) {
                Assert.assertTrue("No tienen los mismos elementos", actual.contains(inner));
            }
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			fail("ClassNotFoundException");
		} catch (SQLException e) {
			e.printStackTrace();
			fail("SQLException");
		}
	}

	@Test
	public void testGetEvaluation3() {
		String subject = "Washington,_D.C.";
		String object = "Stephen_Johnson_Field";
		Integer id = -1;
        this.bfPathsFinder.setK(5);
		this.bfPathsFinder.setMaxRecomm(10000);
		List<String> expectedResult = new ArrayList<String>();
		Connection conn;
		try {
			conn = WikipediaConnector.getTestConnection();
			PreparedStatement stmt = conn.prepareStatement("SELECT * FROM sc3333 WHERE resource = ?");
			stmt.setString(1, subject + " , " + object + " 21156");
			ResultSet expectedDbResults = stmt.executeQuery();
			if (expectedDbResults.next()) {
				expectedResult.add(expectedDbResults.getString("1path"));
				expectedResult.add(expectedDbResults.getString("2path"));
				expectedResult.add(expectedDbResults.getString("3path"));
				expectedResult.add(expectedDbResults.getString("4path"));
				expectedResult.add(expectedDbResults.getString("5path"));
//				expectedResult.add(expectedDbResults.getString("6path"));
//				expectedResult.add(expectedDbResults.getString("7path"));
//				expectedResult.add(expectedDbResults.getString("8path"));
//				expectedResult.add(expectedDbResults.getString("9path"));
//				expectedResult.add(expectedDbResults.getString("10path"));
			}
			List<String> actualResult = this.bfPathsFinder.getEvaluation(object, subject, id);
			
            Assert.assertFalse("No puede no dar recomendaciones", expectedResult.isEmpty());
            assertEquals("No tienen la misma cantidad de recomendaciones.", expectedResult.size(), actualResult.size());
			assertEquals("Puede que las evaluaciones sean iguales, pero en diferente orden si la cantidad de apariciones son iguales", 
					expectedResult, actualResult);
		} catch (Exception e) {
			e.printStackTrace();
			fail("Check stack trace.");
		}		
	}
	 
}
