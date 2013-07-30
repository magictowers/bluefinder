package knn.clean;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import knn.distance.SemanticPair;

import org.junit.Before;
import org.junit.Test;

public class SemanticPairInstanceTestCase {

	SemanticPairInstance instance;
	SemanticPair pair;
	
	@Before
	public void setUp() throws Exception {
		String object = "Lionel_Messi";
		String subject = "Rosario,_Santa_Fe";
		List<String> subjectTypes = new ArrayList<String>();
		subjectTypes.add("Footballer"); subjectTypes.add("Person");
		
		List<String> objectTypes= new ArrayList<String>();
		 objectTypes.add("City");
		
		String semProperty="type";
		
		int id=1;
		
		
		
		 pair=new SemanticPair(object,subject,semProperty,objectTypes,subjectTypes,id);
		this.instance = new SemanticPairInstance(0, pair);
		}

	@Test
	public void testEquals() {
		assertEquals(this.instance, new SemanticPairInstance(0,this.pair));
	}

}
