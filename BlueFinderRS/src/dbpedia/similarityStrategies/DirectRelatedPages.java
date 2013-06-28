package dbpedia.similarityStrategies;

import dbpedia.DBpediaResultSet;

public class DirectRelatedPages extends SimilarityStrategy {

	@Override
	public String getRelatedPagesQuery(String wikipediaPageName) {
		String query = "SELECT distinct ?property ?page  WHERE {"
				+ "<http://dbpedia.org/resource/"+wikipediaPageName+"> ?property ?to."
				+ "?property <http://www.w3.org/2000/01/rdf-schema#domain> ?commonType."
				+ "<http://dbpedia.org/resource/"+wikipediaPageName+"> a ?commonType. "
				+ "?property <http://www.w3.org/1999/02/22-rdf-syntax-ns#type>	<http://www.w3.org/2002/07/owl#ObjectProperty> ."
				+ "?to <http://xmlns.com/foaf/0.1/isPrimaryTopicOf> ?page. }";
		return query;
		
	}

	@Override
	public DBpediaResultSet filterResultSet(DBpediaResultSet resultSet) {
		return resultSet;
	}

}
