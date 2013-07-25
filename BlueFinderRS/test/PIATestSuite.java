import normalization.BasicNormalizationTest;

import org.junit.runners.Suite;
import org.junit.runner.RunWith;

import pia.BipartiteGraphGeneratorTestCase;
import pia.PathFinderTest;

import db.WikipediaConnectorTestCase;

@RunWith(Suite.class)
@Suite.SuiteClasses({WikipediaConnectorTestCase.class, PathFinderTest.class, BipartiteGraphGeneratorTestCase.class
	, BasicNormalizationTest.class})
public class PIATestSuite {
  //nothing
}

