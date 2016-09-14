/**
 * 
 */
package com.fr.bi.sql.analysis.report.widget.field.filtervalue.number;

/**
 * @author Daniel
 *
 */
public interface Operator {
	
	String getOperator();
	
	boolean Compare(double a, double b);

}