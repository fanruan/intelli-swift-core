/**
 * 
 */
package com.fr.bi.conf.data.source.operator.add.date;

import java.util.Calendar;

/**
 * @author Daniel
 *
 */
public class YearGetter implements DateGetter {
	
	public static final YearGetter INSTANCE = new YearGetter();

	@Override
	public Integer get(Long v) {
		if(v == null){
			return null;
		}
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(v);
		return c.get(Calendar.YEAR);
	}

}