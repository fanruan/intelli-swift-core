package com.fr.swift.source.etl.date;

import com.fr.swift.source.etl.utils.ETLConstant;

/**
 * @author Daniel
 *
 */
public class SeasonGetter extends MonthGetter {

    public static final SeasonGetter INSTANCE = new SeasonGetter();
    @Override
    public Integer get(Long v) {
        if(v == null){
            return null;
        }
        Integer month = super.get(v);
        return (month - 1) / ETLConstant.CONF.DATEDELTRA.MONTH_OF_SEASON + 1;
    }

}
