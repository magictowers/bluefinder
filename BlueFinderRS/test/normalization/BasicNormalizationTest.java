package normalization;

import static org.junit.Assert.assertEquals;

import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import db.WikipediaConnector;

public class BasicNormalizationTest {
	
	private BasicNormalization normalizator;
	
	
	@BeforeClass
	public static void setupclass() throws Exception {
		   Assume.assumeTrue(WikipediaConnector.isTestEnvironment());

	}
	
	@Before
	public void setUp(){
		this.normalizator=new BasicNormalization();
	}

	@Test
	public void testNormalizeCategorySpecialCharacter() throws Exception {
		String subCategoryName = "Category_?kubo_Toshimichi";
		String toCatName = "?kubo_Toshimichi";
		String fromCatName = "Kagoshima";
		
	
		String actual = this.normalizator.normalizeCategory(subCategoryName, fromCatName, toCatName);
		assertEquals("Category_#to", actual);
		
	}
	
	@Test
	public void testNormalizeCategoryRegularCharacter() throws Exception {
		String subCategoryName = "People_from_Rosario,_Santa_Fe";
		String toCatName = "Lionel_Messi";
		String fromCatName = "Rosario,_Santa_Fe";
		
	
		String actual = this.normalizator.normalizeCategory(subCategoryName, fromCatName, toCatName);
		assertEquals("People_from_#from", actual);
		
	}

	

}
