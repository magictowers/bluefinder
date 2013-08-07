package suites;
import normalization.BasicNormalizationTest;

import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import pia.BipartiteGraphGeneratorTestCase;
import pia.PathFinderTest;
import db.WikipediaConnector;
import db.WikipediaConnectorTestCase;

@RunWith(Suite.class)
@Suite.SuiteClasses({WikipediaConnectorTestCase.class, 
	PathFinderTest.class, 
	BipartiteGraphGeneratorTestCase.class
	, BasicNormalizationTest.class})
public class PIATestSuite {
	
	@BeforeClass 
	public static void setUpClass() {
       Assume.assumeTrue(WikipediaConnector.isTestEnvironment()); // Common initialization done once for Test1 + Test2
    }
}

