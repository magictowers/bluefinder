package pia;

import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import normalization.BasicNormalization;
import normalization.INormalizator;
import db.WikipediaConnector;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;

/**
 *
 * @author dtorres
 */
public class PathFinder {

    //private int from;
    //private int to;
    private Set<Integer> visited;
    //private Set<Integer> newPaths;
    private int iterations;
    private String reason = "";
    private List<List<String>> specificPaths;
    private int categoryPathIterations;
    private int catIterationsLevel=1;
    private int regularGeneratedPaths = 0;
    private List<String> analysedPathQueryRetrieved; 
    private INormalizator normalizator;
    private static final List<String> BLACKLIST_CATEGORY;
    static {
        List<String> tmp = new ArrayList<String>();
        try {
            //FileReader fileReader = new FileReader("blackilist_category.txt");
        	InputStream blackListIS = PathFinder.class.getClassLoader().getResourceAsStream("blacklist_category.txt");
//            FileInputStream fileReader = new FileInputStream("blacklist_category.txt");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(blackListIS));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                tmp.add(line);
            }
            //fileReader.close();
            bufferedReader.close();
        } catch (IOException e) {
            System.err.print("Some error ocurred while loading category's blacklist.");
            e.printStackTrace();
        }
        BLACKLIST_CATEGORY = Collections.unmodifiableList(tmp);
    }

    public PathFinder() {
        this.visited = new HashSet<Integer>();
        //this.newPaths = new HashSet<Integer>();
        this.reason = "";
        this.specificPaths = new ArrayList<List<String>>();
        this.categoryPathIterations = 1;
        this.regularGeneratedPaths = 0;
        this.analysedPathQueryRetrieved = new ArrayList<String>();
        this.normalizator= new BasicNormalization();
    }
    
    public void setNormalizator(INormalizator normalizator){
    	this.normalizator=normalizator;
    }
    
    public void setCategoryPathIterations(int x){
        this.catIterationsLevel=x;
    }

    public int getRegularGeneratedPaths(){
        return this.regularGeneratedPaths;
    }
    
    public void incrementRegularGeneratedPaths(){
        this.regularGeneratedPaths=this.regularGeneratedPaths+1;
    }
    
    /*
     * Returns true if fromPage has a direct link to toPage. Otherwise returns false. 3th method called.
     */
    public boolean areDirectLinked(String fromPage, String toPage) throws ClassNotFoundException, SQLException{
        boolean result = false;
        Integer fromPageId= this.getPageId(fromPage);
        Integer toPageId= this.getPageId(toPage);
        Set<Integer> setIds = new HashSet<Integer>();
        setIds.add(fromPageId);
        List<Integer> linkedPages = this.getDirectNodesFrom(setIds);
        result = linkedPages.contains(toPageId);
        
        return result;
    }
    
    public int getIterations() {
        return this.iterations;
    }

    /**
     * returns the page id s of all links of the @param current.
     */
    private List<Integer> getDirectNodesFrom(Set<Integer> current) throws ClassNotFoundException, SQLException {
        List<Integer> result = new ArrayList<Integer>();

        Connection wikipediaConnection = WikipediaConnector.getConnection();
        Statement st = wikipediaConnection.createStatement();
        for (Integer pageId : current) {
            //ResultSet rs = st.executeQuery("select  level0.pl_title as pagetitle, page.page_id as pageid from (pagelinks as level0 inner join page on level0.pl_from="+pageId+" and level0.pl_namespace=0 and page.page_namespace=0 and page.page_title=level0.pl_title)");
            ResultSet rs = st.executeQuery("select page.page_id as pageid from (pagelinks as level0 inner join page on level0.pl_from=" + pageId + " and level0.pl_namespace=0 and page.page_namespace=0 and page.page_title=level0.pl_title)");
            while (rs.next()) {
                Integer idDestination = rs.getInt("pageid");
                result.add(idDestination);
                //    System.out.println("New Destination: "+ idDestination);
            }
            rs.close();
        }
        //wikipediaConnection.close();
        return result;
    }

    /*
    private Set<Integer> removeVisited(List<Integer> resultNodes) {
        Set<Integer> result = new HashSet<Integer>();
        for (Integer integer : resultNodes) {
            if (!this.visited.contains(integer)) {
                result.add(integer);
            }
        }
        return result;
    }*/

    /** PARA BORRAR ??  
    private void findSpecificPath(PathFinder finder, String person, String city, Statement st2) throws ClassNotFoundException {
        try {
            //if(finder.findPathBFS(city, person)){
            if (finder.findPathBFS(person, city)) {
                st2.executeUpdate("INSERT INTO found_path (page_from, page_to, hops) VALUES (\"" + person + "\",\"" + city + "\",\"" + finder.getIterations() + "\" )");
               // System.out.println("Camino encontrado para " + person);
            } else {
                //System.out.println("INSERT INTO not_found_path (from, to, hops, reason) VALUES (\"" + person + "\",\"" + city + "\",\"" + finder.getIterations() + "\",\"" + finder.getReason() + "\" )");
                st2.executeUpdate("INSERT INTO not_found_path (page_from, page_to, hops, reason) VALUES (\"" + person + "\",\"" + city + "\",\"" + finder.getIterations() + "\",\"" + finder.getReason() + "\" )");
                //System.out.println("Camino NO para " + person);
            }
        } catch (SQLException ex) {
            
        }
    }
    */

    protected Integer getPageId(String from) throws ClassNotFoundException {
        int page = 0;
        try {
            Connection c = WikipediaConnector.getConnection();
            PreparedStatement st = c.prepareStatement("Select page_id from page where page_namespace=0 and page_title=?");
            //  System.out.println("Select page_id from page where page_namespace=0 and page_title=\"" + from + "\"");
            //ResultSet rs = st.executeQuery("Select page_id from page where page_namespace=0 and page_title=`" + from + "`");
            st.setString(1, from);
            ResultSet rs = st.executeQuery();

            if (rs.next()) {
                page = rs.getInt("page_id");
            } else {
                page = 0;
            }
            st.close();
            //c.close();
            return page;
        } catch (SQLException ex) {
           // Logger.getLogger(PathFinder.class.getName()).log(Level.SEVERE, null, ex);
        }finally{return page;}
    }

    protected Integer getCatPageId(String from) throws ClassNotFoundException {
        int page = 0;
        try {
            Connection c = WikipediaConnector.getConnection();
            Statement st = c.createStatement();
            //System.out.println("Select page_id from page where page_namespace=14 and page_title=\"" + from + "\"");
            ResultSet rs = st.executeQuery("Select page_id from page where page_namespace=14 and page_title=\"" + from + "\"");

            if (rs.next()) {
                page = rs.getInt("page_id");
            } else {
                page = 0;
            }
            st.close();
            //c.close();
            return page;
        } catch (SQLException ex) {
           // System.out.println("Error para obtener el id de "+from);
           // Logger.getLogger(PathFinder.class.getName()).log(Level.SEVERE, from, ex);
        } finally {
            return page;
        }
       // System.out.println("Luego del catch de error"+from);
    }

    public String getReason() {
        return this.reason;
    }

    //----------------------------WITH CATEGORIES --------------------
    /**
     * Returns all normalized paths from a cityPage to a personPage using categories. 1st called method.
     * @param fromPage
     * @param toPage
     * @throws ClassNotFoundException
     * @throws SQLException 
     */
    public List<List<String>> getPathsUsingCategories(String fromPage, String toPage) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        List<String> categories = this.getCategoriesFromPage(fromPage);
        List<String> listOf = this.getListOfFrom(fromPage);
        List<String> current = new ArrayList<String>();
        current.add("#from");
        List<List<String>> allPaths = new ArrayList<List<String>>();
        List<String> direct = new ArrayList<String>();
       
        direct.add("#from");
        direct.add("#to");

        if(this.areDirectLinked(fromPage, toPage)){
            allPaths.add(direct);
        }
        if(this.catIterationsLevel>1){
        for (String category : categories) {
            current.add("Cat:"+this.normalizeCategory(category, fromPage,toPage));
            this.getPathUsingCategories(category,fromPage, toPage, current, allPaths);
            current.remove(current.size() - 1);
        }
        
        for(String listOfpage:listOf){
        	if(this.areDirectLinked(listOfpage, toPage)){
        		String listPageName = this.normalizeCategory(listOfpage, fromPage, toPage);
        		List<String> pathListOf = new ArrayList<String>();
        		pathListOf.add("#from"); pathListOf.add(listPageName); pathListOf.add("#to");
        		allPaths.add(pathListOf);
        	}
        }
        
        }
        
        

        this.specificPaths = allPaths;
        return this.specificPaths;
    }

     List<String> getListOfFrom(String fromPage) {
		List<String> results = new ArrayList<String>();
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
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return results;
	}

	/**
     * Return all the categories which pageId belongs. 2nd called method.
     * @param pageId
     * @return 
     */
    protected List<String> getCategoriesFromPage(String pageName) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        Integer pageId = this.getPageId(pageName);
        List<String> listCategories = new ArrayList<String>();

        Connection c = WikipediaConnector.getConnection();
        Statement st = c.createStatement();
        String query_text = "SELECT convert(cl_to using utf8) as cl_to FROM categorylinks c where cl_from=" + pageId + " and cl_type=\"page\"";
       
        ResultSet rs = st.executeQuery(query_text);

        while (rs.next()) {
            String catTo = rs.getString("cl_to");
            //InputStream stream = rs.getBinaryStream("cl_to");
            //String catTo = stream.toString();
            if (!BLACKLIST_CATEGORY.contains(catTo)) {
                listCategories.add(catTo);
            }
        }
        st.close();
       // c.close();
        return listCategories;
    }
    
    String getStringValue(byte[] varbinary){
        return  new String(varbinary);
    }

    /**
     * Returns a list of paths navigating through categories. 5th called method.
     * @param catId
     * @param personPageId 
     */
 private void getPathUsingCategories(String categoryName, String fromPage,  String toPage, List<String> currentPath, List<List<String>> allPaths) throws ClassNotFoundException, UnsupportedEncodingException {
        if (this.includesPage(categoryName, toPage)) {
            currentPath.add("#to");
            List<String> temporal = new ArrayList<String>();
            temporal.addAll(currentPath);
            allPaths.add(temporal);
            this.regularGeneratedPaths++; //all paths generated counter.
            currentPath.remove(currentPath.size() - 1);
        } 
        if (this.categoryPathIterations < this.catIterationsLevel-1) {
            List<String> subCategories = this.getSubCategories(categoryName);
            for (String subCategoryName : subCategories) {
                this.categoryPathIterations++;
                currentPath.add("Cat:"+this.normalizeCategory(subCategoryName, fromPage,toPage));
                this.getPathUsingCategories(subCategoryName,fromPage, toPage, currentPath, allPaths);
                currentPath.remove(currentPath.size() - 1);
                this.categoryPathIterations--;
            }
        }
    }

    /**
     * Returns true if categoryName includes personPageName as included page.
     * @param categoryPage
     * @param toPage
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException 
     */
    private boolean includesPage(String categoryPage, String toPage) throws ClassNotFoundException, UnsupportedEncodingException {
        try {
            List<String> categories = this.getCategoriesFromPage(toPage);

            return categories.contains(categoryPage);
        } catch (SQLException ex) {
           // Logger.getLogger(PathFinder.class.getName()).log(Level.SEVERE, catName+personPageName, ex);
        }
        return false;

    }

    /**
     * Returns a list of string with the subcategory names of a given category name
     * @param categoryName
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException 
     */
    private List<String> getSubCategories(String categoryName) throws ClassNotFoundException {
        Integer catPageId = this.getCatPageId(categoryName);
        List<String> listCategories = new ArrayList<String>();
        try {
            
          //  System.out.println("Gettin subcategories from: " + catName.toUpperCase());
            Connection c = WikipediaConnector.getConnection();
            Statement st = c.createStatement();
            String query_text = "SELECT convert(cl_from using utf8) as cl_from, convert(page_title using utf8) as page_title from categorylinks inner join page on cl_from=page_id and page.page_namespace=14 and cl_to=\"" + categoryName + "\"";
            // System.out.println(query_text);
           // System.out.println(query_text);

            ResultSet rs = st.executeQuery(query_text);

            while (rs.next()) {
            	//InputStream stream = rs.getBinaryStream("page_title");
            	String page_title = rs.getString("page_title");
            	listCategories.add(page_title);
                //byte[] varbinary = (byte[]) rs.getObject("page_title");
                //String page_title = this.getStringValue(varbinary);
                //listCategories.add(page_title);
            }
            st.close();
            //c.close();
            return listCategories;
        } catch (SQLException ex) {
            //System.out.println("SubCategories error para las de "+catName);
            //Logger.getLogger(PathFinder.class.getName()).log(Level.SEVERE, null, ex);
        }finally{return listCategories;}
    }

    /*
     * Returns a String with the name of the category in a normalized form. It delegates the responsibility to
     * the normalization strategy in normalizator. 4th called method.
     */
    protected String normalizeCategory(String subCategoryName, String fromCatName, String toCatName) {
        return this.normalizator.normalizeCategory(subCategoryName, fromCatName, toCatName);
    }

    /**
     * Return the number of relevantDocuments for the query path.
     * @param queryPathList
     * @return 
     */
    public int getRelevantDocuments(String pathQuery) throws ClassNotFoundException, SQLException, UnsupportedEncodingException{
        //String query = "SELECT V.path, up.page FROM V_Normalized V, UxV uv,U_page up where V.id=uv.u_from and uv.v_to=up.id and V.path=\""+pathQuery+"\"";
        String query = "SELECT distinct up.page FROM U_page up";
        String query_notfound = "SELECT v_from, u_to FROM NFPC";
        
        Connection dbresearch = WikipediaConnector.getResultsConnection();

        Statement st = dbresearch.createStatement();

        ResultSet normalizedPaths = st.executeQuery(query);

        while (normalizedPaths.next()) {            
            //String path = normalizedPaths.getString("path");
            String page = normalizedPaths.getString("page");

            String from = this.getFrom(page);
            String to = this.getTo(page);

            this.computeRelevantDocuments(pathQuery,from,to);
        }

        normalizedPaths = st.executeQuery(query_notfound);
        while (normalizedPaths.next()) {            
            //String path = normalizedPaths.getString("path");
            //String page = normalizedPaths.getString("page");

            String from = normalizedPaths.getString("v_from");
            String to = normalizedPaths.getString("u_to");

            this.computeRelevantDocuments(pathQuery,from,to);
        }

        st.close();  
        return 0;
    }
    
    /*
     * Algoritmo usado para calcular el recall y precision 
     */
    private void computeRelevantDocuments(String path, String from, String to) throws ClassNotFoundException, SQLException, UnsupportedEncodingException{
        if(this.isReachablePath(path,from,to)){
            //    System.out.println("Is reachable "+ path + " from " + from +" to " + to);
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
    
    private boolean pathReacheableCategories(String[] steps) throws ClassNotFoundException{
        for (int i = 0; i < steps.length-1; i++) {
            if(!this.getSubCategories(steps[i]).contains(steps[i+1])){
                return false;
            }           
        }
        return true;
    }
    
    private void writeRelevantPagesFromDirectLink(String from){
        try {
            int pageId = this.getPageId(from);
            Connection wikipedia = WikipediaConnector.getConnection();
            Statement st = wikipedia.createStatement();
            
            if(pageId != 0){
                Set<Integer> nodes = new HashSet<Integer>();
                nodes.add(pageId);
                List<Integer> res = this.getDirectNodesFrom(nodes);
                for (Integer integer : res) {
                    String query = "Select page_title from page where page_id="+integer;
                    System.out.println(query);
                    ResultSet rs = st.executeQuery(query);
                    while(rs.next()){
                      byte[] varbinary = (byte[]) rs.getObject("page_title");
                      String page_title = this.getStringValue(varbinary);
                    //String page_title = rs.getString("page_title");
                    this.saveRelatedPage(page_title);
                    }   
                }
            } else {
                System.out.println("from:" + from +" no tiene page id");
            }
            st.close();
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
                //String page_title = rs.getString("page_title");
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
        System.out.println("Saving ... " + pageTitle);
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
    
    private String getCategory(String path, String from, String to){
        
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
