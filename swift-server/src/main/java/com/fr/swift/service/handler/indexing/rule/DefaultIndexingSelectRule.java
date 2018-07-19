package com.fr.swift.service.handler.indexing.rule;

import com.fr.swift.basics.Invoker;
import com.fr.swift.basics.ProxyFactory;
import com.fr.swift.basics.base.SwiftInvocation;
import com.fr.swift.basics.base.SwiftResult;
import com.fr.swift.basics.base.selector.ProxySelector;
import com.fr.swift.config.bean.IndexingSelectRule;
import com.fr.swift.info.ServerCurrentStatus;
import com.fr.swift.netty.rpc.url.RPCDestination;
import com.fr.swift.netty.rpc.url.RPCUrl;
import com.fr.swift.service.IndexingService;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * @author yee
 * @date 2018/7/16
 */
public class DefaultIndexingSelectRule implements IndexingSelectRule {
    /**
     * 获取所有索引节点当前运行状态
     * 状况最佳的去索引
     *
     * @param indexingServices
     * @return
     */
    @Override
    public String select(Set<String> indexingServices) throws Exception {
        if (1 == indexingServices.size()) {
            return indexingServices.iterator().next();
        }
        final List<ServerCurrentStatus> serverCurrentStatuses = new ArrayList<ServerCurrentStatus>();
        ProxyFactory factory = ProxySelector.getInstance().getFactory();
        for (String indexingService : indexingServices) {
            Invoker invoker = factory.getInvoker(null, IndexingService.class, new RPCUrl(new RPCDestination(indexingService)), true);
            Method method = IndexingService.class.getMethod("currentStatus");
            SwiftResult result = (SwiftResult) invoker.invoke(new SwiftInvocation(method, null));
            serverCurrentStatuses.add((ServerCurrentStatus) result.getValue());
        }
        if (serverCurrentStatuses.isEmpty()) {
            for (String indexingService : indexingServices) {
                return indexingService;
            }
            return null;
        }
        Collections.sort(serverCurrentStatuses);
        return serverCurrentStatuses.get(0).getClusterId();
    }
}
