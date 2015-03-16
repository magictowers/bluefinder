
package utils;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import normalization.BasicNormalization;
import normalization.INormalizator;
import normalization.TranslatorBasicNormalization;
import pia.BipartiteGraphGenerator;
import db.DBConnector;
import db.PropertiesFileIsNotFoundException;
import db.utils.ResultsDbInterface;
import strategies.IGeneralization;
import strategies.LastCategoryGeneralization;
import strategies.UnstarredPathGeneralization;

/**
 *
 * @author mkaminose & mdurante
 */
public class ProjectSetup {

	private String pathStrategy; 
    private String categoryPrefix;
    private String languageCode;
    private String dbpediaTransPrefix;
    private String dbpediaPrefix;
    private String dbpediaTypeTable;
    private String blacklistFilename;
    private boolean translate;
    private boolean createEnhancedTable;
    private ResultsDbInterface resultsDb;
    
    private List<String> blacklist = new ArrayList<String>();
    
    //TODO assign a fromToTable default value
    private String fromToTable;
    
	private String wikiUser;
	private String wikiPass;
	private String wikiDatabase;
	private String resultsUser;
	private String resultsPass;
	private String resultsDatabase;
	
	
	public ProjectSetup() {
        this.categoryPrefix = "Category:";
        this.dbpediaTypeTable = "dbtypes";
        this.languageCode = "en";
        this.dbpediaTransPrefix = "http://dbpedia.org/resource/";
        this.pathStrategy = "unstarred";
        this.dbpediaPrefix = "http://es.dbpedia.org/resource/";
        this.blacklistFilename = "blacklist_category_default.txt";
        this.translate = true;
        this.createEnhancedTable = false;
    }

    public ProjectSetup(String dbpediaTypeTable, String pathStrategy, 
    		String categoryPrefix, String languageCode,
			String dbpediaTransPrefix, String dbpediaPrefix,
			String blacklistFilename, boolean translate,
			boolean createEnhancedTable, ResultsDbInterface resultsDb) throws IOException {
		super();
		this.pathStrategy = pathStrategy;
		this.categoryPrefix = categoryPrefix;
		this.languageCode = languageCode;
		this.dbpediaTransPrefix = dbpediaTransPrefix;
		this.dbpediaPrefix = dbpediaPrefix;
		this.blacklistFilename = blacklistFilename;
		this.translate = translate;
		this.createEnhancedTable = createEnhancedTable;
		this.resultsDb = resultsDb;
		this.dbpediaTypeTable = dbpediaTypeTable;
		this.blacklist = new BlacklistCategory(blacklistFilename).getBlacklist();
	}

    public ProjectSetup(String dbpediaTypeTable, String pathStrategy, 
    		String categoryPrefix, String languageCode,
			String dbpediaTransPrefix, String dbpediaPrefix,
			List<String> blacklist, boolean translate,
			boolean createEnhancedTable, ResultsDbInterface resultsDb) {
		super();
		this.pathStrategy = pathStrategy;
		this.categoryPrefix = categoryPrefix;
		this.languageCode = languageCode;
		this.dbpediaTransPrefix = dbpediaTransPrefix;
		this.dbpediaPrefix = dbpediaPrefix;
		this.blacklistFilename = null;
		this.blacklist = blacklist;
		this.translate = translate;
		this.createEnhancedTable = createEnhancedTable;
		this.resultsDb = resultsDb;
		this.dbpediaTypeTable = dbpediaTypeTable;
	}
    
    public BipartiteGraphGenerator getBipartiteGraphGenerator(DBConnector connector, int iterations) throws SQLException, ClassNotFoundException, PropertiesFileIsNotFoundException {
        BipartiteGraphGenerator bgg;
        if (this.translate) {
            INormalizator normalizator = getNormalizator(connector);
            bgg = new BipartiteGraphGenerator(this, connector, normalizator, iterations);
        } else
            bgg = new BipartiteGraphGenerator(this, connector, iterations);
        return bgg;
    }
    
    public INormalizator getNormalizator(DBConnector connector) {
        INormalizator normalizator;
        if (this.translate)
            normalizator = new TranslatorBasicNormalization(connector,this.languageCode,this.categoryPrefix);
        else
            normalizator = new BasicNormalization();
        return normalizator;
    }
    
    public IGeneralization getGeneralizator() {
        return getGeneralizatorWithPath(this.getPathStrategy());
    }
    
    public IGeneralization getGeneralizatorWithPath(String pathGenerator) {
        if (pathGenerator.equalsIgnoreCase("star") || pathGenerator.equalsIgnoreCase("starred")) {
            return new LastCategoryGeneralization();
        } else if (pathGenerator.equalsIgnoreCase("unstar") || pathGenerator.equalsIgnoreCase("unstarred")) {
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

    /**
     * @return the dbpediaTypeTable name
     */
    public String getDbpediaTypeTable() {
        return dbpediaTypeTable;
    }

    /**
     * @param dbpediaTypeTable the dbpediaTypeTable to set
     */
    public void setDbpediaTypeTable(String dbpediaTypeTable) {
        this.dbpediaTypeTable = dbpediaTypeTable;
    }

	/**
	 * @return the wikiUser
	 */
	public String getWikiUser() {
		return wikiUser;
	}

	/**
	 * @param wikiUser the wikiUser to set
	 */
	public void setWikiUser(String wikiUser) {
		this.wikiUser = wikiUser;
	}

	/**
	 * @return the wikiPass
	 */
	public String getWikiPass() {
		return wikiPass;
	}

	/**
	 * @param wikiPass the wikiPass to set
	 */
	public void setWikiPass(String wikiPass) {
		this.wikiPass = wikiPass;
	}

	/**
	 * @return the wikiDatabase
	 */
	public String getWikiDatabase() {
		return wikiDatabase;
	}

	/**
	 * @param wikiDatabase the wikiDatabase to set
	 */
	public void setWikiDatabase(String wikiDatabase) {
		this.wikiDatabase = wikiDatabase;
	}

	/**
	 * @return the resultsUser
	 */
	public String getResultsUser() {
		return resultsUser;
	}

	/**
	 * @param resultsUser the resultsUser to set
	 */
	public void setResultsUser(String resultsUser) {
		this.resultsUser = resultsUser;
	}

	/**
	 * @return the resultsPass
	 */
	public String getResultsPass() {
		return resultsPass;
	}

	/**
	 * @param resultsPass the resultsPass to set
	 */
	public void setResultsPass(String resultsPass) {
		this.resultsPass = resultsPass;
	}

	/**
	 * @return the resultsDatabase
	 */
	public String getResultsDatabase() {
		return resultsDatabase;
	}

	/**
	 * @param resultsDatabase the resultsDatabase to set
	 */
	public void setResultsDatabase(String resultsDatabase) {
		this.resultsDatabase = resultsDatabase;
	}

	/**
	 * @return the translate
	 */
	public boolean isTranslate() {
		return translate;
	}

	/**
	 * @return the createEnhancedTable
	 */
	public boolean isCreateEnhancedTable() {
		return createEnhancedTable;
	}

	/**
	 * @return the fromToTable
	 */
	public String getFromToTable() {
		return fromToTable;
	}

	/**
	 * @param fromToTable the fromToTable to set
	 */
	public void setFromToTable(String fromToTable) {
		this.fromToTable = fromToTable;
	}
    
	
	/**
	 * @return the blacklist
	 */
	public List<String> getBlacklist() {
		return blacklist;
	}

	/**
	 * @param blacklist the blacklist to set
	 */
	public void setBlacklist(List<String> blacklist) {
		this.blacklist = blacklist;
	}

	// TODO REFACTOR THIS
	public String getResultDatabase1(){
    	return "localhost/local_p06_associatedBand_fr";
    }
    public String getFromToTable1(){
    	return "p06_associatedBand_fr";
    }
    public String getDbpediaPrefix1(){
    	return "http://fr.dbpedia.org/resource/";
    }

    public String getResultDatabase2(){
    	return "localhost/local_p06_associatedBand_es";
    }
    public String getFromToTable2(){
    	return "p06_associatedBand_es";
    }
    public String getDbpediaPrefix2(){
    	return "http://es.dbpedia.org/resource/";
    }
    
}