package com.fr.swift.source.db.dbdealer;

/**
 * Created by pony on 2018/1/17.
 */
public class Number2DateConvertDealer<T extends Number> extends AbstractConvertDealer<Long, T> {
    public Number2DateConvertDealer(DBDealer<T> outerDealer) {
        super(outerDealer);
    }

    @Override
    protected Long convert(T t) {
        return t.longValue();
    }
}
