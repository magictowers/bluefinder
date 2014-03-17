
package evals;

import db.WikipediaConnector;
import db.utils.ResultsDbInterface;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
    
    public Set<String> findConventions() throws ClassNotFoundException, SQLException {
        Set<String> paths1 = new HashSet<String>();
        Set<String> paths2 = new HashSet<String>();
        
        List<Map<String, String>> dbpediaTuples = this.resultsDb.getDbpediaTuples();
        List<Map<String, Set<String>>> combinedPaths1 = new ArrayList<Map<String, Set<String>>>();
        List<Map<String, Object>> combinedPaths2 = new ArrayList<Map<String, Object>>();
        
        ProjectConfiguration.useProperties1();
        for (Map<String, String> transTuple : dbpediaTuples) {
            FromToPair pair1 = new FromToPair(transTuple.get("from1"), transTuple.get("to1"), "");
            Set<String> pair1paths = this.resultsDb.getNormalizedPaths(pair1.getConcatPair());
            Map<String, Set<String>> map = new HashMap<String, Set<String>>();
            map.put(pair1.getConcatPair(), pair1paths);
            combinedPaths1.add(map);
        }
        
        ProjectConfiguration.useProperties2();
        for (Map<String, String> transTuple : dbpediaTuples) {
            FromToPair pair2 = new FromToPair(transTuple.get("from2"), transTuple.get("to2"), "");
            Set<String> pair2paths = this.resultsDb.getNormalizedPaths(pair2.getConcatPair());
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("paths", pair2paths);
            map.put("pair", pair2.getConcatPair());
            combinedPaths2.add(map);
        }
        
        for (int i = 0; i < combinedPaths1.size(); i++) {
            Map<String, Set<String>> map1 = combinedPaths1.get(i);
            Map<String, Object> map2 = combinedPaths2.get(i);
            System.out.printf("%s --- %s\n", (String)map2.get("pair"));
            System.out.println();
        }
        
        paths1.retainAll(paths2);
        return paths1;
    }
    
    public Set<String> findConventions(FromToPair pair1, FromToPair pair2, String props1, String props2) {
        Set<String> paths1 = new HashSet<String>();
        Set<String> paths2 = new HashSet<String>();
        try {
            if (pair1 != null && pair2 != null) {
                WikipediaConnector.closeResultConnection();
                ProjectConfiguration.useProperties1();
                paths1 = this.resultsDb.getNormalizedPaths(pair1.getConcatPair());
                WikipediaConnector.closeResultConnection();
                ProjectConfiguration.useProperties2();
                paths2 = this.resultsDb.getNormalizedPaths(pair2.getConcatPair());
                ProjectConfiguration.setToDefaultProperties();
            } else {
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(EvaluationComparator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(EvaluationComparator.class.getName()).log(Level.SEVERE, null, ex);
        }
        paths1.retainAll(paths2);
        return paths1;
    }
    
    public Set<String> findConventions(String from1, String to1, String prop1, String from2, String to2, String prop2) {
        FromToPair pair1, pair2;
        pair1 = new FromToPair(from1, to1, "");
        pair2 = new FromToPair(from2, to2, "");
        return findConventions(pair1, pair2, prop1, prop2);
    }
    
    public Set<String> findConventions(String from, String to, String prop1, String prop2) throws SQLException, ClassNotFoundException {
        FromToPair pair1, pair2;
        ProjectConfiguration.useProperties1();
        pair1 = resultsDb.getTranslatedTuple(from, to);
        ProjectConfiguration.useProperties2();
        pair2 = resultsDb.getTranslatedTuple(from, to);
        
        ProjectConfiguration.useDefaultProperties();
        return this.findConventions(pair1, pair2, prop1, prop2);
    }
        
    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        int argsLength = args.length;
        if (argsLength != 6 && argsLength != 4 && argsLength != 2) {
            System.err.println("Extected arguments: <from1> <to1> <prop1> <from2> <to2> <prop2>");
            System.err.println("Extected arguments: <from> <to> <prop1> <prop2>");
            System.err.println("Extected arguments: <prop1> <prop2>");
            System.out.println("<propX> por el momento se pasan strings cualquiera");
            System.exit(255);
        }
               
        EvaluationComparator evalComparator = new EvaluationComparator();
        Set<String> conventions;
        if (argsLength == 6) {
            conventions = evalComparator.findConventions(args[0], args[1], args[2], args[3], args[4], args[5]);
        } else if (argsLength == 4) {
            conventions = evalComparator.findConventions(args[0], args[1], args[2], args[3]);
        } else {
            conventions = evalComparator.findConventions();
        }
        System.out.println(conventions);
    }
}
