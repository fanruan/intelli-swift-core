package com.fr.swift.source.db.dbdealer;

import com.fr.stable.StableUtils;

/**
 * Created by pony on 2018/1/17.
 */
public class String2NumberConvertDealer extends AbstractConvertDealer<Double, String> {
    public String2NumberConvertDealer(DBDealer<String> outerDealer) {
        super(outerDealer);
    }

    @Override
    protected Double convert(String s) {
        Number n = StableUtils.string2Number(s);
        return n == null ? null : n.doubleValue();
    }
}
