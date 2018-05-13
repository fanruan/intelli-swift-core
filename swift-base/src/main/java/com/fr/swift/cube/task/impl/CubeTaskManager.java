package com.fr.swift.cube.task.impl;

import com.fr.swift.cube.task.Task.Status;
import com.fr.swift.cube.task.TaskExecutor;
import com.fr.swift.cube.task.TaskKey;
import com.fr.swift.cube.task.TaskManager;
import com.fr.swift.cube.task.WorkerTask;
import com.fr.swift.exception.SwiftServiceException;
import com.fr.swift.service.SwiftServiceEvent;
import com.fr.swift.service.listener.EventOrder;
import com.fr.swift.service.listener.EventType;
import com.fr.swift.service.listener.SwiftServiceListener;
import com.fr.swift.service.listener.SwiftServiceListenerManager;
import com.fr.swift.util.Crasher;

/**
 * @author anchore
 * @date 2017/12/8
 */
public class CubeTaskManager implements TaskManager {
    private TaskExecutor transportExec = new CubeTaskExecutor("Transporter", 4);
    private TaskExecutor indexExec = new CubeTaskExecutor("Indexer", 10);

    @Override
    public void run(WorkerTask task) {
        task.setStatus(Status.RUNNABLE);

        switch (task.key().operation()) {
            case TRANSPORT_TABLE:
                transportExec.add(task);
                break;
            case INDEX_COLUMN:
            case MERGE_COLUMN_DICT:
            case INDEX_RELATION:
            case INDEX_PATH:
            case INDEX_COLUMN_PATH:
                indexExec.add(task);
                break;
            // 咸鱼任务 组任务 直接run了
            case NULL:
            case BUILD_TABLE:
                task.run();
                break;
            default:
                Crasher.crash(String.format("cannot find right pool to execute %s", task));
        }
    }

    public void initListener() throws SwiftServiceException {
        SwiftServiceListenerManager.getInstance().addListener(new SwiftServiceListener<TaskKey>() {
            @Override
            public void handle(SwiftServiceEvent<TaskKey> event) {
                run(from(event.getContent()));
            }

            @Override
            public EventType getType() {
                return EventType.RUN_TASK;
            }

            @Override
            public EventOrder getOrder() {
                return EventOrder.AFTER;
            }
        });
    }

    private static WorkerTask from(TaskKey key) {
        return WorkerTaskPool.getInstance().get(key);
    }

    private static final CubeTaskManager INSTANCE = new CubeTaskManager();

    private CubeTaskManager() {
    }

    public static CubeTaskManager getInstance() {
        return INSTANCE;
    }
}
