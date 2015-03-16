package knn.clean;

import static org.junit.Assert.assertEquals;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import utils.BlacklistCategory;
import utils.ProjectSetup;
import db.DBConnector;
import db.TestSetup;
import db.utils.ResultsDbInterface;

public class BlueFinderEvaluationTestCase {
	
	private BlueFinderEvaluation evaluation;
	private DBConnector connector;
	private BlacklistCategory blacklistCategory;
	private ProjectSetup projectSetup;
	
	
	@BeforeClass
	public static void setupclass() throws Exception {
	
		TestSetup.getDBConnector().executeSqlFromFile("dump_U_pageEnhanced.sql");
		TestSetup.getDBConnector().executeSqlFromFile("test_BlueFinderRecommender.sql");
		TestSetup.getDBConnector().executeSqlFromFile("test_BlueFinderEvaluationAndRecommender.sql");
		TestSetup.getDBConnector().executeSqlFromFile("test_dbtypes.sql");

	}

	@Before
	public void setUp() throws Exception {
		this.connector = TestSetup.getDBConnector();
		this.projectSetup = TestSetup.getProjectSetup();
		this.blacklistCategory = TestSetup.getBlacklistCategory();
//        ProjectSetupForTest projectSetup = new ProjectSetupForTest();
        ResultsDbInterface resultsDb = new ResultsDbInterface(this.projectSetup,this.connector);
        KNN knn = new KNN(this.projectSetup, this.connector, resultsDb);
		this.evaluation=new BlueFinderEvaluation(this.blacklistCategory.getBlacklist(),this.connector,knn, resultsDb, projectSetup);
		this.connector.restoreResultIndex();
		}
	
	@After
	public void tearDown() throws Exception {
		String dropStatistics="DROP TABLE IF EXISTS `generalStatistics`";
		String dropParticular="DROP TABLE IF EXISTS `particularStatistics`";
		
		Statement dropGeneral = this.connector.getResultsConnection().createStatement();
		dropGeneral.executeUpdate(dropStatistics);
		dropGeneral.close();
		
		Statement stdropParticular = this.connector.getResultsConnection().createStatement();
		stdropParticular.executeUpdate(dropParticular);
		stdropParticular.close();
		

	}

	@Test
	public void testCreateResultTable() throws Exception {
		
		String tableName="BlueFinderTestResultTable";
		this.evaluation.createResultTable(tableName);
		String query = "show tables like \""+tableName+"\"";
		Statement st = this.connector.getResultsConnection().createStatement();
		ResultSet rs = st.executeQuery(query);
		rs.last();
		assertEquals(1,rs.getRow());
		}
	
	@Test
	public void testCreateStatisticsTables() throws Exception {
		
		this.evaluation.createStatisticsTables();
		String query = "show tables like \"generalStatistics\"";
		Statement st = this.connector.getResultsConnection().createStatement();
		ResultSet rs = st.executeQuery(query);
		rs.last();
		assertEquals(1,rs.getRow());
		
		String query2 = "show tables like \"particularStatistics\"";
		Statement st2 = this.connector.getResultsConnection().createStatement();
		ResultSet rs2 = st2.executeQuery(query2);
		rs2.last();
		assertEquals(1,rs2.getRow());
	}
	
	@Test
	public void testInsertParticularStatistic() throws Exception {
		
		String experimentName = "SC1-Test";
		double precision = 0.45678;
		double recall = 0.12345;
		double f1 = 0.98765;
		double hit_rate = 0.22222;
		double gindex = 0.55555;
		double itemSupport = 1.44444;
		double userSupport = 2.33333;
		long kValue = 30;
		
		
		this.evaluation.createStatisticsTables();
		
		this.evaluation.insertParticularStatistic(experimentName, kValue, precision,recall,f1,hit_rate,gindex,itemSupport,userSupport, 1);
		
		String queryString = "select * from generalStatistics as g inner join particularStatistics as p on g.id=p.id and g.scenario=? and p.kValue=?";
		PreparedStatement pst = this.connector.getResultsConnection().prepareStatement(queryString);
		
		pst.setString(1,experimentName);
		pst.setLong(2,kValue);
		
		ResultSet rs = pst.executeQuery();
		rs.first();
		assertEquals(experimentName,rs.getString("scenario"));
		assertEquals("Incorrect precision", precision,rs.getDouble("precision"),0.0005);
		assertEquals(recall,rs.getDouble("recall"),0.0005);
		assertEquals(f1,rs.getDouble("f1"),0.0005);
		assertEquals(hit_rate,rs.getDouble("hit_rate"), 0.0005);
		assertEquals(gindex, rs.getDouble("GI"), 0.0005);
		assertEquals(itemSupport, rs.getDouble("itemSupport"), 0.0005);
		assertEquals(userSupport, rs.getDouble("userSupport"), 0.0005);
		assertEquals(kValue,rs.getInt("kValue"));
	}
	
	
	
	
	
}
