package dbpedia;


import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;


public class DBpediaProxyTestCase {
	
	/* Related pages for 
	 * http://dbpedia.org/ontology/birthPlace 	http://dbpedia.org/resource/Wetumka,_Oklahoma <-
http://dbpedia.org/ontology/nationality 	http://dbpedia.org/resource/United_States <-
http://dbpedia.org/ontology/occupation 	http://dbpedia.org/resource/Test_pilot <-
http://dbpedia.org/ontology/occupation 	http://dbpedia.org/resource/United_States_Naval_Aviator <-
http://dbpedia.org/ontology/selection 	http://dbpedia.org/resource/List_of_astronauts_by_year_of_selection <-
http://dbpedia.org/ontology/mission 	http://dbpedia.org/resource/STS-113
	 */
	
	private String w;
	private DBpediaInterface dbpedia;
	
	
	
 
	@Before
	public void setup(){
		this.w = "John_Herrington";
		this.dbpedia= new DBpediaProxyJenaImpl();
		}
	
	@Test
	public void testGetRelatedPagesToW() throws DBpediaQueryException {
		Map<String,String> elementsToTest = new HashMap<String, String>();
		DBpediaResultSet relatedPages = this.dbpedia.getRelatedPagesTo(this.w);
		assertEquals(6, relatedPages.size());
		Iterator<ResultElement> elements = relatedPages.getIterator();
		while(elements.hasNext()){
			ResultElement element = elements.next();
			elementsToTest.put(element.at("page"), element.at("property"));
		}
		assertEquals("Results are not good", "http://dbpedia.org/ontology/birthPlace", elementsToTest.get("http://en.wikipedia.org/wiki/Wetumka,_Oklahoma"));
		assertEquals("Results are not good", "http://dbpedia.org/ontology/nationality", elementsToTest.get("http://en.wikipedia.org/wiki/United_States"));
		assertEquals("Results are not good", "http://dbpedia.org/ontology/occupation", elementsToTest.get("http://en.wikipedia.org/wiki/Test_pilot"));
		assertEquals("Results are not good", "http://dbpedia.org/ontology/occupation", elementsToTest.get("http://en.wikipedia.org/wiki/United_States_Naval_Aviator"));
		assertEquals("Results are not good", "http://dbpedia.org/ontology/selection", elementsToTest.get("http://en.wikipedia.org/wiki/List_of_astronauts_by_year_of_selection"));
		assertEquals("Results are not good", "http://dbpedia.org/ontology/mission", elementsToTest.get("http://en.wikipedia.org/wiki/STS-113"));
	
	}

}
