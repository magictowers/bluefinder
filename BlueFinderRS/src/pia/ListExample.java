/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pia;

import java.util.List;
import java.util.Vector;

/**
 *
 * @author dtorres
 */
public class ListExample {
    
    public static void main(String[] args){
        List<Integer> lista = new Vector<Integer>();
        Integer uno = 1;
        Integer otroUno = 1;
        
        lista.add(uno);
        System.out.println("Esta el uno?" + lista.contains(otroUno));
    }
    
}
