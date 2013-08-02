package knn;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import db.WikipediaConnector;
/**
 * he Jaccard index, also known as the Jaccard similarity coefficient (originally coined coefficient de communauté by Paul Jaccard), 
 * is a statistic used for comparing the similarity and diversity of sample sets.
 * @author dtorres
 *
 */
public class JaccardFunctionTest {
	
	private String multiWordString;
	private String multiWordString2;
	private JaccardFunction jaccardFunction;
	
	@BeforeClass
	public static void classSetUp(){
		
		Assume.assumeTrue(WikipediaConnector.isTestEnvironment());
	}

	@Before
	public void setup(){
		this.multiWordString = "http://xmlns.com/foaf/0.1/Person " +
				"http://dbpedia.org/ontology/Person " +
				"http://schema.org/MusicGroup " +
				"http://dbpedia.org/ontology/Agent " +
				"http://dbpedia.org/ontology/MusicalArtist " +
				"http://schema.org/Person " +
				"http://dbpedia.org/ontology/Artist " +
				"http://dbpedia.org/class/yago/LivingPeople " +
				"http://dbpedia.org/class/yago/ScottishMaleSingers " +
				"http://dbpedia.org/class/yago/Person100007846 " +
				"http://dbpedia.org/class/yago/CustardRecordsArtists " +
				"http://dbpedia.org/class/yago/RutgersUniversityAlumni " +
				"http://dbpedia.org/class/yago/ScottishActors " +
				"http://dbpedia.org/class/yago/ScottishSongwriters " +
				"http://dbpedia.org/class/yago/ScottishPopSingers";
		this.multiWordString2="http://xmlns.com/foaf/0.1/Person " +
				"http://dbpedia.org/ontology/Person " +
				"http://dbpedia.org/ontology/Agent " +
				"http://schema.org/Person " +
				"http://dbpedia.org/ontology/Cleric " +
				"http://dbpedia.org/ontology/ChristianBishop";
		
		this.jaccardFunction = new JaccardFunction();
	}
	
	@Test
	public void testSplitTypes(){
		List<String> types = this.jaccardFunction.splitTypes(this.multiWordString2);
		assertEquals(6, types.size());
		assertTrue(types.contains("http://xmlns.com/foaf/0.1/Person"));
		assertTrue(types.contains("http://dbpedia.org/ontology/Person"));
		assertTrue(types.contains("http://dbpedia.org/ontology/Agent"));
		assertTrue(types.contains("http://schema.org/Person"));
		assertTrue(types.contains("http://dbpedia.org/ontology/Cleric"));
		assertTrue(types.contains("http://dbpedia.org/ontology/ChristianBishop"));
		}
	
	/**
	 * http://en.wikipedia.org/wiki/Jaccard_index
	 * J(A,B) = |A intersection B|/|A U B|
	 * Jaccard distance = 1 - J(A,B)
	 */
	@Test
	public void testJaccardMeassure() {
		List<String> a = this.jaccardFunction.splitTypes(this.multiWordString);
		List<String> b = this.jaccardFunction.splitTypes(this.multiWordString2);
		
		float resultAB = this.jaccardFunction.distance(a,b);
		float resultBA = this.jaccardFunction.distance(b,a);
		float expected = 1f - (4f/17f);
		
		assertEquals(expected, resultAB, 0.00001);
		assertEquals(expected, resultBA,0.00001);
		
	}
	
	@Test
	public void testJaccardMeassureWithStringTypes(){
		float resultAB = this.jaccardFunction.distance(this.multiWordString,this.multiWordString2);
		float resultBA = this.jaccardFunction.distance(this.multiWordString2,this.multiWordString);
		float expected = 1f - (4f/17f);
		
		assertEquals(expected, resultAB, 0.00001);
		assertEquals(expected, resultBA,0.00001);
	}

}
