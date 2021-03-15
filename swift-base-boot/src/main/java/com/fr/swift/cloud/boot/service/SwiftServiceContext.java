package com.fr.swift.cloud.boot.service;

import com.fr.swift.cloud.SwiftContext;
import com.fr.swift.cloud.basics.annotation.ProxyService;
import com.fr.swift.cloud.beans.annotation.SwiftBean;
import com.fr.swift.cloud.db.Where;
import com.fr.swift.cloud.event.SwiftEventDispatcher;
import com.fr.swift.cloud.executor.TaskProducer;
import com.fr.swift.cloud.executor.task.bean.CollateBean;
import com.fr.swift.cloud.executor.task.impl.CollateExecutorTask;
import com.fr.swift.cloud.executor.task.impl.DeleteExecutorTask;
import com.fr.swift.cloud.executor.task.impl.PlanningExecutorTask;
import com.fr.swift.cloud.property.SwiftProperty;
import com.fr.swift.cloud.query.cache.QueryCacheBuilder;
import com.fr.swift.cloud.result.SwiftResultSet;
import com.fr.swift.cloud.result.qrs.QueryResultSet;
import com.fr.swift.cloud.segment.SegmentKey;
import com.fr.swift.cloud.service.AnalyseService;
import com.fr.swift.cloud.service.CollateService;
import com.fr.swift.cloud.service.DeleteService;
import com.fr.swift.cloud.service.MigrateService;
import com.fr.swift.cloud.service.RealtimeService;
import com.fr.swift.cloud.service.ServerService;
import com.fr.swift.cloud.service.ServiceContext;
import com.fr.swift.cloud.service.ServiceType;
import com.fr.swift.cloud.service.TaskService;
import com.fr.swift.cloud.service.event.NodeEvent;
import com.fr.swift.cloud.service.event.NodeMessage;
import com.fr.swift.cloud.source.SourceKey;
import com.fr.swift.cloud.util.ServiceBeanFactory;

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
        SwiftContext.get().getBean(MigrateService.class).start();
        SwiftContext.get().getBean(RealtimeService.class).start();
        SwiftContext.get().getBean(TaskService.class).start();
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
        SwiftContext.get().getBean(MigrateService.class).shutdown();
        SwiftContext.get().getBean(RealtimeService.class).shutdown();
        SwiftContext.get().getBean(TaskService.class).shutdown();
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
    public SwiftResultSet getResultResult(String queryJson) throws Exception {
        return SwiftContext.get().getBean(AnalyseService.class).getResultResult(queryJson);
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