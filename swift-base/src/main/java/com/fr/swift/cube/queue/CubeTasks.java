package com.fr.swift.cube.queue;

import com.fr.swift.context.SwiftContext;
import com.fr.swift.cube.task.SchedulerTask;
import com.fr.swift.cube.task.TaskKey;
import com.fr.swift.cube.task.impl.CubeTaskKey;
import com.fr.swift.cube.task.impl.Operation;
import com.fr.swift.cube.task.impl.SchedulerTaskImpl;
import com.fr.swift.exception.SwiftServiceException;
import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.segment.SwiftSegmentManager;
import com.fr.swift.service.SwiftServiceEvent;
import com.fr.swift.service.listener.EventType;
import com.fr.swift.service.listener.SwiftServiceListenerManager;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.EtlDataSource;
import com.fr.swift.source.RelationSource;
import com.fr.swift.structure.Pair;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author anchore
 * @date 2018/2/7
 */
public class CubeTasks {
    /**
     * 第几次更新
     */
    private static final AtomicInteger COUNTER = new AtomicInteger(0);

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
    @Deprecated
    static SchedulerTask newEtlTask(EtlDataSource etl, SchedulerTask prevTask) throws SwiftMetaDataException {
        List<SchedulerTask> dependTasks = new ArrayList<SchedulerTask>();

        for (DataSource dataSource : etl.getBasedSources()) {
            if (isReadable(dataSource)) {
                continue;
            }

            SchedulerTask task;
            if (dataSource instanceof EtlDataSource) {
                task = newEtlTask(((EtlDataSource) dataSource), prevTask);
            } else {
                task = new SchedulerTaskImpl(newBuildTableTaskKey(dataSource));
                prevTask.addNext(task);
            }

            dependTasks.add(task);
        }

        SchedulerTask etlTask = new SchedulerTaskImpl(newBuildTableTaskKey(etl));
        for (SchedulerTask dependTask : dependTasks) {
            dependTask.addNext(etlTask);
        }
        return etlTask;
    }

    @Deprecated
    private static boolean isReadable(DataSource dataSource) {
        return SwiftContext.getInstance().getBean(SwiftSegmentManager.class).isSegmentsExist(dataSource.getSourceKey());
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

    private static String newId(String name, String id) {
        return name.equals(id) ?
                String.format("%s", id) :
                String.format("%s@%s", name, id);
    }

    private static String newTableName(DataSource ds) throws SwiftMetaDataException {
        return newId(ds.getMetadata().getTableName(), ds.getSourceKey().getId());
    }

    private static String newColumnName(DataSource ds, String columnName) throws SwiftMetaDataException {
        return String.format("%s.%s", newTableName(ds), columnName);
    }

    private static String newRelationName(RelationSource rs) {
        return newId(rs.toString(), rs.getSourceKey().getId());
    }

    public static TaskKey newBuildTableTaskKey(DataSource ds) throws SwiftMetaDataException {
        return new CubeTaskKey(newTableName(ds), Operation.BUILD_TABLE, ds.getMetadata().getTableName());
    }

    public static TaskKey newBuildTableTaskKey(DataSource ds, int round) throws SwiftMetaDataException {
        return new CubeTaskKey(round, newTableName(ds), Operation.BUILD_TABLE, ds.getMetadata().getTableName());
    }

    public static TaskKey newTableBuildEndTaskKey(DataSource ds) throws SwiftMetaDataException {
        return new CubeTaskKey("End of building table " + newTableName(ds), Operation.BUILD_TABLE);
    }

    public static TaskKey newTransportTaskKey(DataSource ds) throws SwiftMetaDataException {
        return new CubeTaskKey(newTableName(ds), Operation.TRANSPORT_TABLE, ds.getMetadata().getTableName());
    }

    public static TaskKey newIndexColumnTaskKey(DataSource ds, String columnName) throws SwiftMetaDataException {
        return new CubeTaskKey(newColumnName(ds, columnName), Operation.INDEX_COLUMN, ds.getMetadata().getTableName() + "." + columnName);
    }

    public static TaskKey newMergeColumnDictTaskKey(DataSource ds, String columnName) throws SwiftMetaDataException {
        return new CubeTaskKey(newColumnName(ds, columnName), Operation.MERGE_COLUMN_DICT, ds.getMetadata().getTableName() + "." + columnName);
    }

    public static TaskKey newIndexRelationTaskKey(RelationSource relation) {
        return new CubeTaskKey(newRelationName(relation), Operation.INDEX_RELATION);
    }

    public static SchedulerTask newStartTask() {
        return new SchedulerTaskImpl(new CubeTaskKey("Start of building Cube"));
    }

    public static SchedulerTask newEndTask() {
        return new SchedulerTaskImpl(new CubeTaskKey("End of building Cube"));
    }

    public static int nextRound() {
        return COUNTER.getAndIncrement();
    }

    public static int getCurrentRound() {
        return COUNTER.get();
    }
}