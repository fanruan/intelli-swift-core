package com.fr.swift.generate.history;

import com.fr.swift.cube.task.LocalTask;
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
import com.fr.swift.generate.history.index.SubDateColumnIndexer;
import com.fr.swift.generate.history.transport.TableTransporter;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.query.group.GroupType;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.column.impl.SubDateColumn;
import com.fr.swift.source.ColumnTypeConstants.ColumnType;
import com.fr.swift.source.ColumnTypeUtils;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;

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
                        initColumnIndexTask();
                    } catch (Exception e) {
                        LOGGER.error("Table :" + dataSource.getSourceKey() + " add field index task failed!", e);
                    }
                }
            }

            private void initColumnIndexTask() throws SwiftMetaDataException {
                for (String indexField : transporter.getIndexFieldsList()) {
                    ColumnIndexer<?> indexer = new ColumnIndexer(dataSource, new ColumnKey(indexField));

                    LocalTask indexTask = new LocalTaskImpl(new CubeTaskKey(
                            String.format("%s@%s.%s", meta.getTableName(), dataSource.getSourceKey(), indexField),
                            Operation.INDEX_COLUMN));
                    indexTask.setWorker(indexer);

                    // link task
                    transportTask.addNext(indexTask);
                    initSubColumnTaskIfHas(indexTask, indexField);
                }
            }

            private void initSubColumnTaskIfHas(LocalTask indexTask, String indexField) throws SwiftMetaDataException {
                if (!hasSubColumn(indexField)) {
                    indexTask.addNext(end);
                    return;
                }
                for (GroupType groupType : SubDateColumn.TYPES_TO_GENERATE) {
                    LocalTask indexSubColumnTask = new LocalTaskImpl(new CubeTaskKey(
                            String.format("%s@%s.%s.%s", meta.getTableName(), dataSource.getSourceKey(), indexField, groupType),
                            Operation.INDEX_COLUMN));
                    indexSubColumnTask.setWorker(new SubDateColumnIndexer(dataSource, new ColumnKey(indexField), groupType));

                    indexTask.addNext(indexSubColumnTask);
                    indexSubColumnTask.addNext(end);
                }
            }

            private boolean hasSubColumn(String indexField) throws SwiftMetaDataException {
                SwiftMetaDataColumn columnMeta = meta.getColumn(indexField);
                return ColumnTypeUtils.getColumnType(columnMeta) == ColumnType.DATE;
            }
        });

        // column index worker
//        for (int i = 1; i <= meta.getColumnCount(); i++) {
//            ColumnIndexer<?> indexer = new ColumnIndexer(dataSource, new ColumnKey(meta.getColumnName(i)));
//
//            LocalTask indexTask = new LocalTaskImpl(new CubeTaskKey(
//                    String.format("%s.%s", meta.getTableName(), meta.getColumnName(i)),
//                    Operation.INDEX_COLUMN));
//            indexTask.setWorker(indexer);
//            // link task
//            transportTask.addNext(indexTask);
//            indexTask.addNext(end);
//        }

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
}