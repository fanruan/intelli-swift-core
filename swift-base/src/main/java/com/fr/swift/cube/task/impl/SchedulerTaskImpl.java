package com.fr.swift.cube.task.impl;

import com.fr.swift.cube.task.SchedulerTask;
import com.fr.swift.cube.task.TaskKey;
import com.fr.swift.cube.task.TaskResult;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.service.SwiftServiceEvent;
import com.fr.swift.service.listener.EventType;
import com.fr.swift.service.listener.SwiftServiceListenerManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author anchore
 * @date 2017/12/8
 */
public class SchedulerTaskImpl extends BaseTask implements SchedulerTask {
    private List<TaskKey> prevTasks = new ArrayList<TaskKey>();

    private List<TaskKey> nextTasks = new ArrayList<TaskKey>();

    public SchedulerTaskImpl(TaskKey key) {
        super(key);

        SchedulerTaskPool.getInstance().add(this);
    }

    @Override
    public void cancel() {
        triggerEvent(EventType.CANCEL_TASK);
    }

    @Override
    public void triggerRun() {
        synchronized (this) {
            if (status.order() > Status.RUNNABLE.order()) {
                return;
            }
        }
        setStatus(Status.RUNNABLE);
        triggerEvent(EventType.RUN_TASK);
    }

    @Override
    public void onDone(TaskResult result) {
        synchronized (this) {
            if (status == Status.DONE) {
                return;
            }
        }
        this.result = result;
        setStatus(Status.DONE);
        end = System.currentTimeMillis();
        SwiftLoggers.getLogger().info(String.format("%s %s", key, result));

        SchedulerTaskTomb.getTomb().add(this);
    }


    @Override
    public void addPrev(TaskKey prevKey) {
        if (key.equals(prevKey)) {
            return;
        }
        if (!prevTasks.contains(prevKey)) {
            prevTasks.add(prevKey);
        }
    }

    @Override
    public void addPrev(SchedulerTask prev) {
        addPrev(prev.key());
    }

    @Override
    public void addNext(TaskKey nextKey) {
        if (key.equals(nextKey)) {
            return;
        }
        if (!nextTasks.contains(nextKey)) {
            nextTasks.add(nextKey);
            // 简化操作，顺便把前置也加了
            from(nextKey).addPrev(key);
        }
    }

    @Override
    public void addNext(SchedulerTask next) {
        addNext(next.key());
    }

    @Override
    public List<TaskKey> prevAll() {
        return Collections.unmodifiableList(prevTasks);
    }

    @Override
    public List<TaskKey> nextAll() {
        return Collections.unmodifiableList(nextTasks);
    }

    private static SchedulerTask from(TaskKey taskKey) {
        return SchedulerTaskPool.getInstance().get(taskKey);
    }

    private void triggerEvent(final EventType eventType) {
        try {
            SwiftServiceListenerManager.getInstance().triggerEvent(new SwiftServiceEvent<TaskKey>() {
                @Override
                public TaskKey getContent() {
                    return key;
                }

                @Override
                public EventType getEventType() {
                    return eventType;
                }
            });
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e);
        }
    }
}