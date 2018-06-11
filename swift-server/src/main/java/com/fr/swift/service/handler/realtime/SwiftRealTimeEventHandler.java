package com.fr.swift.service.handler.realtime;

import com.fr.swift.config.service.SwiftClusterSegmentService;
import com.fr.swift.event.base.AbstractRealTimeRpcEvent;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.query.QueryInfo;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.service.RealtimeService;
import com.fr.swift.service.handler.base.Handler;
import com.fr.swift.service.handler.history.SwiftHistoryEventHandler;
import com.fr.swift.source.SwiftResultSet;
import com.fr.swift.util.concurrent.PoolThreadFactory;
import com.fr.third.springframework.beans.factory.annotation.Autowired;
import com.fr.third.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
public class SwiftRealTimeEventHandler implements Handler<AbstractRealTimeRpcEvent> {

    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(SwiftHistoryEventHandler.class);
    private ExecutorService realTimeService = Executors.newCachedThreadPool(new PoolThreadFactory(getClass()));

    @Autowired
    private SwiftClusterSegmentService clusterSegmentService;

    @Override
    public <S extends Serializable> S handle(AbstractRealTimeRpcEvent event) {
        // TODO 获取RealtimeService代理
        List<RealtimeService> services = new ArrayList<RealtimeService>();
        switch (event.subEvent()) {
            case QUERY:
                final QueryInfo queryInfo = (QueryInfo) event.getContent();
                List<SwiftResultSet> result = new ArrayList<SwiftResultSet>();
                List<Future<?>> futures = new ArrayList<Future<?>>();
                for (final RealtimeService service : services) {
                    futures.add(realTimeService.submit(new FutureTask<SwiftResultSet>(new Callable<SwiftResultSet>() {
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
            case INSERT:
                // TODO insert传resultSet？
                return null;
            case RECOVER:
                final Map<String, List<SegmentKey>> map = clusterSegmentService.getClusterSegments();
                for (final RealtimeService service : services) {
                    realTimeService.submit(new Runnable() {
                        @Override
                        public void run() {
                            String clusterId = service.getID();
                            List<SegmentKey> list = map.get(clusterId);
                            if (null != list && !list.isEmpty()) {
                                try {
                                    service.recover(list);
                                } catch (Exception e) {
                                    LOGGER.error(String.format("clusterId: %s, recover error!", clusterId), e);
                                }
                            }
                        }
                    });
                }
                return null;
            case MERGE:
                final Map<String, List<SegmentKey>> mergeMap = clusterSegmentService.getClusterSegments();
                for (final RealtimeService service : services) {
                    realTimeService.submit(new Runnable() {
                        @Override
                        public void run() {
                            String clusterId = service.getID();
                            List<SegmentKey> list = mergeMap.get(clusterId);
                            if (null != list && !list.isEmpty()) {
                                try {
                                    service.merge(list);
                                } catch (Exception e) {
                                    LOGGER.error(String.format("clusterId: %s, merge error!", clusterId), e);
                                }
                            }
                        }
                    });
                }
                return null;
        }
        return null;
    }
}
