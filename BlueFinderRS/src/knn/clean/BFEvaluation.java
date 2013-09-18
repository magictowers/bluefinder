package knn.clean;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import db.WikipediaConnector;
import dbpedia.similarityStrategies.ValueComparator;
import knn.Instance;
import knn.distance.SemanticPair;
import strategies.LastCategoryGeneralization;

public class BFEvaluation {
	
	private KNN knn;
	
	public BFEvaluation() {}
	
	public BFEvaluation(KNN knn) {
		this.knn = knn;
	}

	public void getEvaluation(String object, String subject, int k, int maxRecomm) throws ClassNotFoundException, SQLException {
		String relatedUFrom = "u_from=0 ";
		String relatedString = "";

		SemanticPair disconnectedPair = new SemanticPair(object, subject, "type", WikipediaConnector.getResourceDBTypes(object), WikipediaConnector.getResourceDBTypes(subject), -1);
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
		System.out.printf("Evaluation for the pair: %s , %s, k=%d, maxRecomm=%d\n", object, subject, k, maxRecomm);
		if (maxRecomm < knnResults.size()) {
			knnResults = knnResults.subList(0, maxRecomm);
		}
		System.out.println(knnResults);		
	}
	
	protected TreeMap<String, Integer> genericPath(ResultSet paths, int kValue) throws SQLException {
		HashMap<String, Integer> pathDictionary = new HashMap<String, Integer>();
		ValueComparator bvc = new ValueComparator(pathDictionary);
		TreeMap<String, Integer> sortedMap = new TreeMap<String, Integer>(bvc);
		LastCategoryGeneralization cg = new LastCategoryGeneralization();

		while (paths.next()) {
			// SELECT v_to, count(v_to) suma,V.path from UxV, V_Normalized V
			String path = paths.getString("path");
			path = cg.generalizePathQuery(path);
			int suma = paths.getInt("suma");
			//
			if ((!path.contains("Articles_") || path.contains("Articles_li�s"))
					&& !path.contains("All_Wikipedia_")
					&& !path.contains("Wikipedia_")
					&& !path.contains("Non-free")
					&& !path.contains("All_pages_")
					&& !path.contains("All_non")) {
				if (pathDictionary.get(path) == null) {
					if (suma == kValue) { // all the cases belongs to this path
											// query
						suma = suma + 1000;
					}
					pathDictionary.put(path, suma);
				} else {
					suma += pathDictionary.get(path);
					pathDictionary.put(path, suma);
				}
			}
		}
		for (String path : pathDictionary.keySet()) {
			Integer suma = pathDictionary.get(path);
			sortedMap.put(path, suma);
		}
		return sortedMap;
	}

	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		if (args.length < 3) {
			System.out.println("Expected arguments: <from> <to> <neighbour> [<max recommendations>]");
			System.exit(255);
		}
		String object = args[0];
//		object = "Bloodletting_(The_Walking_Dead)";
		String subject = args[1];
//		subject = "Maggie_Greene";
		int k = 5;
		try {
			k = Integer.parseInt(args[2]);
			if (k > 10) {
				throw new NumberFormatException();
			}
		} catch (NumberFormatException ex) {
			System.err.println("Invalid neighbour, set to default (5).");
		}
		int maxRecomm = 100000;
		try {
			maxRecomm = Integer.parseInt(args[3]);
		} catch (NumberFormatException ex) {
			System.err.println("Invalid number of recommendations, set to default (all).");
		} catch (ArrayIndexOutOfBoundsException ex) {
			System.err.println("Number of recommendations was not provided, set to default (all).");
		}
		BFEvaluation bfevaluation = new BFEvaluation(new KNN(false));
		bfevaluation.getEvaluation(object,  subject,  k, maxRecomm);
	}

}
