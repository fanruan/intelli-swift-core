package com.fr.swift.source.db.dbdealer;

import com.fr.general.DateUtils;

/**
 * Created by pony on 2018/1/17.
 */
public class String2DateConvertDealer extends AbstractConvertDealer<Long, String> {
    public String2DateConvertDealer(DBDealer<String> outerDealer) {
        super(outerDealer);
    }

    @Override
    protected Long convert(String s) {
        return DateUtils.string2Date(s, false).getTime();
    }
}
