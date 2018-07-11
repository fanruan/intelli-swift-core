package com.fr.swift.task.impl;

import com.fr.event.Event;
import com.fr.event.EventDispatcher;
import com.fr.event.Listener;
import com.fr.swift.task.TaskKey;
import com.fr.swift.task.WorkerTask;
import com.fr.swift.util.function.BiFunction;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author anchore
 * @date 2017/12/29
 */
public class WorkerTaskPool extends BaseTaskPool<WorkerTask> {
    private BiFunction<TaskKey, Object, WorkerTask> taskGenerator;

    public void setTaskGenerator(BiFunction<TaskKey, Object, WorkerTask> taskGenerator) {
        this.taskGenerator = taskGenerator;
    }

    public WorkerTask generate(TaskKey taskKey, Object data) {
        WorkerTask task = taskGenerator.apply(taskKey, data);
        add(task);
        return task;
    }

    public void initListener() {
        EventDispatcher.listen(TaskEvent.LOCAL_CANCEL, new Listener<TaskKey>() {
            @Override
            public void on(Event event, TaskKey key) {
                WorkerTask workerTask = get(key);
                synchronized (workerTask) {
                    workerTask.onCancel();
                }
            }
        });
    }

    private static final WorkerTaskPool INSTANCE = new WorkerTaskPool();

    private WorkerTaskPool() {
        tasks = new ConcurrentHashMap<TaskKey, WorkerTask>();
    }

    public static WorkerTaskPool getInstance() {
        return INSTANCE;
    }
}