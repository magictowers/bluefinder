/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dbpedia.utils.sesame;

/**
 *
 * @author dtorres
 */
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;

/**
 *
 * @author dtorres
 */
public class QueryRetriever {

    private int currentPage;
    private int pageSize;
    private int limit;
    private Sesame mySesameConnector;
    private int totalPages;
    private String sparqlQuery;
    private int totalRows;
    private boolean hasNext;
    public final String from;
    public final String to;
    public final String fromTrans;
    public final String toTrans;

    public QueryRetriever(int pageSize, String sparqlQuery, int totalRows) {
        this.currentPage = 0;
        this.limit = pageSize;
        this.pageSize = pageSize;
        this.sparqlQuery = sparqlQuery;
        this.totalRows = totalRows;
        this.mySesameConnector = new Sesame();
        this.setTotalPages();
        this.hasNext = true;
        String querySelect = sparqlQuery.substring(sparqlQuery.toLowerCase().indexOf("select") + "select".length(),
                                                   sparqlQuery.toLowerCase().indexOf("where"));
        String[] q = querySelect.replaceAll("\\?", "").split(",");
        this.from = q[0].trim();
        this.to = q[1].trim();
        if (q.length > 2) {
            this.fromTrans = q[2].trim();
            this.toTrans = q[3].trim();
        } else {
            this.fromTrans = null;
            this.toTrans = null;
        }
    }

    private void setTotalPages() {
        this.totalPages = this.totalRows / this.pageSize;
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

    public TupleQueryResult getNextPage() throws MalformedQueryException, RepositoryException, QueryEvaluationException {
        String query = "SELECT * WHERE {{" + this.sparqlQuery + "}}";
        query += " OFFSET " + this.getOffset() + " " + this.getLimit();
        System.out.println(query);
        TupleQueryResult queryResult;

        RepositoryConnection con = this.mySesameConnector.getRepositoryConnection();
        TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, query);
        queryResult = tupleQuery.evaluate();
        this.hasNext = queryResult.hasNext();
        this.currentPage += 1;
        System.out.println("NextPage: " + this.currentPage);
        return queryResult;
    }

    public boolean hasNext() {
        return this.hasNext;
    }

}
