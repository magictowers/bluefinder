package knn;

import static org.junit.Assert.*;

import java.sql.SQLException;
import java.util.List;
import java.util.NavigableSet;
import java.util.TreeSet;

import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import db.WikipediaConnector;


public class KnnTest {
	
	private NavigableSet<Instance> resultSet;
	private Instance testInstance;
	private KNN knn;

	@BeforeClass
	public static void setupclass(){
		   Assume.assumeTrue(WikipediaConnector.isTestEnvironment());

	}
	
	@Before
	public void setup() throws ClassNotFoundException, SQLException{
		this.resultSet = new TreeSet<Instance>(new InstanceComparator());
		this.testInstance = new Instance(0,"Diego", "Actor Cantante", 100);
		this.knn = new KNN("pia_index", "knnTest");
	}

	@Test
	public void testKNN1() throws ClassNotFoundException, SQLException {
		List<Instance> result = this.knn.compute(1, this.testInstance);
		assertEquals("Pascal", result.get(0).getResource());
	}
	
	@Test
	public void testKNN2() throws ClassNotFoundException, SQLException{
		List<Instance> result = this.knn.compute(2,this.testInstance);
		assertEquals("Pascal", result.get(0).getResource());
		assertEquals("Torres", result.get(1).getResource());
	}
	
	@Test
	public void testKNN4() throws ClassNotFoundException, SQLException{
		List<Instance> result = this.knn.compute(4, this.testInstance);
		assertEquals("Pascal", result.get(0).getResource());
		assertEquals("Torres", result.get(1).getResource());
		assertEquals("Julian", result.get(2).getResource());
		assertEquals("Nacho", result.get(3).getResource());		
	}

}
