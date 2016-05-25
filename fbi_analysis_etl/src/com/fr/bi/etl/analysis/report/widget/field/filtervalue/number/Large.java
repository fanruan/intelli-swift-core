/**
 * 
 */
package com.fr.bi.etl.analysis.report.widget.field.filtervalue.number;

/**
 * @author Daniel
 *
 */
public class Large implements Operator {
	
	static final Large INSTANCE = new Large();

	@Override
	public String getOperator() {
		return ">";
	}

	@Override
	public boolean Compare(double a, double b) {
		return a > b;
	}

    @Override
    public String toString() {
        return "Large{}";
    }
}