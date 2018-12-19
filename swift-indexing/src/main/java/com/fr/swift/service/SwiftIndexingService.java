package com.fr.swift.service;

import com.fineio.FineIO;
import com.fr.swift.annotation.SwiftService;
import com.fr.swift.config.entity.SwiftTablePathEntity;
import com.fr.swift.config.service.SwiftCubePathService;
import com.fr.swift.config.service.SwiftSegmentLocationService;
import com.fr.swift.config.service.SwiftTablePathService;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.event.SwiftEventDispatcher;
import com.fr.swift.event.SwiftEventListener;
import com.fr.swift.exception.SwiftServiceException;
import com.fr.swift.info.ServerCurrentStatus;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.netty.rpc.server.RpcServer;
import com.fr.swift.segment.SwiftSegmentManager;
import com.fr.swift.segment.container.SegmentContainer;
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

import java.io.File;
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
                        SwiftEventDispatcher.fire(TaskEvent.DONE, result);
                        FineIO.doWhenFinished(new ReplacePathRunnable(result));
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
        SwiftEventDispatcher.fire(TaskEvent.LOCAL_RUN, stuff.getTables());
        SwiftEventDispatcher.fire(TaskEvent.LOCAL_RUN, stuff.getRelations());
        SwiftEventDispatcher.fire(TaskEvent.LOCAL_RUN, stuff.getRelationPaths());
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
        SwiftEventListener listener = new SwiftEventListener<Pair<TaskKey, TaskResult>>() {
            @Override
            public void on(final Pair<TaskKey, TaskResult> result) {
                worker.work(result);
            }
        };
        SwiftEventDispatcher.listen(TaskEvent.LOCAL_DONE, listener);

        initTaskGenerator();
    }

    private class ReplacePathRunnable implements Runnable {

        private Pair<TaskKey, TaskResult> result;
        private SwiftSegmentManager manager;

        public ReplacePathRunnable(Pair<TaskKey, TaskResult> result) {
            this.result = result;
            this.manager = SwiftContext.get().getBean("localSegmentProvider", SwiftSegmentManager.class);
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
                                    ((DataSource) obj).getMetadata().getSwiftDatabase().getDir(),
                                    path,
                                    sourceKey.getId());
                            FileUtil.delete(deletePath);
                            new File(deletePath).getParentFile().delete();
                        }
                        SegmentContainer.NORMAL.remove(sourceKey);
                        SegmentContainer.INDEXING.remove(sourceKey);
                        manager.getSegment(sourceKey);
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