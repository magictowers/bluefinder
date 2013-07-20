package db;

import static org.junit.Assert.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class WikipediaConnectorTestCase {
	
private static String propertyFileName;
private static Properties prop;

   @BeforeClass
   public static void classSetUp() throws IOException{
	   
	   
	   propertyFileName = "setup.properties";
	   prop = new Properties();
	   prop.load(WikipediaConnectorTestCase.class.getClassLoader().getResourceAsStream(propertyFileName));
   	
   }
   

	@Test
	public void testReadConfigurationPropertiesWikipediaBase() {
		assertEquals(prop.getProperty("wikipediaDatabase"), WikipediaConnector.getWikipediaBase());
		assertEquals(prop.getProperty("wikipediaDatabaseUser"), WikipediaConnector.getWikipediaDatabaseUser());
		assertEquals(prop.getProperty("wikipediaDatabasePass"), WikipediaConnector.getWikipediaDatabasePass());
	}
	
	@Test
	public void testReadConfigurationPropertiesResultDatabase() {
		assertEquals(prop.getProperty("resultDatabase"), WikipediaConnector.getResultDatabase());
		assertEquals(prop.getProperty("resultDatabaseUser"), WikipediaConnector.getResultDatabaseUser());
		assertEquals(prop.getProperty("resultDatabasePass"), WikipediaConnector.getResultDatabasePass());
	}
	
	@Test
	public void testReadConfigurationPropertiesTestDatabase() {
		assertEquals(prop.getProperty("testDatabase"), WikipediaConnector.getTestDatabase());
		assertEquals(prop.getProperty("testDatabaseUser"), WikipediaConnector.getTestDatabaseUser());
		assertEquals(prop.getProperty("testDatabasePass"), WikipediaConnector.getTestDatabasePass());
	}

	@Test
	public void testWikipediaConnection() throws ClassNotFoundException, SQLException{
		Connection connection = WikipediaConnector.getConnection();
		assertEquals(WikipediaConnector.getResultDatabase(), connection.getCatalog());
	}

}
