package com.fr.swift.service;

import com.fr.swift.service.listener.RealTimeIndexingFinishListener;
import com.fr.swift.stuff.RealTimeIndexingStuff;

/**
 * Created by pony on 2017/11/14.
 * 本地swift服务
 */
public class LocalSwiftServerService extends AbstractSwiftServerService {

    private SwiftIndexingService indexingService;
    private SwiftRealTimeService realTimeService;
    private SwiftHistoryService historyService;
    private SwiftAnalyseService analyseService;

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
                    realTimeService = (SwiftRealTimeService) service;
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


}
