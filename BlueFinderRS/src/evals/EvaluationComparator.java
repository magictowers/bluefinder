
package evals;

import db.WikipediaConnector;
import db.utils.ResultsDbInterface;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import utils.FromToPair;
import utils.ProjectConfiguration;

/**
 *
 * @author mkaminose
 */
public class EvaluationComparator {
    
    private ResultsDbInterface resultsDb;
    
    public EvaluationComparator() {
        this.resultsDb = new ResultsDbInterface();
    }
    
    public Set<String> findConventions() {
        Set<String> paths1 = new HashSet<String>();
        Set<String> paths2 = new HashSet<String>();
        
        paths1.retainAll(paths2);
        return paths1;
    }
    
    public Set<String> findConventions(FromToPair pair1, FromToPair pair2, String props1, String props2) {
        Set<String> paths1 = new HashSet<String>();
        Set<String> paths2 = new HashSet<String>();
        try {
//            ProjectConfiguration.setCurrentPropertiesSource(props2);
            ProjectConfiguration.useProperties1();
            paths1 = this.resultsDb.getNormalizedPaths(pair1.getConcatPair());
            System.out.println("Paths 1: " + paths1);
            WikipediaConnector.closeResultConnection();
//            ProjectConfiguration.setCurrentPropertiesSource(props2);
            ProjectConfiguration.useProperties2();
            paths2 = this.resultsDb.getNormalizedPaths(pair2.getConcatPair());
            ProjectConfiguration.setToDefaultProperties();
            System.out.println("Paths 2: " + paths2);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(EvaluationComparator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(EvaluationComparator.class.getName()).log(Level.SEVERE, null, ex);
        }
        paths1.retainAll(paths2);
        return paths1;
    }
    
    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        if (args.length != 6 && args.length != 4 && args.length != 2 || args.length != 0) {
            System.err.println("Extected arguments: <from1> <to1> <prop1> <from2> <to2> <prop2>");
            System.err.println("Extected arguments: <from> <to> <prop1> <prop2>");
            System.err.println("Extected arguments: <limit> <offset>");
            System.exit(255);
        }
               
        FromToPair pair1, pair2;
        ResultsDbInterface resultsDb = new ResultsDbInterface();
        if (args.length == 6) {
            pair1 = new FromToPair(args[0], args[1], "");
            pair2 = new FromToPair(args[3], args[4], "");
        } else {
            // ProjectConfiguration.setCurrentPropertiesSource(args[2]);
            ProjectConfiguration.useProperties1();
            pair1 = resultsDb.getTranslatedTuple(args[0], args[1]);
            System.out.println("pair1: " + pair1);
            // ProjectConfiguration.setCurrentPropertiesSource(args[3]);
            ProjectConfiguration.useProperties2();
            pair2 = resultsDb.getTranslatedTuple(args[0], args[1]);
            System.out.println("pair2: " + pair2);
        }
        
        EvaluationComparator evalComparator = new EvaluationComparator();
        System.out.println(evalComparator.findConventions(pair1, pair2, args[2], args[5]));
    }
}
