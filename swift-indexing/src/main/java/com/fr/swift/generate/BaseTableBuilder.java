package com.fr.swift.generate;

import com.fr.swift.SwiftContext;
import com.fr.swift.config.bean.SwiftColumnIdxConfBean;
import com.fr.swift.config.service.IndexingConfService;
import com.fr.swift.cube.queue.CubeTasks;
import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.manager.IndexingSegmentManager;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SwiftSegmentManager;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.DataSource;
import com.fr.swift.task.LocalTask;
import com.fr.swift.task.Task;
import com.fr.swift.task.Task.Status;
import com.fr.swift.task.TaskResult.Type;
import com.fr.swift.task.TaskStatusChangeListener;
import com.fr.swift.task.impl.BaseWorker;
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
    protected DataSource dataSource;

    protected Transporter transporter;

    private int round;

    private boolean isRealtime;

    private IndexingConfService indexingConfService = SwiftContext.get().getBean(IndexingConfService.class);

    private SwiftSegmentManager localSegments = SwiftContext.get().getBean(IndexingSegmentManager.class);

    public BaseTableBuilder(int round, DataSource dataSource) {
        this(round, dataSource, false);
    }

    public BaseTableBuilder(int round, DataSource dataSource, boolean isRealtime) {
        this.round = round;
        this.dataSource = dataSource;
        this.isRealtime = isRealtime;
    }

    protected void init() throws SwiftMetaDataException {
        final LocalTask transportTask = new LocalTaskImpl(
                CubeTasks.newTransportTaskKey(round, dataSource), transporter);

        final LocalTask end = new LocalTaskImpl(
                CubeTasks.newTableBuildEndTaskKey(round, dataSource));

        afterTransport(transportTask, end);

        end.addStatusChangeListener(new TaskStatusChangeListener() {
            @Override
            public void onChange(Task.Status prev, Task.Status now) {
                if (now == Task.Status.DONE) {
                    workOver(end.result());
                }
            }
        });

        transportTask.triggerRun();
    }

    private void afterTransport(final LocalTask transportTask, final LocalTask end) {
        transportTask.addStatusChangeListener(new TaskStatusChangeListener() {
            @Override
            public void onChange(Status prev, Status now) {
                if (now != Status.DONE) {
                    return;
                }
                if (transportTask.result().getType() != Type.SUCCEEDED) {
                    transportTask.addNext(end);
                    return;
                }

                List<Segment> segments = localSegments.getSegment(dataSource.getSourceKey());

                for (String columnName : transporter.getIndexFieldsList()) {
                    SwiftColumnIdxConfBean columnConf = indexingConfService.getColumnConf(dataSource.getSourceKey(), columnName);

                    if (!columnConf.isRequireIndex()) {
                        continue;
                    }

                    try {
                        LocalTask indexTask = new LocalTaskImpl(
                                CubeTasks.newIndexColumnTaskKey(round, dataSource, columnName),
                                new ColumnIndexer(new ColumnKey(columnName), segments));
                        transportTask.addNext(indexTask);

                        if (!columnConf.isRequireGlobalDict()) {
                            indexTask.addNext(end);
                            continue;
                        }
                        LocalTask mergeTask = new LocalTaskImpl(
                                CubeTasks.newMergeColumnDictTaskKey(round, dataSource, columnName),
                                new ColumnDictMerger(dataSource, new ColumnKey(columnName), segments));
                        indexTask.addNext(mergeTask);
                        mergeTask.addNext(end);
                    } catch (SwiftMetaDataException e) {
                        SwiftLoggers.getLogger().error(e);
                    }
                }

            }
        });
    }

    @Override
    public void build() throws Exception {
        init();
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
