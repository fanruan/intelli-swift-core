package com.fr.swift.service;

import com.fr.swift.event.base.SwiftRpcEvent;

import java.io.Serializable;

/**
 *
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
    }
}
