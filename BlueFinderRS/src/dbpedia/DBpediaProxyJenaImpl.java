package dbpedia;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.ResultSetFormatter;

import db.MysqlIndexConnection;
import dbpedia.similarityStrategies.DirectRelatedPages;
import dbpedia.similarityStrategies.SimilarityStrategy;

public class DBpediaProxyJenaImpl implements DBpediaInterface{
	
	private SimilarityStrategy similarityStrategy;
	
	public DBpediaProxyJenaImpl(){
		this.similarityStrategy = new DirectRelatedPages();
	}
	
	
	

	public static void main(String[] args) {

	//consulta sparql
		String queryString = "SELECT distinct ?property ?page  WHERE {"
				+ "<http://dbpedia.org/resource/John_Herrington> ?property ?to."
				+ "?property <http://www.w3.org/2000/01/rdf-schema#domain> ?commonType."
				+ "<http://dbpedia.org/resource/John_Herrington> a ?commonType. "
				+ "?property <http://www.w3.org/1999/02/22-rdf-syntax-ns#type>	<http://www.w3.org/2002/07/owl#ObjectProperty> ."
				+ "?to <http://xmlns.com/foaf/0.1/isPrimaryTopicOf> ?page. }";//Me presenta las diferentes lenguas del Ecuador
	Query query = QueryFactory.create(queryString);
	// Inicializacion de queryExecution factory con el servicio remoto
	QueryExecution qexec = QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", query);


	try {
	ResultSet results = qexec.execSelect();
	QuerySolution solucion = results.nextSolution();
	for(String var: results.getResultVars()){
		System.out.println(var);
	}
	while (results.hasNext()) {
		QuerySolution querySolution = (QuerySolution) results.next();
		//System.out.println(querySolution.get("property").toString());
		//querys
	}
	ResultSetFormatter.out(System.out, results);
	} catch (Exception e) {
	System.out.println("Verificar consulta, no existen datos para mostrar");
	} finally {
	qexec.close();
	}
	}

	@Override
	public DBpediaResultSet getRelatedPagesTo(String wikipediaPageName)
			throws Exception {
		
		/*String query = "SELECT distinct ?property ?page  WHERE {"
				+ "<http://dbpedia.org/resource/"+wikipediaPageName+"> ?property ?to."
				+ "?property <http://www.w3.org/2000/01/rdf-schema#domain> ?commonType."
				+ "<http://dbpedia.org/resource/"+wikipediaPageName+"> a ?commonType. "
				+ "?property <http://www.w3.org/1999/02/22-rdf-syntax-ns#type>	<http://www.w3.org/2002/07/owl#ObjectProperty> ."
				+ "?to <http://xmlns.com/foaf/0.1/isPrimaryTopicOf> ?page. }";
		*/
		QueryResults qr = new QueryResults(this.similarityStrategy.getRelatedPagesQuery(wikipediaPageName));
		return this.similarityStrategy.filterResultSet(qr);
		
	}
	
	
	class QueryResults implements DBpediaResultSet {

		private JenaQueryRetriever queryRetriever;
		private List<ResultElement> elements;

		public QueryResults(String query) {
			this.queryRetriever = new JenaQueryRetriever(query);
			this.elements = new ArrayList<ResultElement>();
			this.startQuery();
		}

		public QueryResults(String query, String dbTable) throws ClassNotFoundException, SQLException {
			this.queryRetriever = new JenaQueryRetriever(query);
			this.startQueryTable(dbTable);
		}

		private void startQueryTable(String dbTable) throws ClassNotFoundException, SQLException {
			while (this.queryRetriever.hasNext()) {
				ResultSet queryResult = this.queryRetriever.getNextPage();
				while(queryResult.hasNext()){
					List<String> vars = queryResult.getResultVars();
					QuerySolution solution = queryResult.next();
					DBpediaResultElement dbelement = new DBpediaResultElement();
					for(String value: vars){
						String elementValue = solution.get(value).toString();
						dbelement.put(value, elementValue);
						//System.out.println(value + "  "+ elementValue);
					}
					
					this.addElementInTable(dbelement,dbTable);
				}
		}
		}

		private void addElementInTable(DBpediaResultElement dbelement,
				String dbTable) throws ClassNotFoundException, SQLException {
			Connection con = MysqlIndexConnection.getIndexConnection();
			String query = "INSERT INTO `pia`.`"+dbTable+"` (`from`, `to`) VALUES (?, ?)";
			PreparedStatement st = con.prepareStatement(query);
			st.setString(1, dbelement.at("to"));
			st.setString(2, dbelement.at("from"));
			st.executeUpdate();
			//con.close();
			
		}

		private void startQuery(){
			while (this.queryRetriever.hasNext()) {
			ResultSet queryResult = this.queryRetriever.getNextPage();
			while(queryResult.hasNext()){
				List<String> vars = queryResult.getResultVars();
				QuerySolution solution = queryResult.next();
				DBpediaResultElement dbelement = new DBpediaResultElement();
				for(String value: vars){
					String elementValue = solution.get(value).toString();
					dbelement.put(value, elementValue);
					//System.out.println(value + "  "+ elementValue);
				}
				
				addElement(dbelement);
			}
			}
		}

		private void addElement(DBpediaResultElement dbelement) {
			this.elements.add(dbelement);
		}

		@Override
		public int size() {
			return this.elements.size();
		}

		@Override
		public Iterator<ResultElement> getIterator() {
			return this.elements.iterator();
		}

		@Override
		public Iterator<ResultElement> iterator() {
			return this.getIterator();
		}

		@Override
		public void addElement(ResultElement element) {
			this.elements.add(element);
			
		}

	}


	@Override
	public void setSimilarPages(SimilarityStrategy strategy) {
		this.similarityStrategy=strategy;
	}


	@Override
	public DBpediaResultSet getResult(String query) {
		QueryResults qr = new QueryResults(query);
		return qr;
	
	}
	
	public DBpediaResultSet getResult(String query, String dbTable) throws ClassNotFoundException, SQLException {
		QueryResults qr = new QueryResults(query,dbTable);
		return qr;
	
	}

}
