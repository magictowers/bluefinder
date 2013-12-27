
package evals;

import db.utils.ResultsDbInterface;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import utils.FromToPair;

/**
 *
 * @author mkaminose
 */
public class EvaluationComparator {
    
    private ResultsDbInterface resultsDb;
    
    public EvaluationComparator() {
        this.resultsDb = new ResultsDbInterface();
    }
    
    public Set<String> findConventions(FromToPair pair1, FromToPair pair2) {
        Set<String> paths1 = new HashSet<String>();
        Set<String> paths2 = new HashSet<String>();
        try {
            paths1 = this.resultsDb.getNormalizedPaths(pair1.getConcatPair());
            System.out.println(paths1);
            paths2 = this.resultsDb.getNormalizedPaths(pair2.getConcatPair());
            System.out.println(paths2);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(EvaluationComparator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(EvaluationComparator.class.getName()).log(Level.SEVERE, null, ex);
        }
        paths1.retainAll(paths2);
        return paths1;
    }
}
