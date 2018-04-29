package com.fr.swift.cube.queue;

import com.fr.swift.cube.task.SchedulerTask;
import com.fr.swift.cube.task.TaskKey;
import com.fr.swift.cube.task.impl.SchedulerTaskPool;
import com.fr.swift.exception.SwiftServiceException;
import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.reliance.AbstractRelationReliance;
import com.fr.swift.reliance.IRelationNode;
import com.fr.swift.reliance.RelationPathReliance;
import com.fr.swift.reliance.RelationReliance;
import com.fr.swift.reliance.SourceNode;
import com.fr.swift.reliance.SourceReliance;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.RelationSource;
import com.fr.swift.source.Source;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.manager.IndexStuffProvider;
import com.fr.swift.structure.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
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

        Map<TaskKey, Pair<TaskKey, Object>> pairMap = new HashMap<TaskKey, Pair<TaskKey, Object>>();
        Map<TaskKey, SchedulerTask> taskMap = new HashMap<TaskKey, SchedulerTask>();

        SchedulerTask start = CubeTasks.newStartTask(),
                end = CubeTasks.newEndTask();

        pairs.add(Pair.of(start.key(), null));
        pairs.add(Pair.of(end.key(), null));

        SourceReliance sourceReliance = stuff.getSourceReliance();
        //根据表依赖和顺序添加更新任务
        for (Map.Entry<SourceKey, SourceNode> entry : sourceReliance.getHeadNodes().entrySet()) {
            SourceNode sourceNode = entry.getValue();
            calcBaseNode(sourceNode, start, end, pairMap, taskMap);
        }
        pairs.addAll(pairMap.values());

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

        // 生成关联
        RelationReliance relationReliance = stuff.getRelationReliance();
        calRelationTask(relationReliance, pairs, end);
        RelationPathReliance relationPathReliance = stuff.getRelationPathReliance();
        calRelationTask(relationPathReliance, pairs, end);

        CubeTasks.sendTasks(pairs);
        start.triggerRun();
    }

    private static void calRelationTask(AbstractRelationReliance relationReliance, List<Pair<TaskKey, Object>> pairs, SchedulerTask end) throws SwiftMetaDataException {
        Map<SourceKey, IRelationNode> relationNodeMap = relationReliance.getHeadNode();
        Iterator<Map.Entry<SourceKey, IRelationNode>> relationIterator = relationNodeMap.entrySet().iterator();
        while (relationIterator.hasNext()) {
            IRelationNode node = relationIterator.next().getValue();
            SchedulerTask relationTask = CubeTasks.newRelationTask(node.getNode());
            List<Source> deps = node.getDepend();
            for (Source dep : deps) {
                SchedulerTask task;
                if (dep instanceof DataSource) {
                    task = SchedulerTaskPool.getInstance().get(CubeTasks.newTaskKey((DataSource) dep));
                } else {
                    task = SchedulerTaskPool.getInstance().get(CubeTasks.newTaskKey((RelationSource) dep));
                }
                if (null != task) {
                    task.addNext(relationTask);
                }
            }
            relationTask.addNext(end);
            pairs.add(new Pair<TaskKey, Object>(relationTask.key(), node.getNode()));
        }
    }

    private static void calcBaseNode(SourceNode sourceNode, SchedulerTask prevTask, SchedulerTask endTask,
                                     Map<TaskKey, Pair<TaskKey, Object>> pairMap, Map<TaskKey, SchedulerTask> taskMap) throws SwiftMetaDataException {
        SchedulerTask currentTask = CubeTasks.newTableTask(sourceNode.getNode());
        if (taskMap.containsKey(currentTask.key())) {
            currentTask = taskMap.get(currentTask.key());
        }
        pairMap.put(currentTask.key(), new Pair<TaskKey, Object>(currentTask.key(), sourceNode));
        taskMap.put(currentTask.key(), currentTask);

        prevTask.addNext(currentTask);
        if (sourceNode.hasNext()) {
            for (SourceNode nextSourceNode : sourceNode.next()) {
                calcBaseNode(nextSourceNode, currentTask, endTask, pairMap, taskMap);
            }
        } else {
            currentTask.addNext(endTask);
        }

    }
}