package com.fr.swift.adaptor.log;

import com.fr.general.DataList;
import com.fr.general.LogOperator;
import com.fr.stable.query.condition.QueryCondition;

import java.util.List;

/**
 * This class created on 2018/5/10
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class LogOperatorProxy implements LogOperator {

    private LogOperator logOperator;

    private LogOperator singleLogOperator;
    private LogOperator clusterLogOperator;

    private LogOperatorProxy() {
        this.singleLogOperator = new SwiftLogOperator();
        //默认单机
        this.logOperator = singleLogOperator;
    }

    @Override
    public <T> DataList<T> find(Class<T> aClass, QueryCondition queryCondition) throws Exception {
        synchronized (LogOperatorProxy.class) {
            return logOperator.find(aClass, queryCondition);
        }
    }

    @Override
    public <T> DataList<T> find(Class<T> aClass, QueryCondition queryCondition, String s) throws Exception {
        synchronized (LogOperatorProxy.class) {
            return logOperator.find(aClass, queryCondition, s);
        }
    }

    @Override
    public void recordInfo(Object o) {
        synchronized (LogOperatorProxy.class) {
            logOperator.recordInfo(o);
        }
    }

    @Override
    public void recordInfo(List<Object> list) {
        synchronized (LogOperatorProxy.class) {
            logOperator.recordInfo(list);
        }
    }

    @Override
    public void initTables(List<Class> list) throws Exception {
        synchronized (LogOperatorProxy.class) {
            logOperator.initTables(list);
        }
    }

    public boolean switchSingle() {
        synchronized (LogOperatorProxy.class) {
            if (logOperator == clusterLogOperator) {
                logOperator = singleLogOperator;
                return true;
            }
            return false;
        }
    }

    public boolean switchCluster() {
        synchronized (LogOperatorProxy.class) {
            if (logOperator == singleLogOperator) {
                if (clusterLogOperator == null) {
                    this.clusterLogOperator = new SwiftClusterLogOperator();
                }
                logOperator = clusterLogOperator;
                return true;
            }
            return false;
        }
    }

    private static final LogOperator INSTANCE = new LogOperatorProxy();

    public static LogOperator getInstance() {
        return INSTANCE;
    }
}
