package com.fr.swift.boot.service;

import com.fr.swift.SwiftContext;
import com.fr.swift.basics.annotation.ProxyService;
import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.db.Where;
import com.fr.swift.event.SwiftEventDispatcher;
import com.fr.swift.executor.TaskProducer;
import com.fr.swift.executor.task.bean.CollateBean;
import com.fr.swift.executor.task.impl.CollateExecutorTask;
import com.fr.swift.executor.task.impl.DeleteExecutorTask;
import com.fr.swift.executor.task.impl.PlanningExecutorTask;
import com.fr.swift.property.SwiftProperty;
import com.fr.swift.query.cache.QueryCacheBuilder;
import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.result.qrs.QueryResultSet;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.service.AnalyseService;
import com.fr.swift.service.CollateService;
import com.fr.swift.service.DeleteService;
import com.fr.swift.service.RealtimeService;
import com.fr.swift.service.MigrateService;
import com.fr.swift.service.ServerService;
import com.fr.swift.service.ServiceContext;
import com.fr.swift.service.ServiceType;
import com.fr.swift.service.event.NodeEvent;
import com.fr.swift.service.event.NodeMessage;
import com.fr.swift.source.SourceKey;
import com.fr.swift.util.ServiceBeanFactory;

import java.util.List;

/**
 * This class created on 2019/3/5
 *
 * @author Lucifer
 * @description
 */
@SwiftBean
@ProxyService(ServiceContext.class)
public class SwiftServiceContext implements ServiceContext {

    @Override
    public boolean start() throws Exception {
        SwiftContext.get().getBean(AnalyseService.class).start();
        SwiftContext.get().getBean(CollateService.class).start();
        SwiftContext.get().getBean(DeleteService.class).start();
        SwiftContext.get().getBean(RealtimeService.class).start();
        List<ServerService> serverServiceList = ServiceBeanFactory.getServerServiceByNames(SwiftProperty.get().getServerServiceNames());
        for (ServerService serverService : serverServiceList) {
            serverService.startServerService();
        }
        return true;
    }

    @Override
    public boolean shutdown() throws Exception {
        SwiftContext.get().getBean(AnalyseService.class).shutdown();
        SwiftContext.get().getBean(CollateService.class).shutdown();
        SwiftContext.get().getBean(DeleteService.class).shutdown();
        SwiftContext.get().getBean(RealtimeService.class).shutdown();
        List<ServerService> serverServiceList = ServiceBeanFactory.getServerServiceByNames(SwiftProperty.get().getServerServiceNames());
        for (ServerService serverService : serverServiceList) {
            serverService.stopServerService();
        }
        return true;
    }

    @Override
    public ServiceType getServiceType() {
        return ServiceType.CONTEXT;
    }

    @Override
    public String getId() {
        return SwiftProperty.get().getMachineId();
    }

    @Override
    public void setId(String id) {
        throw new UnsupportedOperationException("SwiftServiceContext can not set id!");
    }

    @Override
    public QueryResultSet getQueryResult(String queryJson) throws Exception {
        return SwiftContext.get().getBean(AnalyseService.class).getQueryResult(queryJson);
    }

    @Override
    public void appointCollate(SourceKey tableKey, List<SegmentKey> segmentKeyList) throws Exception {
        TaskProducer.produceTask(new CollateExecutorTask(CollateBean.of(tableKey, segmentKeyList)));
    }

    @Override
    public boolean delete(SourceKey tableKey, Where where) throws Exception {
        //delete改为同步删。
        //        return TaskProducer.produceTask(new DeleteExecutorTask(tableKey, where));
        return (boolean) DeleteExecutorTask.of(tableKey, where).getJob().call();
    }

    @Override
    public void clearQuery(String queryId) {
        QueryCacheBuilder.builder().removeCache(queryId);
    }

    @Override
    public void insert(SourceKey tableKey, SwiftResultSet resultSet) throws Exception {
        throw new UnsupportedOperationException("insert not support");
    }

    @Override
    public boolean dispatch(String taskBean, String location) throws Exception {
        return TaskProducer.produceTask(PlanningExecutorTask.of(taskBean));
    }

    @Override
    public boolean report(NodeEvent nodeEvent, NodeMessage nodeMessage) {
        SwiftEventDispatcher.syncFire(nodeEvent, nodeMessage);
        return true;
    }

    @Override
    public boolean deleteFiles(String targetPath, String clusterId) {
        return SwiftContext.get().getBean(MigrateService.class).deleteMigratedFile(targetPath);
    }

    @Override
    public boolean updateConfigs(List<SegmentKey> segmentKeys, String clusterId) {
        return SwiftContext.get().getBean(MigrateService.class).updateMigratedSegsConfig(segmentKeys);
    }
}