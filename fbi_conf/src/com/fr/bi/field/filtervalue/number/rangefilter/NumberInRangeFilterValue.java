package com.fr.bi.field.filtervalue.number.rangefilter;

public class NumberInRangeFilterValue extends NumberRangeFilterValue {
    /**
	 * 
	 */
	private static final long serialVersionUID = 6761227003309404043L;
	private static String XML_TAG = "NumberInRangeFilterValue";

    public NumberInRangeFilterValue() {
    }

    /**
     * @param min
     * @param closemin
     * @param max
     * @param closemax
     */
    public NumberInRangeFilterValue(double min, boolean closemin, double max, boolean closemax) {
        this.min = min;
        this.closemin = closemin;
        this.max = max;
        this.closemax = closemax;
    }

	@Override
	public boolean dealWithNullValue() {
		return false;
	}

	@Override
	protected boolean matchValue(double v) {
		return (closemin ? v >= min : v > min) && (closemax ? v <= max : v < max);
	}
}