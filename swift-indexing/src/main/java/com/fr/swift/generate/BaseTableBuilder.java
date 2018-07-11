package com.fr.swift.generate;

import com.fr.swift.context.SwiftContext;
import com.fr.swift.cube.queue.CubeTasks;
import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.generate.conf.ColumnIndexingConf;
import com.fr.swift.generate.conf.service.IndexingConfService;
import com.fr.swift.generate.conf.service.SwiftIndexingConfService;
import com.fr.swift.generate.history.index.ColumnDictMerger;
import com.fr.swift.generate.history.index.ColumnIndexer;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.manager.LocalSegmentProvider;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SwiftSegmentManager;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.DataSource;
import com.fr.swift.task.LocalTask;
import com.fr.swift.task.Task;
import com.fr.swift.task.TaskResult.Type;
import com.fr.swift.task.TaskStatusChangeListener;
import com.fr.swift.task.WorkerTask;
import com.fr.swift.task.impl.BaseWorker;
import com.fr.swift.task.impl.LocalTaskGroup;
import com.fr.swift.task.impl.LocalTaskImpl;
import com.fr.swift.task.impl.TaskResultImpl;

import java.util.List;

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

    protected WorkerTask taskGroup;

    protected Transporter transporter;

    private int round;

    private boolean isRealtime;

    private IndexingConfService indexingConfService = SwiftIndexingConfService.get();

    private SwiftSegmentManager localSegments = SwiftContext.getInstance().getBean(LocalSegmentProvider.class);

    public BaseTableBuilder(int round, DataSource dataSource) {
        this(round, dataSource, false);
    }

    public BaseTableBuilder(int round, DataSource dataSource, boolean isRealtime) {
        this.round = round;
        this.dataSource = dataSource;
        this.isRealtime = isRealtime;
    }

    protected void init() throws SwiftMetaDataException {
        LocalTask transportTask = new LocalTaskImpl(
                CubeTasks.newTransportTaskKey(round, dataSource), transporter);

        LocalTask end = new LocalTaskImpl(
                CubeTasks.newTableBuildEndTaskKey(round, dataSource));

        List<Segment> segments = localSegments.getSegment(dataSource.getSourceKey());

        for (String columnName : transporter.getIndexFieldsList()) {
            ColumnIndexingConf columnConf = indexingConfService.getColumnConf(dataSource.getSourceKey(), columnName);

            if (!columnConf.requireIndex()) {
                continue;
            }

            LocalTask indexTask = new LocalTaskImpl(
                    CubeTasks.newIndexColumnTaskKey(round, dataSource, columnName),
                    new ColumnIndexer(dataSource, new ColumnKey(columnName), segments));
            transportTask.addNext(indexTask);

            if (!columnConf.requireGlobalDict()) {
                indexTask.addNext(end);
                continue;
            }

            LocalTask mergeTask = new LocalTaskImpl(
                    CubeTasks.newMergeColumnDictTaskKey(round, dataSource, columnName),
                    new ColumnDictMerger(dataSource, new ColumnKey(columnName), segments));
            indexTask.addNext(mergeTask);
            mergeTask.addNext(end);
        }

        taskGroup = new LocalTaskGroup(transportTask, end);

        taskGroup.addStatusChangeListener(new TaskStatusChangeListener() {
            @Override
            public void onChange(Task.Status prev, Task.Status now) {
                if (now == Task.Status.DONE) {
                    workOver(taskGroup.result());
                }
            }
        });

        //监听表取数任务，完成后添加字段索引任务。
//        transportTask.addStatusChangeListener(new TaskStatusChangeListener() {
//            @Override
//            public void onChange(Task.Status prev, Task.Status now) {
//                if (now == Task.Status.DONE && transportTask.result().getType() == SUCCEEDED) {
//                    try {
//                        initColumnIndexTask();
//                    } catch (Exception e) {
//                        LOGGER.error("Table :'" + dataSource.getSourceKey() + "' add field index task failed!", e);
//                    }
//                }
//            }
//
//            private void initColumnIndexTask() throws Exception {
//                if (transporter.getIndexFieldsList().isEmpty()) {
//                    transportTask.addNext(end);
//                }
//                List<Segment> allSegments = SwiftContext.getInstance().getBean(LocalSegmentProvider.class).getSegment(dataSource.getSourceKey());
//                List<Segment> indexSegments = new ArrayList<Segment>();
//                if (isRealtime) {
////                    for (Segment segment : allSegments) {
////                        if (!segment.isHistory()) {
////                            indexSegments.add(segment);
////                        }
////                    }
//                    //todo 目前增量更新临时每次都会合并到磁盘。要在考虑下合并的规则和场景
//                    int hisSegCount = 0;
//                    for (int i = 0; i < allSegments.size(); i++) {
//                        if (allSegments.get(i).isHistory()) {
//                            hisSegCount++;
//                        }
//                    }
//                    if (hisSegCount != allSegments.size()) {
//                        Merger realtimeMerger = new RealtimeMerger(dataSource.getSourceKey(), dataSource.getMetadata(), DataSourceUtils.getSwiftSourceKey(dataSource).getId());
//                        realtimeMerger.merge();
//                        allSegments = SwiftContext.getInstance().getBean(LocalSegmentProvider.class).getSegment(dataSource.getSourceKey());
//                        for (int i = hisSegCount; i < allSegments.size(); i++) {
//                            indexSegments.add(allSegments.get(i));
//                        }
//                    }
//                    LOGGER.info("Update type:realtime! Table :'" + dataSource.getMetadata().getTableName() + "' will do realtime tranport!");
//                } else {
//                    indexSegments.addAll(allSegments);
//                    LOGGER.info("Update type:history! Table :'" + dataSource.getMetadata().getTableName() + "' will do history tranport!");
//                }
//
//                for (String indexField : transporter.getIndexFieldsList()) {
//                    LocalTask indexTask = new LocalTaskImpl(
//                            CubeTasks.newIndexColumnTaskKey(round, dataSource, indexField),
//                            new ColumnIndexer(dataSource, new ColumnKey(indexField), indexSegments));
//
//                    LocalTask mergeTask = new LocalTaskImpl(
//                            CubeTasks.newMergeColumnDictTaskKey(round, dataSource, indexField),
//                            new ColumnDictMerger(dataSource, new ColumnKey(indexField), allSegments));
//                    // link task
//                    transportTask.addNext(indexTask);
//                    indexTask.addNext(mergeTask);
//                    initSubColumnTaskIfHas(mergeTask, indexField, allSegments, indexSegments);
//                }
//            }
//
//            private void initSubColumnTaskIfHas(LocalTask mergeTask, String indexField, List<Segment> allSegments, List<Segment> indexSegments) throws SwiftMetaDataException {
//                if (!hasSubColumn(indexField)) {
//                    mergeTask.addNext(end);
//                    return;
//                }
//                for (GroupType groupType : SubDateColumn.TYPES_TO_GENERATE) {
//                    LocalTask indexSubColumnTask = new LocalTaskImpl(
//                            CubeTasks.newIndexColumnTaskKey(round, dataSource, indexField, groupType),
//                            new SubDateColumnIndexer(dataSource, new ColumnKey(indexField), groupType, indexSegments));
//
//                    LocalTask mergeSubColumnTask = new LocalTaskImpl(
//                            CubeTasks.newMergeColumnDictTaskKey(round, dataSource, indexField, groupType),
//                            new SubDateColumnDictMerger(dataSource, new ColumnKey(indexField), groupType, allSegments));
//
//                    mergeTask.addNext(indexSubColumnTask);
//                    indexSubColumnTask.addNext(mergeSubColumnTask);
//                    mergeSubColumnTask.addNext(end);
//                }
//            }
//
//            private boolean hasSubColumn(String indexField) throws SwiftMetaDataException {
//                SwiftMetaDataColumn columnMeta = meta.getColumn(indexField);
//                return ColumnTypeUtils.getColumnType(columnMeta) == ColumnTypeConstants.ColumnType.DATE;
//            }
//        });
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
