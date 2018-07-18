package com.fr.swift.service;

import com.fr.swift.Invoker;
import com.fr.swift.ProxyFactory;
import com.fr.swift.Result;
import com.fr.swift.config.service.SwiftMetaDataService;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.exception.SwiftServiceException;
import com.fr.swift.invocation.SwiftInvocation;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.query.builder.QueryBuilder;
import com.fr.swift.query.query.QueryBean;
import com.fr.swift.query.query.QueryRunnerProvider;
import com.fr.swift.rpc.annotation.RpcMethod;
import com.fr.swift.rpc.annotation.RpcService;
import com.fr.swift.rpc.annotation.RpcServiceType;
import com.fr.swift.rpc.client.AsyncRpcCallback;
import com.fr.swift.rpc.client.async.RpcFuture;
import com.fr.swift.rpc.server.RpcServer;
import com.fr.swift.rpc.url.RPCDestination;
import com.fr.swift.rpc.url.RPCUrl;
import com.fr.swift.segment.SegmentDestination;
import com.fr.swift.segment.SegmentLocationInfo;
import com.fr.swift.segment.SegmentLocationProvider;
import com.fr.swift.segment.impl.SegmentDestinationImpl;
import com.fr.swift.selector.ProxySelector;
import com.fr.swift.source.SwiftResultSet;
import com.fr.swift.task.service.ServiceTaskExecutor;
import com.fr.third.fasterxml.jackson.databind.ObjectMapper;
import com.fr.third.springframework.beans.factory.annotation.Autowired;
import com.fr.third.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 *
 * @author pony
 * @date 2017/10/12
 * 分析服务
 */
@Service("analyseService")
@RpcService(value = AnalyseService.class, type = RpcServiceType.CLIENT_SERVICE)
public class SwiftAnalyseService extends AbstractSwiftService implements AnalyseService {
    private static final long serialVersionUID = 841582089735823794L;

    private transient static final SwiftLogger LOGGER = SwiftLoggers.getLogger(SwiftAnalyseService.class);

    @Autowired
    private transient RpcServer server;

    @Autowired
    private transient ServiceTaskExecutor taskExecutor;

    private transient ObjectMapper mapper = new ObjectMapper();

    public SwiftAnalyseService(String id) {
        super(id);
    }

    private SwiftAnalyseService() {
    }

    @Override
    public ServiceType getServiceType() {
        return ServiceType.ANALYSE;
    }

    @Override
    public boolean start() throws SwiftServiceException {
        boolean start = super.start();
        QueryRunnerProvider.getInstance().registerRunner(this);
        return start;
    }

    @Override
    @RpcMethod(methodName = "cleanMetaCache")
    public void cleanMetaCache(String[] sourceKeys) {
        SwiftContext.getInstance().getBean(SwiftMetaDataService.class).cleanCache(sourceKeys);
    }

    @Override
    public SwiftResultSet getQueryResult(QueryBean info) throws SQLException {
        return QueryBuilder.buildQuery(info).getQueryResult();
    }

    @Override
    public SwiftResultSet getRemoteQueryResult(final QueryBean info, final SegmentDestination remoteURI) {
        final SwiftResultSet[] resultSet = new SwiftResultSet[1];
        try {
            final CountDownLatch latch = new CountDownLatch(1);
            queryRemoteNodeNode(info, remoteURI).addCallback(new AsyncRpcCallback() {
                @Override
                public void success(Object result) {
                    resultSet[0] = (SwiftResultSet) result;
                    latch.countDown();
                }

                @Override
                public void fail(Exception e) {
                    List<String> spareNodes = remoteURI.getSpareNodes();
                    try {
                        for (String spareNode : spareNodes) {
                            SegmentDestinationImpl spare = new SegmentDestinationImpl(remoteURI);
                            spare.setClusterId(spareNode);
                            final CountDownLatch count = new CountDownLatch(1);
                            queryRemoteNodeNode(info, spare).addCallback(new AsyncRpcCallback() {
                                @Override
                                public void success(Object result) {
                                    resultSet[0] = (SwiftResultSet) result;
                                    count.countDown();
                                }

                                @Override
                                public void fail(Exception e) {
                                    count.countDown();
                                }
                            });
                            count.await();
                            if (null != resultSet[0]) {
                                latch.countDown();
                                break;
                            }
                        }
                        if (resultSet[0] == null) {
                            latch.countDown();
                        }
                    } catch (Exception e1) {
                        LOGGER.error("Query remote node error! ", e1);
                    }
                }
            });
            latch.await();
        } catch (Exception e) {
            LOGGER.error("Query remote node error! ", e);
        }
        return resultSet[0];
    }

    private RpcFuture queryRemoteNodeNode(QueryBean info, SegmentDestination remoteURI) throws Exception {
        String address = remoteURI.getAddress();
        String methodName = remoteURI.getMethodName();
        Class clazz = remoteURI.getServiceClass();
        info.setQuerySegment(remoteURI.getUri());
        ProxyFactory factory = ProxySelector.getInstance().getFactory();
        Invoker invoker = factory.getInvoker(null, clazz, new RPCUrl(new RPCDestination(address)), false);
        Result result = invoker.invoke(new SwiftInvocation(server.getMethodByName(methodName), new Object[]{mapper.writeValueAsString(info)}));
        RpcFuture future = (RpcFuture) result.getValue();
        if (null == future) {
            throw new Exception(result.getException());
        }
        return future;
    }

    @Override
    @RpcMethod(methodName = "updateSegmentInfo")
    public void updateSegmentInfo(SegmentLocationInfo locationInfo, SegmentLocationInfo.UpdateType updateType) {
        SegmentLocationProvider.getInstance().updateSegmentInfo(locationInfo, updateType);
    }
}
