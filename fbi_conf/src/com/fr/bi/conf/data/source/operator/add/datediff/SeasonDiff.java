/**
 * 
 */
package com.fr.bi.conf.data.source.operator.add.datediff;

import com.fr.bi.stable.constant.DateConstant;

/**
 * @author Daniel
 *
 */
public class SeasonDiff extends MonthDiff {

	public static final SeasonDiff INSTANCE = new SeasonDiff();
	@Override
	public int get(Long d1, Long d2) {
		int month = super.get(d1, d2);
		return month / DateConstant.DATEDELTRA.MONTH_OF_SEASON;
	}
}