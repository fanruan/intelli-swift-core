/**
 * 
 */
package com.fr.bi.conf.data.source.operator.add.date;

import java.util.Calendar;

/**
 * @author Daniel
 *
 */
public class MonthGetter implements DateGetter {
	
	public static final MonthGetter INSTANCE = new MonthGetter();

	@Override
	public long get(Long v) {
		if(v == null){
			return 0;
		}
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(v);
		//需要加1Calendar的月份从0开始-11
		return c.get(Calendar.MONTH) + 1;
	}

}