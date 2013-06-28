/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pia;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/**
 *
 * @author dtorres
 */
public class BipartiteGraphFromTextFile {
    public static void main(String[] args){
      File archivo = null;
      FileReader fr = null;
      BufferedReader br = null;
      String fileName  = args[0];
      String pageFrom;
      String pageTo;
      BipartiteGraphGenerator bipartiteGraphGenerator = new BipartiteGraphGenerator();

      try {
         // Apertura del fichero y creacion de BufferedReader para poder
         // hacer una lectura comoda (disponer del metodo readLine()).
         archivo = new File (fileName);
         fr = new FileReader (archivo);
         br = new BufferedReader(fr);
         int processed = 1;

         // Lectura del fichero
         String linea;
         while((linea=br.readLine())!=null){
            String[] pairs = linea.split("\\;");
              //pageFrom=pairs[0];
              //pageTo=pairs[1];
              
              pageFrom=pairs[1].trim();
              pageTo=pairs[2].trim();
              System.out.println("Processing line: "+ processed);
              bipartiteGraphGenerator.generateBiGraph(pageFrom, pageTo);
              System.out.print("Done !");
              processed++;
           }
          
         
      }
      catch(Exception e){
         e.printStackTrace();
      }finally{
         // En el finally cerramos el fichero, para asegurarnos
         // que se cierra tanto si todo va bien como si salta 
         // una excepcion.
         try{                    
            if( null != fr ){   
               fr.close();     
            }                  
         }catch (Exception e2){ 
            e2.printStackTrace();
         }
      }
    }
    
}
