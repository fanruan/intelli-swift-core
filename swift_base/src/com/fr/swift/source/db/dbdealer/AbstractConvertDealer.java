package com.fr.swift.source.db.dbdealer;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by pony on 2018/1/16.
 */
public abstract class AbstractConvertDealer<T, F> implements DBDealer<T> {
    private DBDealer<F> outerDealer;

    public AbstractConvertDealer(DBDealer<F> outerDealer) {
        this.outerDealer = outerDealer;
    }

    @Override
    public T dealWithResultSet(ResultSet rs) throws SQLException {
        F f = outerDealer.dealWithResultSet(rs);
        if (f != null){
            return convert(f);
        }
        return null;
    }

    protected abstract T convert(F f);


}
