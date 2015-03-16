package evals;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import utils.FromToPair;
import utils.PathsResolver;
import db.DBConnector;
import db.TestSetup;


public class PathsCleanerTest {

	private PathsCleaner pathsCleaner;
	private DBConnector connector;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		try {
				TestSetup.getDBConnector().executeSqlFromFile("test_PathsCleaner.sql");
				TestSetup.getDBConnector().executeSqlFromFile("test_PathsCleaner_smallwikipediadump.sql");
				// esta no se carga porque puede llegar a pisar el dump original de la DB!
				// para descomentarlo, asegurarme de que está en test environment.
			} catch (Exception ex) {
				ex.printStackTrace();
				fail("Error while loading required dumps. Cannot execute tests correctly.");
			}
	}

	@Before
	public void setUp() throws Exception {
		this.connector = TestSetup.getDBConnector();
		this.pathsCleaner = new PathsCleaner(this.connector);
	}

	@Test
	public void setAnalysisCaseTest() throws Exception {
		System.out.println("setAnalysisCaseTest");
		Map<Integer, List<String>> expectedAnalysisPaths = new HashMap<Integer, List<String>>();
		List<String> paths = new ArrayList<String>();		
		paths.add("#from / #to");
		paths.add("#from / * / Cat:The_Godfather_characters / #to");
		expectedAnalysisPaths.put(1, paths);
		paths = new ArrayList<String>();
		paths.add("#from / #to");
		paths.add("#from / * / Cat:The_Godfather_characters / #to");
		expectedAnalysisPaths.put(2, paths);
		paths = new ArrayList<String>();
		paths.add("#from / #to");
		paths.add("#from / * / Cat:The_Godfather_characters / #to");
		expectedAnalysisPaths.put(3, paths);
		paths = new ArrayList<String>();
		paths.add("#from / #to");
		paths.add("#from / * / Cat:The_Godfather_characters / #to");
		paths.add("#from / * / Cat:#from_characters / #to");
		expectedAnalysisPaths.put(4, paths);
		paths = new ArrayList<String>();
		paths.add("#from / #to");
		paths.add("#from / * / Cat:The_Godfather_characters / #to");
		paths.add("#from / * / Cat:#from_characters / #to");
		expectedAnalysisPaths.put(5, paths);
		paths = new ArrayList<String>();
		paths.add("#from / #to");
		paths.add("#from / * / Cat:The_Godfather_characters / #to");
		paths.add("#from / * / Cat:#from_characters / #to");
		expectedAnalysisPaths.put(6, paths);
		paths = new ArrayList<String>();
		paths.add("#from / #to");
		paths.add("#from / * / Cat:The_Godfather_characters / #to");
		paths.add("#from / * / Cat:#from_characters / #to");
		expectedAnalysisPaths.put(7, paths);
		paths = new ArrayList<String>();
		paths.add("#from / #to");
		paths.add("#from / * / Cat:#from_characters / #to");
		paths.add("#from / * / Cat:The_Godfather_characters / #to");
		expectedAnalysisPaths.put(8, paths);
		paths = new ArrayList<String>();
		paths.add("#from / #to");
		paths.add("#from / * / Cat:#from_characters / #to");
		paths.add("#from / * / Cat:The_Godfather_characters / #to");
		paths.add("#from / * / List_of_Police_Academy_cast_members / #to");
		paths.add("#from / * / List_of_Police_Academy_characters / #to");
		expectedAnalysisPaths.put(9, paths);
		paths = new ArrayList<String>();
		paths.add("#from / #to");
		paths.add("#from / * / Cat:The_Godfather_characters / #to");
		paths.add("#from / * / Cat:#from_characters / #to");
		paths.add("#from / * / List_of_Police_Academy_cast_members / #to");
		paths.add("#from / * / List_of_Police_Academy_characters / #to");
		expectedAnalysisPaths.put(10, paths);

		String tableName = "test_sc4BFResults";
		int evalId = 105;
		try {
			this.pathsCleaner.setAnalysisCase(tableName, evalId);
			Map<Integer, List<String>> actualAnalysisPaths = this.pathsCleaner.getPathsToAnalyze();
			assertEquals(expectedAnalysisPaths, actualAnalysisPaths);
		} catch (SecurityException e) {
			fail("SecurityException");
		} catch (IllegalArgumentException e) {
			fail("IllegalArgumentException");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			fail("ClassNotFoundException");
		} catch (SQLException e) {
			e.printStackTrace();
			fail("SQLException");
		}
	}

	@Test
	public void getValidPathsTest() {
		System.out.println("getValidPathsTest");
		List<String> paths = new ArrayList<String>();
		FromToPair pair = new FromToPair();
		pair.setFrom("Spy_Kids");
		pair.setTo("Carmen_Cortez");
		this.pathsCleaner.setPair(pair);
		paths.add("#from / * / Cat:#from_characters / #to");
		paths.add("#from / * / List_of_Police_Academy_cast_members / #to");
		paths.add("#from / #to");
		paths.add("#from / * / Cat:#from_charactersssss / #to");
		paths.add("#from / * / Cat:Austin_Powers_characters / #to");
		try {
			Method method = this.pathsCleaner.getClass().getDeclaredMethod("getValidPaths", List.class);
			method.setAccessible(true);
			@SuppressWarnings("unchecked")
			List<String> actual = (List<String>)method.invoke(this.pathsCleaner, paths);
			List<String> expected = new ArrayList<String>();
			expected.add("#from / * / Cat:#from_characters / #to");
			expected.add("#from / * / List_of_Police_Academy_cast_members / #to");
			expected.add("#from / #to");
			expected.add("#from / * / Cat:Austin_Powers_characters / #to");
			assertEquals(expected, actual);
			method.setAccessible(false);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			fail("NoSuchMethodException");
		} catch (SecurityException e) {
			e.printStackTrace();
			fail("SecurityException");
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			fail("IllegalAccessException");
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			fail("IllegalArgumentException");
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			fail("InvocationTargetException");
		}
	}

	@Test
	public void analyzeEvaluationTest() throws Exception {
		System.out.println("analyzeEvaluationTest");
		String tableName = "test_sc4BFResults";
		int evalId = 105;
		String separator = ", ";
		Map<Integer, List<String>> expectedValidPaths = new HashMap<Integer, List<String>>();
		List<String> paths = new ArrayList<String>();		
		paths.add("#from / #to");
		paths.add("#from / * / Cat:The_Godfather_characters / #to");
		expectedValidPaths.put(1, paths);
		paths = new ArrayList<String>();
		paths.add("#from / #to");
		paths.add("#from / * / Cat:The_Godfather_characters / #to");
		expectedValidPaths.put(2, paths);
		paths = new ArrayList<String>();
		paths.add("#from / #to");
		paths.add("#from / * / Cat:The_Godfather_characters / #to");
		expectedValidPaths.put(3, paths);
		paths = new ArrayList<String>();
		paths.add("#from / #to");
		paths.add("#from / * / Cat:The_Godfather_characters / #to");
		expectedValidPaths.put(4, paths);
		paths = new ArrayList<String>();
		paths.add("#from / #to");
		paths.add("#from / * / Cat:The_Godfather_characters / #to");
		expectedValidPaths.put(5, paths);
		paths = new ArrayList<String>();
		paths.add("#from / #to");
		paths.add("#from / * / Cat:The_Godfather_characters / #to");
		expectedValidPaths.put(6, paths);
		paths = new ArrayList<String>();
		paths.add("#from / #to");
		paths.add("#from / * / Cat:The_Godfather_characters / #to");
		expectedValidPaths.put(7, paths);
		paths = new ArrayList<String>();
		paths.add("#from / #to");
		paths.add("#from / * / Cat:The_Godfather_characters / #to");
		expectedValidPaths.put(8, paths);
		paths = new ArrayList<String>();
		paths.add("#from / #to");
		paths.add("#from / * / Cat:The_Godfather_characters / #to");
		paths.add("#from / * / List_of_Police_Academy_cast_members / #to");
		paths.add("#from / * / List_of_Police_Academy_characters / #to");
		expectedValidPaths.put(9, paths);
		paths = new ArrayList<String>();
		paths.add("#from / #to");
		paths.add("#from / * / Cat:The_Godfather_characters / #to");
		paths.add("#from / * / List_of_Police_Academy_cast_members / #to");
		paths.add("#from / * / List_of_Police_Academy_characters / #to");
		expectedValidPaths.put(10, paths);
		
		Map<Integer, List<String>> pathsSample = new HashMap<Integer, List<String>>();
		paths = new ArrayList<String>();		
		paths.add("#from / #to");
		paths.add("#from / * / Cat:The_Godfather_characters / #to");
		pathsSample.put(1, paths);
		paths = new ArrayList<String>();
		paths.add("#from / #to");
		paths.add("#from / * / Cat:The_Godfather_characters / #to");
		pathsSample.put(2, paths);
		paths = new ArrayList<String>();
		paths.add("#from / #to");
		paths.add("#from / * / Cat:The_Godfather_characters / #to");
		pathsSample.put(3, paths);
		paths = new ArrayList<String>();
		paths.add("#from / #to");
		paths.add("#from / * / Cat:The_Godfather_characters / #to");
		paths.add("#from / * / Cat:#from_characters / #to");
		pathsSample.put(4, paths);
		paths = new ArrayList<String>();
		paths.add("#from / #to");
		paths.add("#from / * / Cat:The_Godfather_characters / #to");
		paths.add("#from / * / Cat:#from_characters / #to");
		pathsSample.put(5, paths);
		paths = new ArrayList<String>();
		paths.add("#from / #to");
		paths.add("#from / * / Cat:The_Godfather_characters / #to");
		paths.add("#from / * / Cat:#from_characters / #to");
		pathsSample.put(6, paths);
		paths = new ArrayList<String>();
		paths.add("#from / #to");
		paths.add("#from / * / Cat:The_Godfather_characters / #to");
		paths.add("#from / * / Cat:#from_characters / #to");
		pathsSample.put(7, paths);
		paths = new ArrayList<String>();
		paths.add("#from / #to");
		paths.add("#from / * / Cat:#from_characters / #to");
		paths.add("#from / * / Cat:The_Godfather_characters / #to");
		pathsSample.put(8, paths);
		paths = new ArrayList<String>();
		paths.add("#from / #to");
		paths.add("#from / * / Cat:#from_characters / #to");
		paths.add("#from / * / Cat:The_Godfather_characters / #to");
		paths.add("#from / * / List_of_Police_Academy_cast_members / #to");
		paths.add("#from / * / List_of_Police_Academy_characters / #to");
		pathsSample.put(9, paths);
		paths = new ArrayList<String>();
		paths.add("#from / #to");
		paths.add("#from / * / Cat:The_Godfather_characters / #to");
		paths.add("#from / * / Cat:#from_characters / #to");
		paths.add("#from / * / List_of_Police_Academy_cast_members / #to");
		paths.add("#from / * / List_of_Police_Academy_characters / #to");
		pathsSample.put(10, paths);
		
		this.pathsCleaner.setPair(new FromToPair("The_Godfather_Part_II , Frank_Pentangeli", " , "));
		this.pathsCleaner.setPathsToAnalyze(pathsSample);

			this.pathsCleaner.analyzeEvaluation(tableName, evalId);
			Connection conn = TestSetup.getDBConnector().getResultsConnection();
			PreparedStatement stmt = conn.prepareStatement("SELECT * FROM " + tableName + "_clean WHERE eval_id = ?");
			stmt.setInt(1, evalId);
			PathsResolver pathResolver = new PathsResolver(separator);
			ResultSet results = stmt.executeQuery();
			if (results.next()) {
				for (int k = 1; k <= 10; k++) {
					String resultPath = results.getString(k+"path");
					assertEquals(expectedValidPaths.get(k), pathResolver.simpleDecoupledPaths(resultPath));
				}
			}

	}
}
