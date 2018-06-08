package com.fr.swift.service.handler;

import com.fr.swift.context.SwiftContext;
import com.fr.swift.event.base.AbstractAnalyseEvent;
import com.fr.swift.event.base.AbstractHistoryEvent;
import com.fr.swift.event.base.AbstractIndexingEvent;
import com.fr.swift.event.base.AbstractRealTimeEvent;
import com.fr.swift.event.base.SwiftEvent;
import com.fr.swift.service.handler.analyse.SwiftAnalyseEventHandler;
import com.fr.swift.service.handler.base.Handler;
import com.fr.swift.service.handler.history.SwiftHistoryEventHandler;
import com.fr.swift.service.handler.indexing.SwiftIndexingEventHandler;
import com.fr.swift.service.handler.realtime.SwiftRealTimeEventHandler;

/**
 * @author yee
 * @date 2018/6/8
 */
public class SwiftServiceHandlerManager implements Handler {
    private SwiftHistoryEventHandler historyEventHandler = SwiftContext.getInstance().getBean(SwiftHistoryEventHandler.class);
    private SwiftAnalyseEventHandler analyseService = SwiftContext.getInstance().getBean(SwiftAnalyseEventHandler.class);
    private SwiftRealTimeEventHandler realTimeEventHandler = SwiftContext.getInstance().getBean(SwiftRealTimeEventHandler.class);
    private SwiftIndexingEventHandler indexingEventHandler = SwiftContext.getInstance().getBean(SwiftIndexingEventHandler.class);

    private SwiftServiceHandlerManager() {
    }

    public static SwiftServiceHandlerManager getManager() {
        return SingletonHandler.manager;
    }

    @Override
    public void handle(SwiftEvent event) {
        switch (event.type()) {
            case HISTORY:
                historyEventHandler.handle((AbstractHistoryEvent) event);
                break;
            case REAL_TIME:
                realTimeEventHandler.handle((AbstractRealTimeEvent) event);
                break;
            case ANALYSE:
                analyseService.handle((AbstractAnalyseEvent) event);
                break;
            case INDEXING:
                indexingEventHandler.handle((AbstractIndexingEvent) event);
                break;
            default:
                break;
        }
    }

    private static class SingletonHandler {
        private static SwiftServiceHandlerManager manager = new SwiftServiceHandlerManager();
    }
}
