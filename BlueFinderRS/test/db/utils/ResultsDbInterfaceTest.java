
package db.utils;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import utils.ProjectSetup;
import db.DBConnector;
import db.TestSetup;

/**
 *
 * @author mkaminose & mdurante
 */
public class ResultsDbInterfaceTest {

	private ResultsDbInterface resultsDb;
    private DBConnector connector;
    private ProjectSetup projectSetup;
    
    @Before
    public void setUp() throws Exception {
    	this.connector = TestSetup.getDBConnector();
    	this.projectSetup = TestSetup.getProjectSetup();
        this.resultsDb = new ResultsDbInterface(this.projectSetup,this.connector);
    }
    
    @Test
	public void testGetTypesFromDB() throws Exception {
		ResultsDbInterface.restoreResultIndex(this.connector);
		String[] dt = {"<http://dbpedia.org/class/yago/ArgentinePopSingers>","<http://dbpedia.org/class/yago/PeopleFromBuEnosAires>",
				"<http://dbpedia.org/class/yago/Actor109765278>", "<http://dbpedia.org/class/yago/LivingPeople>",
				"<http://dbpedia.org/class/yago/ArgentinePeopleOfItalianDescent>"};
		Set<String> diegoTypes = new HashSet<String>(Arrays.asList(dt));
		
		assertEquals(diegoTypes, new HashSet<String>(TestSetup.getDBConnector().getResourceDBTypes("Diego_Torres")));
		
	}
    
    @Test
	public void testGetProportionOfConnectedPairs() throws Exception {
		
		ResultsDbInterface.restoreResultIndex(this.connector);
		
		for (int i = 1; i < 11; i++) {
			Connection con = TestSetup.getDBConnector().getResultsConnection();
			Statement st = con.createStatement();
			st.executeUpdate("insert into U_pageEnhanced(`page`,`id`,`subjectTypes`,`objectTypes`) values ("+i+","+i+40+",\"sT\",\"oT\")");
			st.close();
		}
		
		ResultSet rs = this.resultsDb.getRandomProportionOfConnectedPairs(10);
		rs.last();
		assertEquals(1,rs.getRow());
		
		rs = this.resultsDb.getRandomProportionOfConnectedPairs(100);
		rs.last();
		
		rs = this.resultsDb.getRandomProportionOfConnectedPairs(120);
		rs.last();
		assertEquals(10, rs.getRow());
	}
    
    @Test
    public void testCreateResultTable() {
    	String table = "TestTableName";
    	try {
			resultsDb.createResultTable(table);
	    	Statement statement = resultsDb.getConnection().createStatement();
			statement.executeUpdate("DROP TABLE `"+ table +"`");
			statement.close();
		} catch (Exception e) {
			e.printStackTrace();
			fail("Cannot create ResultTable");
		}
    }

	@Test
	public void testCreateStatisticsTables() throws Exception {
		ResultsDbInterface.createStatisticsTables(resultsDb.getConnection());
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
	public void testGetNotFoundPaths() throws Exception {
		Connection resultsConnection = this.connector.getResultsConnection();
		connector.restoreResultIndex();
		List<DbResultMap> ldbm = resultsDb.getNotFoundPaths();
		connector.getResultsConnection().createStatement().executeUpdate("DELETE FROM `NFPC` WHERE `v_from` = \"test1\" AND `u_to` = \"test1\"");
		connector.getResultsConnection().createStatement().executeUpdate("DELETE FROM `NFPC` WHERE `v_from` = \"test2\" AND `u_to` = \"test2\"");

		ResultSet result = resultsConnection.createStatement().executeQuery("select count(*) as suma from `NFPC`");
		result.first();

		connector.getResultsConnection().createStatement().executeUpdate("INSERT INTO `NFPC` (`v_from`,`u_to`) values (\"test1\",\"test1\")");
		connector.getResultsConnection().createStatement().executeUpdate("INSERT INTO `NFPC` (`v_from`,`u_to`) values (\"test2\",\"test2\")");
		
		List<DbResultMap> ldbm2 = resultsDb.getNotFoundPaths();
		assertSame(ldbm.size()+2,ldbm2.size());

		result = resultsConnection.createStatement().executeQuery("select count(*) as suma from `NFPC`");
		result.first();
		assertSame(ldbm.size()+2,result.getInt("suma"));

		connector.getResultsConnection().createStatement().executeUpdate("DELETE FROM `NFPC` WHERE `v_from` = \"test1\" AND `u_to` = \"test1\"");
		connector.getResultsConnection().createStatement().executeUpdate("DELETE FROM `NFPC` WHERE `v_from` = \"test2\" AND `u_to` = \"test2\"");
	}

	@Test
	public void testGetNotFoundPathsWithLimitOffset() throws Exception {
		Connection resultsConnection = this.connector.getResultsConnection();
		connector.restoreResultIndex();
		List<DbResultMap> ldbm = resultsDb.getNotFoundPaths();
		connector.getResultsConnection().createStatement().executeUpdate("DELETE FROM `NFPC` WHERE `v_from` = \"test1\" AND `u_to` = \"test1\"");
		connector.getResultsConnection().createStatement().executeUpdate("DELETE FROM `NFPC` WHERE `v_from` = \"test2\" AND `u_to` = \"test2\"");
		connector.getResultsConnection().createStatement().executeUpdate("DELETE FROM `NFPC` WHERE `v_from` = \"test3\" AND `u_to` = \"test3\"");
		connector.getResultsConnection().createStatement().executeUpdate("DELETE FROM `NFPC` WHERE `v_from` = \"test4\" AND `u_to` = \"test4\"");

		ResultSet result = resultsConnection.createStatement().executeQuery("select count(*) as suma from `NFPC`");
		result.first();

		connector.getResultsConnection().createStatement().executeUpdate("INSERT INTO `NFPC` (`v_from`,`u_to`) values (\"test1\",\"test1\")");
		connector.getResultsConnection().createStatement().executeUpdate("INSERT INTO `NFPC` (`v_from`,`u_to`) values (\"test2\",\"test2\")");
		connector.getResultsConnection().createStatement().executeUpdate("INSERT INTO `NFPC` (`v_from`,`u_to`) values (\"test3\",\"test3\")");
		connector.getResultsConnection().createStatement().executeUpdate("INSERT INTO `NFPC` (`v_from`,`u_to`) values (\"test4\",\"test4\")");

		List<DbResultMap> ldbm2 = resultsDb.getNotFoundPaths((ldbm.size()+4)/2,0);
		List<DbResultMap> ldbm3 = resultsDb.getNotFoundPaths((ldbm.size()+4)/2,(ldbm.size()+4)/2);
		result = resultsConnection.createStatement().executeQuery("select count(*) as suma from `NFPC`");
		result.first();
		assertSame(ldbm.size()+4,result.getInt("suma"));
		assertSame(result.getInt("suma")/2,ldbm2.size());
		assertSame(result.getInt("suma")/2,ldbm3.size());

		connector.getResultsConnection().createStatement().executeUpdate("DELETE FROM `NFPC` WHERE `v_from` = \"test1\" AND `u_to` = \"test1\"");
		connector.getResultsConnection().createStatement().executeUpdate("DELETE FROM `NFPC` WHERE `v_from` = \"test2\" AND `u_to` = \"test2\"");
		connector.getResultsConnection().createStatement().executeUpdate("DELETE FROM `NFPC` WHERE `v_from` = \"test3\" AND `u_to` = \"test3\"");
		connector.getResultsConnection().createStatement().executeUpdate("DELETE FROM `NFPC` WHERE `v_from` = \"test4\" AND `u_to` = \"test4\"");
	}

	@Test
	public void testGetNormalizedPathIdWithString() throws Exception {
		Connection resultsConnection = this.connector.getResultsConnection();
		resultsConnection.createStatement().executeUpdate("DELETE FROM `V_Normalized` WHERE `path` = \"TestPath\"");
		resultsConnection.createStatement().executeUpdate("DELETE FROM `V_Normalized` WHERE `path` = \"AnotherTestPath\"");

		connector.getResultsConnection().createStatement().executeUpdate("INSERT INTO `V_Normalized` (`path`) values (\"TestPath\")");
		ResultSet result = resultsConnection.createStatement().executeQuery("select id, path from `V_Normalized` ORDER BY id DESC");
		result.first();
		assertEquals("TestPath",result.getString("path"));
		int lastId = result.getInt("id");
		assertSame(lastId, resultsDb.getNormalizedPathId("TestPath"));

		connector.getResultsConnection().createStatement().executeUpdate("INSERT INTO `V_Normalized` (`path`) values (\"AnotherTestPath\")");
		result = resultsConnection.createStatement().executeQuery("select id, path from `V_Normalized` ORDER BY id DESC");
		resultsDb.getNormalizedPathId("AnotherTestPath");
		result.first();
		assertEquals("AnotherTestPath",result.getString("path"));
		assertSame(lastId+1, resultsDb.getNormalizedPathId("AnotherTestPath"));
		
		resultsConnection.createStatement().executeUpdate("DELETE FROM `V_Normalized` WHERE `path` = \"TestPath\"");
		resultsConnection.createStatement().executeUpdate("DELETE FROM `V_Normalized` WHERE `path` = \"AnotherTestPath\"");
	}

	@Test
	public void testSaveNormalizedPath() throws Exception {		
		connector.getResultsConnection().createStatement().executeUpdate("DELETE FROM `V_Normalized` WHERE `path` = \"TestingSaveNormalized\"");
		ResultSet rs = this.connector.getResultsConnection().createStatement().executeQuery("select count(*) as suma from V_Normalized");
		rs.first();
		int count = rs.getInt("suma");
		
		this.resultsDb.saveNormalizedPath("TestingSaveNormalized");
		rs = this.connector.getResultsConnection().createStatement().executeQuery("select count(*) as suma from V_Normalized");
		rs.first();
		assertSame(count+1, rs.getInt("suma"));
		assertNotNull(this.resultsDb.getNormalizedPathId("TestingSaveNormalized"));

		connector.getResultsConnection().createStatement().executeUpdate("DELETE FROM `V_Normalized` WHERE `path` = \"TestingSaveNormalized\"");
	}

	@Test
	public void testGetResourceDBTypes() throws Exception {
		Connection resultsConnection = this.connector.getResultsConnection();
		connector.restoreResultIndex();
		String table = this.projectSetup.getDbpediaTypeTable();
		resultsConnection.createStatement().executeUpdate("DELETE FROM `"+table+"` WHERE `resource` = \"test1r\"");
		resultsConnection.createStatement().executeUpdate("DELETE FROM `"+table+"` WHERE `resource` = \"test2r\"");

		List<String> types = this.resultsDb.getResourceDBTypes("test1r");
		assertSame(0,types.size());
		
		ResultSet result = resultsConnection.createStatement().executeQuery("select count(*) as suma from `"+table+"`");
		result.first();

		connector.getResultsConnection().createStatement().executeUpdate("INSERT INTO `"+table+"` (`resource`,`type`) values (\"test1r\",\"test1t1\")");
		connector.getResultsConnection().createStatement().executeUpdate("INSERT INTO `"+table+"` (`resource`,`type`) values (\"test1r\",\"test1t2\")");
		connector.getResultsConnection().createStatement().executeUpdate("INSERT INTO `"+table+"` (`resource`,`type`) values (\"test2r\",\"test2t\")");
		
		List<String> types2 = this.resultsDb.getResourceDBTypes("test1r");
		assertSame(types.size()+2,types2.size());
		assertTrue(types2.containsAll(Arrays.asList("test1t1","test1t2")));

		resultsConnection.createStatement().executeUpdate("DELETE FROM `"+table+"` WHERE `resource` = \"test1r\"");
		resultsConnection.createStatement().executeUpdate("DELETE FROM `"+table+"` WHERE `resource` = \"test2r\"");
	}
	

	@Test
	public void testGetTupleIdWithTuple() throws Exception {// TestPage must not exist
		Connection resultsConnection = this.connector.getResultsConnection();
		resultsConnection.createStatement().executeUpdate("DELETE FROM `U_page` WHERE `page` = \"TestPage\"");
		resultsConnection.createStatement().executeUpdate("DELETE FROM `U_page` WHERE `page` = \"AnotherTestPage\"");
		
		connector.getResultsConnection().createStatement().executeUpdate("INSERT INTO `U_page` (`page`) values (\"TestPage\")");
		ResultSet result = resultsConnection.createStatement().executeQuery("select id, page from `U_page` ORDER BY id DESC");
		result.first();
		assertEquals("TestPage",result.getString("page"));
		int lastId = result.getInt("id");
		assertSame(lastId, resultsDb.getTupleId("TestPage"));

		connector.getResultsConnection().createStatement().executeUpdate("INSERT INTO `U_page` (`page`) values (\"AnotherTestPage\")");
		result = resultsConnection.createStatement().executeQuery("select id, page from `U_page` ORDER BY id DESC");
		resultsDb.getNormalizedPathId("AnotherTestPage");
		result.first();
		assertEquals("AnotherTestPage",result.getString("page"));
		assertSame(lastId+1, resultsDb.getTupleId("AnotherTestPage"));

		resultsConnection.createStatement().executeUpdate("DELETE FROM `U_page` WHERE `page` = \"TestPage\"");
		resultsConnection.createStatement().executeUpdate("DELETE FROM `U_page` WHERE `page` = \"AnotherTestPage\"");
	}
	
	@Test
	public void testSaveTuple() throws Exception {		
		ResultSet rs = this.connector.getResultsConnection().createStatement().executeQuery("select count(*) as suma from U_page");
		rs.first();
		int count = rs.getInt("suma");
		
		this.resultsDb.saveTuple("TestingSaveTuple");
		rs = this.connector.getResultsConnection().createStatement().executeQuery("select count(*) as suma from U_page");
		rs.first();
		assertSame(count+1, rs.getInt("suma"));
		assertNotNull(this.resultsDb.getNormalizedPathId("TestingSaveTuple"));

		connector.getResultsConnection().createStatement().executeUpdate("DELETE FROM `U_page` WHERE `page` = \"TestingSaveTuple\"");
	}

	@Test
	public void testGetTuples() throws Exception { //distincts?
		Connection resultsConnection = this.connector.getResultsConnection();

		connector.getResultsConnection().createStatement().executeUpdate("DELETE FROM `U_page`");
		connector.getResultsConnection().createStatement().executeUpdate("INSERT INTO `U_page` (`page`) values (\"TestPage1\")");
		connector.getResultsConnection().createStatement().executeUpdate("INSERT INTO `U_page` (`page`) values (\"TestPage2\")");
		connector.getResultsConnection().createStatement().executeUpdate("INSERT INTO `U_page` (`page`) values (\"TestPage2\")");
		connector.getResultsConnection().createStatement().executeUpdate("INSERT INTO `U_page` (`page`) values (\"TestPage3\")");
		connector.getResultsConnection().createStatement().executeUpdate("INSERT INTO `U_page` (`page`) values (\"TestPage3\")");
		ResultSet result = resultsConnection.createStatement().executeQuery("SELECT id, page, count(*) as suma FROM `U_page` ORDER BY id DESC LIMIT 0, 3");
		
		result.first();
		assertEquals("5",result.getString("suma"));
		
		List<DbResultMap> list = resultsDb.getTuples();
		assertSame(5, list.size());
		for(DbResultMap m : list){
			m.getString("tuple").contains("TestPage");
		}
		
		connector.getResultsConnection().createStatement().executeUpdate("DELETE FROM `U_page`");
	}

	@Test
	public void testSaveEdgeWithPathIdTupleId() throws Exception {
		connector.getResultsConnection().createStatement().executeUpdate("DELETE FROM `UxV` WHERE `u_from` = 1313 AND `v_to` = 1212");
        
		ResultSet rs = this.connector.getResultsConnection().createStatement().executeQuery("SELECT *, count(*) AS suma FROM UxV WHERE `u_from` = 1313 AND `v_to` = 1212");
		rs.first();
		int count = rs.getInt("suma");
		
		this.resultsDb.saveEdge(1212,1313);
		rs = this.connector.getResultsConnection().createStatement().executeQuery("SELECT *, count(*) AS suma FROM UxV WHERE `u_from` = 1313 AND `v_to` = 1212");
		rs.first();
		assertSame(count+1, rs.getInt("suma"));

		connector.getResultsConnection().createStatement().executeUpdate("DELETE FROM `UxV` WHERE `u_from` = 1313 AND `v_to` = 1212");
	}

	@Test
	public void testSaveEdgeWithPathIdTupleIdDescription() throws Exception {
		connector.getResultsConnection().createStatement().executeUpdate("DELETE FROM `UxV` WHERE `u_from` = 1616 AND `v_to` = 1515 AND `description` = \"TestSaveEdge\"");
		ResultSet rs = this.connector.getResultsConnection().createStatement().executeQuery("SELECT *, count(*) AS suma FROM UxV WHERE `u_from` = 1616 AND `v_to` = 1515 AND `description` = \"TestSaveEdge\"");
		rs.first();
		int count = rs.getInt("suma");
		
		this.resultsDb.saveEdge(1515,1616,"TestSaveEdge");
		rs = this.connector.getResultsConnection().createStatement().executeQuery("SELECT *, count(*) AS suma FROM UxV WHERE `u_from` = 1616 AND `v_to` = 1515 AND `description` = \"TestSaveEdge\"");
		rs.first();
		assertSame(count+1, rs.getInt("suma"));

		connector.getResultsConnection().createStatement().executeUpdate("DELETE FROM `UxV` WHERE `u_from` = 1616 AND `v_to` = 1515 AND `description` = \"TestSaveEdge\"");
	}

	@Test
	public void testSaveNotFoundPath() throws Exception {

		connector.getResultsConnection().createStatement().executeUpdate("DELETE FROM NFPC WHERE `v_from` = \"fromTest\" AND `u_to` = \"toTest\"");
		ResultSet rs = this.connector.getResultsConnection().createStatement().executeQuery("select count(*) as suma from NFPC");
		rs.first();
		int count = rs.getInt("suma");
		
		this.resultsDb.saveNotFoundPath("fromTest", "toTest");
		rs = this.connector.getResultsConnection().createStatement().executeQuery("SELECT *, count(*) AS suma FROM NFPC WHERE `v_from` = \"fromTest\" AND `u_to` = \"toTest\"");
		rs.first();
		assertSame(count+1, rs.getInt("suma"));

		connector.getResultsConnection().createStatement().executeUpdate("DELETE FROM NFPC WHERE `v_from` = \"fromTest\" AND `u_to` = \"toTest\"");
	}

	@Test
	public void testRemoveNotFoundPathWithId() throws Exception {

		connector.getResultsConnection().createStatement().executeUpdate("DELETE FROM NFPC WHERE `v_from` = \"fromTest\" AND `u_to` = \"toTest\"");
		ResultSet rs = this.connector.getResultsConnection().createStatement().executeQuery("SELECT *, count(*) AS suma FROM NFPC WHERE `v_from` = \"fromTest\" AND `u_to` = \"toTest\"");
		rs.first();
		int count = rs.getInt("suma");
		this.resultsDb.saveNotFoundPath("fromTest", "toTest");
		rs = this.connector.getResultsConnection().createStatement().executeQuery("SELECT *, count(*) AS suma FROM NFPC WHERE `v_from` = \"fromTest\" AND `u_to` = \"toTest\"");
		rs.first();
		assertSame(count+1, rs.getInt("suma"));
		
		this.resultsDb.removeNotFoundPath(rs.getInt("id"));
		rs = this.connector.getResultsConnection().createStatement().executeQuery("SELECT *, count(*) AS suma FROM NFPC WHERE `v_from` = \"fromTest\" AND `u_to` = \"toTest\"");
		rs.first();
		assertSame(0, rs.getInt("suma"));

		connector.getResultsConnection().createStatement().executeUpdate("DELETE FROM NFPC WHERE `v_from` = \"fromTest\" AND `u_to` = \"toTest\"");
	}
	
	@Test
	public void testGetNormalizedPathsWithTuple() throws Exception { 
		Connection resultsConnection = this.connector.getResultsConnection();
		resultsConnection.createStatement().executeUpdate("DELETE FROM `V_Normalized` WHERE `path` = \"TestPath1\"");
		resultsConnection.createStatement().executeUpdate("DELETE FROM `V_Normalized` WHERE `path` = \"TestPath2\"");
		resultsConnection.createStatement().executeUpdate("DELETE FROM `V_Normalized` WHERE `path` = \"TestPath3\"");
		resultsConnection.createStatement().executeUpdate("DELETE FROM `U_page` WHERE `page` = \"TestTuple1\"");
		resultsConnection.createStatement().executeUpdate("DELETE FROM `U_page` WHERE `page` = \"TestTuple2\"");
		resultsConnection.createStatement().executeUpdate("DELETE FROM `U_page` WHERE `page` = \"TestTuple3\"");
		resultsConnection.createStatement().executeUpdate("DELETE FROM `UxV` WHERE `description` = \"testGetNormalizedPathsWithTuple\"");

		this.resultsDb.saveNormalizedPath("TestPath1");
		this.resultsDb.saveNormalizedPath("TestPath2");
		this.resultsDb.saveNormalizedPath("TestPath3");

		this.resultsDb.saveTuple("TestTuple1");
		this.resultsDb.saveTuple("TestTuple2");
		this.resultsDb.saveTuple("TestTuple3");

		int normalizedPathId1 = this.resultsDb.getNormalizedPathId("TestPath1");
		int normalizedPathId2 = this.resultsDb.getNormalizedPathId("TestPath2");
		int normalizedPathId3 = this.resultsDb.getNormalizedPathId("TestPath3");
		int tupleId1 = this.resultsDb.getTupleId("TestTuple1");
		int tupleId2 = this.resultsDb.getTupleId("TestTuple2");
		//int tupleId3 = this.resultsDb.getTupleId("TestTuple3");

		this.resultsDb.saveEdge(normalizedPathId1,tupleId1,"testGetNormalizedPathsWithTuple");
		this.resultsDb.saveEdge(normalizedPathId1,tupleId2,"testGetNormalizedPathsWithTuple");
		this.resultsDb.saveEdge(normalizedPathId2,tupleId1,"testGetNormalizedPathsWithTuple");
		this.resultsDb.saveEdge(normalizedPathId2,tupleId2,"testGetNormalizedPathsWithTuple");
		this.resultsDb.saveEdge(normalizedPathId3,tupleId2,"testGetNormalizedPathsWithTuple");

		Set<String> s1 = this.resultsDb.getNormalizedPaths("TestTuple1");
		Set<String> s2 = this.resultsDb.getNormalizedPaths("TestTuple2");
		Set<String> s3 = this.resultsDb.getNormalizedPaths("TestTuple3");

		assertSame(2,s1.size());
		assertTrue(s1.contains("TestPath1"));
		assertTrue(s1.contains("TestPath2"));

		assertSame(3,s2.size());
		assertTrue(s2.contains("TestPath1"));
		assertTrue(s2.contains("TestPath2"));
		assertTrue(s2.contains("TestPath3"));

		assertSame(0,s3.size());

		resultsConnection.createStatement().executeUpdate("DELETE FROM `V_Normalized` WHERE `path` = \"TestPath1\"");
		resultsConnection.createStatement().executeUpdate("DELETE FROM `V_Normalized` WHERE `path` = \"TestPath2\"");
		resultsConnection.createStatement().executeUpdate("DELETE FROM `V_Normalized` WHERE `path` = \"TestPath3\"");
		resultsConnection.createStatement().executeUpdate("DELETE FROM `U_page` WHERE `page` = \"TestTuple1\"");
		resultsConnection.createStatement().executeUpdate("DELETE FROM `U_page` WHERE `page` = \"TestTuple2\"");
		resultsConnection.createStatement().executeUpdate("DELETE FROM `U_page` WHERE `page` = \"TestTuple3\"");
		resultsConnection.createStatement().executeUpdate("DELETE FROM `UxV` WHERE `description` = \"testGetNormalizedPathsWithTuple\"");
	}
	
	
	@Test
	public void testRestoreTestDatabase() throws Exception {
		this.connector.restoreTestDatabase();

		ResultSet result = this.connector.getResultsConnection().createStatement().executeQuery("select page_title from page where page_id=1");
		result.first();
		String messi = result.getString("page_title");

		this.connector.getResultsConnection().createStatement().executeUpdate("UPDATE page SET page_title='Maradona' WHERE page_id=1");

		ResultSet maradonaRS = this.connector.getResultsConnection().createStatement().executeQuery("select page_title from page where page_id=1");
		maradonaRS.first();

		assertEquals("Maradona", maradonaRS.getString("page_title"));

		this.connector.restoreTestDatabase();

		ResultSet messiRS = this.connector.getResultsConnection().createStatement().executeQuery("select page_title from page where page_id=1");
		messiRS.first();
		assertEquals(messi, messiRS.getString("page_title"));

	}

	@Test
	public void testCreateGeneralStatisticsTables() throws Exception {
		ResultSet result;

		String dropTable = "DROP TABLE IF EXISTS `generalStatistics`";
		Statement statement = this.connector.getResultsConnection().createStatement();
		statement.executeUpdate(dropTable);
		statement.close();

		String createSentence = "CREATE TABLE IF NOT EXISTS `generalStatistics` (`id` int(11) NOT NULL AUTO_INCREMENT, `scenario` varchar(45) NOT NULL, PRIMARY KEY (`id`),"
				+ "UNIQUE KEY `scenario_UNIQUE` (`scenario`)) ENGINE=InnoDB DEFAULT CHARSET=utf8";
		statement = this.connector.getResultsConnection().createStatement();
		statement.executeUpdate(createSentence);
		statement.close();

		this.connector.getResultsConnection().createStatement().executeUpdate("INSERT INTO `generalStatistics` (`scenario`) values (\"test\")");
		result = this.connector.getResultsConnection().createStatement().executeQuery("select count(*) as suma from `generalStatistics`");
		result.first();
		assertSame(1,result.getInt("suma"));

		this.connector.createStatisticsTables();	
		result = this.connector.getResultsConnection().createStatement().executeQuery("select id, scenario, count(*) as suma from `generalStatistics`");
		result.first();
		assertSame(0,result.getInt("suma"));

		
		
		dropTable = "DROP TABLE IF EXISTS `particularStatistics`";
		statement = this.connector.getResultsConnection().createStatement();
		statement.executeUpdate(dropTable);
		statement.close();

		createSentence = "CREATE TABLE IF NOT EXISTS `particularStatistics` (`id` int(11) NOT NULL AUTO_INCREMENT, `general_id` int(11) NOT NULL,`kValue` int(11) NOT NULL,`precision` float(15,8) NOT NULL DEFAULT '0',"
				+ "`recall` float(15,8) NOT NULL DEFAULT '0', `f1` float(15,8) NOT NULL DEFAULT '0',`hit_rate` float(15,8) NOT NULL DEFAULT '0',"
				+ " `GI` float(15,8) NOT NULL DEFAULT '0',`itemSupport` float(15,8) NOT NULL DEFAULT '0', `userSupport` float(15,8) NOT NULL DEFAULT '0', `limit` int(11) NOT NULL, "
				+ "PRIMARY KEY (`id`)) ENGINE=InnoDB DEFAULT CHARSET=utf8";
		statement = this.connector.getResultsConnection().createStatement();
		statement.executeUpdate(createSentence);
		statement.close();

		this.connector.getResultsConnection().createStatement().executeUpdate("INSERT INTO `particularStatistics` (`general_id`,`kValue`,`limit`) values (10,10,10)");
		result = this.connector.getResultsConnection().createStatement().executeQuery("select count(*) as suma from `particularStatistics`");
		result.first();
		assertSame(1,result.getInt("suma"));

		this.connector.createStatisticsTables();	
		result = this.connector.getResultsConnection().createStatement().executeQuery("select `id`, `general_id`, `kValue`, `precision`, `recall`, `f1`, `hit_rate`, `GI`, `itemSupport`, `userSupport`, `limit`, count(*) as suma from `particularStatistics`");
		result.first();
		assertSame(0,result.getInt("suma"));
		
	}

	@Test
	public void testInsertIntoParticularStatistic() throws Exception {
		this.connector.createStatisticsTables();
		
		this.resultsDb.insertParticularStatistic("test", 1, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9);
		
		ResultSet gRS = this.connector.getResultsConnection().createStatement().executeQuery("select *, count(*) as suma from generalStatistics where id=1");
		gRS.first();
		assertSame(1, gRS.getInt("suma"));
		assertEquals("test", gRS.getString("scenario"));

		ResultSet pRS = this.connector.getResultsConnection().createStatement().executeQuery("select *, count(*) as suma from particularStatistics where id=1 and general_id=1");
		pRS.first();
		assertSame(1, pRS.getInt("kValue"));
		assertEquals(2.0, pRS.getDouble("precision"),1e-15);
		assertEquals(3.0, pRS.getDouble("recall"),1e-15);
		assertEquals(4.0, pRS.getDouble("f1"),1e-15);
		assertEquals(5.0, pRS.getDouble("hit_rate"),1e-15);
		assertEquals(6.0, pRS.getDouble("GI"),1e-15);
		assertEquals(7.0, pRS.getDouble("itemSupport"),1e-15);
		assertEquals(8.0, pRS.getDouble("userSupport"),1e-15);
		assertSame(9, pRS.getInt("limit"));
		assertSame(1, gRS.getInt("suma"));
		

		this.resultsDb.insertParticularStatistic("test", 2, 3.1, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10);

		pRS = this.connector.getResultsConnection().createStatement().executeQuery("select *, count(*) as suma from particularStatistics where id=2 and general_id=1");
		pRS.first();
		assertSame(2, pRS.getInt("kValue"));
		assertEquals(3.1, pRS.getDouble("precision"),1e-2);
		assertEquals(4.0, pRS.getDouble("recall"),1e-15);
		assertEquals(5.0, pRS.getDouble("f1"),1e-15);
		assertEquals(6.0, pRS.getDouble("hit_rate"),1e-15);
		assertEquals(7.0, pRS.getDouble("GI"),1e-15);
		assertEquals(8.0, pRS.getDouble("itemSupport"),1e-15);
		assertEquals(9.0, pRS.getDouble("userSupport"),1e-15);
		assertSame(10, pRS.getInt("limit"));
	}
	
	@Test
	public void testInsertIntoParticularStatistics() throws Exception {
		this.connector.createStatisticsTables();
		
		this.resultsDb.insertParticularStatistics("test", 1, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9);
		
		ResultSet gRS = this.connector.getResultsConnection().createStatement().executeQuery("select *, count(*) as suma from generalStatistics where id=1");
		gRS.first();
		assertSame(1, gRS.getInt("suma"));
		assertEquals("test", gRS.getString("scenario"));

		ResultSet pRS = this.connector.getResultsConnection().createStatement().executeQuery("select *, count(*) as suma from particularStatistics where id=1 and general_id=1");
		pRS.first();
		assertSame(1, pRS.getInt("kValue"));
		assertEquals(2.0, pRS.getDouble("precision"),1e-15);
		assertEquals(3.0, pRS.getDouble("recall"),1e-15);
		assertEquals(4.0, pRS.getDouble("f1"),1e-15);
		assertEquals(5.0, pRS.getDouble("hit_rate"),1e-15);
		assertEquals(6.0, pRS.getDouble("GI"),1e-15);
		assertEquals(7.0, pRS.getDouble("itemSupport"),1e-15);
		assertEquals(8.0, pRS.getDouble("userSupport"),1e-15);
		assertSame(9, pRS.getInt("limit"));
		assertSame(1, gRS.getInt("suma"));
		

		this.resultsDb.insertParticularStatistics("test", 2, 3.1, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10);

		pRS = this.connector.getResultsConnection().createStatement().executeQuery("select *, count(*) as suma from particularStatistics where id=2 and general_id=1");
		pRS.first();
		assertSame(2, pRS.getInt("kValue"));
		assertEquals(3.1, pRS.getDouble("precision"),1e-2);
		assertEquals(4.0, pRS.getDouble("recall"),1e-15);
		assertEquals(5.0, pRS.getDouble("f1"),1e-15);
		assertEquals(6.0, pRS.getDouble("hit_rate"),1e-15);
		assertEquals(7.0, pRS.getDouble("GI"),1e-15);
		assertEquals(8.0, pRS.getDouble("itemSupport"),1e-15);
		assertEquals(9.0, pRS.getDouble("userSupport"),1e-15);
		assertSame(10, pRS.getInt("limit"));
		
	}

	@Test
	public void testRestoreResultIndex() throws Exception {


		Connection resultsConnection = this.connector.getResultsConnection();
		this.connector.restoreResultIndex();

		ResultSet result = resultsConnection.createStatement().executeQuery("select count(*) as suma from `NFPC`");
		result.first();
		assertSame(0,result.getInt("suma"));

		result = resultsConnection.createStatement().executeQuery("select count(*) as suma from `U_page`");
		result.first();
		assertSame(0,result.getInt("suma"));

		result = resultsConnection.createStatement().executeQuery("select count(*) as suma from `UxV`");
		result.first();
		assertSame(0,result.getInt("suma"));

		result = resultsConnection.createStatement().executeQuery("select count(*) as suma from `V_Normalized`");
		result.first();
		assertSame(0,result.getInt("suma"));




		this.connector.getResultsConnection().createStatement().executeUpdate("INSERT INTO `NFPC` (`v_from`,`u_to`) values (\"test\",\"test\")");
		this.connector.getResultsConnection().createStatement().executeUpdate("INSERT INTO `U_page` (`page`) values (\"test\")");
		this.connector.getResultsConnection().createStatement().executeUpdate("INSERT INTO `UxV` (`u_from`,`v_to`) values (1,2)");
		this.connector.getResultsConnection().createStatement().executeUpdate("INSERT INTO `V_Normalized` (`path`) values (\"#from / Cat:#from / People_from_#from / #to\")");

		this.connector.restoreResultIndex();

		result = resultsConnection.createStatement().executeQuery("select count(*) as suma from `NFPC`");
		result.first();
		assertSame(0,result.getInt("suma"));

		result = resultsConnection.createStatement().executeQuery("select count(*) as suma from `U_page`");
		result.first();
		assertSame(0,result.getInt("suma"));

		result = resultsConnection.createStatement().executeQuery("select count(*) as suma from `UxV`");
		result.first();
		assertSame(0,result.getInt("suma"));

		result = resultsConnection.createStatement().executeQuery("select count(*) as suma from `V_Normalized`");
		result.first();
		assertSame(0,result.getInt("suma"));
	}
}
