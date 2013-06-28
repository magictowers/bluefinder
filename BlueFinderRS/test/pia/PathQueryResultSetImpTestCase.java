package pia;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class PathQueryResultSetImpTestCase {
	
	private PathQueryResultSetImpl resultSet;
	private Pair pair;
	private PathQuery pathQuery;

	@Before
	public void setUp() throws Exception {
		
		this.resultSet=new PathQueryResultSetImpl();
		this.pair = new Pair("source", "target");
		this.pathQuery = new PathQueryImpl("source", "target", "source/Cat:source/Cat:super source/target");
	}

	@Test
	public void test() {
		fail("Not yet implemented");
	}
	
	@Test
	public void addPathQueryTest() throws Exception{
		this.resultSet.addPathQuery(this.pair,this.pathQuery);
		PathQueryIndex result = this.resultSet.getIndex();
		assertEquals(this.pathQuery, result.getFirstRanked(1));
	}


}
