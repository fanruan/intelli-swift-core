package com.fr.swift.boot.service;

import com.fr.swift.SwiftContext;
import com.fr.swift.basics.annotation.ProxyService;
import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.db.Where;
import com.fr.swift.executor.TaskProducer;
import com.fr.swift.executor.task.impl.CollateExecutorTask;
import com.fr.swift.executor.task.impl.DeleteExecutorTask;
import com.fr.swift.property.SwiftProperty;
import com.fr.swift.result.qrs.QueryResultSet;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.service.AnalyseService;
import com.fr.swift.service.CollateService;
import com.fr.swift.service.DeleteService;
import com.fr.swift.service.ServerService;
import com.fr.swift.service.ServiceContext;
import com.fr.swift.service.ServiceType;
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
        List<ServerService> serverServiceList = ServiceBeanFactory.getServerServiceByNames(SwiftProperty.getProperty().getServerServiceNames());
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
        List<ServerService> serverServiceList = ServiceBeanFactory.getServerServiceByNames(SwiftProperty.getProperty().getServerServiceNames());
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
        return SwiftProperty.getProperty().getMachineId();
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
        TaskProducer.produceTask(new CollateExecutorTask(tableKey, segmentKeyList));
    }

    @Override
    public boolean delete(SourceKey tableKey, Where where) throws Exception {
        //delete改为同步删。
        //        return TaskProducer.produceTask(new DeleteExecutorTask(tableKey, where));
        return (boolean) new DeleteExecutorTask(tableKey, where).getJob().call();
    }
}