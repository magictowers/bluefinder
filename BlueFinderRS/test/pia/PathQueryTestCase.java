package pia;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class PathQueryTestCase {
	
	protected String target;
	protected String source ;
	protected String pathQueryString;
	protected String normalizedPathQueryString;
	protected PathQuery pathQuery;
	protected String wpPrefix = "http://en.wikipedia.org/wiki/";

	@Before
	public void setUp() throws Exception {
		this.target = "Jhon_Doe";
		this.source = "Nantes";
		this.pathQueryString = "Nantes/Category:Nantes/Category:People_from_Nantes/Jhon_Doe";
		this.normalizedPathQueryString = "#from/Category:#from/Category:People_from_#from/#to";
		this.pathQuery = new PathQueryImpl(this.source,this.target,this.pathQueryString);
	}

	@Test
	public void testGetSource(){
		assertEquals(this.source, this.pathQuery.getSource());
	}
	
	@Test
	public void testGetTarget(){
		assertEquals(this.target,this.pathQuery.getTarget());
	}
	
	@Test
	public void testNormalize(){
		assertEquals(this.normalizedPathQueryString, this.pathQuery.normalize());
	}
	
	@Test
	public void testActual(){
		assertEquals(this.pathQueryString, this.pathQuery.actual());
	}
	
	@Test
	public void getLongSource(){
		assertEquals(this.wpPrefix+this.source,this.pathQuery.getLongSource());
	}
	@Test
	public void getLongTarget(){
		assertEquals(this.wpPrefix+this.target, this.pathQuery.getLongTarget());
	}
	
	

}
