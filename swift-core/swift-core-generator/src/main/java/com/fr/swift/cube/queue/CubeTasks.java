package com.fr.swift.cube.queue;

import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.query.group.GroupType;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.RelationSource;
import com.fr.swift.task.SchedulerTask;
import com.fr.swift.task.TaskKey;
import com.fr.swift.task.cube.CubeOperation;
import com.fr.swift.task.cube.CubeTaskKey;
import com.fr.swift.task.impl.SchedulerTaskImpl;

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

    private static String newId(String name, String id) {
        return name.equals(id) ? String.format("%s", id) : String.format("%s@%s", name, id);
    }

    public static String newTableName(DataSource ds) throws SwiftMetaDataException {
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
        return new CubeTaskKey(round, newTableName(ds), CubeOperation.BUILD_TABLE, ds.getMetadata().getTableName());
    }

    public static TaskKey newTableBuildEndTaskKey(int round, DataSource ds) throws SwiftMetaDataException {
        return new CubeTaskKey(round, "End of building table " + newTableName(ds), CubeOperation.BUILD_TABLE);
    }

    public static TaskKey newTransportTaskKey(int round, DataSource ds) throws SwiftMetaDataException {
        return new CubeTaskKey(round, newTableName(ds), CubeOperation.TRANSPORT_TABLE, ds.getMetadata().getTableName());
    }

    public static TaskKey newIndexColumnTaskKey(int round, DataSource ds, String columnName) throws SwiftMetaDataException {
        return new CubeTaskKey(round, newColumnName(ds, columnName), CubeOperation.INDEX_COLUMN, ds.getMetadata().getTableName() + "." + columnName);
    }

    public static TaskKey newIndexColumnTaskKey(int round, DataSource ds, String columnName, GroupType type) throws SwiftMetaDataException {
        return new CubeTaskKey(round, newColumnName(ds, columnName, type), CubeOperation.INDEX_COLUMN, ds.getMetadata().getTableName() + "." + columnName);
    }

    public static TaskKey newMergeColumnDictTaskKey(int round, DataSource ds, String columnName) throws SwiftMetaDataException {
        return new CubeTaskKey(round, newColumnName(ds, columnName), CubeOperation.MERGE_COLUMN_DICT, ds.getMetadata().getTableName() + "." + columnName);
    }

    public static TaskKey newMergeColumnDictTaskKey(int round, DataSource ds, String columnName, GroupType type) throws SwiftMetaDataException {
        return new CubeTaskKey(round, newColumnName(ds, columnName, type), CubeOperation.MERGE_COLUMN_DICT, ds.getMetadata().getTableName() + "." + columnName);
    }

    public static TaskKey newIndexRelationTaskKey(int round, RelationSource relation) {
        return new CubeTaskKey(round, newRelationName(relation), CubeOperation.INDEX_RELATION);
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