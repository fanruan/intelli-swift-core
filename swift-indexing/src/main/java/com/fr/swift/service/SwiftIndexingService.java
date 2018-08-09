package com.fr.swift.service;

import com.fineio.FineIO;
import com.fr.event.Event;
import com.fr.event.EventDispatcher;
import com.fr.event.Listener;
import com.fr.stable.StringUtils;
import com.fr.swift.config.entity.SwiftTablePathEntity;
import com.fr.swift.config.service.SwiftCubePathService;
import com.fr.swift.config.service.SwiftSegmentLocationService;
import com.fr.swift.config.service.SwiftTablePathService;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.exception.SwiftServiceException;
import com.fr.swift.info.ServerCurrentStatus;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.netty.rpc.server.RpcServer;
import com.fr.swift.property.SwiftProperty;
import com.fr.swift.source.DataSource;
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
import com.fr.third.springframework.beans.factory.annotation.Autowired;
import com.fr.third.springframework.stereotype.Service;

import java.io.File;
import java.util.Map;

import static com.fr.swift.task.TaskResult.Type.SUCCEEDED;

/**
 * @author pony
 * @date 2017/10/10
 */
@Service("indexingService")
public class SwiftIndexingService extends AbstractSwiftService implements IndexingService {
    private static final long serialVersionUID = -7430843337225891194L;

    @Autowired
    private transient RpcServer server;
    @Autowired
    private transient SwiftCubePathService pathService;
    @Autowired
    private transient SwiftTablePathService tablePathService;
    @Autowired
    private transient SwiftSegmentLocationService locationService;

    private static ListenerWorker worker ;

    private SwiftIndexingService() {
    }

    @Autowired
    private transient ServiceTaskExecutor taskExecutor;

    public SwiftIndexingService(String id) {
        super(id);
    }

    @Override
    public String getID() {
        return StringUtils.isEmpty(super.getID()) ? SwiftContext.get().getBean(SwiftProperty.class).getServerAddress() : super.getID();
    }

    @Override
    public boolean start() throws SwiftServiceException {
        super.start();
        worker = new ListenerWorker() {
            @Override
            public void work(Pair<TaskKey, TaskResult> result) {
                try {
                    EventDispatcher.fire(TaskEvent.DONE, result);
                    FineIO.doWhenFinished(new ReplacePathRunnable(result));
                } catch (Exception e) {
                    SwiftLoggers.getLogger().error(e);
                }
            }
        };
        initListener();
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
        EventDispatcher.listen(TaskEvent.LOCAL_DONE, new Listener<Pair<TaskKey, TaskResult>>() {
            @Override
            public void on(Event event, final Pair<TaskKey, TaskResult> result) {
                worker.work(result);
            }
        });

        initTaskGenerator();
    }


//    private class LocalUploadRunnable extends AbstractUploadRunnable {
//
//        public LocalUploadRunnable(Pair<TaskKey, TaskResult> result) {
//            super(result, getID());
//        }
//
//        @Override
//        protected void upload(URI src, URI dest) throws IOException {
////            SwiftRepositoryManager.getManager().currentRepo().copyToRemote(src, dest);
//        }
//
//        @Override
//        public void doAfterUpload(SwiftRpcEvent event) {
//
//        }
//    }

    private class ReplacePathRunnable implements Runnable {

        private Pair<TaskKey, TaskResult> result;

        public ReplacePathRunnable(Pair<TaskKey, TaskResult> result) {
            this.result = result;
        }

        private void runSuccess(TaskKey key, Object obj) {
            try {
                if (null != obj) {
                    if (obj instanceof DataSource) {
                        SourceKey sourceKey = ((DataSource) obj).getSourceKey();
                        SwiftTablePathEntity entity = SwiftContext.get().getBean(SwiftTablePathService.class).get(sourceKey.getId());
                        Integer path = entity.getTablePath();
                        path = null == path ? -1 : path;
                        Integer tmpPath = entity.getTmpDir();
                        entity.setTablePath(tmpPath);
                        entity.setLastPath(path);
                        if (path.compareTo(tmpPath) != 0 && tablePathService.saveOrUpdate(entity)) {
                            String deletePath = String.format("%s/%s/%d/%s",
                                    pathService.getSwiftPath(),
                                    ((DataSource) obj).getMetadata().getSwiftSchema().getDir(),
                                    path,
                                    sourceKey.getId());
                            FileUtil.delete(deletePath);
                            new File(deletePath).getParentFile().delete();
                        }
                    }
                    ReadyUploadContainer.instance().remove(key);
                }

            } catch (Exception e) {
                SwiftLoggers.getLogger().error(e);
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
                                ((DataSource) obj).getMetadata().getSwiftSchema().getDir(),
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

        @Override
        public void run() {
            if (result.getValue().getType() == SUCCEEDED) {
                TaskKey key = result.getKey();
                Object obj = ReadyUploadContainer.instance().get(key);
                runSuccess(key, obj);
            } else {
                TaskKey key = result.getKey();
                Object obj = ReadyUploadContainer.instance().get(key);
                runFailed(key, obj);
            }

        }
    }
}