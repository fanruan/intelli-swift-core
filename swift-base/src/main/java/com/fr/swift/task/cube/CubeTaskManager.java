package com.fr.swift.task.cube;

import com.fr.event.Event;
import com.fr.event.EventDispatcher;
import com.fr.event.Listener;
import com.fr.swift.task.Task.Status;
import com.fr.swift.task.TaskExecutor;
import com.fr.swift.task.TaskKey;
import com.fr.swift.task.TaskManager;
import com.fr.swift.task.WorkerTask;
import com.fr.swift.task.impl.TaskEvent;
import com.fr.swift.task.impl.WorkerTaskPool;
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

        CubeOperation op = ((CubeOperation) task.key().operation());
        switch (op) {
            case TRANSPORT_TABLE:
                transportExec.add(task);
                return;
            case INDEX_COLUMN:
            case MERGE_COLUMN_DICT:
            case INDEX_RELATION:
            case INDEX_PATH:
            case INDEX_COLUMN_PATH:
                indexExec.add(task);
                return;
            case BUILD_TABLE:
            case NULL:
                task.run();
                return;
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
