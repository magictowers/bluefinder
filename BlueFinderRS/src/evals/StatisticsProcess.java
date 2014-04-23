package evals;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import db.WikipediaConnector;


public class StatisticsProcess {
	
	

		

	
	public void printColumn(String columnName) throws ClassNotFoundException, SQLException {
		double[][] results = new double[10][5];
		//Connection con = StatisticsProcess.getConnection();
		Connection con = WikipediaConnector.getResultsConnection();
		int[] limits = {1,3,5,0};
		
		//System.out.println("Processing "+columnName);
		
		String query = "SELECT AVG(`"+columnName+"`) as k from particularStatistics where general_id=1 and `limit`=? and kValue=?";
		try {
			
			for (int l = 0; l < limits.length; l++) {
				
			  for (int k = 1; k < 11; k++) {
				  PreparedStatement ps = con.prepareStatement(query);
				ps.setInt(1, limits[l]);
				  ps.setInt(2, k);
		//		  System.out.println(query+" limit ="+ l + " k="+k);
				  ResultSet rs = ps.executeQuery();
				  rs.next();
				  results[k-1][l] = rs.getDouble("k");
				  ps.close();
				}
				
			}
			System.out.println(columnName);
			for (int l = 0; l < limits.length; l++) {
				System.out.print(limits[l]);
				for (int k = 0; k < 10; k++) {
					System.out.print(","+results[k][l]);
				}
				System.out.println();
			}
			
			
		} catch (SQLException e) {
			System.out.println("SE ROMPIO EN LA OBTENCION DE RECALL");
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
	}
	
	
public static void main(String[] args) throws ClassNotFoundException, SQLException {
		
		
		StatisticsProcess sp = new StatisticsProcess();
		sp.printColumn("recall");
		sp.printColumn("precision");
		sp.printColumn("f1");
		sp.printColumn("hit_rate");
		
	}

}
