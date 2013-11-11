package utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import strategies.LastCategoryGeneralization;

import knn.clean.Statistics;

import db.WikipediaConnector;

public class ConfidenceAnalyzer {
	
	private String resultsTableName;
	private String confidenceTableName;
	
	public ConfidenceAnalyzer(String resultsTableName, String confidenceTableName) throws ClassNotFoundException, SQLException{
		this.resultsTableName=resultsTableName;
		this.confidenceTableName=confidenceTableName;
		this.createConfidenceTable(this.confidenceTableName);
	}
	
	public void computeStatistics(int kValue, int maxRecomm) throws ClassNotFoundException, SQLException{
		Connection con = WikipediaConnector.getResultsConnection();
		PreparedStatement statement = con.prepareStatement("select "+kValue+"path, convert(resource using utf8) as resource, relevantPaths from `"+this.resultsTableName+"` ");
		ResultSet rs = statement.executeQuery();
		Statistics statistics = new Statistics();
		
		PathsResolver decoupler = new PathsResolver(", ");
		while(rs.next()){
			String paths = rs.getString(kValue+"path");
			String resource = rs.getString("resource");
			String relevantPaths = rs.getString("relevantPaths");
			
			Map<String, Integer> decoupledPaths = decoupler.decouple(paths);
			
			List<String> decoupled = decoupler.simpleDecoupledPaths(paths);
			if (maxRecomm >= 0 && decoupled.size() > maxRecomm) {
				decoupled = decoupled.subList(0, maxRecomm);
			}
			Set<String> relevants = statistics.getSetOfRelevantPathQueries(relevantPaths);
			Set<String> starRelevant = new HashSet<String>();
			LastCategoryGeneralization cg = new LastCategoryGeneralization();

			for (String path : relevants) {
				starRelevant.add(cg.generalizePathQuery(path));
			}
			
			relevants=starRelevant;
			
			for (String dPath : decoupled) {
				int hit = 0;
				if(relevants.contains(dPath)){
					hit=hit+1;
				}
				this.insertIntoConfidenceTable(resource,dPath,decoupledPaths.get(dPath),decoupled.indexOf(dPath),hit);
			}
			
			
		}
		
		
		
		
	}
	
	private void insertIntoConfidenceTable(String resource, String dPath,
			Integer confidence, int position, int hit) throws ClassNotFoundException, SQLException {
		
		Connection con = WikipediaConnector.getResultsConnection();
		PreparedStatement st = con.prepareStatement("INSERT INTO `"+this.confidenceTableName+"` (`pair`, `path`, `confidence`,`position`,`hit`) VALUES (?,?,?,?,?)");
		st.setString(1, resource);
		st.setString(2, dPath);
		st.setInt(3, confidence);
		st.setInt(4, position);
		st.setInt(5, hit);
		st.executeUpdate();
		st.close();
		
		
		
	}

	public void createConfidenceTable(String ptTable) throws ClassNotFoundException, SQLException{
		Connection conn = WikipediaConnector.getResultsConnection();
		conn.createStatement().executeUpdate("DROP TABLE IF EXISTS `"+ptTable+"`");
		conn.createStatement().executeUpdate(
				"CREATE TABLE `"+ptTable+"` ("
				+ "`id` int(3) NOT NULL AUTO_INCREMENT,"
				+ "`pair` MEDIUMTEXT CHARACTER SET 'utf8' NOT NULL,"
				+ "`path` MEDIUMTEXT CHARACTER SET 'utf8' NOT NULL,"
				+ "`confidence` INT NOT NULL ,"
				+ "`position` INT NOT NULL,"
				+ "`hit` INT NOT NULL ,"
				+ "PRIMARY KEY (`id`)"
				+ ") ENGINE=MyISAM AUTO_INCREMENT=1 DEFAULT CHARSET=utf8"
		);
		
		conn.setAutoCommit(false);
		
	}
	
	protected double highConfidenceProportion() throws SQLException, ClassNotFoundException{
		Connection con = WikipediaConnector.getResultsConnection();
		Statement st = con.createStatement();
		Statement confHit = con.createStatement();
		ResultSet cantHC = st.executeQuery("SELECT count(*) as cant FROM "+this.confidenceTableName+" where confidence > 999");
		ResultSet cantHCHit = confHit.executeQuery("SELECT count(*) as cant FROM "+this.confidenceTableName+" where confidence > 999 and hit=1");
		cantHCHit.first();
		cantHC.first();
		double cantHits = cantHCHit.getDouble("cant");
		double cantTotal = cantHC.getDouble("cant");
		confHit.close();
		confHit.close();
		
		return cantHits / cantTotal;

	}
	
	protected double positionConfidenceProportion(int position) throws SQLException, ClassNotFoundException{
		Connection con = WikipediaConnector.getResultsConnection();
		int position2 = position-1;
		Statement st = con.createStatement();
		Statement confHit = con.createStatement();
		ResultSet cantHC = st.executeQuery("SELECT count(*) as cant FROM "+this.confidenceTableName+" where position = "+position2+"");
		ResultSet cantHCHit = confHit.executeQuery("SELECT count(*) as cant FROM "+this.confidenceTableName+" where position = "+position2+" and hit=1");
		cantHCHit.first();
		cantHC.first();
		double cantHits = cantHCHit.getDouble("cant");
		double cantTotal = cantHC.getDouble("cant");
		confHit.close();
		confHit.close();
		
		return cantHits / cantTotal;

	}
	
	
	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		ConfidenceAnalyzer confidence = new ConfidenceAnalyzer("sc15", "sc15Conf");
		confidence.computeStatistics(5, 5);
		System.out.println(confidence.highConfidenceProportion());
		double highConfProportion = confidence.highConfidenceProportion();
		List<Double> results = new ArrayList<Double>();
		results.add(0, highConfProportion);
		for (int i = 1; i <= 5; i++) {
			results.add(i,confidence.positionConfidenceProportion(i));
		}
		
		
		String newLineMark = System.getProperty("line.separator");
		String leftAlignFormat = "| %-9f | %-9f | %-9f | %-9f | %-9f | %-9f | " + newLineMark;
		System.out.format("+--------------+-----------+-----------+-----------+-----------+-----------+-----------+-----------+-----------+-----------+-----------+" + newLineMark);
		System.out.printf("| High Confidence   |   1 pos   |   2 pos   |   3 pos   |   4 pos   |   5 pos   |" + newLineMark);
		System.out.format("+--------------+-----------+-----------+-----------+-----------+-----------+-----------+-----------+-----------+-----------+-----------+" + newLineMark);
		System.out.format(leftAlignFormat, results.get(0), results.get(1), results.get(2), results.get(3), results.get(4), results.get(5));
		System.out.format("+--------------+-----------+-----------+-----------+-----------+-----------+-----------+-----------+-----------+-----------+-----------+" + newLineMark);
		System.out.println(results);
		
	}
	

}
