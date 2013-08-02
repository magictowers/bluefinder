package knn.clean;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pia.BipartiteGraphGenerator;

import db.WikipediaConnector;

public class BlueFinderEvaluationTestCase {
	
	private BlueFinderEvaluation evaluation;
	private String dtTypes;
	private String sfTypes;
	
	@BeforeClass
	public static void setupclass(){
		   Assume.assumeTrue(WikipediaConnector.isTestEnvironment());

	}

	@Before
	public void setUp() throws Exception {
		this.evaluation=new BlueFinderEvaluation(new KNN());
		WikipediaConnector.restoreResultIndex();
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
	
	
}
