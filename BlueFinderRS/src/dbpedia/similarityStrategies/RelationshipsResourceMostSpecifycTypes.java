package dbpedia.similarityStrategies;


import dbpedia.DBResultSetImp;
import dbpedia.DBpediaInterface;
import dbpedia.DBpediaProxyJenaImpl;
import dbpedia.DBpediaQueryException;
import dbpedia.DBpediaResultSet;
import dbpedia.ResultElement;

public class RelationshipsResourceMostSpecifycTypes extends SimilarityStrategy{

	private String property;
	private DBpediaInterface dbpedia;
	private String domainElement;
	private SimilarityStrategy similarStrategy;

	/**
	 * This strategy will compute the pairs of other resources in DBpedia with the most specific type
	 * of range and most specific types of domain. For example, if the values are 
	 * <Diego_Maradona> <birthPlace> <Lanus>, then the strategy will compute pairs of
	 * <SoccerPlayerInstance> <birthPlace> <CityInstance> etc.
	 * @param range
	 * @param property
	 * @param domainElement
	 */
	public RelationshipsResourceMostSpecifycTypes(String property, String domainElement, SimilarityStrategy strategy){
		this.property = property;
		this.domainElement = domainElement;
		this.dbpedia = new DBpediaProxyJenaImpl();
		this.similarStrategy = strategy;
	}
	
	@Override
	public String getRelatedPagesQuery(String wikipediaPage) {
		return this.similarStrategy.getRelatedPagesQuery(wikipediaPage);
	}
	
	@Override
	public DBpediaResultSet filterResultSet(DBpediaResultSet resultSet) throws Exception {
		DBpediaResultSet propertyResult = new DBResultSetImp();
		DBpediaResultSet filterResult = this.similarStrategy.filterResultSet(resultSet);
                this.dbpedia.setSimilarPages(this.similarStrategy);
		try {
			//DBpediaResultSet domainTypes = dbpedia.getRelatedPagesTo(this.domainElement);
			for (ResultElement rangeType : filterResult) {
				//for(ResultElement domainType: domainTypes ){
					//this.generateResultsFor(rangeType.at("t"), domainType.at("t"), propertyResult);
                            this.generateResultsFor(rangeType.at("t"),null, propertyResult);
				//}
				
			}
			
		} catch (DBpediaQueryException e) {
			e.printStackTrace();
		}
		return propertyResult;
		
	}

	private void generateResultsFor(String rangeType, String domainType, DBpediaResultSet resultSet) throws Exception {
//		String query = "select ?from ?to where{"+
//	                  "?from <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <"+rangeType+">."+
//	                  "?from <"+this.property+"> ?to. " +
//	                  "?to <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <"+domainType+">." +
//	                  		"}";
		String query = "select ?from ?to where{"+
                "?from <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <"+rangeType+">."+
                "?from <"+this.property+"> ?to." +
                		"?to <http://xmlns.com/foaf/0.1/isPrimaryTopicOf> <http://en.wikipedia.org/wiki/"+this.domainElement+">. " +
                		"}";

                System.out.println(query);
		DBpediaResultSet subTypeResults = dbpedia.getResult(query, "query_pairs");
		//for (ResultElement resultElement : subTypeResults) {
			//resultSet.addElement(resultElement);
	//		System.out.println(resultElement.at("from") + " to "+ resultElement.at("to"));
		//}
		
		
	}
	
	public static void main(String[] args) throws Exception {
		//String subject = args[0];
		//String object = args[1];
		String subject = "Irving_Brown";
		String object = "The_Bronx";
		DBpediaInterface dbpedia = new DBpediaProxyJenaImpl();
		RelationshipsResourceMostSpecifycTypes strategy = new RelationshipsResourceMostSpecifycTypes("http://dbpedia.org/property/birthPlace", object, new YagoResourceMostSpecificType());
		dbpedia.setSimilarPages(strategy);
		@SuppressWarnings("unused")
		DBpediaResultSet result = dbpedia.getRelatedPagesTo(subject);
		strategy = new RelationshipsResourceMostSpecifycTypes("http://dbpedia.org/ontology/birthPlace", object, new YagoResourceMostSpecificType());
		dbpedia.setSimilarPages(strategy);
		
		dbpedia.getRelatedPagesTo(subject);
		
	}
	
	
	
}
