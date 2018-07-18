package com.fr.swift.service;

import com.fr.swift.Invoker;
import com.fr.swift.ProxyFactory;
import com.fr.swift.Result;
import com.fr.swift.URL;
import com.fr.swift.config.bean.SwiftServiceInfoBean;
import com.fr.swift.config.service.SwiftMetaDataService;
import com.fr.swift.config.service.SwiftServiceInfoService;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.core.rpc.SwiftClusterService;
import com.fr.swift.db.Where;
import com.fr.swift.db.impl.SwiftDatabase;
import com.fr.swift.event.global.PushSegLocationRpcEvent;
import com.fr.swift.exception.SwiftServiceException;
import com.fr.swift.invocation.SwiftInvocation;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.query.info.bean.query.QueryInfoBean;
import com.fr.swift.query.info.bean.query.QueryInfoBeanFactory;
import com.fr.swift.query.session.factory.SessionFactory;
import com.fr.swift.rpc.annotation.RpcMethod;
import com.fr.swift.rpc.annotation.RpcService;
import com.fr.swift.rpc.annotation.RpcServiceType;
import com.fr.swift.rpc.client.AsyncRpcCallback;
import com.fr.swift.rpc.client.async.RpcFuture;
import com.fr.swift.rpc.server.RpcServer;
import com.fr.swift.segment.Incrementer;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.SwiftSegmentManager;
import com.fr.swift.segment.operator.delete.RowDeleter;
import com.fr.swift.segment.recover.SegmentRecovery;
import com.fr.swift.selector.ProxySelector;
import com.fr.swift.selector.UrlSelector;
import com.fr.swift.service.listener.SwiftServiceListenerHandler;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftResultSet;
import com.fr.swift.task.service.ServiceTaskExecutor;
import com.fr.swift.task.service.ServiceTaskType;
import com.fr.swift.task.service.SwiftServiceCallable;
import com.fr.swift.util.concurrent.CommonExecutor;
import com.fr.third.springframework.beans.factory.annotation.Autowired;
import com.fr.third.springframework.stereotype.Service;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * @author pony
 * @date 2017/10/10
 */
@Service("realtimeService")
@RpcService(type = RpcServiceType.CLIENT_SERVICE, value = RealtimeService.class)
public class SwiftRealtimeService extends AbstractSwiftService implements RealtimeService, Serializable {

    private transient RpcServer server = SwiftContext.getInstance().getBean(RpcServer.class);

    private transient SwiftSegmentManager segmentManager = (SwiftSegmentManager) SwiftContext.getInstance().getBean("localSegmentProvider");

    private SwiftRealtimeService() {
    }

    @Autowired
    private transient ServiceTaskExecutor taskExecutor;

    public static SwiftRealtimeService getInstance() {
        return SingletonHolder.service;
    }

    @Override
    public void insert(final SourceKey tableKey, final SwiftResultSet resultSet) throws Exception {
        SwiftLoggers.getLogger().info("insert");
        taskExecutor.submit(new SwiftServiceCallable(tableKey, ServiceTaskType.INSERT) {
            @Override
            public void doJob() throws Exception {
                rpcSegmentLocation(PushSegLocationRpcEvent.fromSourceKey(getServiceType(), Arrays.asList(tableKey.getId())));
                new Incrementer(SwiftDatabase.getInstance().getTable(tableKey)).increment(resultSet);
            }
        });
    }

//    @Override
//    @RpcMethod(methodName = "merge")
//    public void merge(List<SegmentKey> tableKeys) {
//        SwiftLoggers.getLogger().info("merge");
//        rpcSegmentLocation(PushSegLocationRpcEvent.fromSegmentKey(getServiceType(), tableKeys));
//    }

    @Override
    @RpcMethod(methodName = "realtimeDelete")
    public boolean delete(final SourceKey sourceKey, final Where where) throws Exception {
        taskExecutor.submit(new SwiftServiceCallable(sourceKey, ServiceTaskType.DELETE) {
            @Override
            public void doJob() throws Exception {
                List<Segment> segments = segmentManager.getSegment(sourceKey);
                for (Segment segment : segments) {
                    RowDeleter rowDeleter = (RowDeleter) SwiftContext.getInstance().getBean("decrementer", segment);
                    rowDeleter.delete(sourceKey, where);
                }
            }
        });
        return true;
    }

    @Override
    @RpcMethod(methodName = "recover")
    public void recover(List<SegmentKey> tableKeys) {
        SwiftLoggers.getLogger().info("recover");
    }

    @Override
    @RpcMethod(methodName = "cleanMetaCache")
    public void cleanMetaCache(String[] sourceKeys) {
        SwiftContext.getInstance().getBean(SwiftMetaDataService.class).cleanCache(sourceKeys);
    }

    @Override
    @RpcMethod(methodName = "realTimeQuery")
    public SwiftResultSet query(final String queryDescription) throws SQLException {
        try {
            final QueryInfoBean bean = QueryInfoBeanFactory.create(queryDescription);
            SessionFactory sessionFactory = SwiftContext.getInstance().getBean(SessionFactory.class);
            return sessionFactory.openSession(bean.getQueryId()).executeQuery(bean);
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }

    @Override
    public boolean start() throws SwiftServiceException {
        super.start();

        recover0();

        return true;
    }

    private static void recover0() {
        CommonExecutor.get().submit(new Callable<Boolean>() {
            @Override
            public Boolean call() {
                try {
                    // 恢复所有realtime块
                    SegmentRecovery segmentRecovery = (SegmentRecovery) SwiftContext.getInstance().getBean("segmentRecovery");
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
    public ServiceType getServiceType() {
        return ServiceType.REAL_TIME;
    }

    private static final long serialVersionUID = 4719723736240190155L;

    public SwiftRealtimeService(String id) {
        super(id);
    }

    private static class SingletonHolder {
        private static SwiftRealtimeService service = new SwiftRealtimeService();
    }

    private void rpcSegmentLocation(PushSegLocationRpcEvent event) {
        URL masterURL = getMasterURL();
        ProxyFactory factory = ProxySelector.getInstance().getFactory();
        Invoker invoker = factory.getInvoker(null, SwiftServiceListenerHandler.class, masterURL, false);
        Result result = invoker.invoke(new SwiftInvocation(server.getMethodByName("rpcTrigger"), new Object[]{event}));
        RpcFuture future = (RpcFuture) result.getValue();
        future.addCallback(new AsyncRpcCallback() {
            @Override
            public void success(Object result) {
                logger.info("rpcTrigger success! ");
            }

            @Override
            public void fail(Exception e) {
                logger.error("rpcTrigger error! ", e);
            }
        });
    }

    private URL getMasterURL() {
        List<SwiftServiceInfoBean> swiftServiceInfoBeans = SwiftContext.getInstance().
                getBean(SwiftServiceInfoService.class).getServiceInfoByService(SwiftClusterService.SERVICE);
        SwiftServiceInfoBean swiftServiceInfoBean = swiftServiceInfoBeans.get(0);
        return UrlSelector.getInstance().getFactory().getURL(swiftServiceInfoBean.getServiceInfo());
    }
}