package knn;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import db.WikipediaConnector;

public class KnnTestCompleteTestCAse {
	
	private KNNTestComplete knnTest;
	private List<String> actualPaths;
	private List<String> comparedPaths;
	
	@BeforeClass
	public static void setupclass() throws Exception {
		   Assume.assumeTrue(WikipediaConnector.isTestEnvironment());

	}
	
	@Before
	public void setUp() throws Exception {
		this.knnTest=new KNNTestComplete(new KNN("dbresearch", "peo2ZippedTypes"));
		actualPaths = new ArrayList<String>();
		this.actualPaths.add("[to]");
		this.actualPaths.add("*/People_from_[from]/[to]");
		this.actualPaths.add("*/XXXXX/[to]");
		
		this.comparedPaths= new ArrayList<String>();
		this.comparedPaths.add("[to]");
		this.comparedPaths.add("*/People_from_[from]/[to]");
		this.comparedPaths.add("Portail:Europe/Articles_li�s/[to]");
		
		
		
	}

	@Test
	public void testDecodePathQuery() {
		String encodedPath = "{*/Musicians_from_[from]/[to]=8, [to]=1, */Musicians_from_[from],_California/[to]=1}";
		List<String> decodedPaths = this.knnTest.decodePathsFromRow(encodedPath);
		assertTrue(decodedPaths.contains("*/Musicians_from_[from]/[to]"));
		assertTrue(decodedPaths.contains("[to]"));
		assertTrue(decodedPaths.contains("*/Musicians_from_[from],_California/[to]"));
		assertEquals(3,decodedPaths.size());
		
		encodedPath="{[to]=1}";
		decodedPaths = this.knnTest.decodePathsFromRow(encodedPath);
		assertTrue(decodedPaths.contains("[to]"));
		assertEquals(1,decodedPaths.size());
		
	}
	
	@Test
	public void testCountCorrectPaths(){
		int result = this.knnTest.countCorrectPaths(this.actualPaths, this.comparedPaths);
		assertSame(2, result);
		
		
		
	}

}
