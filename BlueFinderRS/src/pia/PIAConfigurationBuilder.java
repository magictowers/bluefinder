package pia;

import normalization.INormalizator;
import normalization.TranslatorBasicNormalization;

public class PIAConfigurationBuilder {
  
	public static BipartiteGraphGenerator interlanguageWikipedia(int iterations, 
			String languageCode, String strCategory) {
		INormalizator translator = new TranslatorBasicNormalization(languageCode, strCategory);
		BipartiteGraphGenerator bgg = new BipartiteGraphGenerator(translator, iterations);
		 
		return bgg;
	}
}
