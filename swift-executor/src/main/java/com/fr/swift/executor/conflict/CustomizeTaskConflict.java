package com.fr.swift.executor.conflict;

import com.fr.swift.base.json.JsonBuilder;
import com.fr.swift.executor.task.ExecutorTask;
import com.fr.swift.executor.type.LockType;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.source.SourceKey;
import com.fr.swift.structure.Pair;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Author: Bellman
 * @Date: 2019/9/12 1:36 下午
 */
public class CustomizeTaskConflict implements TaskConflict {

    // 具体的冲突列表
    private List<LockConflict> lockConflicts = new ArrayList<>();

    // 已经出现过的 sourceKey
    private Set<String> sourceKeys = new HashSet<>();

    // lockType 为 virtual lock 的 task 索引
    private Set<Pair<SourceKey, String>> virtualSegmentLocks = new HashSet<>();

    // 与其他任务都互斥的任务类型
    private Set<String> stopTheWorldTaskTypes = new HashSet<>();

    public CustomizeTaskConflict(List<LockConflict> lockConflicts) {
        this.lockConflicts = lockConflicts;
    }

    /**
     * 用 json 配置文件的信息进行初始化
     *
     * @param confPath 配置文件路径
     * @throws Exception
     */
    public CustomizeTaskConflict(String confPath) throws Exception {
        Map res = JsonBuilder.readValue(readFromFile(confPath), Map.class);
        for (Object obj : (ArrayList) res.get("conflicts")) {
            lockConflicts.add(new BaseLockConflict.Builder((Map) obj).build());
            initSourceKeys((Map) obj);
        }
        for (Object obj : (ArrayList) res.get("stopTheWorldTaskTypes")) {
            stopTheWorldTaskTypes.add(obj.toString());
        }
    }

    /**
     * 完成以下功能
     * 0. 将 未出现过的 sourceKey 添加到冲突列表里, 默认计数器为 1
     * 1. 找出相关的约束
     * 2. 初始化约束计数器
     *
     * @param executorTask
     * @return
     */
    private List<LockConflict> initLockConflicts(ExecutorTask executorTask) {
        // 第一次出现的表名，加入到冲突列表中，这样可以避免把全部表名写到配置文件
        if (!sourceKeys.contains(executorTask.getSourceKey().toString())) {
            sourceKeys.add(executorTask.getSourceKey().toString());
            LockConflict conflict = new BaseLockConflict.Builder()
                    .setSourceKey(executorTask.getSourceKey())
                    .setSemaphore(1)
                    .build();
            lockConflicts.add(conflict);
        }
        List<LockConflict> relativeConflicts = new ArrayList<>();
        if (lockConflicts != null) {
            for (LockConflict conflict : lockConflicts) {
                if (conflict.isRelatedConflict(executorTask)) {
                    // 初始化每个冲突的计数器
                    conflict.initCheck();
                    relativeConflicts.add(conflict);
                }
            }
        }
        return relativeConflicts;
    }

    @Override
    public boolean isConflict(ExecutorTask executorTask, List<ExecutorTask> inQueueTasks) {
        // 进行 stopTheWorld 检查
        if (stopTheWorldTaskTypes.contains(executorTask.getExecutorTaskType().name())) {
            // 如果队列中有其他类型的任务，认为冲突
            for (ExecutorTask task : inQueueTasks) {
                if (!task.getExecutorTaskType().name().equals(executorTask.getExecutorTaskType().name())) {
                    return true;
                }
            }
            // 这里希望信号量能够控制到这个任务，所以不默认不冲突
            // return false;
        }
        // 优先进行虚拟锁检查
        if (LockType.isVirtualLock(executorTask)) {
            return virtualSegmentLocks.contains(new Pair<>(executorTask.getSourceKey(), executorTask.getLockKey()));
        }
        // None 锁直接通过
        if (LockType.isNoneLock(executorTask)) {
            return false;
        }
        // table 锁、real_seg 锁一起处理
        // 实际上是一个刷新计数器的过程，通过遍历 全队列+当前任务，对冲突的计数器进行更新
        // 一旦超过允许，即可认定为冲突
        List<LockConflict> relativeConflicts = initLockConflicts(executorTask);
        for (LockConflict conflict : relativeConflicts) {
            for (ExecutorTask queueTask : inQueueTasks) {
                // TODO 这一步有重复计算的嫌疑，要求更高的性能可以从这里优化
                if (conflict.isConflict(queueTask)) {
                    return true;
                }
            }
            if (conflict.isConflict(executorTask)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void initVirtualLocks(List<ExecutorTask> inQueueTasks) {
        virtualSegmentLocks = new HashSet<>();
        if (inQueueTasks != null) {
            for (ExecutorTask task : inQueueTasks) {
                if (LockType.isVirtualLock(task)) {
                    virtualSegmentLocks.add(new Pair<>(task.getSourceKey(), task.getLockKey()));
                }
            }
        }
    }

    @Override
    public void finishVirtualLocks() {
        virtualSegmentLocks = null;
    }

    private void initSourceKeys(Map parseMapObj) {
        if (parseMapObj.get("sourceKey") != null) {
            sourceKeys.add((String) parseMapObj.get("sourceKey"));
        }
    }

    private String readFromFile(String path) throws IOException {
        StringBuffer conf = new StringBuffer();
        try {
            File file = new File(path);
            if (file.isFile() && file.exists()) {
                InputStreamReader read = new InputStreamReader(new FileInputStream(file), "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(read);
                String lineTxt = bufferedReader.readLine();
                while (lineTxt != null) {
                    conf.append(lineTxt);
                    lineTxt = bufferedReader.readLine();
                }
            }
        } catch (IOException e) {
            SwiftLoggers.getLogger().error(e.toString());
            throw e;
        }
        return conf.toString();
    }
}
