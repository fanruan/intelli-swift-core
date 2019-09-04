package com.fr.swift.boot.service;

import com.fr.swift.SwiftContext;
import com.fr.swift.basics.annotation.ProxyService;
import com.fr.swift.basics.base.selector.ProxySelector;
import com.fr.swift.beans.annotation.SwiftAutoWired;
import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.config.bean.ServerCurrentStatus;
import com.fr.swift.db.Where;
import com.fr.swift.exception.event.HealthInspectionRpcEvent;
import com.fr.swift.executor.TaskProducer;
import com.fr.swift.executor.task.ExecutorTask;
import com.fr.swift.executor.task.impl.CollateExecutorTask;
import com.fr.swift.executor.task.impl.DeleteExecutorTask;
import com.fr.swift.executor.task.impl.DownloadExecutorTask;
import com.fr.swift.executor.task.impl.RealtimeInsertExecutorTask;
import com.fr.swift.executor.task.impl.TruncateExecutorTask;
import com.fr.swift.executor.task.impl.UploadExecutorTask;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.query.session.Session;
import com.fr.swift.query.session.factory.SessionFactory;
import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.result.qrs.QueryResultSet;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.SegmentLocationInfo;
import com.fr.swift.service.AnalyseService;
import com.fr.swift.service.BaseService;
import com.fr.swift.service.HistoryService;
import com.fr.swift.service.ServiceContext;
import com.fr.swift.service.SwiftService;
import com.fr.swift.service.listener.RemoteSender;
import com.fr.swift.source.SourceKey;
import com.fr.swift.stuff.IndexingStuff;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This class created on 2019/3/5
 *
 * @author Lucifer
 * @description
 */
@SwiftBean
@ProxyService(ServiceContext.class)
public class SwiftServiceContext implements ServiceContext {
    @SwiftAutoWired
    private AnalyseService analyseService;
    @SwiftAutoWired
    private HistoryService historyService;
    @SwiftAutoWired
    private BaseService baseService;
    @SwiftAutoWired
    private SessionFactory sessionFactory;

    @Override
    public void cleanMetaCache(String[] sourceKeys) {
        baseService.cleanMetaCache(sourceKeys);
    }

    @Override
    public QueryResultSet getQueryResult(String queryJson) throws Exception {
        return analyseService.getQueryResult(queryJson);
    }

    @Override
    public void updateSegmentInfo(SegmentLocationInfo locationInfo, SegmentLocationInfo.UpdateType updateType) {
        analyseService.updateSegmentInfo(locationInfo, updateType);
    }

    @Override
    public void removeSegments(String clusterId, SourceKey sourceKey, List<String> segmentKeys) {
        analyseService.removeSegments(clusterId, sourceKey, segmentKeys);
    }

    @Override
    public void insert(SourceKey tableKey, SwiftResultSet resultSet) throws Exception {
        TaskProducer.produceTask(new RealtimeInsertExecutorTask(tableKey, resultSet));
    }

    @Override
    public void truncate(SourceKey tableKey) throws Exception {
        TaskProducer.produceTask(new TruncateExecutorTask(tableKey));
    }

    @Override
    public void removeHistory(List<SegmentKey> needRemoveList) {
        historyService.removeHistory(needRemoveList);
    }

    @Override
    public <Stuff extends IndexingStuff> void index(Stuff stuff) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ServerCurrentStatus currentStatus() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void appointCollate(SourceKey tableKey, List<SegmentKey> segmentKeyList) throws Exception {
        TaskProducer.produceTask(new CollateExecutorTask(tableKey, segmentKeyList));
    }

    @Override
    public boolean delete(SourceKey tableKey, Where where) throws Exception {
        return TaskProducer.produceTask(new DeleteExecutorTask(tableKey, where));
    }

    @Override
    public void upload(Set<SegmentKey> segKeys) throws Exception {
        Set<ExecutorTask> executorTasks = new HashSet<ExecutorTask>();
        for (SegmentKey segKey : segKeys) {
            executorTasks.add(UploadExecutorTask.ofWholeSeg(segKey));
        }
        TaskProducer.produceTasks(executorTasks);
    }

    @Override
    public void download(Set<SegmentKey> segKeys, boolean replace) throws Exception {
        Set<ExecutorTask> executorTasks = new HashSet<ExecutorTask>();
        for (SegmentKey segKey : segKeys) {
            executorTasks.add(DownloadExecutorTask.ofWholeSeg(segKey, replace));
        }
        TaskProducer.produceTasks(executorTasks);

    }

    @Override
    public void uploadAllShow(Set<SegmentKey> segKeys) throws Exception {
        Set<ExecutorTask> executorTasks = new HashSet<ExecutorTask>();
        for (SegmentKey segKey : segKeys) {
            executorTasks.add(UploadExecutorTask.ofAllShowIndex(segKey));
        }
        TaskProducer.produceTasks(executorTasks);
    }

    @Override
    public void downloadAllShow(Set<SegmentKey> segKeys) throws Exception {
        Set<ExecutorTask> executorTasks = new HashSet<ExecutorTask>();
        for (SegmentKey segKey : segKeys) {
            executorTasks.add(DownloadExecutorTask.ofAllShowIndex(segKey));
        }
        TaskProducer.produceTasks(executorTasks);
    }

    @Override
    public void clearQuery(String queryId) throws Exception {
        Session session = sessionFactory.openSession(queryId);
        session.cleanCache(true);
    }

    @Override
    public Set<String> inspectMasterRpcHealth(SwiftService service) {
        SwiftLoggers.getLogger().debug("Inspect RpcHealth to Master");
        try {
            return Collections.singleton((String) ProxySelector.getProxy(RemoteSender.class).trigger(new HealthInspectionRpcEvent().inspectMasterAccessiable()));
        } catch (Exception e) {
            SwiftLoggers.getLogger().error("Master Communication inspect failed:", e);
        }
        return Collections.EMPTY_SET;
    }

    @Override
    public Set<String> inspectSlaveRpcHealth(SwiftService service) {
        Set<String> healthySwiftServiceSets = new HashSet<>();
        try {
            healthySwiftServiceSets.add(SwiftContext.get().getBean(service.getClass()).getId() + "/" + service.getClass().getName());
        } catch (Exception e) {
            SwiftLoggers.getLogger().error("Other Slave Communication inspect failed:", e);
        }
        return healthySwiftServiceSets;
    }
}
