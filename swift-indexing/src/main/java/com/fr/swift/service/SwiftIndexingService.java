package com.fr.swift.service;

import com.fineio.FineIO;
import com.fr.event.Event;
import com.fr.event.EventDispatcher;
import com.fr.event.Listener;
import com.fr.swift.annotation.SwiftService;
import com.fr.swift.config.service.SwiftCubePathService;
import com.fr.swift.config.service.SwiftSegmentLocationService;
import com.fr.swift.config.service.SwiftTablePathService;
import com.fr.swift.event.base.SwiftRpcEvent;
import com.fr.swift.exception.SwiftServiceException;
import com.fr.swift.info.ServerCurrentStatus;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.netty.rpc.server.RpcServer;
import com.fr.swift.repository.manager.SwiftRepositoryManager;
import com.fr.swift.source.Source;
import com.fr.swift.structure.Pair;
import com.fr.swift.stuff.IndexingStuff;
import com.fr.swift.task.TaskKey;
import com.fr.swift.task.TaskResult;
import com.fr.swift.task.cube.CubeTaskGenerator;
import com.fr.swift.task.cube.CubeTaskManager;
import com.fr.swift.task.impl.TaskEvent;
import com.fr.swift.task.impl.WorkerTaskPool;
import com.fr.swift.task.service.ServiceTaskExecutor;
import com.fr.swift.upload.AbstractUploadRunnable;
import com.fr.swift.upload.ReadyUploadContainer;
import com.fr.third.springframework.beans.factory.annotation.Autowired;
import com.fr.third.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

/**
 * @author pony
 * @date 2017/10/10
 */
@Service()
@SwiftService(name = "indexing")
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

    private static ListenerWorker worker;

    private SwiftIndexingService() {
        worker = new ListenerWorker() {
            @Override
            public void work(Pair<TaskKey, TaskResult> result) {
                SwiftLoggers.getLogger().info("rpc通知server任务完成");
                try {
                    EventDispatcher.fire(TaskEvent.DONE, result);
                    FineIO.doWhenFinished(new LocalUploadRunnable(result));
                } catch (Exception e) {
                    SwiftLoggers.getLogger().error(e);
                }
            }
        };
    }

    @Autowired
    private transient ServiceTaskExecutor taskExecutor;

    public SwiftIndexingService(String id) {
        super(id);
        worker = new ListenerWorker() {
            @Override
            public void work(Pair<TaskKey, TaskResult> result) {
                SwiftLoggers.getLogger().info("rpc通知server任务完成");
                try {
                    EventDispatcher.fire(TaskEvent.DONE, result);
                    FineIO.doWhenFinished(new LocalUploadRunnable(result));
                } catch (Exception e) {
                    SwiftLoggers.getLogger().error(e);
                }
            }
        };
    }

    @Override
    public boolean start() throws SwiftServiceException {
        super.start();
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


    private class LocalUploadRunnable extends AbstractUploadRunnable {

        public LocalUploadRunnable(Pair<TaskKey, TaskResult> result) {
            super(result, getID());
        }

        @Override
        protected void upload(URI src, URI dest) throws IOException {
            SwiftRepositoryManager.getManager().currentRepo().copyToRemote(src, dest);
        }

        @Override
        public void doAfterUpload(SwiftRpcEvent event) {

        }
    }
}