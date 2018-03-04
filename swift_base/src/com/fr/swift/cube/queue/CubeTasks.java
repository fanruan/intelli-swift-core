package com.fr.swift.cube.queue;

import com.fr.swift.context.SwiftContext;
import com.fr.swift.cube.task.SchedulerTask;
import com.fr.swift.cube.task.TaskKey;
import com.fr.swift.cube.task.impl.CubeTaskKey;
import com.fr.swift.cube.task.impl.Operation;
import com.fr.swift.cube.task.impl.SchedulerTaskImpl;
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
    public static SchedulerTask newTableTask(DataSource ds) throws SwiftMetaDataException {
        return new SchedulerTaskImpl(newTaskKey(ds));
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

    public static SchedulerTask newRelationTask(RelationSource relation/*, DataSource primary, DataSource foreign*/) {
        return new SchedulerTaskImpl(newTaskKey(relation));
    }

    private static boolean isReadable(DataSource dataSource) {
        return SwiftContext.getInstance().getSegmentProvider().isSegmentsExist(dataSource.getSourceKey());
    }

    public static TaskKey newTaskKey(DataSource ds) throws SwiftMetaDataException {
        return new CubeTaskKey(ds.getMetadata().getTableName() + "@" + ds.getSourceKey().getId(),
                Operation.BUILD_TABLE);
    }

    public static TaskKey newTaskKey(RelationSource relation) {
        return new CubeTaskKey(relation + "@" + relation.getSourceKey().getId(),
                Operation.INDEX_RELATION);
    }

    public static SchedulerTask newStartTask() {
        return new SchedulerTaskImpl(new CubeTaskKey("start of all@" + Long.toHexString(System.nanoTime())));
    }

    public static SchedulerTask newEndTask() {
        return new SchedulerTaskImpl(new CubeTaskKey("end of all@" + Long.toHexString(System.nanoTime())));
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