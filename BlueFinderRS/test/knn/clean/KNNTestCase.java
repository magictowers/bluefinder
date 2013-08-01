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
import java.util.Set;

import knn.distance.SemanticPair;

import org.junit.Before;
import org.junit.Test;

import db.WikipediaConnector;

public class KNNTestCase {

	private KNN knn;
	Set<String> rosarioTypes;
	Set<String> diegoTypes;
	String dtTypes;
	String sfTypes;
	
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

}
