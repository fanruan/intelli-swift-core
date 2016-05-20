/**
 * 
 */
package com.fr.bi.etl.analysis.report.widget.field.filtervalue.number;

/**
 * @author Daniel
 *
 */
public class NumberLargeOrEqualsCLFilter extends NumberCalculateLineFilter {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3026020133625295929L;

	/**
	 * @param t
	 */
	public NumberLargeOrEqualsCLFilter() {
		super(LargeOrEquals.INSTANCE);
	}

    protected  void parsClose(boolean isClose){
        t = isClose ? LargeOrEquals.INSTANCE : Large.INSTANCE;
    }
}