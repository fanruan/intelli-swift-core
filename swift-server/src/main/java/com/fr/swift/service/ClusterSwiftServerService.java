package com.fr.swift.service;

import com.fr.swift.ProxyFactory;
import com.fr.swift.config.bean.SwiftServiceInfoBean;
import com.fr.swift.config.service.SwiftConfigServiceProvider;
import com.fr.swift.frrpc.FRDestination;
import com.fr.swift.frrpc.FRProxyCache;
import com.fr.swift.frrpc.FRUrl;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.selector.ProxySelector;
import com.fr.swift.util.Crasher;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by pony on 2017/11/14.
 * 分布式的server服务，还要负责cube分块的均衡等
 */
public class ClusterSwiftServerService extends AbstractSwiftServerService {

    private Map<String, SwiftIndexingService> indexingServiceMap = new HashMap<String, SwiftIndexingService>();
    private Map<String, SwiftRealTimeService> realTimeServiceMap = new HashMap<String, SwiftRealTimeService>();
    private Map<String, HistoryService> historyServiceMap = new HashMap<String, HistoryService>();
    private Map<String, SwiftAnalyseService> analyseServiceMap = new HashMap<String, SwiftAnalyseService>();

    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(ClusterSwiftServerService.class);

    @Override
    public boolean start() {
        super.start();
        List<SwiftServiceInfoBean> swiftServiceBeanList = SwiftConfigServiceProvider.getInstance().getAllServiceInfo();
        for (SwiftServiceInfoBean swiftServiceInfoBean : swiftServiceBeanList) {
            try {
                String service = swiftServiceInfoBean.getService();
                switch (ServiceType.valueOf(service)) {
                    case ANALYSE:
                        new SwiftAnalyseService(swiftServiceInfoBean.getClusterId()).start();
                        break;
                    case HISTORY:
                        SwiftHistoryService.getInstance().setId(swiftServiceInfoBean.getClusterId());
                        SwiftHistoryService.getInstance().start();
                        break;
                    case INDEXING:
                        new SwiftIndexingService(swiftServiceInfoBean.getClusterId()).start();
                        break;
                    case REAL_TIME:
                        new SwiftRealTimeService(swiftServiceInfoBean.getClusterId()).start();
                }
            } catch (Exception e) {
                LOGGER.error(e);
            }
        }
        return true;
    }

    @Override
    public void registerService(SwiftService service) {
        if (service.getID() == null) {
            Crasher.crash("Service's clusterId is null! Can't be registered!");
        }
        LOGGER.info(service.getID() + " register service :" + service.getServiceType().name());
        ProxyFactory proxyFactory = ProxySelector.getInstance().getFactory();
        synchronized (this) {
            SwiftConfigServiceProvider.getInstance().saveOrUpdateServiceInfo(new SwiftServiceInfoBean(
                    service.getServiceType().name(), service.getID(), "", false));
            switch (service.getServiceType()) {
                case ANALYSE:
                    analyseServiceMap.put(service.getID(), (SwiftAnalyseService) service);
                    break;
                case HISTORY:
                    try {
                        HistoryService historyServiceProxy = proxyFactory.getProxy((HistoryService) FRProxyCache.getInstance(HistoryService.class),
                                HistoryService.class, new FRUrl(new FRDestination(service.getID())));
                        historyServiceMap.put(service.getID(), historyServiceProxy);
                    } catch (Exception e) {
                        historyServiceMap.put(service.getID(), (HistoryService) service);
                    }
                    break;
                case INDEXING:
                    indexingServiceMap.put(service.getID(), (SwiftIndexingService) service);
                    break;
                case REAL_TIME:
                    realTimeServiceMap.put(service.getID(), (SwiftRealTimeService) service);
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
            SwiftConfigServiceProvider.getInstance().removeServiceInfo(new SwiftServiceInfoBean(service.getServiceType().name(), service.getID(), ""));
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
