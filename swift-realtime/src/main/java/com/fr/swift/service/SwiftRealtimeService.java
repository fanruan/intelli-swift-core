package com.fr.swift.service;

import com.fr.swift.URL;
import com.fr.swift.config.bean.SwiftServiceInfoBean;
import com.fr.swift.config.service.SwiftMetaDataService;
import com.fr.swift.config.service.SwiftServiceInfoService;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.db.impl.SwiftDatabase;
import com.fr.swift.exception.SwiftServiceException;
import com.fr.swift.frrpc.SwiftClusterService;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.query.builder.QueryBuilder;
import com.fr.swift.query.info.bean.query.QueryInfoBean;
import com.fr.swift.query.info.bean.query.QueryInfoBeanFactory;
import com.fr.swift.query.query.QueryBean;
import com.fr.swift.query.query.QueryType;
import com.fr.swift.query.session.AbstractSession;
import com.fr.swift.query.session.Session;
import com.fr.swift.query.session.SessionBuilder;
import com.fr.swift.query.session.factory.SessionFactory;
import com.fr.swift.result.DetailResultSet;
import com.fr.swift.result.GroupNode;
import com.fr.swift.result.NodeMergeResultSet;
import com.fr.swift.result.NodeResultSet;
import com.fr.swift.result.SwiftNode;
import com.fr.swift.result.serialize.LocalAllNodeResultSet;
import com.fr.swift.result.serialize.LocalPartNodeResultSet;
import com.fr.swift.result.serialize.SerializableDetailResultSet;
import com.fr.swift.result.serialize.SerializableResultSet;
import com.fr.swift.rpc.annotation.RpcMethod;
import com.fr.swift.rpc.annotation.RpcService;
import com.fr.swift.rpc.annotation.RpcServiceType;
import com.fr.swift.rpc.server.RpcServer;
import com.fr.swift.segment.Incrementer;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.recover.SegmentRecovery;
import com.fr.swift.selector.UrlSelector;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftResultSet;
import com.fr.swift.util.concurrent.CommonExecutor;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * @author pony
 * @date 2017/10/10
 */
@RpcService(type = RpcServiceType.CLIENT_SERVICE, value = RealtimeService.class)
public class SwiftRealtimeService extends AbstractSwiftService implements RealtimeService, Serializable {

    private transient RpcServer server = SwiftContext.getInstance().getBean(RpcServer.class);

    @Override
    public void insert(SourceKey tableKey, SwiftResultSet resultSet) throws SQLException {
        SwiftLoggers.getLogger().info("insert");

        new Incrementer(SwiftDatabase.getInstance().getTable(tableKey)).increment(resultSet);

        // TODO 生成realtime的SegmentLocation信息

//        URL masterURL = getMasterURL();
//        ProxyFactory factory = ProxySelector.getInstance().getFactory();
//        Invoker invoker = factory.getInvoker(null, SwiftServiceListenerHandler.class, masterURL, false);
//        Result result = invoker.invoke(new SwiftInvocation(server.getMethodByName("rpcTrigger"), new Object[]{new SegmentLocationRpcEvent(new SegmentLocationInfoImpl(ServiceType.REAL_TIME, new HashMap<String, Pair<Integer, List<SegmentDestination>>>()))}));
//        RpcFuture future = (RpcFuture) result.getValue();
//        future.addCallback(new AsyncRpcCallback() {
//            @Override
//            public void success(Object result) {
//                logger.info("rpcTrigger success! ");
//            }
//
//            @Override
//            public void fail(Exception e) {
//                logger.error("rpcTrigger error! ", e);
//            }
//        });
    }

    @Override
    @RpcMethod(methodName = "merge")
    public void merge(List<SegmentKey> tableKeys) {
        SwiftLoggers.getLogger().info("merge");
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
    @SuppressWarnings("Duplicates")
    @RpcMethod(methodName = "realTimeQuery")
    public SwiftResultSet query(final String queryDescription) throws SQLException {
        try {
            final QueryInfoBean bean = QueryInfoBeanFactory.create(queryDescription);
            SessionFactory sessionFactory = SwiftContext.getInstance().getBean(SessionFactory.class);
            return sessionFactory.openSession(new SessionBuilder() {
                @Override
                public Session build(long cacheTimeout) {
                    return new AbstractSession(cacheTimeout) {
                        @Override
                        protected SwiftResultSet query(QueryBean queryInfo) throws SQLException {
                            // 先到QueryResultSetManager找一下有没有缓存，没有则构建查询。
                            SwiftResultSet resultSet = QueryBuilder.buildQuery(queryInfo).getQueryResult();
                            SerializableResultSet result;
                            QueryType type = queryInfo.getQueryType();
                            switch (type) {
                                case LOCAL_GROUP_ALL:
                                    result = new LocalAllNodeResultSet(queryInfo.getQueryId(), (NodeResultSet<SwiftNode>) resultSet);
                                    break;
                                case LOCAL_GROUP_PART:
                                    result = new LocalPartNodeResultSet(queryInfo.getQueryId(), (NodeMergeResultSet<GroupNode>) resultSet);
                                    break;
                                default:
                                    result = new SerializableDetailResultSet(queryInfo.getQueryId(), (DetailResultSet) resultSet);
                            }
                            return result;
                        }
                    };
                }

                @Override
                public String getQueryId() {
                    return bean.getQueryId();
                }
            }).executeQuery(bean);
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

    public SwiftRealtimeService() {
    }

    private URL getMasterURL() {
        List<SwiftServiceInfoBean> swiftServiceInfoBeans = SwiftContext.getInstance().getBean(SwiftServiceInfoService.class).getServiceInfoByService(SwiftClusterService.SERVICE);
        SwiftServiceInfoBean swiftServiceInfoBean = swiftServiceInfoBeans.get(0);
        return UrlSelector.getInstance().getFactory().getURL(swiftServiceInfoBean.getServiceInfo());
    }
}