package knn.distance;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import db.WikipediaConnector;

public class SemanticPairTestCase {
	
	private SemanticPair pair;
	private List<String> objectTypes;
	private List<String> subjectTypes;
	private String object;
	private String subject;
	private String property;
	private String semProperty;
	private int id;
	
	@BeforeClass
	public static void setupclass(){
		   Assume.assumeTrue(WikipediaConnector.isTestEnvironment());

	}

	@Before
	public void setUp() throws Exception {
		
		this.object = "Lionel_Messi";
		this.subject = "Rosario,_Santa_Fe";
		this.subjectTypes = new ArrayList<String>();
		this.subjectTypes.add("Footballer"); this.subjectTypes.add("Person");
		
		this.objectTypes= new ArrayList<String>();
		this.objectTypes.add("City");
		
		this.semProperty="type";
		
		this.id=1;
		
		
		
		this.pair=new SemanticPair(this.object,this.subject,this.semProperty,this.objectTypes,this.subjectTypes,this.id);
	}

	@Test
	public void testSemanticPair() {
		assertEquals(this.object, this.pair.getObject());
		assertEquals(this.subject,this.pair.getSubject());
		assertEquals(this.objectTypes, this.pair.getObjectElementsBySemProperty("any"));
		assertEquals(this.subjectTypes, this.pair.getSubjectElementsBySemProperty("other"));
		assertEquals(this.id,this.pair.getId());
	}

	
}
