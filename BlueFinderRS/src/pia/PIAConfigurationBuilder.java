package pia;

import normalization.BasicNormalization;
import normalization.INormalizator;
import normalization.TranslatorBasicNormalization;
import strategies.IGeneralization;
import strategies.LastCategoryGeneralization;

public class PIAConfigurationBuilder {
    
    private static PIAConfigurationBuilder INSTANCE;
    
    public PIAConfigurationBuilder() {}
  
	public static BipartiteGraphGenerator interlanguageWikipedia(int iterations, 
			String languageCode, String strCategory) {
		INormalizator translator = new TranslatorBasicNormalization(languageCode, strCategory);
		BipartiteGraphGenerator bgg = new BipartiteGraphGenerator(translator, iterations);
		 
		return bgg;
	}
    
    public static INormalizator getNormalizator() {
        return new BasicNormalization();
    }
    
    public static IGeneralization getGeneralizator() {
        return new LastCategoryGeneralization();
    }
    
    public static PIAConfigurationBuilder getInstance() {
        if (INSTANCE == null)
            INSTANCE = new PIAConfigurationBuilder();
        return INSTANCE;
    }
}
