package com.fr.swift.service.handler.history;

import com.fr.swift.event.base.AbstractHistoryRpcEvent;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.query.QueryInfo;
import com.fr.swift.service.SwiftHistoryService;
import com.fr.swift.service.handler.base.Handler;
import com.fr.swift.source.SwiftResultSet;
import com.fr.swift.util.concurrent.PoolThreadFactory;
import com.fr.third.springframework.beans.factory.annotation.Autowired;
import com.fr.third.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

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
        //TODO 拿到HistoryService的代理对象
        List<SwiftHistoryService> services = new ArrayList<SwiftHistoryService>();

        switch (event.subEvent()) {
            case QUERY:
                final QueryInfo queryInfo = (QueryInfo) event.getContent();
                List<SwiftResultSet> result = new ArrayList<SwiftResultSet>();
                List<Future<?>> futures = new ArrayList<Future<?>>();
                for (final SwiftHistoryService service : services) {
                    futures.add(historyService.submit(new FutureTask<SwiftResultSet>(new Callable<SwiftResultSet>() {
                        @Override
                        public SwiftResultSet call() throws Exception {
                            return service.query(queryInfo);
                        }
                    })));
                }
                try {
                    for (Future<?> future : futures) {
                        result.add((SwiftResultSet) future.get());
                    }
                } catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                }
                return (S) result;
            case LOAD:
                return historyDataSyncManager.handle(services);
        }
        return null;
    }
}
