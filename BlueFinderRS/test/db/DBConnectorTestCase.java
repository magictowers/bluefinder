package db;

import static org.junit.Assert.*;

import org.junit.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.Before;

import static org.mockito.Mockito.*;



public class DBConnectorTestCase {


	//private Connection testConnection;
	private DBConnector connector;

	/**
	 * The test db environment have to be called "localhost/dbresearch_test"
	 * @throws Exception
	
	@BeforeClass
	public static void classSetUp() throws Exception {
		Assume.assumeTrue(DBConnector.isTestEnvironment());
	} */

	@Before
	public void setUp() throws Exception {
		//armar la instancia de DBConnector con las conexiones
		//this.testConnection = WikipediaConnector.getTestConnection();
		this.connector = TestSetup.getDBConnector();
		//this.testConnection = this.connector.getResultsConnection();
		//this.connector= new DBConnector(this.testConnection,this.testConnection, this.testConnection);
	}




	@After
	public void tearDown() throws Exception {
		//connector.restoreResultIndex();
		//this.connector.getResultsConnection().close();
		this.connector.closeConnection();
	}



	@Test
	public void testConnectionClosedMustBeRecreated() throws Exception {
		Connection resConnection = connector.getResultsConnection();
	
		resConnection.prepareCall("");
		resConnection.close();
		resConnection = connector.getResultsConnection();
		

		assertFalse(resConnection.isClosed());

		resConnection.prepareCall("");

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
	public void testInsertIntoParticularStatistics() throws Exception {
		this.connector.createStatisticsTables();
		
		this.connector.insertParticularStatistics("test", 1, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9);
		
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
		

		this.connector.insertParticularStatistics("test", 2, 3.1, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10);

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


		Connection resultsConnection = connector.getResultsConnection();
		connector.restoreResultIndex();

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




		connector.getResultsConnection().createStatement().executeUpdate("INSERT INTO `NFPC` (`v_from`,`u_to`) values (\"test\",\"test\")");
		connector.getResultsConnection().createStatement().executeUpdate("INSERT INTO `U_page` (`page`) values (\"test\")");
		connector.getResultsConnection().createStatement().executeUpdate("INSERT INTO `UxV` (`u_from`,`v_to`) values (1,2)");
		connector.getResultsConnection().createStatement().executeUpdate("INSERT INTO `V_Normalized` (`path`) values (\"#from / Cat:#from / People_from_#from / #to\")");

		connector.restoreResultIndex();

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

	
	
	@Test
	public void testGetTypesFromDB() throws Exception {
		connector.restoreResultIndex();
		String[] dt = {"<http://dbpedia.org/class/yago/ArgentinePopSingers>","<http://dbpedia.org/class/yago/PeopleFromBuEnosAires>",
				"<http://dbpedia.org/class/yago/Actor109765278>", "<http://dbpedia.org/class/yago/LivingPeople>",
		"<http://dbpedia.org/class/yago/ArgentinePeopleOfItalianDescent>"};
		Set<String> diegoTypes = new HashSet<String>(Arrays.asList(dt));

		assertEquals(diegoTypes, new HashSet<String>(connector.getResourceDBTypes("Diego_Torres")));

	}

	@Test
	public void testCloseConnection() throws Exception {

		DBConnector con = new DBConnector(connector.getWikiConnection(),"root","root","localhost/dbresearch_test",connector.getResultsConnection(),"root","root","localhost/dbresearch_test");

		assertSame(con.getWikiConnection(), connector.getWikiConnection());
		assertSame(con.getResultsConnection(), connector.getResultsConnection());

		con.closeConnection();

		assertNotSame(con.getWikiConnection(), connector.getWikiConnection());
		assertNotSame(con.getResultsConnection(), connector.getResultsConnection());

	}
	
	@Test
	public void testConnectionClosed() throws Exception {

		Connection mockWiki = mock(Connection.class);
		Connection mockResults = mock(Connection.class);

		when(mockWiki.isClosed()).thenReturn(false);
		when(mockResults.isClosed()).thenReturn(false);

		DBConnector con = new DBConnector(mockWiki, mockResults);

		con.closeConnection();

		verify(mockWiki).close();
		verify(mockResults).close();

	}

	@Test(expected = BadInstantiationofDBConnector.class)
	public void testConstructorNullConnections() throws Exception {
		new DBConnector(null, null);
	}

	@Test(expected = BadInstantiationofDBConnector.class)
	public void testConstructorWikiNullConnection() throws Exception {
		new DBConnector(null, connector.getResultsConnection());
	}
	
	@Test(expected = BadInstantiationofDBConnector.class)
	public void testConstructorResultsNullConnection() throws Exception {
		new DBConnector(connector.getWikiConnection(), null);
	}

	@Test
	public void testCompleteConstructorConnection() throws Exception {
		DBConnector c = new DBConnector(connector.getWikiConnection(),"wikiUser","wikiPass","wikiDB",
				connector.getResultsConnection(),"resultsUser","resultsPass","resultsDB");
		
		assertSame(c.getWikiConnection(), connector.getWikiConnection());
		assertEquals(c.getWikiUser(), "wikiUser");
		assertEquals(c.getWikiPass(), "wikiPass");
		assertEquals(c.getWikiDatabase(), "wikiDB");
		assertSame(c.getResultsConnection(), connector.getResultsConnection());
		assertEquals(c.getResultsUser(), "resultsUser");
		assertEquals(c.getResultsPass(), "resultsPass");
		assertEquals(c.getResultsDatabase(), "resultsDB");
		
	}

	@Test
	public void testConnectionSetters() throws Exception {
		DBConnector c = new DBConnector(connector.getResultsConnection(), connector.getWikiConnection());
		c.setWikiConnection(connector.getWikiConnection());
		c.setResultsConnection(connector.getResultsConnection());
		c.setWikiUser("wikiUser");
		c.setWikiPass("wikiPass");
		c.setWikiDatabase("wikiDB");
		c.setResultsUser("resultsUser");
		c.setResultsPass("resultsPass");
		c.setResultsDatabase("resultsDB");

		assertSame(c.getWikiConnection(), connector.getWikiConnection());
		assertSame(c.getResultsConnection(), connector.getResultsConnection());
		assertEquals(c.getWikiUser(), "wikiUser");
		assertEquals(c.getWikiPass(), "wikiPass");
		assertEquals(c.getWikiDatabase(), "wikiDB");
		assertEquals(c.getResultsUser(), "resultsUser");
		assertEquals(c.getResultsPass(), "resultsPass");
		assertEquals(c.getResultsDatabase(), "resultsDB");
	}


}
