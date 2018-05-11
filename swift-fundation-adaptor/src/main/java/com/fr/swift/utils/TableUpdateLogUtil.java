package com.fr.swift.utils;

import com.finebi.conf.internalimp.update.GlobalUpdateLog;
import com.finebi.conf.internalimp.update.IndexProcessInfo;
import com.finebi.conf.internalimp.update.IndexUpdateInfo;
import com.finebi.conf.internalimp.update.IndexUpdateItem;
import com.finebi.conf.internalimp.update.RelationProcessInfo;
import com.finebi.conf.internalimp.update.TranSportInfo;
import com.fr.swift.cube.task.Task;
import com.fr.swift.cube.task.TaskKey;
import com.fr.swift.cube.task.impl.SchedulerTaskPool;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class created on 2018/5/11
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class TableUpdateLogUtil {

    /**
     * transport的进度、日志
     *
     * @param transportTaskMap
     * @return
     */
    public static TranSportInfo getTranSportInfo(Map<TaskKey, Task> transportTaskMap) {
        int count = transportTaskMap.size();
        int finishCount = 0;
        List<IndexUpdateItem> items = new ArrayList<IndexUpdateItem>();
        //todo 处理业务包和业务表
        List<String> packName = new ArrayList<String>();
        packName.add("package");

        for (Map.Entry<TaskKey, Task> entry : transportTaskMap.entrySet()) {
            if (entry.getValue().status().equals(Task.Status.DONE)) {
                finishCount++;
                IndexUpdateItem item = new IndexUpdateItem();
                item.setIndex(finishCount);
                item.setTableName(entry.getKey().info());
                item.setTime(entry.getValue().getCostTime());
                item.setPackNames(packName);
                items.add(item);
            }
        }
        IndexProcessInfo indexProcessInfo = new IndexProcessInfo();
        indexProcessInfo.setAllIndex(count);
        indexProcessInfo.setAlreadyIndex(finishCount);
        TranSportInfo tranSportInfo = new TranSportInfo();
        tranSportInfo.setProcess(indexProcessInfo);
        tranSportInfo.setItems(items);
        return tranSportInfo;
    }

    /**
     * index和dict merge的进度、日志
     *
     * @param indexTaskMap
     * @param mergeTaskMap
     * @return
     */
    public static IndexUpdateInfo getIndexInfo(Map<TaskKey, Task> indexTaskMap, Map<TaskKey, Task> mergeTaskMap) {
        int count = indexTaskMap.size();
        int finishCount = 0;
        List<IndexUpdateItem> items = new ArrayList<IndexUpdateItem>();

        //todo 处理业务包和业务表
        List<String> packName = new ArrayList<String>();
        packName.add("package");

        for (Map.Entry<TaskKey, Task> entry : indexTaskMap.entrySet()) {
            if (entry.getValue().status().equals(Task.Status.DONE)) {
                if (mergeTaskMap.get(entry.getKey()) == null || mergeTaskMap.get(entry.getKey()).status().equals(Task.Status.DONE)) {
                    finishCount++;
                    IndexUpdateItem item = new IndexUpdateItem();
                    item.setIndex(finishCount);
                    item.setTableName(entry.getKey().info());
                    item.setTime(entry.getValue().getCostTime());
                    item.setPackNames(packName);
                    items.add(item);
                }
            }
        }
        IndexProcessInfo indexProcessInfo = new IndexProcessInfo();
        indexProcessInfo.setAllIndex(count);
        indexProcessInfo.setAlreadyIndex(finishCount);
        IndexUpdateInfo indexInfo = new IndexUpdateInfo();
        indexInfo.setProcess(indexProcessInfo);
        indexInfo.setItems(items);
        return indexInfo;
    }

    /**
     * 判断是否有任务在执行
     *
     * @return
     */
    public static boolean hasTask() {
        Collection<TaskKey> allTaskKey = SchedulerTaskPool.getInstance().allTasks();
        for (TaskKey taskKey : allTaskKey) {
            Task task = SchedulerTaskPool.getInstance().get(taskKey);
            if (task.status() != Task.Status.DONE) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取所有正在running或者runnable的轮数
     *
     * @return
     */
    public static Set<Integer> getRunningRounds() {
        Collection<TaskKey> allTaskKey = SchedulerTaskPool.getInstance().allTasks();
        Set<Integer> rounds = new HashSet<Integer>();
        for (TaskKey taskKey : allTaskKey) {
            Task task = SchedulerTaskPool.getInstance().get(taskKey);
            if (task.status() == Task.Status.RUNNING || task.status() == Task.Status.RUNNABLE) {
                rounds.add(taskKey.getRound());
            }
        }
        return rounds;
    }

    /**
     * 获得总的百分比
     *
     * @param globalUpdateLog
     * @return
     */
    public static double getProcess(GlobalUpdateLog globalUpdateLog) {
        int allProcess = 0, finishProcess = 0;

        if (globalUpdateLog.getTransportInfo() != null) {
            IndexProcessInfo transportProcess = globalUpdateLog.getTransportInfo().getProcess();
            allProcess += transportProcess.getAllIndex();
            finishProcess += transportProcess.getAlreadyIndex();
        }
        if (globalUpdateLog.getIndexInfo() != null) {
            IndexProcessInfo indexProcess = globalUpdateLog.getIndexInfo().getProcess();
            allProcess += indexProcess.getAllIndex();
            finishProcess += indexProcess.getAlreadyIndex();
        }
        if (globalUpdateLog.getRelationInfo() != null) {
            RelationProcessInfo relationProcess = globalUpdateLog.getRelationInfo().getProcess();
            allProcess += relationProcess.getAllRelation();
            finishProcess += relationProcess.getAlreadyRelation();
        }
        if (allProcess == 0) {
            return 0;
        } else {
            return ((double) finishProcess) / allProcess;
        }
    }
}
