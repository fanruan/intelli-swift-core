package com.fr.swift.service;

import com.fr.event.Event;
import com.fr.event.EventDispatcher;
import com.fr.event.Listener;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.event.base.SwiftRpcEvent;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.property.SwiftProperty;
import com.fr.swift.source.DataSource;
import com.fr.swift.stuff.DefaultIndexingStuff;
import com.fr.swift.task.TaskKey;
import com.fr.swift.task.impl.TaskEvent;

import java.io.Serializable;
import java.util.Map;

/**
 * @author pony
 * @date 2017/11/14
 * 本地swift服务
 */
public class LocalSwiftServerService extends AbstractSwiftServerService {

    private static final long serialVersionUID = 9167111609239393074L;
    private IndexingService indexingService;
    private RealtimeService realTimeService;
    private HistoryService historyService;
    private AnalyseService analyseService;
    private CollateService collateService;

    @Override
    public Serializable trigger(SwiftRpcEvent event) {
        return null;
    }

    @Override
    public void registerService(SwiftService service) {
        synchronized (this) {
            switch (service.getServiceType()) {
                case ANALYSE:
                    analyseService = (AnalyseService) service;
                    break;
                case HISTORY:
                    historyService = (HistoryService) service;
                    break;
                case INDEXING:
                    indexingService = (IndexingService) service;
                    break;
                case REAL_TIME:
                    realTimeService = (RealtimeService) service;
                    break;
                case COLLATE:
                    collateService = (CollateService) service;
                default:
            }
        }
    }

    @Override
    public void unRegisterService(SwiftService service) {
        synchronized (this) {
            switch (service.getServiceType()) {
                case ANALYSE:
                    analyseService = null;
                    break;
                case HISTORY:
                    historyService = null;
                    break;
                case INDEXING:
                    indexingService = null;
                    break;
                case REAL_TIME:
                    realTimeService = null;
                    break;
                case COLLATE:
                    collateService = null;
                default:
            }
        }
    }


    @Override
    protected void initListener() {
        super.initListener();
        EventDispatcher.listen(TaskEvent.RUN, new Listener<Map<TaskKey, ?>>() {
            @Override
            public void on(Event event, Map<TaskKey, ?> taskKeyMap) {
                if (!SwiftContext.get().getBean("swiftProperty", SwiftProperty.class).isCluster()) {
                    try {
                        indexingService.index(new DefaultIndexingStuff((Map<TaskKey, DataSource>) taskKeyMap));
                    } catch (Exception e) {
                        SwiftLoggers.getLogger().error(e);
                    }
                }

            }
        });
        EventDispatcher.listen(TaskEvent.CANCEL, new Listener<TaskKey>() {
            @Override
            public void on(Event event, TaskKey taskKey) {
                SwiftLoggers.getLogger().info("Local Task cancel");
            }
        });
    }
}
