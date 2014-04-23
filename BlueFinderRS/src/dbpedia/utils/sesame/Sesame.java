/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dbpedia.utils.sesame;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.openrdf.model.util.LiteralUtil;
import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.http.HTTPRepository;

/**
 *
 * @author dtorres
 */
public class Sesame {

    private int offset = 0;
    private int offsetStep = 9999;
    private int limit = 0;
    //String sesameServer = "	http://cobani:8080/openrdf-sesame/";
    String sesameServer = "http://dbpedia.org/sparql";
    String repositoryID = "";

    private String getOffset() {
        if (this.offset == 0) {
            return "";
        } else {
            return "Offset " + this.offset;
        }
    }

    private void nextOffset() {
        this.offset += this.offsetStep;
    }

    private String getLimit() {
        if (this.limit == 0) {
            return "";
        } else {
            return "Limit " + this.limit;
        }
    }

    public TupleQueryResult getPeople() throws MalformedQueryException, RepositoryException, QueryEvaluationException {

        String query = "select ?p where{ ?p a <http://xmlns.com/foaf/0.1/Person>}" + this.getOffset() + " " + this.getLimit();
        TupleQueryResult queryResult = null;


        RepositoryConnection con = this.getRepositoryConnection();
        TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, query);
        queryResult = tupleQuery.evaluate();
        return queryResult;
    }

    public TupleQueryResult getCities() throws RepositoryException, MalformedQueryException, QueryEvaluationException {
        String query = "select distinct ?city, ?page    where {  ?city a dbpedia-owl:City. ?city foaf:page ?page. OPTIONAL {?city2 rdfs:label ?cityLabel. ?city2 dcterms:subject ?category. ?category rdfs:label ?cityLabel. FILTER(?city2 = ?city)}. FILTER(!bound(?city2)) }";
        TupleQueryResult queryResult = null;


        RepositoryConnection con = this.getRepositoryConnection();
        TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, query);
        queryResult = tupleQuery.evaluate();

        return queryResult;

    }

    public RepositoryConnection getRepositoryConnection() throws RepositoryException {

        RepositoryConnection con = null;


        Repository myRepository = new HTTPRepository(sesameServer, repositoryID);

        myRepository.initialize();
        con = myRepository.getConnection();




        return con;

    }

    public static void main(String[] args) {
        //String sesameServer = "	http://cobani:8080/openrdf-sesame/";
        String sesameServer = "http://dbpedia.org/sparql";
        String repositoryID = "";

        Repository myRepository = new HTTPRepository(sesameServer, repositoryID);
        try {
            myRepository.initialize();
            RepositoryConnection con = myRepository.getConnection();
            //String query = "select ?p where{ ?p a <http://xmlns.com/foaf/0.1/Person>} limit 500000 offset 10990";
            //String query = "select count(?p) as ?p  where{ ?p a <http://xmlns.com/foaf/0.1/Person>}";
            String query = "select (count(distinct ?city) as ?cant)    where {  ?city a dbpedia-owl:City. ?city foaf:page ?page. OPTIONAL {?city2 rdfs:label ?cityLabel. ?city2 dcterms:subject ?category. ?category rdfs:label ?cityLabel. FILTER(?city2 = ?city)}. FILTER(!bound(?city2)) }";

            TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, query);
            TupleQueryResult queryResult = tupleQuery.evaluate();
            int i = 0;
            while (queryResult.hasNext()) {
                BindingSet bindingSet = queryResult.next();
                System.out.println(bindingSet.getValue("cant"));
                int cant = LiteralUtil.getIntValue(bindingSet.getValue("cant"), -1);
                System.out.println("Cant :"+cant);
                System.out.println("Valor: " + i++);

            }


            /**
             * http://dbpedia.org/resource/Mary_Jo_Slater
            Valor: 9997
            http://dbpedia.org/resource/Michael_Slater
            Valor: 9998
            http://dbpedia.org/resource/Michael_Slater_%28general%29
            Valor: 9999
             * 
             */
        } catch (QueryEvaluationException ex) {
            Logger.getLogger(Sesame.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MalformedQueryException ex) {
            Logger.getLogger(Sesame.class.getName()).log(Level.SEVERE, null, ex);
        } catch (RepositoryException ex) {
        }
    }
}
