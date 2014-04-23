package db;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Represents the set of results. This set has no duplicated values
 * @author dtorres
 *
 */

public class DBResultSetImp implements DBpediaResultSet {
	
	private Set<ResultElement> elements;
	
	public DBResultSetImp(){
		this.elements=new LinkedHashSet<ResultElement>();
	}

	@Override
	public int size() {
		return this.elements.size();
	}

	@Override
	public Iterator<ResultElement> getIterator() {
		return this.elements.iterator();
	}

	@Override
	public Iterator<ResultElement> iterator() {
		return this.getIterator();
	}
	
	public void addElement(ResultElement element){
		this.elements.add(element);
	}

}
