package knn.clean;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import knn.Instance;
import knn.InstanceComparator;
import knn.distance.JaccardDistanceCalculator;
import knn.distance.SemanticPair;
import db.DBConnector;
import db.PropertiesFileIsNotFoundException;
import db.utils.ResultsDbInterface;
import db.utils.WikipediaDbInterface;
import utils.ProgressCounter;
import utils.ProjectSetup;

public class KNN {

	private List<Instance> neighbors;
	private ResultSet rs;
    private ResultsDbInterface resultsDb;
    private ProjectSetup projectSetup;
    private DBConnector connector;

	public KNN(ProjectSetup projectSetup,DBConnector connector) throws ClassNotFoundException, SQLException, PropertiesFileIsNotFoundException {
		this.connector = connector;
		this.projectSetup = projectSetup;
		this.neighbors = new ArrayList<Instance>();
        this.resultsDb = new ResultsDbInterface(projectSetup,connector);
		this.enhanceUPage();
		Connection con = this.resultsDb.getConnection();
		Statement statement = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		statement.execute("SELECT convert(page using utf8) as page, id, convert(subjectTypes using utf8) as subjectTypes, convert(objectTypes using utf8) as objectTypes FROM U_pageEnhanced");
		this.rs = statement.getResultSet();
	}

	public KNN(ProjectSetup projectSetup,DBConnector connector, boolean loadEnhancedUPage) throws ClassNotFoundException, SQLException, PropertiesFileIsNotFoundException {
		this.connector = connector;
        this.projectSetup = projectSetup;
		this.neighbors = new ArrayList<Instance>();
        resultsDb = new ResultsDbInterface(projectSetup, connector);
		if (loadEnhancedUPage) {
			this.enhanceUPage();
		}
		Connection con = resultsDb.getConnection();
		Statement statement = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		statement.execute("SELECT convert(page using utf8) as page, id, convert(subjectTypes using utf8) as subjectTypes, convert(objectTypes using utf8) as objectTypes FROM U_pageEnhanced");
		this.rs = statement.getResultSet();
	}
    
    public KNN(ProjectSetup projectSetup,DBConnector connector, ResultsDbInterface resultsDb) throws SQLException, ClassNotFoundException, PropertiesFileIsNotFoundException {
		this.connector = connector;
        this.projectSetup = projectSetup;
		this.neighbors = new ArrayList<Instance>();
        this.resultsDb = resultsDb;
        if (this.projectSetup.hasToCreateEnhancedTable()) {
            this.enhanceUPage();
        }
		Connection con = resultsDb.getConnection();
		Statement statement = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		statement.execute("SELECT convert(page using utf8) as page, id, convert(subjectTypes using utf8) as subjectTypes, convert(objectTypes using utf8) as objectTypes FROM U_pageEnhanced");
		this.rs = statement.getResultSet();
    }

	public List<Instance> getKNearestNeighbors(int k, SemanticPair instanceToCompare) 
			throws ClassNotFoundException, SQLException {
		this.neighbors.clear();
		this.rs.beforeFirst();
		JaccardDistanceCalculator function = new JaccardDistanceCalculator();
				
		while (rs.next()) {
			SemanticPair connectedPair = this.generateSemanticPair(rs.getString("page"), rs.getLong("id"), 
			rs.getString("subjectTypes"), rs.getString("objectTypes"));
			double distance = function.distance(instanceToCompare, connectedPair);
			
			
			Instance instance = new Instance(connectedPair, distance);
			
			//avoid to insert the instanceToCompare as neighbor.
			if(!(instanceToCompare.getObject().equalsIgnoreCase(connectedPair.getObject()) && instanceToCompare.getSubject().equalsIgnoreCase(connectedPair.getSubject()))  ){
                this.neighbors.add(instance);
			}
			Collections.sort(this.neighbors, new InstanceComparator());
			if (this.neighbors.size() > k) {
				this.neighbors.remove(this.neighbors.size() - 1);
			}
		}

		List<Instance> result = new ArrayList<Instance>();
		for (Iterator<Instance> iterator = this.neighbors.iterator(); iterator.hasNext(); ) {
			Instance instance = iterator.next();
			result.add(instance);
		}

		return result;
	}

	public SemanticPair generateSemanticPair(String string, long id)
			throws SQLException, ClassNotFoundException {
		String[] values = string.split(" ");
		String subject = values[0];
		String object = values[2];

		List<String> objectTypes = getResultsDb().getResourceDBTypes(object);
		List<String> subjectTypes = getResultsDb().getResourceDBTypes(subject);

		SemanticPair result = new SemanticPair(object, subject, "", objectTypes, subjectTypes, id);

		return result;
	}
	
	public SemanticPair generateSemanticPair(String string, long id, String subjectTypes, String objectTypes) {
		String[] values = string.split(" ");
		String subject = values[0];
		String object = values[2];
		String[] objectTypesArray = objectTypes.split(" ");
		List<String> objectTypesList = Arrays.asList(objectTypesArray);
		
		String[] subjectTypesArray = subjectTypes.split(" ");
		List<String> subjectTypesList = Arrays.asList(subjectTypesArray); 
		
		SemanticPair result = new SemanticPair(object, subject, "type", objectTypesList, subjectTypesList, id);
		return result;
		
	}
	
	public void enhanceUPage() throws ClassNotFoundException, SQLException, PropertiesFileIsNotFoundException {
        System.out.println("Page enhancement...\n");
		Connection resultsConnection = getResultsDb().getConnection();
		resultsConnection.createStatement().executeUpdate("DROP TABLE IF EXISTS `U_pageEnhanced`");
		resultsConnection.createStatement().executeUpdate("CREATE  TABLE `U_pageEnhanced` (`id` INT NOT NULL , `page` BLOB NOT NULL , `subjectTypes` BLOB NOT NULL , `objectTypes` BLOB NOT NULL , PRIMARY KEY (`id`))");
		
		String queryInsert = "INSERT INTO `U_pageEnhanced`(`id`,`page`,`subjectTypes`,`objectTypes`) VALUES "+
						"(?,?,?,?)";
		
		ResultSet rs = getResultsDb().getConnection().createStatement().executeQuery("select convert(page using utf8) as page, id from `U_page`");
        ProgressCounter progressCounter = new ProgressCounter();
		while(rs.next()){
			String string=rs.getString("page");
			String[] values = string.split(" ");
			String subject = values[0];
			String object = values[2];
            String transObject = object;
            String transSubject = subject;
            if (projectSetup.hasToTranslate()) {
            	System.out.println("Translate ");
                WikipediaDbInterface wikipediaDb = new WikipediaDbInterface(this.projectSetup,this.connector);
                transObject = wikipediaDb.getTranslatedPage(object);
                transSubject = wikipediaDb.getTranslatedPage(subject);
                transObject = transObject.replaceAll(" ", "_");
                transSubject = transSubject.replaceAll(" ", "_");
                
            }            

			List<String> objectTypes = getResultsDb().getResourceDBTypes(transObject);
			List<String> subjectTypes = getResultsDb().getResourceDBTypes(transSubject);

			SemanticPair result = new SemanticPair(object, subject, "", objectTypes, subjectTypes, rs.getLong("id"));
			
			PreparedStatement statement = getResultsDb().getConnection().prepareStatement(queryInsert);
			statement.setLong(1, result.getId());
			statement.setString(2, string);
			String subjectT="";
			for (String type : result.getSubjectElementsBySemProperty("type")) {
				subjectT=subjectT+" "+type;
			}
			if(!subjectT.equals("")) {
				subjectT = subjectT.trim();
			}
			
			statement.setString(3, subjectT);
			String objectT = "";
			for (String string2 : result.getObjectElementsBySemProperty("type")) {
				objectT = objectT + " " + string2;
			}
			if(!objectT.equals("")) {
				objectT = objectT.trim();
			}
			
			statement.setString(4, objectT);			
			statement.executeUpdate();
			statement.close();	
            progressCounter.increment();
		}
        System.out.println("\nFinished page enhancement.");
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
