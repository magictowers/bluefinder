package strategies;

import java.util.List;

/**
 *
 * @author mkaminose
 */
public interface IGeneralization {
    
    public abstract String generalizePathQuery(String pathQuery);
    public abstract String generalizePathQuery(List<String> pathQuery);
}
