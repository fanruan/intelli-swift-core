package com.fr.swift.boot.service;

import com.fr.swift.SwiftContext;
import com.fr.swift.basics.annotation.ProxyService;
import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.db.Where;
import com.fr.swift.executor.TaskProducer;
import com.fr.swift.executor.task.impl.CollateExecutorTask;
import com.fr.swift.executor.task.impl.DeleteExecutorTask;
import com.fr.swift.query.cache.QueryCacheBuilder;
import com.fr.swift.result.qrs.QueryResultSet;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.service.AnalyseService;
import com.fr.swift.service.HistoryService;
import com.fr.swift.service.ServiceContext;
import com.fr.swift.source.SourceKey;

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
    private AnalyseService analyseService = SwiftContext.get().getBean(AnalyseService.class);
    private HistoryService historyService = SwiftContext.get().getBean(HistoryService.class);

    @Override
    public QueryResultSet getQueryResult(String queryJson) throws Exception {
        return analyseService.getQueryResult(queryJson);
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
    public void clearQuery(String queryId) {
        QueryCacheBuilder.builder().removeCache(queryId);
    }
}