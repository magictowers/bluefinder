package knn;

public class Instance {
	private float distance;
	private String resource;
	private String types;
	private int id;

	public Instance(float distance, String resource, String types, int id) {
		super();
		this.distance = distance;
		this.resource = resource;
		this.types = types;
		this.id = id;
	}
	public float getDistance() {
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
	public int getId() {
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
