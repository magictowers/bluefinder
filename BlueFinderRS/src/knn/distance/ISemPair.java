package knn.distance;

import java.util.List;

public interface ISemPair {
	String getObject();
	String getSubject();
	String getSemProperty();
	List<String> getSubjectElementsBySemProperty(String semProperty);
	List<String> getObjectElementsBySemProperty(String semProperty);
	List<String> getSemPropertyElementsBySemProperty(String semProperty);
}
