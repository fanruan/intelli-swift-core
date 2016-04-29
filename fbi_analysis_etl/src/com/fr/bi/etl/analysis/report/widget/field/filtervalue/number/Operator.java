/**
 * 
 */
package com.fr.bi.etl.analysis.report.widget.field.filtervalue.number;

/**
 * @author Daniel
 *
 */
public interface Operator {
	
	public String getOperator();
	
	public boolean Compare(double a, double b);

}