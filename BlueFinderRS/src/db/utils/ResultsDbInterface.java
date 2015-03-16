package db.utils;

import db.DBConnector;
import db.PropertiesFileIsNotFoundException;
import db.TestSetup;
import db.TestDatabaseSameThatWikipediaDatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import db.WikipediaConnector;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import utils.FromToPair;
import utils.ProjectSetup;

/**
 * Clase encargada de interactuar con la base de resultados.
 * La idea es que todas las consultas estén encapsuladas acá.
 * 
 * @author mkaminose
 */
public class ResultsDbInterface {
    
    //private Connection connection;
	private DBConnector connector;
	private ProjectSetup projectSetup;
    
    public ResultsDbInterface(ProjectSetup projectSetup, DBConnector connector) throws SQLException, ClassNotFoundException, PropertiesFileIsNotFoundException {
        this.connector = connector;
        this.projectSetup = projectSetup;
    }
    

    
    public Connection getConnection() throws ClassNotFoundException, SQLException {
        return this.connector.getResultsConnection();
    }

    public void createResultTable(String tableName) throws SQLException, ClassNotFoundException {
        String queryDrop = "DROP TABLE IF EXISTS `"+ tableName +"`";
		String query = "CREATE TABLE `"+ tableName +"` (`id` int(11) NOT NULL AUTO_INCREMENT, `resource` blob, `related_resources` blob, `1path` text, `2path` text,`3path` text," +
		"`4path` text, `5path` text, `6path` text, `7path` text, `8path` text, `9path` text, `10path` text, `time` bigint(20) DEFAULT NULL, `relevantPaths` text, PRIMARY KEY (`id`)) ENGINE=InnoDB DEFAULT CHARSET=utf8";

		Statement statement = getConnection().createStatement();
		statement.executeUpdate(queryDrop);
		statement.close();
		
		statement = getConnection().createStatement();
		statement.executeUpdate(query);
		statement.close();
    }
    
	public List<DbResultMap> getNotFoundPaths() throws SQLException, ClassNotFoundException {
		String query = "SELECT id, v_from, u_to FROM NFPC";
		Connection conn = this.getConnection();
		PreparedStatement stmt = conn.prepareStatement(query);
		ResultSet results = stmt.executeQuery();
		List<DbResultMap> dbmaps = new ArrayList<DbResultMap>();
		while (results.next()) {
			DbResultMap map = new DbResultMap();
			map.put("id", results.getInt("id"));
			map.put("v_from", results.getString("v_from"));
			map.put("u_to", results.getString("u_to"));
			dbmaps.add(map);
		}
		stmt.close();
		return dbmaps;
	}

	public List<DbResultMap> getNotFoundPaths(int limit, int offset) throws SQLException, ClassNotFoundException {
		String query = "SELECT id, v_from, u_to FROM NFPC LIMIT " + String.valueOf(limit) + " OFFSET " + String.valueOf(offset);
		Connection conn = this.getConnection();
		PreparedStatement stmt = conn.prepareStatement(query);
		ResultSet results = stmt.executeQuery();
		List<DbResultMap> dbmaps = new ArrayList<DbResultMap>();
		while (results.next()) {
			DbResultMap map = new DbResultMap();
			map.put("id", results.getInt("id"));
			map.put("v_from", results.getString("v_from"));
			map.put("u_to", results.getString("u_to"));
			dbmaps.add(map);
		}
		stmt.close();
		return dbmaps;
	}
	
	public Integer getNormalizedPathId(String path) throws ClassNotFoundException, SQLException {
		int id = 0;
		Connection conn = this.getConnection();
		String query = "SELECT id FROM V_Normalized where path = ?";
		PreparedStatement stmt = conn.prepareStatement(query);
		stmt.setString(1, path);
		ResultSet results = stmt.executeQuery();
		if (results.next()) {
			id = results.getInt("id");
		}
		stmt.close();
		return id;
	}
	
	public void saveNormalizedPath(String path) throws SQLException, ClassNotFoundException {
		Connection conn = this.getConnection();
		String query = "INSERT INTO V_Normalized (path) VALUES (?)";
		PreparedStatement stmt = conn.prepareStatement(query);
		stmt.setString(1, path);
		stmt.executeUpdate();
		stmt.close();
	}

	public Integer getTupleId(String tuple) throws ClassNotFoundException, SQLException {
		int id = 0;
		Connection c = this.getConnection();
        String query = "SELECT id FROM U_page where page = ?";
        PreparedStatement pst = c.prepareStatement(query);
        pst.setString(1, tuple);
        ResultSet rs = pst.executeQuery();
        if (rs.next()) {
            id = rs.getInt("id");
        }
		return id;
	}
	
	public void saveTuple(String tuple) throws ClassNotFoundException, SQLException {
		Connection conn = this.getConnection();
        String query = "INSERT INTO U_page (page) VALUES (?)";
        PreparedStatement preparedStatement = conn.prepareStatement(query);
        preparedStatement.setString(1, tuple);
        preparedStatement.executeUpdate();
        preparedStatement.close();
	}
	
	public List<DbResultMap> getTuples() throws ClassNotFoundException, SQLException {
		List<DbResultMap> tuples = new ArrayList<DbResultMap>();
		String query = "SELECT distinct up.id, up.page FROM U_page up";        
        Connection conn = this.getConnection();
        Statement st = conn.createStatement();
        ResultSet results = st.executeQuery(query);
        while (results.next()) {
        	DbResultMap map = new DbResultMap();
        	map.put("id", results.getInt("id"));
        	map.put("tuple", results.getString("page"));
        	tuples.add(map);
        }
        
		return tuples;
	}
	
	public void saveEdge(int pathId, int tupleId) throws ClassNotFoundException, SQLException {
		Connection c = this.getConnection();
        String query = "INSERT INTO UxV (u_from, v_to) VALUES (?, ?)";
        PreparedStatement st = c.prepareStatement(query);
        st.setInt(1, tupleId);
        st.setInt(2, pathId);
        st.executeUpdate();
        st.close();
	}
	
	public void saveEdge(int pathId, int tupleId, String description) throws ClassNotFoundException, SQLException {
		Connection c = this.getConnection();
        String query = "INSERT INTO UxV (u_from, v_to, description) VALUES (?, ?, ?)";
        PreparedStatement st = c.prepareStatement(query);
        st.setInt(1, tupleId);
        st.setInt(2, pathId);
        st.setString(3, description);
        st.executeUpdate();
        st.close();
	}
	
	public void saveNotFoundPath(String from, String to) throws ClassNotFoundException, SQLException {
		String query = "INSERT INTO NFPC (v_from,u_to) VALUES (?, ?)";
        PreparedStatement pre = this.getConnection().prepareStatement(query);
        pre.setString(1, from);
        pre.setString(2, to);
        pre.executeUpdate();
        pre.close();
	}
	
	public void removeNotFoundPath(int id) throws ClassNotFoundException, SQLException {
		Connection c = this.getConnection();
		String query = "DELETE FROM `NFPC` WHERE `id` = ?";
		PreparedStatement st = c.prepareStatement(query);
		st.setInt(1, id);
		st.executeUpdate();
		st.close();
	}

    public Set<String> getNormalizedPaths(String tuple) throws ClassNotFoundException, SQLException {
        Set<String> paths = new HashSet<String>();
        Integer tupleId = this.getTupleId(tuple);
        Connection conn = this.getConnection();
        String query = ""
                + "SELECT p.path AS path FROM UxV uv INNER JOIN V_Normalized p ON uv.v_to = p.id"
                + " WHERE uv.u_from = ?";
        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setInt(1, tupleId);
        
        ResultSet results = stmt.executeQuery();
        while (results.next()) {
        	paths.add(results.getString("path"));
        }
        stmt.close();
        return paths;
    }
    
    /**
     * Receive the English version of a tuple, and return its translation.
     * The prefixes are removed.
     * 
     * @param from
     * @param to
     * @return
     * @throws SQLException
     * @throws ClassNotFoundException 
     */
    public FromToPair getTranslatedTuple(String from, String to) throws SQLException, ClassNotFoundException {
        FromToPair pair = null;
        String dbpediaLanguagePrefix = this.projectSetup.getDbpediaPrefix();
        String queryStr = ""
                + "SELECT CONVERT(fromto_table.from USING utf8) AS fromPage, CONVERT(fromto_table.to USING utf8) AS toPage "
                + "FROM " + this.projectSetup.getFromToTable() + " AS fromto_table"
                + " WHERE fromTrans = ? AND toTrans = ?";
        // WikipediaConnector.closeConnection();
        Connection conn = this.getConnection();
        PreparedStatement stmt = conn.prepareStatement(queryStr);
        stmt.setString(1, dbpediaLanguagePrefix + from);
        stmt.setString(2, dbpediaLanguagePrefix + to);
        ResultSet result = stmt.executeQuery();
//        if (result.next()) {
//            pair = new FromToPair(result.getString("fromPage"), result.getString("toPage"), ProjectConfigurationReader.languageCode());
//        }
        if (result.next()) {
            from = result.getString("fromPage");
            to = result.getString("toPage");
            String dbpediaPrefix = this.projectSetup.getDbpediaPrefix();
            pair = new FromToPair(from.replace(dbpediaPrefix, ""), to.replace(dbpediaPrefix, ""), this.projectSetup.getLanguageCode());
        }
        return pair;
    }
    
    /**
     * From a `fromto_table` get the tuples without removing the prefix.
     * 
     * @param limit
     * @param offset
     * @return
     * @throws SQLException
     * @throws ClassNotFoundException 
     */
    public List<Map<String, String>> getDbpediaTuplesSingle(Integer limit, Integer offset) throws SQLException, ClassNotFoundException {
        String fromtoTable = this.projectSetup.getFromToTable();
        
        Connection conn = this.getConnection();
        
        String strQuery = ""
                + "SELECT CONVERT(t.from USING utf8) AS dbpedia_from, CONVERT(t.to USING utf8) AS dbpedia_to, "
                + "CONVERT(t.fromTrans USING utf8) AS fromTrans, CONVERT(t.toTrans USING utf8) AS toTrans "
                + "FROM " + fromtoTable + " AS t";
        if (limit != null) {
            strQuery += " LIMIT " + limit;
            if (offset != null)
                strQuery += " OFFSET " + offset;
        }
        PreparedStatement stmt = conn.prepareStatement(strQuery);
        ResultSet results = stmt.executeQuery();
        List<Map<String, String>> dbpediaTuples = new ArrayList<Map<String, String>>();
        while (results.next()) {
            Map<String, String> transTuple = new HashMap<String, String>();
            transTuple.put("from", results.getString("dbpedia_from"));
            transTuple.put("to", results.getString("dbpedia_to"));
            transTuple.put("fromTrans", results.getString("fromTrans"));
            transTuple.put("toTrans", results.getString("toTrans"));
            dbpediaTuples.add(transTuple);
        }        
    //    ProjectConfigurationReader.useDefaultProperties();
        return dbpediaTuples;
    }
    
    public List<Map<String, String>> getDbpediaTuplesSingle() throws SQLException, ClassNotFoundException {
        return this.getDbpediaTuplesSingle(null, null);
    }
    
    /**
     * if input tuple = (eng1, eng2), then get (eng1sp, eng2sp) from 
     * Spanish DB, and (eng1fr, eng2fr) from French DB.
     * @param limit
     * @param offset
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException 
     */
    public List<Map<String, String>> getDbpediaCombinedTuples(int limit, int offset) throws ClassNotFoundException, SQLException {
//        ProjectConfigurationReader.useProperties1();
        String db1 = this.projectSetup.getResultDatabase1().split("/")[1];
        String fromtoTable1 = this.projectSetup.getFromToTable1();
        String dbpediaPrefix1 = this.projectSetup.getDbpediaPrefix1();
//        ProjectConfigurationReader.useProperties2();
        String db2 = this.projectSetup.getResultDatabase2().split("/")[1];
        String fromtoTable2 = this.projectSetup.getFromToTable2();
        String dbpediaPrefix2 = this.projectSetup.getDbpediaPrefix2();
        
        Connection conn = this.getConnection();
        
        String strQuery = ""
                + "SELECT CONVERT(t1.from USING utf8) AS from1, CONVERT(t2.from USING utf8) AS from2, "
                    + "CONVERT(t1.to USING utf8) AS to1, CONVERT(t2.to USING utf8) AS to2 "
                + "FROM " + db1 + "." + fromtoTable1 + " t1 INNER JOIN " + db2 + "." + fromtoTable2 + " t2 ON "
                + "(t1.fromTrans = t2.fromTrans AND t1.toTrans = t2.toTrans)";
        if (limit != 0) {
            strQuery += " LIMIT " + limit + " OFFSET " + offset;
        }
        PreparedStatement stmt = conn.prepareStatement(strQuery);
        ResultSet results = stmt.executeQuery();
        List<Map<String, String>> dbpediaTuples = new ArrayList<Map<String, String>>();
        while (results.next()) {
            Map<String, String> transTuple = new HashMap<String, String>();
            transTuple.put("from1", results.getString("from1").replace(dbpediaPrefix1, ""));
            transTuple.put("to1", results.getString("to1").replace(dbpediaPrefix1, ""));
            
            transTuple.put("from2", results.getString("from2").replace(dbpediaPrefix2, ""));
            transTuple.put("to2", results.getString("to2").replace(dbpediaPrefix2, ""));
            
            dbpediaTuples.add(transTuple);
        }        
//        ProjectConfigurationReader.useDefaultProperties();
        return dbpediaTuples;
    }

    public List<Map<String, String>> getDbpediaTuples() throws ClassNotFoundException, SQLException {
        return this.getDbpediaCombinedTuples(0, 0);
    }
    
    public List<String> getResourceDBTypes(String resource) throws SQLException, ClassNotFoundException {
		String query = "select type from " + this.projectSetup.getDbpediaTypeTable() + " where resource=?";
		PreparedStatement statement = getConnection().prepareStatement(query);
		
		statement.setString(1, resource);
		ResultSet rs = statement.executeQuery();
		
		List<String> results = new ArrayList<String>();
		while(rs.next()){
			results.add(rs.getString("type"));
		}
		return results;
	}
    
    public ResultSet getRandomProportionOfConnectedPairs(int proportion) throws ClassNotFoundException, SQLException {
		Statement st = getConnection().createStatement();
		ResultSet rs = st.executeQuery("select count(*) as total from U_pageEnhanced");
		double p = proportion;
		rs.next();
		long rows = rs.getLong("total");
		rs.close();
		int prop;
		if(rows > 0) {
			prop = (int)(rows*(p/100.0));
		} else {
			prop=0;
		}
		
		st = getConnection().createStatement();
		String query = "select convert(page using utf8) as page, id, convert(subjectTypes using utf8) as subjectTypes, convert(objectTypes using utf8) as objectTypes FROM U_pageEnhanced order by RAND() limit "+ prop;		
		rs = st.executeQuery(query);
		return rs;		
	}
    
    public static void createStatisticsTables(Connection conn) throws SQLException, ClassNotFoundException {
		
		String dropTable = "DROP TABLE IF EXISTS `generalStatistics`";
		Statement statement = conn.createStatement();
		statement.executeUpdate(dropTable);
		statement.close();
		
		String createSentence = "CREATE TABLE IF NOT EXISTS `generalStatistics` (`id` int(11) NOT NULL AUTO_INCREMENT, `scenario` varchar(45) NOT NULL, PRIMARY KEY (`id`),"+
    						"UNIQUE KEY `scenario_UNIQUE` (`scenario`)) ENGINE=InnoDB DEFAULT CHARSET=utf8";

		statement = conn.createStatement();
		statement.executeUpdate(createSentence);
		statement.close();		

		dropTable = "DROP TABLE IF EXISTS `particularStatistics`";
		statement = conn.createStatement();
		statement.executeUpdate(dropTable);
		statement.close();
		
		String createParticular = "CREATE TABLE IF NOT EXISTS `particularStatistics` (`id` int(11) NOT NULL AUTO_INCREMENT, `general_id` int(11) NOT NULL,`kValue` int(11) NOT NULL,`precision` float(15,8) NOT NULL DEFAULT '0',"+
								"`recall` float(15,8) NOT NULL DEFAULT '0', `f1` float(15,8) NOT NULL DEFAULT '0',`hit_rate` float(15,8) NOT NULL DEFAULT '0',"+
								" `GI` float(15,8) NOT NULL DEFAULT '0',`itemSupport` float(15,8) NOT NULL DEFAULT '0', `userSupport` float(15,8) NOT NULL DEFAULT '0', `limit` int(11) NOT NULL, "+
								"PRIMARY KEY (`id`)) ENGINE=InnoDB DEFAULT CHARSET=utf8";

		statement = conn.createStatement();
		statement.executeUpdate(createParticular);
		statement.close();		
	}
    
    public void insertParticularStatistic(String experimentName, long kValue, double precision, double recall, double f1, 
    		double hit_rate, double gindex, double itemSupport, double userSupport, int limit) 
    				throws SQLException, ClassNotFoundException {
		
		this.insertParticularStatistics(experimentName, kValue,
			precision, recall, f1, hit_rate,gindex, itemSupport, userSupport, limit);		
	}
    
    public void insertParticularStatistics(String experimentName, long kValue, double precision, double recall, double f1,
            double hit_rate, double gindex, double itemSupport, double userSupport, int limit) 
            		throws SQLException, ClassNotFoundException {
		
		String generalStatistic = "select * from `generalStatistics` where scenario=?";
		
		PreparedStatement gs = getConnection().prepareStatement(generalStatistic);
		gs.setString(1, experimentName);
		ResultSet rs = gs.executeQuery();
		long general_id=0;
		if (rs.next()) {
			general_id=rs.getLong("id");
		} else {
			String insertIntoGeneral = "INSERT INTO `generalStatistics` (`scenario`) VALUES (?)";
			PreparedStatement psInsertGeneral = getConnection().prepareStatement(insertIntoGeneral);
			psInsertGeneral.setString(1, experimentName);
			psInsertGeneral.executeUpdate();
			gs = getConnection().prepareStatement(generalStatistic);
			gs.setString(1, experimentName);
			rs = gs.executeQuery();
			rs.next();
			general_id=rs.getLong("id");
		}
		
		String insertParticularStatistic= "INSERT INTO `particularStatistics` (`general_id`,`kValue`,`precision`,`recall`, "+
				"`f1`,`hit_rate`,`GI`,`itemSupport`,`userSupport`, `limit`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		
		PreparedStatement prepared = getConnection().prepareStatement(insertParticularStatistic);		
		prepared.setLong(1, general_id);
		prepared.setLong(2,kValue);
		prepared.setDouble(3, precision);
		prepared.setDouble(4, recall);
		prepared.setDouble(5, f1);
		prepared.setDouble(6, hit_rate);
		prepared.setDouble(7, gindex);
		prepared.setDouble(8, itemSupport);
		prepared.setDouble(9, userSupport);
		prepared.setInt(10, limit);		
		
		prepared.execute();
	}

    /**
     * Usado solo en ambiente de testing.
     * 
     * @param filePath
     * @throws FileNotFoundException
     * @throws IOException
     * @throws SQLException
     * @throws ClassNotFoundException
     * @throws TestDatabaseSameThatWikipediaDatabaseException 
     * @throws PropertiesFileIsNotFoundException 
     */
    public static void executeSqlFromFile(String filePath) 
    		throws FileNotFoundException, IOException, SQLException, ClassNotFoundException, TestDatabaseSameThatWikipediaDatabaseException, PropertiesFileIsNotFoundException {
    	Connection conn = TestSetup.getDBConnector().getResultsConnection();
    	queryRunner(conn, filePath);
    }
    
	private static void queryRunner(Connection con, String scriptPathFile) throws IOException,
			SQLException, FileNotFoundException, ClassNotFoundException {
		ScriptRunner runner;
		runner = new ScriptRunner(TestSetup.getDBConnector().getResultsConnection(),false,true);
		
		runner.setLogWriter(null);
		InputStream is= ResultsDbInterface.class.getClassLoader().getResourceAsStream(scriptPathFile);
		InputStreamReader reader = new InputStreamReader(is);
		//runner.runScript(new BufferedReader(new FileReader(scriptPathFile)));
		runner.runScript(new BufferedReader(reader));
	}
    
    public static void restoreResultIndex(DBConnector conn) throws ClassNotFoundException, SQLException, FileNotFoundException, IOException {
		queryRunner(conn.getResultsConnection(),"bluefinder.sql");
	}
    
}
