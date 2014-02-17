package utils;

import db.WikipediaConnector;
import java.io.IOException;
import java.util.Properties;

/**
 *
 * @author mkaminose
 */
public class ProjectConfiguration {

    private static Properties properties;
    
    private static Properties getProperties(){
    	if (properties == null) {
            properties = new Properties();
            try {
                properties.load(WikipediaConnector.class.getClassLoader().getResourceAsStream("setup.properties"));
            } catch (IOException e) {
                System.err.println("setup.properties not found. Aborting...");
                System.exit(255);
            }
        }
    	return properties;
    }
    
    private static boolean getBooleanValue(String key) {
        String property = (String) getProperties().get(key);
        return Boolean.parseBoolean(property);
    }
    
    private static String getStringValue(String key, String defaultValue) {
        String prop;
        try {
            prop = (String)getProperties().get(key);
        } catch (NullPointerException ex) {
            prop = defaultValue;
        }
        if (prop == null) {
            prop = defaultValue;
        }
        return prop;
    }
    
    public static boolean enhanceTable() {
        return getBooleanValue("CREATE_ENHANCED_TABLE");
    }
    
    public static boolean translate() {
        return getBooleanValue("TRANSLATE");
    }
    
    public static boolean testEnvironment() {
        return getBooleanValue("testEnvironment");
    }
    
    public static String dbpediaPrefix() {
        return getStringValue("DBPEDIA_PREFIX", "http://es.dbpedia.org/resource/");
    }
    
    public static String blacklistFilename() {
        return getStringValue("BLACKLIST_FILENAME", "blacklist_category.txt");
    }
    
    public static String dbpediaTypeTable() {
        return getStringValue("DBPEDIA_TYPE_TABLE", "dbtypes");
    }
    
    public static String languageCode() {
        return getStringValue("LANGUAGE_CODE", "en");
    }
    
    public static String categoryPrefix() {
        return getStringValue("CATEGORY_PREFIX", "Category:");
    }
}
