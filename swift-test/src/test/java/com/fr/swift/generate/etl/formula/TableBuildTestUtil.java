package com.fr.swift.generate.etl.formula;

import com.fr.swift.cube.queue.CubeTasks;
import com.fr.swift.generate.history.TableBuilder;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.source.DataSource;
import com.fr.swift.structure.Pair;
import com.fr.swift.task.SchedulerTask;
import com.fr.swift.task.TaskKey;
import com.fr.swift.task.cube.CubeOperation;
import com.fr.swift.task.cube.CubeTaskKey;
import com.fr.swift.task.cube.CubeTaskManager;
import com.fr.swift.task.impl.SchedulerTaskImpl;
import com.fr.swift.task.impl.SchedulerTaskPool;
import com.fr.swift.task.impl.WorkerTaskImpl;
import com.fr.swift.task.impl.WorkerTaskPool;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Lucifer
 * @Description:
 * @Date: Created in 2018-3-6
 */
public class TableBuildTestUtil {

    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(TableBuildTestUtil.class);

    public static void initGeneratorListener() {
        SchedulerTaskPool.getInstance().initListener();
        WorkerTaskPool.getInstance().initListener();
        WorkerTaskPool.getInstance().setTaskGenerator((taskKey, taskInfo) -> {
            if (taskKey.operation() == CubeOperation.NULL) {
                return new WorkerTaskImpl(taskKey);
            }

//            if (!(o instanceof ETLSource)) {
            DataSource ds = ((DataSource) taskInfo);
            return new WorkerTaskImpl(taskKey, new TableBuilder(taskKey.getRound(), ds));
//            } else {
//                ETLSource etlSource = ((ETLSource) o);
//                WorkerTask wt = new WorkerTaskImpl(taskKey);
//                if (ComparatorUtils.equals(etlSource.getOperator().getOperatorType().name(), OperatorType.COLUMNFORMULA.name())) {
//                    List<String> fields = new ArrayList<String>();
//                    fields.add(((ColumnFormulaOperator) etlSource.getOperator()).getColumnMD5());
//                    try {
//                        wt.setWorker(new TableFieldBuilder(etlSource, fields));
//                    } catch (Exception e) {
//                        LOGGER.error(e.getMessage(), e);
//                        wt.setWorker(new TableBuilder(etlSource));
//                    }
//                } else {
//                    wt.setWorker(new TableBuilder(etlSource));
//                }
//                return wt;
//
//            }
        });
        CubeTaskManager.getInstance().initListener();
    }

    public static void preparePairList(List<DataSource> dataSources) throws Exception {
        List<Pair<TaskKey, Object>> l = new ArrayList<>();

        int round = CubeTasks.nextRound();

        SchedulerTask start = new SchedulerTaskImpl(new CubeTaskKey(round, "start all")),
                end = new SchedulerTaskImpl(new CubeTaskKey(round, "end all"));

        l.add(new Pair<>(start.key(), null));
        l.add(new Pair<>(end.key(), null));

        SchedulerTask task = start;
        for (DataSource dataSource : dataSources) {
            SchedulerTask dataTask = new SchedulerTaskImpl(new CubeTaskKey(round, dataSource.getMetadata().getTableName(), CubeOperation.BUILD_TABLE));
            task.addNext(dataTask);
            task = dataTask;
            l.add(new Pair<>(task.key(), dataSource));
        }
        task.addNext(end);

//        CubeTasks.sendTasks(l);
        start.triggerRun();
    }
}
