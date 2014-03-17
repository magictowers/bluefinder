package suites;
import knn.clean.BlueFinderRecommenderTest;
import normalization.BasicNormalizationTest;

import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import pia.BipartiteGraphGeneratorTestCase;
import pia.PathFinderTest;
import utils.FromToPairTest;
import db.WikipediaConnector;
import db.WikipediaConnectorTestCase;
import evals.GiniIndexTest;
import evals.PathsCleanerTest;
import strategies.UnstarredPathGeneralizationTest;
import utils.PathsResolverTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	WikipediaConnectorTestCase.class, 
	PathFinderTest.class, 
	BipartiteGraphGeneratorTestCase.class, 
	BasicNormalizationTest.class,
	GiniIndexTest.class,
	PathsCleanerTest.class,
	BlueFinderRecommenderTest.class,
	FromToPairTest.class,
    UnstarredPathGeneralizationTest.class,
    PathsResolverTest.class
})
public class PIATestSuite {
	
	@BeforeClass 
	public static void setUpClass() {
       Assume.assumeTrue(WikipediaConnector.isTestEnvironment()); // Common initialization done once for Test1 + Test2
    }
}

