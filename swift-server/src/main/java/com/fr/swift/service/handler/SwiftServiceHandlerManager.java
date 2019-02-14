package com.fr.swift.service.handler;

import com.fr.swift.SwiftContext;
import com.fr.swift.event.base.AbstractAnalyseRpcEvent;
import com.fr.swift.event.base.AbstractGlobalRpcEvent;
import com.fr.swift.event.base.AbstractHistoryRpcEvent;
import com.fr.swift.event.base.AbstractIndexingRpcEvent;
import com.fr.swift.event.base.AbstractRealTimeRpcEvent;
import com.fr.swift.event.base.SwiftRpcEvent;
import com.fr.swift.service.handler.analyse.SwiftAnalyseEventHandler;
import com.fr.swift.service.handler.base.Handler;
import com.fr.swift.service.handler.global.SwiftGlobalEventHandler;
import com.fr.swift.service.handler.history.SwiftHistoryEventHandler;
import com.fr.swift.service.handler.indexing.SwiftIndexingEventHandler;
import com.fr.swift.service.handler.realtime.SwiftRealTimeEventHandler;

import java.io.Serializable;

/**
 * @author yee
 * @date 2018/6/8
 */
public class SwiftServiceHandlerManager implements Handler {
    private SwiftHistoryEventHandler historyEventHandler = SwiftContext.get().getBean(SwiftHistoryEventHandler.class);
    private SwiftAnalyseEventHandler analyseService = SwiftContext.get().getBean(SwiftAnalyseEventHandler.class);
    private SwiftRealTimeEventHandler realTimeEventHandler = SwiftContext.get().getBean(SwiftRealTimeEventHandler.class);
    private SwiftIndexingEventHandler indexingEventHandler = SwiftContext.get().getBean(SwiftIndexingEventHandler.class);
    private SwiftGlobalEventHandler globalEventHandler = SwiftContext.get().getBean(SwiftGlobalEventHandler.class);

    private SwiftServiceHandlerManager() {
    }

    public static SwiftServiceHandlerManager getManager() {
        return SingletonHandler.manager;
    }

    @Override
    public Serializable handle(SwiftRpcEvent event) throws Exception {
        switch (event.type()) {
            case HISTORY:
                return historyEventHandler.handle((AbstractHistoryRpcEvent) event);
            case REAL_TIME:
                return realTimeEventHandler.handle((AbstractRealTimeRpcEvent) event);
            case ANALYSE:
                return analyseService.handle((AbstractAnalyseRpcEvent) event);
            case INDEXING:
                return indexingEventHandler.handle((AbstractIndexingRpcEvent) event);
            case GLOBAL:
                return globalEventHandler.handle((AbstractGlobalRpcEvent) event);
            default:
                break;
        }
        return null;
    }

    private static class SingletonHandler {
        private static SwiftServiceHandlerManager manager = new SwiftServiceHandlerManager();
    }
}
