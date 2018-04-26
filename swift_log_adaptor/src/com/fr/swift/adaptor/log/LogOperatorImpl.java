package com.fr.swift.adaptor.log;

import com.fr.general.DataList;
import com.fr.general.LogOperator;
import com.fr.stable.query.condition.QueryCondition;

import java.util.List;

/**
 * @author anchore
 * @date 2018/4/26
 */
public class LogOperatorImpl implements LogOperator {
    @Override
    public <T> DataList<T> find(Class<T> aClass, QueryCondition queryCondition) {
        return null;
    }

    @Override
    public <T> DataList<T> find(Class<T> aClass, QueryCondition queryCondition, String s) {
        return null;
    }

    @Override
    public void recordInfo(Object o) {

    }

    @Override
    public void recordInfo(List<Object> list) {

    }

    @Override
    public void initTables(List<Class> list) {

    }
}