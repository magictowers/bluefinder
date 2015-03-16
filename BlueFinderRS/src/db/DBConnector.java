package db;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import utils.ProjectSetup;
import db.utils.ScriptRunner;

/**
 * This class control the connections to the different databases.
 * 
 * @author dtorres & mateoDurante
 */

public class DBConnector {

	private Connection wikiConnection;
	private Connection resultsConnection;

	private String wikiUser;
	private String wikiPass;
	private String wikiDatabase;
	private String resultsUser;
	private String resultsPass;
	private String resultsDatabase;

	/*
	 * private String testUser; private String testPass; private String
	 * testDatabase;
	 */

	/*
	 * TODO Hacer que reciba usuario pass y db
	 */

	public DBConnector(Connection wikiConnection, Connection resultsConnection) throws BadInstantiationofDBConnector {
		if(wikiConnection==null || resultsConnection==null){
			throw new BadInstantiationofDBConnector();
		}
		this.wikiConnection = wikiConnection;
		this.resultsConnection = resultsConnection;
	}

	public DBConnector(ProjectSetup projectSetup) {
		this.wikiUser = projectSetup.getWikiUser();
		this.wikiPass = projectSetup.getWikiPass();
		this.wikiDatabase = projectSetup.getWikiDatabase();
		this.resultsUser = projectSetup.getResultsUser();
		this.resultsPass = projectSetup.getResultsPass();
		this.resultsDatabase = projectSetup.getResultsDatabase();
	}

	public DBConnector(Connection wikiConnection, String wikiUser,
			String wikiPass, String wikiDb, Connection resultsConnection,
			String resultsUser, String resultsPass, String resultsDb) {
		this.wikiConnection = wikiConnection;
		this.wikiUser = wikiUser;
		this.wikiPass = wikiPass;
		this.wikiDatabase = wikiDb;
		this.resultsConnection = resultsConnection;
		this.resultsUser = resultsUser;
		this.resultsPass = resultsPass;
		this.resultsDatabase = resultsDb;
	}

	public DBConnector(String wikiUser, String wikiPass, String wikiDb,
			String resultsUser, String resultsPass, String resultsDb) {
		this.wikiUser = wikiUser;
		this.wikiPass = wikiPass;
		this.wikiDatabase = wikiDb;
		this.resultsUser = resultsUser;
		this.resultsPass = resultsPass;
		this.resultsDatabase = resultsDb;
	}

	public void setWikiConnection(Connection wikiConnection) {
		this.wikiConnection = wikiConnection;
	}

	public void setResultsConnection(Connection resultsConnection) {
		this.resultsConnection = resultsConnection;
	}

	public String getWikiUser() {
		return wikiUser;
	}

	public void setWikiUser(String wikiUser) {
		this.wikiUser = wikiUser;
	}

	public String getWikiPass() {
		return wikiPass;
	}

	public void setWikiPass(String wikiPass) {
		this.wikiPass = wikiPass;
	}

	public String getWikiDatabase() {
		return wikiDatabase;
	}

	public void setWikiDatabase(String wikiDatabase) {
		this.wikiDatabase = wikiDatabase;
	}

	public String getResultsUser() {
		return resultsUser;
	}

	public void setResultsUser(String resultsUser) {
		this.resultsUser = resultsUser;
	}

	public String getResultsPass() {
		return resultsPass;
	}

	public void setResultsPass(String resultsPass) {
		this.resultsPass = resultsPass;
	}

	public String getResultsDatabase() {
		return resultsDatabase;
	}

	public void setResultsDatabase(String resultsDatabase) {
		this.resultsDatabase = resultsDatabase;
	}

	public Connection getResultsConnection() throws ClassNotFoundException,
			SQLException {
		if (this.resultsConnection == null || this.resultsConnection.isClosed()) {
			Class.forName("com.mysql.jdbc.Driver");
			this.resultsConnection = DriverManager
					.getConnection("jdbc:mysql://" + getResultsDatabase()
							+ "?user=" + getResultsUser() + "&password="
							+ getResultsPass() + "&characterEncoding=utf8");
		}
		return this.resultsConnection;
	}

	public Connection getWikiConnection() throws ClassNotFoundException,
			SQLException {
		if (this.wikiConnection == null || this.wikiConnection.isClosed()) {
			Class.forName("com.mysql.jdbc.Driver");
			this.wikiConnection = DriverManager
					.getConnection("jdbc:mysql://" + getWikiDatabase()
							+ "?user=" + getWikiUser() + "&password="
							+ getWikiPass() + "&useUnicode=true&characterEncoding=utf8");
		}
		return this.wikiConnection;
	}

	/*
	 * public Connection getTestConnection() throws ClassNotFoundException,
	 * SQLException { Class.forName("com.mysql.jdbc.Driver"); if
	 * (this.testConnection==null || this.testConnection.isClosed()) {
	 * this.testConnection =
	 * DriverManager.getConnection("jdbc:mysql://"+getTestDatabase
	 * ()+"?user="+getTestUser
	 * ()+"&password="+getTestPass()+"&characterEncoding=utf8"); } return
	 * this.testConnection; }
	 */
	public void restoreTestDatabase() throws ClassNotFoundException,
			SQLException, TestDatabaseSameThatWikipediaDatabaseException,
			FileNotFoundException, IOException,
			PropertiesFileIsNotFoundException {
		if (this.isTestEnvironment()) {
			Connection con = getResultsConnection();
			queryRunner(con, "testBasicWikipedia.sql");
		}
	}

	public void executeSqlFromFile(String filePath)
			throws FileNotFoundException, IOException, SQLException,
			ClassNotFoundException {
		if (this.isTestEnvironment()) {
			Connection conn = getResultsConnection();
			queryRunner(conn, filePath);
		}
	}

	private void queryRunner(Connection con, String scriptPathFile)
			throws IOException, SQLException, FileNotFoundException {
		ScriptRunner runner = new ScriptRunner(con, false, true);
		runner.setLogWriter(null);
		InputStream is = DBConnector.class.getClassLoader()
				.getResourceAsStream(scriptPathFile);
		InputStreamReader reader = new InputStreamReader(is);
		// runner.runScript(new BufferedReader(new FileReader(scriptPathFile)));
		runner.runScript(new BufferedReader(reader));
	}

	public void restoreResultIndex() throws SQLException,
			FileNotFoundException, IOException, ClassNotFoundException {
		Connection con;
		con = getResultsConnection();
		queryRunner(con, "bluefinder.sql");
	}

	public List<String> getResourceDBTypes(String resource)
			throws SQLException, ClassNotFoundException {
		String query = "select type from "
				+ new ProjectSetup().getDbpediaTypeTable()
				+ " where resource=?";
		PreparedStatement statement;

		statement = getResultsConnection().prepareStatement(query);

		statement.setString(1, resource);
		ResultSet rs = statement.executeQuery();

		List<String> results = new ArrayList<String>();
		while (rs.next()) {
			results.add(rs.getString("type"));
		}
		return results;
	}

	public void createStatisticsTables() throws SQLException,
			ClassNotFoundException, PropertiesFileIsNotFoundException {

		String dropTable = "DROP TABLE IF EXISTS `generalStatistics`";
		Statement statement = this.getResultsConnection().createStatement();
		statement.executeUpdate(dropTable);
		statement.close();

		String createSentence = "CREATE TABLE IF NOT EXISTS `generalStatistics` (`id` int(11) NOT NULL AUTO_INCREMENT, `scenario` varchar(45) NOT NULL, PRIMARY KEY (`id`),"
				+ "UNIQUE KEY `scenario_UNIQUE` (`scenario`)) ENGINE=InnoDB DEFAULT CHARSET=utf8";

		statement = this.getResultsConnection().createStatement();
		statement.executeUpdate(createSentence);
		statement.close();

		dropTable = "DROP TABLE IF EXISTS `particularStatistics`";
		statement = this.getResultsConnection().createStatement();
		statement.executeUpdate(dropTable);
		statement.close();

		String createParticular = "CREATE TABLE IF NOT EXISTS `particularStatistics` (`id` int(11) NOT NULL AUTO_INCREMENT, `general_id` int(11) NOT NULL,`kValue` int(11) NOT NULL,`precision` float(15,8) NOT NULL DEFAULT '0',"
				+ "`recall` float(15,8) NOT NULL DEFAULT '0', `f1` float(15,8) NOT NULL DEFAULT '0',`hit_rate` float(15,8) NOT NULL DEFAULT '0',"
				+ " `GI` float(15,8) NOT NULL DEFAULT '0',`itemSupport` float(15,8) NOT NULL DEFAULT '0', `userSupport` float(15,8) NOT NULL DEFAULT '0', `limit` int(11) NOT NULL, "
				+ "PRIMARY KEY (`id`)) ENGINE=InnoDB DEFAULT CHARSET=utf8";

		statement = this.getResultsConnection().createStatement();
		statement.executeUpdate(createParticular);
		statement.close();
	}

	public void insertParticularStatistics(String scenario, long kValue,
			double precision, double recall, double f1, double hit_rate,
			double gindex, double itemSupport, double userSupport, int limit)
			throws SQLException, ClassNotFoundException,
			PropertiesFileIsNotFoundException {

		String generalStatistic = "select * from `generalStatistics` where scenario=?";

		PreparedStatement gs = getResultsConnection().prepareStatement(
				generalStatistic);
		gs.setString(1, scenario);
		ResultSet rs = gs.executeQuery();
		long general_id = 0;
		if (rs.next()) {
			general_id = rs.getLong("id");
		} else {
			String insertIntoGeneral = "INSERT INTO `generalStatistics` (`scenario`) VALUES (?)";
			PreparedStatement psInsertGeneral = this.getResultsConnection()
					.prepareStatement(insertIntoGeneral);
			psInsertGeneral.setString(1, scenario);
			psInsertGeneral.executeUpdate();
			gs = getResultsConnection().prepareStatement(generalStatistic);
			gs.setString(1, scenario);
			rs = gs.executeQuery();
			rs.next();
			general_id = rs.getLong("id");
		}

		String insertParticularStatistic = "INSERT INTO `particularStatistics` (`general_id`,`kValue`,`precision`,`recall`, "
				+ "`f1`,`hit_rate`,`GI`,`itemSupport`,`userSupport`, `limit`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

		PreparedStatement prepared = this.getResultsConnection()
				.prepareStatement(insertParticularStatistic);
		prepared.setLong(1, general_id);
		prepared.setLong(2, kValue);
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

	public void closeConnection() throws ClassNotFoundException {
		try {
			if (!(this.getResultsConnection() == null)
					&& !this.getResultsConnection().isClosed()) {

				this.getResultsConnection().close();

			}
			if (!(this.getWikiConnection() == null)
					&& !this.getWikiConnection().isClosed()) {

				this.getWikiConnection().close();

			}
		} catch (SQLException e) {
			this.resultsConnection = null;
			this.wikiConnection = null;
		}

	}

	/**
	 * The test db environment have to be called "localhost/dbresearch_test"
	 * 
	 * @throws Exception
	 */
	public boolean isTestEnvironment() {
		return this.resultsDatabase
				.equalsIgnoreCase("localhost/dbresearch_test");
	}
}
