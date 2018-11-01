package com.fr.swift.basics.base.handler;

import com.fr.swift.basics.AsyncRpcCallback;
import com.fr.swift.basics.InvokerCreater;
import com.fr.swift.basics.RpcFuture;
import com.fr.swift.basics.URL;
import com.fr.swift.basics.annotation.Target;
import com.fr.swift.basics.base.ProxyServiceRegistry;
import com.fr.swift.basics.base.selector.UrlSelector;
import com.fr.swift.basics.handler.IndexPHDefiner;
import com.fr.swift.config.bean.IndexingSelectRule;
import com.fr.swift.config.bean.ServerCurrentStatus;
import com.fr.swift.config.service.IndexingSelectRuleService;
import com.fr.swift.log.SwiftLoggers;

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
public abstract class BaseIndexStatusProcessHandler extends BaseProcessHandler implements IndexPHDefiner.StatusProcessHandler {

    public BaseIndexStatusProcessHandler(InvokerCreater invokerCreater) {
        super(invokerCreater);
    }

    @Override
    protected Object mergeResult(List resultList) throws Throwable {
        final List<ServerCurrentStatus> statusList = new ArrayList<ServerCurrentStatus>();
        for (Object obj : resultList) {
            if (obj instanceof RpcFuture) {
                RpcFuture future = (RpcFuture) obj;
                final CountDownLatch latch = new CountDownLatch(1);
                future.addCallback(new AsyncRpcCallback() {
                    @Override
                    public void success(Object result) {
                        if (result instanceof ServerCurrentStatus) {
                            statusList.add((ServerCurrentStatus) result);
                        }
                        latch.countDown();
                    }

                    @Override
                    public void fail(Exception e) {
                        SwiftLoggers.getLogger().warn(e.getMessage());
                        latch.countDown();
                    }
                });
                latch.await();
            } else if (obj instanceof ServerCurrentStatus) {
                statusList.add((ServerCurrentStatus) obj);
            }
        }
        IndexingSelectRule rule = ProxyServiceRegistry.INSTANCE.getInternalService(IndexingSelectRuleService.class).getCurrentRule();
        return rule.select(statusList);
    }

    @Override
    public List<URL> processUrl(Target target) {
        // TODO 获取所有Index节点地址
        Set<String> clusterIds = new HashSet<String>();
        List<URL> urls = new ArrayList<URL>();
        for (String clusterId : clusterIds) {
            urls.add(UrlSelector.getInstance().getFactory().getURL(clusterId));
        }
        return Collections.unmodifiableList(urls);
    }
}
