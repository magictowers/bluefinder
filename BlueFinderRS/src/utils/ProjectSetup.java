
package utils;

/**
 *
 * @author mkaminose
 */
public class ProjectSetup {

    private String categoryPrefix;
    private String languageCode;
    private String dbpediaTransPrefix;
    private String pathStrategy;
    private String dbpediaPrefix;
    private String blacklistFilename;
    private boolean translate;
    private boolean createEnhancedTable;
    
    public ProjectSetup() {}
    
    public ProjectSetup(boolean defaultConfiguration) {
        this();
        if (defaultConfiguration) {
            categoryPrefix = "Category:";
            languageCode = "en";
            dbpediaTransPrefix = "http://dbpedia.org/resource/";
            pathStrategy = "unstarred";
            dbpediaPrefix = "http://es.dbpedia.org/resource/";
            blacklistFilename = "blacklist_category_default.txt";
            translate = true;
            createEnhancedTable = false;
        }        
    }
    
    public ProjectSetup(String categoryPrefix, String languageCode, String dbpediaTransPrefix, String pathStrategy,
            String dbpediaPrefix, String blacklistFilename, boolean translate, boolean createEnhancedTable) {
        this.categoryPrefix = categoryPrefix;
        this.languageCode = languageCode;
        this.dbpediaTransPrefix = dbpediaTransPrefix;
        this.pathStrategy = pathStrategy;
        this.dbpediaPrefix = dbpediaPrefix;
        this.blacklistFilename = blacklistFilename;
        this.translate = translate;
        this.createEnhancedTable = createEnhancedTable;
    }
    
    private boolean isInitialized(String str) {
        return (str != null && str.length() > 0);
    }

    /**
     * @return the categoryPrefix
     */
    public String getCategoryPrefix() {
        if (!isInitialized(categoryPrefix)) {
            setCategoryPrefix(ProjectConfigurationReader.categoryPrefix());
        }
        return categoryPrefix;
    }

    /**
     * @param categoryPrefix the categoryPrefix to set
     */
    public void setCategoryPrefix(String categoryPrefix) {
        this.categoryPrefix = categoryPrefix;
    }

    /**
     * @return the languageCode
     */
    public String getLanguageCode() {
        if (!isInitialized(languageCode)) {
            setLanguageCode(ProjectConfigurationReader.languageCode());
        }
        return languageCode;
    }

    /**
     * @param languageCode the languageCode to set
     */
    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    /**
     * @return the dbpediaTransPrefix
     */
    public String getDbpediaTransPrefix() {
        if (!isInitialized(dbpediaTransPrefix)) {
            setDbpediaTransPrefix(ProjectConfigurationReader.dbpediaLanguagePrefix());
        }
        return dbpediaTransPrefix;
    }

    /**
     * @param dbpediaTransPrefix the dbpediaTransPrefix to set
     */
    public void setDbpediaTransPrefix(String dbpediaTransPrefix) {
        this.dbpediaTransPrefix = dbpediaTransPrefix;
    }

    /**
     * @return the pathStrategy
     */
    public String getPathStrategy() {
        return pathStrategy;
    }

    /**
     * @param pathStrategy the pathStrategy to set
     */
    public void setPathStrategy(String pathStrategy) {
        this.pathStrategy = pathStrategy;
    }

    /**
     * @return the dbpediaPrefix
     */
    public String getDbpediaPrefix() {
        if (!isInitialized(dbpediaPrefix)) {
            setDbpediaPrefix(ProjectConfigurationReader.dbpediaPrefix());
        }
        return dbpediaPrefix;
    }

    /**
     * @param dbpediaPrefix the dbpediaPrefix to set
     */
    public void setDbpediaPrefix(String dbpediaPrefix) {
        this.dbpediaPrefix = dbpediaPrefix;
    }

    /**
     * @return the blacklistFilename
     */
    public String getBlacklistFilename() {
        if (!isInitialized(blacklistFilename)) {
            setBlacklistFilename(ProjectConfigurationReader.blacklistFilename());
        }
        return blacklistFilename;
    }

    /**
     * @param blacklistFilename the blacklistFilename to set
     */
    public void setBlacklistFilename(String blacklistFilename) {
        this.blacklistFilename = blacklistFilename;
    }

    /**
     * @return the translate
     */
    public boolean hasToTranslate() {
        return translate;
    }

    /**
     * @param translate the translate to set
     */
    public void setTranslate(boolean translate) {
        this.translate = translate;
    }

    /**
     * @return the createEnhancedTable
     */
    public boolean hasToCreateEnhancedTable() {
        return createEnhancedTable;
    }

    /**
     * @param createEnhancedTable the createEnhancedTable to set
     */
    public void setCreateEnhancedTable(boolean createEnhancedTable) {
        this.createEnhancedTable = createEnhancedTable;
    }
    
}
