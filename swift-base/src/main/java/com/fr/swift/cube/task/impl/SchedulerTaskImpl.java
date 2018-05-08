package com.fr.swift.cube.task.impl;

import com.fr.swift.cube.task.PrevOneDoneHandler;
import com.fr.swift.cube.task.SchedulerTask;
import com.fr.swift.cube.task.TaskKey;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.service.SwiftServiceEvent;
import com.fr.swift.service.listener.EventType;
import com.fr.swift.service.listener.SwiftServiceListenerManager;

import java.util.ArrayList;
import java.util.List;

/**
 * @author anchore
 * @date 2017/12/8
 */
public class SchedulerTaskImpl extends BaseTask implements SchedulerTask {
    private List<TaskKey> prevTasks = new ArrayList<TaskKey>();

    private List<TaskKey> nextTasks = new ArrayList<TaskKey>();

    private final PrevOneDoneHandler prevOneDoneHandler;

    public SchedulerTaskImpl(TaskKey key) {
        this(key, new DefaultHandler());
    }

    public SchedulerTaskImpl(final TaskKey key, PrevOneDoneHandler prevOneDoneHandler) {
        super(key);
        this.prevOneDoneHandler = prevOneDoneHandler;

        SchedulerTaskPool.getInstance().add(this);
    }

    @Override
    public void onPrevOneDone(TaskKey prevDoneOne) {
        synchronized (prevOneDoneHandler) {
            prevOneDoneHandler.handle(key, prevDoneOne);
        }
    }

    @Override
    public void cancel() {
        try {
            SwiftServiceListenerManager.getInstance().triggerEvent(new SwiftServiceEvent<TaskKey>() {
                @Override
                public TaskKey getContent() {
                    return key;
                }

                @Override
                public EventType getEventType() {
                    return EventType.CANCEL_TASK;
                }
            });
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e);
        }
    }

    @Override
    public void triggerRun() {
        setStatus(Status.RUNNABLE);
        try {
            SwiftServiceListenerManager.getInstance().triggerEvent(new SwiftServiceEvent<TaskKey>() {
                @Override
                public TaskKey getContent() {
                    return key;
                }

                @Override
                public EventType getEventType() {
                    return EventType.RUN_TASK;
                }
            });
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e);
        }
    }

    @Override
    public void onDone(Result result) {
        synchronized (this) {
            if (status == Status.DONE) {
                return;
            }
            this.result = result;
            setStatus(Status.DONE);
        }

        SwiftLoggers.getLogger().info(String.format("%s %s", key, result));

        for (TaskKey next : nextTasks) {
            from(next).onPrevOneDone(key);
        }
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
        return prevTasks;
    }

    @Override
    public List<TaskKey> nextAll() {
        return nextTasks;
    }

    private static SchedulerTask from(TaskKey taskKey) {
        return SchedulerTaskPool.getInstance().get(taskKey);
    }
}