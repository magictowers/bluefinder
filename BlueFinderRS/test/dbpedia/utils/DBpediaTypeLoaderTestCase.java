package dbpedia.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import db.TestDatabaseSameThatWikipediaDatabaseException;
import db.WikipediaConnector;
import java.io.File;
import java.io.InputStream;
import pia.PathFinder;
import utils.ProjectConfigurationReader;

public class DBpediaTypeLoaderTestCase {
	
	private Connection testConnection;
	private String typesTableName = ProjectConfigurationReader.dbpediaTypeTable();
	private String typesFile;
	
	@BeforeClass
	public static void setupclass(){
		Assume.assumeTrue(WikipediaConnector.isTestEnvironment());
        
	}
	
	@Before
	public void setUp() throws ClassNotFoundException, SQLException, TestDatabaseSameThatWikipediaDatabaseException, FileNotFoundException, IOException{
		this.testConnection=WikipediaConnector.getTestConnection();
		this.typesFile = "test_DBpediaTypeLoader_file.nt";        
		WikipediaConnector.restoreTestDatabase();		
		
	}	

    
	@Test
	public void testLoad() throws ForbidenTableNameException, SQLException, IOException {
		Set<String> expected = new HashSet<String>();
		expected.add("<http://dbpedia.org/ontology/Scientist>");
		expected.add("<http://dbpedia.org/ontology/Person>");
		expected.add("<http://xmlns.com/foaf/0.1/Person>");
        
		DBpediaTypeLoader.load(this.testConnection,this.typesTableName,this.typesFile);
		List<String> types = DBpediaTypeLoader.getTypes("Albert_Einstein",this.testConnection,this.typesTableName);
		Set<String> result = new HashSet<String>();
		result.addAll(types);
		
		assertEquals(expected, result);
	}
	
	@Test
	public void testLoadCodedDBUris(){
		Set<String> expected = new HashSet<String>();
		expected.add("<http://umbel.org/umbel/rc/Athlete>");
		expected.add("<http://umbel.org/umbel/rc/SoccerPlayer>");
		
        // Alvaro_Mesen - poner acentos
		List<String> types = DBpediaTypeLoader.getTypes("Álvaro_Mesén", this.testConnection, this.typesTableName);
		
		Set<String> result = new HashSet<String>();
		result.addAll(types);
		assertEquals(expected, result);		
        
        
        
	}
	
	@Test
	public void testLoadForbidenNamePage() throws SQLException, IOException{	
		try {
			DBpediaTypeLoader.load(this.testConnection,"page","fakettl.ttl");
			fail("Does not throw ForbidenTableNameException!");
		} catch (ForbidenTableNameException e) {
			// TODO Auto-generated catch block
            
		} 
        
	}
	
	@Test
	public void testLoadForbidenNameCategory() throws SQLException, IOException{	
		try {
			DBpediaTypeLoader.load(this.testConnection,"category","fakettl.ttl");
			fail("Does not throw ForbidenTableNameException!");
		} catch (ForbidenTableNameException e) {
			// TODO Auto-generated catch block		
            
		} 	
        
	}
	
	@Test
	public void testLoadForbidenNamePagelinks() throws SQLException, IOException{	
		try {
			DBpediaTypeLoader.load(this.testConnection,"pagelinks","fakettl.ttl");
			fail("Does not throw ForbidenTableNameException!");
		} catch (ForbidenTableNameException e) {
			// TODO Auto-generated catch block	
            
		} 		
        
	}
	
	@Test
	public void testLoadForbidenNameCategoryLinks() throws SQLException, IOException{	
		try {
			DBpediaTypeLoader.load(this.testConnection,"categorylinks","fakettl.ttl");
			fail("Does not throw ForbidenTableNameException!");
		} catch (ForbidenTableNameException e) {
			// TODO Auto-generated catch block
		} 
	}
	

}
