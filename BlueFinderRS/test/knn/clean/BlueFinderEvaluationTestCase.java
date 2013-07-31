package knn.clean;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.Before;
import org.junit.Test;

import db.WikipediaConnector;

public class BlueFinderEvaluationTestCase {
	
	private BlueFinderEvaluation evaluation;

	@Before
	public void setUp() throws Exception {
		this.evaluation=new BlueFinderEvaluation(new KNN());
	}

	@Test
	public void testCreateResultTable() throws FileNotFoundException, ClassNotFoundException, SQLException, IOException {
		WikipediaConnector.restoreResultIndex();
		String tableName="BlueFinderTestResultTable";
		this.evaluation.createResultTable(tableName);
		String query = "show tables like \""+tableName+"\"";
		Statement st = WikipediaConnector.getResultsConnection().createStatement();
		ResultSet rs = st.executeQuery(query);
		rs.last();
		assertEquals(1,rs.getRow());
	}

}
