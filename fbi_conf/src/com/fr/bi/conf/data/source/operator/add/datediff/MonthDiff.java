/**
 * 
 */
package com.fr.bi.conf.data.source.operator.add.datediff;

import java.util.Calendar;

import com.fr.bi.stable.constant.DateConstant;

/**
 * @author Daniel
 *
 */
public class MonthDiff implements DateDiffCalculator {

	public static final MonthDiff INSTANCE = new MonthDiff();
	@Override
	public int get(Long d1, Long d2) {
		if(d1 == null || d2 == null){
			return 0;
		}
		Calendar c1 = Calendar.getInstance();
		c1.setTimeInMillis(d1.longValue());
		Calendar c2 = Calendar.getInstance();
		c2.setTimeInMillis(d2.longValue());
		int year = c2.get(Calendar.YEAR) - c1.get(Calendar.YEAR);
		int month = c2.get(Calendar.MONTH) - c1.get(Calendar.MONTH);
		return year * DateConstant.DATEDELTRA.MONTH_OF_YEAR + month;
	}

}