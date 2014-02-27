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
        boolean bool;
        if (testEnvironment())
            bool = true;
        else
            bool = getBooleanValue("CREATE_ENHANCED_TABLE");
        return bool;
    }
    
    public static boolean translate() {
        boolean bool;
        if (testEnvironment())
            bool = false;
        else
            bool = getBooleanValue("TRANSLATE");
        return bool;
    }
    
    public static boolean testEnvironment() {
        return getBooleanValue("testEnvironment");
    }
    
    public static String dbpediaPrefix() {
        String str;
        if (testEnvironment())
            str = "http://dbpedia.org/resource/";
        else
            str = getStringValue("DBPEDIA_PREFIX", "http://dbpedia.org/resource/");
        return str;
    }
    
    public static String blacklistFilename() {
        String str;
        if (testEnvironment())
            str = blacklistFilenameDefault();
        else
            str = getStringValue("BLACKLIST_FILENAME", "blacklist_category_default.txt");
        return str;
    }
    
    public static String blacklistFilenameDefault() {
        return "blacklist_category_default.txt";
    }
    
    public static String dbpediaTypeTable() {
        String str;
        if (testEnvironment())
            str = "dbtypes";
        else
            str = getStringValue("DBPEDIA_TYPE_TABLE", "dbtypes");
        return str;
    }
    
    public static String languageCode() {
        String str;
        if (testEnvironment())
            str = "en";
        else
            str = getStringValue("LANGUAGE_CODE", "en");
        return str;
    }
    
    public static String categoryPrefix() {
        String str;
        if (testEnvironment())
            str = "Category:";
        else
            str = getStringValue("CATEGORY_PREFIX", "Category:");
        return str;
    }
}
