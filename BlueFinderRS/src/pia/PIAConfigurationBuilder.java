package pia;

import normalization.INormalizator;
import normalization.TranslatorBasicNormalization;

public class PIAConfigurationBuilder {
  
	public static BipartiteGraphGenerator interlanguageWikipedia(int iterations) {		
		INormalizator translator = new TranslatorBasicNormalization("en", "Category:");
		BipartiteGraphGenerator bgg = new BipartiteGraphGenerator(translator, iterations);
		 
		return bgg;
	}
}
