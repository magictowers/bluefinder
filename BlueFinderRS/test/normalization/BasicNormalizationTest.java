package normalization;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class BasicNormalizationTest {
	
	private BasicNormalization normalizator;
	
	@Before
	public void setUp(){
		this.normalizator=new BasicNormalization();
	}

	@Test
	public void testNormalizeCategorySpecialCharacter() {
		String subCategoryName = "Category_?kubo_Toshimichi";
		String toCatName = "?kubo_Toshimichi";
		String fromCatName = "Kagoshima";
		
	
		String actual = this.normalizator.normalizeCategory(subCategoryName, fromCatName, toCatName);
		assertEquals("Category_#to", actual);
		
	}
	
	@Test
	public void testNormalizeCategoryRegularCharacter(){
		String subCategoryName = "People_from_Rosario,_Santa_Fe";
		String toCatName = "Lionel_Messi";
		String fromCatName = "Rosario,_Santa_Fe";
		
	
		String actual = this.normalizator.normalizeCategory(subCategoryName, fromCatName, toCatName);
		assertEquals("People_from_#from", actual);
		
	}

	

}
