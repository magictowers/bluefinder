package utils;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;


public class FromToPairTest {

	private FromToPair pair;
	

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
	public void concatPairTest() {
		String separator = FromToPair.SEPARATOR;
		String expected = "hola" + separator + "chau";
		assertEquals(expected, FromToPair.concatPair("hola", "chau"));
	}
	
	@Test
	public void splitPairTest() {
		String separator = FromToPair.SEPARATOR;
		String expectedFrom = "hola";
		String expectedTo = "chau";
		FromToPair pair = FromToPair.splitPair(expectedFrom + separator + expectedTo);
		assertEquals(expectedFrom, pair.getFrom());
		assertEquals(expectedTo, pair.getTo());
		pair = FromToPair.splitPair(expectedFrom + ", " + expectedTo);
		assertEquals("", pair.getFrom());
		assertEquals("", pair.getTo());
	}
	
	@Test
	public void pathHasWildCardsTest() {
		System.out.println("pathHasWildCardsTest");
		assertTrue(this.pair.pathHasWildCards("List_of_#from's_magical_creatures"));
		assertFalse(this.pair.pathHasWildCards("List_of_Police_Academy_cast_members"));
	}
	
	@Test
	public void generateFullPathTest() {
		System.out.println("generateFullPathTest");
		this.pair.setFrom("Wicked:_The_Life_and_Times_of_the_Wicked_Witch_of_the_West");
		this.pair.setTo("Elphaba");
		assertEquals("Wicked:_The_Life_and_Times_of_the_Wicked_Witch_of_the_West / Cat:Parallel_literature / Cat:The_Wicked_Years / Cat:Characters_in_Wicked:_The_Life_and_Times_of_the_Wicked_Witch_of_the_West / Elphaba", this.pair.generateFullPath("#from / Cat:Parallel_literature / Cat:The_Wicked_Years / Cat:Characters_in_#from / #to"));
		assertEquals("Wicked:_The_Life_and_Times_of_the_Wicked_Witch_of_the_West / List_of_awards_and_nominations_received_by_the_musical_Wicked / Elphaba", this.pair.generateFullPath("#from / List_of_awards_and_nominations_received_by_the_musical_Wicked / #to"));
	}

}
