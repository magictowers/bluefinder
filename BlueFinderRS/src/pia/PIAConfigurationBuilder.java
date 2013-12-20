package pia;

import db.WikipediaConnector;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import normalization.BasicNormalization;
import normalization.INormalizator;
import normalization.TranslatorBasicNormalization;
import strategies.IGeneralization;
import strategies.LastCategoryGeneralization;

public class PIAConfigurationBuilder {
    
    private static PIAConfigurationBuilder INSTANCE;
    private static Map<String, String> properties;    
    
    public PIAConfigurationBuilder() {
        properties = new HashMap<String, String>();
        Properties prop = new Properties();
        try {
			prop.load(WikipediaConnector.class.getClassLoader().getResourceAsStream("setup.properties"));
		} catch (IOException e) {
			System.err.println("The configuration file could not be read. Aborting.");
			System.exit(255);
		}
        
        String isTestEnvironment;
        String useTranslator;
        String languageCode;
        String categoryPrefix;
        try {
            useTranslator = prop.getProperty("TRANSLATE");
        } catch (NullPointerException ex) {
            useTranslator = "false";
        }
        try {
            languageCode = prop.getProperty("LANGUAGE_CODE");
        } catch (NullPointerException ex) {
            languageCode = "en";
        }
        try {
            categoryPrefix = prop.getProperty("CATEGORY_PREFIX");
        } catch (NullPointerException ex) {
            categoryPrefix = "Category:";
        }
        try {
            isTestEnvironment = prop.getProperty("testEnvironment");
        } catch (NullPointerException ex) {
            isTestEnvironment = "false";
        }
        properties.put("languageCode", languageCode);
        properties.put("useTranslator", useTranslator);
        properties.put("categoryPrefix", categoryPrefix);
        properties.put("isTestEnvironment", isTestEnvironment);
    }
  
//	public static BipartiteGraphGenerator interlanguageWikipedia(int iterations, 
//			String languageCode, String strCategory) {
//		INormalizator translator = new TranslatorBasicNormalization(languageCode, strCategory);
//		BipartiteGraphGenerator bgg = new BipartiteGraphGenerator(translator, iterations);
//		 
//		return bgg;
//	}
    
    public static BipartiteGraphGenerator getBipartiteGraphGenerator(int iterations) {
        getInstance();
        BipartiteGraphGenerator bgg;
        boolean useTranslator = Boolean.valueOf(properties.get("useTranslator"));
        boolean isTestEnvironment = Boolean.valueOf(properties.get("isTestEnvironment"));
        if (useTranslator && !isTestEnvironment) {
            INormalizator normalizator = getNormalizator();
            bgg = new BipartiteGraphGenerator(normalizator, iterations);
        } else
            bgg = new BipartiteGraphGenerator(iterations);
        return bgg;
    }
    
    public static INormalizator getNormalizator() {
        getInstance();
        INormalizator normalizator;
        boolean useTranslator = Boolean.valueOf(properties.get("useTranslator"));
        boolean isTestEnvironment = Boolean.valueOf(properties.get("isTestEnvironment"));
        if (useTranslator && !isTestEnvironment)
            normalizator = new TranslatorBasicNormalization(properties.get("languageCode"), properties.get("categoryPrefix"));
        else
            normalizator = new BasicNormalization();
        return normalizator;
    }
    
    public static IGeneralization getGeneralizator() {
        getInstance();
        return new LastCategoryGeneralization();
    }

    public static PIAConfigurationBuilder getInstance() {
        if (INSTANCE == null)
            INSTANCE = new PIAConfigurationBuilder();
        return INSTANCE;
    }
}
