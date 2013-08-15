package utils;

import java.util.Comparator;
import java.util.Map;

class MapSorter implements Comparator<String> {

    Map<String, Float> base;
    
    public MapSorter(Map<String, Float> base) {
        this.base = base;
    }

    // Note: this comparator imposes orderings that are inconsistent with equals.    
    public int compare(String a, String b) {
        if (this.base.get(a) < this.base.get(b)) {
            return -1;
        } else if (this.base.get(a) > this.base.get(b)) {
            return 1;
        } else {
        	return a.compareTo(b);
        }
    }
}