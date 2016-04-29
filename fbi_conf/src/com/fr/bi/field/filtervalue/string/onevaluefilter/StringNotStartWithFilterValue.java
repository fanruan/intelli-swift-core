/**
 * 
 */
package com.fr.bi.field.filtervalue.string.onevaluefilter;

/**
 * @author Daniel
 *
 */
public class StringNotStartWithFilterValue extends StringOneValueFilterValue {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4674486222319107741L;


	/* (non-Javadoc)
	 * @see com.fr.bi.cal.analyze.report.report.widget.field.filtervalue.string.onevaluefilter.StringOneValueFilterValue#contains(java.lang.String)
	 */
	@Override
	public boolean isMatchValue(String key) {
		return !key.startsWith(value);
	}

}