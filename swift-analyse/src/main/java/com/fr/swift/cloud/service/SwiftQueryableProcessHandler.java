package com.fr.swift.cloud.service;

import com.fr.swift.cloud.SwiftContext;
import com.fr.swift.cloud.basic.URL;
import com.fr.swift.cloud.basics.AsyncRpcCallback;
import com.fr.swift.cloud.basics.Invocation;
import com.fr.swift.cloud.basics.Invoker;
import com.fr.swift.cloud.basics.InvokerCreator;
import com.fr.swift.cloud.basics.RpcFuture;
import com.fr.swift.cloud.basics.annotation.RegisteredHandler;
import com.fr.swift.cloud.basics.annotation.Target;
import com.fr.swift.cloud.basics.base.SwiftInvocation;
import com.fr.swift.cloud.basics.base.handler.BaseProcessHandler;
import com.fr.swift.cloud.basics.base.selector.UrlSelector;
import com.fr.swift.cloud.basics.handler.QueryableProcessHandler;
import com.fr.swift.cloud.beans.annotation.SwiftBean;
import com.fr.swift.cloud.beans.annotation.SwiftScope;
import com.fr.swift.cloud.local.LocalUrl;
import com.fr.swift.cloud.log.SwiftLoggers;
import com.fr.swift.cloud.query.builder.QueryBuilder;
import com.fr.swift.cloud.query.info.bean.query.QueryBeanFactory;
import com.fr.swift.cloud.query.query.Query;
import com.fr.swift.cloud.query.query.QueryBean;
import com.fr.swift.cloud.query.result.SerializedQueryResultSetMerger;
import com.fr.swift.cloud.query.result.serialize.BaseSerializedQueryResultSet;
import com.fr.swift.cloud.result.qrs.EmptyQueryResultSet;
import com.fr.swift.cloud.result.qrs.QueryResultSet;
import com.fr.swift.cloud.segment.SegmentService;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

/**
 * @author yee
 * @date 2018/10/25
 */
@SwiftBean
@SwiftScope("prototype")
@RegisteredHandler(QueryableProcessHandler.class)
class SwiftQueryableProcessHandler extends BaseProcessHandler implements QueryableProcessHandler {

    public SwiftQueryableProcessHandler(InvokerCreator invokerCreator) {
        super(invokerCreator);
    }

    @Override
    public QueryResultSet<?> processResult(final Method method, Target[] targets, Object... args) throws Throwable {
        String queryJson = (String) args[0];
        final QueryBean queryBean = QueryBeanFactory.create(queryJson);
        Set<String> segments = queryBean.getSegments();
        List<URL> urls = processUrl(targets, segments);
        Class<?> proxyClass = method.getDeclaringClass();
        Class<?>[] parameterTypes = method.getParameterTypes();
        String methodName = method.getName();

        CountDownLatch latch = new CountDownLatch(urls.size());
        List<QueryResultSet<?>> resultSets = Collections.synchronizedList(new ArrayList<>());
        for (URL url : urls) {
            final String query = QueryBeanFactory.queryBean2String(queryBean);
            final Invoker<?> invoker = invokerCreator.createAsyncInvoker(proxyClass, url);
            RpcFuture<?> rpcFuture = (RpcFuture<?>) invoke(invoker, proxyClass, method, methodName, parameterTypes, query);
            rpcFuture.addCallback(new AsyncRpcCallback() {
                @Override
                public void success(final Object result) {
                    try {
                        // 包装一下远程节点返回的resultSet，内部能通过invoker发起远程调用取下一页，使得上层查询不用区分本地和远程
                        final QueryResultSet<?> rs = (QueryResultSet<?>) result;
                        switch (queryBean.getQueryType()) {
                            case DETAIL_SORT:
                            case DETAIL:
                            case GROUP: {
                                BaseSerializedQueryResultSet<?> qrs = (BaseSerializedQueryResultSet<?>) rs;
                                BaseSerializedQueryResultSet.SyncInvoker syncInvoker = new BaseSerializedQueryResultSet.SyncInvoker() {
                                    @Override
                                    public <D> BaseSerializedQueryResultSet<D> invoke() {
                                        Invoker<?> invoker = invokerCreator.createSyncInvoker(proxyClass, url);
                                        Invocation invocation = new SwiftInvocation(method, new Object[]{query});
                                        try {
                                            return (BaseSerializedQueryResultSet<D>) invoker.invoke(invocation).recreate();
                                        } catch (Throwable throwable) {
                                            throw new RuntimeException(throwable);
                                        }
                                    }
                                };
                                qrs.setInvoker(syncInvoker);
                                resultSets.add(qrs);
                                break;
                            }
                            default:
                                resultSets.add(rs);
                        }
                    } finally {
                        latch.countDown();
                    }
                }

                @Override
                public void fail(Exception e) {
                    latch.countDown();
                    SwiftLoggers.getLogger().error("Remote invoke error:", e);
                }
            });
        }
        latch.await();
        if (resultSets.isEmpty()) {
            return EmptyQueryResultSet.get();
        }
        QueryResultSet<?> resultAfterMerge = (QueryResultSet<?>) mergeResult(resultSets, queryBean);
        Query<QueryResultSet<?>> postQuery = QueryBuilder.buildPostQuery(resultAfterMerge, queryBean);
        return postQuery.getQueryResult();
    }

    @Override
    protected Object mergeResult(List resultList, Object... args) {
        return SerializedQueryResultSetMerger.merge((QueryBean) args[0], resultList);
    }

    /**
     * querySegments == null或empty，查所有节点
     * querySegments全在本地，查本地
     * querySegments包含@FINE_IO@-1，查本地
     * 其他情况查所有节点
     *
     * @param targets
     * @param args
     * @return
     */
    @Override
    public List<URL> processUrl(Target[] targets, Object... args) {
        HashSet<String> querySegments = (HashSet<String>) args[0];
        if (querySegments == null || querySegments.isEmpty()) {
            return nodeContainer.getOnlineNodes().keySet().stream().map(id -> UrlSelector.getInstance().getFactory().getURL(id)).collect(Collectors.toList());
        }
        if (SwiftContext.get().getBean(SegmentService.class).existAll(querySegments)) {
            return Collections.singletonList(new LocalUrl());
        }
        if (querySegments.size() == 1 && ((String) querySegments.toArray()[0]).contains("@FINE_IO@-1")) {
            return Collections.singletonList(new LocalUrl());
        }
        return nodeContainer.getOnlineNodes().keySet().stream().map(id -> UrlSelector.getInstance().getFactory().getURL(id)).collect(Collectors.toList());
    }
}
