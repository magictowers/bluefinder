package knn.clean;

import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import knn.Instance;
import knn.distance.SemanticPair;

import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import db.WikipediaConnector;

public class KNNTestCase {

	private KNN knn;
	Set<String> rosarioTypes;
	Set<String> diegoTypes;
	String dtTypes;
	String sfTypes;
	
	@BeforeClass
	public static void setupclass(){
		   Assume.assumeTrue(WikipediaConnector.isTestEnvironment());
	}
	
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
		
		this.dtTypes="<http://dbpedia.org/class/yago/ArgentinePopSingers> <http://dbpedia.org/class/yago/PeopleFromBuEnosAires> " +
				"<http://dbpedia.org/class/yago/Actor109765278> <http://dbpedia.org/class/yago/LivingPeople> " +
				"<http://dbpedia.org/class/yago/ArgentinePeopleOfItalianDescent>";
		
		this.sfTypes="<http://dbpedia.org/class/yago/YagoGeoEntity> <http://dbpedia.org/class/yago/PopulatedPlacesInSantaFeProvince>";
	
	}
		

	@Test
	public void testGenerateSemanticPair() throws SQLException, ClassNotFoundException {
		SemanticPair pair = this.knn.generateSemanticPair("Rosario,_Santa_Fe , Diego_Torres", 1);
		assertEquals("Rosario,_Santa_Fe", pair.getSubject());
		assertEquals("Diego_Torres", pair.getObject());
		assertEquals(this.rosarioTypes,new HashSet<String>(pair.getSubjectElementsBySemProperty("type")));
		assertEquals(this.diegoTypes, new HashSet<String>(pair.getObjectElementsBySemProperty("type")));
		
	}
	
	@Test
	public void testEnhanceU_Page() throws FileNotFoundException, ClassNotFoundException, SQLException, IOException{
		WikipediaConnector.restoreResultIndex();
		
		Statement st = WikipediaConnector.getResultsConnection().createStatement();
		st.executeUpdate("INSERT INTO `U_page`(`page`) VALUES (\"Rosario,_Santa_Fe , Diego_Torres\")");
		st.close();
		String tableName = "U_pageEnhanced";
		this.knn.enhanceUPage();
		
		String query = "show tables like \""+tableName+"\"";
		st = WikipediaConnector.getResultsConnection().createStatement();
		ResultSet rs = st.executeQuery(query);
		rs.last();
		
		assertEquals(1,rs.getRow());
		
		PreparedStatement pst = WikipediaConnector.getResultsConnection().prepareStatement("select * from U_pageEnhanced where page=?");
		pst.setString(1, "Rosario,_Santa_Fe , Diego_Torres");
		rs = pst.executeQuery();
		
		rs.next();
		assertEquals(this.dtTypes, rs.getString("objectTypes"));
		assertEquals(this.sfTypes, rs.getString("subjectTypes"));
	}

	@Test
	public void testGenerateSemanticPairAllParams(){
		SemanticPair result = this.knn.generateSemanticPair("Rosario,_Santa_Fe , Diego_Torres", 1, this.dtTypes, this.sfTypes);
		
		assertEquals("Diego_Torres", result.getObject());
		assertEquals(1,result.getId());
		assertEquals(this.diegoTypes, new HashSet<String>(result.getSubjectElementsBySemProperty("type")));
		assertEquals(this.rosarioTypes, new HashSet<String>(result.getObjectElementsBySemProperty("type")));
		
		
	}
	
	@Test
	public void testGetKNearestNeighbors() throws FileNotFoundException, ClassNotFoundException, SQLException, IOException{
		WikipediaConnector.restoreResultIndex();
		
		String[] pairs = {"Rosario,_Santa_Fe , Diego_Torres", "Rosario,_Santa_Fe , Lionel_Messi", 
				"Ada,_Ohio , Rollo_May", "Peekskill,_New_York , Mel_Gibson",
				"Buenos_Aires , Charly_Garcia"};
		
		// Cargar valores de prueba
		Statement st = WikipediaConnector.getResultsConnection().createStatement();
		for (int i = 0; i <= 4; i++){
			st.executeUpdate("INSERT INTO `U_page`(`page`) VALUES ('" + pairs[i] + "')");
		}
		
		st.executeUpdate("INSERT INTO `dbtypes`(`resource`,`type`) VALUES ('Rosario,_Santa_Fe','<http://dbpedia.org/class/yago/YagoGeoEntity>')");		
		st.executeUpdate("INSERT INTO `dbtypes`(`resource`,`type`) VALUES ('Rosario,_Santa_Fe','<http://dbpedia.org/class/yago/PopulatedPlacesInSantaFeProvince>')");

		st.executeUpdate("INSERT INTO `dbtypes`(`resource`,`type`) VALUES ('Lionel_Messi','<http://dbpedia.org/class/yago/ArgentineFootballers>')");
		st.executeUpdate("INSERT INTO `dbtypes`(`resource`,`type`) VALUES ('Lionel_Messi','<http://dbpedia.org/class/yago/ArgentinePeopleOfItalianDescent>')");
		st.executeUpdate("INSERT INTO `dbtypes`(`resource`,`type`) VALUES ('Lionel_Messi','<http://dbpedia.org/class/yago/PeopleFromRosario>')");
		st.executeUpdate("INSERT INTO `dbtypes`(`resource`,`type`) VALUES ('Lionel_Messi','<http://dbpedia.org/class/yago/LivingPeople>')");
		st.executeUpdate("INSERT INTO `dbtypes`(`resource`,`type`) VALUES ('Lionel_Messi','<http://dbpedia.org/class/yago/NaturalisedCitizensOfSpain>')");

		st.executeUpdate("INSERT INTO `dbtypes`(`resource`,`type`) VALUES ('Charly_Garcia','<http://dbpedia.org/class/yago/PeopleWithBipolarDisorder>')");
		st.executeUpdate("INSERT INTO `dbtypes`(`resource`,`type`) VALUES ('Charly_Garcia','<http://dbpedia.org/class/yago/LivingPeople>')");
		st.executeUpdate("INSERT INTO `dbtypes`(`resource`,`type`) VALUES ('Charly_Garcia','<http://dbpedia.org/class/yago/ArgentineMusicians>')");
		st.executeUpdate("INSERT INTO `dbtypes`(`resource`,`type`) VALUES ('Charly_Garcia','<http://dbpedia.org/class/yago/ArgentineSongwriters>')");
		st.executeUpdate("INSERT INTO `dbtypes`(`resource`,`type`) VALUES ('Charly_Garcia','<http://dbpedia.org/class/yago/PeopleFromBuEnosAires>')");

		st.executeUpdate("INSERT INTO `dbtypes`(`resource`,`type`) VALUES ('Mel_Gibson','<http://dbpedia.org/class/yago/PeopleWithBipolarDisorder>')");
		st.executeUpdate("INSERT INTO `dbtypes`(`resource`,`type`) VALUES ('Mel_Gibson','<http://dbpedia.org/class/yago/LivingPeople>')");
		st.executeUpdate("INSERT INTO `dbtypes`(`resource`,`type`) VALUES ('Mel_Gibson','<http://dbpedia.org/class/yago/FilmMaker110088390>')");
		st.executeUpdate("INSERT INTO `dbtypes`(`resource`,`type`) VALUES ('Mel_Gibson','<http://dbpedia.org/class/yago/Actor109765278>')");
		st.executeUpdate("INSERT INTO `dbtypes`(`resource`,`type`) VALUES ('Mel_Gibson','<http://dbpedia.org/class/yago/ActorsFromNewYork>')");

		st.executeUpdate("INSERT INTO `dbtypes`(`resource`,`type`) VALUES ('Rollo_May','<http://dbpedia.org/class/yago/PeopleFromHardinCounty,Ohio>')");
		st.executeUpdate("INSERT INTO `dbtypes`(`resource`,`type`) VALUES ('Rollo_May','<http://dbpedia.org/class/yago/AmericanPsychologists>')");
		st.executeUpdate("INSERT INTO `dbtypes`(`resource`,`type`) VALUES ('Rollo_May','<http://dbpedia.org/class/yago/OberlinCollegeAlumni>')");


		st.executeUpdate("INSERT INTO `dbtypes`(`resource`,`type`) VALUES ('Ada,_Ohio','<http://dbpedia.org/class/yago/CitiesInOhio>')");
		st.executeUpdate("INSERT INTO `dbtypes`(`resource`,`type`) VALUES ('Ada,_Ohio','<http://dbpedia.org/class/yago/YagoGeoEntity>')");

		st.executeUpdate("INSERT INTO `dbtypes`(`resource`,`type`) VALUES ('Buenos_Aires','<http://dbpedia.org/ontology/City>')");

		st.executeUpdate("INSERT INTO `dbtypes`(`resource`,`type`) VALUES ('Peekskill,_New_York','<http://dbpedia.org/class/yago/PopulatedPlacesOnTheHuDsonRiver>')");
		st.executeUpdate("INSERT INTO `dbtypes`(`resource`,`type`) VALUES ('Peekskill,_New_York','<http://dbpedia.org/class/yago/CitiesInNewYork>')");
		st.executeUpdate("INSERT INTO `dbtypes`(`resource`,`type`) VALUES ('Peekskill,_New_York','<http://dbpedia.org/class/yago/YagoGeoEntity>')");
		st.executeUpdate("INSERT INTO `dbtypes`(`resource`,`type`) VALUES ('Peekskill,_New_York','<http://dbpedia.org/class/yago/PopulatedPlacesEstablishedIn1684>')");
		st.executeUpdate("INSERT INTO `dbtypes`(`resource`,`type`) VALUES ('Peekskill,_New_York','<http://dbpedia.org/class/yago/PopulatedPlacesInWestchesterCounty,NewYork>')");
		st.close();
		
		// Obtener nuevo KNN con los valores cargados
		this.knn= new KNN();
		this.knn.enhanceUPage();
				
		// k = 0 , 5 pares
		SemanticPair pair = this.knn.generateSemanticPair("Rosario,_Santa_Fe , Diego_Torres", 0);
		List<Instance> l0 = this.knn.getKNearestNeighbors(0, pair);
//		System.out.println(l0.size() + l0.get(0).getResource());
		assertEquals(l0.size(), 0);
		
		
		// k = 1 , 5 pares
		List<Instance> l1 = this.knn.getKNearestNeighbors(1, pair);
		System.out.println(l1.size() + l1.get(0).getResource());
		assertEquals(l1.size(), 1);		
		Instance instance = l1.get(0);
		assertEquals(instance.getResource(), pairs[0]);
		assertEquals(instance.getDistance(), 0, 1e-15);
		System.out.println("tipos: " + instance.getTypes());
		

		///////////falta lo siguiente
		
		// caso k = 10 , 5 pares
		List<Instance> l2 = this.knn.getKNearestNeighbors(5, pair);
		System.out.println(l1.size() + l2.get(0).getResource());
		assertEquals(l2.size(), 5);
		instance = l2.get(0);
		assertEquals(instance.getResource(), pairs[0]);System.out.println("distancia 1: " + instance.getDistance());
		assertEquals(instance.getDistance(), 0, 1e-15);
		instance = l2.get(1);
		assertEquals(instance.getResource(), pairs[1]);System.out.println("distancia 2: " + instance.getDistance());
		assertEquals(instance.getDistance(), 0, 1e-15);
		instance = l2.get(2);
		assertEquals(instance.getResource(), pairs[2]);System.out.println("distancia 3: " + instance.getDistance());
		assertEquals(instance.getDistance(), 0, 1e-15);
		instance = l2.get(3);
		assertEquals(instance.getResource(), pairs[3]);System.out.println("distancia 4: " + instance.getDistance());
		assertEquals(instance.getDistance(), 0, 1e-15);
		instance = l2.get(4);
		assertEquals(instance.getResource(), pairs[4]);System.out.println("distancia 5: " + instance.getDistance());
		assertEquals(instance.getDistance(), 0, 1e-15);

		// caso k = 5 , 11 pares
		
		// caso k = 10 , 11 pares
		for (Iterator<Instance> iterator = l1.iterator(); iterator.hasNext();) {
			instance = iterator.next();
			assertEquals(instance.getResource(), "Rosario,_Santa_Fe , Diego_Torres");
			assertEquals(instance.getDistance(), 0, 1e-15);
			System.out.println("tipos: " + instance.getTypes());
		}
		
		
	}
	
	

}
