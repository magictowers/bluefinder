package pia.deprecated;

import java.sql.SQLException;
import java.util.List;

import normalization.BasicNormalization;
import normalization.INormalizator;
import normalization.TranslatorBasicNormalization;
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
@Deprecated /** use ProjectSetup **/
public class PIAConfigurationContainer {

	private String pathStrategy; 
    private String categoryPrefix;
    private String languageCode;
    private String dbpediaTransPrefix;
    private String dbpediaPrefix;
    private String blacklistFilename;
    private boolean translate;
    private boolean createEnhancedTable;
    private ResultsDbInterface resultsDb;
    
/**
    public PIAConfigurationContainer(Boolean useTranslator,
			String pathGenerator, IGeneralization generalizator,
			String pathStrategy, String categoryPrefix, String languageCode,
			String dbpediaTransPrefix, String dbpediaPrefix,
			String blacklistFilename, boolean translate,
			boolean createEnhancedTable, boolean testEnvironment,
			ResultsDbInterface resultsDb) {
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
	}

	public PIAConfigurationContainer() {
        this.categoryPrefix = "Category:";
        this.languageCode = "en";
        this.dbpediaTransPrefix = "http://dbpedia.org/resource/";
        this.pathStrategy = "unstarred";
        this.dbpediaPrefix = "http://es.dbpedia.org/resource/";
        this.blacklistFilename = "blacklist_category_default.txt";
        this.translate = true;
        this.createEnhancedTable = false;
		this.resultsDb = resultsDb;
    }
    
    public BipartiteGraphGenerator getBipartiteGraphGenerator(List<String> blacklist, DBConnector connector, int iterations) throws SQLException, ClassNotFoundException, PropertiesFileIsNotFoundException {
        BipartiteGraphGenerator bgg;
        if (this.translate) {
            INormalizator normalizator = getNormalizator(connector);
            bgg = new BipartiteGraphGenerator(blacklist, connector, normalizator, iterations);
        } else
            bgg = new BipartiteGraphGenerator(blacklist, connector, iterations);
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
    **/
    
}
