package com.fr.swift.source.db.dbdealer;

/**
 * Created by pony on 2018/1/17.
 */
public class Number2StringConvertDealer<T extends Number> extends AbstractConvertDealer<String, T> {
    public Number2StringConvertDealer(DBDealer<T> outerDealer) {
        super(outerDealer);
    }

    @Override
    protected String convert(T t) {
        return t.toString();
    }
}
