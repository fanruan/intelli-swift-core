package com.fr.swift.service;

import com.fr.swift.config.service.SwiftMetaDataService;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.event.base.SwiftRpcEvent;
import com.fr.swift.service.listener.RealTimeIndexingFinishListener;
import com.fr.swift.stuff.RealTimeIndexingStuff;

import java.io.Serializable;

/**
 * Created by pony on 2017/11/14.
 * 本地swift服务
 */
public class LocalSwiftServerService extends AbstractSwiftServerService {

    private SwiftIndexingService indexingService;
    private SwiftRealtimeService realTimeService;
    private SwiftHistoryService historyService;
    private SwiftAnalyseService analyseService;

    @Override
    public Serializable trigger(SwiftRpcEvent event) {
        return null;
    }

    @Override
    public void registerService(SwiftService service) {
        synchronized (this) {
            switch (service.getServiceType()) {
                case ANALYSE:
                    analyseService = (SwiftAnalyseService) service;
                    break;
                case HISTORY:
                    historyService = (SwiftHistoryService) service;
                    break;
                case INDEXING:
                    indexingService = (SwiftIndexingService) service;
                    break;
                case REAL_TIME:
                    realTimeService = (SwiftRealtimeService) service;
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
                default:
            }
        }
    }


    @Override
    protected void initListener() {
        super.initListener();
        initRealTimeListener();
    }

    private void initRealTimeListener() {
        addListener(new RealTimeIndexingFinishListener() {
            @Override
            public void handle(SwiftServiceEvent<RealTimeIndexingStuff> event) {

            }
        });
    }


    @Override
    public void cleanMetaCache(String[] sourceKeys) {
        SwiftContext.getInstance().getBean(SwiftMetaDataService.class).cleanCache(sourceKeys);
    }
}
