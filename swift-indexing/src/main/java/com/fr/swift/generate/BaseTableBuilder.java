package com.fr.swift.generate;

import com.fr.swift.context.SwiftContext;
import com.fr.swift.cube.queue.CubeTasks;
import com.fr.swift.cube.task.LocalTask;
import com.fr.swift.cube.task.Task;
import com.fr.swift.cube.task.TaskResult.Type;
import com.fr.swift.cube.task.TaskStatusChangeListener;
import com.fr.swift.cube.task.impl.BaseWorker;
import com.fr.swift.cube.task.impl.LocalTaskGroup;
import com.fr.swift.cube.task.impl.LocalTaskImpl;
import com.fr.swift.cube.task.impl.TaskResultImpl;
import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.generate.history.index.ColumnDictMerger;
import com.fr.swift.generate.history.index.ColumnIndexer;
import com.fr.swift.generate.history.index.SubDateColumnDictMerger;
import com.fr.swift.generate.history.index.SubDateColumnIndexer;
import com.fr.swift.generate.segment.operator.merger.RealtimeMerger;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.manager.LocalSegmentProvider;
import com.fr.swift.query.group.GroupType;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.column.impl.SubDateColumn;
import com.fr.swift.segment.operator.Merger;
import com.fr.swift.source.ColumnTypeConstants;
import com.fr.swift.source.ColumnTypeUtils;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;
import com.fr.swift.util.DataSourceUtils;

import java.util.ArrayList;
import java.util.List;

import static com.fr.swift.cube.task.TaskResult.Type.SUCCEEDED;

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
        final LocalTask transportTask = new LocalTaskImpl(
                CubeTasks.newTransportTaskKey(dataSource), transporter);

        final LocalTask end = new LocalTaskImpl(
                CubeTasks.newTableBuildEndTaskKey(dataSource), BaseWorker.nullWorker());

        //监听表取数任务，完成后添加字段索引任务。
        transportTask.addStatusChangeListener(new TaskStatusChangeListener() {
            @Override
            public void onChange(Task.Status prev, Task.Status now) {
                if (now == Task.Status.DONE && transportTask.result().getType() == SUCCEEDED) {
                    try {
                        initColumnIndexTask();
                    } catch (Exception e) {
                        LOGGER.error("Table :'" + dataSource.getSourceKey() + "' add field index task failed!", e);
                    }
                }
            }

            private void initColumnIndexTask() throws Exception {
                if (transporter.getIndexFieldsList().isEmpty()) {
                    transportTask.addNext(end);
                }
                List<Segment> allSegments = SwiftContext.getInstance().getBean(LocalSegmentProvider.class).getSegment(dataSource.getSourceKey());
                List<Segment> indexSegments = new ArrayList<Segment>();
                if (isRealtime) {
//                    for (Segment segment : allSegments) {
//                        if (!segment.isHistory()) {
//                            indexSegments.add(segment);
//                        }
//                    }
                    //todo 目前增量更新临时每次都会合并到磁盘。要在考虑下合并的规则和场景
                    int hisSegCount = 0;
                    for (int i = 0; i < allSegments.size(); i++) {
                        if (allSegments.get(i).isHistory()) {
                            hisSegCount++;
                        }
                    }
                    if (hisSegCount != allSegments.size()) {
                        Merger realtimeMerger = new RealtimeMerger(dataSource.getSourceKey(), dataSource.getMetadata(), DataSourceUtils.getSwiftSourceKey(dataSource).getId());
                        realtimeMerger.merge();
                        allSegments = SwiftContext.getInstance().getBean(LocalSegmentProvider.class).getSegment(dataSource.getSourceKey());
                        for (int i = hisSegCount; i < allSegments.size(); i++) {
                            indexSegments.add(allSegments.get(i));
                        }
                    }
                    LOGGER.info("Update type:realtime! Table :'" + dataSource.getMetadata().getTableName() + "' will do realtime tranport!");
                } else {
                    indexSegments.addAll(allSegments);
                    LOGGER.info("Update type:history! Table :'" + dataSource.getMetadata().getTableName() + "' will do history tranport!");
                }

                for (String indexField : transporter.getIndexFieldsList()) {
                    LocalTask indexTask = new LocalTaskImpl(
                            CubeTasks.newIndexColumnTaskKey(dataSource, indexField),
                            new ColumnIndexer(dataSource, new ColumnKey(indexField), indexSegments));

                    LocalTask mergeTask = new LocalTaskImpl(
                            CubeTasks.newMergeColumnDictTaskKey(dataSource, indexField),
                            new ColumnDictMerger(dataSource, new ColumnKey(indexField), allSegments));
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
                    LocalTask indexSubColumnTask = new LocalTaskImpl(
                            CubeTasks.newIndexColumnTaskKey(dataSource, indexField + "." + groupType),
                            new SubDateColumnIndexer(dataSource, new ColumnKey(indexField), groupType, indexSegments));

                    LocalTask mergeSubColumnTask = new LocalTaskImpl(
                            CubeTasks.newMergeColumnDictTaskKey(dataSource, indexField + "." + groupType),
                            new SubDateColumnDictMerger(dataSource, new ColumnKey(indexField), groupType, allSegments));

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

        taskGroup = new LocalTaskGroup(transportTask, end);

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
            workOver(new TaskResultImpl(Type.FAILED, e));
        }
    }

}
