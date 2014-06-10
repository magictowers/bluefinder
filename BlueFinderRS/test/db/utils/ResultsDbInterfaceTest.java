
package db.utils;

import db.TestDatabaseSameThatWikipediaDatabaseException;
import db.WikipediaConnector;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import static org.junit.Assert.assertEquals;
import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author mkaminose
 */
public class ResultsDbInterfaceTest {

    private ResultsDbInterface resultsDb;
    
    @BeforeClass
    public static void setUpBeforeClass() {
        Assume.assumeTrue(WikipediaConnector.isTestEnvironment());
    }
    
    @Before
    public void setUp() throws SQLException, ClassNotFoundException, TestDatabaseSameThatWikipediaDatabaseException {
        this.resultsDb = new ResultsDbInterface();
        this.resultsDb.setConnection(WikipediaConnector.getTestConnection());
    }
    
    @Test
	public void testGetTypesFromDB() throws SQLException, ClassNotFoundException, FileNotFoundException, IOException{
		ResultsDbInterface.restoreResultIndex(this.resultsDb.getConnection());
		String[] dt = {"<http://dbpedia.org/class/yago/ArgentinePopSingers>","<http://dbpedia.org/class/yago/PeopleFromBuEnosAires>",
				"<http://dbpedia.org/class/yago/Actor109765278>", "<http://dbpedia.org/class/yago/LivingPeople>",
				"<http://dbpedia.org/class/yago/ArgentinePeopleOfItalianDescent>"};
		Set<String> diegoTypes = new HashSet<String>(Arrays.asList(dt));
		
		assertEquals(diegoTypes, new HashSet<String>(WikipediaConnector.getResourceDBTypes("Diego_Torres")));
		
	}
    
    @Test
	public void testGetProportionOfConnectedPairs() throws ClassNotFoundException, SQLException, FileNotFoundException, IOException{
		
		ResultsDbInterface.restoreResultIndex(this.resultsDb.getConnection());
		
		for (int i = 1; i < 11; i++) {
			Connection con = WikipediaConnector.getResultsConnection();
			Statement st = con.createStatement();
			st.executeUpdate("insert into U_pageEnhanced(`page`,`id`,`subjectTypes`,`objectTypes`) values ("+i+","+i+40+",\"sT\",\"oT\")");
			st.close();
		}
		
		ResultSet rs = this.resultsDb.getRandomProportionOfConnectedPairs(10);
		rs.last();
		assertEquals(1,rs.getRow());
		
		rs = this.resultsDb.getRandomProportionOfConnectedPairs(100);
		rs.last();
		
		rs = this.resultsDb.getRandomProportionOfConnectedPairs(120);
		rs.last();
		assertEquals(10, rs.getRow());
	}
        
}
