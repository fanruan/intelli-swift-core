package com.fr.swift.service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pony on 2017/11/14.
 * 分布式的server服务，还要负责cube分块的均衡等
 */
public class ClusterSwiftServerService extends AbstractSwiftServerService {

    private List<SwiftIndexingService> indexingServiceList = new ArrayList<SwiftIndexingService>();
    private List<SwiftRealTimeService> realTimeServiceList = new ArrayList<SwiftRealTimeService>();
    private List<SwiftHistoryService> historyServiceList = new ArrayList<SwiftHistoryService>();
    private List<SwiftAnalyseService> analyseServiceList = new ArrayList<SwiftAnalyseService>();

    @Override
    public void registerService(SwiftService service) {
        synchronized (this){
            switch (service.getServiceType()){
                case ANALYSE:
                    analyseServiceList.add((SwiftAnalyseService) service);
                    break;
                case HISTORY:
                    historyServiceList.add((SwiftHistoryService) service);
                    break;
                case INDEXING:
                    indexingServiceList.add((SwiftIndexingService) service);
                    break;
                case REAL_TIME:
                    realTimeServiceList.add((SwiftRealTimeService) service);
            }
        }
    }

    @Override
    public void unRegisterService(SwiftService service) {
        synchronized (this){
            switch (service.getServiceType()){
                case ANALYSE:
                    analyseServiceList.remove(service);
                case HISTORY:
                    historyServiceList.remove(service);
                case INDEXING:
                    indexingServiceList.remove(service);
                case REAL_TIME:
                    realTimeServiceList.remove(service);
            }
        }
    }


    @Override
    protected void initListener(){
        super.initListener();
    }

}
