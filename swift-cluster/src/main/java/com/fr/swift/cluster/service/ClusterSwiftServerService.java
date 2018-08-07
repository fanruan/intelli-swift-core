package com.fr.swift.cluster.service;

import com.fr.event.Event;
import com.fr.event.EventDispatcher;
import com.fr.event.Listener;
import com.fr.swift.basics.Invoker;
import com.fr.swift.basics.ProxyFactory;
import com.fr.swift.basics.Result;
import com.fr.swift.basics.URL;
import com.fr.swift.basics.base.SwiftInvocation;
import com.fr.swift.basics.base.selector.ProxySelector;
import com.fr.swift.basics.base.selector.UrlSelector;
import com.fr.swift.config.bean.IndexingSelectRule;
import com.fr.swift.config.bean.SwiftServiceInfoBean;
import com.fr.swift.config.service.IndexingSelectRuleService;
import com.fr.swift.config.service.SwiftServiceInfoService;
import com.fr.swift.container.NodeContainer;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.event.base.SwiftRpcEvent;
import com.fr.swift.exception.SwiftServiceException;
import com.fr.swift.heart.HeartBeatInfo;
import com.fr.swift.heart.NodeState;
import com.fr.swift.heart.NodeType;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.netty.rpc.client.AsyncRpcCallback;
import com.fr.swift.netty.rpc.client.async.RpcFuture;
import com.fr.swift.netty.rpc.url.RPCDestination;
import com.fr.swift.netty.rpc.url.RPCUrl;
import com.fr.swift.property.SwiftProperty;
import com.fr.swift.service.AbstractSwiftService;
import com.fr.swift.service.AnalyseService;
import com.fr.swift.service.HistoryService;
import com.fr.swift.service.IndexingService;
import com.fr.swift.service.RealtimeService;
import com.fr.swift.service.ServiceType;
import com.fr.swift.service.SwiftService;
import com.fr.swift.cluster.entity.ClusterEntity;
import com.fr.swift.service.SwiftServiceEvent;
import com.fr.swift.service.listener.SwiftServiceListener;
import com.fr.swift.service.listener.SwiftServiceListenerHandler;
import com.fr.swift.service.listener.SwiftServiceListenerManager;
import com.fr.swift.source.DataSource;
import com.fr.swift.stuff.DefaultIndexingStuff;
import com.fr.swift.stuff.IndexingStuff;
import com.fr.swift.task.TaskKey;
import com.fr.swift.task.impl.SchedulerTaskPool;
import com.fr.swift.task.impl.TaskEvent;
import com.fr.swift.util.Crasher;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author pony
 * @date 2017/11/14
 * 分布式的server服务，还要负责cube分块的均衡等
 */
public class ClusterSwiftServerService  extends AbstractSwiftService implements SwiftServiceListenerHandler {
    private static final long serialVersionUID = -611300229622871920L;

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

    private SwiftServiceInfoService serviceInfoService = SwiftContext.get().getBean(SwiftServiceInfoService.class);

    private ClusterSwiftServerService() {
    }

    public static ClusterSwiftServerService getInstance() {
        return SingletonHolder.instance;
    }

    private static class SingletonHolder {
        private static ClusterSwiftServerService instance = new ClusterSwiftServerService();
    }

    @Override
    @PostConstruct
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
    public void addListener(SwiftServiceListener listener) {

    }

    @Override
    public void trigger(SwiftServiceEvent event) {

    }

    @Override
    public Serializable trigger(SwiftRpcEvent event) {
        return null;
    }

    @Override
    public void registerService(SwiftService service) {
        SwiftProperty swiftProperty = SwiftContext.get().getBean("swiftProperty", SwiftProperty.class);

        if (service.getID() == null) {
            Crasher.crash("Service's clusterId is null! Can't be registered!");
        }
        LOGGER.info(service.getID() + " register service :" + service.getServiceType().name());
        synchronized (this) {
            serviceInfoService.saveOrUpdate(new SwiftServiceInfoBean(
                    service.getServiceType().name(), service.getID(), swiftProperty.getServerAddress(), false));

            URL url = UrlSelector.getInstance().getFactory().getURL(service.getID());
            switch (service.getServiceType()) {
                case ANALYSE:
                    analyseServiceMap.put(service.getID(), new ClusterEntity(url, service.getServiceType(), AnalyseService.class));
                    break;
                case HISTORY:
                    historyServiceMap.put(service.getID(), new ClusterEntity(url, service.getServiceType(), HistoryService.class));
                    break;
                case INDEXING:
                    indexingServiceMap.put(service.getID(), new ClusterEntity(url, service.getServiceType(), IndexingService.class));
                    break;
                case REAL_TIME:
                    realTimeServiceMap.put(service.getID(), new ClusterEntity(url, service.getServiceType(), RealtimeService.class));
                default:
            }
        }
    }

    public Map<String, ClusterEntity> getClusterEntityByService(ServiceType serviceType) {
        // 接口调用
        switch (serviceType) {
            case ANALYSE:
                return getOnlineClusterEntityService(analyseServiceMap);
            case HISTORY:
                return getOnlineClusterEntityService(historyServiceMap);
            case INDEXING:
                return getOnlineClusterEntityService(indexingServiceMap);
            case REAL_TIME:
                return getOnlineClusterEntityService(realTimeServiceMap);
            default:
                return null;
        }
    }

    private Map<String, ClusterEntity> getOnlineClusterEntityService(Map<String, ClusterEntity> map) {
        List<NodeState> nodeStates = NodeContainer.getAllNodeStates();
        Map<String, ClusterEntity> result = new HashMap<String, ClusterEntity>();
        for (NodeState nodeState : nodeStates) {
            HeartBeatInfo info = nodeState.getHeartBeatInfo();
            if (map.containsKey(info.getAddress())) {
                if (nodeState.getNodeType() != NodeType.OFFLINE) {
                    result.put(info.getAddress(), map.get(info.getAddress()));
                }
            }
        }
        return result;
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
                    break;
                case HISTORY:
                    historyServiceMap.remove(service.getID());
                    break;
                case INDEXING:
                    indexingServiceMap.remove(service.getID());
                    break;
                case REAL_TIME:
                    realTimeServiceMap.remove(service.getID());
                    break;
                default:
            }
        }
    }

    protected void initListener() {
        SchedulerTaskPool.getInstance().initListener();
        EventDispatcher.listen(TaskEvent.RUN, new Listener<Map<TaskKey, ?>>() {
            @Override
            public void on(Event event, Map<TaskKey, ?> taskKeyMap) {
                // rpc告诉indexing节点执行任务
                SwiftLoggers.getLogger().info("rpc告诉indexing节点执行任务");
                String address = null;
                try {
                    IndexingSelectRule rule = SwiftContext.get().getBean(IndexingSelectRuleService.class).getCurrentRule();
                    address = rule.select(indexingServiceMap.keySet());
                    ClusterEntity entity = indexingServiceMap.get(address);
                    ProxyFactory factory = ProxySelector.getInstance().getFactory();
                    Invoker invoker = factory.getInvoker(null, entity.getServiceClass(), new RPCUrl(new RPCDestination(address)), false);

                    Result result = invoker.invoke(
                            new SwiftInvocation(entity.getServiceClass().getDeclaredMethod("index", IndexingStuff.class),
                                    new Object[]{new DefaultIndexingStuff((Map<TaskKey, DataSource>) taskKeyMap)}));
                    RpcFuture future = (RpcFuture) result.getValue();
                    if (null == future) {
                        throw new Exception(result.getException());
                    }
                    future.addCallback(new AsyncRpcCallback() {
                        @Override
                        public void success(Object result) {
//                                    LOGGER.info(String.format("Indexing success! Cost: %d", System.currentTimeMillis() - start));
                        }

                        @Override
                        public void fail(Exception e) {
                            LOGGER.error("Indexing error! ", e);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
        EventDispatcher.listen(TaskEvent.CANCEL, new Listener<TaskKey>() {
            @Override
            public void on(Event event, TaskKey taskKey) {
                // rpc告诉indexing节点取消任务
                SwiftLoggers.getLogger().info("rpc告诉indexing节点取消任务");
            }
        });
    }
}