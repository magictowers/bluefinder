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
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import normalization.INormalizator;
import db.WikipediaConnector;

/**
 *
 * @author dtorres
 */
public class BipartiteGraphGenerator implements PathIndex{

    private PathFinder finder;
    private List<String> pathsNotFound;

    public BipartiteGraphGenerator() {
        this.finder = new PathFinder();
        this.pathsNotFound = new ArrayList<String>();
    }

    public BipartiteGraphGenerator(int categoryIterations) {
        this();
        this.finder.setCategoryPathIterations(categoryIterations);
    }
    
    public BipartiteGraphGenerator(INormalizator normalizator, int iterations){
    	this(iterations);
    	this.finder.setNormalizator(normalizator);
    }
       
    public boolean areDirectLinked(String domain, String target) throws ClassNotFoundException, SQLException {
        return this.finder.areDirectLinked(domain, target);        
    }

    public void generateBiGraph(String fromPageName, String toPage) throws SQLException, ClassNotFoundException, UnsupportedEncodingException {
        List<List<String>> paths = this.finder.getPathsUsingCategories(fromPageName, toPage);

        for (List<String> path : paths) {
            int dbPathId = this.getNormalizedPathIdIntoDB(path);
            int dbPageId = this.getCityPageIdIntoDB(fromPageName + " , " + toPage);
            if (!(dbPageId == 0 || dbPathId == 0)) {
                this.addEdge(dbPathId, dbPageId);
            }
        }
       
        if (paths.isEmpty()) {
            this.pathsNotFound.add(fromPageName + " , " + toPage);
            this.addNotFoundedPath(fromPageName, toPage);
        }
    }

    public List<String> notFoundPath() {
        return this.pathsNotFound;
    }

    /*
    private void writeIntoFile() throws IOException {
        File file = new File("Graph.txt");
        BufferedWriter output = new BufferedWriter(new FileWriter(file));
        this.writeNodesOnAFile(output);

        output.close();
        System.out.println("Your file has been written");
    }*/

    public int getNormalizedPathIdIntoDB(List<String> path) {
        int result = 0;
        String normalizedPath = this.pathToString(path);
        result = this.getPathIndex(normalizedPath);
        if (result == 0) {
            result = this.saveNormalizedPath(normalizedPath);
        }
        return result;
    }

    public int getCityPageIdIntoDB(String page) {
        int result = 0;
        result = this.getTupleIndex(page);
        if (result == 0) {
            result = this.saveTuple(page);
        }
        return result;
    }

    public void removeNotFound(int id){
    	try {
    		Connection c = WikipediaConnector.getResultsConnection();
    		Statement st = c.createStatement();
    		String query_text = "DELETE FROM NFPC where id="+id;

            System.out.println(query_text);
            st.executeUpdate(query_text);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(BipartiteGraphGenerator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(BipartiteGraphGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }
    
    private void addNotFoundedPath(String from, String to) {
        try {
            //Connection c = WikipediaConnector.getResultsConnection();
            //Statement st = c.createStatement();
            String query_textp = "INSERT INTO NFPC (v_from,u_to) VALUES (?,?)";
            PreparedStatement pre = WikipediaConnector.getResultsConnection().prepareStatement(query_textp);
            pre.setString(1, from);
            pre.setString(2, to);
            pre.executeUpdate();
            pre.close();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(BipartiteGraphGenerator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(BipartiteGraphGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void addEdge(int pathId, int pageId) {
        try {
            Connection c = WikipediaConnector.getResultsConnection();
            Statement st = c.createStatement();
            String query_text = "INSERT INTO UxV (u_from,v_to,description) VALUES (" + pageId + "," + pathId + ",\" \")";
            st.executeUpdate(query_text);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(BipartiteGraphGenerator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
             Logger.getLogger(BipartiteGraphGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /*
    private void insertPathIntoGraph(List<String> path, String toPage) {
        String textualPath = this.pathToString(path);

        if (this.normalizedPaths.get(textualPath) == null) {
            this.normalizedPaths.put(textualPath, new HashSet<String>());
        }
        this.normalizedPaths.get(textualPath).add(toPage);
    }*/

    private String pathToString(List<String> path) {
        String text = "";
        for (String step : path) {
            text += step + " / ";
        }
        return text.substring(0, text.lastIndexOf(" / "));
    }

    /**
     * Save the normalizedPath into de DB and returns the id of the insertions. 0 if it fails.
     * @param normalizedPath
     * @return 
     */
    private int saveNormalizedPath(String normalizedPath) {
        int result = 0;
        try {
            Connection c = WikipediaConnector.getResultsConnection();
            PreparedStatement st = c.prepareStatement("INSERT INTO V_Normalized (path) VALUES (?)");
            st.setString(1, normalizedPath);
            st.executeUpdate();
            result = this.getPathIndex(normalizedPath);
            st.close();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(BipartiteGraphGenerator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(BipartiteGraphGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }

    /**
     * Obtain the id of the path index from the database. 0 if path not founded.
     * @param normalizedPath
     * @return 
     */
    private int getPathIndex(String normalizedPath) {
        int result = 0;
        try {
            Connection c = WikipediaConnector.getResultsConnection();
            String query = "SELECT id FROM V_Normalized where path=?";
            PreparedStatement pst = c.prepareStatement(query);
            pst.setString(1, normalizedPath);

            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                result = rs.getInt("id");
            }
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
            Connection c = WikipediaConnector.getResultsConnection();
            String query_text_prepared = "INSERT INTO U_page (page) VALUES (?)";
            PreparedStatement preparedStatement = c.prepareStatement(query_text_prepared);
            preparedStatement.setString(1, tuple);
            preparedStatement.executeUpdate();
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
            Connection c = WikipediaConnector.getResultsConnection();
            String query_prepared = "SELECT id FROM U_page where page=?";
            PreparedStatement pst = c.prepareStatement(query_prepared);
            pst.setString(1, tuple);
            ResultSet rs =pst.executeQuery();
            if (rs.next()) {
                result = rs.getInt("id");
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(BipartiteGraphGenerator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(BipartiteGraphGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }

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
}
