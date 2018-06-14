package com.fr.swift.service.handler.history;

import com.fr.swift.event.base.AbstractHistoryRpcEvent;
import com.fr.swift.event.history.HistoryLoadRpcEvent;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.service.handler.base.Handler;
import com.fr.swift.util.concurrent.PoolThreadFactory;
import com.fr.third.springframework.beans.factory.annotation.Autowired;
import com.fr.third.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author yee
 * @date 2018/6/8
 */
@Service
public class SwiftHistoryEventHandler implements Handler<AbstractHistoryRpcEvent> {

    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(SwiftHistoryEventHandler.class);
    @Autowired
    private HistoryDataSyncManager historyDataSyncManager;
    private ExecutorService historyService = Executors.newCachedThreadPool(new PoolThreadFactory(getClass()));

    @Override
    public <S extends Serializable> S handle(AbstractHistoryRpcEvent event) {
        switch (event.subEvent()) {
            case LOAD:
                return historyDataSyncManager.handle((HistoryLoadRpcEvent) event);
        }
        return null;
    }
}
