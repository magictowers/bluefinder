package knn;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class JaccardFunctionOnlyDB extends JaccardFunction{
	/*
	 * http://xmlns.com/foaf/0.1/Person http://dbpedia.org/class/yago/AmericanJazzSingers http://dbpedia.org/class/yago/Jazz-bluEsSaxophonists http://dbpedia.org/class/yago/IndiaNavigationArtists http://dbpedia.org/class/yago/Person100007846 
	 * http://dbpedia.org/class/yago/AmericanJazzSaxophonists
	 */
	@Override
	public float distance(List<String> a, List<String> b) {
		Set<String> aSet = new HashSet<String>(a);
		Set<String> bSet = new HashSet<String>(b);
		List<String> aSSet = new ArrayList<String>(); 
		List<String> bSSet = new ArrayList<String>();
		
		for (String string : bSet) {
			if(!(string.startsWith("http://dbpedia.org/class/yago/") || string.startsWith("http://umbel.org")) ){
				bSSet.add(string);
			}
		}
		for (String string : aSet) {
			if(!(string.startsWith("http://dbpedia.org/class/yago/") || string.startsWith("http://umbel.org")) ){
				aSSet.add(string);
			}
		}
		System.out.println("ONLY DB");
		return super.distance(aSSet,bSSet);
		
	}


}
