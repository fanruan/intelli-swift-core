/**
 * 
 */
package com.fr.bi.etl.analysis.report.widget.field.filtervalue.number;

/**
 * @author Daniel
 *
 */
public class Small implements Operator {
	
	static final Small INSTANCE = new Small();


	@Override
	public String getOperator() {
		return "<";
	}


	@Override
	public boolean Compare(double a, double b) {
		return a < b;
	}

}