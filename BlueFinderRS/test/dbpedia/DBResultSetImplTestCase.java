package dbpedia;

import static org.junit.Assert.assertEquals;

import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import db.WikipediaConnector;

public class DBResultSetImplTestCase {
	
	private DBResultSetImp resultSet;
	private DBpediaResultElement diegoCloneElement;
	private DBpediaResultElement diegoElement;
	
	@BeforeClass
	public static void setupclass(){
		   Assume.assumeTrue(WikipediaConnector.isTestEnvironment());

	}

	@Before
	public void setup(){
		this.resultSet = new DBResultSetImp();
		diegoElement = new DBpediaResultElement();
		diegoCloneElement = new DBpediaResultElement();
		diegoElement.put("name", "Diego");
		diegoElement.put("surname", "Torres");
		diegoCloneElement.put("name", "Diego");
		diegoCloneElement.put("surname", "Torres");
		
	}

	@Test
	public void testConstructor() {
		assertEquals(0, this.resultSet.size());
	}
	
	@Test
	public void testAddElement(){
		System.out.println(this.diegoElement.equals(this.diegoCloneElement));
		System.out.println(this.diegoCloneElement.equals(this.diegoElement));
		this.resultSet.addElement(this.diegoElement);
		assertEquals(1, this.resultSet.size());
		this.resultSet.addElement(diegoCloneElement);
		assertEquals(1, this.resultSet.size());
		
	}

}
