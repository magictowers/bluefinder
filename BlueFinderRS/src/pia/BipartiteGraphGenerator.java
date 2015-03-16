/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pia;

import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import utils.FromToPair;
import utils.PathsResolver;
import utils.ProjectSetup;
import normalization.BasicNormalization;
import normalization.INormalizator;
import db.DBConnector;
import db.PropertiesFileIsNotFoundException;
import db.WikipediaConnector;
import db.utils.ResultsDbInterface;
import strategies.IGeneralization;
import strategies.UnstarredPathGeneralization;

/**
 *
 * @author dtorres
 */
public class BipartiteGraphGenerator implements PathIndex{

    private PathFinder finder;
    private List<String> pathsNotFound;
    private ResultsDbInterface resultsDb;
    private IGeneralization generalizator;
    private DBConnector connector;
    private ProjectSetup projectSetup;
    private List<String> blacklist;

    /**
     * Create a BipartiteGraphGenerator with the UnstarredPathGeneralization().
     * @param connector
     * @throws SQLException
     * @throws ClassNotFoundException
     * @throws PropertiesFileIsNotFoundException
     */
    public BipartiteGraphGenerator(ProjectSetup projectSetup, DBConnector connector) throws SQLException, ClassNotFoundException, PropertiesFileIsNotFoundException {
        this.finder = new PathFinder(projectSetup, connector);
        this.blacklist = projectSetup.getBlacklist();
        this.resultsDb = new ResultsDbInterface(projectSetup, connector);
        this.pathsNotFound = new ArrayList<String>();
        this.generalizator =  new UnstarredPathGeneralization();
        this.connector = connector;
    }
    
    public BipartiteGraphGenerator(ProjectSetup projectSetup, DBConnector connector, IGeneralization generalization) throws SQLException, ClassNotFoundException, PropertiesFileIsNotFoundException {
        this(projectSetup,connector);
        this.generalizator = generalization;
    }
    
    public BipartiteGraphGenerator(ProjectSetup projectSetup, DBConnector connector, IGeneralization generalization, int categoryIterations) throws SQLException, ClassNotFoundException, PropertiesFileIsNotFoundException {
        this(projectSetup,connector);
        this.generalizator = generalization;
        this.finder.setCategoryPathIterations(categoryIterations);
    }

    public BipartiteGraphGenerator(ProjectSetup projectSetup, DBConnector connector, int categoryIterations) throws SQLException, ClassNotFoundException, PropertiesFileIsNotFoundException {
        this(projectSetup,connector);
        this.finder.setCategoryPathIterations(categoryIterations);
    }
    
    public BipartiteGraphGenerator(ProjectSetup projectSetup, DBConnector connector, INormalizator normalizator, int iterations) throws SQLException, ClassNotFoundException, PropertiesFileIsNotFoundException {
    	this(projectSetup,connector,iterations);
    	this.finder.setNormalizator(normalizator);
    }
           
    public boolean areDirectLinked(String domain, String target) throws ClassNotFoundException, SQLException {
        return this.finder.areDirectLinked(domain, target);        
    }

    public void generateBiGraph(String fromPageName, String toPage) throws SQLException, ClassNotFoundException, UnsupportedEncodingException, PropertiesFileIsNotFoundException {
        List<List<String>> paths = this.finder.getPathsUsingCategories(fromPageName, toPage);
        for (List<String> path : paths) {
            String strPath = getGeneralizator().generalizePathQuery(path);
            int dbPathId = this.getNormalizedStarPathId(strPath);
            int dbPageId = this.getTupleIdIntoDB(FromToPair.concatPair(fromPageName, toPage));
            if (!(dbPageId == 0 || dbPathId == 0)) {
            	this.resultsDb.saveEdge(dbPathId, dbPageId, "Tuple: " + FromToPair.concatPair(fromPageName, toPage) + " Path: " + strPath);
                // this.resultsDb.saveEdge(dbPathId, dbPageId);
            }
        }
       
        if (paths.isEmpty()) {
//            this.pathsNotFound.add(fromPageName + " , " + toPage);
            this.pathsNotFound.add(FromToPair.concatPair(fromPageName, toPage));
            this.addNotFoundPath(fromPageName, toPage);
        }
    }
    
    public int getNormalizedStarPathId(String path) {
        int result = this.getPathIndex(path);
        if (result == 0)
            result = this.saveNormalizedPath(path);
        return result;
    }

    public List<String> notFoundPath() {
        return this.pathsNotFound;
    }

    public int getNormalizedPathIdIntoDB(List<String> path) {
        int result = 0;
        String normalizedPath = this.pathToString(path);
        result = this.getPathIndex(normalizedPath);
        if (result == 0) {
            result = this.saveNormalizedPath(normalizedPath);
        }
        return result;
    }

    public int getTupleIdIntoDB(String page) {
        int result = 0;
        result = this.getTupleIndex(page);
        if (result == 0) {
            result = this.saveTuple(page);
        }
        return result;
    }

    public void removeNotFound(int id){
    	try {
    		this.resultsDb.removeNotFoundPath(id);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(BipartiteGraphGenerator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(BipartiteGraphGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }
    
    private void addNotFoundPath(String from, String to) {
        try {
            this.resultsDb.saveNotFoundPath(from, to);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(BipartiteGraphGenerator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(BipartiteGraphGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void addEdge(int pathId, int pageId) {
        try {

        	this.resultsDb.saveEdge(pathId, pageId);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(BipartiteGraphGenerator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
             Logger.getLogger(BipartiteGraphGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private String pathToString(List<String> path) {
    	return PathsResolver.pathToString(path);
    }

    /**
     * Save the normalizedPath into de DB and returns the id of the insertions. 0 if it fails.
     * @param normalizedPath
     * @return 
     */
    private int saveNormalizedPath(String normalizedPath) {
        int result = 0;
        try {
            this.resultsDb.saveNormalizedPath(normalizedPath);
            result = this.resultsDb.getNormalizedPathId(normalizedPath);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(BipartiteGraphGenerator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(BipartiteGraphGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }

    /**
     * Obtain the id of the path index from the database. 0 if path not found.
     * @param normalizedPath
     * @return 
     */
    private int getPathIndex(String normalizedPath) {
        int result = 0;
        try {
        	result = this.resultsDb.getNormalizedPathId(normalizedPath);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(BipartiteGraphGenerator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(BipartiteGraphGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }

    private int saveTuple(String tuple) {
        int result = 0;
        try {
            this.resultsDb.saveTuple(tuple);
            result = this.getTupleIndex(tuple);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(BipartiteGraphGenerator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(BipartiteGraphGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }

    /**
     * Return de id in the U_page table with page name cityPage
     * @param tuple
     * @return 
     */
    private int getTupleIndex(String tuple) {
        int result = 0;
        try {
            result = this.resultsDb.getTupleId(tuple);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(BipartiteGraphGenerator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(BipartiteGraphGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }

    public int getRegularGeneratedPaths() {
        return this.finder.getRegularGeneratedPaths();
    }

	public PathIndex getPathIndex() {
		return this;
	}

	/**
	 * Methods of PathIndex interface. This method is used to obtain the path queries for a specific pair.
	 * @throws PropertiesFileIsNotFoundException 
	 */
	@Override
	public List<String> getPathQueries(String x, String y) throws PropertiesFileIsNotFoundException {
		List<String> results = new ArrayList<String>();
		try {
			Connection con = this.connector.getResultsConnection();
			PreparedStatement st = con.prepareStatement("select `V_Normalized`.id as path_id, `V_Normalized`.path from  (select id from U_page where page like ?) as Ta inner join UxV on Ta.id=UxV.u_from inner join `V_Normalized` on UxV.v_to=V_Normalized.id");
			st.setString(1, x+" , "+ y);
			ResultSet rs = st.executeQuery();
			while(rs.next()){
				results.add(rs.getString("path"));
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return results;		
	}


  

    /**
     * @return the generalizator
     */
    public IGeneralization getGeneralizator() {
        return generalizator;
    }

    /**
     * @param generalizator the generalizator to set
     */
    public void setGeneralizator(IGeneralization generalizator) {
        this.generalizator = generalizator;
    }

}
