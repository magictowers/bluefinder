package pia;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import normalization.BasicNormalization;
import normalization.INormalizator;
import normalization.TranslatorBasicNormalization;
import strategies.IGeneralization;
import strategies.LastCategoryGeneralization;
import strategies.UnstarredPathGeneralization;
import utils.ProjectConfigurationReader;

public class PIAConfigurationBuilder {
    
    private static PIAConfigurationBuilder INSTANCE;
    private static Map<String, String> properties;    
    private static IGeneralization generalizator;
    
    public PIAConfigurationBuilder() {
        properties = new HashMap<String, String>();
        
        String isTestEnvironment = String.valueOf(ProjectConfigurationReader.testEnvironment());
        String useTranslator = String.valueOf(ProjectConfigurationReader.translate());
        String languageCode = ProjectConfigurationReader.languageCode();
        String categoryPrefix = ProjectConfigurationReader.categoryPrefix();
        
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
    
    public static BipartiteGraphGenerator getBipartiteGraphGenerator(int iterations) throws SQLException, ClassNotFoundException {
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
        
    public static void unsetGeneralizator() {
        generalizator = null;
    }
        
    public static void setGeneralizator(IGeneralization paramGeneralizator) {
        generalizator = paramGeneralizator;
    }
    
    public static void setGeneralizator(String pathGeneralizator) {
        if (pathGeneralizator.equalsIgnoreCase("star") || pathGeneralizator.equalsIgnoreCase("starred")) {
            generalizator = new LastCategoryGeneralization();
        } else if (pathGeneralizator.equalsIgnoreCase("unstar") || pathGeneralizator.equalsIgnoreCase("unstarred")) {
            generalizator = new UnstarredPathGeneralization();
        }
    }
    
    public static IGeneralization getGeneralizator() {
        getInstance();
        return getGeneralizator(ProjectConfigurationReader.pathGenerator());
    }
    
    public static IGeneralization getGeneralizator(String pathGenerator) {
        if (generalizator != null)
            return generalizator;        
        
        if (pathGenerator.equalsIgnoreCase("star") || pathGenerator.equalsIgnoreCase("starred")) {
            return new LastCategoryGeneralization();
        } else if (pathGenerator.equalsIgnoreCase("unstar") || pathGenerator.equalsIgnoreCase("unstarred")) {
            return new UnstarredPathGeneralization();
        }
        // if none of the strategies are stated
        if (ProjectConfigurationReader.useStarpath()) {
            return new LastCategoryGeneralization();
        } else {
            return new UnstarredPathGeneralization();
        }
    }
    
    public static PIAConfigurationBuilder getInstance() {
        if (INSTANCE == null)
            INSTANCE = new PIAConfigurationBuilder();
        return INSTANCE;
    }
}
