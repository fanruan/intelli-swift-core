/**
 * 
 */
package com.fr.bi.conf.data.source.operator.add.datediff;

import com.fr.bi.stable.constant.DateConstant;

/**
 * @author Daniel
 *
 */
public class DayDiff implements DateDiffCalculator {
	
	public static final DayDiff INSTANCE = new DayDiff();

	@Override
	public int get(Long d1, Long d2) {
		if(d1 == null || d2 == null){
			return 0;
		}
		long t = d1.longValue() - d2.longValue();
		return (int) (t / DateConstant.DATEDELTRA.DAY);
	}

}