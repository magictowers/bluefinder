package dbpedia.similarityStrategies;

import dbpedia.DBpediaResultSet;

public abstract class SimilarityStrategy {

	public abstract String getRelatedPagesQuery(String wikipediaPage);
	
	public abstract DBpediaResultSet filterResultSet(DBpediaResultSet resultSet) throws Exception;
	
}
