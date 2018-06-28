package com.fr.swift.service.handler.indexing.rule;

import com.fr.swift.Invoker;
import com.fr.swift.ProxyFactory;
import com.fr.swift.info.ServerCurrentStatus;
import com.fr.swift.invocation.SwiftInvocation;
import com.fr.swift.result.SwiftResult;
import com.fr.swift.rpc.url.RPCDestination;
import com.fr.swift.rpc.url.RPCUrl;
import com.fr.swift.selector.ProxySelector;
import com.fr.swift.service.entity.ClusterEntity;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author yee
 * @date 2018/6/11
 */
public interface IndexingSelectRule {
    IndexingSelectRule DEFAULT = new IndexingSelectRule() {
        /**
         * 获取所有索引节点当前运行状态
         * 状况最佳的去索引
         *
         * @param indexingServices
         * @return
         */
        @Override
        public String select(Map<String, ClusterEntity> indexingServices) throws Exception {
            if (1 == indexingServices.size()) {
                return indexingServices.keySet().iterator().next();
            }
            Iterator<Map.Entry<String, ClusterEntity>> iterator = indexingServices.entrySet().iterator();
            final List<ServerCurrentStatus> serverCurrentStatuses = new ArrayList<ServerCurrentStatus>();
            ProxyFactory factory = ProxySelector.getInstance().getFactory();
            while (iterator.hasNext()) {
                Map.Entry<String, ClusterEntity> entityEntry = iterator.next();
                Invoker invoker = factory.getInvoker(null, entityEntry.getValue().getServiceClass(), new RPCUrl(new RPCDestination(entityEntry.getKey())), true);
                Method method = entityEntry.getValue().getServiceClass().getMethod("currentStatus");
                SwiftResult result = (SwiftResult) invoker.invoke(new SwiftInvocation(method, null));
                serverCurrentStatuses.add((ServerCurrentStatus) result.getValue());
            }
            if (serverCurrentStatuses.isEmpty()) {
                Iterator<String> keyIterator = indexingServices.keySet().iterator();
                if (keyIterator.hasNext()) {
                    return keyIterator.next();
                }
                return null;
            }
            Collections.sort(serverCurrentStatuses);
            return serverCurrentStatuses.get(0).getClusterId();
        }
    };

    String select(Map<String, ClusterEntity> indexingServices) throws Exception;

}
