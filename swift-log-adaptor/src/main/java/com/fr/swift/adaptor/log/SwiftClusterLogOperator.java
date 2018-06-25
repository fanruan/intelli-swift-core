package com.fr.swift.adaptor.log;

import com.fr.cluster.core.ClusterNode;
import com.fr.cluster.engine.rpc.base.InvokerManager;
import com.fr.cluster.rpc.base.Invocation;
import com.fr.cluster.rpc.base.Invoker;
import com.fr.cluster.rpc.base.Result;
import com.fr.general.LogOperator;
import com.fr.stable.query.condition.QueryCondition;
import com.fr.stable.query.data.DataList;
import com.fr.swift.frrpc.FRClusterNodeManager;
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
public class SwiftClusterLogOperator implements LogOperator {

    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(SwiftClusterLogOperator.class);

    private Invoker invoker;

    public SwiftClusterLogOperator() {
        invoker = InvokerManager.getInstance().create(LogOperatorProxy.class);
    }

    //todo masterNode为空的情况

    @Override
    public <T> DataList<T> find(Class<T> aClass, QueryCondition queryCondition) {
        ClusterNode masterNode = FRClusterNodeManager.getInstance().getMasterNode();
        Invocation invocation = Invocation.create(LogOperatorProxy.class, "find",
                new Class[]{Class.class, QueryCondition.class}, aClass, queryCondition);
        Result result = invoker.invoke(masterNode, invocation);
        return (DataList) result.get();
    }

    @Override
    public <T> DataList<T> find(Class<T> aClass, QueryCondition queryCondition, String s) {
        ClusterNode masterNode = FRClusterNodeManager.getInstance().getMasterNode();
        Invocation invocation = Invocation.create(LogOperatorProxy.class, "find",
                new Class[]{Class.class, QueryCondition.class, String.class}, aClass, queryCondition, s);
        Result result = invoker.invoke(masterNode, invocation);
        return (DataList) result.get();
    }

    @Override
    public DataList<List<Object>> find(String s) {
        return null;
    }

    @Override
    public void recordInfo(Object o) {
        ClusterNode masterNode = FRClusterNodeManager.getInstance().getMasterNode();
        Invocation invocation = Invocation.create(LogOperatorProxy.class, "recordInfo",
                new Class[]{Object.class}, o);
        invoker.invoke(masterNode, invocation);
    }

    @Override
    public void recordInfo(List<Object> list) {
        ClusterNode masterNode = FRClusterNodeManager.getInstance().getMasterNode();
        Invocation invocation = Invocation.create(LogOperatorProxy.class, "recordInfo",
                new Class[]{List.class}, list);
        invoker.invoke(masterNode, invocation);
    }

    @Override
    public void initTables(List<Class> list) {
        ClusterNode masterNode = FRClusterNodeManager.getInstance().getMasterNode();
        Invocation invocation = Invocation.create(LogOperatorProxy.class, "initTables",
                new Class[]{List.class}, list);
        invoker.invoke(masterNode, invocation);
    }

    @Override
    public void clearLogBefore(Date date) {
        ClusterNode masterNode = FRClusterNodeManager.getInstance().getMasterNode();
        Invocation invocation = Invocation.create(LogOperatorProxy.class, "clearLogBefore",
                new Class[]{Date.class}, date);
        invoker.invoke(masterNode, invocation);
    }
}
