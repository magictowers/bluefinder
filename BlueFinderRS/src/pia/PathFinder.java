package pia;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import utils.FromToPair;
import normalization.BasicNormalization;
import normalization.INormalizator;
import db.WikipediaConnector;
import db.utils.DbResultMap;
import db.utils.ResultsDbInterface;
import db.utils.WikipediaDbInterface;
import utils.PathsResolver;

/**
 *
 * @author dtorres
 */
public class PathFinder {

    private int iterations;
    private String reason = "";
    private List<List<String>> specificPaths;
    /**
     * Depth level to traverse categories.
     */
    private int categoryPathIterations;
    private int catIterationsLevel = 1;
    private int regularGeneratedPaths = 0;
    private List<String> analysedPathQueryRetrieved; 
    private INormalizator normalizator;
    private WikipediaDbInterface wikipediaDb;
    private ResultsDbInterface resultsDb;
    public static final List<String> BLACKLIST_CATEGORY;
    static {
        List<String> tmp = new ArrayList<String>();
        try {
        	Properties prop = new Properties();
        	prop.load(WikipediaConnector.class.getClassLoader().getResourceAsStream("setup.properties"));
        	String filename = prop.getProperty("BACKLIST_FILENAME");
        	InputStream blackListIS = PathFinder.class.getClassLoader().getResourceAsStream(filename);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(blackListIS));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                tmp.add(line);
            }
            bufferedReader.close();
        } catch (IOException e) {
            System.err.print("Some error ocurred while loading category's blacklist.");
            e.printStackTrace();
        }
        BLACKLIST_CATEGORY = Collections.unmodifiableList(tmp);
    }

    public PathFinder() {
        this.reason = "";
        this.specificPaths = new ArrayList<List<String>>();
        this.categoryPathIterations = 1;
        this.regularGeneratedPaths = 0;
        this.analysedPathQueryRetrieved = new ArrayList<String>();
        this.normalizator = PIAConfigurationBuilder.getNormalizator();
        this.wikipediaDb = new WikipediaDbInterface();
        this.resultsDb = new ResultsDbInterface();
    }
    
    public void setNormalizator(INormalizator normalizator) {
    	this.normalizator = normalizator;
    }
    
    public void setCategoryPathIterations(int x) {
        this.catIterationsLevel = x;
    }

    public int getRegularGeneratedPaths() {
        return this.regularGeneratedPaths;
    }
    
    public void incrementRegularGeneratedPaths() {
        this.regularGeneratedPaths = this.regularGeneratedPaths + 1;
    }
    
    /**
     * Returns true if fromPage has a direct link to toPage. Otherwise returns false. 3th method called.
     * @param fromPage
     * @param toPage
     * @return
     * @throws java.lang.ClassNotFoundException
     * @throws java.sql.SQLException
     */
    public boolean areDirectLinked(String fromPage, String toPage) throws ClassNotFoundException, SQLException{
        boolean areLinked = false;
        Integer fromPageId = this.getPageId(fromPage);
        Integer toPageId = this.getPageId(toPage);
        areLinked = this.wikipediaDb.areDirectlyLinked(fromPageId, toPageId);
//        List<DbResultMap> nodes = this.wikipediaDb.getDirectNodes(fromPageId);
        // TODO: optimizar : done? Verlo con lo de arriba
//        for (int i = 0; i < nodes.size() && !areLinked; i++) {
//			DbResultMap map = nodes.get(i);
//			if (map.getInteger("id").equals(toPageId)) {
//				areLinked = true;
//			}
//		}
        return areLinked;
    }
    
    public int getIterations() {
        return this.iterations;
    }

//    private List<List<DbResultMap>> getDirectNodesFrom(Set<Integer> current) throws ClassNotFoundException, SQLException {
//        List<List<DbResultMap>> nodes = new ArrayList<List<DbResultMap>>();
//        for (Integer pageId : current) {
//        	List<DbResultMap> results = this.wikipediaDb.getDirectNodes(pageId);
//        	nodes.add(results);
//        }
//        return nodes;    	
//    }

    protected Integer getPageId(String from) throws ClassNotFoundException {
        return this.wikipediaDb.getPageId(from);
    }

    protected Integer getCatPageId(String from) throws ClassNotFoundException {
        return this.wikipediaDb.getCategoryId(from);
    }

    public String getReason() {
        return this.reason;
    }

    //----------------------------WITH CATEGORIES --------------------
    /**
     * Returns all normalized paths from a fromPage to a toPage using categories. 1st called method.
     * @param fromPage
     * @param toPage
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws java.io.UnsupportedEncodingException
     */
    public List<List<String>> getPathsUsingCategories(String fromPage, String toPage) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        List<String> categoriesOfFromPage = this.getCategoriesFromPage(fromPage);
        List<String> categoriesOfToPage = this.getCategoriesFromPage(toPage);
        List<String> listOf = this.getListOfFrom(fromPage);
        // List<String> listOf = new ArrayList<String>();
        List<String> current = new ArrayList<String>();
        current.add(FromToPair.FROM_WILDCARD);  // current.add("#from");
        List<List<String>> allPaths = new ArrayList<List<String>>();
        List<String> direct = new ArrayList<String>();
        List<String> visited;
       
        direct.add(FromToPair.FROM_WILDCARD);  // direct.add("#from");
        direct.add(FromToPair.TO_WILDCARD);  // direct.add("#to");

        if (this.areDirectLinked(fromPage, toPage)) {
            allPaths.add(direct);
        }
        if (this.catIterationsLevel > 1) {
	        for (String fromCategoryName : categoriesOfFromPage) {
	        	visited = new ArrayList<String>();
//	        	String currentCat = "Cat:" + this.normalizeCategory(fromCategoryName, fromPage,toPage);
                String currentCat = PathsResolver.CATEGORY_PREFIX + this.normalizeCategory(fromCategoryName, fromPage,toPage);
	            current.add(currentCat);
	            this.getPathUsingCategories(fromCategoryName,fromPage, toPage, current, allPaths, categoriesOfToPage,visited);
	            current.remove(current.size() - 1);
	        }
	        
	        for (String listOfPage : listOf) {
	        	if (this.areDirectLinked(listOfPage, toPage)) {
	        		String listPageName = this.normalizeCategory(listOfPage, fromPage, toPage);
	        		List<String> pathListOf = new ArrayList<String>();
	        		pathListOf.add(FromToPair.FROM_WILDCARD);  // pathListOf.add("#from");
	        		pathListOf.add(listPageName); 
	        		pathListOf.add(FromToPair.TO_WILDCARD);  // pathListOf.add("#to");
	        		allPaths.add(pathListOf);
	        	}
	        }        
        }        

        this.specificPaths = allPaths;
        return this.specificPaths;
    }

    List<String> getListOfFrom(String fromPage) {
/*		List<String> results = new ArrayList<String>();
	    int id;
		try {
			id = this.getPageId(fromPage);
		    String query = "select page.page_id as pageid, page.page_title as page_title from (pagelinks as level0 inner join page on level0.pl_from=? and level0.pl_namespace=0 and page.page_namespace=0 and page.page_title=level0.pl_title and page_title like \"List_of_%\")";
			PreparedStatement pst = WikipediaConnector.getConnection().prepareStatement(query);
			pst.setInt(1, id);
			ResultSet rs = pst.executeQuery();
			while(rs.next()){
				results.add(rs.getString("page_title"));
			}
			pst.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		return results;*/
    	List<String> results = new ArrayList<String>();
    	try {
			Integer pageId = this.wikipediaDb.getPageId(fromPage);
			results = this.wikipediaDb.getListOf(pageId);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
    	return results;
	}

	/**
     * Return all the categories which pageId belongs. 2nd called method.
     * @param pageName
     * @return 
     * @throws java.lang.ClassNotFoundException 
     * @throws java.sql.SQLException 
     * @throws java.io.UnsupportedEncodingException 
     */
    protected List<String> getCategoriesFromPage(String pageName) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        Integer pageId = this.getPageId(pageName);
        List<String> tmpcategories = this.wikipediaDb.getCategories(pageId);
        List<String> categories = new ArrayList<String>();
        for (String category : tmpcategories) {
        	if (!this.isBlackCategory(category)) {
        		categories.add(category);
        	}
        }
        return categories;
        /* List<String> listCategories = new ArrayList<String>();

        Connection c = WikipediaConnector.getConnection();
        Statement st = c.createStatement();
        String query_text = "SELECT convert(cl_to using utf8) as cl_to FROM categorylinks c where cl_from=" + pageId + " and cl_type=\"page\"";
        ResultSet rs = st.executeQuery(query_text);

        while (rs.next()) {
            String catTo = rs.getString("cl_to");
            //InputStream stream = rs.getBinaryStream("cl_to");
            //String catTo = stream.toString();
            if (!this.isBlackCategory(catTo)) {
                listCategories.add(catTo);
            }
        }
        st.close();
        return listCategories;*/
    }
    
    String getStringValue(byte[] varbinary) {
        return new String(varbinary);
    }

    /**
     * Returns a list of paths navigating through categories. 5th called method.
     * @param categoriesOfToPage 
     * @param visited 
     * @param catId
     * @param personPageId 
     */
    private void getPathUsingCategories(String fromCategoryName, String fromPage,  String toPage, List<String> currentPath, 
    		List<List<String>> allPaths, List<String> categoriesOfToPage, List<String> visited) 
    		throws ClassNotFoundException, UnsupportedEncodingException {   
    	if (categoriesOfToPage.contains(fromCategoryName)) {
            currentPath.add("#to");
            List<String> temporal = new ArrayList<String>();
            temporal.addAll(currentPath);
            allPaths.add(temporal);
            this.regularGeneratedPaths++; //all paths generated counter.
            currentPath.remove(currentPath.size() - 1);
        } 
        if (this.categoryPathIterations < this.catIterationsLevel - 1) {
            List<String> subCategories = this.getSubCategories(fromCategoryName);
//            System.out.printf("%s : level %d\n", fromCategoryName, this.categoryPathIterations);
            for (String subCategoryName : subCategories) {
                if(!visited.contains(subCategoryName)) {
                	this.categoryPathIterations++;
                	String currentCat = "Cat:" + this.normalizeCategory(subCategoryName, fromPage, toPage);
                    currentPath.add(currentCat);                    
                	visited.add(subCategoryName);
	                this.getPathUsingCategories(subCategoryName,fromPage, toPage, currentPath, allPaths, categoriesOfToPage, visited);
	                currentPath.remove(currentPath.size() - 1);
	                this.categoryPathIterations--;
                }
            }
        }
    }

    /**
     * Returns a list of string with the subcategory names of a given category name
     * @param categoryName
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException 
     */
    private List<String> getSubCategories(String categoryName) throws ClassNotFoundException {
    	return this.wikipediaDb.getSubcategories(categoryName);
    }

    /**
     * Returns a String with the name of the category in a normalized form. It delegates the responsibility to
     * the normalization strategy in normalizator. 4th called method.
     * @param subCategoryName
     * @param fromCatName
     * @param toCatName
     * @return 
     */
    protected String normalizeCategory(String subCategoryName, String fromCatName, String toCatName) {
        return this.normalizator.normalizeCategory(subCategoryName, fromCatName, toCatName);
    }

    /**
     * Only used by `main` of this class.
     * 
     * Return the number of relevantDocuments for the query path.
     * @param pathQuery
     * @return 
     * @throws java.lang.ClassNotFoundException 
     * @throws java.sql.SQLException 
     * @throws java.io.UnsupportedEncodingException 
     */
    public int getRelevantDocuments(String pathQuery) throws ClassNotFoundException, 
            SQLException, UnsupportedEncodingException {
        String query = "SELECT distinct up.page FROM U_page up";        
        Connection dbresearch = WikipediaConnector.getResultsConnection();
        Statement st = dbresearch.createStatement();
        ResultSet normalizedPaths = st.executeQuery(query);
        while (normalizedPaths.next()) {
            String page = normalizedPaths.getString("page");
            String from = this.getFrom(page);
            String to = this.getTo(page);
            System.out.printf("Page: %s   -   From: %s - To: %s", page, from, to);
            this.computeRelevantDocuments(pathQuery,from,to);
        }
        
        List<DbResultMap> results = this.resultsDb.getTuples();
        
        results = this.resultsDb.getNotFoundPaths();
        for (DbResultMap nfpc : results) {
        	String from = nfpc.getString("v_from");
        	String to = nfpc.getString("u_to");
        	this.computeRelevantDocuments(pathQuery, from, to);
        }

        st.close();  
        return 0;
    }
    
    /**
     * Algoritmo usado para calcular el recall y precision 
     */
    private void computeRelevantDocuments(String path, String from, String to) 
    		throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        if (this.isReachablePath(path, from, to)) {
            String categoryName = this.getCategory(path,from, to);
        
            if(!this.analysedPathQueryRetrieved.contains(categoryName)) {
                if(!categoryName.equals("")) {
                    this.analysedPathQueryRetrieved.add(categoryName);
                    this.writeRelevantPagesFromCategory(categoryName);
                } else {
                    this.analysedPathQueryRetrieved.add("DIEGO"+from);
                    this.writeRelevantPagesFromDirectLink(from);
                }
            }
        }
    }
    
    /*
     * Algoritmo viejo, creo que puede ser borrado.
     */
    public boolean isReachablePath(String pathQuery, String from, String to) throws ClassNotFoundException, SQLException, UnsupportedEncodingException{
        if(pathQuery.equals("[to]")){
            return true;
        }
        String path  = pathQuery.substring(0, pathQuery.length()-5);
        String[] steps = path.split("/");
        for (int i = 0; i < steps.length; i++) {
            steps[i] = steps[i].replaceAll("\\[from\\]", from);
            steps[i] = steps[i].replaceAll("\\[to\\]", to);
        }    
         
        if(this.getCategoriesFromPage(from).contains(steps[0])) {   
            return this.pathReacheableCategories(steps);
        } else { 
            return false;
        }
    }
    
    private boolean pathReacheableCategories(String[] steps) throws ClassNotFoundException {
        for (int i = 0; i < steps.length-1; i++) {
            if(!this.getSubCategories(steps[i]).contains(steps[i+1])){
                return false;
            }           
        }
        return true;
    }
    
    private void writeRelevantPagesFromDirectLink(String from) {
    	try {
            int pageId = this.getPageId(from);            
            if (pageId != 0) {
                List<DbResultMap> res = this.wikipediaDb.getDirectNodes(pageId);
                for (DbResultMap map : res) {
                    String pageTitle = map.getString("page_title");
                    this.saveRelatedPage(pageTitle);
                }
            } else {
                System.out.println("from:" + from +" no tiene page id");
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(PathFinder.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(PathFinder.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void writeRelevantPagesFromCategory(String categoryName) {
        try {
            Connection wikipedia = WikipediaConnector.getConnection();
            Statement st = wikipedia.createStatement();
            
            String query_text = "SELECT p.page_title FROM categorylinks c, page p where cl_to=\""+categoryName+"\" and cl_type=\"page\" and p.page_id=c.cl_from";
           
            ResultSet rs = st.executeQuery(query_text);
            
            while(rs.next()){
                 byte[] varbinary = (byte[]) rs.getObject("page_title");
                 String page_title = this.getStringValue(varbinary);
                this.saveRelatedPage(page_title);
            }
            st.close();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(PathFinder.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(PathFinder.class.getName()).log(Level.SEVERE, null, ex);
        }
    }    
    
    private void saveRelatedPage(String pageTitle){
        try {
            Connection con = WikipediaConnector.getResultsConnection();
            Statement st = con.createStatement();
            
            String query = "INSERT INTO relevant_pages (page) values (\""+pageTitle+"\")";
            
            st.executeUpdate(query);
            
            st.close();   
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(PathFinder.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            //Logger.getLogger(PathFinder.class.getName()).log(Level.SEVERE, , ex);   
        }
    }
    
    private String getCategory(String path, String from, String to) {
    	
        String[] steps = ("/"+path).split("/");
        
        String result = steps[steps.length-2];
                
        result = result.replaceAll("\\[from\\]", from);
        
        result = result.replaceAll("\\[to\\]", to);
        
        return result;        
    }
    
    private String getFrom(String pairOfPages){
        String result = pairOfPages.substring(1, pairOfPages.length()-2);
        result = result.replaceAll(",_", "__");
        String[] tokens = result.split(",");
        result = tokens[0].replaceAll("__", ",_");
        
        return result;
    }
    
    private String getTo(String pairOfPages){
    	String result = pairOfPages.substring(1, pairOfPages.length()-1);
        result = result.replaceAll(",_", "__");
        String[] tokens = result.split(",");
        result = tokens[1].replaceAll("__", ",_");
        return result;
    }

	public boolean isBlackCategory(String blackCategory) {
		if(BLACKLIST_CATEGORY.contains(blackCategory))
			return true;
            
		for(String black : BLACKLIST_CATEGORY) {
			if(blackCategory.startsWith(black))
				return true;
		}
        
		return false;
	}
   
    /*
     
     public static void main(String[] args) throws ClassNotFoundException, SQLException, UnsupportedEncodingException{
         
         Connection results = WikipediaConnector.getResultsConnection();
         String query = "CREATE TABLE IF NOT EXISTS `relevant_pages` (`id` int(4) NOT NULL AUTO_INCREMENT,`page` varchar(300) NOT NULL, PRIMARY KEY (`id`))";
         String query_truncate = "Truncate relevant_pages";
         Statement st = results.createStatement();
         PathFinder finder = new PathFinder();
         
         st.executeUpdate(query);
         st.executeUpdate(query_truncate);
         
         
        if (args.length < 1 || args[0].equalsIgnoreCase("help")) {
            System.out.println("Usage: <pathQuery>");
            
            return;
        }
        
        String pathQuery = args[0];
        finder.getRelevantDocuments(pathQuery);
        
        String relevantNumber = "SELECT count(distinct page) as total FROM relevant_pages";
        
        ResultSet resultSet = st.executeQuery(relevantNumber);
        if(resultSet.next()){
        int total = resultSet.getInt("total");
        
         System.out.println("Relevant pages: " + total);
        }
         
         
     }*/
}
