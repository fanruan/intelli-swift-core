package com.fr.swift.cloud.service;

import com.fr.swift.cloud.SwiftContext;
import com.fr.swift.cloud.annotation.SwiftService;
import com.fr.swift.cloud.beans.annotation.SwiftBean;
import com.fr.swift.cloud.config.service.SwiftSegmentLocationService;
import com.fr.swift.cloud.config.service.SwiftSegmentService;
import com.fr.swift.cloud.db.Table;
import com.fr.swift.cloud.db.impl.SwiftDatabase;
import com.fr.swift.cloud.exception.SwiftServiceException;
import com.fr.swift.cloud.executor.TaskProducer;
import com.fr.swift.cloud.executor.task.impl.RealtimeInsertExecutorTask;
import com.fr.swift.cloud.executor.task.impl.RecoveryExecutorTask;
import com.fr.swift.cloud.log.SwiftLoggers;
import com.fr.swift.cloud.result.SwiftResultSet;
import com.fr.swift.cloud.segment.SegmentKey;
import com.fr.swift.cloud.segment.column.impl.base.ResourceDiscovery;
import com.fr.swift.cloud.source.SourceKey;

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
        segSvc = SwiftContext.get().getBean(SwiftSegmentService.class);
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
        RealtimeInsertExecutorTask realtimeInsertExecutorTask = new RealtimeInsertExecutorTask(tableKey, resultSet);
        TaskProducer.produceTask(realtimeInsertExecutorTask);
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