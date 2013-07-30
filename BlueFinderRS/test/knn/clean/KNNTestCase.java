package knn.clean;

import static org.junit.Assert.*;

import java.sql.SQLException;
import java.util.ArrayList;
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
		
		String[] rt = {"<http://dbpedia.org/class/yago/YagoGeoEntity>","<http://dbpedia.org/class/yago/PopulatedPlacesInSantaFeProvince>",
				"<http://dbpedia.org/class/yago/PopulatedPlacesEstablishedIn1793>", "<http://dbpedia.org/ontology/City>",
				"<http://schema.org/City>", "<http://dbpedia.org/ontology/Settlement>",
				"<http://dbpedia.org/ontology/PopulatedPlace>", "<http://dbpedia.org/ontology/Place>",
				"<http://schema.org/Place>", "<http://www.w3.org/2002/07/owl#Thing>"};
		rosarioTypes = new HashSet<String>(Arrays.asList(rt));
		
		String[] dt = {"<http://dbpedia.org/class/yago/ArgentinePopSingers>","<http://dbpedia.org/class/yago/PeopleFromBuEnosAires>",
				"<http://dbpedia.org/class/yago/Actor109765278>", "<http://dbpedia.org/class/yago/LivingPeople>",
				"<http://dbpedia.org/class/yago/ArgentinePeopleOfItalianDescent>", "<http://dbpedia.org/class/yago/Person100007846>",
				"<http://dbpedia.org/class/yago/Songwriter110624540>", "<http://dbpedia.org/class/yago/ArgentineMaleSingers>",
				"<http://dbpedia.org/ontology/MusicalArtist>", "<http://dbpedia.org/ontology/Artist>",
				"<http://dbpedia.org/ontology/Person>", "<http://xmlns.com/foaf/0.1/Person>",
				"<http://www.w3.org/2002/07/owl#Thing>", "<http://schema.org/Person>",
				"<http://dbpedia.org/ontology/Agent>", "<http://schema.org/MusicGroup>" };
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
