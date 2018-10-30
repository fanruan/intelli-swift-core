package com.fr.swift.service;

import com.fineio.FineIO;
import com.fr.event.Event;
import com.fr.event.EventDispatcher;
import com.fr.event.Listener;
import com.fr.swift.annotation.SwiftService;
import com.fr.swift.basics.base.selector.ProxySelector;
import com.fr.swift.config.bean.ServerCurrentStatus;
import com.fr.swift.config.entity.SwiftTablePathEntity;
import com.fr.swift.config.service.SwiftCubePathService;
import com.fr.swift.config.service.SwiftSegmentLocationService;
import com.fr.swift.config.service.SwiftSegmentService;
import com.fr.swift.config.service.SwiftTablePathService;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.cube.CubeUtil;
import com.fr.swift.event.base.SwiftRpcEvent;
import com.fr.swift.event.history.HistoryCommonLoadRpcEvent;
import com.fr.swift.event.history.HistoryLoadSegmentRpcEvent;
import com.fr.swift.exception.SwiftServiceException;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.netty.rpc.server.RpcServer;
import com.fr.swift.repository.SwiftRepositoryManager;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.SwiftSegmentManager;
import com.fr.swift.segment.container.SegmentContainer;
import com.fr.swift.segment.relation.RelationIndexImpl;
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
import com.fr.swift.upload.ReadyUploadContainer;
import com.fr.swift.util.FileUtil;
import com.fr.swift.util.Strings;
import com.fr.third.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.fr.swift.task.TaskResult.Type.SUCCEEDED;

/**
 * @author pony
 * @date 2017/10/10
 */
@SwiftService(name = "indexing")
public class SwiftIndexingService extends AbstractSwiftService implements IndexingService {
    private static final long serialVersionUID = -7430843337225891194L;

    private transient RpcServer server;

    private transient SwiftCubePathService pathService;

    private transient SwiftTablePathService tablePathService;

    private transient SwiftSegmentLocationService locationService;

    private static ListenerWorker worker;

    private transient ServiceTaskExecutor taskExecutor;

    private transient boolean initable = true;
    @Autowired(required = false)
    private transient SwiftRepositoryManager repositoryManager;

    private SwiftIndexingService() {
    }

    @Override
    public boolean start() throws SwiftServiceException {
        super.start();
        if (initable) {
            worker = new ListenerWorker() {
                @Override
                public void work(Pair<TaskKey, TaskResult> result) {
                    try {
                        EventDispatcher.fire(TaskEvent.DONE, result);
                        FineIO.doWhenFinished(new UploadRunnable(result, getID()));
                    } catch (Exception e) {
                        SwiftLoggers.getLogger().error(e);
                    }
                }
            };
            initListener();
            initable = false;
        }
        server = SwiftContext.get().getBean(RpcServer.class);
        pathService = SwiftContext.get().getBean(SwiftCubePathService.class);
        tablePathService = SwiftContext.get().getBean(SwiftTablePathService.class);
        locationService = SwiftContext.get().getBean(SwiftSegmentLocationService.class);
        taskExecutor = SwiftContext.get().getBean(ServiceTaskExecutor.class);
        return true;
    }

    @Override
    public boolean shutdown() throws SwiftServiceException {
        super.shutdown();
        server = null;
        pathService = null;
        tablePathService = null;
        locationService = null;
        taskExecutor = null;
        return true;
    }

    @Override
    public ServiceType getServiceType() {
        return ServiceType.INDEXING;
    }

    @Override
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
                    ReadyUploadContainer.instance().put(entry.getKey(), entry.getValue());
                }
            }
        }
    }

    private void triggerIndexing(IndexingStuff stuff) {
        EventDispatcher.fire(TaskEvent.LOCAL_RUN, stuff.getTables());
        EventDispatcher.fire(TaskEvent.LOCAL_RUN, stuff.getRelations());
        EventDispatcher.fire(TaskEvent.LOCAL_RUN, stuff.getRelationPaths());
    }

    public void initTaskGenerator() {
        WorkerTaskPool.getInstance().initListener();
        WorkerTaskPool.getInstance().setTaskGenerator(new CubeTaskGenerator());
        CubeTaskManager.getInstance().initListener();
    }

    @Override
    public ServerCurrentStatus currentStatus() {
        return new ServerCurrentStatus(getID());
    }

    @Override
    public void setListenerWorker(ListenerWorker listenerWorker) {
        worker = listenerWorker;
    }

    private void initListener() {
        Listener listener = new Listener<Pair<TaskKey, TaskResult>>() {
            @Override
            public void on(Event event, final Pair<TaskKey, TaskResult> result) {
                worker.work(result);
            }
        };
        EventDispatcher.listen(TaskEvent.LOCAL_DONE, listener);

        initTaskGenerator();
    }

    private class UploadRunnable implements Runnable {

        protected Pair<TaskKey, TaskResult> result;
        private String id;
        private SwiftSegmentManager manager;

        public UploadRunnable(Pair<TaskKey, TaskResult> result, String id) {
            this.result = result;
            this.id = id;
            this.manager = SwiftContext.get().getBean("localSegmentProvider", SwiftSegmentManager.class);
        }

        public void uploadTable(final DataSource dataSource) throws Exception {
            final SourceKey sourceKey = dataSource.getSourceKey();
            SwiftTablePathEntity entity = SwiftContext.get().getBean(SwiftTablePathService.class).get(sourceKey.getId());
            Integer path = entity.getTablePath();
            path = null == path ? -1 : path;
            Integer tmpPath = entity.getTmpDir();
            entity.setTablePath(tmpPath);
            entity.setLastPath(path);
            List<SegmentKey> segmentKeys = SwiftContext.get().getBean("segmentServiceProvider", SwiftSegmentService.class).getSegmentByKey(sourceKey.getId());
            String cubePath = pathService.getSwiftPath();
            if (null != segmentKeys) {
                repositoryManager.currentRepo().delete(String.format("%s/%s", dataSource.getMetadata().getSwiftDatabase().getDir(), sourceKey.getId()));
                for (SegmentKey segmentKey : segmentKeys) {
                    try {
                        String uploadPath = String.format("%s/%s",
                                segmentKey.getSwiftSchema().getDir(),
                                segmentKey.getUri().getPath());
                        String local = String.format("%s/%s", cubePath, CubeUtil.getHistorySegPath(dataSource, tmpPath, segmentKey.getOrder()));
                        upload(local, uploadPath);
                    } catch (Exception e) {
                        SwiftLoggers.getLogger().error("upload error! ", e);
                    }
                }
                if (path.compareTo(tmpPath) != 0 && tablePathService.saveOrUpdate(entity)
                        && locationService.delete(sourceKey.getId(), id)) {
                    String deletePath = String.format("%s/%s/%d/%s",
                            pathService.getSwiftPath(),
                            dataSource.getMetadata().getSwiftDatabase().getDir(),
                            path,
                            sourceKey.getId());
                    FileUtil.delete(deletePath);
                    new File(deletePath).getParentFile().delete();
                }
                manager.remove(sourceKey);
                SegmentContainer.INDEXING.remove(sourceKey);
                manager.getSegment(sourceKey);
                doAfterUpload(new HistoryLoadSegmentRpcEvent(sourceKey.getId(), getID()));
            }
        }

        public void uploadRelation(RelationSource relation) throws Exception {
            SourceKey sourceKey = relation.getForeignSource();
            SourceKey primary = relation.getPrimarySource();
            Map<String, List<String>> segNeedUpload = new HashMap<String, List<String>>();
            List<SegmentKey> segmentKeys = SwiftContext.get().getBean("segmentServiceProvider", SwiftSegmentService.class).getSegmentByKey(sourceKey.getId());
            if (null != segmentKeys) {
                if (relation.getRelationType() != RelationSourceType.FIELD_RELATION) {
                    for (SegmentKey segmentKey : segmentKeys) {
                        String key = segmentKey.toString();
                        if (null == segNeedUpload.get(key)) {
                            segNeedUpload.put(key, new ArrayList<String>());
                        }
                        try {
                            String src = Strings.unifySlash(
                                    String.format("%s/%s/%s/%s",
                                            pathService.getSwiftPath(),
                                            CubeUtil.getSegPath(segmentKey),
                                            RelationIndexImpl.RELATIONS_KEY,
                                            primary.getId()
                                    ));
                            String dest = String.format("%s/%s/%s/%s",
                                    segmentKey.getSwiftSchema().getDir(),
                                    Strings.unifySlash(segmentKey.getUri().getPath() + "/"),
                                    RelationIndexImpl.RELATIONS_KEY,
                                    primary.getId());
                            upload(src, dest);
                            segNeedUpload.get(key).add(dest);
                        } catch (IOException e) {
                            SwiftLoggers.getLogger().error("upload error! ", e);
                        }
                    }
                } else {
                    for (SegmentKey segmentKey : segmentKeys) {
                        String key = segmentKey.toString();
                        if (null == segNeedUpload.get(key)) {
                            segNeedUpload.put(key, new ArrayList<String>());
                        }
                        try {
                            String src = Strings.unifySlash(
                                    String.format("%s/field/%s/%s",
                                            CubeUtil.getSegPath(segmentKey),
                                            RelationIndexImpl.RELATIONS_KEY,
                                            primary.getId()
                                    ));
                            String dest = String.format("%s/%s/field/%s/%s",
                                    segmentKey.getSwiftSchema().getDir(),
                                    Strings.unifySlash(segmentKey.getUri().getPath() + "/"),
                                    RelationIndexImpl.RELATIONS_KEY,
                                    primary.getId());
                            upload(src, dest);
                            segNeedUpload.get(key).add(dest);
                        } catch (IOException e) {
                            SwiftLoggers.getLogger().error("upload error! ", e);
                        }
                    }
                }
                doAfterUpload(new HistoryCommonLoadRpcEvent(Pair.of(sourceKey.getId(), segNeedUpload), getID()));
            }
        }

        @Override
        public void run() {
            if (result.getValue().getType() == SUCCEEDED) {
                TaskKey key = result.getKey();
                Object obj = ReadyUploadContainer.instance().get(key);
                try {
                    if (null != obj) {
                        if (obj instanceof DataSource) {
                            uploadTable((DataSource) obj);
                        } else if (obj instanceof RelationSource) {
                            uploadRelation((RelationSource) obj);
                        }
                        ReadyUploadContainer.instance().remove(key);
                    }

                } catch (Exception e) {
                    SwiftLoggers.getLogger().error(e);
                }
            } else {
                TaskKey key = result.getKey();
                Object obj = ReadyUploadContainer.instance().get(key);
                runFailed(key, obj);
            }
        }

        private void runFailed(TaskKey key, Object obj) {
            try {
                if (null != obj) {
                    if (obj instanceof DataSource) {
                        SourceKey sourceKey = ((DataSource) obj).getSourceKey();
                        SwiftTablePathEntity entity = SwiftContext.get().getBean(SwiftTablePathService.class).get(sourceKey.getId());
                        Integer tmpPath = entity.getTmpDir();
                        String deletePath = String.format("%s/%s/%d/%s",
                                pathService.getSwiftPath(),
                                ((DataSource) obj).getMetadata().getSwiftDatabase().getDir(),
                                tmpPath,
                                sourceKey.getId());
                        FileUtil.delete(deletePath);
                        new File(deletePath).getParentFile().delete();
                        ReadyUploadContainer.instance().remove(key);
                    }
                }
            } catch (Exception e) {
                SwiftLoggers.getLogger().error(e);
            }

        }

        protected void upload(String src, String dest) throws IOException {
            repositoryManager.currentRepo().copyToRemote(src, dest);
        }

        public void doAfterUpload(SwiftRpcEvent event) {
            ProxySelector.getInstance().getFactory().getProxy(SwiftServiceListenerHandler.class).trigger(event);
        }
    }
}