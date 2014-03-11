
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
            this.resultsDb.setLangCode(pair1.getLanguage());
            paths1 = this.resultsDb.getNormalizedPaths(pair1.getConcatPair());
            System.out.println("Paths 1: " + paths1);
            this.resultsDb.setLangCode(pair2.getLanguage());
            paths2 = this.resultsDb.getNormalizedPaths(pair2.getConcatPair());
            System.out.println("Paths 2: " + paths2);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(EvaluationComparator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(EvaluationComparator.class.getName()).log(Level.SEVERE, null, ex);
        }
        paths1.retainAll(paths2);
        return paths1;
    }
    
    public static void main(String[] args) {
        if (args.length != 6 && args.length != 4) {
            System.err.println("Extected arguments: <from1> <to1> <lang1> <from2> <to2> <lang2>");
            System.err.println("Extected arguments: <from1> <to1> <from2> <to2>");
            System.exit(255);
        }
        
        FromToPair pair1, pair2;
        if (args.length == 6) {
             pair1 = new FromToPair(args[0], args[1], args[2]);
             pair2 = new FromToPair(args[3], args[4], args[5]);
        } else {
             pair1 = new FromToPair(args[0], args[1], "");
             pair2 = new FromToPair(args[2], args[3], "");
        }
        
        EvaluationComparator evalComparator = new EvaluationComparator();
        System.out.println(evalComparator.findConventions(pair1, pair2));
    }
}
