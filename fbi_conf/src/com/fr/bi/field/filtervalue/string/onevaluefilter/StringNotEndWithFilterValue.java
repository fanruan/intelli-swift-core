/**
 * 
 */
package com.fr.bi.field.filtervalue.string.onevaluefilter;

/**
 * @author Daniel
 *
 */
public class StringNotEndWithFilterValue extends StringOneValueFilterValue {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8113992480239929463L;


	/* (non-Javadoc)
	 * @see com.fr.bi.cal.analyze.report.report.widget.field.filtervalue.string.onevaluefilter.StringOneValueFilterValue#contains(java.lang.String)
	 */
	@Override
	public boolean isMatchValue(String key) {
		return !key.endsWith(value);
	}

}