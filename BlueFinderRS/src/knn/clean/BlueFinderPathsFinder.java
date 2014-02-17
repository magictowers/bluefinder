package knn.clean;

import com.mysql.jdbc.exceptions.jdbc4.MySQLSyntaxErrorException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import db.WikipediaConnector;
import db.utils.DbResultMap;
import db.utils.ResultsDbInterface;
import db.utils.WikipediaDbInterface;
import dbpedia.similarityStrategies.ValueComparator;
import java.sql.PreparedStatement;
import knn.Instance;
import knn.distance.SemanticPair;
import pia.BipartiteGraphGenerator;
import pia.PIAConfigurationBuilder;
import pia.PathIndex;
import strategies.IGeneralization;
import utils.FromToPair;
import utils.ProjectConfiguration;

public class BlueFinderPathsFinder {
	
	private KNN knn;
	private int k;
	private int maxRecomm;
    private boolean saveResults;
    private String tableName;
    private ResultsDbInterface resultsDb;

	public BlueFinderPathsFinder() {
        this.resultsDb = new ResultsDbInterface();
    }
	
	public BlueFinderPathsFinder(KNN knn) {
        this();
		this.knn = knn;
		this.k = 5;
		this.maxRecomm = 10000;
	}
	
	public BlueFinderPathsFinder(KNN knn, int k, int maxRecomm) {
		this(knn);
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
		String queryDrop = "DROP TABLE IF EXISTS `"+ this.getTableName() +"`";
		//String query = "CREATE TABLE `"+resultTableName+"` ( `id` int(11) NOT NULL AUTO_INCREMENT, `resource` BLOB, `1path` int(11) DEFAULT NULL, `1pC` int(11) DEFAULT NULL,  `2path` int(11) DEFAULT NULL, `2pC` int(11) DEFAULT NULL,   `3path` int(11) DEFAULT NULL,  `3pC` int(11) DEFAULT NULL,  `4path` int(11) DEFAULT NULL,  `4pC` int(11) DEFAULT NULL,  `5path` int(11) DEFAULT NULL,  `5pC` int(11) DEFAULT NULL,  `6path` int(11) DEFAULT NULL,  `6pC` int(11) DEFAULT NULL,  `7path` int(11) DEFAULT NULL,  `7pC` int(11) DEFAULT NULL,  `8path` int(11) DEFAULT NULL,  `8pC` int(11) DEFAULT NULL,  `9path` int(11) DEFAULT NULL,  `9pC` int(11) DEFAULT NULL,  `10path` int(11) DEFAULT NULL,  `10pC` int(11) DEFAULT NULL,  `resourcePaths` int(11) DEFAULT NULL,  PRIMARY KEY (`id`)) ENGINE=InnoDB DEFAULT CHARSET=utf8";
		String query = "CREATE TABLE `"+ this.getTableName() +"` (`id` int(11) NOT NULL AUTO_INCREMENT, `resource` blob, `related_resources` blob, `1path` text, `2path` text,`3path` text," +
		"`4path` text, `5path` text, `6path` text, `7path` text, `8path` text, `9path` text, `10path` text, `time` bigint(20) DEFAULT NULL, `relevantPaths` text, PRIMARY KEY (`id`)) ENGINE=InnoDB DEFAULT CHARSET=utf8";

		Statement statement = WikipediaConnector.getResultsConnection().createStatement();
		statement.executeUpdate(queryDrop);
		statement.close();
		
		statement = WikipediaConnector.getResultsConnection().createStatement();
		statement.executeUpdate(query);
		statement.close();		
	}
    
    public void getEvaluation(String scenarioName, int maxRecomms, int limit) throws ClassNotFoundException, SQLException {
        this.setTableName(scenarioName);
        if (this.hasToSaveResults()) {
            this.createResultTable();
        }
        List<DbResultMap> results = this.resultsDb.getNotFoundPaths();
        for (DbResultMap result : results) {
            this.getEvaluation(result.getString("v_from"), result.getString("u_to"), result.getInteger("id"));
        }
    }

	public List<String> getEvaluation(String object, String subject, Integer id) throws ClassNotFoundException, SQLException {
        long timeStart = System.currentTimeMillis();
		String relatedUFrom = "u_from=0 ";
		String relatedString = "";
        String transObject = object;
        String transSubject = subject;
        if (ProjectConfiguration.translate()) {
            WikipediaDbInterface wikipediaDb = new WikipediaDbInterface();
            transObject = wikipediaDb.getTranslatedPage(object);
            transSubject = wikipediaDb.getTranslatedPage(subject);
            transObject = transObject.replaceAll(" ", "_");
            transSubject = transSubject.replaceAll(" ", "_");
            
            System.out.println(object);
            System.out.println(WikipediaConnector.getResourceDBTypes(object));
            System.out.println(transObject);
            System.out.println(WikipediaConnector.getResourceDBTypes(transObject));
            System.out.println(subject);
            System.out.println(transSubject);
        }
		SemanticPair disconnectedPair = new SemanticPair(object, subject, "type", WikipediaConnector.getResourceDBTypes(transObject), WikipediaConnector.getResourceDBTypes(transSubject), -1);

        List<Instance> kNearestNeighbors = this.knn.getKNearestNeighbors(k, disconnectedPair);
		SemanticPairInstance disconnectedInstance = new SemanticPairInstance(0, disconnectedPair);
		kNearestNeighbors.remove(disconnectedInstance);

		List<String> knnResults = new ArrayList<String>();
		for (Instance neighbor : kNearestNeighbors) {
			relatedUFrom = relatedUFrom + "or u_from = " + neighbor.getId() + " ";
			relatedString = relatedString + "(" + neighbor.getDistance() + ") " + neighbor.getResource() + " ";

			Statement st = WikipediaConnector.getResultsConnection().createStatement();
			String queryFixed = "SELECT v_to, count(v_to) suma,V.path from UxV, V_Normalized V where v_to=V.id and ("
					+ relatedUFrom + ") group by v_to order by suma desc";
			ResultSet paths = st.executeQuery(queryFixed);
			TreeMap<String, Integer> map = this.genericPath(paths, knnResults.size() + 1);
			knnResults.add(map.toString());
		}
        if (this.getTableName() == null || this.getTableName().isEmpty())
            this.setTableName(object + "_" + subject);
        
        if (this.hasToSaveResults()) {     
            PathIndex pathIndex = new BipartiteGraphGenerator().getPathIndex();
            String insertSentence = "INSERT INTO `" + this.getTableName()
					+ "` (`resource`, `related_resources`,`1path`, `2path`, `3path`, `4path`, `5path`, `6path`, `7path`, `8path`, `9path`, `10path`,`time`, `relevantPaths`)"
					+ "VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )";
			PreparedStatement statementInsert = WikipediaConnector.getResultsConnection()
					.prepareStatement(insertSentence);
			String firstParam = FromToPair.concatPair(object, subject) + " " + id; 
			statementInsert.setString(1, firstParam);
			statementInsert.setString(2, relatedString);
			int i = 3;
			for (String string : knnResults) {
				statementInsert.setString(i, string);
				i++;
			}
			
			List<String> disconnectedPairPathQueries = pathIndex.getPathQueries(disconnectedPair.getSubject(), disconnectedPair.getObject());
			String relevantPathQueries = this.convertToString(disconnectedPairPathQueries);
			
            long timeEnd = System.currentTimeMillis();
			statementInsert.setLong(13, timeEnd - timeStart);
			statementInsert.setString(14, relevantPathQueries);
			try {
				statementInsert.executeUpdate();
			} catch (MySQLSyntaxErrorException ex) {
				System.out.println("Error while executing the statement...");
			}
        } else {
            for (int i = 0; i < knnResults.size(); i++) {
                System.out.println((i + 1) + "path: " + knnResults.get(i));
            }
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
	
	protected TreeMap<String, Integer> genericPath(ResultSet paths, int kValue) throws SQLException {
		HashMap<String, Integer> pathDictionary = new HashMap<String, Integer>();
		ValueComparator bvc = new ValueComparator(pathDictionary);
		TreeMap<String, Integer> sortedMap = new TreeMap<String, Integer>(bvc);
        IGeneralization cg = PIAConfigurationBuilder.getGeneralizator();

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

	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		if (args.length < 3) {
			System.out.println("Expected arguments: single <bool save to DB> <from> <to> <neighbour> [<max recommendations>]");
			System.out.println("Expected arguments: complete <bool save to DB> <scenario name> <proportion of experiment>");
			System.exit(255);
		}
        BlueFinderPathsFinder bfevaluation;
        String type = args[0];
        boolean save = Boolean.getBoolean(args[1]);
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
            bfevaluation = new BlueFinderPathsFinder(new KNN(false), k, maxRecomm);
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
        } else if (args.length == 4 && type.equalsIgnoreCase("complete")) {
            String scenarioName;
            int proportion;
            try {
                scenarioName = args[2];
                proportion = Integer.parseInt(args[3]);
            } catch (ArrayIndexOutOfBoundsException ex) {
                scenarioName = "sc1Evaluation";
                proportion = 3;
            }
            if (ProjectConfiguration.enhanceTable()) {
                System.out.println("It was true!");
            } else {
                System.out.println("It was false!");
            }
            KNN knn = new KNN(ProjectConfiguration.enhanceTable());
            BlueFinderPathsFinder bfe = new BlueFinderPathsFinder(knn);
            bfe.setSaveResults(save);

            bfe.getEvaluation(scenarioName, 11, proportion);
        } else {
			System.out.println("Expected arguments: single <bool save to DB> <from> <to> <neighbour> [<max recommendations>]");
			System.out.println("Expected arguments: complete <bool save to DB> <scenario name> <proportion of experiment>");
            System.exit(255);
        }		
	}

}
