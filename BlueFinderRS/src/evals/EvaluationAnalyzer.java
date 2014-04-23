package evals;

import java.sql.SQLException;

import knn.clean.Statistics;

public class EvaluationAnalyzer {

	public void analyze(String pathsTableName, String evalTableName) {
		try {
			PathsCleaner pathsCleaner = new PathsCleaner();
			pathsCleaner.analyzeEvaluations(evalTableName);
			String cleanedEvalsTableName = evalTableName + pathsCleaner.SUFFIX;
			GiniIndex giniIndex = new GiniIndex(pathsTableName, true);
			giniIndex.setPathsSample(-1, -1);
			Statistics statistics = new Statistics(giniIndex);
			statistics.computeStatistics(cleanedEvalsTableName);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			System.err.println("Error while generating statistics.");
		} catch (SQLException e) {
			e.printStackTrace();
			System.err.println("Error while generating statistics.");
		}
	}
	
	public static void main(String[] args) {
		if (args.length != 2) {
			System.err.println("Expected parameters: <paths table name (V_Normalized)> <evaluation table name> [<maxRecommendations>]");
			System.exit(255);
		}
		String pathsTableName = args[0];
		String evalTableName = args[1];

		long startTime = System.currentTimeMillis();
		EvaluationAnalyzer evalsAnalyzer = new EvaluationAnalyzer();
		evalsAnalyzer.analyze(pathsTableName, evalTableName);
		long lapse = System.currentTimeMillis() - startTime;
		System.out.println("Took " + lapse + " millis.");
	}

}
