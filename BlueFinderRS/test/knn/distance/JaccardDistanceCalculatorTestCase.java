package knn.distance;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

/**
 * @author dtorres
 *
 */
public class JaccardDistanceCalculatorTestCase {
	
	protected ISemPair pairOne= mock(ISemPair.class);
	protected ISemPair pairTwo = mock(ISemPair.class);
	protected ISemPair emptyPair = mock(ISemPair.class);
	protected JaccardDistanceCalculator calculator;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		
		this.calculator = new JaccardDistanceCalculator();
		
		String[] subjectTypesOne = {"<http://dbpedia.org/class/yago/BritishCapitals>",
				"<http://dbpedia.org/class/yago/HostCitiesOfTheCommonwealthGames>",
				"<http://dbpedia.org/resource/City_status_in_the_United_Kingdom>"};
		when(pairOne.getSubjectElementsBySemProperty("<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>")).thenReturn(Arrays.asList(subjectTypesOne));
		
		String[] objetctTypesOne = {"<http://dbpedia.org/class/yago/Actor", "<http://dbpedia.org/class/yago/OscarWinner"};
		when(pairOne.getObjectElementsBySemProperty("<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>")).thenReturn(Arrays.asList(objetctTypesOne));
		
		String[] subjectTypesTwo = {"<http://dbpedia.org/class/yago/BritishCapitals>",
				"<http://dbpedia.org/class/yago/GameOfThrones>"};
		String[] objectTypesTwo = {"<http://dbpedia.org/class/yago/Live_Actor"};
		
		when(pairTwo.getObjectElementsBySemProperty("<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>")).thenReturn(Arrays.asList(objectTypesTwo));
		when(pairTwo.getSubjectElementsBySemProperty("<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>")).thenReturn(Arrays.asList(subjectTypesTwo));
		
		
		when(emptyPair.getSubjectElementsBySemProperty("<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>")).thenReturn(new ArrayList<String>());
		when(emptyPair.getObjectElementsBySemProperty("<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>")).thenReturn(new ArrayList<String>());
		
		
	}

	@Test
	public void testDistanceSameElement() {
		assertEquals(0.0,this.calculator.distance(this.pairOne,this.pairOne),  1e-15);
	}
	@Test
	public void testDistanceNoTypes(){
		assertEquals(0.0, this.calculator.distance(emptyPair, emptyPair), 1e-15);
		
	}
	@Test
	public void testDistance(){
		assertEquals(0.875,this.calculator.distance(pairOne, pairTwo), 1e-15);
	}
	

}

