package com.fr.swift.task.cube;

import com.fr.event.Event;
import com.fr.event.EventDispatcher;
import com.fr.event.Listener;
import com.fr.swift.task.Operation;
import com.fr.swift.task.Task.Status;
import com.fr.swift.task.TaskExecutor;
import com.fr.swift.task.TaskKey;
import com.fr.swift.task.TaskManager;
import com.fr.swift.task.WorkerTask;
import com.fr.swift.task.impl.TaskEvent;
import com.fr.swift.task.impl.WorkerTaskPool;
import com.fr.swift.util.Crasher;
import com.fr.swift.util.Util;

import java.util.Map;
import java.util.Map.Entry;

import static com.fr.swift.task.cube.CubeOperation.BUILD_TABLE;
import static com.fr.swift.task.cube.CubeOperation.INDEX_COLUMN;
import static com.fr.swift.task.cube.CubeOperation.INDEX_COLUMN_PATH;
import static com.fr.swift.task.cube.CubeOperation.INDEX_PATH;
import static com.fr.swift.task.cube.CubeOperation.INDEX_RELATION;
import static com.fr.swift.task.cube.CubeOperation.MERGE_COLUMN_DICT;
import static com.fr.swift.task.cube.CubeOperation.NULL;
import static com.fr.swift.task.cube.CubeOperation.TRANSPORT_TABLE;

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

        Operation op = task.key().operation();
        if (Util.in(op, TRANSPORT_TABLE)) {
            transportExec.add(task);
            return;
        }
        if (Util.in(op, INDEX_COLUMN, MERGE_COLUMN_DICT, INDEX_RELATION, INDEX_PATH, INDEX_COLUMN_PATH)) {
            indexExec.add(task);
            return;
        }
        if (Util.in(op, NULL, BUILD_TABLE)) {
            task.run();
            return;
        }
        Crasher.crash(String.format("cannot find right pool to execute %s", task));
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
