package pia;

import static org.junit.Assert.*;

import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import db.WikipediaConnector;

public class BipartiteGraphGeneratorTestCase {

	private BipartiteGraphGenerator pathIndex;
	
	@Before
	public void setUp() throws Exception {
		WikipediaConnector.restoreTestDatabase();
		this.pathIndex= new BipartiteGraphGenerator();
	}

	@Test
	public void testPathIndex1Hop() throws UnsupportedEncodingException, SQLException, ClassNotFoundException {
		this.pathIndex = new BipartiteGraphGenerator(1);
		this.pathIndex.generateBiGraph("Rosario", "Lionel_Messi");
		PathIndex pathIndex = this.pathIndex.getPathIndex();
		List<String> path_queries = pathIndex.getPathQueries("Rosario,_Santa_Fe", "Lionel_Messi");
		assertEquals(1, path_queries.size());
		assertEquals("#from / #to", path_queries.get(0));
	}
	
	

}
