package com.fr.swift.service;

import com.fr.swift.SwiftContext;
import com.fr.swift.annotation.SwiftService;
import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.config.service.SwiftSegmentLocationService;
import com.fr.swift.config.service.SwiftSegmentService;
import com.fr.swift.db.Table;
import com.fr.swift.db.impl.SwiftDatabase;
import com.fr.swift.exception.SwiftServiceException;
import com.fr.swift.executor.TaskProducer;
import com.fr.swift.executor.task.impl.RecoveryExecutorTask;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.segment.Incrementer;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.column.impl.base.ResourceDiscovery;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.alloter.RowInfo;
import com.fr.swift.source.alloter.SwiftSourceAlloter;
import com.fr.swift.source.alloter.impl.BaseAllotRule;
import com.fr.swift.source.alloter.impl.line.LineAllotRule;
import com.fr.swift.source.alloter.impl.line.RealtimeLineSourceAlloter;

import java.io.Serializable;
import java.util.List;

/**
 * @author pony
 * @date 2017/10/10
 */
@SwiftService(name = "realtime")
@SwiftBean(name = "realtime")
public class SwiftRealtimeService extends AbstractSwiftService implements RealtimeService, Serializable {

    private static final long serialVersionUID = 4719723736240190155L;

    private transient volatile boolean recoverable = true;

    private transient SwiftSegmentService segSvc;
    private transient SwiftSegmentLocationService segLocationSvc;

    @Override
    public boolean start() throws SwiftServiceException {
        super.start();
        segSvc = SwiftContext.get().getBean("segmentServiceProvider", SwiftSegmentService.class);
        segLocationSvc = SwiftContext.get().getBean(SwiftSegmentLocationService.class);
        if (recoverable) {
            recover0();
            recoverable = false;
        }
        return true;
    }

    @Override
    public boolean shutdown() throws SwiftServiceException {
        super.shutdown();
        ResourceDiscovery.getInstance().releaseAll();
        recoverable = true;
        segSvc = null;
        segLocationSvc = null;
        return true;
    }

    @Override
    public void insert(final SourceKey tableKey, final SwiftResultSet resultSet) throws Exception {
        SwiftSourceAlloter alloter = new RealtimeLineSourceAlloter(tableKey, new LineAllotRule(BaseAllotRule.MEM_CAPACITY));
        Table table = SwiftDatabase.getInstance().getTable(tableKey);
        new Incrementer<SwiftSourceAlloter<?, RowInfo>>(table, alloter).importData(resultSet);
    }

    private void recover0() {
        for (Table table : SwiftDatabase.getInstance().getAllTables()) {
            List<SegmentKey> ownSegKeys = segSvc.getOwnSegments(table.getSourceKey());

            for (SegmentKey segKey : ownSegKeys) {
                if (segKey.getStoreType().isPersistent()) {
                    continue;
                }
                try {
                    TaskProducer.produceTask(new RecoveryExecutorTask(segKey));
                } catch (Exception e) {
                    SwiftLoggers.getLogger().error(e);
                }
            }
        }
    }

    @Override
    public void truncate(SourceKey tableKey) {
    }

    @Override
    public ServiceType getServiceType() {
        return ServiceType.REAL_TIME;
    }
}