package db;


import dbpedia.similarityStrategies.SimilarityStrategy;


public interface DBpediaInterface {

	public DBpediaResultSet getRelatedPagesTo(String wikipediaPageName) throws DBpediaQueryException, Exception;
	public void setSimilarPages(SimilarityStrategy strategy);
	public DBpediaResultSet getResult(String query);
	public DBpediaResultSet getResult(String query, String dbTable) throws Exception;
	
	
}