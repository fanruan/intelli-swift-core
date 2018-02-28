package com.fr.swift.cube.queue;

import com.fr.swift.context.SwiftContext;
import com.fr.swift.cube.task.SchedulerTask;
import com.fr.swift.cube.task.TaskKey;
import com.fr.swift.cube.task.impl.CubeTaskKey;
import com.fr.swift.cube.task.impl.Operation;
import com.fr.swift.cube.task.impl.SchedulerTaskImpl;
import com.fr.swift.cube.task.impl.SchedulerTaskPool;
import com.fr.swift.exception.SwiftServiceException;
import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.service.SwiftServiceEvent;
import com.fr.swift.service.listener.EventType;
import com.fr.swift.service.listener.SwiftServiceListenerManager;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.ETLDataSource;
import com.fr.swift.source.RelationSource;
import com.fr.swift.structure.Pair;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author anchore
 * @date 2018/2/7
 */
public class CubeTasks {
    static SchedulerTask newTableTask(DataSource ds) throws SwiftMetaDataException {
        return new SchedulerTaskImpl(new CubeTaskKey(CubeTasks.newTaskName(ds), Operation.BUILD_TABLE));
    }

    // newEtlTask(etl, prevTask) return e
    //                           prevTask
    //                  ________/
    //                 /   /   /
    // a   b     =>   a   b   /
    //  \ /            \ /   /
    //   c   d          c   d
    //    \ /            \ /
    //     e              e

    /**
     * @param etl      etl表
     * @param prevTask 整个etl生成任务的前置
     * @return etl task 如上图，返回的是E
     * @throws SwiftMetaDataException 异常
     */
    static SchedulerTask newEtlTask(ETLDataSource etl, SchedulerTask prevTask) throws SwiftMetaDataException {
        List<SchedulerTask> dependTasks = new ArrayList<SchedulerTask>();

        for (DataSource dataSource : etl.getBasedSources()) {
            if (isReadable(dataSource)) {
                continue;
            }

            SchedulerTask task;
            if (dataSource instanceof ETLDataSource) {
                task = newEtlTask(((ETLDataSource) dataSource), prevTask);
            } else {
                task = newTableTask(dataSource);
                prevTask.addNext(task);
            }

            dependTasks.add(task);
        }

        SchedulerTask etlTask = newTableTask(etl);
        for (SchedulerTask dependTask : dependTasks) {
            dependTask.addNext(etlTask);
        }
        return etlTask;
    }

    static SchedulerTask newRelationTask(RelationSource relation, DataSource primary, DataSource foreign) throws SwiftMetaDataException {
        SchedulerTask relationTask = new SchedulerTaskImpl(new CubeTaskKey(newTaskName(relation), Operation.INDEX_RELATION));

        TaskKey primaryTaskKey = new CubeTaskKey(newTaskName(primary), Operation.BUILD_TABLE),
                foreignTaskKey = new CubeTaskKey(newTaskName(foreign), Operation.BUILD_TABLE);

        SchedulerTaskPool pool = SchedulerTaskPool.getInstance();
        // 这里默认任务已经生成 即先生成所有table的任务，然后生成relation任务
        pool.get(primaryTaskKey).addNext(relationTask);
        pool.get(foreignTaskKey).addNext(relationTask);

        return relationTask;
    }

    private static boolean isReadable(DataSource dataSource) {
        return SwiftContext.getInstance().getSegmentProvider().isSegmentsExist(dataSource.getSourceKey());
    }

    static String newTaskName(DataSource ds) throws SwiftMetaDataException {
        return ds.getMetadata().getTableName() + "@" + ds.getSourceKey().getId();
    }

    private static String newTaskName(RelationSource relation) {
        return relation + "@" + relation.getSourceKey().getId();
    }

    static SchedulerTask newStartTask() {
        return new SchedulerTaskImpl(new CubeTaskKey("start of all @ " + Long.toHexString(System.nanoTime())));
    }

    static SchedulerTask newEndTask() {
        return new SchedulerTaskImpl(new CubeTaskKey("end of all @ " + Long.toHexString(System.nanoTime())));
    }

    public static void sendTasks(final Collection<Pair<TaskKey, Object>> tasks) throws SwiftServiceException {
        SwiftServiceListenerManager.getInstance().triggerEvent(new SwiftServiceEvent<Collection<Pair<TaskKey, Object>>>() {
            @Override
            public Collection<Pair<TaskKey, Object>> getContent() {
                return tasks;
            }

            @Override
            public EventType getEventType() {
                return EventType.INIT_TASK;
            }
        });
    }
}