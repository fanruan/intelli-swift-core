package com.fr.swift.service;

import com.fr.swift.context.SwiftContext;
import com.fr.swift.db.Where;
import com.fr.swift.event.SwiftEventDispatcher;
import com.fr.swift.event.SwiftEventListener;
import com.fr.swift.event.base.AbstractGlobalRpcEvent;
import com.fr.swift.event.base.SwiftRpcEvent;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.property.SwiftProperty;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.SourceKey;
import com.fr.swift.structure.Pair;
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
        try {
            if (event.type() == SwiftRpcEvent.EventType.GLOBAL) {
                switch (((AbstractGlobalRpcEvent) event).subEvent()) {
                    case DELETE:
                        Pair<SourceKey, Where> content = (Pair<SourceKey, Where>) event.getContent();
                        realTimeService.delete(content.getKey(), content.getValue());
                        historyService.delete(content.getKey(), content.getValue());
                        return null;
                    default:
                }
            }
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e);
        }
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
        SwiftEventDispatcher.listen(TaskEvent.RUN, new SwiftEventListener<Map<TaskKey, ?>>() {
            @Override
            public void on(Map<TaskKey, ?> taskKeyMap) {
                if (!SwiftContext.get().getBean("swiftProperty", SwiftProperty.class).isCluster()) {
                    try {
                        indexingService.index(new DefaultIndexingStuff((Map<TaskKey, DataSource>) taskKeyMap));
                    } catch (Exception e) {
                        SwiftLoggers.getLogger().error(e);
                    }
                }

            }
        });
        SwiftEventDispatcher.listen(TaskEvent.CANCEL, new SwiftEventListener<TaskKey>() {
            @Override
            public void on(TaskKey taskKey) {
                SwiftLoggers.getLogger().info("Local Task cancel");
            }
        });
    }
}
