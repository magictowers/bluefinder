package utils;

import static org.junit.Assert.*;

import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;

import db.WikipediaConnector;

public class PathsResolverTest {

	@BeforeClass 
	public static void setUpClass() {
       Assume.assumeTrue(WikipediaConnector.isTestEnvironment());
    }
	
	@Test
	public void test() {
		fail("Not yet implemented");
	}

}
