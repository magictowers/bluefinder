package utils;

import static org.junit.Assert.*;

import java.sql.SQLException;

import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import db.DBConnector;
import db.TestSetup;
import db.WikipediaConnector;

public class WikipediaTest {
	
	DBConnector connector;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
       Assume.assumeTrue(WikipediaConnector.isTestEnvironment());
	}

	@Before
	public void setUp() throws Exception {
		this.connector = TestSetup.getDBConnector();
	}

	@Test
	public void testCategoryExists() throws Exception {
		boolean result = Wikipedia.categoryExists(this.connector,"\"The_Raven\",_Édouard_Manet's_illustrations_(featured_picture_set)");
		assertTrue(result);
		result = Wikipedia.categoryExists(this.connector,"The_Raven,_Édouard_Manet's_illustrations_(featured_picture_set)");
		assertFalse(result);
	}

}
