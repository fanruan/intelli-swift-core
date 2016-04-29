/**
 * 
 */
package com.fr.bi.cal.stable.cube;

import com.fr.bi.stable.file.ColumnFile;

import java.util.Map;

/**
 * @author Daniel
 *
 */
public class ColumnFiles {
	
	 private ColumnFile<?>[] columns;
	    
	 private Map<String, Integer> colIndexMap;

	 public ColumnFiles(ColumnFile<?>[] columns, Map<String, Integer> colIndexMap){
		 this.columns = columns;
		 this.colIndexMap = colIndexMap;
	 }
	 
	 public ColumnFile getColumnFile(String key){
		 return getColumnFile(colIndexMap.get(key));
	 }
	 
	 public ColumnFile getColumnFile(int index){
		 return columns[index];
	 }
	 
	 public int size(){
		 return columns.length;
	 }
}