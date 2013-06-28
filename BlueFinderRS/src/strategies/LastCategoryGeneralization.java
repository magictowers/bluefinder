
package strategies;

/**
 * This class take a table with normalized paths and generates a new one clustering
 * normalized paths only by the last categories and replace the gneneral part 
 * with a wildcard. So:[from]/People_from_[from]/[to]  and 
 * [from]/others/People_from_[from]/[to] are relating with a more general path:
 * * /People_from_[from]/[to]
 * 
 * @author dtorres
 */

public class LastCategoryGeneralization {
    
    public String generalizePathQuery(String pathQuery){
        //[from]/something_algo/People_from_[from]/[to]
    	//fix for fr version [from]/something_algo/Portail:Italie/Articles liés/[to]
       String returnedPath = "";
       
       String[] tokens = pathQuery.split("\\/");
       if (tokens.length > 1 ){
    	   if(tokens[tokens.length-2].equals("Articles_liés")){
    		   returnedPath = "*/"+tokens[tokens.length-3]+"/"+tokens[tokens.length-2]+"/";
    	   }else{
          returnedPath = "*/"+tokens[tokens.length-2]+"/";}
       }
       
       returnedPath= returnedPath+tokens[tokens.length-1];
       
       
        return returnedPath;
    }
    
}
