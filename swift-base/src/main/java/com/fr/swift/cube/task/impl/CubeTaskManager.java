package com.fr.swift.cube.task.impl;

import com.fr.event.Event;
import com.fr.event.EventDispatcher;
import com.fr.event.Listener;
import com.fr.swift.cube.task.Task.Status;
import com.fr.swift.cube.task.TaskExecutor;
import com.fr.swift.cube.task.TaskKey;
import com.fr.swift.cube.task.TaskManager;
import com.fr.swift.cube.task.WorkerTask;
import com.fr.swift.util.Crasher;

import java.util.Map;
import java.util.Map.Entry;

/**
 * @author anchore
 * @date 2017/12/8
 */
public class CubeTaskManager implements TaskManager {
    private TaskExecutor transportExec = new CubeTaskExecutor("Transporter", 4);
    private TaskExecutor indexExec = new CubeTaskExecutor("Indexer", 10);

    @Override
    public void run(WorkerTask task) {
        synchronized (task) {
            if (task.status().compare(Status.RUNNABLE) > 0) {
                return;
            }
        }
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

    public void initListener() {
        EventDispatcher.listen(TaskEvent.LOCAL_RUN, new Listener<Map<TaskKey, ?>>() {
            @Override
            public void on(Event event, Map<TaskKey, ?> tasks) {
                for (Entry<TaskKey, ?> entry : tasks.entrySet()) {
                    WorkerTask task = WorkerTaskPool.getInstance().generate(entry.getKey(), entry.getValue());
                    CubeTaskManager.getInstance().run(task);
                }
            }
        });
    }

    private static final CubeTaskManager INSTANCE = new CubeTaskManager();

    private CubeTaskManager() {
    }

    public static CubeTaskManager getInstance() {
        return INSTANCE;
    }
}
