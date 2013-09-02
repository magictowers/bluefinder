package utils;

import static org.junit.Assert.*;

import java.sql.SQLException;

import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import db.WikipediaConnector;

public class WikipediaTest {
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
       Assume.assumeTrue(WikipediaConnector.isTestEnvironment());
	}

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testCategoryExists() throws ClassNotFoundException, SQLException {
		boolean result = Wikipedia.categoryExists("\"The_Raven\",_Édouard_Manet's_illustrations_(featured_picture_set)");
		assertTrue(result);
		result = Wikipedia.categoryExists("The_Raven,_Édouard_Manet's_illustrations_(featured_picture_set)");
		assertFalse(result);
	}

}
