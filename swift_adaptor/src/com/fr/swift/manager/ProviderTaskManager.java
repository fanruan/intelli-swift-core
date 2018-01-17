package com.fr.swift.manager;

import com.fr.swift.cube.queue.StuffFetchThread;
import com.fr.swift.cube.task.TaskKey;
import com.fr.swift.cube.task.WorkerTask;
import com.fr.swift.cube.task.impl.CubeTaskManager;
import com.fr.swift.cube.task.impl.Operation;
import com.fr.swift.cube.task.impl.SchedulerTaskPool;
import com.fr.swift.cube.task.impl.WorkerTaskImpl;
import com.fr.swift.cube.task.impl.WorkerTaskPool;
import com.fr.swift.generate.impl.TableBuilder;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.source.DataSource;

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
        init();
        initTaskFetchThread();
    }

    private static class SingletonHolder {
        private static final ProviderTaskManager INSTANCE = new ProviderTaskManager();
    }

    public static void start() {
        ProviderTaskManager.getManager();
    }

    public static final ProviderTaskManager getManager() {
        return SingletonHolder.INSTANCE;
    }

    private void init() {
        SchedulerTaskPool.getInstance();
        WorkerTaskPool.getInstance().setGenerator(pair -> {
            TaskKey taskKey = pair.key();
            if (taskKey.operation() == Operation.NULL) {
                return new WorkerTaskImpl(taskKey);
            }

            Object o = pair.value();
            if (o instanceof DataSource) {
                DataSource ds = ((DataSource) o);
                WorkerTask wt = new WorkerTaskImpl(taskKey);
                wt.setWorker(new TableBuilder(ds));
                return wt;
            } else {
                return null;
            }
        });
        CubeTaskManager.getInstance();
    }

    private void initTaskFetchThread() {
        new Thread(new StuffFetchThread()).start();
    }
}
