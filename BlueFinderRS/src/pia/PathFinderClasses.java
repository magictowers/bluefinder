/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pia;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author dtorres
 */
public class PathFinderClasses {   
    
    public void getPaths(int cityPageId, int personPageId) {        
        List<Integer> categories = this.getCategories(cityPageId);
        for(Integer cat: categories){
        	this.getPathUsingCategories(cat,personPageId);    
        }
    }
    
    private List<Integer> getCategories(int pageId){
        List<Integer> listCategories = new ArrayList<Integer> ();
        
        return listCategories;
    }
    
    private void getPathUsingCategories(int catId, int personPageId){
        @SuppressWarnings("unused")
		List<List<String>> paths = new ArrayList<List<String>>();
    }    
}
