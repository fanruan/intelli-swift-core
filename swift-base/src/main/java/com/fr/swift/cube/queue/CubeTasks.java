package com.fr.swift.cube.queue;

import com.fr.swift.cube.task.SchedulerTask;
import com.fr.swift.cube.task.TaskKey;
import com.fr.swift.cube.task.impl.CubeTaskKey;
import com.fr.swift.cube.task.impl.Operation;
import com.fr.swift.cube.task.impl.SchedulerTaskImpl;
import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.query.group.GroupType;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.RelationSource;
import com.fr.swift.structure.Pair;

import java.util.Collection;
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

//    /**
//     * @param etl      etl表
//     * @param prevTask 整个etl生成任务的前置
//     * @return etl task 如上图，返回的是E
//     * @throws SwiftMetaDataException 异常
//     */
//    @Deprecated
//    static SchedulerTask newEtlTask(EtlDataSource etl, SchedulerTask prevTask) throws SwiftMetaDataException {
//        List<SchedulerTask> dependTasks = new ArrayList<SchedulerTask>();
//
//        for (DataSource dataSource : etl.getBasedSources()) {
//            if (isReadable(dataSource)) {
//                continue;
//            }
//
//            SchedulerTask task;
//            if (dataSource instanceof EtlDataSource) {
//                task = newEtlTask(((EtlDataSource) dataSource), prevTask);
//            } else {
//                task = new SchedulerTaskImpl(newBuildTableTaskKey(dataSource));
//                prevTask.addNext(task);
//            }
//
//            dependTasks.add(task);
//        }
//
//        SchedulerTask etlTask = new SchedulerTaskImpl(newBuildTableTaskKey(etl));
//        for (SchedulerTask dependTask : dependTasks) {
//            dependTask.addNext(etlTask);
//        }
//        return etlTask;
//    }
//
//    @Deprecated
//    private static boolean isReadable(DataSource dataSource) {
//        return SwiftContext.getInstance().getBean(SwiftSegmentManager.class).isSegmentsExist(dataSource.getSourceKey());
//    }

    @Deprecated
    public static void sendTasks(final Collection<Pair<TaskKey, Object>> tasks) {
//        SwiftServiceListenerManager.getInstance().triggerEvent(new SwiftServiceEvent<Collection<Pair<TaskKey, Object>>>() {
//            @Override
//            public Collection<Pair<TaskKey, Object>> getContent() {
//                return tasks;
//            }
//
//            @Override
//            public EventType getEventType() {
//                return EventType.INIT_TASK;
//            }
//        });
    }

    private static String newId(String name, String id) {
        return name.equals(id) ? String.format("%s", id) : String.format("%s@%s", name, id);
    }

    private static String newTableName(DataSource ds) throws SwiftMetaDataException {
        return newId(ds.getMetadata().getTableName(), ds.getSourceKey().getId());
    }

    private static String newColumnName(DataSource ds, String columnName) throws SwiftMetaDataException {
        return String.format("%s.%s", newTableName(ds), columnName);
    }

    private static String newColumnName(DataSource ds, String columnName, GroupType type) throws SwiftMetaDataException {
        return String.format("%s.%s.%s", newTableName(ds), columnName, type);
    }

    private static String newRelationName(RelationSource rs) {
        return newId(rs.toString(), rs.getSourceKey().getId());
    }

    public static TaskKey newBuildTableTaskKey(int round, DataSource ds) throws SwiftMetaDataException {
        return new CubeTaskKey(round, newTableName(ds), Operation.BUILD_TABLE, ds.getMetadata().getTableName());
    }

    public static TaskKey newTableBuildEndTaskKey(int round, DataSource ds) throws SwiftMetaDataException {
        return new CubeTaskKey(round, "End of building table " + newTableName(ds), Operation.BUILD_TABLE);
    }

    public static TaskKey newTransportTaskKey(int round, DataSource ds) throws SwiftMetaDataException {
        return new CubeTaskKey(round, newTableName(ds), Operation.TRANSPORT_TABLE, ds.getMetadata().getTableName());
    }

    public static TaskKey newIndexColumnTaskKey(int round, DataSource ds, String columnName) throws SwiftMetaDataException {
        return new CubeTaskKey(round, newColumnName(ds, columnName), Operation.INDEX_COLUMN, ds.getMetadata().getTableName() + "." + columnName);
    }

    public static TaskKey newIndexColumnTaskKey(int round, DataSource ds, String columnName, GroupType type) throws SwiftMetaDataException {
        return new CubeTaskKey(round, newColumnName(ds, columnName, type), Operation.INDEX_COLUMN, ds.getMetadata().getTableName() + "." + columnName);
    }

    public static TaskKey newMergeColumnDictTaskKey(int round, DataSource ds, String columnName) throws SwiftMetaDataException {
        return new CubeTaskKey(round, newColumnName(ds, columnName), Operation.MERGE_COLUMN_DICT, ds.getMetadata().getTableName() + "." + columnName);
    }

    public static TaskKey newMergeColumnDictTaskKey(int round, DataSource ds, String columnName, GroupType type) throws SwiftMetaDataException {
        return new CubeTaskKey(round, newColumnName(ds, columnName, type), Operation.MERGE_COLUMN_DICT, ds.getMetadata().getTableName() + "." + columnName);
    }

    public static TaskKey newIndexRelationTaskKey(int round, RelationSource relation) {
        return new CubeTaskKey(round, newRelationName(relation), Operation.INDEX_RELATION);
    }

    public static SchedulerTask newStartTask(int round) {
        return new SchedulerTaskImpl(new CubeTaskKey(round, "Start of building Cube"));
    }

    public static SchedulerTask newEndTask(int round) {
        return new SchedulerTaskImpl(new CubeTaskKey(round, "End of building Cube"));
    }

    public static int nextRound() {
        return COUNTER.getAndIncrement();
    }

    public static int getCurrentRound() {
        return COUNTER.get();
    }
}