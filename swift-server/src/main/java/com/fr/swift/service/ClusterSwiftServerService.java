package com.fr.swift.service;

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
import com.fr.swift.context.SwiftContext;
import com.fr.swift.event.base.SwiftRpcEvent;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.netty.rpc.client.AsyncRpcCallback;
import com.fr.swift.netty.rpc.client.async.RpcFuture;
import com.fr.swift.netty.rpc.url.RPCDestination;
import com.fr.swift.netty.rpc.url.RPCUrl;
import com.fr.swift.property.SwiftProperty;
import com.fr.swift.service.entity.ClusterEntity;
import com.fr.swift.source.DataSource;
import com.fr.swift.stuff.HistoryIndexingStuff;
import com.fr.swift.stuff.IndexingStuff;
import com.fr.swift.task.TaskKey;
import com.fr.swift.task.impl.TaskEvent;
import com.fr.swift.util.Crasher;

import java.io.Serializable;
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
    private Map<String, ClusterEntity> collateServiceMap = new ConcurrentHashMap<String, ClusterEntity>();


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
    public boolean start() {
        super.start();
        return true;
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
        LOGGER.debug(service.getID() + " register service :" + service.getServiceType().name());
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
                    break;
                case COLLATE:
                    collateServiceMap.put(service.getID(), new ClusterEntity(url, service.getServiceType(), CollateService.class));
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
            default:
                return null;
        }
    }

    @Override
    public void unRegisterService(SwiftService service) {
        if (service.getID() == null) {
            Crasher.crash("Service's clusterId is null! Can't be removed!");
        }
        LOGGER.debug(service.getID() + " unregister service :" + service.getServiceType().name());
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
                case COLLATE:
                    collateServiceMap.remove(service.getID());
                    break;
                default:
            }
        }
    }

    @Override
    protected void initListener() {
        super.initListener();
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
                                    new Object[]{new HistoryIndexingStuff((Map<TaskKey, DataSource>) taskKeyMap)}));
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