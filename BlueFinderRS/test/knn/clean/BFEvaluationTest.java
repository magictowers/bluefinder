package knn.clean;

import static org.junit.Assert.*;

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

public class BFEvaluationTest {

	private BFEvaluation bfEvaluation;
	
	@BeforeClass
	public static void setupclass(){
		Assume.assumeTrue(WikipediaConnector.isTestEnvironment());
	}

	@Before
	public void setUp() throws Exception {
		KNN knn = new KNN(false);
		this.bfEvaluation = new BFEvaluation(knn);
	}

	@Test
	public void testGetEvaluation() {
		String subject = "France";
		String object = "William_Kissam_Vanderbilt";
		this.bfEvaluation.setK(6);
		this.bfEvaluation.setMaxRecomm(3);
		try {
			List<String> actualResult = this.bfEvaluation.getEvaluation(object, subject);
			List<String> expectedResult = new ArrayList<String>();
			Map<String, Integer> map = new LinkedHashMap<String, Integer>();
			
			map.put("#from / * / Cat:French_businesspeople / #to", 1015);
			map.put("#from / * / Cat:French_racehorse_owners_and_breeders / #to", 1001);
			expectedResult.add(map.toString());
			
			map.clear();
			map.put("#from / * / Cat:French_racehorse_owners_and_breeders / #to", 1002);
			map.put("#from / * / Cat:French_businesspeople / #to", 15);
			expectedResult.add(map.toString());
			
			map.clear();
			map.put("#from / * / Cat:French_businesspeople / #to", 15);
			map.put("#from / * / Cat:French_racehorse_owners_and_breeders / #to", 2);
			map.put("#from / * / Cat:Drug-related_deaths_in_#from / #to", 1);
			expectedResult.add(map.toString());
			
			map.clear();
			map.put("#from / * / Cat:French_businesspeople / #to", 15);
			map.put("#from / * / Cat:French_racehorse_owners_and_breeders / #to", 2);
			map.put("#from / * / Cat:Kingdom_of_#from_stubs / #to", 2);
			expectedResult.add(map.toString());
			
			map.clear();
			map.put("#from / * / Cat:French_businesspeople / #to", 15);
			map.put("#from / * / Cat:Ephrussi_family / #to", 7);
			map.put("#from / * / Cat:French_racehorse_owners_and_breeders / #to", 3);
			expectedResult.add(map.toString());
			
			assertEquals("Puede que las evaluaciones sean iguales, pero en diferente orden si la cantidad de apariciones son iguales", 
					expectedResult, actualResult);
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
		this.bfEvaluation.setK(10);
		this.bfEvaluation.setMaxRecomm(10000);
		try {
			List<String> actualResult = this.bfEvaluation.getEvaluation(object, subject);
			List<String> expectedResult = new ArrayList<String>();
			Map<String, Integer> map = new LinkedHashMap<String, Integer>();
			
			// 1k
			map.put("#from / * / Cat:#from_Ballet / #to", 1001);
			expectedResult.add(map.toString());
			
			map.clear(); // 2k
			map.put("#from / * / Cat:People_from_#from / #to", 4);
			map.put("#from / * / Cat:#from_Ballet / #to", 1);
			expectedResult.add(map.toString());
			
			map.clear(); // 3k
			map.put("#from / * / Cat:People_from_#from / #to", 4);
			map.put("#from / * / Cat:People_from_Brooklyn / #to", 2);
			map.put("#from / * / Cat:#from_Ballet / #to", 1);
			expectedResult.add(map.toString());
			
			map.clear(); // 4k
			map.put("#from / * / Cat:People_from_#from / #to", 4);
			map.put("#from / * / Cat:People_from_Brooklyn / #to", 2);
			map.put("#from / * / Cat:#from_Ballet / #to", 1);
			map.put("#from / * / Cat:Good_articles / #to", 1);
			expectedResult.add(map.toString());
			
			map.clear(); // 5k
			map.put("#from / * / Cat:People_from_#from / #to", 4);
			map.put("#from / * / Cat:People_from_Brooklyn / #to", 2);
			map.put("#from / * / Cat:Shubert_Organization / #to", 1);
			map.put("#from / * / Cat:#from_Ballet / #to", 1);
			map.put("#from / * / Cat:Good_articles / #to", 1);
			expectedResult.add(map.toString());
			
			map.clear(); // 6k
			map.put("#from / * / Cat:History_of_#from / #to", 4);
			map.put("#from / * / Cat:People_from_#from / #to", 4);
			map.put("#from / * / Cat:People_from_Brooklyn / #to", 2);
			map.put("#from / * / Cat:Shubert_Organization / #to", 1);
			map.put("#from / * / Cat:#from_Ballet / #to", 1);
			map.put("#from / * / Cat:Good_articles / #to", 1);
			expectedResult.add(map.toString());
			
			map.clear(); // 7k
			map.put("#from / * / Cat:History_of_#from / #to", 4);
			map.put("#from / * / Cat:People_from_#from / #to", 4);
			map.put("#from / * / Cat:Architects_from_#from / #to", 3);
			map.put("#from / * / Cat:People_from_Brooklyn / #to", 2);
			map.put("#from / * / Cat:Shubert_Organization / #to", 1);
			map.put("#from / * / Cat:#from_Ballet / #to", 1);
			map.put("#from / * / Cat:Artists_from_Philadelphia,_Pennsylvania / #to", 1);
			map.put("#from / * / Cat:Good_articles / #to", 1);
			expectedResult.add(map.toString());
			
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
			expectedResult.add(map.toString());
			
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
			expectedResult.add(map.toString());
			
			assertEquals("Puede que las evaluaciones sean iguales, pero en diferente orden si la cantidad de apariciones son iguales", 
					expectedResult, actualResult);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			fail("ClassNotFoundException");
		} catch (SQLException e) {
			e.printStackTrace();
			fail("SQLException");
		}
	}

}
