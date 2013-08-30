package utils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PathsResolver {

	private String pathSeparator = " , ";
	
	public PathsResolver() {
	}
		
 	public PathsResolver(String pathSeparator) {
		this.pathSeparator = pathSeparator;
	}

	public Map<String, Integer> decouple(String paths) {
		// First, remove the `{` and `}`
		paths = paths.replaceFirst("\\{", "");
		paths = paths.substring(0, paths.length() - 1);
		String[] tmpDecoupledPaths = paths.split(this.pathSeparator);
		Map<String, Integer> decoupledPaths = new LinkedHashMap<String, Integer>();
		for(String path : tmpDecoupledPaths) {
			// Get the path count
			int eqPos = path.lastIndexOf("=");
			String tmpCount = path.substring(eqPos + 1, path.length());
			int count = 0;
			try {
				count = Integer.parseInt(tmpCount);
			} catch (NumberFormatException ex) {
				count = -1;
			}
			// Finally, get the actual path
			path = path.substring(0, eqPos);
			decoupledPaths.put(path, count);
		}
		return decoupledPaths;
	}
	
	public List<String> simpleDecoupledPaths(String paths) {
		Map<String, Integer> tmpDecoupledPaths = this.decouple(paths);
		Set<String> keys = tmpDecoupledPaths.keySet();
		List<String> decoupledPaths = new ArrayList<String>();
		for(String path : keys) {
			decoupledPaths.add(path);
		}
		return decoupledPaths;
	}
	
	public String simpleDoupledPaths(List<String> paths) {
		String concatPath = "{";
		for (String path : paths) {
			concatPath += path + this.pathSeparator;
		}
		int extraConcat = concatPath.lastIndexOf(this.pathSeparator);
		concatPath = concatPath.substring(0, extraConcat);
		return concatPath + "}";
	}
	
	public String getPathSeparator() {
		return pathSeparator;
	}

	public void setPathSeparator(String pathSeparator) {
		this.pathSeparator = pathSeparator;
	}
}
