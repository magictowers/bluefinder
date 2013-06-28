/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pia;

import db.WikipediaConnector;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.sql.Connection;
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

/**
 *
 * @author dtorres
 */
public class PathFinder {

    private int from;
    private int to;
    private Set<Integer> visited;
    private Set<Integer> newPaths;
    private int iterations;
    private String reason = "";
    private List<List<String>> specificPaths;
    private int categoryPathIterations;
    private int catIterationsLevel = 5;
    private int regularGeneratedPaths = 0;
    private List<String> analysedPathQueryRetrieved; 
    private INormalizator normalizator;

    public PathFinder() {
        this.visited = new HashSet<Integer>();
        this.newPaths = new HashSet<Integer>();
        this.reason = "";
        this.specificPaths = new ArrayList<List<String>>();
        this.categoryPathIterations = 0;
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
    
    public boolean areDirectLinked(String domainTitle, String targetTitle) throws ClassNotFoundException, SQLException{
        boolean result = false;
        Integer domainId= this.getPageId(domainTitle);
        Integer targetId= this.getPageId(targetTitle);
        Set<Integer> setIds = new HashSet<Integer>();
        setIds.add(domainId);
        List<Integer> linkedPages = this.getDirectNodesFrom(setIds);
        result = linkedPages.contains(targetId);
        
        return result;
    }
    
    
    public boolean findPathBFS(String from, String to) throws ClassNotFoundException, SQLException {
        this.iterations = 0;
        this.reason = "";


        this.visited = new HashSet<Integer>();
        Set<Integer> initialSet = new HashSet<Integer>();

        Integer fromId = this.getPageId(from);
        Integer toId = this.getPageId(to);
        System.out.println(from + " pageId> " + fromId);
        System.out.println(to + " pageId> " + toId);
        initialSet.add(fromId);
        if (!((toId == 0) || (fromId == 0))) {
            return this.findPath(initialSet, toId);
        } else {
            this.reason = "Error fetching pages id: fromId=" + fromId + " toId=" + toId;
            return false;
        }


        //else
        // --- save the nodes
        // --- use the nodes
    }

    private boolean findPath(Set<Integer> pageIdsFrom, Integer pageIdTo) throws ClassNotFoundException, SQLException {
        System.out.println("Iteration number " + this.iterations);
        if (this.iterations == 3) {
            this.reason = "Limit iterations reached: ";
            return false;
        }
        List<Integer> resultNodes = this.getDirectNodesFrom(pageIdsFrom);
        System.out.println("PAGE ID TO " + pageIdTo);
        System.out.println(resultNodes);
        if (resultNodes.contains(pageIdTo)) {
            this.reason = "Found Path!";
            return true;
        }
        Set<Integer> toContinue = this.removeVisited(resultNodes);
        this.visited.addAll(toContinue);
        if (!toContinue.isEmpty()) {
            System.out.println("NOT Empty");
            this.iterations += 1;
            return this.findPath(toContinue, pageIdTo);
        }

        System.out.println("Es vacio");
        this.reason = "Path does not exists";
        return false;

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

    private Set<Integer> removeVisited(List<Integer> resultNodes) {
        Set<Integer> result = new HashSet<Integer>();
        for (Integer integer : resultNodes) {
            if (!this.visited.contains(integer)) {
                result.add(integer);
            }
        }
        return result;
    }

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

    /*
    public static void main(String[] args) throws UnsupportedEncodingException, ClassNotFoundException, SQLException {
    String uri = "Jos%C3%A9_Mar%C3%ADa_Robles_Hurtado";
    String decoded = URLDecoder.decode(uri, "UTF-8");
    PathFinder finder = new PathFinder();
    Connection conReserarch = WikipediaConnector.getResultsConnection();
    Statement st = conReserarch.createStatement();
    Statement st2 = conReserarch.createStatement();
    ResultSet resultSet = st.executeQuery("Select * from person_and_city");
    while (resultSet.next()) {
    String person = resultSet.getString("person");
    person = URLDecoder.decode(person, "UTF-8");
    String city = resultSet.getString("city");
    city = URLDecoder.decode(city, "UTF-8");
    city = city.substring(28);
    person = person.substring(28);
    finder.findSpecificPath(finder, person, city, st2);
    
    
    }
    st.close();
    conReserarch.close();
    
    System.out.println(decoded);
    }
    
     * 
     */
    private Integer getPageId(String from) throws ClassNotFoundException {
        int page = 0;
        try {
            
            Connection c = WikipediaConnector.getConnection();
            Statement st = c.createStatement();
          //  System.out.println("Select page_id from page where page_namespace=0 and page_title=\"" + from + "\"");
            ResultSet rs = st.executeQuery("Select page_id from page where page_namespace=0 and page_title=\"" + from + "\"");

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

    private Integer getCatPageId(String from) throws ClassNotFoundException {
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
        }finally {return page;}
       // System.out.println("Luego del catch de error"+from);
        
    }

    public String getReason() {
        return this.reason;
    }

    //----------------------------WITH CATEGORIES --------------------
    /**
     * Returns all normalized paths from a cityPage to a personPage using categories.
     * @param cityPage
     * @param personPage
     * @throws ClassNotFoundException
     * @throws SQLException 
     */
    public List<List<String>> getPathsUsingCategories(String cityPage, String personPage) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        List<String> categories = this.getCategoriesFromPage(cityPage);
        List<String> current = new ArrayList<String>();
        List<List<String>> allPaths = new ArrayList<List<String>>();
        
       List<String> direct = new ArrayList<String>();
       direct.add("[to]");

        if(this.areDirectLinked(cityPage, personPage)){
            allPaths.add(direct);
        }
        for (String category : categories) {
            current.add(this.normalizeCategory(category, cityPage,personPage));
            this.getPathUsingCategories(category,cityPage, personPage, current, allPaths);
            current.remove(current.size() - 1);
        }

        this.specificPaths = allPaths;
        return this.specificPaths;

    }

    /**
     * Return all the categories which pageId belongs.
     * @param pageId
     * @return 
     */
    private List<String> getCategoriesFromPage(String pageName) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        Integer pageId = this.getPageId(pageName);
        List<String> listCategories = new ArrayList<String>();

        Connection c = WikipediaConnector.getConnection();
        Statement st = c.createStatement();
        String query_text = "SELECT convert(cl_to using utf8) as cl_to FROM categorylinks c where cl_from=" + pageId + " and cl_type=\"page\"";
       // System.out.println(query_text);

        ResultSet rs = st.executeQuery(query_text);

        while (rs.next()) {
            String catTo = rs.getString("cL_to");
            //InputStream stream = rs.getBinaryStream("cl_to");
            //String catTo = stream.toString();
            if (!this.categoryBlackList().contains(catTo)) {
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
     * Returns a list of paths.
     * @param catId
     * @param personPageId 
     */
  /*  private void getPathUsingCategories(String catName, String originalCity,  String personPageName, List<String> currentPath, List<List<String>> allPaths) throws ClassNotFoundException, UnsupportedEncodingException {
        if (this.includesPage(catName, personPageName)) {
            currentPath.add("[to]");
            List<String> temporal = new ArrayList<String>();
            temporal.addAll(currentPath);
            allPaths.add(temporal);
            this.regularGeneratedPaths++; //all paths generated counter.
            currentPath.remove(currentPath.size() - 1);
        } else {
            if (this.categoryPathIterations < this.catIterationsLevel) {
                List<String> subCategories = this.getSubCategories(catName);
                for (String subCategoryName : subCategories) {
                    this.categoryPathIterations++;
                    currentPath.add(this.normalize(subCategoryName, originalCity,personPageName));
                    this.getPathUsingCategories(subCategoryName,originalCity, personPageName, currentPath, allPaths);
                    currentPath.remove(currentPath.size() - 1);
                    this.categoryPathIterations--;
                }
            }
        }*/
        
        private void getPathUsingCategories(String catName, String originalCity,  String personPageName, List<String> currentPath, List<List<String>> allPaths) throws ClassNotFoundException, UnsupportedEncodingException {
        if (this.includesPage(catName, personPageName)) {
            currentPath.add("[to]");
            List<String> temporal = new ArrayList<String>();
            temporal.addAll(currentPath);
            allPaths.add(temporal);
            this.regularGeneratedPaths++; //all paths generated counter.
            currentPath.remove(currentPath.size() - 1);
        } 
            if (this.categoryPathIterations < this.catIterationsLevel) {
                List<String> subCategories = this.getSubCategories(catName);
                for (String subCategoryName : subCategories) {
                    this.categoryPathIterations++;
                    currentPath.add(this.normalizeCategory(subCategoryName, originalCity,personPageName));
                    this.getPathUsingCategories(subCategoryName,originalCity, personPageName, currentPath, allPaths);
                    currentPath.remove(currentPath.size() - 1);
                    this.categoryPathIterations--;
                }
            }
        

        /**
         * catId-> ver si esta como pagina incluida.
         * catId-> getSubCats()
         * foreach subcat -> check pages
         */
    }

    /**
     * Returns true if catName includes personPageName as included page.
     * @param catName
     * @param personPageName
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException 
     */
    private boolean includesPage(String catName, String personPageName) throws ClassNotFoundException, UnsupportedEncodingException {
        try {
            List<String> categories = this.getCategoriesFromPage(personPageName);

            return categories.contains(catName);
        } catch (SQLException ex) {
           // Logger.getLogger(PathFinder.class.getName()).log(Level.SEVERE, catName+personPageName, ex);
        }
        return false;

    }

    /**
     * Returns a list of string with the subcategory names of a given category name
     * @param catName
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException 
     */
    private List<String> getSubCategories(String catName) throws ClassNotFoundException {
        Integer catPageId = this.getCatPageId(catName);
            List<String> listCategories = new ArrayList<String>();
        try {
            
          //  System.out.println("Gettin subcategories from: " + catName.toUpperCase());
            Connection c = WikipediaConnector.getConnection();
            Statement st = c.createStatement();
            String query_text = "SELECT convert(cl_from using utf8) as cl_from, convert(page_title using utf8) as page_title from categorylinks inner join page on cl_from=page_id and page.page_namespace=14 and cl_to=\"" + catName + "\"";
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

    protected String normalizeCategory(String subCategoryName, String fromCatName, String toCatName) {
      //  String normalized;
      //  normalized = subCategoryName.replaceAll(fromCatName, "[from]");
       // return normalized.replaceAll(toCatName, "[to]");
        return this.normalizator.normalizeCategory(subCategoryName, fromCatName, toCatName);
    }

    private List<String> categoryBlackList() {
        List<String> list = new ArrayList<String>();

        list.add("All_articles_needing_additional_references");
        list.add("All_articles_with_dead_external_links");
        list.add("All_articles_with_unsourced_statements");
        list.add("Articles_containing_explicitly_cited_English_language_text");
        list.add("Articles_needing_additional_references_from_December_2010");
        list.add("Articles_with_Spanish_language_external_links");
        list.add("Articles_with_dead_external_links_from_September_2010");
        list.add("Articles_containing_Latin_language_text");
        list.add("All_articles_needing_cleanup");
        list.add("Articles_with_dead_external_links_from_October_2010");
        list.add("Articles_with_unsourced_statements_from_May_2010");
        list.add("All_articles_to_be_expanded");


        return list;
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
    
    private void computeRelevantDocuments(String path, String from, String to) throws ClassNotFoundException, SQLException, UnsupportedEncodingException{
        
        if(this.isReachablePath(path,from,to)){
        //    System.out.println("Is reachable "+ path + " from " + from +" to " + to);
        String categoryName = this.getCategory(path,from, to);
        
        if(!this.analysedPathQueryRetrieved.contains(categoryName)){
        
        if(!categoryName.equals("")){
            this.analysedPathQueryRetrieved.add(categoryName);
            this.writeRelevantPagesFromCategory(categoryName);
        }else{
            
            this.analysedPathQueryRetrieved.add("DIEGO"+from);
            this.writeRelevantPagesFromDirectLink(from);
        }}}
    }
    
    
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
        
         
                 if(this.getCategoriesFromPage(from).contains(steps[0])){
                     
                     return this.pathReacheableCategories(steps);
                 }else return false;
            
            
        
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
            
            
            
            if(pageId!=0){
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
                 
            
            }else{
                System.out.println("from:" + from +" no tiene page id");}
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
           // System.out.println(query_text);

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
   
    
//    public static void main(String[] args) throws ClassNotFoundException, SQLException {
//        PathFinder finder = new PathFinder();
//        //System.out.println( finder.normalize("People_from_Nantes_Brasil", "Nantes_Brasil")) ;
//
//        //System.out.println(finder.getSubCategories("La_Plata"));
//        //System.out.println(finder.getCategoriesFromPage("Buenos_Aires"));
//        //System.out.println(finder.includesPage("Paris", "Paris"));
//        //finder.getPathsUsingCategories("La_Plata", "Estudiantes_de_La_Plata");
//        //System.out.println(finder.specificPaths);
//        System.out.println("From "+ finder.getFrom("(Kaunas,_Illinois,Emmanuel_Levinas)"));
//        System.out.println("To "+ finder.getTo("(Kaunas,_Illinois,Emmanuel_Levinas)"));
//        System.out.println(finder.getCategory("[from],_Illinois/People_from_[from],_Illinois/[to]", "La_Plata", "Diego"));
//    }
     
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
         
         
     }
}
