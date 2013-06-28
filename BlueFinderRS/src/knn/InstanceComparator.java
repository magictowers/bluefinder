package knn;

import java.util.Comparator;

public class InstanceComparator implements Comparator<Instance>{

	@Override
	public int compare(Instance o1, Instance o2) {
		Instance inst1 = (Instance) o1;
		Instance inst2 = (Instance) o2;
		int value = Float.compare(inst1.getDistance(), inst2.getDistance());
		if(value==0){
			return inst1.getResource().compareTo(inst2.getResource());
		}else{
			return value;
		}
		
	}
	
}