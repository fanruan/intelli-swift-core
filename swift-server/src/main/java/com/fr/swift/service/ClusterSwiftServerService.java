package com.fr.swift.service;

import com.fr.swift.config.bean.SwiftServiceInfoBean;
import com.fr.swift.config.service.SwiftServiceInfoService;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.property.SwiftProperty;
import com.fr.swift.util.Crasher;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by pony on 2017/11/14.
 * 分布式的server服务，还要负责cube分块的均衡等
 */
public class ClusterSwiftServerService extends AbstractSwiftServerService {

    //key: 机器address  value:service对象
    private Map<String, SwiftIndexingService> indexingServiceMap = new HashMap<String, SwiftIndexingService>();
    private Map<String, SwiftRealtimeService> realTimeServiceMap = new HashMap<String, SwiftRealtimeService>();
    private Map<String, HistoryService> historyServiceMap = new HashMap<String, HistoryService>();
    private Map<String, SwiftAnalyseService> analyseServiceMap = new HashMap<String, SwiftAnalyseService>();

    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(ClusterSwiftServerService.class);

    private SwiftServiceInfoService serviceInfoService = SwiftContext.getInstance().getBean(SwiftServiceInfoService.class);

    @Override
    public boolean start() {
        super.start();
        return true;
    }

    @Override
    public void registerService(SwiftService service) {
        SwiftProperty swiftProperty = (SwiftProperty) SwiftContext.getInstance().getRpcContext().getBean("swiftProperty");

        if (service.getID() == null) {
            Crasher.crash("Service's clusterId is null! Can't be registered!");
        }
        LOGGER.info(service.getID() + " register service :" + service.getServiceType().name());
        synchronized (this) {
            serviceInfoService.saveOrUpdateServiceInfo(new SwiftServiceInfoBean(
                    service.getServiceType().name(), service.getID(), swiftProperty.getRpcAddress(), false));
            switch (service.getServiceType()) {
                case ANALYSE:
                    analyseServiceMap.put(service.getID(), (SwiftAnalyseService) service);
                    break;
                case HISTORY:
                    try {
                        historyServiceMap.put(service.getID(), (HistoryService) service);
                    } catch (Exception e) {
                        historyServiceMap.put(service.getID(), (HistoryService) service);
                    }
                    break;
                case INDEXING:
                    indexingServiceMap.put(service.getID(), (SwiftIndexingService) service);
                    break;
                case REAL_TIME:
                    realTimeServiceMap.put(service.getID(), (SwiftRealtimeService) service);
            }
        }
    }

    @Override
    public void unRegisterService(SwiftService service) {
        if (service.getID() == null) {
            Crasher.crash("Service's clusterId is null! Can't be removed!");
        }
        LOGGER.info(service.getID() + " unregister service :" + service.getServiceType().name());
        synchronized (this) {
            serviceInfoService.removeServiceInfo(new SwiftServiceInfoBean(service.getServiceType().name(), service.getID(), ""));
            switch (service.getServiceType()) {
                case ANALYSE:
                    analyseServiceMap.remove(service.getID());
                case HISTORY:
                    historyServiceMap.remove(service.getID());
                case INDEXING:
                    indexingServiceMap.remove(service.getID());
                case REAL_TIME:
                    realTimeServiceMap.remove(service.getID());
            }
        }
    }

    @Override
    protected void initListener() {
        super.initListener();
    }

}
