/**
 * 
 */
package com.fr.bi.conf.report.filter;

/**
 * @author Daniel
 *
 */
public interface RowFilter<T> {

	boolean isMatchValue(T v);
	
}