/**
 * 
 */
package com.fr.bi.etl.analysis.report.widget.field.filtervalue.number;

/**
 * @author Daniel
 *
 */
public class SmallOrEquals implements Operator {
	
	static final SmallOrEquals INSTANCE = new SmallOrEquals();

	@Override
	public String getOperator() {
		return "<=";
	}

	@Override
	public boolean Compare(double a, double b) {
		return a <= b;
	}

}