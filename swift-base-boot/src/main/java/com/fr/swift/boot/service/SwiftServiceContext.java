package com.fr.swift.boot.service;

import com.fr.swift.SwiftContext;
import com.fr.swift.basics.annotation.ProxyService;
import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.config.bean.ServerCurrentStatus;
import com.fr.swift.db.Table;
import com.fr.swift.db.Where;
import com.fr.swift.db.impl.SwiftDatabase;
import com.fr.swift.executor.TaskProducer;
import com.fr.swift.executor.task.ExecutorTask;
import com.fr.swift.executor.task.impl.CollateExecutorTask;
import com.fr.swift.executor.task.impl.DeleteExecutorTask;
import com.fr.swift.executor.task.impl.DownloadExecutorTask;
import com.fr.swift.executor.task.impl.RealtimeInsertExecutorTask;
import com.fr.swift.executor.task.impl.TruncateExecutorTask;
import com.fr.swift.executor.task.impl.UploadExecutorTask;
import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.result.qrs.QueryResultSet;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.SegmentLocationInfo;
import com.fr.swift.segment.backup.BackupBlockImporter;
import com.fr.swift.segment.backup.ReusableResultSet;
import com.fr.swift.service.AnalyseService;
import com.fr.swift.service.BaseService;
import com.fr.swift.service.HistoryService;
import com.fr.swift.service.ServiceContext;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.alloter.impl.BaseAllotRule;
import com.fr.swift.source.alloter.impl.line.BackupLineSourceAlloter;
import com.fr.swift.source.alloter.impl.line.LineAllotRule;
import com.fr.swift.stuff.IndexingStuff;

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
    private AnalyseService analyseService = SwiftContext.get().getBean(AnalyseService.class);
    private HistoryService historyService = SwiftContext.get().getBean(HistoryService.class);
    private BaseService baseService = SwiftContext.get().getBean(BaseService.class);

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
        // 先备份，后insert
        BackupLineSourceAlloter alloter = new BackupLineSourceAlloter(tableKey, new LineAllotRule(BaseAllotRule.MEM_CAPACITY));
        Table table = SwiftDatabase.getInstance().getTable(tableKey);
        ReusableResultSet reusableResultSet = new ReusableResultSet(resultSet);
        new BackupBlockImporter(table, alloter).importData(reusableResultSet);

        // 暂定备份出问题就直接pass
        TaskProducer.produceTask(new RealtimeInsertExecutorTask(tableKey, reusableResultSet.reuse()));
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
}
