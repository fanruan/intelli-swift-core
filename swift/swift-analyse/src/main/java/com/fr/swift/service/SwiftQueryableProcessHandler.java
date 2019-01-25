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
import com.fr.swift.basics.base.selector.UrlSelector;
import com.fr.swift.basics.handler.QueryableProcessHandler;
import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.beans.annotation.SwiftScope;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.property.SwiftProperty;
import com.fr.swift.query.builder.QueryBuilder;
import com.fr.swift.query.info.bean.query.QueryBeanFactory;
import com.fr.swift.query.query.Query;
import com.fr.swift.query.query.QueryBean;
import com.fr.swift.query.result.serialize.BaseSerializableQRS;
import com.fr.swift.result.EmptyDetailQueryResultSet;
import com.fr.swift.result.qrs.QueryResultSet;
import com.fr.swift.result.qrs.QueryResultSetMerger;
import com.fr.swift.segment.SegmentDestination;
import com.fr.swift.segment.SegmentLocationProvider;
import com.fr.swift.selector.ClusterSelector;
import com.fr.swift.source.SourceKey;
import com.fr.swift.structure.Pair;
import com.fr.swift.util.Crasher;
import com.fr.swift.util.Strings;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

/**
 * @author yee
 * @date 2018/10/25
 */
@SwiftBean
@SwiftScope("prototype")
@RegisteredHandler(QueryableProcessHandler.class)
public class SwiftQueryableProcessHandler extends BaseProcessHandler implements QueryableProcessHandler {

    public SwiftQueryableProcessHandler(InvokerCreator invokerCreator) {
        super(invokerCreator);
    }

    private static boolean isLocalURL(URL url) {
        return url == null || url.getDestination().getId() == null
                || url.getDestination().getId().equals(ClusterSelector.getInstance().getFactory().getCurrentId());
    }

    @Override
    public Object processResult(final Method method, Target[] targets, Object... args) throws Throwable {
        String queryJson = (String) args[0];
        final QueryBean queryBean = QueryBeanFactory.create(queryJson);
        queryBean.setQueryId(UUID.randomUUID().toString());
        SourceKey table = new SourceKey(queryBean.getTableName());
        List<SegmentDestination> segmentDestinations = SegmentLocationProvider.getInstance().getSegmentLocationURI(table);
        List<Pair<URL, Set<String>>> pairs = processUrl(targets, segmentDestinations);
        final List<QueryResultSet> resultSets = new ArrayList<QueryResultSet>();
        final Class proxyClass = method.getDeclaringClass();
        final Class<?>[] parameterTypes = method.getParameterTypes();
        final String methodName = method.getName();
        final CountDownLatch latch = new CountDownLatch(pairs.size());
        for (final Pair<URL, Set<String>> pair : pairs) {
            queryBean.setSegments(pair.getValue() == null ? Collections.<String>emptySet() : pair.getValue());
            final String query = QueryBeanFactory.queryBean2String(queryBean);
            final Invoker invoker = invokerCreator.createAsyncInvoker(proxyClass, pair.getKey());
            RpcFuture rpcFuture = (RpcFuture) invoke(invoker, proxyClass,
                    method, methodName, parameterTypes, query);
            rpcFuture.addCallback(new AsyncRpcCallback() {
                @Override
                public void success(final Object result) {
                    // 包装一下远程节点返回的resultSet，内部能通过invoker发起远程调用取下一页，使得上层查询不用区分本地和远程
                    final QueryResultSet rs = (QueryResultSet) result;
                    switch (queryBean.getQueryType()) {
                        case DETAIL_SORT:
                        case DETAIL:
                        case GROUP: {
                            BaseSerializableQRS qrs = (BaseSerializableQRS) rs;
                            BaseSerializableQRS.SyncInvoker syncInvoker = new BaseSerializableQRS.SyncInvoker() {
                                @Override
                                public <D, T extends BaseSerializableQRS<D>> T invoke() {
                                    Invoker invoker = invokerCreator.createSyncInvoker(proxyClass, pair.getKey());
                                    Invocation invocation = new SwiftInvocation(method, new Object[]{query});
                                    try {
                                        return (T) invoker.invoke(invocation).recreate();
                                    } catch (Throwable throwable) {
                                        return Crasher.crash(throwable);
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
                    latch.countDown();
                }

                @Override
                public void fail(Exception e) {
                    SwiftLoggers.getLogger().error("Remote invoke error:", e);
                    latch.countDown();
                }
            });
        }
        latch.await();
        if (resultSets.isEmpty()) {
            return new EmptyDetailQueryResultSet();
        }
        QueryResultSet resultAfterMerge = (QueryResultSet) mergeResult(resultSets, resultSets.get(0).getMerger());
        Query postQuery = QueryBuilder.buildPostQuery(resultAfterMerge, queryBean);
        return postQuery.getQueryResult();
    }

    @Override
    protected Object mergeResult(List resultList, Object... args) throws Throwable {
        return ((QueryResultSetMerger) args[0]).merge(resultList);
    }

    @Override
    public List<Pair<URL, Set<String>>> processUrl(Target[] targets, Object... args) {
        List<SegmentDestination> uris = (List<SegmentDestination>) args[0];
        Map<String, Pair<URL, Set<String>>> map = new HashMap<String, Pair<URL, Set<String>>>();
        for (SegmentDestination destination : uris) {
            String clusterId = destination.getClusterId();
            if (!map.containsKey(clusterId)) {
                URL url = UrlSelector.getInstance().getFactory().getURL(clusterId);
                map.put(clusterId, Pair.<URL, Set<String>>of(url, new HashSet<String>()));
            }
            String segmentId = destination.getSegmentId();
            if (Strings.isNotEmpty(segmentId)) {
                map.get(clusterId).getValue().add(segmentId);
            }
        }
        if (map.isEmpty() && !SwiftProperty.getProperty().isCluster()) {
            // 单机
            map.put(Strings.EMPTY, Pair.<URL, Set<String>>of(null, new HashSet<String>()));
        }
        return Collections.unmodifiableList(new ArrayList<Pair<URL, Set<String>>>(map.values()));
    }
}
