package com.fr.swift.cube.queue;

import com.fr.swift.event.SwiftEventDispatcher;
import com.fr.swift.event.SwiftEventListener;
import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.source.DataSource;
import com.fr.swift.task.SchedulerTask;
import com.fr.swift.task.TaskKey;
import com.fr.swift.task.impl.SchedulerTaskImpl;
import com.fr.swift.task.impl.TaskEvent;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * This class created on 2018-1-16 15:57:05
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public class StuffFetcher implements Runnable {
    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(StuffFetcher.class);

    private Map<TaskKey, Object> taskInfos = new HashMap<TaskKey, Object>();

    public StuffFetcher() {
        initListener();
    }

    private void initListener() {
        SwiftEventDispatcher.listen(TaskEvent.TRIGGER, new SwiftEventListener<TaskKey>() {
            @Override
            public void on(TaskKey taskKey) {
                SwiftEventDispatcher.fire(TaskEvent.RUN, Collections.singletonMap(taskKey, taskInfos.get(taskKey)));
            }
        });
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                consume(StuffProviderQueue.getQueue().take());
            } catch (InterruptedException ite) {
                LOGGER.error(ite);
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                LOGGER.error(e);
            }
        }
    }

    private void consume(ImportStuff stuff) throws SwiftMetaDataException {
        int round = CubeTasks.nextRound();

        final SchedulerTask start = CubeTasks.newStartTask(round),
                end = CubeTasks.newEndTask(round);

        taskInfos.put(start.key(), null);
        taskInfos.put(end.key(), null);

        for (DataSource dataSource : stuff.getTables()) {
            TaskKey key = CubeTasks.newBuildTableTaskKey(round, dataSource);
            taskInfos.put(key, dataSource);

            SchedulerTask task = new SchedulerTaskImpl(key);
            start.addNext(task);
            task.addNext(end);
        }

        start.triggerRun();
    }

//    public synchronized void update(final IndexStuffProvider stuff) throws SwiftMetaDataException {
//        final long t = System.currentTimeMillis();
//        CubeTasks.nextRound();
//        int currentRound = CubeTasks.getCurrentRound();
//        LocalTaskPool.getInstance().putIndexStuffMedium(currentRound, stuff.getIndexStuffMedium());
//
//        Map<TaskKey, SchedulerTask> taskMap = new HashMap<TaskKey, SchedulerTask>();
//
//        final SchedulerTask start = CubeTasks.newStartTask(),
//                end = CubeTasks.newEndTask();
//
//        taskInfos.put(start.key(), null);
//        taskInfos.put(end.key(), null);
//
//        SourceReliance sourceReliance = stuff.getSourceReliance();
//        //根据表依赖和顺序添加更新任务
//        if (sourceReliance.getHeadNodes().isEmpty()) {
//            start.addNext(end);
//        }
//        for (Map.Entry<SourceKey, SourceNode> entry : sourceReliance.getHeadNodes().entrySet()) {
//            SourceNode sourceNode = entry.getValue();
//            calcBaseNode(sourceNode, start, end, taskMap);
//        }
//
//        // 生成关联
//        RelationReliance relationReliance = stuff.getRelationReliance();
//        calRelationTask(relationReliance, end);
//        RelationPathReliance relationPathReliance = stuff.getRelationPathReliance();
//        calRelationTask(relationPathReliance, end);
//
//        start.triggerRun();
//
//        end.addStatusChangeListener(new TaskStatusChangeListener() {
//            @Override
//            public void onChange(Status prev, Status now) {
//                if (now == Status.DONE) {
//                    LOGGER.info("build cost " + DateUtils.timeCostFrom(t));
//                    LOGGER.info("build " + end.result());
//                    for (IndexStuffProvider.TaskResultListener taskResultListener : stuff.taskResultListeners()) {
//                        taskResultListener.call(end.result());
//                    }
//                }
//            }
//        });
//
//    }

//    private void calRelationTask(AbstractRelationReliance relationReliance, SchedulerTask end) throws SwiftMetaDataException {
//        Map<SourceKey, IRelationNode> relationNodeMap = relationReliance.getHeadNode();
//        for (Entry<SourceKey, IRelationNode> entry : relationNodeMap.entrySet()) {
//            IRelationNode node = entry.getValue();
//            SchedulerTask relationTask = new SchedulerTaskImpl(CubeTasks.newIndexRelationTaskKey(node.getNode()));
//            List<Source> deps = node.getDepend();
//            for (Source dep : deps) {
//                SchedulerTask task;
//                if (dep instanceof DataSource) {
//                    task = SchedulerTaskPool.getInstance().get(CubeTasks.newBuildTableTaskKey((DataSource) dep));
//                } else {
//                    task = SchedulerTaskPool.getInstance().get(CubeTasks.newIndexRelationTaskKey((RelationSource) dep));
//                }
//                if (null != task) {
//                    task.addNext(relationTask);
//                }
//            }
//            relationTask.addNext(end);
//            taskInfos.put(relationTask.key(), node.getNode());
//        }
//    }
//
//    private void calcBaseNode(SourceNode sourceNode, SchedulerTask prevTask, SchedulerTask endTask, Map<TaskKey, SchedulerTask> taskMap) throws SwiftMetaDataException {
//        TaskKey taskKey = CubeTasks.newBuildTableTaskKey(sourceNode.getNode());
//        SchedulerTask currentTask;
//        if (taskMap.containsKey(taskKey)) {
//            currentTask = taskMap.get(taskKey);
//        } else {
//            currentTask = new SchedulerTaskImpl(taskKey);
//        }
//        taskInfos.put(currentTask.key(), sourceNode);
//        taskMap.put(currentTask.key(), currentTask);
//
//        prevTask.addNext(currentTask);
//        if (sourceNode.hasNext()) {
//            for (SourceNode nextSourceNode : sourceNode.next()) {
//                calcBaseNode(nextSourceNode, currentTask, endTask, taskMap);
//            }
//        } else {
//            currentTask.addNext(endTask);
//        }
//
//    }
}