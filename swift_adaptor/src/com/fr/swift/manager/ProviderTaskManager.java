//package com.fr.swift.manager;
//
//import com.fr.swift.cube.queue.StuffFetcher;
//import com.fr.swift.cube.task.TaskKey;
//import com.fr.swift.cube.task.WorkerTask;
//import com.fr.swift.cube.task.impl.CubeTaskManager;
//import com.fr.swift.cube.task.impl.Operation;
//import com.fr.swift.cube.task.impl.SchedulerTaskPool;
//import com.fr.swift.cube.task.impl.WorkerTaskImpl;
//import com.fr.swift.cube.task.impl.WorkerTaskPool;
//import com.fr.swift.generate.history.TableBuilder;
//import com.fr.swift.log.SwiftLogger;
//import com.fr.swift.log.SwiftLoggers;
//import com.fr.swift.provider.ConnectionProvider;
//import com.fr.swift.source.DataSource;
//import com.fr.swift.source.db.ConnectionManager;
//
///**
// * This class created on 2018-1-16 16:51:49
// *
// * @author Lucifer
// * @description
// * @since Advanced FineBI Analysis 1.0
// */
//public class ProviderTaskManager {
//
//    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(ProviderTaskManager.class);
//
//    private ProviderTaskManager() {
//        init();
//        initTaskFetchThread();
//        ConnectionManager.getInstance().registerProvider(new ConnectionProvider());
//    }
//
//    private static class SingletonHolder {
//        private static final ProviderTaskManager INSTANCE = new ProviderTaskManager();
//    }
//
//    public static void start() {
//        ProviderTaskManager.getManager();
//    }
//
//    public static final ProviderTaskManager getManager() {
//        return SingletonHolder.INSTANCE;
//    }
//
//    private void init() {
//        SchedulerTaskPool.getInstance();
//        WorkerTaskPool.getInstance().setGenerator(pair -> {
//            TaskKey taskKey = pair.key();
//            if (taskKey.operation() == Operation.NULL) {
//                return new WorkerTaskImpl(taskKey);
//            }
//
//            Object o = pair.value();
//            if (o instanceof DataSource) {
//                DataSource ds = ((DataSource) o);
//                WorkerTask wt = new WorkerTaskImpl(taskKey);
//                wt.setWorker(new TableBuilder(ds));
//                return wt;
//            } else {
//                return null;
//            }
//        });
//        CubeTaskManager.getInstance();
//    }
//
//    private void initTaskFetchThread() {
//        new Thread(new StuffFetcher()).start();
//    }
//}

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
import com.fr.swift.generate.history.MultiRelationIndexer;
import com.fr.swift.generate.history.TableBuilder;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.relation.utils.MultiRelationHelper;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.RelationSource;
import com.fr.swift.structure.Pair;
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
                    WorkerTask wt = new WorkerTaskImpl(taskKey);
                    wt.setWorker(BaseWorker.nullWorker());
                    return wt;
                }

                Object o = pair.getValue();
                if (o instanceof DataSource) {
                    DataSource ds = ((DataSource) o);
                    WorkerTask wt = new WorkerTaskImpl(taskKey);
                    wt.setWorker(new TableBuilder(ds));
                    return wt;
                } else if (o instanceof RelationSource) {
                    RelationSource source = (RelationSource) o;
                    WorkerTask wt = new WorkerTaskImpl(taskKey);
                    wt.setWorker(new MultiRelationIndexer(MultiRelationHelper.convert2CubeRelation(source), LocalSegmentProvider.getInstance()));
                    return wt;
                } else {
                    return null;
                }
            }
        });
        CubeTaskManager.getInstance().initListener();
    }

    private void initTaskFetchThread() {
        new Thread(new StuffFetcher()).start();
    }
}