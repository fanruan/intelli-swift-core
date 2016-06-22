/**
 * 
 */
package com.fr.bi.conf.data.source.operator.add.date;

import com.fr.bi.stable.constant.DateConstant;

/**
 * @author Daniel
 *
 */
public class SeasonGetter extends MonthGetter {

	public static final SeasonGetter INSTANCE = new SeasonGetter();
	@Override
	public int get(Long v) {
        int month = super.get(v);
		return (month - 1) / DateConstant.DATEDELTRA.MONTH_OF_SEASON + 1;
	}

}