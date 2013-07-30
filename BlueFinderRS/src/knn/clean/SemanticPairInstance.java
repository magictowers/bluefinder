package knn.clean;

import knn.Instance;
import knn.distance.SemanticPair;

public class SemanticPairInstance extends Instance{

	public SemanticPairInstance(double distance, SemanticPair pair) {
		
			super(distance, pair.getSubject().toString()+" , "+pair.getObject().toString(), 
					pair.getSubjectElementsBySemProperty("type").toString()+" ,, "+pair.getObjectElementsBySemProperty("type").toString(), pair.getId());
		}
	}
	
	


