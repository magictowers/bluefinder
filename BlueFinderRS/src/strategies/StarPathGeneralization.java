package strategies;

import java.util.ArrayList;
import java.util.List;
import utils.FromToPair;
import utils.PathsResolver;

public class StarPathGeneralization implements IGeneralization {

    @Override
    public String generalizePathQuery(String pathQuery) {
        String returnedPath = "";
        List<String> steps = new ArrayList<String>();
        String[] tokens = pathQuery.split(PathsResolver.STEP_SEPARATOR);
        for (String t : tokens)
            steps.add(t);
        
        if (tokens.length > 2 ) {
            returnedPath = "* / "+tokens[tokens.length-2]+" / ";}
       
        returnedPath = "#from / "+returnedPath+tokens[tokens.length-1];
        
        return this.generalizePathQuery(steps);
    }

    @Override
    public String generalizePathQuery(List<String> pathQuery) {
        String starPath = "";
        System.out.println(pathQuery);
        if (pathQuery.size() > 3 &&
               pathQuery.get(pathQuery.size() - 2).contains(PathsResolver.CATEGORY_PREFIX)) {
            starPath = FromToPair.FROM_WILDCARD + PathsResolver.STEP_SEPARATOR + "*";
            starPath += PathsResolver.STEP_SEPARATOR + pathQuery.get(pathQuery.size() - 2);
            starPath += PathsResolver.STEP_SEPARATOR + FromToPair.TO_WILDCARD;
            System.out.println("Converted: " + starPath);
        } else {
            starPath = PathsResolver.pathToString(pathQuery);
            System.out.println(starPath);
        }
        return starPath;
    }

}
