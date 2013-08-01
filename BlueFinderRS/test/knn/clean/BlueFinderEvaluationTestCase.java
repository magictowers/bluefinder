package knn.clean;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.Before;
import org.junit.Test;

import pia.BipartiteGraphGenerator;

import db.WikipediaConnector;

public class BlueFinderEvaluationTestCase {
	
	private BlueFinderEvaluation evaluation;
	private String dtTypes;
	private String sfTypes;

	@Before
	public void setUp() throws Exception {
		this.evaluation=new BlueFinderEvaluation(new KNN());
		WikipediaConnector.restoreResultIndex();
		this.dtTypes="<http://dbpedia.org/class/yago/ArgentinePopSingers> <http://dbpedia.org/class/yago/PeopleFromBuEnosAires> " +
				"<http://dbpedia.org/class/yago/Actor109765278> <http://dbpedia.org/class/yago/LivingPeople> " +
				"<http://dbpedia.org/class/yago/ArgentinePeopleOfItalianDescent>";
		
		this.sfTypes="<http://dbpedia.org/class/yago/YagoGeoEntity> <http://dbpedia.org/class/yago/PopulatedPlacesInSantaFeProvince>";
	}

	@Test
	public void testCreateResultTable() throws FileNotFoundException, ClassNotFoundException, SQLException, IOException {
		
		String tableName="BlueFinderTestResultTable";
		this.evaluation.createResultTable(tableName);
		String query = "show tables like \""+tableName+"\"";
		Statement st = WikipediaConnector.getResultsConnection().createStatement();
		ResultSet rs = st.executeQuery(query);
		rs.last();
		assertEquals(1,rs.getRow());
	}
	
	@Test
	public void testEnhanceU_Page() throws FileNotFoundException, ClassNotFoundException, SQLException, IOException{
		WikipediaConnector.restoreResultIndex();
		
		Statement st = WikipediaConnector.getResultsConnection().createStatement();
		st.executeUpdate("INSERT INTO `U_page`(`page`) VALUES (\"Rosario,_Santa_Fe , Diego_Torres\")");
		st.close();
		String tableName = "U_pageEnhanced";
		this.evaluation.enhanceUPage();
		
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

}
