package com.fr.swift.adaptor.log;

import com.fr.general.LogOperator;
import com.fr.stable.query.condition.QueryCondition;
import com.fr.stable.query.data.DataList;
import com.fr.swift.event.ClusterEvent;
import com.fr.swift.event.ClusterEventListener;
import com.fr.swift.event.ClusterEventType;
import com.fr.swift.event.ClusterListenerHandler;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;

import java.util.Date;
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
    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(LogOperatorProxy.class);

    private LogOperatorProxy() {
        this.singleLogOperator = new SwiftLogOperator();
        this.logOperator = singleLogOperator;
        ClusterListenerHandler.addListener(new ClusterEventListener() {
            @Override
            public void handleEvent(ClusterEvent clusterEvent) {
                if (clusterEvent.getEventType() == ClusterEventType.JOIN_CLUSTER) {
                    switchCluster();
                } else if (clusterEvent.getEventType() == ClusterEventType.LEFT_CLUSTER) {
                    switchSingle();
                }
            }
        });
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
    public DataList<List<Object>> find(String s) {
        return null;
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

    @Override
    public void clearLogBefore(Date date) throws Exception {
        synchronized (LogOperatorProxy.class) {
            logOperator.clearLogBefore(date);
        }
    }

    public boolean switchSingle() {
        synchronized (LogOperatorProxy.class) {
            if (logOperator == clusterLogOperator) {
                logOperator = singleLogOperator;
                LOGGER.info("LogOperator switch to single successfully!");
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
                LOGGER.info("LogOperator switch to cluster successfully!");
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
