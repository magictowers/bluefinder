package knn.clean;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import knn.Instance;
import knn.distance.SemanticPair;
import pia.BipartiteGraphGenerator;
import pia.PathIndex;
import strategies.IGeneralization;
import utils.FromToPair;
import utils.ProjectSetup;

import com.mysql.jdbc.exceptions.jdbc4.MySQLSyntaxErrorException;

import db.DBConnector;
import db.PropertiesFileIsNotFoundException;
import db.utils.DbResultMap;
import db.utils.ResultsDbInterface;
import db.utils.WikipediaDbInterface;
import dbpedia.similarityStrategies.ValueComparator;

public class BlueFinderPathsFinder {
	
	private KNN knn;
	private int k;
	private int maxRecomm;
    private boolean saveResults;
    private String tableName;
    private ResultsDbInterface resultsDb;
	private DBConnector connector;
	private ProjectSetup projectSetup;

	public BlueFinderPathsFinder(ProjectSetup projectSetup, DBConnector connector) throws SQLException, ClassNotFoundException, PropertiesFileIsNotFoundException {
		this.connector = connector;
		this.projectSetup = projectSetup;
        this.resultsDb = new ResultsDbInterface(projectSetup, connector);
        this.saveResults = false;
    }
	
	public BlueFinderPathsFinder(ProjectSetup projectSetup, DBConnector connector, KNN knn) throws SQLException, ClassNotFoundException, PropertiesFileIsNotFoundException {
        this(projectSetup, connector);
		this.knn = knn;
		this.k = 5;
		this.maxRecomm = 10000;
	}
	
	public BlueFinderPathsFinder(ProjectSetup projectSetup, DBConnector connector, KNN knn, int k, int maxRecomm) throws SQLException, ClassNotFoundException, PropertiesFileIsNotFoundException {
		this(projectSetup, connector, knn);
		this.k = k;
		this.maxRecomm = maxRecomm;
	}
	
	public BlueFinderPathsFinder(ProjectSetup projectSetup, DBConnector connector, KNN knn, int k, int maxRecomm, ProjectSetup setup) throws SQLException, ClassNotFoundException, PropertiesFileIsNotFoundException {
		this(projectSetup, connector, knn);
		this.k = k;
		this.maxRecomm = maxRecomm;
	}
	
	public int getK() {
		return k;
	}

	public void setK(int k) {
		this.k = k;
	}

	public int getMaxRecomm() {
		return maxRecomm;
	}

	public void setMaxRecomm(int maxRecomm) {
		this.maxRecomm = maxRecomm;
	}

    /**
     * @return the saveResults
     */
    public boolean hasToSaveResults() {
        return saveResults;
    }

    /**
     * @param saveResults the saveResults to set
     */
    public void setSaveResults(boolean saveResults) {
        this.saveResults = saveResults;
    }

    /**
     * @return the tableName
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * @param tableName the tableName to set
     */
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
    
    private void createResultTable() throws SQLException, ClassNotFoundException {
        this.getResultsDb().createResultTable(getTableName());
	}
    
    public void getEvaluation(ProjectSetup projectSetup, String scenarioName, int maxRecomms, int limit, int offset) throws ClassNotFoundException, SQLException, ClassCastException, PropertiesFileIsNotFoundException, InsufficentKException {
        this.setK(10);
        this.setMaxRecomm(10000);
        this.setTableName(scenarioName);
        if (this.hasToSaveResults()) {
            this.createResultTable();
        }
        List<DbResultMap> results;
        if (limit == 0) {
            results = this.getResultsDb().getNotFoundPaths();
        } else {
            results = this.getResultsDb().getNotFoundPaths(limit, offset);
        }
        for (DbResultMap result : results) {
            this.getEvaluation(projectSetup, result.getString("v_from"), result.getString("u_to"), result.getInteger("id"));
        }
    }

	public List<String> getEvaluation(ProjectSetup projectSetup, String object, String subject, Integer id) throws ClassNotFoundException, SQLException, PropertiesFileIsNotFoundException, InsufficentKException {
        long timeStart = System.currentTimeMillis();
		String relatedUFrom = "u_from=0 ";
		String relatedString = "";
        String transObject = object;
        String transSubject = subject;
        if (projectSetup.isTranslate()) {
            WikipediaDbInterface wikipediaDb = new WikipediaDbInterface(projectSetup, this.connector);
            transObject = wikipediaDb.getTranslatedPage(object);
            transSubject = wikipediaDb.getTranslatedPage(subject);
            transObject = transObject.replaceAll(" ", "_");
            transSubject = transSubject.replaceAll(" ", "_");
        }	
        SemanticPair disconnectedPair = new SemanticPair(transObject, transSubject, "type", getResultsDb().getResourceDBTypes(transObject), getResultsDb().getResourceDBTypes(transSubject), -1);

        List<Instance> kNearestNeighbors = this.knn.getKNearestNeighbors(k, disconnectedPair);

		List<String> knnResults = new ArrayList<String>();
		for (Instance neighbor : kNearestNeighbors) {
			relatedUFrom = relatedUFrom + "or u_from = " + neighbor.getId() + " ";
			relatedString = relatedString + "(" + neighbor.getDistance() + ") " + neighbor.getResource() + " ";

			Statement st = this.getResultsDb().getConnection().createStatement();
			String queryFixed = "SELECT v_to, count(v_to) suma,V.path from UxV, V_Normalized V where v_to=V.id and ("
					+ relatedUFrom + ") group by v_to order by suma desc";
			ResultSet paths = st.executeQuery(queryFixed);
			TreeMap<String, Integer> map = this.genericPath(projectSetup, paths, knnResults.size() + 1);
			knnResults.add(map.toString());
		}
        
        if (this.hasToSaveResults()) {     
            if (this.getTableName() == null || this.getTableName().isEmpty())
                this.setTableName(object + "_" + subject);
            PathIndex pathIndex = new BipartiteGraphGenerator(projectSetup,this.connector).getPathIndex();
            String insertSentence = "INSERT INTO `" + this.getTableName()
					+ "` (`resource`, `related_resources`,`1path`, `2path`, `3path`, `4path`, `5path`, `6path`, `7path`, `8path`, `9path`, `10path`,`time`, `relevantPaths`)"
					+ "VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )";
			PreparedStatement statementInsert = this.getResultsDb().getConnection().prepareStatement(insertSentence);
			String firstParam = FromToPair.concatPair(object, subject) + " " + id; 
			statementInsert.setString(1, firstParam);
			statementInsert.setString(2, relatedString);
			if(knnResults.size() < 10){
				throw new InsufficentKException();
			}
			int i = 3;
			for (String string : knnResults) {
				statementInsert.setString(i, string);
				i++;
			}
            
            int unwantedResultPath = 10 - this.getK(); // el K al final a la hora de guardar las cosas es K=K-1
                System.out.println("unwantedResultPath "  + unwantedResultPath);
            // Para que los otros X path tengan algo
            for (int j = unwantedResultPath; j > 0; j--) {
                statementInsert.setString(i, "");
                i++;
            }
			
			List<String> disconnectedPairPathQueries = pathIndex.getPathQueries(disconnectedPair.getSubject(), disconnectedPair.getObject());
			String relevantPathQueries = this.convertToString(disconnectedPairPathQueries);
			
            long timeEnd = System.currentTimeMillis();
			statementInsert.setLong(13, timeEnd - timeStart);
			statementInsert.setString(14, relevantPathQueries);
			statementInsert.executeUpdate();
        } else {
            System.out.printf("\tObject: %s - Subject: %s\n", object, subject);
            for (int i = 0; i < knnResults.size(); i++) {
                System.out.println((i + 1) + "path: " + knnResults.get(i));
            }
            System.out.println("\n");
        }
		return knnResults;
	}
    
    private String convertToString(List<String> disconnectedPairPathQueries) {
		String result = "";
		for (String pathQuery : disconnectedPairPathQueries) {
			result = result + " , " + pathQuery;
		}
		if(!result.equals("")) {
			result=result.substring(3);
		}
		return result;
	}
	
	protected TreeMap<String, Integer> genericPath(ProjectSetup projectSetup, ResultSet paths, int kValue) throws SQLException {
		HashMap<String, Integer> pathDictionary = new HashMap<String, Integer>();
		ValueComparator bvc = new ValueComparator(pathDictionary);
		TreeMap<String, Integer> sortedMap = new TreeMap<String, Integer>(bvc);
        IGeneralization cg = projectSetup.getGeneralizator();

		while (paths.next()) {
			String path = paths.getString("path");
			path = cg.generalizePathQuery(path);
			int suma = paths.getInt("suma");
			if ((!path.contains("Articles_") || path.contains("Articles_li�s"))
					&& !path.contains("All_Wikipedia_")
					&& !path.contains("Wikipedia_")
					&& !path.contains("Non-free")
					&& !path.contains("All_pages_")
					&& !path.contains("All_non")) {
				if (pathDictionary.get(path) == null) {
					if (suma == kValue) { // all the cases belongs to this path query
						suma = suma + 1000;
					}
				} else {
					suma += pathDictionary.get(path);
				}
				pathDictionary.put(path, suma);
			}
		}
		
		for (String path : pathDictionary.keySet()) {
			Integer suma = pathDictionary.get(path);
			sortedMap.put(path, suma);
		}
		
		if (sortedMap.size() > this.maxRecomm) {
			int cantPaths = 0;
			TreeMap<String, Integer> tmpSortedMap = new TreeMap<String, Integer>(bvc);
			while (cantPaths < this.maxRecomm) {
				Map.Entry<String, Integer> entry = sortedMap.pollFirstEntry();
				tmpSortedMap.put(entry.getKey(), entry.getValue());
				cantPaths++;
			}
			sortedMap = tmpSortedMap;
		}
		return sortedMap;
	}

	public static void main(String[] args) throws ClassNotFoundException, SQLException, PropertiesFileIsNotFoundException {
//		if (args.length < 3) {
//			System.out.println("Expected arguments: single <bool save to DB> <from> <to> <neighbour> [<max recommendations>]");
//			System.out.println("Expected arguments: complete <bool save to DB> <scenario name> <limit> [<offset>]");
//			System.exit(255);
//		}
        BlueFinderPathsFinder bfevaluation;
        String type = args[0];
        boolean save = Boolean.parseBoolean(args[1]);
        if ((args.length == 5 || args.length == 6) && type.equalsIgnoreCase("single")) {
            String subject = args[2];
            String object = args[3];
            int k;
            try {
                k = Integer.parseInt(args[4]);
                if (k > 11) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException ex) {			
                System.err.println("Invalid neighbour, set to default (5).");
                k = 5;
            }
            int maxRecomm = 100000;
            try {
                maxRecomm = Integer.parseInt(args[5]);
            } catch (NumberFormatException ex) {
                System.err.println("Invalid number of recommendations, set to default (all).");
            } catch (ArrayIndexOutOfBoundsException ex) {
                System.err.println("Number of recommendations was not provided, set to default (all).");
            }
            ProjectSetup setup = new ProjectSetup();
            bfevaluation = new BlueFinderPathsFinder(new KNN(setup), k, maxRecomm);
            bfevaluation.setSaveResults(save);
            List<String> knnResults = bfevaluation.getEvaluation(object, subject, Integer.getInteger("-1"));

            if (!save) {
                System.out.printf("Evaluation for the pair: %s , %s, k=%d, maxRecomm=%d\n", object, subject, k, maxRecomm);
                if (knnResults.isEmpty()) {
                    System.out.println("There are no recommendations.");
                }
                for (int i = 0; i < knnResults.size(); i++) {
                    System.out.println((i + 1) + "path: " + knnResults.get(i));
                }
            }
        } else if ((args.length == 4 || args.length == 5) && type.equalsIgnoreCase("complete")) {
            String scenarioName = args[2];
            int limit = Integer.parseInt(args[3]);
            int offset;
            try {
                offset = Integer.parseInt(args[4]);
            } catch (ArrayIndexOutOfBoundsException ex) {
                offset = 0;
            }
            KNN knn = new KNN(ProjectConfigurationReader.enhanceTable());
            BlueFinderPathsFinder bfe = new BlueFinderPathsFinder(knn);
            bfe.setSaveResults(save);

            bfe.getEvaluation(scenarioName, 11, limit, offset);
        } else {
			System.out.println("Expected arguments: single <bool save to DB> <from> <to> <neighbour> [<max recommendations>]");
			System.out.println("Expected arguments: complete <bool save to DB> <scenario name> <limit> [<offset>]");
            System.exit(255);
        }		
	}

    /**
     * @return the setup
     */
    public ProjectSetup getSetup() {
        return this.projectSetup;
    }

    /**
     * @param setup the setup to set
     */
    public void setSetup(ProjectSetup setup) {
        this.projectSetup = setup;
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
