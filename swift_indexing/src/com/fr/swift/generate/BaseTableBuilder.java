package com.fr.swift.generate;

import com.fr.swift.cube.queue.CubeTasks;
import com.fr.swift.cube.task.LocalTask;
import com.fr.swift.cube.task.Task;
import com.fr.swift.cube.task.TaskStatusChangeListener;
import com.fr.swift.cube.task.impl.BaseWorker;
import com.fr.swift.cube.task.impl.CubeTaskKey;
import com.fr.swift.cube.task.impl.LocalTaskGroup;
import com.fr.swift.cube.task.impl.LocalTaskImpl;
import com.fr.swift.cube.task.impl.Operation;
import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.generate.history.index.ColumnDictMerger;
import com.fr.swift.generate.history.index.ColumnIndexer;
import com.fr.swift.generate.history.index.SubDateColumnDictMerger;
import com.fr.swift.generate.history.index.SubDateColumnIndexer;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.manager.LocalSegmentProvider;
import com.fr.swift.query.group.GroupType;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.column.impl.SubDateColumn;
import com.fr.swift.segment.operator.SwiftColumnDictMerger;
import com.fr.swift.segment.operator.SwiftColumnIndexer;
import com.fr.swift.source.ColumnTypeConstants;
import com.fr.swift.source.ColumnTypeUtils;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;

import java.util.ArrayList;
import java.util.List;

import static com.fr.swift.cube.task.Task.Result.SUCCEEDED;

/**
 * @Author: Lucifer
 * @Description:
 * @Date: Created in 2018-3-6
 * <p>
 * 全局字典和字段索引任务分开
 * 流程:
 * tableTransport -> index -> merge -> subIndex -> subMerge
 */
public abstract class BaseTableBuilder extends BaseWorker implements SwiftTableBuilder {

    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(BaseTableBuilder.class);

    protected DataSource dataSource;

    protected LocalTaskGroup taskGroup;

    protected Transporter transporter;

    private boolean isRealtime = false;

    public BaseTableBuilder(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public BaseTableBuilder(DataSource dataSource, boolean isRealtime) {
        this(dataSource);
        this.isRealtime = isRealtime;
    }

    protected void init() throws SwiftMetaDataException {
        final SwiftMetaData meta = dataSource.getMetadata();
        // transport worker
        final LocalTask transportTask = new LocalTaskImpl(CubeTasks.newPartStartTaskKey(dataSource));
        transportTask.setWorker(transporter);

        final LocalTask end = new LocalTaskImpl(CubeTasks.newPartEndTaskKey(dataSource));
        end.setWorker(BaseWorker.nullWorker());

        //监听表取数任务，完成后添加字段索引任务。
        transportTask.addStatusChangeListener(new TaskStatusChangeListener() {
            @Override
            public void onChange(Task.Status prev, Task.Status now) {
                if (now == Task.Status.DONE && transportTask.result() == SUCCEEDED) {
                    try {
                        initColumnIndexTask();
                    } catch (Exception e) {
                        LOGGER.error("Table :" + dataSource.getSourceKey() + " add field index task failed!", e);
                    }
                }
            }

            private void initColumnIndexTask() throws SwiftMetaDataException {
                if (transporter.getIndexFieldsList().isEmpty()) {
                    transportTask.addNext(end);
                }
                List<Segment> allSegments = LocalSegmentProvider.getInstance().getSegment(dataSource.getSourceKey());
                List<Segment> indexSegments = new ArrayList<Segment>();
                if (isRealtime) {
                    for (Segment segment : allSegments) {
                        if (!segment.isHistory()) {
                            indexSegments.add(segment);
                        }
                    }
                } else {
                    indexSegments.addAll(allSegments);
                }

                for (String indexField : transporter.getIndexFieldsList()) {

                    SwiftColumnIndexer indexer = new ColumnIndexer(dataSource, new ColumnKey(indexField), indexSegments);
                    LocalTask indexTask = new LocalTaskImpl(new CubeTaskKey(
                            String.format("%s@%s.%s", meta.getTableName(), dataSource.getSourceKey(), indexField),
                            Operation.INDEX_COLUMN));
                    indexTask.setWorker(indexer);

                    SwiftColumnDictMerger merger = new ColumnDictMerger(dataSource, new ColumnKey(indexField), allSegments);
                    LocalTask mergeTask = new LocalTaskImpl(new CubeTaskKey(
                            String.format("%s@%s.%s", meta.getTableName(), dataSource.getSourceKey(), indexField),
                            Operation.MERGE_COLUMN_DICT));
                    mergeTask.setWorker(merger);
                    // link task
                    transportTask.addNext(indexTask);
                    indexTask.addNext(mergeTask);

                    initSubColumnTaskIfHas(mergeTask, indexField, allSegments, indexSegments);
                }
            }

            private void initSubColumnTaskIfHas(LocalTask mergeTask, String indexField, List<Segment> allSegments, List<Segment> indexSegments) throws SwiftMetaDataException {
                if (!hasSubColumn(indexField)) {
                    mergeTask.addNext(end);
                    return;
                }
                for (GroupType groupType : SubDateColumn.TYPES_TO_GENERATE) {
                    LocalTask indexSubColumnTask = new LocalTaskImpl(new CubeTaskKey(
                            String.format("%s@%s.%s.%s", meta.getTableName(), dataSource.getSourceKey(), indexField, groupType),
                            Operation.INDEX_COLUMN));
                    indexSubColumnTask.setWorker(new SubDateColumnIndexer(dataSource, new ColumnKey(indexField), groupType, indexSegments));

                    LocalTask mergeSubColumnTask = new LocalTaskImpl(new CubeTaskKey(
                            String.format("%s@%s.%s.%s", meta.getTableName(), dataSource.getSourceKey(), indexField, groupType),
                            Operation.MERGE_COLUMN_DICT));
                    mergeSubColumnTask.setWorker(new SubDateColumnDictMerger(dataSource, new ColumnKey(indexField), groupType, allSegments));

                    mergeTask.addNext(indexSubColumnTask);
                    indexSubColumnTask.addNext(mergeSubColumnTask);
                    mergeSubColumnTask.addNext(end);
                }
            }

            private boolean hasSubColumn(String indexField) throws SwiftMetaDataException {
                SwiftMetaDataColumn columnMeta = meta.getColumn(indexField);
                return ColumnTypeUtils.getColumnType(columnMeta) == ColumnTypeConstants.ColumnType.DATE;
            }
        });

        taskGroup = new LocalTaskGroup(new CubeTaskKey(meta.getTableName(), Operation.NULL));
        taskGroup.wrap(transportTask, end);

        taskGroup.addStatusChangeListener(new TaskStatusChangeListener() {
            @Override
            public void onChange(Task.Status prev, Task.Status now) {
                if (now == Task.Status.DONE) {
                    workOver(taskGroup.result());
                }
            }
        });
    }

    @Override
    public void build() throws Exception {
        init();
        taskGroup.run();
    }

    @Override
    public void work() {
        try {
            build();
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e);
            workOver(Task.Result.FAILED);
        }
    }

}
