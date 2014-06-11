
package utils;

import db.utils.ResultsDbInterface;
import strategies.IGeneralization;
import strategies.LastCategoryGeneralization;
import strategies.UnstarredPathGeneralization;

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
    private boolean testEnvironment;
    private ResultsDbInterface resultsDb;
    
    public ProjectSetup() {
        testEnvironment = false;
        categoryPrefix = "Category:";
        languageCode = "en";
        dbpediaTransPrefix = "http://dbpedia.org/resource/";
        pathStrategy = "unstarred";
        dbpediaPrefix = "http://es.dbpedia.org/resource/";
        blacklistFilename = "blacklist_category_default.txt";
        translate = true;
        createEnhancedTable = false;
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
        
    public IGeneralization getGeneralizator() {
        if (getPathStrategy().equalsIgnoreCase("star") || getPathStrategy().equalsIgnoreCase("starred")) {
            return new LastCategoryGeneralization();
        } else if (getPathStrategy().equalsIgnoreCase("unstar") || getPathStrategy().equalsIgnoreCase("unstarred")) {
            return new UnstarredPathGeneralization();
        } else {
            return new UnstarredPathGeneralization();
        }
    }
    
    /**
     * Category prefix used for translation.
     * Depends on the Wikipedia database language.
     * Ex: if wiki@en then "Category:"
     * @return string
     */
    public String getCategoryPrefix() {
        return categoryPrefix;
    }

    /**
     * Category prefix used for translation.
     * Depends on the Wikipedia database language.
     * Ex: if wiki@en then "Category:"
     * @param categoryPrefix the categoryPrefix to set
     */
    public void setCategoryPrefix(String categoryPrefix) {
        this.categoryPrefix = categoryPrefix;
    }

    /**
     * Language code used for langlinks.
     * Ex: if working on wiki@es, to be translated to English, then "en"
     * @return the languageCode
     */
    public String getLanguageCode() {
        return languageCode;
    }

    /**
     * Language code used for langlinks.
     * Ex: if working on wiki@es, to be translated to English, then "en"
     * @param languageCode the languageCode to set
     */
    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    /**
     * Prefix for the translated column in fromto table.
     * Ex: if is to be translated to English, then "http://dbpedia.org/resource/"
     * @return the dbpediaTransPrefix
     */
    public String getDbpediaTransPrefix() {
        return dbpediaTransPrefix;
    }

    /**
     * Prefix for the translated column in fromto table.
     * Ex: if is to be translated to English, then "http://dbpedia.org/resource/"
     * @param dbpediaTransPrefix the dbpediaTransPrefix to set
     */
    public void setDbpediaTransPrefix(String dbpediaTransPrefix) {
        this.dbpediaTransPrefix = dbpediaTransPrefix;
    }

    /**
     * Values can be: "starred", "star", "unstarred", or "unstar"
     * @return the pathStrategy
     */
    public String getPathStrategy() {
        return pathStrategy;
    }

    /**
     * Values can be: "starred", "star", "unstarred", or "unstar"
     * @param pathStrategy the pathStrategy to set
     */
    public void setPathStrategy(String pathStrategy) {
        this.pathStrategy = pathStrategy;
    }

    /**
     * @return the dbpediaPrefix
     */
    public String getDbpediaPrefix() {
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

    /**
     * @return the testEnvironment
     */
    public boolean isTestEnvironment() {
        return testEnvironment;
    }

    /**
     * @param testEnvironment the testEnvironment to set
     */
    public void setTestEnvironment(boolean testEnvironment) {
        this.testEnvironment = testEnvironment;
    }

    /**
     * @return the resultsDb
     */
    public ResultsDbInterface getResultsDb() {
        return resultsDb;
    }

    /**
     * @param resultsDb the resultsDb to set
     */
    public void setResultsDb(ResultsDbInterface resultsDb) {
        this.resultsDb = resultsDb;
    }
    
}
