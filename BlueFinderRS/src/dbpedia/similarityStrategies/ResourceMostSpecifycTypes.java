package dbpedia.similarityStrategies;

import java.util.Iterator;

import dbpedia.DBpediaInterface;
import dbpedia.DBpediaQueryException;
import dbpedia.DBpediaResultSet;
import dbpedia.ResultElement;

public class ResourceMostSpecifycTypes extends SimilarityStrategy {

	@Override
	public String getRelatedPagesQuery(String wikipediaPage) {
//		PREFIX owl: <http://www.w3.org/2002/07/owl#>
//			select distinct ?t where {
//			<http://dbpedia.org/resource/Paris> a ?t.
//			#<http://dbpedia.org/resource/Paris> a ?t2.
//			OPTIONAL { 
//			?t2 <http://www.w3.org/2000/01/rdf-schema#subClassOf> ?t .<http://dbpedia.org/resource/Paris> a ?t2.
//			FILTER ( ?t2 != owl:Nothing && ?t2 != ?t )
//				}
//			FILTER (!bound(?t2))
//			 	}
		String query = "PREFIX owl: <http://www.w3.org/2002/07/owl#>" +
				"select distinct ?t where {"+
				"<http://dbpedia.org/resource/"+wikipediaPage+"> a ?t."+
				"OPTIONAL { "+
				"?t2 <http://www.w3.org/2000/01/rdf-schema#subClassOf> ?t .<http://dbpedia.org/resource/"+wikipediaPage+"> a ?t2." +
				"FILTER ( ?t2 != owl:Nothing && ?t2 != ?t )	}"+
				"FILTER (!bound(?t2)) 	}";
		return query;
	}
		

	@Override
	public DBpediaResultSet filterResultSet(DBpediaResultSet resultSet) {
		return resultSet;
	}
	
	/**
	 * This main is only for test usage.
	 * @param args
	 * @throws DBpediaQueryException
	 */
	public static void main(String[] args) throws DBpediaQueryException, Exception {
		DBpediaInterface dbpedia = new DBpediaProxyJenaImpl();
		ResourceMostSpecifycTypes strategy = new ResourceMostSpecifycTypes();
		dbpedia.setSimilarPages(strategy);
		DBpediaResultSet result = dbpedia.getRelatedPagesTo(args[0]);
		Iterator<ResultElement> it = result.getIterator();
		while(it.hasNext()){
			ResultElement elem = it.next();
			System.out.println(elem.at("t"));
		}
	}

}
