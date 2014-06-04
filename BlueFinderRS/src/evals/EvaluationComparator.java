
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
import utils.PathsResolver;
import utils.ProjectConfigurationReader;

/**
 *
 * @author mkaminose
 */
public class EvaluationComparator {
    
    private ResultsDbInterface resultsDb;
    
    public EvaluationComparator() throws SQLException, ClassNotFoundException {
        this.resultsDb = new ResultsDbInterface();
    }
    
    /**
     * Iterate over dbpedia tuples, according to `limit` and `offset`
     * @param prop1 setup file for language 1
     * @param prop2 setup file for language 2
     * @param limit
     * @param offset
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException 
     */
    public Map<String, Set<String>> findConventions(String prop1, String prop2, int limit, int offset) throws ClassNotFoundException, SQLException {
        Set<String> paths1 = new HashSet<String>();
        Set<String> paths2 = new HashSet<String>();
        Map<String, Set<String>> conventions = new HashMap<String, Set<String>>();
        
        List<Map<String, String>> dbpediaTuples = this.resultsDb.getDbpediaCombinedTuples(limit, offset);
        List<Map<String, Object>> combinedPaths1 = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> combinedPaths2 = new ArrayList<Map<String, Object>>();
        
        ProjectConfigurationReader.useProperties1();
        for (Map<String, String> transTuple : dbpediaTuples) {
            FromToPair pair1 = new FromToPair(transTuple.get("from1"), transTuple.get("to1"), "");
            Set<String> pair1paths = this.resultsDb.getNormalizedPaths(pair1.getConcatPair());
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("pair", pair1.getConcatPair());
            map.put("paths", pair1paths);
            combinedPaths1.add(map);
        }
        
        ProjectConfigurationReader.useProperties2();
        for (Map<String, String> transTuple : dbpediaTuples) {
            FromToPair pair2 = new FromToPair(transTuple.get("from2"), transTuple.get("to2"), "");
            Set<String> pair2paths = this.resultsDb.getNormalizedPaths(pair2.getConcatPair());
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("paths", pair2paths);
            map.put("pair", pair2.getConcatPair());
            combinedPaths2.add(map);
        }
        
        for (int i = 0; i < combinedPaths1.size(); i++) {
            Map<String, Object> map1 = combinedPaths1.get(i);
            Map<String, Object> map2 = combinedPaths2.get(i);
            paths1 = (Set<String>)map1.get("paths");
            paths2 = (Set<String>)map2.get("paths");
            paths1.retainAll(paths2);
            conventions.put(String.format("%s%s%s", (String)map1.get("pair"), PathsResolver.STEP_SEPARATOR, (String)map2.get("pair")), paths1);
        }
        
        return conventions;
    }
    
    public Map<String, Set<String>> findConventions(String prop1, String prop2) throws ClassNotFoundException, SQLException {
        return this.findConventions(prop1, prop2, 0, 0);
    }
    
    /**
     * 
     * @param pair1 for language 1
     * @param pair2 for language 2
     * @param props1 setup file for language 1
     * @param props2 setup file for language 2
     * @return 
     */
    public Set<String> findConventions(FromToPair pair1, FromToPair pair2, String props1, String props2) {
        Set<String> paths1 = new HashSet<String>();
        Set<String> paths2 = new HashSet<String>();
        try {
            if (pair1 != null && pair2 != null) {
                WikipediaConnector.closeConnection();
                ProjectConfigurationReader.useProperties1();
                paths1 = this.resultsDb.getNormalizedPaths(pair1.getConcatPair());
                WikipediaConnector.closeConnection();
                ProjectConfigurationReader.useProperties2();
                paths2 = this.resultsDb.getNormalizedPaths(pair2.getConcatPair());
                ProjectConfigurationReader.setToDefaultProperties();
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
    
    /**
     * 
     * @param from1 for language 1
     * @param to1 for language 1
     * @param prop1 setup file for language 1
     * @param from2 for language 2
     * @param to2 for language 2
     * @param prop2 setup file for language 2
     * @return 
     */
    public Set<String> findConventions(String from1, String to1, String prop1, String from2, String to2, String prop2) {
        FromToPair pair1, pair2;
        pair1 = new FromToPair(from1, to1, "");
        pair2 = new FromToPair(from2, to2, "");
        return findConventions(pair1, pair2, prop1, prop2);
    }
    
    /**
     * 
     * @param from in English
     * @param to in English
     * @param prop1 setup file for language 1
     * @param prop2 setup file for language 2
     * @return the intersection of pair_lang1 and pair_lang2 paths
     * @throws SQLException
     * @throws ClassNotFoundException 
     */
    public Set<String> findConventions(String from, String to, String prop1, String prop2) throws SQLException, ClassNotFoundException {
        FromToPair pair1, pair2;
        ProjectConfigurationReader.useProperties1();
        pair1 = resultsDb.getTranslatedTuple(from, to);
        ProjectConfigurationReader.useProperties2();
        pair2 = resultsDb.getTranslatedTuple(from, to);
        
        ProjectConfigurationReader.useDefaultProperties();
        return this.findConventions(pair1, pair2, prop1, prop2);
    }
        
    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        int argsLength = args.length;
        if (argsLength != 6 && argsLength != 4 && argsLength != 2) {
            System.err.println("Extected arguments: <from1> <to1> <prop1> <from2> <to2> <prop2>");
            System.err.println("Extected arguments: <from> <to> <prop1> <prop2>");
            System.err.println("Extected arguments: <prop1> <prop2> [<limit> <offset>]");
            System.out.println("<propX> por el momento se pasan strings cualquiera");
            System.exit(255);
        }
               
        EvaluationComparator evalComparator = new EvaluationComparator();
        Object conventions;
        if (argsLength == 6) {
            conventions = evalComparator.findConventions(args[0], args[1], args[2], args[3], args[4], args[5]);
        } else if (argsLength == 4) {
            int limit = 0;
            int offset = 0;
            try {
                limit = Integer.parseInt(args[2]);
                offset = Integer.parseInt(args[3]);
                conventions = evalComparator.findConventions(args[0], args[1], limit, offset);
                Map<String, Set<String>> tmp = (Map<String, Set<String>>)conventions;
                System.out.printf("%d comparaciones hechas.\n", tmp.keySet().size());
                for (String key : tmp.keySet()) {
                    System.out.println(String.format("%s: %s", key, tmp.get(key)));
                }
            } catch (NumberFormatException ex) {
                conventions = evalComparator.findConventions(args[0], args[1], args[2], args[3]);
            }           
        } else {
            conventions = evalComparator.findConventions(args[0], args[1]);
            Map<String, Set<String>> tmp = (Map<String, Set<String>>)conventions;
            System.out.printf("%d comparaciones hechas.\n", tmp.keySet().size());
            for (String key : tmp.keySet()) 
                System.out.println(String.format("%s: %s", key, tmp.get(key)));
        }
        System.out.println(conventions);
    }
}
