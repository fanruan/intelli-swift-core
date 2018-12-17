package com.fr.swift.service;

import com.fr.swift.annotation.SwiftService;
import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.cube.io.ResourceDiscovery;
import com.fr.swift.db.Table;
import com.fr.swift.db.Where;
import com.fr.swift.db.impl.SwiftDatabase;
import com.fr.swift.event.SwiftEventDispatcher;
import com.fr.swift.exception.SwiftServiceException;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.netty.rpc.server.RpcServer;
import com.fr.swift.query.info.bean.query.QueryInfoBean;
import com.fr.swift.query.query.QueryBeanFactory;
import com.fr.swift.query.session.factory.SessionFactory;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.SwiftSegmentManager;
import com.fr.swift.segment.event.SegmentEvent;
import com.fr.swift.segment.operator.delete.WhereDeleter;
import com.fr.swift.segment.recover.SegmentRecovery;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftResultSet;
import com.fr.swift.task.service.ServiceTaskExecutor;
import com.fr.swift.task.service.ServiceTaskType;
import com.fr.swift.task.service.SwiftServiceCallable;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.List;

/**
 * @author pony
 * @date 2017/10/10
 */
@SwiftService(name = "realtime")
public class SwiftRealtimeService extends AbstractSwiftService implements RealtimeService, Serializable {

    private static final long serialVersionUID = 4719723736240190155L;

    private transient RpcServer server;

    private transient SwiftSegmentManager segmentManager;

    private transient ServiceTaskExecutor taskExecutor;

    private transient QueryBeanFactory queryBeanFactory;

    private transient volatile boolean recoverable = true;

    private SwiftRealtimeService() {
    }

    @Override
    public boolean start() throws SwiftServiceException {
        super.start();
        server = SwiftContext.get().getBean(RpcServer.class);
        segmentManager = SwiftContext.get().getBean("localSegmentProvider", SwiftSegmentManager.class);
        taskExecutor = SwiftContext.get().getBean(ServiceTaskExecutor.class);
        queryBeanFactory = SwiftContext.get().getBean(QueryBeanFactory.class);
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
        server = null;
        segmentManager = null;
        taskExecutor = null;
        queryBeanFactory = null;
        return true;
    }

    @Override
    public void insert(final SourceKey tableKey, final SwiftResultSet resultSet) throws Exception {
        taskExecutor.submit(new SwiftServiceCallable(tableKey, ServiceTaskType.INSERT) {
            @Override
            public void doJob() throws Exception {
                SwiftDatabase.getInstance().getTable(tableKey).insert(resultSet);
            }
        });
    }

    private void recover0() {
        for (Table table : SwiftDatabase.getInstance().getAllTables()) {
            final SourceKey tableKey = table.getSourceKey();
            try {
                taskExecutor.submit(new SwiftServiceCallable(tableKey, ServiceTaskType.RECOVERY) {
                    @Override
                    public void doJob() {
                        // 恢复所有realtime块
                        SegmentRecovery segmentRecovery = (SegmentRecovery) SwiftContext.get().getBean("segmentRecovery");
                        segmentRecovery.recover(tableKey);
                    }
                });
            } catch (InterruptedException e) {
                SwiftLoggers.getLogger().warn(e);
            }
        }
    }

    @Override
    public void recover(List<SegmentKey> segKeys) {
        SwiftLoggers.getLogger().info("recover");
    }

    @Override
    public boolean delete(final SourceKey tableKey, final Where where) throws Exception {
        taskExecutor.submit(new SwiftServiceCallable(tableKey, ServiceTaskType.DELETE) {
            @Override
            public void doJob() throws Exception {
                List<SegmentKey> segments = segmentManager.getSegmentKeys(tableKey);
                for (SegmentKey segment : segments) {
                    if (segment.getStoreType().isTransient()) {
                        WhereDeleter whereDeleter = (WhereDeleter) SwiftContext.get().getBean("decrementer", segment);
                        ImmutableBitMap allshow = whereDeleter.delete(where);
                        if (allshow.isEmpty()) {
                            SwiftEventDispatcher.fire(SegmentEvent.REMOVE_HISTORY, segment);
                        }
                    }
                }
            }
        });
        return true;
    }

    @Override
    public SwiftResultSet query(final String queryDescription) throws SQLException {
        try {
            final QueryInfoBean bean = queryBeanFactory.create(queryDescription, false);
            SessionFactory sessionFactory = SwiftContext.get().getBean(SessionFactory.class);
            return sessionFactory.openSession(bean.getQueryId()).executeQuery(bean);
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }

    @Override
    public ServiceType getServiceType() {
        return ServiceType.REAL_TIME;
    }
}