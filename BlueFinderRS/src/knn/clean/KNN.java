package knn.clean;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import knn.Instance;
import knn.InstanceComparator;
import knn.distance.JaccardDistanceCalculator;
import knn.distance.SemanticPair;
import db.WikipediaConnector;

public class KNN {

	private List<Instance> neighbors;
	private ResultSet rs;

	public KNN() throws ClassNotFoundException, SQLException {
		this.neighbors = new ArrayList<Instance>();
		Connection con = WikipediaConnector.getResultsConnection();
		Statement statement = con.createStatement(
				ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		statement.execute("SELECT * FROM U_page");
		this.rs = statement.getResultSet();

	}

	public List<Instance> getKNearestNeighbors(int k,
			SemanticPair instanceToCompare) throws ClassNotFoundException,
			SQLException {
		this.neighbors.clear();
		this.rs.beforeFirst();
		JaccardDistanceCalculator function = new JaccardDistanceCalculator();

		while (rs.next()) {

			SemanticPair connectedPair = this.generateSemanticPair(
					rs.getString("page"), rs.getLong("id"));
			double distance = function.distance(instanceToCompare,
					connectedPair);

			Instance instance = new Instance(connectedPair, distance);
			this.neighbors.add(instance);
			Collections.sort(this.neighbors, new InstanceComparator());
			if (this.neighbors.size() > k + 1) {
				this.neighbors.remove(this.neighbors.size() - 1);
			}
		}

		List<Instance> result = new ArrayList<Instance>();
		for (Iterator<Instance> iterator = this.neighbors.iterator(); (iterator
				.hasNext());) {
			Instance instance = iterator.next();
			result.add(instance);
			// System.out.println(instance.getDistance() + " - " +
			// instance.getResource() + " - " + instance.getId());
		}

		return result;
	}

	public SemanticPair generateSemanticPair(String string, long id)
			throws SQLException, ClassNotFoundException {
		String[] values = string.split(" ");
		String subject = values[0];
		String object = values[2];

		List<String> objectTypes = WikipediaConnector
				.getResourceDBTypes(object);
		List<String> subjectTypes = WikipediaConnector
				.getResourceDBTypes(subject);

		SemanticPair result = new SemanticPair(object, subject, "",
				objectTypes, subjectTypes, id);

		return result;
	}

}
