package knn.clean;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import db.WikipediaConnector;
import db.utils.WikipediaDbInterface;
import dbpedia.similarityStrategies.ValueComparator;
import knn.Instance;
import knn.distance.SemanticPair;
import pia.PIAConfigurationBuilder;
import strategies.IGeneralization;
import strategies.LastCategoryGeneralization;
import utils.ProjectConfiguration;

public class BlueFinderRecommender {
	
	private KNN knn;
	private int k;
	private int maxRecomm;

	public BlueFinderRecommender() {}
	
	public BlueFinderRecommender(KNN knn) {
		this.knn = knn;
		this.k = 5;
		this.maxRecomm = 10000;
	}
	
	public BlueFinderRecommender(KNN knn, int k, int maxRecomm) {
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

	public List<String> getEvaluation(String object, String subject) throws ClassNotFoundException, SQLException {
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
		return knnResults;
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
			System.out.println("Expected arguments: <from> <to> <neighbour> [<max recommendations>]");
			System.exit(255);
		}
		String subject = args[0];
		String object = args[1];
		int k = 5;
		try {
			k = Integer.parseInt(args[2]);
			if (k > 11) {
				throw new NumberFormatException();
			}
		} catch (NumberFormatException ex) {			
			System.err.println("Invalid neighbour, set to default (5).");
			k = 5;
		}
		int maxRecomm = 100000;
		try {
			maxRecomm = Integer.parseInt(args[3]);
		} catch (NumberFormatException ex) {
			System.err.println("Invalid number of recommendations, set to default (all).");
		} catch (ArrayIndexOutOfBoundsException ex) {
			System.err.println("Number of recommendations was not provided, set to default (all).");
		}
		BlueFinderRecommender bfevaluation = new BlueFinderRecommender(new KNN(ProjectConfiguration.enhanceTable()), k, maxRecomm);
		List<String> knnResults = bfevaluation.getEvaluation(object,  subject);

		System.out.printf("Evaluation for the pair: %s , %s, k=%d, maxRecomm=%d\n", object, subject, k, maxRecomm);
		if (knnResults.size() == 0) {
			System.out.println("There are no recommendations.");
		}
		for (int i = 0; i < knnResults.size(); i++) {
			System.out.println((i + 1) + "path: " + knnResults.get(i));
		}
	}

}
