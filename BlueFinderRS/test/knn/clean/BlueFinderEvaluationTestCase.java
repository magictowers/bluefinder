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
	
	@Test
	public void testCreateStatisticsTables(){
		
		this.evaluation.createStatisticsTables();
		String query = "show tables like \"generalStatistics\"";
		Statement st = WikipediaConnector.getResultsConnection().createStatement();
		ResultSet rs = st.executeQuery(query);
		rs.last();
		assertEquals(1,rs.getRow());
		
		String query2 = "show tables like \"particularStatistics\"";
		Statement st2 = WikipediaConnector.getResultsConnection().createStatement();
		ResultSet rs2 = st2.executeQuery(query);
		rs2.last();
		assertEquals(1,rs2.getRow());
	}
	
	@Test
	public void testInsertParticularStatistic(){
		
		String experimentName = "SC1-Test";
		double precision = 0.45678;
		double recall = 0.12345;
		double f1 = 0.98765;
		double hit_rate = 0.22222;
		double gindex = 0.55555;
		double itemSupport = 1.44444;
		double userSupport = 2.33333;
		long kValue = 30000;
		
		
		
		this.evaluation.insertParticularStatistic(experimentName, kValue, precision,recall,f1,hit_rate,gindex,itemSupport,userSupport);
		
		String queryString = "select * from generalStatistics as g inner join particularStatic as p on g.id=p.id and g.scneario=? and p.kValue=?";
		PreparedStatement pst = WikipediaConnector.getResultsConnection().prepareStatement(queryString);
		
		pst.setString(1,experimentName);
		pst.setLong(2,kValue);
		
		ResultSet rs = pst.executeQuery();
		rs.first();
		assertEquals(experimentName,rs.getString("scenario"));
		assertEquals(precision,rs.getDouble("presicion"),0.0005);
		assertEquals(recall,rs.getDouble("recall"),0.0005);
		assertEquals(f1,rs.getDouble("f1"),0.0005);
		assertEquals(hit_rate,rs.getDouble("hit_rate"), 0.0005);
		assertEquals(gindex, rs.getDouble("GI"), 0.0005);
		assertEquals(itemSupport, rs.getDouble("itemSupport"), 0.0005);
		assertEquals(userSupport, rs.getDouble("userSupport"), 0.0005);
		assertSame(kValue,rs.getInt("kValue"));
	
	}
	
	
	
	
}
