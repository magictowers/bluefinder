package knn;

import knn.distance.SemanticPair;

public class Instance {
	private double distance;
	private String resource;
	private String types;
	private long id;

	public Instance(double distance, String resource, String types, long id) {
		super();
		this.distance = distance;
		this.resource = resource;
		this.types = types;
		this.id = id;
	}
	
	public Instance(SemanticPair pair, double distance){
		this(distance, pair.getSubject().toString()+" , "+pair.getObject().toString(), 
				pair.getSubjectElementsBySemProperty("type").toString()+" ,, "+pair.getObjectElementsBySemProperty("type").toString(), pair.getId());
	}
	public double getDistance() {
		return distance;
	}
	public void setDistance(float distance) {
		this.distance = distance;
	}
	public String getResource() {
		return resource;
	}
	public String getTypes() {
		return types;
	}
	public long getId() {
		return id;
	}
	
	@Override
	public String toString(){
		return this.getResource()+"-"+this.getDistance();
	}
	
	@Override
	public boolean equals(Object obj){
		if (obj instanceof Instance){
			Instance other = (Instance) obj;
			return (other.getResource().equals(this.getResource()));
		}else{
			return false;
		}
	}
	
	

}
