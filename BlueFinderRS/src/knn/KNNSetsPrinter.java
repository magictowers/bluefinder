package knn;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import db.MysqlIndexConnection;

public class KNNSetsPrinter {
	public static void main(String[] args) throws ClassNotFoundException,
			SQLException {
		String piaIndexBase;
		String typesTable;

		if (args.length != 3) {
			System.out.println("Wrong number of params.");
			System.out.println("<piaIndexBase> <typesTable> <kValue>");
			System.out.println("name of the piaIndexBase in localhost.");
			System.out.println("Name of table with resource types in piaIndexBase");
			System.exit(1);
		}

		piaIndexBase = args[0];
		typesTable = args[1];
		int kValue = Integer.parseInt(args[2]);

		KNN knn = new KNN(piaIndexBase, typesTable);
		Connection connection = MysqlIndexConnection
				.getConnection(piaIndexBase);
		Statement statement = connection.createStatement();
		ResultSet resultSet = statement.executeQuery("select * from "
				+ typesTable + " where path_query='?'");
		while (resultSet.next()) {
			Instance instance = new Instance(0,
					resultSet.getString("resource"),
					resultSet.getString("types"), 0);
			System.out.println("Resource: " + resultSet.getString("resource"));
			List<Instance> result =knn.compute(kValue, instance);
			for (Instance instance2 : result) {
				System.out.println("D="+instance2.getDistance()+" "+instance2.getResource()+" id:"+instance2.getId());
			}
			System.out.println("----------------------------------------------\n");

		}

	}

}
