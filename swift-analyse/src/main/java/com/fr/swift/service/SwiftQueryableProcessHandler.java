package com.fr.swift.service;

import com.fr.swift.basic.URL;
import com.fr.swift.basics.AsyncRpcCallback;
import com.fr.swift.basics.Invocation;
import com.fr.swift.basics.Invoker;
import com.fr.swift.basics.InvokerCreator;
import com.fr.swift.basics.RpcFuture;
import com.fr.swift.basics.annotation.RegisteredHandler;
import com.fr.swift.basics.annotation.Target;
import com.fr.swift.basics.base.SwiftInvocation;
import com.fr.swift.basics.base.handler.BaseProcessHandler;
import com.fr.swift.basics.handler.QueryableProcessHandler;
import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.beans.annotation.SwiftScope;
import com.fr.swift.local.LocalUrl;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.query.builder.QueryBuilder;
import com.fr.swift.query.info.bean.query.QueryBeanFactory;
import com.fr.swift.query.query.Query;
import com.fr.swift.query.query.QueryBean;
import com.fr.swift.query.query.QueryType;
import com.fr.swift.query.result.SerializedQueryResultSetMerger;
import com.fr.swift.query.result.serialize.BaseSerializedQueryResultSet;
import com.fr.swift.result.qrs.EmptyQueryResultSet;
import com.fr.swift.result.qrs.QueryResultSet;
import com.fr.swift.source.SourceKey;
import com.fr.swift.structure.Pair;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

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
        SourceKey table = new SourceKey(queryBean.getTableName());
        // TODO: 2020/3/13
//        List<SegmentDestination> segmentDestinations = SegmentLocationProvider.getInstance().getSegmentLocationURI(table);
        List<Pair<URL, Set<String>>> pairs = processUrl(targets);
        Class<?> proxyClass = method.getDeclaringClass();
        Class<?>[] parameterTypes = method.getParameterTypes();
        String methodName = method.getName();

        CountDownLatch latch = new CountDownLatch(pairs.size());
        List<QueryResultSet<?>> resultSets = Collections.synchronizedList(new ArrayList<>());
        for (final Pair<URL, Set<String>> pair : pairs) {
            queryBean.setSegments(pair.getValue() == null ? Collections.emptySet() : pair.getValue());
            final String query = QueryBeanFactory.queryBean2String(queryBean);
            final Invoker<?> invoker = invokerCreator.createAsyncInvoker(proxyClass, pair.getKey());
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
                                        Invoker<?> invoker = invokerCreator.createSyncInvoker(proxyClass, pair.getKey());
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
        QueryResultSet<?> resultAfterMerge = (QueryResultSet<?>) mergeResult(resultSets, queryBean.getQueryType());
        Query<QueryResultSet<?>> postQuery = QueryBuilder.<QueryResultSet<?>>buildPostQuery(resultAfterMerge, queryBean);
        return postQuery.getQueryResult();
    }

    @Override
    protected Object mergeResult(List resultList, Object... args) {
        return SerializedQueryResultSetMerger.merge((QueryType) args[0], resultList);
    }

    @Override
    public List<Pair<URL, Set<String>>> processUrl(Target[] targets, Object... args) {
        return Collections.singletonList(new Pair<>(new LocalUrl(), new HashSet()));
    }
}
