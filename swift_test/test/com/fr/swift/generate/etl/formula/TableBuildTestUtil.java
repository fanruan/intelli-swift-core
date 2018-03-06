package com.fr.swift.generate.etl.formula;

import com.fr.swift.cube.queue.CubeTasks;
import com.fr.swift.cube.task.SchedulerTask;
import com.fr.swift.cube.task.TaskKey;
import com.fr.swift.cube.task.WorkerTask;
import com.fr.swift.cube.task.impl.BaseWorker;
import com.fr.swift.cube.task.impl.CubeTaskKey;
import com.fr.swift.cube.task.impl.CubeTaskManager;
import com.fr.swift.cube.task.impl.Operation;
import com.fr.swift.cube.task.impl.SchedulerTaskImpl;
import com.fr.swift.cube.task.impl.SchedulerTaskPool;
import com.fr.swift.cube.task.impl.WorkerTaskImpl;
import com.fr.swift.cube.task.impl.WorkerTaskPool;
import com.fr.swift.generate.history.TableBuilder;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.source.DataSource;
import com.fr.swift.structure.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Lucifer
 * @Description:
 * @Date: Created in 2018-3-6
 */
public class TableBuildTestUtil {

    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(TableBuildTestUtil.class);

    public static void initGeneratorListener() throws Exception {
        SchedulerTaskPool.getInstance().initListener();
        WorkerTaskPool.getInstance().initListener();
        WorkerTaskPool.getInstance().setGenerator(pair -> {
            TaskKey taskKey = pair.getKey();
            if (taskKey.operation() == Operation.NULL) {
                WorkerTask wt = new WorkerTaskImpl(taskKey);
                wt.setWorker(BaseWorker.nullWorker());
                return wt;
            }

            Object o = pair.getValue();
//            if (!(o instanceof ETLSource)) {
            DataSource ds = ((DataSource) o);
            WorkerTask wt = new WorkerTaskImpl(taskKey);
            wt.setWorker(new TableBuilder(ds));
            return wt;
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

        SchedulerTask start = new SchedulerTaskImpl(new CubeTaskKey("start all")),
                end = new SchedulerTaskImpl(new CubeTaskKey("end all"));

        l.add(new Pair<>(start.key(), null));
        l.add(new Pair<>(end.key(), null));

        SchedulerTask task = start;
        for (DataSource dataSource : dataSources) {
            SchedulerTask dataTask = new SchedulerTaskImpl(new CubeTaskKey(dataSource.getMetadata().getTableName(), Operation.BUILD_TABLE));
            task.addNext(dataTask);
            task = dataTask;
            l.add(new Pair<>(task.key(), dataSource));
        }
        task.addNext(end);

        CubeTasks.sendTasks(l);
        start.triggerRun();
    }
}
