package com.fr.swift.cloud.service;

import com.fr.swift.cloud.SwiftContext;
import com.fr.swift.cloud.analyse.CalcPage;
import com.fr.swift.cloud.analyse.merged.BaseDetailResultSet;
import com.fr.swift.cloud.analyse.merged.CalcDetailResultSet;
import com.fr.swift.cloud.analyse.merged.EmptySwiftResultSet;
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
import com.fr.swift.cloud.basics.handler.DetailQueryableProcessHandler;
import com.fr.swift.cloud.beans.annotation.SwiftBean;
import com.fr.swift.cloud.beans.annotation.SwiftScope;
import com.fr.swift.cloud.local.LocalUrl;
import com.fr.swift.cloud.log.SwiftLoggers;
import com.fr.swift.cloud.query.info.bean.query.QueryBeanFactory;
import com.fr.swift.cloud.query.query.QueryBean;
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
 * @author xiqiu
 * @date 2021/1/15
 * @description
 * @since swift-1.2.0
 */

@SwiftBean
@SwiftScope("prototype")
@RegisteredHandler(DetailQueryableProcessHandler.class)
public class DetailSwiftQueryableProcessHandler extends BaseProcessHandler implements DetailQueryableProcessHandler {
    public DetailSwiftQueryableProcessHandler(InvokerCreator invokerCreator) {
        super(invokerCreator);
    }

    @Override
    public BaseDetailResultSet processResult(final Method method, Target[] targets, Object... args) throws Throwable {
        String queryJson = (String) args[0];
        final QueryBean queryBean = QueryBeanFactory.create(queryJson);
        Set<String> segments = queryBean.getSegments();
        List<URL> urls = processUrl(targets, segments);
        Class<?> proxyClass = method.getDeclaringClass();
        Class<?>[] parameterTypes = method.getParameterTypes();
        String methodName = method.getName();

        CountDownLatch latch = new CountDownLatch(urls.size());
        List<CalcPage> resultSets = Collections.synchronizedList(new ArrayList<>());
        for (URL url : urls) {
            final String query = QueryBeanFactory.queryBean2String(queryBean);
            final Invoker<?> invoker = invokerCreator.createAsyncInvoker(proxyClass, url);
            RpcFuture<?> rpcFuture = (RpcFuture<?>) invoke(invoker, proxyClass, method, methodName, parameterTypes, query);
            rpcFuture.addCallback(new AsyncRpcCallback() {
                @Override
                public void success(final Object result) {
                    try {
                        final CalcPage rs = (CalcPage) result;
                        switch (queryBean.getQueryType()) {
                            case DETAIL_SORT:
                            case DETAIL:
                            case GROUP: {
                                CalcPage qrs = rs;
                                CalcPage.SyncInvoker syncInvoker = () -> {
                                    Invoker<?> invoker = invokerCreator.createSyncInvoker(proxyClass, url);
                                    Invocation invocation = new SwiftInvocation(method, new Object[]{query});
                                    try {
                                        return (CalcPage) invoker.invoke(invocation).recreate();
                                    } catch (Throwable throwable) {
                                        throw new RuntimeException(throwable);
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
            return EmptySwiftResultSet.get();
        }

        return (CalcDetailResultSet) mergeResult(resultSets);
    }


    @Override
    protected List<URL> processUrl(Target[] targets, Object... args) throws Exception {
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

    @Override
    protected Object mergeResult(List resultList, Object... args) {
        List<CalcPage> calList = resultList;
        return new CalcDetailResultSet(calList.get(0).getFetchSize(), calList);
    }
}
