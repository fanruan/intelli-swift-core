package com.fr.swift.service;

import com.fr.swift.ProxyFactory;
import com.fr.swift.URL;
import com.fr.swift.config.bean.SwiftServiceInfoBean;
import com.fr.swift.config.service.SwiftServiceInfoService;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.property.SwiftProperty;
import com.fr.swift.rpc.url.RPCDestination;
import com.fr.swift.rpc.url.RPCUrl;
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
    private Map<String, SwiftRealtimeService> realTimeServiceMap = new HashMap<String, SwiftRealtimeService>();
    private Map<String, HistoryService> historyServiceMap = new HashMap<String, HistoryService>();
    private Map<String, SwiftAnalyseService> analyseServiceMap = new HashMap<String, SwiftAnalyseService>();

    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(ClusterSwiftServerService.class);

    private SwiftServiceInfoService serviceInfoService = SwiftContext.getInstance().getBean(SwiftServiceInfoService.class);

    @Override
    public boolean start() {
        super.start();
        List<SwiftServiceInfoBean> swiftServiceBeanList = serviceInfoService.getAllServiceInfo();
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
                        new SwiftRealtimeService(swiftServiceInfoBean.getClusterId()).start();
                }
            } catch (Exception e) {
                LOGGER.error(e);
            }
        }
        return true;
    }

    @Override
    public void registerService(SwiftService service) {

        SwiftProperty swiftProperty = (SwiftProperty) SwiftContext.getInstance().getRpcContext().getBean("swiftProperty");

        if (service.getID() == null) {
            Crasher.crash("Service's clusterId is null! Can't be registered!");
        }
        LOGGER.info(service.getID() + " register service :" + service.getServiceType().name());
        ProxyFactory proxyFactory = ProxySelector.getInstance().getFactory();
        synchronized (this) {
            serviceInfoService.saveOrUpdateServiceInfo(new SwiftServiceInfoBean(
                    service.getServiceType().name(), service.getID(), swiftProperty.getRpcAddress(), false));
            switch (service.getServiceType()) {
                case ANALYSE:
                    analyseServiceMap.put(service.getID(), (SwiftAnalyseService) service);
                    break;
                case HISTORY:
                    try {
//                        HistoryService historyServiceProxy = proxyFactory.getProxy((HistoryService) FRProxyCache.getInstance(HistoryService.class),
//                                HistoryService.class, new FRUrl(new FRDestination(service.getID())));
                        URL url = new RPCUrl(new RPCDestination(service.getID()));
                        HistoryService historyServiceProxy = proxyFactory.getProxy(null, HistoryService.class, url);
                        historyServiceMap.put(service.getID(), historyServiceProxy);
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
