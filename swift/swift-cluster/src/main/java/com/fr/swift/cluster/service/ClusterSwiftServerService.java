package com.fr.swift.cluster.service;

import com.fr.swift.SwiftContext;
import com.fr.swift.basic.URL;
import com.fr.swift.basics.ProxyFactory;
import com.fr.swift.basics.base.selector.ProxySelector;
import com.fr.swift.basics.base.selector.UrlSelector;
import com.fr.swift.cluster.ClusterEntity;
import com.fr.swift.config.bean.SwiftServiceInfoBean;
import com.fr.swift.config.service.SwiftServiceInfoService;
import com.fr.swift.event.SwiftEventDispatcher;
import com.fr.swift.event.SwiftEventListener;
import com.fr.swift.event.base.SwiftRpcEvent;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.property.SwiftProperty;
import com.fr.swift.service.AbstractSwiftService;
import com.fr.swift.service.AnalyseService;
import com.fr.swift.service.CollateService;
import com.fr.swift.service.DeleteService;
import com.fr.swift.service.HistoryService;
import com.fr.swift.service.IndexingService;
import com.fr.swift.service.RealtimeService;
import com.fr.swift.service.ServiceType;
import com.fr.swift.service.SwiftService;
import com.fr.swift.service.UploadService;
import com.fr.swift.service.listener.SwiftServiceEventHandler;
import com.fr.swift.service.listener.SwiftServiceListenerManager;
import com.fr.swift.source.DataSource;
import com.fr.swift.stuff.DefaultIndexingStuff;
import com.fr.swift.task.TaskKey;
import com.fr.swift.task.impl.SchedulerTaskPool;
import com.fr.swift.task.impl.TaskEvent;
import com.fr.swift.util.Assert;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author pony
 * @date 2017/11/14
 * 分布式的server服务，还要负责cube分块的均衡等
 */
public class ClusterSwiftServerService extends AbstractSwiftService implements SwiftServiceEventHandler {
    private static final long serialVersionUID = -611300229622871920L;

    //key: 机器address  value:service对象

    private Map<String, ClusterEntity> indexingServiceMap = new ConcurrentHashMap<String, ClusterEntity>();
    private Map<String, ClusterEntity> realTimeServiceMap = new ConcurrentHashMap<String, ClusterEntity>();
    private Map<String, ClusterEntity> historyServiceMap = new ConcurrentHashMap<String, ClusterEntity>();
    private Map<String, ClusterEntity> analyseServiceMap = new ConcurrentHashMap<String, ClusterEntity>();
    private Map<String, ClusterEntity> collateServiceMap = new ConcurrentHashMap<String, ClusterEntity>();
    private Map<String, ClusterEntity> uploadSegmentServices = new ConcurrentHashMap<String, ClusterEntity>();
    private Map<String, ClusterEntity> deleteSegmentServices = new ConcurrentHashMap<String, ClusterEntity>();

    private Map<String, ClusterEntity> indexingOfflineMap = new ConcurrentHashMap<String, ClusterEntity>();
    private Map<String, ClusterEntity> realTimeOfflineMap = new ConcurrentHashMap<String, ClusterEntity>();
    private Map<String, ClusterEntity> historyOfflineMap = new ConcurrentHashMap<String, ClusterEntity>();
    private Map<String, ClusterEntity> analyseOfflineMap = new ConcurrentHashMap<String, ClusterEntity>();

    private SwiftServiceInfoService serviceInfoService = SwiftContext.get().getBean(SwiftServiceInfoService.class);

    private SwiftProperty swiftProperty = SwiftProperty.getProperty();

    private ClusterSwiftServerService() {
    }

    public static ClusterSwiftServerService getInstance() {
        return SingletonHolder.instance;
    }

    private static Set<URL> getUrls(Map<String, ClusterEntity> analyseServiceMap) {
        HashSet<URL> urls = new HashSet<URL>();
        for (String clusterId : analyseServiceMap.keySet()) {
            urls.add(UrlSelector.getInstance().getFactory().getURL(clusterId));
        }
        return urls;
    }

    public void initService() {
        List<SwiftServiceInfoBean> swiftServiceInfoBeanList = serviceInfoService.getAllServiceInfo();
        for (SwiftServiceInfoBean swiftServiceInfoBean : swiftServiceInfoBeanList) {
            ServiceType serviceType = ServiceType.getServiceType(swiftServiceInfoBean.getService());
            if (serviceType != null) {
                URL url = UrlSelector.getInstance().getFactory().getURL(swiftServiceInfoBean.getClusterId());
                switch (serviceType) {
                    case HISTORY:
                        historyServiceMap.put(swiftServiceInfoBean.getClusterId(), new ClusterEntity(url, serviceType, HistoryService.class));
                        break;
                    case REAL_TIME:
                        realTimeServiceMap.put(swiftServiceInfoBean.getClusterId(), new ClusterEntity(url, serviceType, RealtimeService.class));
                        break;
                    case ANALYSE:
                        analyseServiceMap.put(swiftServiceInfoBean.getClusterId(), new ClusterEntity(url, serviceType, AnalyseService.class));
                        break;
                    case INDEXING:
                        indexingServiceMap.put(swiftServiceInfoBean.getClusterId(), new ClusterEntity(url, serviceType, IndexingService.class));
                        break;
                    case COLLATE:
                        collateServiceMap.put(swiftServiceInfoBean.getClusterId(), new ClusterEntity(url, serviceType, CollateService.class));
                        break;
                    case DELETE:
                        deleteSegmentServices.put(swiftServiceInfoBean.getClusterId(), new ClusterEntity(url, serviceType, DeleteService.class));
                        break;
                    case UPLOAD:
                        uploadSegmentServices.put(swiftServiceInfoBean.getClusterId(), new ClusterEntity(url, serviceType, UploadService.class));
                        break;
                    default:
                }
                swiftServiceInfoBean.setServiceInfo(swiftProperty.getServerAddress());
                serviceInfoService.saveOrUpdate(swiftServiceInfoBean);
            }
        }
    }

    public void offline(String address) {
        switchStatusPerMap(address, indexingServiceMap, indexingOfflineMap);
        switchStatusPerMap(address, realTimeServiceMap, realTimeOfflineMap);
        switchStatusPerMap(address, historyServiceMap, historyOfflineMap);
        switchStatusPerMap(address, analyseServiceMap, analyseOfflineMap);
    }

    public void online(String address) {
        switchStatusPerMap(address, indexingOfflineMap, indexingServiceMap);
        switchStatusPerMap(address, realTimeOfflineMap, realTimeServiceMap);
        switchStatusPerMap(address, historyOfflineMap, historyServiceMap);
        switchStatusPerMap(address, analyseOfflineMap, analyseServiceMap);
    }

    private void switchStatusPerMap(String address, Map<String, ClusterEntity> online, Map<String, ClusterEntity> offline) {
        ClusterEntity entity = online.remove(address);
        if (null != entity) {
            offline.put(address, entity);
        }
    }

    @Override
    public Set<URL> getNodeUrls(Class<?> proxyIface) {
        if (proxyIface == AnalyseService.class) {
            return getUrls(analyseServiceMap);
        }
        if (proxyIface == RealtimeService.class) {
            return getUrls(realTimeServiceMap);
        }
        if (proxyIface == HistoryService.class) {
            return getUrls(historyServiceMap);
        }
        if (proxyIface == IndexingService.class) {
            return getUrls(indexingServiceMap);
        }
        if (proxyIface == CollateService.class) {
            return getUrls(collateServiceMap);
        }
        if (proxyIface == DeleteService.class) {
            return getUrls(deleteSegmentServices);
        }
        if (proxyIface == UploadService.class) {
            return getUrls(uploadSegmentServices);
        }
        return Collections.emptySet();
    }

    @Override
    public boolean start() {
        SwiftServiceListenerManager.getInstance().registerHandler(this);
        initListener();
        return true;
    }

    @Override
    public ServiceType getServiceType() {
        return ServiceType.SERVER;
    }

    @Override
    public Serializable trigger(SwiftRpcEvent event) {
        return null;
    }

    @Override
    public void registerService(SwiftService service) {
        Assert.notNull(service.getId(), "Service's clusterId is null! Can't be registered!");

        SwiftLoggers.getLogger().info("{} register service :{}", service.getId(), service.getServiceType().name());
        synchronized (this) {
            serviceInfoService.saveOrUpdate(new SwiftServiceInfoBean(
                    service.getServiceType().name(), service.getId(), swiftProperty.getServerAddress(), false));

            URL url = UrlSelector.getInstance().getFactory().getURL(service.getId());
            switch (service.getServiceType()) {
                case ANALYSE:
                    ClusterEntity entity = new ClusterEntity(url, service.getServiceType(), AnalyseService.class);
                    analyseServiceMap.put(service.getId(), entity);
                    break;
                case HISTORY:
                    historyServiceMap.put(service.getId(), new ClusterEntity(url, service.getServiceType(), HistoryService.class));
                    break;
                case INDEXING:
                    indexingServiceMap.put(service.getId(), new ClusterEntity(url, service.getServiceType(), IndexingService.class));
                    break;
                case REAL_TIME:
                    realTimeServiceMap.put(service.getId(), new ClusterEntity(url, service.getServiceType(), RealtimeService.class));
                    break;
                case COLLATE:
                    collateServiceMap.put(service.getId(), new ClusterEntity(url, service.getServiceType(), CollateService.class));
                    break;
                case DELETE:
                    deleteSegmentServices.put(service.getId(), new ClusterEntity(url, service.getServiceType(), DeleteService.class));
                    break;
                case UPLOAD:
                    uploadSegmentServices.put(service.getId(), new ClusterEntity(url, service.getServiceType(), UploadService.class));
                    break;
                default:
            }
        }
    }

    public Map<String, ClusterEntity> getClusterEntityByService(ServiceType serviceType) {
        // 接口调用
        switch (serviceType) {
            case ANALYSE:
                return new HashMap<String, ClusterEntity>(analyseServiceMap);
            case HISTORY:
                return new HashMap<String, ClusterEntity>(historyServiceMap);
            case INDEXING:
                return new HashMap<String, ClusterEntity>(indexingServiceMap);
            case REAL_TIME:
                return new HashMap<String, ClusterEntity>(realTimeServiceMap);
            case COLLATE:
                return new HashMap<String, ClusterEntity>(collateServiceMap);
            case DELETE:
                return new HashMap<String, ClusterEntity>(deleteSegmentServices);
            case UPLOAD:
                return new HashMap<String, ClusterEntity>(uploadSegmentServices);
            default:
                return Collections.emptyMap();
        }
    }

    @Override
    public void unRegisterService(SwiftService service) {
        Assert.notNull(service.getId(), "Service's clusterId is null! Can't be removed!");

        SwiftLoggers.getLogger().debug("{} unregister service :{}", service.getId(), service.getServiceType().name());
        synchronized (this) {
            serviceInfoService.removeServiceInfo(new SwiftServiceInfoBean(service.getServiceType().name(), service.getId(), ""));
            switch (service.getServiceType()) {
                case ANALYSE:
                    analyseServiceMap.remove(service.getId());
                    break;
                case HISTORY:
                    historyServiceMap.remove(service.getId());
                    break;
                case INDEXING:
                    indexingServiceMap.remove(service.getId());
                    break;
                case REAL_TIME:
                    realTimeServiceMap.remove(service.getId());
                    break;
                case COLLATE:
                    collateServiceMap.remove(service.getId());
                    break;
                default:
            }
        }
    }

    protected void initListener() {
        SchedulerTaskPool.getInstance().initListener();
        SwiftEventDispatcher.listen(TaskEvent.RUN, new SwiftEventListener<Map<TaskKey, ?>>() {
            @Override
            public void on(Map<TaskKey, ?> taskKeyMap) {
                // rpc告诉indexing节点执行任务
                SwiftLoggers.getLogger().info("rpc告诉indexing节点执行任务");
                try {
                    ProxyFactory factory = ProxySelector.getInstance().getFactory();
                    IndexingService indexingService = factory.getProxy(IndexingService.class);
                    indexingService.index(new DefaultIndexingStuff((Map<TaskKey, DataSource>) taskKeyMap));
                } catch (Exception e) {
                    SwiftLoggers.getLogger().warn(e);
                }

            }
        });
        SwiftEventDispatcher.listen(TaskEvent.CANCEL, new SwiftEventListener<TaskKey>() {
            @Override
            public void on(TaskKey taskKey) {
                // rpc告诉indexing节点取消任务
                SwiftLoggers.getLogger().info("rpc告诉indexing节点取消任务");
            }
        });
    }

    private static class SingletonHolder {
        private static ClusterSwiftServerService instance = new ClusterSwiftServerService();
    }
}