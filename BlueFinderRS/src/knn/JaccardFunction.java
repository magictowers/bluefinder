package knn;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class JaccardFunction {

	public List<String> splitTypes(String typesString) {
		
		return Arrays.asList(typesString.split(" "));
	}
	
	public float distance(String types1, String types2){
		return this.distance(this.splitTypes(types1), this.splitTypes(types2));
	}

	/**
	 * http://en.wikipedia.org/wiki/Jaccard_index
	 * J(A,B) = |A intersection B|/|A U B|
	 * Jaccard distance = 1 - J(A,B)
	 */
	public float distance(List<String> a, List<String> b) {
		Set<String> aSet = new HashSet<String>(a);
		Set<String> bSet = new HashSet<String>(b);
		
		if(aSet.size() > bSet.size()){
			aSet = new HashSet<String>(b);
			bSet = new HashSet<String>(a);
		}
		float countIntersection = 0f;
		for (String string : aSet) {
			if(bSet.contains(string)){
				countIntersection++;
			}
		}
		bSet.addAll(aSet);
		return 1f - (countIntersection/bSet.size());
		
	}

}
