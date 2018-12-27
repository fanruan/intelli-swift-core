package com.fr.swift.service;

import com.fr.swift.SwiftContext;
import com.fr.swift.annotation.SwiftService;
import com.fr.swift.basics.annotation.ProxyService;
import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.cube.io.ResourceDiscovery;
import com.fr.swift.db.Table;
import com.fr.swift.db.Where;
import com.fr.swift.db.impl.SwiftDatabase;
import com.fr.swift.event.SwiftEventDispatcher;
import com.fr.swift.exception.SwiftServiceException;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.query.info.bean.query.QueryBeanFactory;
import com.fr.swift.query.info.bean.query.QueryInfoBean;
import com.fr.swift.query.session.factory.SessionFactory;
import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.SwiftSegmentManager;
import com.fr.swift.segment.event.SegmentEvent;
import com.fr.swift.segment.operator.Importer;
import com.fr.swift.segment.operator.delete.WhereDeleter;
import com.fr.swift.segment.recover.SegmentRecovery;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.alloter.SwiftSourceAlloter;
import com.fr.swift.source.alloter.impl.line.LineAllotRule;
import com.fr.swift.source.alloter.impl.line.RealtimeLineSourceAlloter;
import com.fr.swift.task.service.ServiceTaskExecutor;
import com.fr.swift.task.service.ServiceTaskType;
import com.fr.swift.task.service.SwiftServiceCallable;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * @author pony
 * @date 2017/10/10
 */
@SwiftService(name = "realtime")
@ProxyService(RealtimeService.class)
@SwiftBean(name = "realtime")
public class SwiftRealtimeService extends AbstractSwiftService implements RealtimeService, Serializable {

    private static final long serialVersionUID = 4719723736240190155L;

    private transient SwiftSegmentManager segmentManager;

    private transient ServiceTaskExecutor taskExecutor;

    private transient volatile boolean recoverable = true;

    public SwiftRealtimeService() {
    }

    @Override
    public boolean start() throws SwiftServiceException {
        super.start();
        segmentManager = SwiftContext.get().getBean("localSegmentProvider", SwiftSegmentManager.class);
        taskExecutor = SwiftContext.get().getBean(ServiceTaskExecutor.class);
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
        segmentManager = null;
        taskExecutor = null;
        return true;
    }

    @Override
    public void insert(final SourceKey tableKey, final SwiftResultSet resultSet) throws Exception {
        taskExecutor.submit(new SwiftServiceCallable<Void>(tableKey, ServiceTaskType.INSERT, new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                try {
                    SwiftSourceAlloter alloter = new RealtimeLineSourceAlloter(tableKey, new LineAllotRule(LineAllotRule.MEM_STEP));
                    Table table = SwiftDatabase.getInstance().getTable(tableKey);
                    Importer importer = SwiftContext.get().getBean("incrementer", Importer.class, table, alloter);
                    importer.importData(resultSet);
                } catch (Exception e) {
                    throw new SQLException(e);
                } finally {
                    resultSet.close();
                }
                return null;
            }
        }));
    }

    private void recover0() {
        for (Table table : SwiftDatabase.getInstance().getAllTables()) {
            final SourceKey tableKey = table.getSourceKey();
            try {
                taskExecutor.submit(new SwiftServiceCallable<Void>(tableKey, ServiceTaskType.RECOVERY, new Callable<Void>() {
                    @Override
                    public Void call() {
                        // 恢复所有realtime块
                        SegmentRecovery segmentRecovery = (SegmentRecovery) SwiftContext.get().getBean("segmentRecovery");
                        segmentRecovery.recover(tableKey);
                        return null;
                    }
                }));
            } catch (InterruptedException e) {
                SwiftLoggers.getLogger().warn(e);
            }
        }
    }

    @Override
    public void recover(List<SegmentKey> segKeys) {
        SwiftLoggers.getLogger().info("recover");
    }

    /**
     * todo SwiftHistoryService
     */
    @Override
    public SwiftResultSet query(final String queryDescription) throws SQLException {
        try {
            final QueryInfoBean bean = QueryBeanFactory.create(queryDescription);
            SessionFactory sessionFactory = SwiftContext.get().getBean(SessionFactory.class);
            // TODO: 2018/11/28  
            return (SwiftResultSet) sessionFactory.openSession(bean.getQueryId()).executeQuery(bean);
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }

    /**
     * todo 公用实现，SwiftHistoryService
     */
    @Override
    public boolean delete(final SourceKey sourceKey, final Where where, final List<String> needUpload) throws Exception {
        Future<Boolean> future = taskExecutor.submit(new SwiftServiceCallable<Boolean>(sourceKey, ServiceTaskType.DELETE, new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                List<SegmentKey> segmentKeys = segmentManager.getSegmentKeys(sourceKey);
                for (SegmentKey segKey : segmentKeys) {
                    if (!segmentManager.existsSegment(segKey)) {
                        continue;
                    }
                    WhereDeleter whereDeleter = (WhereDeleter) SwiftContext.get().getBean("decrementer", segKey);
                    ImmutableBitMap allShowBitmap = whereDeleter.delete(where);
                    if (segKey.getStoreType().isTransient()) {
                        continue;
                    }

                    if (needUpload.contains(segKey.toString())) {
                        if (allShowBitmap.isEmpty()) {
                            SwiftEventDispatcher.fire(SegmentEvent.REMOVE_HISTORY, segKey);
                        } else {
                            SwiftEventDispatcher.fire(SegmentEvent.MASK_HISTORY, segKey);
                        }
                    }
                }
                return true;
            }
        }));
        return future.get();
    }

    @Override
    public ServiceType getServiceType() {
        return ServiceType.REAL_TIME;
    }
}