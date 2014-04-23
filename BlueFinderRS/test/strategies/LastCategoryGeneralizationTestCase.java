package strategies;

import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class LastCategoryGeneralizationTestCase {
	
	private LastCategoryGeneralization gen;

	@Before
	public void setUp() throws Exception {
		this.gen=new LastCategoryGeneralization();
	}

	@Test
	public void test() {
		String fromTo = "#from / #to";
		String peopleFromFrom = "#from / Cat:#from / Cat:People_from_#from / #to";
		
		assertEquals(fromTo, this.gen.generalizePathQuery(fromTo));
		assertEquals("#from / * / Cat:People_from_#from / #to", this.gen.generalizePathQuery(peopleFromFrom));
		
	}
    
	@Test
	public void testGeneralizePathQueryList() {
        List<String> pathList = new ArrayList<String>();
        pathList.add("#from");
        pathList.add("#to");
        String fromTo = "#from / #to";
		assertEquals(fromTo, this.gen.generalizePathQuery(pathList));
        
		String peopleFromFrom = "#from / Cat:#from / Cat:People_from_#from / #to";		
        pathList = new ArrayList<String>();
        pathList.add("#from");
        pathList.add("Cat:#from");
        pathList.add("Cat:People_from_#from");
        pathList.add("#to");
		assertEquals("#from / * / Cat:People_from_#from / #to", this.gen.generalizePathQuery(pathList));
		
	}

}
