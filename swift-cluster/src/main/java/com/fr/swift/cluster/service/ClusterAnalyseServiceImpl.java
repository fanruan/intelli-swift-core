package com.fr.swift.cluster.service;

import com.fr.swift.annotation.RpcMethod;
import com.fr.swift.annotation.RpcService;
import com.fr.swift.annotation.RpcServiceType;
import com.fr.swift.annotation.SwiftService;
import com.fr.swift.basics.AsyncRpcCallback;
import com.fr.swift.basics.Invoker;
import com.fr.swift.basics.ProxyFactory;
import com.fr.swift.basics.Result;
import com.fr.swift.basics.RpcFuture;
import com.fr.swift.basics.base.SwiftInvocation;
import com.fr.swift.basics.base.selector.ProxySelector;
import com.fr.swift.exception.SwiftServiceException;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.netty.rpc.server.RpcServer;
import com.fr.swift.netty.rpc.url.RPCDestination;
import com.fr.swift.netty.rpc.url.RPCUrl;
import com.fr.swift.query.query.QueryBean;
import com.fr.swift.query.query.QueryBeanFactory;
import com.fr.swift.segment.SegmentDestination;
import com.fr.swift.segment.SegmentLocationInfo;
import com.fr.swift.segment.impl.SegmentDestinationImpl;
import com.fr.swift.service.AbstractSwiftService;
import com.fr.swift.service.AnalyseService;
import com.fr.swift.service.ServiceType;
import com.fr.swift.service.cluster.ClusterAnalyseService;
import com.fr.swift.source.SwiftResultSet;
import com.fr.swift.util.Assert;
import com.fr.third.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @author yee
 * @date 2018/8/6
 */
@SwiftService(name = "clusterAnalyse")
@RpcService(value = AnalyseService.class, type = RpcServiceType.CLIENT_SERVICE)
public class ClusterAnalyseServiceImpl extends AbstractSwiftService implements ClusterAnalyseService {
    private static final long serialVersionUID = 7637989460502966453L;
    @Autowired(required = false)
    private transient RpcServer server;
    @Autowired(required = false)
    private transient QueryBeanFactory queryBeanFactory;
    @Autowired(required = false)
    private transient AnalyseService analyseService;

    @Override
    public SwiftResultSet getRemoteQueryResult(final String jsonString, final SegmentDestination remoteURI) {
        final SwiftResultSet[] resultSet = new SwiftResultSet[1];
        try {
            final CountDownLatch latch = new CountDownLatch(1);
            queryRemoteNodeNode(jsonString, remoteURI).addCallback(new AsyncRpcCallback() {
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
                            queryRemoteNodeNode(jsonString, spare).addCallback(new AsyncRpcCallback() {
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
                        SwiftLoggers.getLogger().error("Query remote node error! ", e1);
                    }
                }
            });
            latch.await();
        } catch (Exception e) {
            SwiftLoggers.getLogger().error("Query remote node error! ", e);
        }
        return resultSet[0];
    }

    @Override
    public boolean start() throws SwiftServiceException {
        analyseService.start();
        // 这边为了覆盖掉analyse的注册，所以再调一次注册
        return super.start();
    }

    @Override
    @RpcMethod(methodName = "updateSegmentInfo")
    public void updateSegmentInfo(SegmentLocationInfo locationInfo, SegmentLocationInfo.UpdateType updateType) {
        analyseService.updateSegmentInfo(locationInfo, updateType);
    }

    private RpcFuture queryRemoteNodeNode(String jsonString, SegmentDestination remoteURI) throws Exception {
        if (null == remoteURI) {
            QueryBean queryBean = queryBeanFactory.create(jsonString);
            remoteURI = queryBean.getQueryDestination();
        }
        Assert.notNull(remoteURI);
        String address = remoteURI.getAddress();
        String methodName = remoteURI.getMethodName();
        Class clazz = remoteURI.getServiceClass();
        ProxyFactory factory = ProxySelector.getInstance().getFactory();
        Invoker invoker = factory.getInvoker(null, clazz, new RPCUrl(new RPCDestination(address)), false);
        Result result = invoker.invoke(new SwiftInvocation(server.getMethodByName(methodName), new Object[]{jsonString}));
        RpcFuture future = (RpcFuture) result.getValue();
        if (null == future) {
            throw new Exception(result.getException());
        }
        return future;
    }

    @Override
    public ServiceType getServiceType() {
        return ServiceType.ANALYSE;
    }

    @Override
    public SwiftResultSet getQueryResult(QueryBean info) throws Exception {
        return analyseService.getQueryResult(info);
    }
}
