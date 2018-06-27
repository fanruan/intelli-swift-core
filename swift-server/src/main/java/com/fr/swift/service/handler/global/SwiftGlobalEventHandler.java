package com.fr.swift.service.handler.global;

import com.fr.swift.event.base.CleanMetaDataCacheEvent;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.rpc.client.AsyncRpcCallback;
import com.fr.swift.service.ClusterSwiftServerService;
import com.fr.swift.service.ServiceType;
import com.fr.swift.service.entity.ClusterEntity;
import com.fr.swift.service.handler.base.AbstractHandler;
import com.fr.third.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;

/**
 * @author yee
 * @date 2018/6/27
 */
@Service
public class SwiftGlobalEventHandler extends AbstractHandler<CleanMetaDataCacheEvent> {
    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(SwiftGlobalEventHandler.class);

    @Override
    public <S extends Serializable> S handle(CleanMetaDataCacheEvent event) {
        Map<String, ClusterEntity> analyseMap = ClusterSwiftServerService.getInstance().getClusterEntityByService(ServiceType.ANALYSE);
        Map<String, ClusterEntity> realTimeMap = ClusterSwiftServerService.getInstance().getClusterEntityByService(ServiceType.REAL_TIME);
        Map<String, ClusterEntity> historyMap = ClusterSwiftServerService.getInstance().getClusterEntityByService(ServiceType.HISTORY);
        Map<String, ClusterEntity> indexingMap = ClusterSwiftServerService.getInstance().getClusterEntityByService(ServiceType.INDEXING);
        String[] sourceKeys = event.getContent();
        try {
            if (null != sourceKeys) {
                clean(analyseMap, sourceKeys);
                clean(realTimeMap, sourceKeys);
                clean(historyMap, sourceKeys);
                clean(indexingMap, sourceKeys);
            }
        } catch (Exception e) {
            LOGGER.error(e);
        }
        return null;
    }

    private void clean(Map<String, ClusterEntity> map, String[] sourceKeys) throws Exception {
        Iterator<Map.Entry<String, ClusterEntity>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, ClusterEntity> entry = iterator.next();
            runAsyncRpc(entry.getKey(), entry.getValue().getServiceClass(), "cleanMetaCache", sourceKeys)
                    .addCallback(new AsyncRpcCallback() {
                        @Override
                        public void success(Object result) {

                        }

                        @Override
                        public void fail(Exception e) {

                        }
                    });
        }
    }
}
