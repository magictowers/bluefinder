package evals;

import java.sql.SQLException;

import knn.clean.Statistics;

public class EvaluationAnalyzer {

	public void analyze(String pathsTableName, String evalTableName, int maxRecomm) {
		try {
			PathsCleaner pathsCleaner = new PathsCleaner();
			pathsCleaner.analyzeEvaluations(evalTableName);
			String cleanedEvalsTableName = evalTableName + pathsCleaner.SUFFIX;
			Statistics statistics = new Statistics();
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
		if (args.length < 2) {
			System.err.println("Expected parameters: <paths table name (V_Normalized)> <evaluation table name> [<maxRecommendations>]");
			System.exit(255);
		}
		String pathsTableName = args[0];
		String evalTableName = args[1];
		int maxRecomm = 1000;
		try {
			maxRecomm = Integer.parseInt(args[2]);
		} catch (NumberFormatException e) {
			System.err.println("Wrong number, number of recommendations set to default (all of them).");
		} catch (ArrayIndexOutOfBoundsException e) {
			System.err.println("Number of recommendations not provided, set to default (all of them).");
		}
		long startTime = System.currentTimeMillis();
		EvaluationAnalyzer evalsAnalyzer = new EvaluationAnalyzer();
		evalsAnalyzer.analyze(pathsTableName, evalTableName, maxRecomm);
		long lapse = System.currentTimeMillis() - startTime;
		System.out.println("Took " + lapse + " millis.");
	}

}
