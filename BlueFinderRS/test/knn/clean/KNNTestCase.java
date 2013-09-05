package knn.clean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.HashSet;
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
				"Buenos_Aires , Charly_Garcia","Rosario,_Santa_Fe , Che_Guevara",
				"Zundert , Vincent_van_Gogh","Buenos_Aires , Fidel_Nadal_(reggae_musician)",
				"Amsterdam , Edwin_Kempes","Tierra , Arthur_Dent",
				"Piqua,_Kansas , Buster_Keaton"};
		
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


		st.executeUpdate("INSERT INTO `dbtypes`(`resource`,`type`) VALUES ('Ada,_Ohio','<http://dbpedia.org/class/yago/YagoGeoEntity>')");

		st.executeUpdate("INSERT INTO `dbtypes`(`resource`,`type`) VALUES ('Buenos_Aires','<http://dbpedia.org/ontology/City>')");

		st.executeUpdate("INSERT INTO `dbtypes`(`resource`,`type`) VALUES ('Peekskill,_New_York','<http://dbpedia.org/class/yago/PopulatedPlacesOnTheHuDsonRiver>')");
		st.executeUpdate("INSERT INTO `dbtypes`(`resource`,`type`) VALUES ('Peekskill,_New_York','<http://dbpedia.org/class/yago/CitiesInNewYork>')");
		st.executeUpdate("INSERT INTO `dbtypes`(`resource`,`type`) VALUES ('Peekskill,_New_York','<http://dbpedia.org/class/yago/YagoGeoEntity>')");
		st.executeUpdate("INSERT INTO `dbtypes`(`resource`,`type`) VALUES ('Peekskill,_New_York','<http://dbpedia.org/class/yago/PopulatedPlacesEstablishedIn1684>')");
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
		assertEquals(l1.size(), 1);		
		Instance instance = l1.get(0);
		assertEquals(instance.getResource(), pairs[0]);
		assertEquals(instance.getDistance(), 0, 1e-15);
				
		// k = 10 , 5 pares
		List<Instance> l2 = this.knn.getKNearestNeighbors(10, pair);
		assertEquals(l2.size(), 5);
		instance = l2.get(0);
		assertEquals(instance.getResource(), pairs[0]);
		assertEquals(instance.getDistance(), 0, 1e-15);
		instance = l2.get(1);
		assertEquals(instance.getResource(), pairs[1]);
		assertEquals(instance.getDistance(), 0.375, 1e-15);
		instance = l2.get(2);
		assertEquals(instance.getResource(), pairs[2]);
 		assertEquals(instance.getDistance(), 0.75, 1e-15);
		instance = l2.get(3);
		assertEquals(instance.getResource(), pairs[3]);
		assertEquals(instance.getDistance(), 0.775, 1e-15);
		instance = l2.get(4);
		assertEquals(instance.getResource(), pairs[4]);
		assertEquals(instance.getDistance(), 0.875, 1e-15);
		
		
		///////////falta lo siguiente
		
		// cargar hasta 11 pares
		st = WikipediaConnector.getResultsConnection().createStatement();
		for (int i = 5; i <= 10; i++){
			st.executeUpdate("INSERT INTO `U_page`(`page`) VALUES ('" + pairs[i] + "')");
		}

//		st.executeUpdate("INSERT INTO `dbtypes`(`resource`,`type`) VALUES ('Che_Guevara','')");

		st.executeUpdate("INSERT INTO `dbtypes`(`resource`,`type`) VALUES ('Vincent_van_Gogh','<http://dbpedia.org/class/yago/Person100007846>')");
		st.executeUpdate("INSERT INTO `dbtypes`(`resource`,`type`) VALUES ('Vincent_van_Gogh','<http://dbpedia.org/class/yago/DutchExpatriatesInTheUnitedKingdom>')");
		st.executeUpdate("INSERT INTO `dbtypes`(`resource`,`type`) VALUES ('Vincent_van_Gogh','<http://dbpedia.org/class/yago/PeopleFromNorthBrabant>')");
		st.executeUpdate("INSERT INTO `dbtypes`(`resource`,`type`) VALUES ('Vincent_van_Gogh','<http://dbpedia.org/class/yago/Artist109812338>')");
		st.executeUpdate("INSERT INTO `dbtypes`(`resource`,`type`) VALUES ('Vincent_van_Gogh','<http://dbpedia.org/class/yago/PeopleWithBipolarDisorder>')");
		st.executeUpdate("INSERT INTO `dbtypes`(`resource`,`type`) VALUES ('Vincent_van_Gogh','<http://dbpedia.org/class/yago/FlowerArtists>')");
		st.executeUpdate("INSERT INTO `dbtypes`(`resource`,`type`) VALUES ('Vincent_van_Gogh','<http://dbpedia.org/class/yago/DutchPainters>')");
		st.executeUpdate("INSERT INTO `dbtypes`(`resource`,`type`) VALUES ('Vincent_van_Gogh','<http://dbpedia.org/class/yago/DutchExpatriatesInFrance>')");
		st.executeUpdate("INSERT INTO `dbtypes`(`resource`,`type`) VALUES ('Vincent_van_Gogh','<http://dbpedia.org/class/yago/DutchExpatriatesInBelgium>')");
		st.executeUpdate("INSERT INTO `dbtypes`(`resource`,`type`) VALUES ('Vincent_van_Gogh','<http://dbpedia.org/class/yago/Post-impressionistPainters>')");
		st.executeUpdate("INSERT INTO `dbtypes`(`resource`,`type`) VALUES ('Vincent_van_Gogh','<http://dbpedia.org/class/yago/ArtistsWhoCommittedSuicide>')");
		st.executeUpdate("INSERT INTO `dbtypes`(`resource`,`type`) VALUES ('Vincent_van_Gogh','<http://dbpedia.org/class/yago/AlumniOfTheRoyalAcademyOfFineArts(Antwerp)>')");

		st.executeUpdate("INSERT INTO `dbtypes`(`resource`,`type`) VALUES ('Fidel_Nadal_(reggae_musician)','<http://dbpedia.org/class/yago/ArgentinePeopleOfBlackAfricanDescent>')");
		st.executeUpdate("INSERT INTO `dbtypes`(`resource`,`type`) VALUES ('Fidel_Nadal_(reggae_musician)','<http://dbpedia.org/class/yago/ArgentinePeopleOfSpanishDescent>')");
		st.executeUpdate("INSERT INTO `dbtypes`(`resource`,`type`) VALUES ('Fidel_Nadal_(reggae_musician)','<http://dbpedia.org/class/yago/LivingPeople>')");
		st.executeUpdate("INSERT INTO `dbtypes`(`resource`,`type`) VALUES ('Fidel_Nadal_(reggae_musician)','<http://dbpedia.org/class/yago/PeopleFromBuEnosAires>')");
		st.executeUpdate("INSERT INTO `dbtypes`(`resource`,`type`) VALUES ('Fidel_Nadal_(reggae_musician)','<http://dbpedia.org/class/yago/ReggaeMusicians>')");

		st.executeUpdate("INSERT INTO `dbtypes`(`resource`,`type`) VALUES ('Edwin_Kempes','<http://dbpedia.org/class/yago/Person100007846>')");
		st.executeUpdate("INSERT INTO `dbtypes`(`resource`,`type`) VALUES ('Edwin_Kempes','<http://dbpedia.org/class/yago/LivingPeople>')");
		st.executeUpdate("INSERT INTO `dbtypes`(`resource`,`type`) VALUES ('Edwin_Kempes','<http://dbpedia.org/class/yago/PeopleFromAmsterdam>')");
		st.executeUpdate("INSERT INTO `dbtypes`(`resource`,`type`) VALUES ('Edwin_Kempes','<http://dbpedia.org/class/yago/DutchTennisPlayers>')");

		st.executeUpdate("INSERT INTO `dbtypes`(`resource`,`type`) VALUES ('Buster_Keaton','<http://dbpedia.org/class/yago/Person100007846>')");
		st.executeUpdate("INSERT INTO `dbtypes`(`resource`,`type`) VALUES ('Buster_Keaton','<http://dbpedia.org/class/yago/SilentFilmComedians>')");
		st.executeUpdate("INSERT INTO `dbtypes`(`resource`,`type`) VALUES ('Buster_Keaton','<http://dbpedia.org/class/yago/ActorsFromKansas>')");
		st.executeUpdate("INSERT INTO `dbtypes`(`resource`,`type`) VALUES ('Buster_Keaton','<http://dbpedia.org/class/yago/AmericanSilentFilmActors>')");
		st.executeUpdate("INSERT INTO `dbtypes`(`resource`,`type`) VALUES ('Buster_Keaton','<http://dbpedia.org/class/yago/AmericanPeopleOfDutchDescent>')");
		st.executeUpdate("INSERT INTO `dbtypes`(`resource`,`type`) VALUES ('Buster_Keaton','<http://dbpedia.org/class/yago/AmericanFilmDirectors>')");
		st.executeUpdate("INSERT INTO `dbtypes`(`resource`,`type`) VALUES ('Buster_Keaton','<http://dbpedia.org/class/yago/Mimes>')");
		st.executeUpdate("INSERT INTO `dbtypes`(`resource`,`type`) VALUES ('Buster_Keaton','<http://dbpedia.org/class/yago/SlapstickComedians>')");
		st.executeUpdate("INSERT INTO `dbtypes`(`resource`,`type`) VALUES ('Buster_Keaton','<http://dbpedia.org/class/yago/AmericanFilmActors>')");
		st.executeUpdate("INSERT INTO `dbtypes`(`resource`,`type`) VALUES ('Buster_Keaton','<http://dbpedia.org/class/yago/UkulelePlayers>')");
		st.executeUpdate("INSERT INTO `dbtypes`(`resource`,`type`) VALUES ('Buster_Keaton','<http://dbpedia.org/class/yago/AmericanPeopleOfGermanDescent>')");
		st.executeUpdate("INSERT INTO `dbtypes`(`resource`,`type`) VALUES ('Buster_Keaton','<http://dbpedia.org/class/yago/AmericanPeopleOfEnglishDescent>')");
		st.executeUpdate("INSERT INTO `dbtypes`(`resource`,`type`) VALUES ('Buster_Keaton','<http://dbpedia.org/class/yago/AmericanPeopleOfScotch-IrishDescent>')");
		st.executeUpdate("INSERT INTO `dbtypes`(`resource`,`type`) VALUES ('Buster_Keaton','<http://dbpedia.org/class/yago/PeopleSelf-identifyingAsAlcoholics>')");
		st.executeUpdate("INSERT INTO `dbtypes`(`resource`,`type`) VALUES ('Buster_Keaton','<http://dbpedia.org/class/yago/VauDEvillePerformers>')");
		st.executeUpdate("INSERT INTO `dbtypes`(`resource`,`type`) VALUES ('Buster_Keaton','<http://dbpedia.org/class/yago/Actor109765278>')");

		st.executeUpdate("INSERT INTO `dbtypes`(`resource`,`type`) VALUES ('Arthur_Dent','<http://dbpedia.org/class/yago/FictionalCharacter109587565>')");
		st.executeUpdate("INSERT INTO `dbtypes`(`resource`,`type`) VALUES ('Arthur_Dent','<http://dbpedia.org/class/yago/CharactersInBritishNovelsOfThe20thCentury>')");
		st.executeUpdate("INSERT INTO `dbtypes`(`resource`,`type`) VALUES ('Arthur_Dent','<http://dbpedia.org/class/yago/CharactersInWrittenScienceFiction>')");

		st.executeUpdate("INSERT INTO `dbtypes`(`resource`,`type`) VALUES ('Zundert','<http://dbpedia.org/class/yago/MunicipalitiesOfNorthBrabant>')");
		st.executeUpdate("INSERT INTO `dbtypes`(`resource`,`type`) VALUES ('Zundert','<http://dbpedia.org/class/yago/GeoclassPopulatedPlace>')");
		st.executeUpdate("INSERT INTO `dbtypes`(`resource`,`type`) VALUES ('Zundert','<http://dbpedia.org/class/yago/YagoGeoEntity>')");
		st.executeUpdate("INSERT INTO `dbtypes`(`resource`,`type`) VALUES ('Zundert','<http://dbpedia.org/class/yago/PopulatedPlacesInNorthBrabant>')");

		st.executeUpdate("INSERT INTO `dbtypes`(`resource`,`type`) VALUES ('Amsterdam','<http://dbpedia.org/class/yago/PopulatedPlacesInNorthHolland>')");
		st.executeUpdate("INSERT INTO `dbtypes`(`resource`,`type`) VALUES ('Amsterdam','<http://dbpedia.org/class/yago/EuropeanCapitalsOfCulture>')");
		st.executeUpdate("INSERT INTO `dbtypes`(`resource`,`type`) VALUES ('Amsterdam','<http://dbpedia.org/class/yago/PopulatedPlacesEstablishedInThe13thCentury>')");
		st.executeUpdate("INSERT INTO `dbtypes`(`resource`,`type`) VALUES ('Amsterdam','<http://dbpedia.org/class/yago/HostCitiesOfTheSummerOlympicGames>')");
		st.executeUpdate("INSERT INTO `dbtypes`(`resource`,`type`) VALUES ('Amsterdam','<http://dbpedia.org/class/yago/CapitalsInEurope>')");
		st.executeUpdate("INSERT INTO `dbtypes`(`resource`,`type`) VALUES ('Amsterdam','<http://dbpedia.org/class/yago/GeoclassCapitalOfAPoliticalEntity>')");
		st.executeUpdate("INSERT INTO `dbtypes`(`resource`,`type`) VALUES ('Amsterdam','<http://dbpedia.org/class/yago/PortCitiesAndTownsOfTheNorthSea>')");
		st.executeUpdate("INSERT INTO `dbtypes`(`resource`,`type`) VALUES ('Amsterdam','<http://dbpedia.org/class/yago/PortCitiesAndTownsInTheNetherlands>')");
		st.executeUpdate("INSERT INTO `dbtypes`(`resource`,`type`) VALUES ('Amsterdam','<http://dbpedia.org/class/yago/CitiesInTheNetherlands>')");
		st.executeUpdate("INSERT INTO `dbtypes`(`resource`,`type`) VALUES ('Amsterdam','<http://dbpedia.org/class/yago/1920SummerOlympicVenuEs>')");
		st.executeUpdate("INSERT INTO `dbtypes`(`resource`,`type`) VALUES ('Amsterdam','<http://dbpedia.org/class/yago/1928SummerOlympicVenuEs>')");
		
//		st.executeUpdate("INSERT INTO `dbtypes`(`resource`,`type`) VALUES ('Piqua,_Kansas','')");
		
		st.executeUpdate("INSERT INTO `dbtypes`(`resource`,`type`) VALUES ('Earth','<http://dbpedia.org/class/yago/TerrestrialPlanets>')");
		st.close();
		
		// Obtener nuevo KNN con los valores cargados
		this.knn= new KNN();
		this.knn.enhanceUPage();

		// k = 5 , 11 pares
		List<Instance> l3 = this.knn.getKNearestNeighbors(5, pair);
		assertEquals(l3.size(), 5);
		instance = l3.get(0);
		assertEquals(instance.getResource(), pairs[0]);
		assertEquals(instance.getDistance(), 0, 1e-15);
		instance = l3.get(1);
		assertEquals(instance.getResource(), pairs[1]);
		assertEquals(instance.getDistance(), 0.375, 1e-15);
		instance = l3.get(2);
		assertEquals(instance.getResource(), pairs[5]);
 		assertEquals(instance.getDistance(), 0.5, 1e-15);
		instance = l3.get(3);
		assertEquals(instance.getResource(), pairs[2]);
		assertEquals(instance.getDistance(), 0.75, 1e-15);
		instance = l3.get(4);
		assertEquals(instance.getResource(), pairs[3]);
		assertEquals(instance.getDistance(), 0.775, 1e-15);
		
		
		// k = 10 , 11 pares
		List<Instance> l4 = this.knn.getKNearestNeighbors(10, pair);
		assertEquals(l4.size(), 10);
		instance = l4.get(0);
		assertEquals(instance.getResource(), pairs[0]);
		assertEquals(instance.getDistance(), 0, 1e-15);
		instance = l4.get(1);
		assertEquals(instance.getResource(), pairs[1]);
		assertEquals(instance.getDistance(), 0.375, 1e-15);
		instance = l4.get(2);
		assertEquals(instance.getResource(), pairs[5]);
 		assertEquals(instance.getDistance(), 0.5, 1e-15);
		instance = l4.get(3);
		assertEquals(instance.getResource(), pairs[2]);
		assertEquals(instance.getDistance(), 0.75, 1e-15);
		instance = l4.get(4);
		assertEquals(instance.getResource(), pairs[3]);
		assertEquals(instance.getDistance(), 0.775, 1e-15);

		String[] arrayOfPairs = {pairs[4],pairs[7]};
		Set<String> setOfPairs = new HashSet<String>(Arrays.asList(arrayOfPairs));
		assertTrue(setOfPairs.contains(l4.get(5).getResource()));
		assertTrue(setOfPairs.contains(l4.get(6).getResource()));
		assertEquals(l4.get(5).getDistance(), 0.875, 1e-15);
		assertEquals(l4.get(6).getDistance(), 0.875, 1e-15);
		
		instance = l4.get(7);
		assertEquals(instance.getResource(), pairs[6]);
		assertEquals(instance.getDistance(), 0.9, 1e-15);
		instance = l4.get(8);
		assertEquals(instance.getResource(), pairs[8]);
		assertEquals(instance.getDistance(), 0.9375, 1e-15);
		instance = l4.get(9);
		assertEquals(instance.getResource(), pairs[10]);
		assertEquals(instance.getDistance(), 0.975, 1e-15);
		
	}
	
	

}
