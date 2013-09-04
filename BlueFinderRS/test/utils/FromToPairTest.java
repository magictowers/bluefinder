package utils;

import static org.junit.Assert.*;

import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import db.WikipediaConnector;

public class FromToPairTest {

	private FromToPair pair;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Assume.assumeTrue(WikipediaConnector.isTestEnvironment());
	}

	@Before
	public void setUp() throws Exception {
		this.pair = new FromToPair();
	}

	@Test
	public void setPairTest() {
		System.out.println("setPairTest");
		this.pair.setSeparator(", ");
		this.pair.setPair("from, to 456asdf");
		assertEquals("from", this.pair.getFrom());
		assertEquals("to", this.pair.getTo());
		this.pair.setSeparator("   , ");
		this.pair.setPair("alskdj   , aklsdjfseli");
		assertEquals("alskdj", this.pair.getFrom());
		assertEquals("aklsdjfseli", this.pair.getTo());
	}
	
	@Test
	public void pathHasWildCardsTest() {
		System.out.println("pathHasWildCardsTest");
		this.pair.setFromWildcard("#from");
		this.pair.setToWildcard("#to");
		assertTrue(this.pair.pathHasWildCards("List_of_#from's_magical_creatures"));
		assertFalse(this.pair.pathHasWildCards("List_of_Police_Academy_cast_members"));
	}
	
	@Test
	public void generateFullPathTest() {
		System.out.println("generateFullPathTest");
		this.pair.setFromWildcard("#from");
		this.pair.setToWildcard("#to");
		this.pair.setFrom("Wicked:_The_Life_and_Times_of_the_Wicked_Witch_of_the_West");
		this.pair.setTo("Elphaba");
		assertEquals("Wicked:_The_Life_and_Times_of_the_Wicked_Witch_of_the_West / Cat:Parallel_literature / Cat:The_Wicked_Years / Cat:Characters_in_Wicked:_The_Life_and_Times_of_the_Wicked_Witch_of_the_West / Elphaba", this.pair.generateFullPath("#from / Cat:Parallel_literature / Cat:The_Wicked_Years / Cat:Characters_in_#from / #to"));
		assertEquals("Wicked:_The_Life_and_Times_of_the_Wicked_Witch_of_the_West / List_of_awards_and_nominations_received_by_the_musical_Wicked / Elphaba", this.pair.generateFullPath("#from / List_of_awards_and_nominations_received_by_the_musical_Wicked / #to"));
	}

}
