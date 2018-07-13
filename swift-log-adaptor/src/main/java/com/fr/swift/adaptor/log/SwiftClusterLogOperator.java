package com.fr.swift.adaptor.log;

import com.fr.cluster.core.ClusterNode;
import com.fr.cluster.engine.rpc.base.InvokerManager;
import com.fr.cluster.rpc.base.Invocation;
import com.fr.cluster.rpc.base.Invoker;
import com.fr.cluster.rpc.base.Result;
import com.fr.intelli.record.scene.impl.BaseMetric;
import com.fr.stable.query.condition.QueryCondition;
import com.fr.stable.query.data.DataList;
import com.fr.swift.frrpc.FRClusterNodeManager;

import java.util.List;

/**
 * This class created on 2018/5/10
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class SwiftClusterLogOperator extends BaseMetric {

    private Invoker invoker;

    public SwiftClusterLogOperator() {
        invoker = InvokerManager.getInstance().create(MetricProxy.class);
    }

    @Override
    public <T> DataList<T> find(Class<T> aClass, QueryCondition queryCondition) {
        ClusterNode masterNode = FRClusterNodeManager.getInstance().getMasterNode();
        Invocation invocation = Invocation.create(MetricProxy.class, "find",
                new Class[]{Class.class, QueryCondition.class}, aClass, queryCondition);
        Result result = invoker.invoke(masterNode, invocation);
        return (DataList) result.get();
    }

    @Override
    public DataList<List<Object>> find(String s) {
        return null;
    }

    @Override
    public void submit(Object o) {
        ClusterNode masterNode = FRClusterNodeManager.getInstance().getMasterNode();
        Invocation invocation = Invocation.create(MetricProxy.class, "submit",
                new Class[]{Object.class}, o);
        invoker.invoke(masterNode, invocation);
    }

    @Override
    public void submit(List<Object> list) {
        ClusterNode masterNode = FRClusterNodeManager.getInstance().getMasterNode();
        Invocation invocation = Invocation.create(MetricProxy.class, "submit",
                new Class[]{List.class}, list);
        invoker.invoke(masterNode, invocation);
    }

    @Override
    public void pretreatment(List<Class> list) {
        ClusterNode masterNode = FRClusterNodeManager.getInstance().getMasterNode();
        Invocation invocation = Invocation.create(MetricProxy.class, "pretreatment",
                new Class[]{List.class}, list);
        invoker.invoke(masterNode, invocation);
    }

    @Override
    public void clean(QueryCondition condition) {
        ClusterNode masterNode = FRClusterNodeManager.getInstance().getMasterNode();
        Invocation invocation = Invocation.create(MetricProxy.class, "clean",
                new Class[]{QueryCondition.class}, condition);
        invoker.invoke(masterNode, invocation);
    }
}
