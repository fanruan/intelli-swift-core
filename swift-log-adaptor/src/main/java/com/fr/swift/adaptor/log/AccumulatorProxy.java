package com.fr.swift.adaptor.log;

import com.fr.intelli.record.AccumulatorException;
import com.fr.intelli.record.scene.Accumulator;
import com.fr.intelli.record.scene.impl.BaseAccumulator;
import com.fr.log.FineLoggerFactory;
import com.fr.stable.query.condition.QueryCondition;
import com.fr.stable.query.data.DataList;
import com.fr.swift.event.ClusterEvent;
import com.fr.swift.event.ClusterEventListener;
import com.fr.swift.event.ClusterEventType;
import com.fr.swift.event.ClusterListenerHandler;

import java.util.Date;
import java.util.List;

/**
 * This class created on 2018/5/10
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class AccumulatorProxy extends BaseAccumulator {

    private Accumulator logOperator;
    private Accumulator singleLogOperator;
    private Accumulator clusterLogOperator;

    private AccumulatorProxy() {
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
    public <T> DataList<T> find(Class<T> aClass, QueryCondition queryCondition) throws AccumulatorException {
        synchronized (AccumulatorProxy.class) {
            return logOperator.find(aClass, queryCondition);
        }
    }

    @Override
    public DataList<List<Object>> find(String s) {
        return null;
    }

    @Override
    public void submit(Object o) {
        synchronized (AccumulatorProxy.class) {
            logOperator.submit(o);
        }
    }

    @Override
    public void submit(List<Object> list) {
        synchronized (AccumulatorProxy.class) {
            logOperator.submit(list);
        }
    }

    @Override
    public void pretreatment(List<Class> list) throws Exception {
        synchronized (AccumulatorProxy.class) {
            logOperator.pretreatment(list);
        }
    }

    @Override
    public void clearLogBefore(Date date) throws Exception {
        synchronized (AccumulatorProxy.class) {
            logOperator.clearLogBefore(date);
        }
    }

    public boolean switchSingle() {
        synchronized (AccumulatorProxy.class) {
            if (logOperator == clusterLogOperator) {
                logOperator = singleLogOperator;
                FineLoggerFactory.getLogger().info("LogOperator switch to single successfully!");
                return true;
            }
            return false;
        }
    }

    public boolean switchCluster() {
        synchronized (AccumulatorProxy.class) {
            if (logOperator == singleLogOperator) {
                if (clusterLogOperator == null) {
                    this.clusterLogOperator = new SwiftClusterLogOperator();
                }
                logOperator = clusterLogOperator;
                FineLoggerFactory.getLogger().info("LogOperator switch to cluster successfully!");
                return true;
            }
            return false;
        }
    }

    private static final Accumulator INSTANCE = new AccumulatorProxy();

    public static Accumulator getInstance() {
        return INSTANCE;
    }
}
