package knn;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import db.MysqlIndexConnection;
/**
 * This class compute KNN using the pia index database and a table that includes
 * the list of elements to consider in the knn (related and non related).
 * @author dtorres
 *
 */

public class KNN {

	private List<Instance> neighbors;
	private ResultSet rs;

	public KNN(String dbName, String tableName) throws ClassNotFoundException, SQLException {
		this.neighbors = new ArrayList<Instance>();
		Connection con = MysqlIndexConnection.getConnection(dbName);
		Statement statement = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		statement.execute("SELECT * FROM " + tableName
				+ " where path_query<>'?'");
		this.rs = statement.getResultSet();

	}

	public List<Instance> compute(int k, Instance instanceToCompare) throws ClassNotFoundException,
			SQLException {
		this.neighbors.clear();
		this.rs.beforeFirst();
		//JaccardFunction function = new JaccardFunction();
		//JaccardFunction function = new PlusYagoJaccardFunction();
		JaccardFunction function = new JaccardFunctionOnlyDB();
		
		while (rs.next()) {

			float distance = function.distance(instanceToCompare.getTypes(),
					rs.getString("types"));
			Instance instance = new Instance(distance,
					rs.getString("resource"), rs.getString("types"),
					rs.getInt("path_query"));
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
			//System.out.println(instance.getDistance() + " - "	+ instance.getResource() + " - " + instance.getId());
		}

		return result;
	}

	
}
