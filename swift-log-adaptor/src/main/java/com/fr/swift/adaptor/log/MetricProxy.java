package com.fr.swift.adaptor.log;

import com.fr.intelli.record.MetricException;
import com.fr.intelli.record.scene.Metric;
import com.fr.intelli.record.scene.impl.BaseMetric;
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
public class MetricProxy extends BaseMetric {

    private Metric logOperator;
    private Metric singleLogOperator;
    private Metric clusterLogOperator;

    private MetricProxy() {
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
    public <T> DataList<T> find(Class<T> aClass, QueryCondition queryCondition) throws MetricException {
        synchronized (MetricProxy.class) {
            return logOperator.find(aClass, queryCondition);
        }
    }

    @Override
    public DataList<List<Object>> find(String s) {
        return null;
    }

    @Override
    public void submit(Object o) {
        synchronized (MetricProxy.class) {
            logOperator.submit(o);
        }
    }

    @Override
    public void submit(List<Object> list) {
        synchronized (MetricProxy.class) {
            logOperator.submit(list);
        }
    }

    @Override
    public void pretreatment(List<Class> list) throws Exception {
        synchronized (MetricProxy.class) {
            logOperator.pretreatment(list);
        }
    }

    @Override
    public void clean(QueryCondition condition) throws Exception {
        synchronized (MetricProxy.class) {
            logOperator.clean(condition);
        }
    }

    public boolean switchSingle() {
        synchronized (MetricProxy.class) {
            if (logOperator == clusterLogOperator) {
                logOperator = singleLogOperator;
                FineLoggerFactory.getLogger().info("LogOperator switch to single successfully!");
                return true;
            }
            return false;
        }
    }

    public boolean switchCluster() {
        synchronized (MetricProxy.class) {
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

    private static final Metric INSTANCE = new MetricProxy();

    public static Metric getInstance() {
        return INSTANCE;
    }
}
