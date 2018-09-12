package com.fr.swift.service;

import com.fr.swift.annotation.SwiftService;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.db.Where;
import com.fr.swift.db.impl.SwiftDatabase;
import com.fr.swift.exception.SwiftServiceException;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.netty.rpc.server.RpcServer;
import com.fr.swift.query.info.bean.query.QueryInfoBean;
import com.fr.swift.query.query.QueryBeanFactory;
import com.fr.swift.query.session.factory.SessionFactory;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.SwiftSegmentManager;
import com.fr.swift.segment.operator.delete.WhereDeleter;
import com.fr.swift.segment.recover.SegmentRecovery;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftResultSet;
import com.fr.swift.task.service.ServiceTaskExecutor;
import com.fr.swift.task.service.ServiceTaskType;
import com.fr.swift.task.service.SwiftServiceCallable;
import com.fr.swift.util.concurrent.CommonExecutor;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.Callable;

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

    private transient boolean recoverable = true;

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
        CommonExecutor.get().submit(new Callable<Boolean>() {
            @Override
            public Boolean call() {
                try {
                    // 恢复所有realtime块
                    SegmentRecovery segmentRecovery = (SegmentRecovery) SwiftContext.get().getBean("segmentRecovery");
                    segmentRecovery.recoverAll();
                    return true;
                } catch (Exception e) {
                    SwiftLoggers.getLogger().error(e);
                    return false;
                }
            }
        });
    }

    @Override
    public void recover(List<SegmentKey> tableKeys) {
        SwiftLoggers.getLogger().info("recover");
    }

    @Override
    public boolean delete(final SourceKey sourceKey, final Where where) throws Exception {
        taskExecutor.submit(new SwiftServiceCallable(sourceKey, ServiceTaskType.DELETE) {
            @Override
            public void doJob() throws Exception {
                List<Segment> segments = segmentManager.getSegment(sourceKey);
                for (Segment segment : segments) {
                    WhereDeleter whereDeleter = (WhereDeleter) SwiftContext.get().getBean("decrementer", sourceKey, segment);
                    whereDeleter.delete(where);
                }
            }
        });
        return true;
    }

    @Override
    public SwiftResultSet query(final String queryDescription) throws SQLException {
        try {
            final QueryInfoBean bean = queryBeanFactory.create(queryDescription);
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