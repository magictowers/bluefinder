package dbpedia.similarityStrategies;

import java.util.Iterator;

import dbpedia.DBResultSetImp;
import dbpedia.DBpediaInterface;
import dbpedia.DBpediaProxyJenaImpl;
import dbpedia.DBpediaQueryException;
import dbpedia.DBpediaResultSet;
import dbpedia.ResultElement;

public class DBpediaResourceMostSpecyficType extends ResourceMostSpecifycTypes{
	
	@Override
	public DBpediaResultSet filterResultSet(DBpediaResultSet resultSet) {
		DBpediaResultSet result = super.filterResultSet(resultSet);
		DBpediaResultSet newResult = new DBResultSetImp();
		for (ResultElement resultElement : result) {
			if(resultElement.at("t").contains("http://dbpedia.org/ontology")){
				newResult.addElement(resultElement);
			}
		}
		return newResult;
	}
	
	/**
	 * This main is only for test usage.
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args)  {
		DBpediaInterface dbpedia = new DBpediaProxyJenaImpl();
		DBpediaResourceMostSpecyficType strategy = new DBpediaResourceMostSpecyficType();
		dbpedia.setSimilarPages(strategy);
		DBpediaResultSet result;
		try {
			result = dbpedia.getRelatedPagesTo("Charlie_Aitken_(footballer_born_1942)");
			Iterator<ResultElement> it = result.getIterator();
			while(it.hasNext()){
				ResultElement elem = it.next();
				System.out.println(elem.at("t"));
			}
		} catch (DBpediaQueryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	
}
