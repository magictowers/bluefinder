package dbpedia;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class DBpediaResultElementTestCase {

	private DBpediaResultElement sut;
	
	@BeforeClass
	public static void setupclass() throws Exception {
		 //  Assume.assumeTrue(WikipediaConnector.isTestEnvironment());

	}
	
	@Before
	public void setup(){
		this.sut= new DBpediaResultElement();

	}
	
	@Test
	public void testDBpediaResultElement() {
		DBpediaResultElement re = new DBpediaResultElement();
		assertNull(re.at("any"));
	}

	@Test
	public void testAt() {
		this.sut.put("clave","http://wikipedia.org/Jhon_Doe");
	    assertEquals("http://wikipedia.org/Jhon_Doe", sut.at("clave"));
	    assertNull(sut.at("other"));
	}

	@Test
	public void testPut() {
		this.sut.put("clave","http://wikipedia.org/Jhon_Doe");
	    assertEquals("http://wikipedia.org/Jhon_Doe", sut.at("clave"));
	    this.sut.put("clave", "http://en.wikipedia.org/wiki/Diego_Torres");
	    assertEquals("http://en.wikipedia.org/wiki/Diego_Torres", sut.at("clave"));
	}
	
	@Test
	public void testEquals(){
		DBpediaResultElement other = new DBpediaResultElement();
		this.sut.put("hola", "otros");
		other.put("hola", "otros");
		assertEquals(this.sut, other);
		other.put("Messi", "Jugador 20");
		assertEquals(this.sut, other);
		this.sut.put("Diego", "Maradona");
		assertFalse(this.sut.equals(other));
		
	}

}
