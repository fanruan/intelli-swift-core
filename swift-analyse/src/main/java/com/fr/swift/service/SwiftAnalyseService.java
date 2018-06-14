package com.fr.swift.service;

import com.fr.swift.Invoker;
import com.fr.swift.ProxyFactory;
import com.fr.swift.Result;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.exception.SwiftServiceException;
import com.fr.swift.invocation.SwiftInvocation;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.query.QueryInfo;
import com.fr.swift.query.QueryRunnerProvider;
import com.fr.swift.query.builder.QueryBuilder;
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
import com.fr.swift.selector.ProxySelector;
import com.fr.swift.source.SwiftResultSet;

import java.sql.SQLException;
import java.util.concurrent.CountDownLatch;

/**
 * Created by pony on 2017/10/12.
 * 分析服务
 */
@RpcService(value = AnalyseService.class, type = RpcServiceType.CLIENT_SERVICE)
public class SwiftAnalyseService extends AbstractSwiftService implements AnalyseService {

    private static final long serialVersionUID = 841582089735823794L;
    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(SwiftAnalyseService.class);
    private RpcServer server = SwiftContext.getInstance().getBean(RpcServer.class);

    public SwiftAnalyseService(String id) {
        super(id);
    }

    public SwiftAnalyseService() {
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
    public <T extends SwiftResultSet> T getQueryResult(QueryInfo<T> info) throws SQLException {
        return QueryBuilder.buildQuery(info).getQueryResult();
    }

    @Override
    public <T extends SwiftResultSet> T getRemoteQueryResult(QueryInfo<T> info, SegmentDestination remoteURI) {
        final SwiftResultSet[] resultSet = new SwiftResultSet[1];
        try {
            String address = remoteURI.getAddress();
            String methodName = remoteURI.getMethodName();
            Class clazz = remoteURI.getServiceClass();
            ProxyFactory factory = ProxySelector.getInstance().getFactory();
            Invoker invoker = factory.getInvoker(null, clazz, new RPCUrl(new RPCDestination(address)), false);
            Result result = invoker.invoke(new SwiftInvocation(server.getMethodByName(methodName), new Object[]{info, remoteURI.order()}));
            RpcFuture future = (RpcFuture) result.getValue();
            final CountDownLatch latch = new CountDownLatch(1);

            future.addCallback(new AsyncRpcCallback() {
                @Override
                public void success(Object result) {
                    resultSet[0] = (SwiftResultSet) result;
                    latch.countDown();
                }

                @Override
                public void fail(Exception e) {
                    latch.countDown();
                }
            });
            latch.await();
        } catch (Exception e) {
            LOGGER.error("Query remote node error! ", e);
        }
        return (T) resultSet[0];
    }

    @Override
    @RpcMethod(methodName = "updateSegmentInfo")
    public void updateSegmentInfo(SegmentLocationInfo locationInfo, SegmentLocationInfo.UpdateType updateType) {
        SegmentLocationProvider.getInstance().updateSegmentInfo(locationInfo, updateType);
    }
}
