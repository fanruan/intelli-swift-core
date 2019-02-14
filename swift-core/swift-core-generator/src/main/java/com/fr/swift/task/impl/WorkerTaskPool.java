package com.fr.swift.task.impl;

import com.fr.swift.event.SwiftEventDispatcher;
import com.fr.swift.event.SwiftEventListener;
import com.fr.swift.task.TaskKey;
import com.fr.swift.task.WorkerTask;
import com.fr.swift.util.function.BinaryFunction;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author anchore
 * @date 2017/12/29
 */
public class WorkerTaskPool extends BaseTaskPool<WorkerTask> {
    private static final WorkerTaskPool INSTANCE = new WorkerTaskPool();
    private BinaryFunction<TaskKey, Object, WorkerTask> taskGenerator;

    private WorkerTaskPool() {
        tasks = new ConcurrentHashMap<TaskKey, WorkerTask>();
    }

    public static WorkerTaskPool getInstance() {
        return INSTANCE;
    }

    public void setTaskGenerator(BinaryFunction<TaskKey, Object, WorkerTask> taskGenerator) {
        this.taskGenerator = taskGenerator;
    }

    public WorkerTask generate(TaskKey taskKey, Object data) {
        WorkerTask task = taskGenerator.apply(taskKey, data);
        add(task);
        return task;
    }

    public void initListener() {
        SwiftEventDispatcher.listen(TaskEvent.LOCAL_CANCEL, new SwiftEventListener<TaskKey>() {
            @Override
            public void on(TaskKey key) {
                WorkerTask workerTask = get(key);
                synchronized (workerTask) {
                    workerTask.onCancel();
                }
            }
        });
    }
}