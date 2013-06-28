package knn;

import java.util.List;

public class PlusYagoJaccardFunction extends JaccardFunction {
	
	@Override
	public float distance(String types1, String types2){
		List<String> typesList1 = this.splitTypes(types1);
		List<String> typesList2 = this.splitTypes(types2);
		float original = this.distance(typesList1, this.splitTypes(types2));
		if(!(this.containsYagoTypes(typesList1) && this.containsYagoTypes(typesList2))){
			original = 1;
		}
		return original;
	}

	private boolean containsYagoTypes(List<String> typesList1) {
		for (String string : typesList1) {
			if(string.contains("yago")){
				return true;
			}
		}
		return false;
	}

}
