package db;

import java.io.IOException;

import utils.BlacklistCategory;
import utils.ProjectSetup;

public class TestSetup {

	public static DBConnector getDBConnector(){
		return new DBConnector(
				"root",
				"root",
				"localhost/dbresearch_test",
				"root",
				"root",
				"localhost/dbresearch_test");
	}
	public static ProjectSetup getProjectSetup() throws IOException{
		return new ProjectSetup(
				"dbtypes",
				"starred", 
				"Category:", 
				"en", 
				"http://dbpedia.org/resource/", 
				"http://dbpedia.org/resource/", 
				getBlacklistFilename(), 
				false, 
				false, 
				null);
	}
	public static BlacklistCategory getBlacklistCategory() throws IOException{
		return new BlacklistCategory(getBlacklistFilename());
	}
	public static String getBlacklistFilename() throws IOException{
		return "blacklist_category_default.txt";
	}
}
