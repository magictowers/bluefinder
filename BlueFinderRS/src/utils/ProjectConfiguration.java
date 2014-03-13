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
    private static String currentSuffix = "";
    public static boolean useDefaultProperties = true;
    public static boolean useProperties1 = false;
    public static boolean useProperties2 = false;
    private static final String defaultPropertiesSource = "setup.properties";
    private static String currentPropertiesSource = defaultPropertiesSource;
    private static String lastPropertiesSource = "";
    
    public static void useProperties1() {
        useProperties1 = true;
        useProperties2 = false;
        useDefaultProperties = false;
    }
    
    public static void useProperties2() {
        useProperties1 = false;
        useProperties2 = true;
        useDefaultProperties = false;
    }
    
    public static void useDefaultProperties() {
        useProperties1 = false;
        useProperties2 = false;
        useDefaultProperties = true;
    }
        
    public static void setProperties(String prop1, String prop2) throws IOException {
        properties = new Properties();
        properties.load(WikipediaConnector.class.getClassLoader().getResourceAsStream(prop1));
        Properties properties2 = new Properties();
        properties2.load(WikipediaConnector.class.getClassLoader().getResourceAsStream(prop2));
        
        properties.putAll(properties2);
    }
    
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
    
    public static String getCurrentPropertiesSource() {
        return currentPropertiesSource;
    }
    
    public static void setCurrentPropertiesSource(String str) {
        lastPropertiesSource = currentPropertiesSource;
        currentPropertiesSource = str;
    }
    
    public static void setToDefaultProperties() {
        lastPropertiesSource = currentPropertiesSource;
        currentPropertiesSource = defaultPropertiesSource;
        useDefaultProperties();
    }
    
    public static void setLastPropertiesSource() {
        if (lastPropertiesSource.length() > 0)
            currentPropertiesSource = lastPropertiesSource;
    }
    
    private static boolean getBooleanValue(String key) {
        if (useProperties1)
            currentSuffix = "1";
        else if (useDefaultProperties)
            currentSuffix = "2";
        else 
            currentSuffix = "";
        key += currentSuffix;
        String property = (String) getProperties().get(key);
        return Boolean.parseBoolean(property);
    }
    
    private static String getStringValue(String key, String defaultValue) {
        String prop;
        if (useProperties1)
            currentSuffix = "1";
        else if (useProperties2)
            currentSuffix = "2";
        else 
            currentSuffix = "";
        key += currentSuffix;
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
    
    public static String dbpediaPrefix(String propertiesSource) {
        String str;
        if (testEnvironment())
            str = "http://dbpedia.org/resource/";
        else {
            setCurrentPropertiesSource(propertiesSource);
            str = getStringValue("DBPEDIA_PREFIX", "http://dbpedia.org/resource/");
        }
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
    
    public static boolean multipleDatabases() {
        boolean bool;
        if (testEnvironment())
            bool = false;
        else
            bool = getBooleanValue("MULTIPLE_DATABASES");
        return bool;
    }
    
    public static String fromToTable() {
        String str;
        if (testEnvironment())
            str = "fromto_table";
        else
            str = getStringValue("FROMTO_TABLE", "default_fromto_table");
        return str;        
    }
    
    public static String dbpediaLanguagePrefix() {
        String str;
        if (testEnvironment())
            str = "http://dbpedia.org/resource/";
        else
            str = getStringValue("DBPEDIA_LANGUAGE_PREFIX", "http://dbpedia.org/resource/");
        return str;
    }
    
    public static String resultDatabase() {
        String str;
        if (testEnvironment())
            str = "http://dbpedia.org/resource/";
        else {
            str = getStringValue("resultDatabase", "http://dbpedia.org/resource/");
        }
        return str;
    }
    
    public static String resultDatabase(String propertiesSource) {
        String str;
        if (testEnvironment())
            str = "http://dbpedia.org/resource/";
        else {
            setCurrentPropertiesSource(propertiesSource);
            str = getStringValue("resultDatabase", "http://dbpedia.org/resource/");
        }
        return str;
    }
    
    public static String resultDatabaseUser() {
        String str;
        if (testEnvironment())
            str = "http://dbpedia.org/resource/";
        else {
            str = getStringValue("resultDatabaseUser", "http://dbpedia.org/resource/");
        }
        return str;
    }
    
    public static String resultDatabaseUser(String propertiesSource) {
        String str;
        if (testEnvironment())
            str = "http://dbpedia.org/resource/";
        else {
            setCurrentPropertiesSource(propertiesSource);
            str = getStringValue("resultDatabaseUser", "http://dbpedia.org/resource/");
        }
        return str;
    }
    
    public static String resultDatabasePassword() {
        String str;
        if (testEnvironment())
            str = "http://dbpedia.org/resource/";
        else
            str = getStringValue("resultDatabasePass", "http://dbpedia.org/resource/");
        return str;
    }
    
    public static String resultDatabasePassword(String propertiesSource) {
        String str;
        if (testEnvironment())
            str = "http://dbpedia.org/resource/";
        else {
            setCurrentPropertiesSource(propertiesSource);
            str = getStringValue("resultDatabasePass", "http://dbpedia.org/resource/");
        }
        return str;
    }
    
    public static String language(String propertiesSource) {
        String str;
        if (testEnvironment())
            str = "http://dbpedia.org/resource/";
        else {
            setCurrentPropertiesSource(propertiesSource);
            str = getStringValue("LANGUAGE", "en");
        }
        return str;
    }
}
