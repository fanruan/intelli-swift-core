/**
 * 
 */
package com.fr.bi.conf.data.source.operator.add.datediff;

import java.util.Calendar;

/**
 * @author Daniel
 *
 */
public class YearDiff implements DateDiffCalculator {

	public static final YearDiff INSTANCE = new YearDiff();
	@Override
	public int get(Long d1, Long d2) {
		if(d1 == null || d2 == null){
			return 0;
		}
		Calendar c1 = Calendar.getInstance();
		c1.setTimeInMillis(d1.longValue());
		Calendar c2 = Calendar.getInstance();
		c2.setTimeInMillis(d2.longValue());
		return c1.get(Calendar.YEAR) - c2.get(Calendar.YEAR);
	}

}