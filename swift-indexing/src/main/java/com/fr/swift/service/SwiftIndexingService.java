package com.fr.swift.service;

import com.fr.swift.SwiftContext;
import com.fr.swift.annotation.SwiftService;
import com.fr.swift.basics.annotation.ProxyService;
import com.fr.swift.basics.base.selector.ProxySelector;
import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.config.bean.ServerCurrentStatus;
import com.fr.swift.config.bean.SwiftTablePathBean;
import com.fr.swift.config.service.SwiftCubePathService;
import com.fr.swift.config.service.SwiftTablePathService;
import com.fr.swift.event.SwiftEventDispatcher;
import com.fr.swift.event.SwiftEventListener;
import com.fr.swift.event.base.SwiftRpcEvent;
import com.fr.swift.exception.SwiftServiceException;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.repository.manager.SwiftRepositoryManager;
import com.fr.swift.segment.SegmentHelper;
import com.fr.swift.segment.SwiftSegmentManager;
import com.fr.swift.service.listener.RemoteSender;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.RelationSource;
import com.fr.swift.source.Source;
import com.fr.swift.source.SourceKey;
import com.fr.swift.structure.Pair;
import com.fr.swift.stuff.IndexingStuff;
import com.fr.swift.task.ReadyUploadContainer;
import com.fr.swift.task.TaskKey;
import com.fr.swift.task.TaskResult;
import com.fr.swift.task.cube.CubeTaskGenerator;
import com.fr.swift.task.cube.CubeTaskManager;
import com.fr.swift.task.impl.TaskEvent;
import com.fr.swift.task.impl.WorkerTaskPool;
import com.fr.swift.util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import static com.fr.swift.task.TaskResult.Type.SUCCEEDED;

/**
 * @author pony
 * @date 2017/10/10
 */
@SwiftService(name = "indexing")
@ProxyService(IndexingService.class)
@SwiftBean(name = "indexing")
public class SwiftIndexingService extends AbstractSwiftService implements IndexingService {

    private static final long serialVersionUID = -7430843337225891194L;

    private transient boolean initable = true;

    public SwiftIndexingService() {
    }

    @Override
    public boolean start() throws SwiftServiceException {
        super.start();
        if (initable) {
            initListener();
            initable = false;
        }
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

    private void initTaskGenerator() {
        WorkerTaskPool.getInstance().initListener();
        WorkerTaskPool.getInstance().setTaskGenerator(new CubeTaskGenerator());
        CubeTaskManager.getInstance().initListener();
    }

    @Override
    public ServerCurrentStatus currentStatus() {
        return new ServerCurrentStatus(getId());
    }

    private void initListener() {
        SwiftEventListener listener = new SwiftEventListener<Pair<TaskKey, TaskResult>>() {
            @Override
            public void on(final Pair<TaskKey, TaskResult> result) {
                try {
                    SwiftEventDispatcher.fire(TaskEvent.DONE, result);
                    new UploadRunnable(result).run();
                } catch (Exception e) {
                    SwiftLoggers.getLogger().error(e);
                }
            }
        };
        SwiftEventDispatcher.listen(TaskEvent.LOCAL_DONE, listener);

        initTaskGenerator();
    }

    private class UploadRunnable implements Runnable {

        Pair<TaskKey, TaskResult> result;
        private SwiftCubePathService pathService = SwiftContext.get().getBean(SwiftCubePathService.class);

        private SwiftSegmentManager manager;

        public UploadRunnable(Pair<TaskKey, TaskResult> result) {
            this.result = result;
            this.manager = SwiftContext.get().getBean("localSegmentProvider", SwiftSegmentManager.class);
        }

        @Override
        public void run() {
            if (result.getValue().getType() == SUCCEEDED) {
                TaskKey key = result.getKey();
                Object obj = ReadyUploadContainer.instance().get(key);
                try {
                    if (null != obj) {
                        if (obj instanceof DataSource) {
                            SegmentHelper.uploadTable(manager, (DataSource) obj, getId());
                        } else if (obj instanceof RelationSource) {
                            SegmentHelper.uploadRelation((RelationSource) obj, getId());
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
                        SwiftTablePathBean entity = SwiftContext.get().getBean(SwiftTablePathService.class).get(sourceKey.getId());
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
            SwiftRepositoryManager.getManager().currentRepo().copyToRemote(src, dest);
        }

        public void doAfterUpload(SwiftRpcEvent event) {
            ProxySelector.getInstance().getFactory().getProxy(RemoteSender.class).trigger(event);
        }
    }
}