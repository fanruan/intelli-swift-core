package com.fr.swift.service;

import com.fr.swift.URL;
import com.fr.swift.config.bean.SwiftServiceInfoBean;
import com.fr.swift.config.service.SwiftServiceInfoService;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.property.SwiftProperty;
import com.fr.swift.selector.UrlSelector;
import com.fr.swift.service.entity.ClusterEntity;
import com.fr.swift.util.Crasher;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by pony on 2017/11/14.
 * 分布式的server服务，还要负责cube分块的均衡等
 */
public class ClusterSwiftServerService extends AbstractSwiftServerService {

    //key: 机器address  value:service对象

    private Map<String, ClusterEntity> indexingServiceMap = new ConcurrentHashMap<String, ClusterEntity>();
    private Map<String, ClusterEntity> realTimeServiceMap = new ConcurrentHashMap<String, ClusterEntity>();
    private Map<String, ClusterEntity> historyServiceMap = new ConcurrentHashMap<String, ClusterEntity>();
    private Map<String, ClusterEntity> analyseServiceMap = new ConcurrentHashMap<String, ClusterEntity>();


//    private Map<String, SwiftIndexingService> indexingServiceMap = new HashMap<String, SwiftIndexingService>();
//    private Map<String, SwiftRealtimeService> realTimeServiceMap = new HashMap<String, SwiftRealtimeService>();
//    private Map<String, HistoryService> historyServiceMap = new HashMap<String, HistoryService>();
//    private Map<String, SwiftAnalyseService> analyseServiceMap = new HashMap<String, SwiftAnalyseService>();

    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(ClusterSwiftServerService.class);

    private SwiftServiceInfoService serviceInfoService = SwiftContext.getInstance().getBean(SwiftServiceInfoService.class);

    private ClusterSwiftServerService() {
    }

    public static ClusterSwiftServerService getInstance() {
        return SingletonHolder.instance;
    }

    private static class SingletonHolder {
        private static ClusterSwiftServerService instance = new ClusterSwiftServerService();
    }

    @Override
    public boolean start() {
        super.start();
        return true;
    }

    @Override
    public void registerService(SwiftService service) {

        SwiftProperty swiftProperty = SwiftContext.getInstance().getBean("swiftProperty", SwiftProperty.class);

        if (service.getID() == null) {
            Crasher.crash("Service's clusterId is null! Can't be registered!");
        }
        LOGGER.info(service.getID() + " register service :" + service.getServiceType().name());
        synchronized (this) {
            serviceInfoService.saveOrUpdateServiceInfo(new SwiftServiceInfoBean(
                    service.getServiceType().name(), service.getID(), swiftProperty.getRpcAddress(), false));

            URL url = UrlSelector.getInstance().getFactory().getURL(service.getID());
            switch (service.getServiceType()) {
                case ANALYSE:
                    analyseServiceMap.put(service.getID(), new ClusterEntity(url, service.getServiceType(), SwiftAnalyseService.class));
                    break;
                case HISTORY:
                    historyServiceMap.put(service.getID(), new ClusterEntity(url, service.getServiceType(), HistoryService.class));
                    break;
                case INDEXING:
                    indexingServiceMap.put(service.getID(), new ClusterEntity(url, service.getServiceType(), SwiftIndexingService.class));
                    break;
                case REAL_TIME:
                    realTimeServiceMap.put(service.getID(), new ClusterEntity(url, service.getServiceType(), SwiftRealtimeService.class));
            }
        }
    }

    //接口调用
    public Map<String, ClusterEntity> getClusterEntityByService(ServiceType serviceType) {
        switch (serviceType) {
            case ANALYSE:
                return new HashMap<String, ClusterEntity>(analyseServiceMap);
            case HISTORY:
                return new HashMap<String, ClusterEntity>(historyServiceMap);
            case INDEXING:
                return new HashMap<String, ClusterEntity>(indexingServiceMap);
            case REAL_TIME:
                return new HashMap<String, ClusterEntity>(realTimeServiceMap);
            default:
                return null;
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
