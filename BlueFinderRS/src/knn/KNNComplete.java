package knn;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import db.MysqlIndexConnection;
import dbpedia.similarityStrategies.ValueComparator;
import pia.PIAConfigurationBuilder;
import strategies.IGeneralization;


public class KNNComplete {

    private KNN knn;

    public KNNComplete(KNN knn){
        this.setKnn(knn);

    }

    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        String piaIndexBase;
        String typesTable;

        if (args.length != 4) {
            System.out.println("Wrong number of params.");
            System.out.println("<piaIndexBase> <typesTable> <kValue> <resultTableName>");
            System.out.println("name of the piaIndexBase in localhost.");
            System.out
                    .println("Name of table with resource types in piaIndexBase");
            System.out.println("result table name");
            System.exit(1);
        }
        long startTime, endTime;
        startTime = System.currentTimeMillis();

        piaIndexBase = args[0];
        typesTable = args[1];
        int kValue = Integer.parseInt(args[2]);
        String resultTableName = args[3];

        KNN knn = new KNN(piaIndexBase, typesTable);
        KNNComplete knnComplete = new KNNComplete(knn);
        knnComplete.process(piaIndexBase, typesTable, kValue, resultTableName);
        endTime =  System.currentTimeMillis();
        System.out.println("the task has taken "+ ( (endTime - startTime) / 1000 ) +" seconds");
    }

    protected void process(String piaIndexBase, String typesTable, int kValue, 
            String resultTableName) throws ClassNotFoundException, SQLException {
        Connection connection = MysqlIndexConnection.getConnection(piaIndexBase);
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("select * from "+ typesTable + " where path_query='?'");
        String relatedVTo="v_to=0 ";
        String relatedString="";
        while (resultSet.next()) {
            long time_start, time_end;
            time_start = System.currentTimeMillis();
            Instance instance = new Instance(0,
                    resultSet.getString("resource"),
                    resultSet.getString("types"), 0);
        //	System.out.println("Resource: " + resultSet.getString("resource"));
            int kvalue=1;
            List<Instance> result =getKnn().compute(kValue, instance);
            List<String> knnResults = new ArrayList<String>();
            for (Instance instance2 : result) {
                relatedVTo=relatedVTo+"or v_to = "+instance2.getId()+ " ";
                relatedString = relatedString + "("+instance2.getDistance()+") "+instance2.getResource()+" " ;
                Statement st = connection.createStatement();
                String queryPaths="SELECT u_from, count(u_from) suma,V.path from UxV, V_Normalized V where u_from=V.id and (" 
                        +relatedVTo+") group by u_from order by suma desc ";
                ResultSet paths = st.executeQuery(queryPaths);
                TreeMap<String,Integer> map = this.genericPath(paths);
                //for (String pathGen : map.keySet()) {
            //		System.out.println(map);

                knnResults.add(map.toString());
            //	System.out.println("end ---- k="+kvalue);
                kvalue++;
            }
            time_end = System.currentTimeMillis();
            //Insert statem
            String insertSentence = "INSERT INTO `dbresearch`.`"+resultTableName+"` (`v_to`, `related_resources`,`1path`, `2path`, `3path`, `4path`, `5path`, `6path`, `7path`, `8path`, `9path`, `10path`,`time`)" + 
                    "VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )";
            PreparedStatement st = connection.prepareStatement(insertSentence);
            st.setString(1, resultSet.getString("resource"));
            st.setString(2, relatedString);
            int i=3;
            for (String string : knnResults) {
                st.setString(i, string);
                i++;
            }
            st.setLong(13, time_end - time_start);
            st.executeUpdate();

            relatedVTo="v_to=0 ";
            relatedString="";
        }
    }

    protected TreeMap<String,Integer> genericPath(ResultSet paths) throws SQLException {
        HashMap<String, Integer> pathDictionary = new HashMap<String, Integer>();
        ValueComparator bvc =  new ValueComparator(pathDictionary);
        TreeMap<String,Integer> sorted_map = new TreeMap<String,Integer>(bvc);
        IGeneralization cg = PIAConfigurationBuilder.getGeneralizator();
        while(paths.next()){
            String path=paths.getString("path");
            path=cg.generalizePathQuery(path);
            int suma = paths.getInt("suma");

            if((!path.contains("Articles_") || path.contains("Articles_li�s") ) && !path.contains("All_Wikipedia_") && !path.contains("Wikipedia_") && 
                    !path.contains("Non-free") && !path.contains("All_pages_") && !path.contains("All_non") ){
                if(pathDictionary.get(path)==null){
                    pathDictionary.put(path, suma);
                } else {
                    suma+=pathDictionary.get(path);
                    pathDictionary.put(path, suma);
                }
            }

        }
        //sorted_map.putAll(pathDictionary);
        for (String path : pathDictionary.keySet()) {
            Integer suma = pathDictionary.get(path);
            sorted_map.put(path,suma);
        }
        return sorted_map;
    }

    public KNN getKnn() {
        return knn;
    }

    public void setKnn(KNN knn) {
        this.knn = knn;
    }

}
