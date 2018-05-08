package com.fr.swift.manager;

import com.fr.swift.cube.queue.StuffFetcher;
import com.fr.swift.cube.task.TaskKey;
import com.fr.swift.cube.task.WorkerTask;
import com.fr.swift.cube.task.impl.BaseWorker;
import com.fr.swift.cube.task.impl.CubeTaskManager;
import com.fr.swift.cube.task.impl.Operation;
import com.fr.swift.cube.task.impl.SchedulerTaskPool;
import com.fr.swift.cube.task.impl.WorkerTaskImpl;
import com.fr.swift.cube.task.impl.WorkerTaskPool;
import com.fr.swift.exception.SwiftServiceException;
import com.fr.swift.flow.FlowRuleController;
import com.fr.swift.generate.history.TableBuilder;
import com.fr.swift.generate.history.index.FieldPathIndexer;
import com.fr.swift.generate.history.index.MultiRelationIndexer;
import com.fr.swift.generate.history.index.TablePathIndexer;
import com.fr.swift.generate.realtime.RealtimeTableBuilder;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.relation.utils.RelationPathHelper;
import com.fr.swift.reliance.SourceNode;
import com.fr.swift.source.RelationSource;
import com.fr.swift.source.relation.FieldRelationSource;
import com.fr.swift.structure.Pair;
import com.fr.swift.util.concurrent.SingleThreadFactory;
import com.fr.swift.util.function.Function;

/**
 * This class created on 2018-1-16 16:51:49
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public class ProviderTaskManager {

    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(ProviderTaskManager.class);

    private ProviderTaskManager() {
        try {
            init();
        } catch (SwiftServiceException e) {
            SwiftLoggers.getLogger().error(e);

        }
        initTaskFetchThread();
    }

    private static class SingletonHolder {
        private static final ProviderTaskManager INSTANCE = new ProviderTaskManager();
    }

    public static void start() {
        ProviderTaskManager.getManager();
    }

    public static ProviderTaskManager getManager() {
        return SingletonHolder.INSTANCE;
    }

    private void init() throws SwiftServiceException {
        SchedulerTaskPool.getInstance().initListener();
        WorkerTaskPool.getInstance().initListener();
        WorkerTaskPool.getInstance().setGenerator(new Function<Pair<TaskKey, Object>, WorkerTask>() {
            @Override
            public WorkerTask apply(Pair<TaskKey, Object> pair) {
                TaskKey taskKey = pair.getKey();
                if (taskKey.operation() == Operation.NULL) {
                    return new WorkerTaskImpl(taskKey, BaseWorker.nullWorker());
                }

                Object o = pair.getValue();
                if (o instanceof SourceNode) {
                    SourceNode sourceNode = ((SourceNode) o);
                    WorkerTask wt;
                    if (sourceNode.isIncrement()) {
                        wt = new WorkerTaskImpl(taskKey, new RealtimeTableBuilder(sourceNode.getNode(), sourceNode.getIncrement(), new FlowRuleController()));
                    } else {
                        wt = new WorkerTaskImpl(taskKey, new TableBuilder(sourceNode.getNode()));
                    }
                    return wt;
                } else if (o instanceof RelationSource) {
                    RelationSource source = (RelationSource) o;
                    WorkerTask wt = null;
                    switch (source.getRelationType()) {
                        case RELATION:
                            wt = new WorkerTaskImpl(taskKey, new MultiRelationIndexer(RelationPathHelper.convert2CubeRelation(source), LocalSegmentProvider.getInstance()));
                            break;
                        case RELATION_PATH:
                            wt = new WorkerTaskImpl(taskKey, new TablePathIndexer(RelationPathHelper.convert2CubeRelationPath(source), LocalSegmentProvider.getInstance()));
                            break;
                        case FIELD_RELATION:
                            FieldRelationSource fieldRelationSource = (FieldRelationSource) source;
                            wt = new WorkerTaskImpl(taskKey, new FieldPathIndexer(RelationPathHelper.convert2CubeRelationPath(fieldRelationSource.getRelationSource()), fieldRelationSource.getColumnKey(), LocalSegmentProvider.getInstance()));
                            break;
                        default:
                    }
                    return wt;
                } else {
                    return null;
                }
            }
        });
        CubeTaskManager.getInstance().initListener();
    }

    private void initTaskFetchThread() {
        new SingleThreadFactory("StuffFetcher").newThread(new StuffFetcher()).start();
    }
}