package knn.clean;

import static org.junit.Assert.assertEquals;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import knn.distance.SemanticPair;

import org.junit.Before;
import org.junit.Test;

import db.WikipediaConnector;

public class KNNTestCase {

	private KNN knn;
	Set<String> rosarioTypes;
	Set<String> diegoTypes;
	
	@Before
	public void setUp() throws Exception {
		WikipediaConnector.restoreResultIndex();
		WikipediaConnector.restoreTestDatabase();
		this.knn= new KNN();
		
		String[] rt = {"<http://dbpedia.org/class/yago/YagoGeoEntity>","<http://dbpedia.org/class/yago/PopulatedPlacesInSantaFeProvince>"};
		rosarioTypes = new HashSet<String>(Arrays.asList(rt));
		
		String[] dt = {"<http://dbpedia.org/class/yago/ArgentinePopSingers>","<http://dbpedia.org/class/yago/PeopleFromBuEnosAires>",
				"<http://dbpedia.org/class/yago/Actor109765278>", "<http://dbpedia.org/class/yago/LivingPeople>",
				"<http://dbpedia.org/class/yago/ArgentinePeopleOfItalianDescent>"};
		diegoTypes = new HashSet<String>(Arrays.asList(dt));
				
				
		}
		

	@Test
	public void testGenerateSemanticPair() throws SQLException, ClassNotFoundException {
		SemanticPair pair = this.knn.generateSemanticPair("Rosario,_Santa_Fe , Diego_Torres", 1);
		assertEquals("Rosario,_Santa_Fe", pair.getSubject());
		assertEquals("Diego_Torres", pair.getObject());
		assertEquals(this.rosarioTypes,new HashSet<String>(pair.getSubjectElementsBySemProperty("type")));
		assertEquals(this.diegoTypes, new HashSet<String>(pair.getObjectElementsBySemProperty("type")));
		
		
	}

}
