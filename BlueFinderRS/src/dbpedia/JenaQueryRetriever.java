package dbpedia;

import com.hp.hpl.jena.query.ARQ;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;

public class JenaQueryRetriever {
	private int currentPage;
	private int pageSize;
	private String sparqlQuery;

	private boolean hasNext;

	public JenaQueryRetriever(int pageSize, String sparqlQuery, int totalRows) {
		this.currentPage = 0;
		this.pageSize = pageSize;
		this.sparqlQuery = sparqlQuery;
		this.hasNext = true;

	}

	public JenaQueryRetriever(String sparqlQuery) {
		this(10000, sparqlQuery, 1000000);
	}

	private int getOffset() {
		return this.getCurrentPage() * this.pageSize;
	}

	private int getCurrentPage() {
		return this.currentPage;
	}

	public int getPageSize() {
		return this.pageSize;
	}

	private String getLimit() {
		return "LIMIT " + this.getPageSize();
	}

	public ResultSet getNextPage() {

		String queryString = this.sparqlQuery + "OFFSET " + this.getOffset()
				+ " " + this.getLimit();
		ARQ.getContext().setTrue(ARQ.useSAX) ;
		Query query = QueryFactory.create(queryString);
		// Inicializacion de queryExecution factory con el servicio remoto
		QueryExecution qexec = QueryExecutionFactory.sparqlService(
				"http://dbpedia.org/sparql", query);

		ResultSet results = qexec.execSelect();
		this.hasNext = results.hasNext();

		this.currentPage += 1;
		return results;
	}

	

	

	public boolean hasNext() {
		return this.hasNext;
	}

}
