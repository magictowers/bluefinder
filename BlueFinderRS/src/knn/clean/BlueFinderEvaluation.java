package knn.clean;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import knn.Instance;
import db.MysqlIndexConnection;

/**This class compute the evaluation for one PathIndex. The evaluation is the one that is
 * included in the journal article.
 * 
 * @author dtorres
 *
 */

public class BlueFinderEvaluation {
	
	private KNN knn;
	
	public BlueFinderEvaluation(KNN knn){
		this.knn=knn;
	}
	
	
	
	private void processTest(String piaIndexBase, String typesTable,
			int kValue, int testRowsNumber, String resultTableName)
			throws ClassNotFoundException, SQLException {
		
		
		
		
		String relatedVTo = "v_to=0 ";
		String relatedString = "";
		while (resultSet.next()) {
			long time_start, time_end;
			time_start = System.currentTimeMillis();
			Instance instance = new Instance(0,
					resultSet.getString("resource"),
					resultSet.getString("types"), 0);
			// System.out.println("Resource: " +
			// resultSet.getString("resource"));
			
			List<Instance> result = this.getKnn().compute(kValue + 1, instance);
			result.remove(instance);
			List<String> knnResults = new ArrayList<String>();
			for (Instance instance2 : result) {
				relatedVTo = relatedVTo + "or v_to = " + instance2.getId()
						+ " ";
				relatedString = relatedString + "(" + instance2.getDistance()
						+ ") " + instance2.getResource() + " ";
				Statement st = connection.createStatement();
				String queryPaths = "SELECT u_from, count(u_from) suma,V.path from UxV, V_Normalized V where u_from=V.id and ("
						+ relatedVTo + ") group by u_from order by suma desc ";
				ResultSet paths = st.executeQuery(queryPaths);
				TreeMap<String, Integer> map = this.genericPath(paths);
				// for (String pathGen : map.keySet()) {
				// System.out.println(map);

				knnResults.add(map.toString());
				// System.out.println("end ---- k="+kvalue);
			
			}
			time_end = System.currentTimeMillis();
			// Insert statem
			String insertSentence = "INSERT INTO `dbresearch`.`"+ resultTableName + "` (`v_to`, `related_resources`,`1path`, `2path`, `3path`, `4path`, `5path`, `6path`, `7path`, `8path`, `9path`, `10path`,`time`)"
					+ "VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )";
			PreparedStatement st = connection.prepareStatement(insertSentence);
			st.setString(1, resultSet.getString("resource")+" "+resultSet.getInt("path_query"));
			st.setString(2, relatedString);
			int i = 3;
			for (String string : knnResults) {
				st.setString(i, string);
				i++;

			}
			st.setLong(13, time_end - time_start);
			st.executeUpdate();

			relatedVTo = "v_to=0 ";
			relatedString = "";
		}
	}
	

}
