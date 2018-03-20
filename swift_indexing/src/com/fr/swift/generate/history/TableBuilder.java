package com.fr.swift.generate.history;

import com.fr.swift.cube.task.LocalTask;
import com.fr.swift.cube.task.Task;
import com.fr.swift.cube.task.Task.Status;
import com.fr.swift.cube.task.TaskStatusChangeListener;
import com.fr.swift.cube.task.impl.BaseWorker;
import com.fr.swift.cube.task.impl.CubeTaskKey;
import com.fr.swift.cube.task.impl.LocalTaskGroup;
import com.fr.swift.cube.task.impl.LocalTaskImpl;
import com.fr.swift.cube.task.impl.Operation;
import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.generate.BaseTableBuilder;
import com.fr.swift.generate.history.index.ColumnIndexer;
import com.fr.swift.generate.history.transport.TableTransporter;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.SwiftMetaData;

import static com.fr.swift.cube.task.Task.Result.SUCCEEDED;

/**
 * This class created on 2017-12-28 10:53:49
 *
 * @author Lucifer
 * @description 完整表进行取数、索引。
 * @since Advanced FineBI Analysis 1.0
 */
public class TableBuilder extends BaseTableBuilder {
    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(TableBuilder.class);

    public TableBuilder(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected void init() throws SwiftMetaDataException {
        final SwiftMetaData meta = dataSource.getMetadata();
        // transport worker
        final TableTransporter transporter = new TableTransporter(dataSource);

        final LocalTask transportTask = new LocalTaskImpl(newPartStartTaskKey(dataSource));
        transportTask.setWorker(transporter);

        final LocalTask end = new LocalTaskImpl(newPartEndTaskKey(dataSource));
        end.setWorker(BaseWorker.nullWorker());

        //监听表取数任务，完成后添加字段索引任务。
        transportTask.addStatusChangeListener(new TaskStatusChangeListener() {
            @Override
            public void onChange(Status prev, Status now) {
                if (now == Status.DONE && transportTask.result() == SUCCEEDED) {
                    try {
                        for (String indexField : transporter.getIndexFieldsList()) {
                            ColumnIndexer<?> indexer = new ColumnIndexer(dataSource, new ColumnKey(indexField));

                            LocalTask indexTask = new LocalTaskImpl(new CubeTaskKey(
                                    String.format("%s@%s.%s", meta.getTableName(),
                                            dataSource.getSourceKey().getId(),
                                            indexField),
                                    Operation.INDEX_COLUMN));
                            indexTask.setWorker(indexer);
                            // link task
                            transportTask.addNext(indexTask);
                            indexTask.addNext(end);
                        }
                    } catch (Exception e) {
                        LOGGER.error("Table :" + dataSource.getSourceKey() + " add field index task failed!", e);
                    }
                }
            }
        });

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

    private static CubeTaskKey newPartStartTaskKey(DataSource ds) throws SwiftMetaDataException {
        return new CubeTaskKey("part start of " + ds.getMetadata().getTableName() + "@" + ds.getSourceKey().getId(), Operation.BUILD_TABLE);
    }

    private static CubeTaskKey newPartEndTaskKey(DataSource ds) throws SwiftMetaDataException {
        return new CubeTaskKey("part end of " + ds.getMetadata().getTableName() + "@" + ds.getSourceKey().getId(), Operation.BUILD_TABLE);
    }

    @Override
    public void work() {
        try {
            init();
            taskGroup.run();
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e);
            workOver(Task.Result.FAILED);
        }
    }
}