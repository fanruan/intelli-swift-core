package com.fr.swift.cube.queue;

import com.fr.swift.context.SwiftContext;
import com.fr.swift.cube.task.SchedulerTask;
import com.fr.swift.cube.task.TaskKey;
import com.fr.swift.cube.task.impl.CubeTaskKey;
import com.fr.swift.cube.task.impl.Operation;
import com.fr.swift.cube.task.impl.SchedulerTaskImpl;
import com.fr.swift.cube.task.impl.SchedulerTaskPool;
import com.fr.swift.exception.SwiftServiceException;
import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.ETLDataSource;
import com.fr.swift.source.manager.IndexStuffProvider;
import com.fr.swift.structure.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * This class created on 2018-1-16 15:57:05
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public class StuffFetcher implements Runnable {
    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(StuffFetcher.class);

    @Override
    public void run() {
        try {
            while (true) {
                IndexStuffProvider provider = StuffProviderQueue.getQueue().take();
                update(provider);
            }
        } catch (Throwable e) {
            LOGGER.error(e);
        }
    }

    public static void update(IndexStuffProvider stuff) throws SwiftMetaDataException, SwiftServiceException {
        List<DataSource> dataSources = stuff.getAllTables();
        List<Pair<TaskKey, Object>> pairList = new ArrayList<Pair<TaskKey, Object>>();

        // fixme 任务key以后坑定要改的 先这么做
        SchedulerTask start = new SchedulerTaskImpl(new CubeTaskKey("start all")),
                end = new SchedulerTaskImpl(new CubeTaskKey("end all"));

        pairList.add(new Pair<TaskKey, Object>(start.key(), null));
        pairList.add(new Pair<TaskKey, Object>(end.key(), null));

        for (DataSource dataSource : dataSources) {
            SchedulerTask task;

            if (dataSource instanceof ETLDataSource) {
                task = newEtlTask((ETLDataSource) dataSource, start);
            } else {
                task = new SchedulerTaskImpl(new CubeTaskKey(dataSource.getMetadata().getTableName(), Operation.BUILD_TABLE));
                start.addNext(task);
            }

            task.addNext(end);
            pairList.add(new Pair<TaskKey, Object>(task.key(), dataSource));
        }

        SchedulerTaskPool.sendTasks(pairList);
        start.triggerRun();
    }

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
    private static SchedulerTask newEtlTask(ETLDataSource etl, SchedulerTask prevTask) throws SwiftMetaDataException {
        List<SchedulerTask> dependTasks = new ArrayList<SchedulerTask>();

        for (DataSource dataSource : etl.getBasedSources()) {
            if (isReadable(dataSource)) {
                continue;
            }

            SchedulerTask task;
            if (dataSource instanceof ETLDataSource) {
                task = newEtlTask(((ETLDataSource) dataSource), prevTask);
            } else {
                task = new SchedulerTaskImpl(new CubeTaskKey(dataSource.getMetadata().getTableName(), Operation.BUILD_TABLE));
                prevTask.addNext(task);
            }

            dependTasks.add(task);
        }

        SchedulerTask etlTask = new SchedulerTaskImpl(new CubeTaskKey(etl.getMetadata().getTableName(), Operation.BUILD_TABLE));
        for (SchedulerTask dependTask : dependTasks) {
            dependTask.addNext(etlTask);
        }
        return etlTask;
    }

    private static boolean isReadable(DataSource dataSource) {
        return SwiftContext.getInstance().getSwiftSegmentProvider().isSegmentsExists(dataSource.getSourceKey());
    }
}