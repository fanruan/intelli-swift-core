/**
 * 
 */
package com.fr.bi.etl.analysis.report.widget.field.filtervalue.number;

/**
 * @author Daniel
 *
 */
public class LargeOrEquals implements Operator {
	
	static final LargeOrEquals INSTANCE = new LargeOrEquals();

	@Override
	public String getOperator() {
		return ">=";
	}

	@Override
	public boolean Compare(double a, double b) {
		return a >= b;
	}

    @Override
    public String toString() {
        return "LargeOrEquals{}";
    }
}