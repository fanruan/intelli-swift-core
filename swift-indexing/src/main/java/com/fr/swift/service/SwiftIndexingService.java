package com.fr.swift.service;

import com.fr.event.Event;
import com.fr.event.EventDispatcher;
import com.fr.event.Listener;
import com.fr.stable.StringUtils;
import com.fr.swift.Invoker;
import com.fr.swift.ProxyFactory;
import com.fr.swift.Result;
import com.fr.swift.URL;
import com.fr.swift.config.bean.SwiftServiceInfoBean;
import com.fr.swift.config.service.SwiftMetaDataService;
import com.fr.swift.config.service.SwiftSegmentServiceProvider;
import com.fr.swift.config.service.SwiftServiceInfoService;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.cube.task.TaskKey;
import com.fr.swift.cube.task.TaskResult;
import com.fr.swift.cube.task.impl.TaskEvent;
import com.fr.swift.event.history.HistoryLoadRpcEvent;
import com.fr.swift.exception.SwiftServiceException;
import com.fr.swift.frrpc.SwiftClusterService;
import com.fr.swift.info.ServerCurrentStatus;
import com.fr.swift.invocation.SwiftInvocation;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.property.SwiftProperty;
import com.fr.swift.repository.SwiftRepositoryManager;
import com.fr.swift.rpc.annotation.RpcMethod;
import com.fr.swift.rpc.annotation.RpcService;
import com.fr.swift.rpc.annotation.RpcServiceType;
import com.fr.swift.rpc.client.AsyncRpcCallback;
import com.fr.swift.rpc.client.async.RpcFuture;
import com.fr.swift.rpc.server.RpcServer;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.selector.ProxySelector;
import com.fr.swift.selector.UrlSelector;
import com.fr.swift.service.listener.SwiftServiceListenerHandler;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.SourceKey;
import com.fr.swift.structure.Pair;
import com.fr.swift.stuff.IndexingStuff;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author pony
 * @date 2017/10/10
 */
@RpcService(type = RpcServiceType.CLIENT_SERVICE, value = IndexingService.class)
public class SwiftIndexingService extends AbstractSwiftService implements IndexingService {
    private static final long serialVersionUID = -7430843337225891194L;
    private transient RpcServer server = SwiftContext.getInstance().getBean(RpcServer.class);

    private SwiftIndexingService() {
    }

    public static SwiftIndexingService getInstance() {
        return SingletonHolder.service;
    }

    public SwiftIndexingService(String id) {
        super(id);
    }

    @Override
    public String getID() {
        return StringUtils.isEmpty(super.getID()) ? SwiftContext.getInstance().getBean(SwiftProperty.class).getRpcAddress() : super.getID();
    }

    @Override
    public boolean start() throws SwiftServiceException {
        super.start();
        initListener();
        return true;
    }

    @Override
    @RpcMethod(methodName = "cleanMetaCache")
    public void cleanMetaCache(String[] sourceKeys) {
        SwiftContext.getInstance().getBean(SwiftMetaDataService.class).cleanCache(sourceKeys);
    }

    @Override
    public ServiceType getServiceType() {
        return ServiceType.INDEXING;
    }

    @Override
    @RpcMethod(methodName = "index")
    public void index(IndexingStuff stuff) {
        SwiftLoggers.getLogger().info("indexing stuff");

        // TODO 更新调用
        triggerIndexing(stuff);

//        doAfterIndexing(stuff);
    }

    private void doAfterIndexing(IndexingStuff stuff) {
        Map<TaskKey, DataSource> tables = stuff.getTables();
        Iterator<Map.Entry<TaskKey, DataSource>> iterator = tables.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<TaskKey, DataSource> entry = iterator.next();
            SourceKey sourceKey = entry.getValue().getSourceKey();
            List<SegmentKey> segmentKeys = SwiftSegmentServiceProvider.getProvider().getSegmentByKey(sourceKey.getId());
            for (SegmentKey segmentKey : segmentKeys) {
                try {
                    SwiftRepositoryManager.getManager().getCurrentRepository().copyToRemote(segmentKey.getAbsoluteUri(), segmentKey.getUri());
                } catch (IOException e) {
                    logger.error("upload error! ", e);
                }
            }
        }
        URL masterURL = getMasterURL();
        ProxyFactory factory = ProxySelector.getInstance().getFactory();
        Invoker invoker = factory.getInvoker(null, SwiftServiceListenerHandler.class, masterURL, false);
        Result result = invoker.invoke(new SwiftInvocation(server.getMethodByName("rpcTrigger"), new Object[]{new HistoryLoadRpcEvent()}));
        RpcFuture future = (RpcFuture) result.getValue();
        future.addCallback(new AsyncRpcCallback() {
            @Override
            public void success(Object result) {
                logger.info("rpcTrigger success! ");
            }

            @Override
            public void fail(Exception e) {
                logger.error("rpcTrigger error! ", e);
            }
        });
    }

    private void triggerIndexing(IndexingStuff stuff) {
        EventDispatcher.fire(TaskEvent.RUN, stuff.getTables());
        EventDispatcher.fire(TaskEvent.RUN, stuff.getRelations());
        EventDispatcher.fire(TaskEvent.RUN, stuff.getRelationPaths());
    }

    @Override
    public ServerCurrentStatus currentStatus() {
        return new ServerCurrentStatus(getID());
    }

    private static class SingletonHolder {
        private static SwiftIndexingService service = new SwiftIndexingService();
    }

    private URL getMasterURL() {
        List<SwiftServiceInfoBean> swiftServiceInfoBeans = SwiftContext.getInstance().getBean(SwiftServiceInfoService.class).getServiceInfoByService(SwiftClusterService.SERVICE);
        SwiftServiceInfoBean swiftServiceInfoBean = swiftServiceInfoBeans.get(0);
        return UrlSelector.getInstance().getFactory().getURL(swiftServiceInfoBean.getServiceInfo());
    }

    private void initListener() {
        EventDispatcher.listen(TaskEvent.DONE, new Listener<Pair<TaskKey, TaskResult>>() {
            @Override
            public void on(Event event, Pair<TaskKey, TaskResult> result) {
                // rpc通知server任务完成
                SwiftLoggers.getLogger().info("rpc通知server任务完成");
            }
        });
    }
}