package utils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import pia.PIAConfigurationBuilder;
import strategies.IGeneralization;

public class PathsResolver {

	private String pathSeparator = ", ";  // " , ";
	public static String STEP_SEPARATOR = " / ";
    public static String CATEGORY_PREFIX = "Cat:";
	
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
			int count = -1;
			if (eqPos != -1) {
				String tmpCount = path.substring(eqPos + 1, path.length());
				try {
					count = Integer.parseInt(tmpCount);
				} catch (NumberFormatException ex) {
					count = -1;
				}
				// Finally, get the actual path
				path = path.substring(0, eqPos);
			}
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
	
	public String simpleCoupledPaths(List<String> paths) {
		String concatPath = "{";
		if (paths.size() > 0) {
			for (String path : paths) {
				concatPath += path + this.pathSeparator;
			}
			int extraConcat = concatPath.lastIndexOf(this.pathSeparator);
			concatPath = concatPath.substring(0, extraConcat);
		}
		return concatPath + "}";
	}
	
	public String simpleCoupledPaths(List<String> paths, boolean brackets) {
		String strPath = this.simpleCoupledPaths(paths);
		if (!brackets) {
			strPath = strPath.replaceFirst("{", "");
			strPath = strPath.replace("}", "");
		}
		return strPath;
	}
	
	public int generalizePaths(Map<Integer, String> paths) throws ClassNotFoundException, SQLException {
		int totalStarPaths = 0;		
        IGeneralization generalizator = PIAConfigurationBuilder.getGeneralizator();
		Set<String> starPaths = new HashSet<String>();
		for (int id : paths.keySet()) {
			String starPath = generalizator.generalizePathQuery(paths.get(id));
			starPaths.add(starPath);
		}
		totalStarPaths = starPaths.size();
		return totalStarPaths;
	}

	public static String pathToString(List<String> path) {
		String strPath = "";
		for (String step : path) {
			strPath += step + STEP_SEPARATOR;
		}
		return strPath.substring(0, strPath.lastIndexOf(STEP_SEPARATOR));
	}
	
	public String getPathSeparator() {
		return pathSeparator;
	}

	public void setPathSeparator(String pathSeparator) {
		this.pathSeparator = pathSeparator;
	}
}
