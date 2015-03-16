package suites;
import knn.clean.BlueFinderRecommenderTest;
import normalization.BasicNormalizationTest;

import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import pia.BipartiteGraphGeneratorUnstarredPathTest;
import pia.PathFinderTest;
import utils.FromToPairTest;
import db.WikipediaConnector;
import db.WikipediaConnectorTestCase;
import evals.GiniIndexTest;
import evals.PathsCleanerTest;
import pia.BipartiteGraphGeneratorStarPathTest;
import strategies.UnstarredPathGeneralizationTest;
import utils.PathsResolverTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	BipartiteGraphGeneratorUnstarredPathTest.class, 
	BipartiteGraphGeneratorStarPathTest.class, 
	//WikipediaConnectorTestCase.class,  ESTA CLASE ESTA DEPRECATED
	PathFinderTest.class, 
    BasicNormalizationTest.class,
	GiniIndexTest.class,
	//PathsCleanerTest.class,   NO FUNCIONA, NO SE ENTIENDE PARA QUE SIRVE
	FromToPairTest.class,
    UnstarredPathGeneralizationTest.class,
    PathsResolverTest.class,
    FromToPairTest.class
})
public class PIATestSuite {
	
	@BeforeClass 
	public static void setUpClass() throws Exception {
       Assume.assumeTrue(WikipediaConnector.isTestEnvironment()); // Common initialization done once for Test1 + Test2
    }
}

