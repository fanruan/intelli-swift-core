package com.fr.swift.source.db.dbdealer;

import com.fr.general.DateUtils;

import java.util.Date;

/**
 * Created by pony on 2018/1/16.
 */
public class Date2StringConvertDealer extends AbstractConvertDealer<String, Long> {
    public Date2StringConvertDealer(DBDealer<Long> outerDealer) {
        super(outerDealer);
    }

    @Override
    protected String convert(Long value) {
        return DateUtils.getDate2LStr(new Date(value));
    }

}
