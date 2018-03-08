package com.fr.swift.cube.queue;

import com.fr.swift.cube.task.SchedulerTask;
import com.fr.swift.cube.task.TaskKey;
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
                try {
                    IndexStuffProvider provider = StuffProviderQueue.getQueue().take();
                    update(provider);
                } catch (Exception e) {

                }
            }
        } catch (Throwable e) {
            LOGGER.error(e);
        }
    }

    public static void update(IndexStuffProvider stuff) throws SwiftMetaDataException, SwiftServiceException {
        List<Pair<TaskKey, Object>> pairs = new ArrayList<Pair<TaskKey, Object>>();

        SchedulerTask start = CubeTasks.newStartTask(),
                end = CubeTasks.newEndTask();

        pairs.add(Pair.of(start.key(), null));
        pairs.add(Pair.of(end.key(), null));

        // 所有表
        for (DataSource dataSource : stuff.getAllTables()) {
            SchedulerTask task;

            if (dataSource instanceof ETLDataSource) {
                task = CubeTasks.newEtlTask((ETLDataSource) dataSource, start);
            } else {
                task = CubeTasks.newTableTask(dataSource);
                start.addNext(task);
            }

            task.addNext(end);

            pairs.add(new Pair<TaskKey, Object>(task.key(), dataSource));
        }

        // 所有关联
//        for (RelationSource relation : stuff.getAllRelations()) {
//            DataSource primary = stuff.getTableById(relation.getPrimarySource().getId());
//            DataSource foreign = stuff.getTableById(relation.getForeignSource().getId());
//
//            SchedulerTask relationTask = CubeTasks.newRelationTask(relation);
//            SchedulerTask primaryTask = SchedulerTaskPool.getInstance().get(CubeTasks.newTaskKey(primary)),
//                    foreignTask = SchedulerTaskPool.getInstance().get(CubeTasks.newTaskKey(foreign));
//            primaryTask.addNext(relationTask);
//            foreignTask.addNext(relationTask);
//
//            relationTask.addNext(end);
//            pairs.add(new Pair<TaskKey, Object>(relationTask.key(), relation));
//        }

        CubeTasks.sendTasks(pairs);
        start.triggerRun();
    }
}