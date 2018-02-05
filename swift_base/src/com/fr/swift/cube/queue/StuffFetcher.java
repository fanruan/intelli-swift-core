package com.fr.swift.cube.queue;

import com.fr.swift.cube.task.SchedulerTask;
import com.fr.swift.cube.task.TaskKey;
import com.fr.swift.cube.task.impl.CubeTaskKey;
import com.fr.swift.cube.task.impl.Operation;
import com.fr.swift.cube.task.impl.SchedulerTaskImpl;
import com.fr.swift.cube.task.impl.SchedulerTaskPool;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.source.DataSource;
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
        while (true) {
            try {
                IndexStuffProvider provider = StuffProviderQueue.getQueue().take();

                List<DataSource> dataSources = provider.getAllTables();
                List<Pair<TaskKey, Object>> pairList = new ArrayList<Pair<TaskKey, Object>>();

                SchedulerTask start = new SchedulerTaskImpl(new CubeTaskKey("start all")),
                        end = new SchedulerTaskImpl(new CubeTaskKey("end all"));

                pairList.add(new Pair<TaskKey, Object>(start.key(), null));
                pairList.add(new Pair<TaskKey, Object>(end.key(), null));

                for (DataSource dataSource : dataSources) {
                    SchedulerTask task = new SchedulerTaskImpl(new CubeTaskKey(dataSource.getMetadata().getTableName(), Operation.BUILD_TABLE));
                    start.addNext(task);
                    task.addNext(end);
                    pairList.add(new Pair<TaskKey, Object>(task.key(), dataSource));
                }
                SchedulerTaskPool.sendTasks(pairList);
                start.triggerRun();
            } catch (Throwable e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }
}
