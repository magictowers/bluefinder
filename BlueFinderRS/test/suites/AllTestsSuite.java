package suites;

import evals.EvaluationComparatorTest;
import knn.clean.BlueFinderEvaluationTestCase;
import knn.clean.BlueFinderPathsFinderTest;
import knn.clean.BlueFinderRecommenderTest;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import strategies.LastCategoryGeneralizationTestCase;
import strategies.StarPathGeneralizationTest;
import strategies.UnstarredPathGeneralizationTest;

/**
 *
 * @author mkaminose
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
    suites.PIATestSuite.class,
    LastCategoryGeneralizationTestCase.class,
//    StarPathGeneralizationTest.class,
    UnstarredPathGeneralizationTest.class,
    BlueFinderEvaluationTestCase.class,
    BlueFinderRecommenderTest.class,
    BlueFinderPathsFinderTest.class,
    EvaluationComparatorTest.class,
})
public class AllTestsSuite {

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }
    
}
