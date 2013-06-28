package pia;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class PathQueryInIndexImplTestCaseTest extends PathQueryTestCase {

	protected PathQueryInIndex pathQueryInIndex;
	
	@Before
	public void setUp() throws Exception{
		super.setUp();
		this.pathQueryInIndex = new PathQueryInIndexImpl(this.pathQueryString);
		
	}
	
	
	@Test
	public void testGetRanking(){
		
        assertEquals(Integer.MAX_VALUE, this.pathQueryInIndex.getRanking());
        pathQueryInIndex.setRanking(3);
        assertEquals(3,pathQueryInIndex.getRanking());
		
	}
	
	@Test
	public void testGetCoverage(){
        assertEquals(0,pathQueryInIndex.getCoverage());
        pathQueryInIndex.setCoverage(2);
        assertEquals(2,pathQueryInIndex.getCoverage());
	}
	
	@Test
	public void testIncrementCoverage(){
		int formerRanking = this.pathQueryInIndex.getRanking();
		this.pathQueryInIndex.incrementCoverage();
		assertEquals(1, this.pathQueryInIndex.getCoverage());
		assertEquals(formerRanking,this.pathQueryInIndex.getRanking());
		
		
	}

}
