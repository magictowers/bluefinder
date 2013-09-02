package evals;

import static org.junit.Assert.*;

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

import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import utils.FromToPair;
import utils.PathsResolver;
import db.TestDatabaseSameThatWikipediaDatabaseException;
import db.WikipediaConnector;
import evals.PathsCleaner;


public class PathsCleanerTest {

	private PathsCleaner pathsCleaner;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Assume.assumeTrue(WikipediaConnector.isTestEnvironment()); // Common initialization done once for Test1 + Test2
	}

	@Before
	public void setUp() throws Exception {
		this.pathsCleaner = new PathsCleaner();
	}
	
	@Test
	public void saveEvaluationTest() {
		System.out.println("saveEvaluationTest");
		String tableName = "test_eval";
		int evalId = 5;
		String separator = " , ";
		Map<Integer, List<String>> validPaths = new HashMap<Integer, List<String>>();
		for (int i = 1; i <= 10; i++) {
			List<String> paths = new ArrayList<String>();
			paths.add(String.valueOf(i));
			paths.add("dummy");
			validPaths.put(i, paths);
		}
		try {
			FromToPair pair = new FromToPair();
			pair.setFrom("Spy_Kids");
			pair.setTo("Carmen_Cortez");
			this.pathsCleaner.setPair(pair);
			this.pathsCleaner.saveEvaluation(tableName, evalId, separator, validPaths);
			Connection conn = WikipediaConnector.getTestConnection();
			PreparedStatement stmt = conn.prepareStatement("SELECT * FROM " + tableName + "_clean WHERE eval_id = ?");
			stmt.setInt(1, evalId);
			PathsResolver pathResolver = new PathsResolver(" , ");
			ResultSet results = stmt.executeQuery();
			if (results.next()) {
				for (int k = 1; k <= 10; k++) {
					String resultPath = results.getString(k+"path");
					assertEquals(validPaths.get(k), pathResolver.simpleDecoupledPaths(resultPath));
				}
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			fail("ClassNotFoundException");
		} catch (SQLException e) {
			e.printStackTrace();
			fail("SQLException");
		} catch (TestDatabaseSameThatWikipediaDatabaseException e) {
			e.printStackTrace();
			fail("TestDatabaseSameThatWikipediaDatabaseException");
		}
	}

	@Test
	public void setAnalysisCaseTest() {
		System.out.println("setAnalysisCaseTest");
//		String tableName = "test_eval";
//		int evalId = 2;
//		String separator = " , ";
//		Map<Integer, List<String>> validPaths = new HashMap<Integer, List<String>>();
//		@SuppressWarnings("rawtypes")
//		Class[] cArgs = new Class[3];
//        cArgs[0] = String.class;
//        cArgs[1] = Integer.class;
//        cArgs[2] = String.class;
//		Method method;
//		try {
//			method = this.pathsCleaner.getClass().getDeclaredMethod("setAnalysisCase", cArgs);
//			method.setAccessible(true);
//			Object[] params = new Object[3];
//			params[0] = tableName;
//			params[1] = evalId;
//			params[2] = separator;
//			method.invoke(this.pathsCleaner, params);
//		} catch (NoSuchMethodException e) {
//			fail("NoSuchMethodException");
//		} catch (SecurityException e) {
//			fail("SecurityException");
//		} catch (IllegalAccessException e) {
//			fail("IllegalAccessException");
//		} catch (IllegalArgumentException e) {
//			fail("IllegalArgumentException");
//		} catch (InvocationTargetException e) {
//			fail("InvocationTargetException");
//		}
		fail("Not implemented yet.");
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
}
