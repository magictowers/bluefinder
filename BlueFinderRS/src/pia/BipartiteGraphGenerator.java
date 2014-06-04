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
import normalization.INormalizator;
import db.WikipediaConnector;
import db.utils.ResultsDbInterface;
import strategies.IGeneralization;

/**
 *
 * @author dtorres
 */
public class BipartiteGraphGenerator implements PathIndex{

    private PathFinder finder;
    private List<String> pathsNotFound;
    private ResultsDbInterface resultsDb;
    private IGeneralization generalizator;

    public BipartiteGraphGenerator() throws SQLException, ClassNotFoundException {
        this.finder = new PathFinder();
        this.resultsDb = new ResultsDbInterface();
        this.pathsNotFound = new ArrayList<String>();
        this.generalizator = PIAConfigurationBuilder.getGeneralizator();
    }
    
    public BipartiteGraphGenerator(IGeneralization generalization) throws SQLException, ClassNotFoundException {
        this();
        this.generalizator = generalization;
    }

    public BipartiteGraphGenerator(int categoryIterations) throws SQLException, ClassNotFoundException {
        this();
        this.finder.setCategoryPathIterations(categoryIterations);
    }
    
    public BipartiteGraphGenerator(INormalizator normalizator, int iterations) throws SQLException, ClassNotFoundException {
    	this(iterations);
    	this.finder.setNormalizator(normalizator);
    }
           
    public boolean areDirectLinked(String domain, String target) throws ClassNotFoundException, SQLException {
        return this.finder.areDirectLinked(domain, target);        
    }

    public void generateBiGraph(String fromPageName, String toPage) throws SQLException, ClassNotFoundException, UnsupportedEncodingException {
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
//            Connection c = WikipediaConnector.getResultsConnection();
//            Statement st = c.createStatement();
//            String query_text = "INSERT INTO UxV (u_from,v_to,description) VALUES (" + pageId + "," + pathId + ",\" \")";
//            st.executeUpdate(query_text);
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
	 */
	@Override
	public List<String> getPathQueries(String x, String y) {
		List<String> results = new ArrayList<String>();
		try {
			Connection con = WikipediaConnector.getResultsConnection();
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


    /*
    private void writeIntoFile() throws IOException {
        File file = new File("Graph.txt");
        BufferedWriter output = new BufferedWriter(new FileWriter(file));
        this.writeNodesOnAFile(output);

        output.close();
        System.out.println("Your file has been written");
    }

    private void insertPathIntoGraph(List<String> path, String toPage) {
        String textualPath = this.pathToString(path);

        if (this.normalizedPaths.get(textualPath) == null) {
            this.normalizedPaths.put(textualPath, new HashSet<String>());
        }
        this.normalizedPaths.get(textualPath).add(toPage);
    }*/
    /*
    public static void main(String[] args) throws ClassNotFoundException, SQLException, IOException {
        BipartiteGraphGenerator bgg = new BipartiteGraphGenerator();
        bgg.generateFromMemoryDataSet(bgg.dataMemory());
         System.out.println( bgg.getPathIndex("[from]/people_from_[from]/[o]"));
        System.out.println(bgg.saveNormalizedPath("pathNormalized"));
        System.out.println(bgg.saveCityPage("Madrid"));
        bgg.addEdge(3, 4);
    }  */

/*    private void addNewDataElement(List<List<String>> dataset, String city, String person) {
        List<String> data = new ArrayList<String>();
        data.add(city);
        data.add(person);
        dataset.add(data);
    }

    private void writeNodesOnAFile(BufferedWriter output) throws IOException {
        try {
            String text = "digraph G { \n"
                    + "compound=true;\n"
                    + "ranksep=1.25;\n"
                    + "node [shape=plaintext, fontsize=16];\n"
                    + "bgcolor=white;\n"
                    + "edge [arrowsize=1, color=black];\n";

            output.write(text);

            Connection c = WikipediaConnector.getResultsConnection();
            Statement st = c.createStatement();
//                String query_text = "SELECT path,id FROM V_Normalized";
//                System.out.println(query_text);
//
//                ResultSet rs = st.executeQuery(query_text);
//                while(rs.next()){
//                    text = rs.getString("path");
//                    int id = rs.getInt("id");
//                    String toWrite = "subgraph V"+id+"V {label=\""+text+"\"; labelloc=\"b\"};\n"; 
//                    output.write(toWrite);
//                    
//                }
//                
//                query_text = "SELECT page,id FROM U_page";
//                rs = st.executeQuery(query_text);
//                while(rs.next()){
//                    text = rs.getString("page");
//                    int id = rs.getInt("id");
//                    String toWrite = "subgraph U"+id+"U [label=\""+text+"\"];\n"; 
//                    output.write(toWrite);
//                 }
//                
            String query_text = "SELECT V.path, U.page FROM V_Normalized V inner join UxV uv on V.id=uv.u_from inner join U_page U on uv.v_to=U.id";
            ResultSet rs = st.executeQuery(query_text);
            while (rs.next()) {
                String idU = rs.getString("U.page");
                String idV = rs.getString("V.path");
                String toWrite = "\"" + idV + "\" -> \"" + idU + "\";\n";
                output.write(toWrite);
            }
            output.write("}");

        } catch (ClassNotFoundException ex) {
            Logger.getLogger(BipartiteGraphGenerator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(BipartiteGraphGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }


    }*/

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
