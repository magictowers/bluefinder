package db;

import java.util.Iterator;

public interface DBpediaResultSet extends Iterable<ResultElement> {

	int size();

	Iterator<ResultElement> getIterator();
	
	void addElement(ResultElement element);

}
