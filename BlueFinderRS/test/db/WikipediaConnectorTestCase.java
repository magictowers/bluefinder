package db;

import static org.junit.Assert.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import org.junit.BeforeClass;
import org.junit.Test;

public class WikipediaConnectorTestCase {
	
private static String propertyFileName;
private static Properties prop;

   @BeforeClass
   public static void setUp() throws IOException{
	   
	   
	   propertyFileName = "setup.properties";
	   prop = new Properties();
	   prop.load(WikipediaConnectorTestCase.class.getClassLoader().getResourceAsStream(propertyFileName));
   	
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
	public void testWikipediaConnection() throws ClassNotFoundException, SQLException{
		Connection connection = WikipediaConnector.getConnection();
		assertEquals(WikipediaConnector.getWikipediaBase(), "localhost/"+connection.getCatalog());
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
	
	
	

}
