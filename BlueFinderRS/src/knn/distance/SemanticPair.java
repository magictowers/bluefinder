package knn.distance;

import java.util.ArrayList;
import java.util.List;

public class SemanticPair implements ISemPair{
	
	private String object;
	private String subject;
	private String semProperty;
	private List<String> objectTypes;
	private List<String> subjectTypes;
	private long id;
	
	public SemanticPair(String object, String subject, String semProperty, List<String> objectTypes, List<String> subjectTypes, long id){
		this.object=object;
		this.subject=subject;
		this.semProperty=semProperty;
		this.objectTypes=objectTypes;
		this.subjectTypes=subjectTypes;
		this.id = id;
	}

	@Override
	public String getObject() {
		return this.object;
	}

	@Override
	public String getSubject() {
		return this.subject;
	}

	@Override
	public String getSemProperty() {
		return this.semProperty;
	}

	@Override
	public List<String> getSubjectElementsBySemProperty(String semProperty) {
		return this.subjectTypes;
	}

	@Override
	public List<String> getObjectElementsBySemProperty(String semProperty) {
		return this.objectTypes;
	}

	@Override
	public List<String> getSemPropertyElementsBySemProperty(String semProperty) {
		return new ArrayList<String>();
	}

	public long getId() {
		return this.id;
	}

}
