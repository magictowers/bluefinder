package db;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class WikipediaConnectorTestCase {
	
private static String propertyFileName;
private static Properties prop;

private Connection testConection;

   @BeforeClass
   public static void classSetUp() throws IOException{
	   
	   
	   propertyFileName = "setup.properties";
	   prop = new Properties();
	   prop.load(WikipediaConnectorTestCase.class.getClassLoader().getResourceAsStream(propertyFileName));
   	
   }
   
   @Before
   public void setUp() throws ClassNotFoundException, SQLException, TestDatabaseSameThatWikipediaDatabaseException{
	   this.testConection=WikipediaConnector.getTestConnection();
   }
   
   @After
   public void tearDown() throws SQLException{
	   this.testConection.close();
   }
   
   @Test
   public void testRestoreTestDatabase() throws FileNotFoundException, ClassNotFoundException, SQLException, TestDatabaseSameThatWikipediaDatabaseException, IOException{
	   
	   ResultSet result = this.testConection.createStatement().executeQuery("select page_title from page where page_id=1");
	   result.first();
	   String messi = result.getString("page_title");
	   
	   this.testConection.createStatement().executeUpdate("UPDATE page SET page_title='Maradona' WHERE page_id=1");
	   
	   ResultSet maradonaRS = this.testConection.createStatement().executeQuery("select page_title from page where page_id=1");
	   maradonaRS.first();
	   
	   assertEquals("Maradona", maradonaRS.getString("page_title"));
	   
	   WikipediaConnector.restoreTestDatabase();
	   
	   ResultSet messiRS = this.testConection.createStatement().executeQuery("select page_title from page where page_id=1");
	   messiRS.first();
	   assertEquals(messi, messiRS.getString("page_title"));
	   
   }

	@Test
	public void testReadConfigurationPropertiesWikipediaBase() {
		assertEquals(prop.getProperty("wikipediaDatabase"), WikipediaConnector.getWikipediaBase());
		assertNotNull(WikipediaConnector.getWikipediaBase());
		assertEquals(prop.getProperty("wikipediaDatabaseUser"), WikipediaConnector.getWikipediaDatabaseUser());
		assertNotNull(WikipediaConnector.getWikipediaDatabaseUser());
		assertEquals(prop.getProperty("wikipediaDatabasePass"), WikipediaConnector.getWikipediaDatabasePass());
		assertNotNull(WikipediaConnector.getWikipediaDatabasePass());
	}
	
	@Test
	public void testReadConfigurationPropertiesResultDatabase() {
		assertEquals(prop.getProperty("resultDatabase"), WikipediaConnector.getResultDatabase());
		assertNotNull(WikipediaConnector.getResultDatabase());
		assertEquals(prop.getProperty("resultDatabaseUser"), WikipediaConnector.getResultDatabaseUser());
		assertNotNull(WikipediaConnector.getResultDatabaseUser());
		assertEquals(prop.getProperty("resultDatabasePass"), WikipediaConnector.getResultDatabasePass());
		assertNotNull(WikipediaConnector.getResultDatabasePass());
	}
	
	@Test
	public void testReadConfigurationPropertiesTestDatabase() {
		assertEquals(prop.getProperty("testDatabase"), WikipediaConnector.getTestDatabase());
		assertNotNull(WikipediaConnector.getTestDatabase());
		assertEquals(prop.getProperty("testDatabaseUser"), WikipediaConnector.getTestDatabaseUser());
		assertNotNull(WikipediaConnector.getTestDatabaseUser());
		assertEquals(prop.getProperty("testDatabasePass"), WikipediaConnector.getTestDatabasePass());
		assertNotNull(WikipediaConnector.getTestDatabasePass());
	}

	
	@Test
	public void testResultsConnection() throws ClassNotFoundException, SQLException{
		Connection connection = WikipediaConnector.getResultsConnection();
		assertEquals(WikipediaConnector.getResultDatabase(), "localhost/"+connection.getCatalog());
		connection.close();
	}
	
	@Test
	public void testTestConnection() throws ClassNotFoundException, SQLException, TestDatabaseSameThatWikipediaDatabaseException{
		Connection connection;
		connection = WikipediaConnector.getTestConnection();
		assertEquals(WikipediaConnector.getTestDatabase(), "localhost/"+connection.getCatalog());
		connection.close();
		
	}
	
	@Test
	public void testTestEnvironmentWithConnection() throws ClassNotFoundException, SQLException, TestDatabaseSameThatWikipediaDatabaseException{
		if(prop.getProperty("testEnvironment").equals("true")){
			Connection connection = WikipediaConnector.getConnection();
			assertEquals(WikipediaConnector.getTestDatabase(), "localhost/"+connection.getCatalog());
		}else{
			Connection connection = WikipediaConnector.getConnection();
			assertEquals(WikipediaConnector.getWikipediaBase(), "localhost/"+connection.getCatalog());
				
		}
	}
	
	
	
	
	

}
