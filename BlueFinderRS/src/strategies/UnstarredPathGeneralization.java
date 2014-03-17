package strategies;

import java.util.ArrayList;
import java.util.List;
import utils.FromToPair;
import utils.PathsResolver;


public class UnstarredPathGeneralization implements IGeneralization {

    @Override
    public String generalizePathQuery(String pathQuery) {
        List<String> steps = new ArrayList<String>();
        String[] tokens = pathQuery.split(PathsResolver.STEP_SEPARATOR);
        for (String t : tokens)
            steps.add(t);
        return this.generalizePathQuery(steps);
    }

    @Override
    public String generalizePathQuery(List<String> pathQuery) {
        String starPath;
        starPath = PathsResolver.pathToString(pathQuery);
        return starPath;
    }

}
