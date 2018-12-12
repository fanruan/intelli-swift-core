package com.fr.swift.basics.base.handler;

import com.fr.swift.basic.URL;
import com.fr.swift.basics.AsyncRpcCallback;
import com.fr.swift.basics.Invoker;
import com.fr.swift.basics.InvokerCreator;
import com.fr.swift.basics.RpcFuture;
import com.fr.swift.basics.annotation.Target;
import com.fr.swift.basics.base.selector.UrlSelector;
import com.fr.swift.basics.handler.QueryableProcessHandler;
import com.fr.swift.property.SwiftProperty;
import com.fr.swift.query.info.bean.query.QueryBeanFactory;
import com.fr.swift.query.query.QueryBean;
import com.fr.swift.result.qrs.DSType;
import com.fr.swift.result.qrs.QueryResultSet;
import com.fr.swift.result.qrs.QueryResultSetMerger;
import com.fr.swift.result.qrs.QueryResultSetUtils;
import com.fr.swift.segment.SegmentDestination;
import com.fr.swift.segment.SegmentLocationProvider;
import com.fr.swift.source.SourceKey;
import com.fr.swift.structure.Pair;
import com.fr.swift.util.Crasher;

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
public class SwiftQueryableProcessHandler extends BaseProcessHandler implements QueryableProcessHandler {

    public SwiftQueryableProcessHandler(InvokerCreator invokerCreator) {
        super(invokerCreator);
    }

    @Override
    public Object processResult(final Method method, Target target, Object... args) throws Throwable {
        String queryJson = (String) args[0];
        QueryBean queryBean = QueryBeanFactory.create(queryJson);
        queryBean.setQueryId(UUID.randomUUID().toString());
        SourceKey table = new SourceKey(queryBean.getTableName());
        List<SegmentDestination> segmentDestinations = SegmentLocationProvider.getInstance().getSegmentLocationURI(table);
        List<Pair<URL, Set<String>>> pairs = processUrl(target, segmentDestinations);
        final List<QueryResultSet> resultSets = new ArrayList<QueryResultSet>();
        final Class proxyClass = method.getDeclaringClass();
        final Class<?>[] parameterTypes = method.getParameterTypes();
        final String methodName = method.getName();
        final CountDownLatch latch = new CountDownLatch(pairs.size());
        for (final Pair<URL, Set<String>> pair : pairs) {
            queryBean.setSegments(pair.getValue());
            final String query = QueryBeanFactory.queryBean2String(queryBean);
            // TODO: 2018/12/01 这边这么调是不对的，这个URL不一定启了AnalyseService，有可能只是一台History或者Memory
            final Invoker invoker = invokerCreator.createAsyncInvoker(proxyClass, pair.getKey());
            RpcFuture rpcFuture = (RpcFuture) invoke(invoker, proxyClass,
                    method, methodName, parameterTypes, query);
            rpcFuture.addCallback(new AsyncRpcCallback() {
                @Override
                public void success(final Object result) {
                    if (!isLocalURL(pair.getKey())) {
                        // 包装一下远程节点返回的resultSet，内部能通过invoker发起远程调用取下一页，使得上层查询不用区分本地和远程
                        final QueryResultSet rs = (QueryResultSet) result;
                        resultSets.add(new QueryResultSet() {
                            private String queryJson = query;
                            private DSType type = rs.type();
                            private int fetchSize = rs.getFetchSize();
                            private QueryResultSet resultSet = rs;

                            @Override
                            public int getFetchSize() {
                                return fetchSize;
                            }

                            @Override
                            public DSType type() {
                                return type;
                            }

                            @Override
                            public Object getPage() {
                                Object ret = resultSet.getPage();
                                // TODO: 2018/11/27 如何判断远程是否还有下一页？无脑取下一个resultSet判断是否为空？还是通过接口支持？
                                if (hasNextPage()) {
                                    Invoker invoker = invokerCreator.createSyncInvoker(proxyClass, pair.getKey());
                                    try {
                                        resultSet = (QueryResultSet) invoke(invoker, proxyClass,
                                                method, methodName, parameterTypes, queryJson);
                                    } catch (Throwable throwable) {
                                        return Crasher.crash(throwable);
                                    }
                                }
                                return ret;
                            }

                            @Override
                            public boolean hasNextPage() {
                                return resultSet != null && resultSet.hasNextPage();
                            }
                        });
                    } else {
                        resultSets.add((QueryResultSet) result);
                    }
                    latch.countDown();
                }

                @Override
                public void fail(Exception e) {
                    // TODO: 2018/11/26
                }
            });
        }
        latch.await();
        QueryResultSetMerger merger = QueryResultSetUtils.createMerger(queryBean.getQueryType());
        // TODO: 2018/11/27 postAggregation
        return mergeResult(resultSets, merger);
    }

    private static boolean isLocalURL(URL url) {
        return url == null || url.getDestination().getId().equals(SwiftProperty.getProperty().getClusterId());
    }

    @Override
    protected Object mergeResult(List resultList, Object... args) throws Throwable {
        return ((QueryResultSetMerger) args[0]).merge(resultList);
    }

    @Override
    public List<Pair<URL, Set<String>>> processUrl(Target target, Object... args) {
        List<SegmentDestination> uris = (List<SegmentDestination>) args[0];
        Map<String, Pair<URL, Set<String>>> map = new HashMap<String, Pair<URL, Set<String>>>();
        for (SegmentDestination destination : uris) {
            String clusterId = destination.getClusterId();
            if (!map.containsKey(clusterId)) {
                URL url = UrlSelector.getInstance().getFactory().getURL(clusterId);
                map.put(clusterId, Pair.<URL, Set<String>>of(url, new HashSet<String>()));
            }
            map.get(clusterId).getValue().add(destination.getClusterId());
        }
        return Collections.unmodifiableList(new ArrayList<Pair<URL, Set<String>>>(map.values()));
    }
}
