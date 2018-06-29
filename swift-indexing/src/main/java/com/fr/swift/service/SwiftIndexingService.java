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
import com.fr.swift.cube.task.WorkerTask;
import com.fr.swift.cube.task.impl.CubeTaskManager;
import com.fr.swift.cube.task.impl.Operation;
import com.fr.swift.cube.task.impl.TaskEvent;
import com.fr.swift.cube.task.impl.WorkerTaskImpl;
import com.fr.swift.cube.task.impl.WorkerTaskPool;
import com.fr.swift.event.history.HistoryLoadRelationRpcEvent;
import com.fr.swift.event.history.HistoryLoadRpcEvent;
import com.fr.swift.exception.SwiftServiceException;
import com.fr.swift.frrpc.SwiftClusterService;
import com.fr.swift.generate.history.TableBuilder;
import com.fr.swift.generate.history.index.FieldPathIndexer;
import com.fr.swift.generate.history.index.MultiRelationIndexer;
import com.fr.swift.generate.history.index.TablePathIndexer;
import com.fr.swift.info.ServerCurrentStatus;
import com.fr.swift.invocation.SwiftInvocation;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.manager.LocalSegmentProvider;
import com.fr.swift.property.SwiftProperty;
import com.fr.swift.relation.utils.RelationPathHelper;
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
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.relation.FieldRelationSource;
import com.fr.swift.structure.Pair;
import com.fr.swift.stuff.IndexingStuff;
import com.fr.swift.util.Strings;
import com.fr.swift.util.function.Function2;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author pony
 * @date 2017/10/10
 */
@RpcService(type = RpcServiceType.CLIENT_SERVICE, value = IndexingService.class)
public class SwiftIndexingService extends AbstractSwiftService implements IndexingService {
    private static final long serialVersionUID = -7430843337225891194L;
    private transient RpcServer server = SwiftContext.getInstance().getBean(RpcServer.class);

    private Map<TaskKey, Object> stuffObject = new ConcurrentHashMap<TaskKey, Object>();

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
        appendStuffMap(stuff);
        triggerIndexing(stuff);
    }

    private void appendStuffMap(IndexingStuff stuff) {
        stuffObject.putAll(stuff.getTables());
        stuffObject.putAll(stuff.getRelationPaths());
        stuffObject.putAll(stuff.getRelations());
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
                SwiftLoggers.getLogger().info("rpc通知server任务完成");
                TaskKey key = result.getKey();
                Object obj = stuffObject.get(key);
                URL masterURL = getMasterURL();
                if (null != obj) {
                    if (obj instanceof DataSource) {
                        SourceKey sourceKey = ((DataSource) obj).getSourceKey();
                        List<SegmentKey> segmentKeys = SwiftSegmentServiceProvider.getProvider().getSegmentByKey(sourceKey.getId());
                        for (SegmentKey segmentKey : segmentKeys) {
                            try {
                                SwiftRepositoryManager.getManager().getCurrentRepository().copyToRemote(segmentKey.getAbsoluteUri(), segmentKey.getUri());
                            } catch (IOException e) {
                                logger.error("upload error! ", e);
                            }
                        }

                        ProxyFactory factory = ProxySelector.getInstance().getFactory();
                        Invoker invoker = factory.getInvoker(null, SwiftServiceListenerHandler.class, masterURL, false);
                        Result invokeResult = invoker.invoke(new SwiftInvocation(server.getMethodByName("rpcTrigger"), new Object[]{new HistoryLoadRpcEvent(sourceKey.getId())}));
                        RpcFuture future = (RpcFuture) invokeResult.getValue();
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
                    } else if (obj instanceof RelationSource) {
                        SourceKey sourceKey = ((RelationSource) obj).getForeignSource();
                        SourceKey primary = ((RelationSource) obj).getPrimarySource();
                        List<URI> needUpload = new ArrayList<URI>();
                        List<SegmentKey> segmentKeys = SwiftSegmentServiceProvider.getProvider().getSegmentByKey(sourceKey.getId());
                        if (((RelationSource) obj).getRelationType() != RelationSourceType.FIELD_RELATION) {
                            for (SegmentKey segmentKey : segmentKeys) {
                                try {
                                    URI src = URI.create(String.format("%s/%s/%s", Strings.trimSeparator(segmentKey.getAbsoluteUri().getPath() + "/", "/"), RelationIndexImpl.RELATIONS_KEY, primary.getId()));
                                    URI dest = URI.create(String.format("%s/%s/%s", Strings.trimSeparator(segmentKey.getUri().getPath() + "/", "/"), RelationIndexImpl.RELATIONS_KEY, primary.getId()));
                                    SwiftRepositoryManager.getManager().getCurrentRepository().copyToRemote(src, dest);
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
                                    SwiftRepositoryManager.getManager().getCurrentRepository().copyToRemote(src, dest);
                                    needUpload.add(dest);
                                } catch (IOException e) {
                                    logger.error("upload error! ", e);
                                }
                            }
                        }
                        ProxyFactory factory = ProxySelector.getInstance().getFactory();
                        Invoker invoker = factory.getInvoker(null, SwiftServiceListenerHandler.class, masterURL, false);
                        Result invokeResult = invoker.invoke(new SwiftInvocation(server.getMethodByName("rpcTrigger"), new Object[]{new HistoryLoadRelationRpcEvent(Pair.of(sourceKey.getId(), needUpload))}));
                        RpcFuture future = (RpcFuture) invokeResult.getValue();
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
                }
            }
        });

        WorkerTaskPool.getInstance().initListener();
        WorkerTaskPool.getInstance().setTaskGenerator(new TaskGenerator());

        CubeTaskManager.getInstance().initListener();
    }

    private static class TaskGenerator implements Function2<TaskKey, Object, WorkerTask> {
        @Override
        public WorkerTask apply(TaskKey taskKey, Object data) {
            if (taskKey.operation() == Operation.NULL) {
                return new WorkerTaskImpl(taskKey);
            }

            WorkerTask wt = null;
            if (data instanceof DataSource) {
                wt = new WorkerTaskImpl(taskKey, new TableBuilder(taskKey.getRound(), (DataSource) data));
                return wt;
            }
            if (data instanceof RelationSource) {
                RelationSource source = (RelationSource) data;
                switch (source.getRelationType()) {
                    case RELATION:
                        wt = new WorkerTaskImpl(taskKey, new MultiRelationIndexer(RelationPathHelper.convert2CubeRelation(source), SwiftContext.getInstance().getBean(LocalSegmentProvider.class)));
                        break;
                    case RELATION_PATH:
                        wt = new WorkerTaskImpl(taskKey, new TablePathIndexer(RelationPathHelper.convert2CubeRelationPath(source), SwiftContext.getInstance().getBean(LocalSegmentProvider.class)));
                        break;
                    case FIELD_RELATION:
                        FieldRelationSource fieldRelationSource = (FieldRelationSource) source;
                        wt = new WorkerTaskImpl(taskKey, new FieldPathIndexer(RelationPathHelper.convert2CubeRelationPath(fieldRelationSource.getRelationSource()), fieldRelationSource.getColumnKey(), SwiftContext.getInstance().getBean(LocalSegmentProvider.class)));
                        break;
                    default:
                }
                return wt;
            }
            return null;
        }
    }
}