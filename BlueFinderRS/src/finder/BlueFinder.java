package finder;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import pia.PIAInterface;
import pia.PIANode4J;
import pia.Pair;
import pia.PathQueryResultSet;

import dbpedia.DBpediaInterface;
import dbpedia.DBpediaProxyJenaImpl;
import dbpedia.DBpediaQueryException;
import dbpedia.DBpediaResultSet;
import dbpedia.ResultElement;

public class BlueFinder {
	
//	public static void main(String[] args) throws DBpediaQueryException {
//		DBpediaInterface dbpedia = new DBpediaProxyJenaImpl();
//		if(args.length != 1){
//			System.out.println("wrong parameter number");
//			System.out.println("webPageName");
//		}
//		String wPage = args[0];
//		@SuppressWarnings("unused")
//		DBpediaResultSet res = dbpedia.getRelatedPagesTo(wPage);
//		Iterator<ResultElement> it = res.getIterator();
//		while (it.hasNext()) {
//			ResultElement resultElement = (ResultElement) it.next();
//			
//		}
//		pia.PIANode4J pia = new pia.PIANode4J();
//		pia.Pair pair = new pia.Pair("Argentina", "Brenda Asnicar");
//		List<Pair> list = new java.util.ArrayList<Pair>();
//		list.add(pair);
//		pia.setInputPairs(list);
//		PathQueryResultSet results = pia.runPIA();
//		System.out.println(results.getUnrelatedPairs());
//	}

	public static void main(String[] args) throws Exception {
		BlueFinder bf = new BlueFinder();
		String query = "select ?from ?to where {"+
	    "?to a <http://dbpedia.org/ontology/Philosopher>."+
		"?to <http://dbpedia.org/property/birthPlace> ?from."+
	    "?from <http://www.w3.org/2002/07/owl#sameAs> <http://cs.dbpedia.org/resource/Francie>.}";
		DBpediaProxyJenaImpl dbpedia = new DBpediaProxyJenaImpl();
		DBpediaResultSet result = dbpedia.getResult(query);
		Iterator<ResultElement> it = result.getIterator();
		List<Pair> list = new ArrayList<Pair>();
		while(it.hasNext()){
			ResultElement re = it.next();
			//System.out.println(re.at("from")+" - "+re.at("to"));
			Pair pair = new Pair(bf.fromDBpediaResourceToWikipediaTitle(re.at("from")),bf.fromDBpediaResourceToWikipediaTitle(re.at("to")));
			list.add(pair);
		}
		PIAInterface pia = new PIANode4J();
		pia.setInputPairs(list);
		PathQueryResultSet resultSet = pia.runPIA();
		
		
	}
	
	public String fromDBpediaResourceToWikipediaTitle(String dbpediaResource) throws UnsupportedEncodingException{
		//String newResource = dbpediaResource.replaceFirst("http://dbpedia.org/resource/", "");
		String result = URLDecoder.decode(dbpediaResource,"UTF-8");
		result = result.replaceFirst("http://dbpedia.org/resource/", "");
		result = result.replaceAll("_", " ");
		
		
		
		return result.trim();
	}
}
