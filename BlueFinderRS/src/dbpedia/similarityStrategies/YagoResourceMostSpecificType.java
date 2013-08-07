package dbpedia.similarityStrategies;

import java.util.Iterator;

import dbpedia.DBResultSetImp;
import dbpedia.DBpediaInterface;
import dbpedia.DBpediaQueryException;
import dbpedia.DBpediaResultSet;
import dbpedia.ResultElement;

public class YagoResourceMostSpecificType extends ResourceMostSpecifycTypes {
	
	@Override
	public DBpediaResultSet filterResultSet(DBpediaResultSet resultSet) {
		DBpediaResultSet result = super.filterResultSet(resultSet);
		DBpediaResultSet newResult = new DBResultSetImp();
		for (ResultElement resultElement : result) {
			if(resultElement.at("t").contains("http://dbpedia.org/class/yago") && !resultElement.at("t").contains("http://dbpedia.org/class/yago/LivingPeople")){
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
	public static void main(String[] args) {
            System.out.println("Yago Resource Most Specific Type");
		DBpediaInterface dbpedia = new DBpediaProxyJenaImpl();
		YagoResourceMostSpecificType strategy = new YagoResourceMostSpecificType();
		dbpedia.setSimilarPages(strategy);
		DBpediaResultSet result;
		try {
			result = dbpedia.getRelatedPagesTo("Brad_Holman");
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
