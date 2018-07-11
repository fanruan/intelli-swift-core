package com.fr.swift.service.handler.global;

import com.fr.event.EventDispatcher;
import com.fr.swift.event.base.AbstractGlobalRpcEvent;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.rpc.client.AsyncRpcCallback;
import com.fr.swift.service.ClusterSwiftServerService;
import com.fr.swift.service.ServiceType;
import com.fr.swift.service.entity.ClusterEntity;
import com.fr.swift.service.handler.base.AbstractHandler;
import com.fr.swift.structure.Pair;
import com.fr.swift.task.TaskKey;
import com.fr.swift.task.TaskResult;
import com.fr.swift.task.impl.TaskEvent;
import com.fr.third.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;

/**
 * @author yee
 * @date 2018/6/27
 */
@Service
public class SwiftGlobalEventHandler extends AbstractHandler<AbstractGlobalRpcEvent> {
    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(SwiftGlobalEventHandler.class);

    @Override
    public <S extends Serializable> S handle(AbstractGlobalRpcEvent event) {
        switch (event.subEvent()) {
            case TASK_DONE:
                Pair<TaskKey, TaskResult> pair = (Pair<TaskKey, TaskResult>) event.getContent();
                EventDispatcher.fire(TaskEvent.DONE, pair);
                break;
            case CLEAN:
                Map<String, ClusterEntity> analyseMap = ClusterSwiftServerService.getInstance().getClusterEntityByService(ServiceType.ANALYSE);
                Map<String, ClusterEntity> realTimeMap = ClusterSwiftServerService.getInstance().getClusterEntityByService(ServiceType.REAL_TIME);
                Map<String, ClusterEntity> historyMap = ClusterSwiftServerService.getInstance().getClusterEntityByService(ServiceType.HISTORY);
                Map<String, ClusterEntity> indexingMap = ClusterSwiftServerService.getInstance().getClusterEntityByService(ServiceType.INDEXING);
                String[] sourceKeys = (String[]) event.getContent();
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
                break;
            default:
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
