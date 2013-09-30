package knn.distance;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class JaccardDistanceCalculator {
	
	/**
	 * /**
	 * http://en.wikipedia.org/wiki/Jaccard_index
	 * J(A,B) = |A intersection B|/|A U B|
	 * Jaccard distance = 1 - J(A,B)
	 
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

	 * @param pairOne
	 * @param pairTwo
	 * @return
	 */

	public double distance(ISemPair pairOne, ISemPair pairTwo) {		
		double objectDistance = jaccardDistance(pairOne.getObjectElementsBySemProperty("<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>"),
				pairTwo.getObjectElementsBySemProperty("<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>"));
		double subjectDistance = jaccardDistance(pairOne.getSubjectElementsBySemProperty("<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>"),
				pairTwo.getSubjectElementsBySemProperty("<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>"));

		return (objectDistance+subjectDistance)/ 2;
	}
	
	private double jaccardDistance(List<String> a, List<String> b) {
		Set<String> aSet = new HashSet<String>(a);
		Set<String> bSet = new HashSet<String>(b);
		
		if(aSet.size() > bSet.size()){
			aSet = new HashSet<String>(b);
			bSet = new HashSet<String>(a);
		}
		double countIntersection = 0.0;
		for (String string : aSet) {
			if(bSet.contains(string)){
				countIntersection++;
			}
		}
		bSet.addAll(aSet);
		if(countIntersection==0 && bSet.size()==0){
			return 0.0;
		}
		return 1.0 - (countIntersection/bSet.size());
		
	}
	

}
