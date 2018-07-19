package com.fr.swift.service;

import com.fineio.FineIO;
import com.fr.event.Event;
import com.fr.event.EventDispatcher;
import com.fr.event.Listener;
import com.fr.stable.StringUtils;
import com.fr.swift.Invoker;
import com.fr.swift.ProxyFactory;
import com.fr.swift.Result;
import com.fr.swift.URL;
import com.fr.swift.config.bean.SwiftServiceInfoBean;
import com.fr.swift.config.entity.SwiftTablePathEntity;
import com.fr.swift.config.service.SwiftMetaDataService;
import com.fr.swift.config.service.SwiftPathService;
import com.fr.swift.config.service.SwiftSegmentServiceProvider;
import com.fr.swift.config.service.SwiftServiceInfoService;
import com.fr.swift.config.service.SwiftTablePathService;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.core.rpc.SwiftClusterService;
import com.fr.swift.event.global.TaskDoneRpcEvent;
import com.fr.swift.event.history.HistoryCommonLoadRpcEvent;
import com.fr.swift.event.history.HistoryLoadSegmentRpcEvent;
import com.fr.swift.exception.SwiftServiceException;
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
import com.fr.swift.segment.relation.RelationIndexImpl;
import com.fr.swift.selector.ProxySelector;
import com.fr.swift.selector.UrlSelector;
import com.fr.swift.service.listener.SwiftServiceListenerHandler;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.RelationSource;
import com.fr.swift.source.RelationSourceType;
import com.fr.swift.source.Source;
import com.fr.swift.source.SourceKey;
import com.fr.swift.structure.Pair;
import com.fr.swift.stuff.IndexingStuff;
import com.fr.swift.task.TaskKey;
import com.fr.swift.task.TaskResult;
import com.fr.swift.task.cube.CubeTaskGenerator;
import com.fr.swift.task.cube.CubeTaskManager;
import com.fr.swift.task.impl.TaskEvent;
import com.fr.swift.task.impl.WorkerTaskPool;
import com.fr.swift.task.service.ServiceTaskExecutor;
import com.fr.swift.util.FileUtil;
import com.fr.swift.util.Strings;
import com.fr.third.springframework.beans.factory.annotation.Autowired;
import com.fr.third.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.fr.swift.task.TaskResult.Type.SUCCEEDED;

/**
 * @author pony
 * @date 2017/10/10
 */
@Service("indexingService")
@RpcService(type = RpcServiceType.CLIENT_SERVICE, value = IndexingService.class)
public class SwiftIndexingService extends AbstractSwiftService implements IndexingService {
    private static final long serialVersionUID = -7430843337225891194L;

    @Autowired
    private transient RpcServer server;
    @Autowired
    private transient SwiftPathService pathService;
    @Autowired
    private transient SwiftTablePathService tablePathService;

    private static Map<TaskKey, Object> stuffObject = new ConcurrentHashMap<TaskKey, Object>();

    private SwiftIndexingService() {
    }

    @Autowired
    private transient ServiceTaskExecutor taskExecutor;

    public SwiftIndexingService(String id) {
        super(id);
    }

    @Override
    public String getID() {
        return StringUtils.isEmpty(super.getID()) ? SwiftContext.get().getBean(SwiftProperty.class).getRpcAddress() : super.getID();
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
        SwiftContext.get().getBean(SwiftMetaDataService.class).cleanCache(sourceKeys);
    }

    @Override
    public ServiceType getServiceType() {
        return ServiceType.INDEXING;
    }

    @Override
    @RpcMethod(methodName = "index")
    public void index(IndexingStuff stuff) {
        SwiftLoggers.getLogger().info("indexing stuff");
        appendStuffMap(stuff);
        triggerIndexing(stuff);
    }

    private void appendStuffMap(IndexingStuff stuff) {
        appendStuffMap(stuff.getTables());
        appendStuffMap(stuff.getRelations());
        appendStuffMap(stuff.getRelationPaths());
    }

    private void appendStuffMap(Map<TaskKey, ? extends Source> map) {
        if (null != map) {
            for (Map.Entry<TaskKey, ? extends Source> entry : map.entrySet()) {
                if (null != entry.getValue()) {
                    stuffObject.put(entry.getKey(), entry.getValue());
                }
            }
        }
    }

    private void triggerIndexing(IndexingStuff stuff) {
        EventDispatcher.fire(TaskEvent.LOCAL_RUN, stuff.getTables());
        EventDispatcher.fire(TaskEvent.LOCAL_RUN, stuff.getRelations());
        EventDispatcher.fire(TaskEvent.LOCAL_RUN, stuff.getRelationPaths());
    }

    @Override
    public ServerCurrentStatus currentStatus() {
        return new ServerCurrentStatus(getID());
    }

    private URL getMasterURL() {
        List<SwiftServiceInfoBean> swiftServiceInfoBeans = SwiftContext.get().getBean(SwiftServiceInfoService.class).getServiceInfoByService(SwiftClusterService.SERVICE);
        SwiftServiceInfoBean swiftServiceInfoBean = swiftServiceInfoBeans.get(0);
        return UrlSelector.getInstance().getFactory().getURL(swiftServiceInfoBean.getServiceInfo());
    }

    private void initListener() {
        EventDispatcher.listen(TaskEvent.LOCAL_DONE, new Listener<Pair<TaskKey, TaskResult>>() {
            @Override
            public void on(Event event, final Pair<TaskKey, TaskResult> result) {
                SwiftLoggers.getLogger().info("rpc通知server任务完成");
                try {
                    runRpc(new TaskDoneRpcEvent(result));
                    FineIO.doWhenFinished(new UploadRunnable(result));
                } catch (Exception e) {
                    SwiftLoggers.getLogger().error(e);
                }
            }
        });

        WorkerTaskPool.getInstance().initListener();
        WorkerTaskPool.getInstance().setTaskGenerator(new CubeTaskGenerator());

        CubeTaskManager.getInstance().initListener();
    }

    private RpcFuture runRpc(Object... args) throws Exception {
        URL masterURL = getMasterURL();
        ProxyFactory factory = ProxySelector.getInstance().getFactory();
        Invoker invoker = factory.getInvoker(null, SwiftServiceListenerHandler.class, masterURL, false);
        Result invokeResult = invoker.invoke(new SwiftInvocation(server.getMethodByName("rpcTrigger"), args));
        RpcFuture future = (RpcFuture) invokeResult.getValue();
        if (null != future) {
            return future;
        }
        throw new Exception(invokeResult.getException());
    }

    private class UploadRunnable implements Runnable {
        private Pair<TaskKey, TaskResult> result;

        public UploadRunnable(Pair<TaskKey, TaskResult> result) {
            this.result = result;
        }

        private void uploadTable(final DataSource dataSource) throws Exception {
            final SourceKey sourceKey = dataSource.getSourceKey();
            SwiftTablePathEntity entity = SwiftContext.get().getBean(SwiftTablePathService.class).get(sourceKey.getId());
            Integer path = entity.getTablePath();
            Integer tmpPath = entity.getTmpDir();
            entity.setTablePath(tmpPath);
            entity.setLastPath(path);
            if (tablePathService.saveOrUpdate(entity)) {
                String deletePath = String.format("%s/%s/%d/%s",
                        pathService.getSwiftPath(),
                        dataSource.getMetadata().getSwiftSchema().getDir(),
                        path,
                        sourceKey.getId());
                FileUtil.delete(deletePath);
                new File(deletePath).getParentFile().delete();
            }
            List<SegmentKey> segmentKeys = SwiftSegmentServiceProvider.getProvider().getSegmentByKey(sourceKey.getId());
            if (null != segmentKeys) {
                for (SegmentKey segmentKey : segmentKeys) {
                    try {
                        SwiftRepositoryManager.getManager().currentRepo().copyToRemote(segmentKey.getAbsoluteUri(), segmentKey.getUri());
                    } catch (IOException e) {
                        logger.error("upload error! ", e);
                    }
                }

                runRpc(new HistoryLoadSegmentRpcEvent(sourceKey.getId()))
                        .addCallback(new AsyncRpcCallback() {
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
        }

        private void uploadRelation(RelationSource relation) throws Exception {
            SourceKey sourceKey = relation.getForeignSource();
            SourceKey primary = relation.getPrimarySource();
            List<URI> needUpload = new ArrayList<URI>();
            List<SegmentKey> segmentKeys = SwiftSegmentServiceProvider.getProvider().getSegmentByKey(sourceKey.getId());
            if (null != segmentKeys) {
                if (relation.getRelationType() != RelationSourceType.FIELD_RELATION) {
                    for (SegmentKey segmentKey : segmentKeys) {
                        try {
                            URI src = URI.create(String.format("%s/%s/%s", Strings.trimSeparator(segmentKey.getAbsoluteUri().getPath() + "/", "/"), RelationIndexImpl.RELATIONS_KEY, primary.getId()));
                            URI dest = URI.create(String.format("%s/%s/%s", Strings.trimSeparator(segmentKey.getUri().getPath() + "/", "/"), RelationIndexImpl.RELATIONS_KEY, primary.getId()));
                            SwiftRepositoryManager.getManager().currentRepo().copyToRemote(src, dest);
                            needUpload.add(dest);
                        } catch (IOException e) {
                            logger.error("upload error! ", e);
                        }
                    }
                } else {
                    for (SegmentKey segmentKey : segmentKeys) {
                        try {
                            URI src = URI.create(String.format("%s/%s%s/%s", Strings.trimSeparator(segmentKey.getAbsoluteUri().getPath() + "/", "/"), "field", RelationIndexImpl.RELATIONS_KEY, primary.getId()));
                            URI dest = URI.create(String.format("%s/%s/%s/%s", Strings.trimSeparator(segmentKey.getUri().getPath() + "/", "/"), "field", RelationIndexImpl.RELATIONS_KEY, primary.getId()));
                            SwiftRepositoryManager.getManager().currentRepo().copyToRemote(src, dest);
                            needUpload.add(dest);
                        } catch (IOException e) {
                            logger.error("upload error! ", e);
                        }
                    }
                }

                runRpc(new HistoryCommonLoadRpcEvent(Pair.of(sourceKey.getId(), needUpload)))
                        .addCallback(new AsyncRpcCallback() {
                            @Override
                            public void success(Object result) {
                                logger.info("rpcTrigger success");
                            }

                            @Override
                            public void fail(Exception e) {
                                logger.error("rpcTrigger error", e);
                            }
                        });
            }
        }

        @Override
        public void run() {
            if (result.getValue().getType() == SUCCEEDED) {
                TaskKey key = result.getKey();
                Object obj = stuffObject.get(key);
                try {
                    if (null != obj) {
                        if (obj instanceof DataSource) {
                            uploadTable((DataSource) obj);
                        } else if (obj instanceof RelationSource) {
                            uploadRelation((RelationSource) obj);
                        }
                        stuffObject.remove(key);
                    }

                } catch (Exception e) {
                    logger.error(e);
                }
            }
        }
    }
}