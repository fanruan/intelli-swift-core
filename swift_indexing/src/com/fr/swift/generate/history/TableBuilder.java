package com.fr.swift.generate.history;

import com.fr.swift.cube.task.LocalTask;
import com.fr.swift.cube.task.Task.Result;
import com.fr.swift.cube.task.Task.Status;
import com.fr.swift.cube.task.TaskStatusChangeListener;
import com.fr.swift.cube.task.impl.BaseWorker;
import com.fr.swift.cube.task.impl.CubeTaskKey;
import com.fr.swift.cube.task.impl.LocalTaskGroup;
import com.fr.swift.cube.task.impl.LocalTaskImpl;
import com.fr.swift.cube.task.impl.Operation;
import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.SwiftMetaData;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * This class created on 2017-12-28 10:53:49
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public class TableBuilder extends BaseWorker {
    private DataSource dataSource;

    private LocalTaskGroup taskGroup;

    public TableBuilder(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private void init() throws SwiftMetaDataException {
        SwiftMetaData meta = dataSource.getMetadata();
        // transport worker
        DataTransporter transporter = new DataTransporter(dataSource);

        LocalTask transportTask = new LocalTaskImpl(new CubeTaskKey(meta.getTableName(), Operation.TRANSPORT_TABLE));
        transportTask.setWorker(transporter);

        LocalTask end = new LocalTaskImpl(new CubeTaskKey("end of " + meta.getTableName()));
        end.setWorker(BaseWorker.nullWorker());

        // column index worker
        for (int i = 1; i <= meta.getColumnCount(); i++) {
            ColumnIndexer<?> indexer = new ColumnIndexer(dataSource, new ColumnKey(meta.getColumnName(i)));

            LocalTask indexTask = new LocalTaskImpl(new CubeTaskKey(
                    String.format("%s.%s", meta.getTableName(), meta.getColumnName(i)),
                    Operation.INDEX_COLUMN));
            indexTask.setWorker(indexer);
            // link task
            transportTask.addNext(indexTask);
            indexTask.addNext(end);
        }

        taskGroup = new LocalTaskGroup(new CubeTaskKey(meta.getTableName(), Operation.NULL));
        taskGroup.wrap(transportTask, end);

        taskGroup.addStatusChangeListener(new TaskStatusChangeListener() {
            @Override
            public void onChange(Status prev, Status now) {
                if (now == Status.DONE) {
                    workOver(taskGroup.result());
                }
            }
        });
    }

    @Override
    public void work() {
        try {
            init();
            taskGroup.run();
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e);
            workOver(Result.FAILED);
        }
    }

    public static void main(String[] args) {
        System.out.println(new AtomicInteger(Integer.MAX_VALUE).incrementAndGet());

    }
}
