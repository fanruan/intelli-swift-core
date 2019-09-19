package com.fr.swift.executor.conflict;

import com.fr.swift.executor.task.ExecutorTask;
import com.fr.swift.source.SourceKey;

import java.util.Map;

/**
 * @Author: Bellman
 * @Date: 2019/9/12 3:22 下午
 */
public class BaseLockConflict implements LockConflict {

    // 任务表名
    private SourceKey sourceKey = null;
    // 任务类型名
    private String executorTaskType = null;
    // 任务相关块名
    private String lockKey = null;
    // 当前指定任务最多在 queue 中存在的条数
    private int semaphore = 1;
    // 当前指定任务在 queue 中存在的条数
    private transient int cur_conflicts = 0;

    private BaseLockConflict() {
    }

    @Override
    public boolean isConflict(ExecutorTask other) {
        // 无限量，永远不冲突
        if (semaphore == -1) {
            return false;
        }
        // 发生一次冲突 +1，知道超过最大允许就是真正的冲突
        if (taskConflict(other)) {
            cur_conflicts += 1;
        }
        return cur_conflicts > semaphore;
    }

    /**
     * 通过对判定冲突的定制化，可以实现粒度更细的并发控制
     * 当前判断逻辑是建立在一个前提下：虚拟锁和None锁都已经提前处理，与当下无关
     *
     * @param other
     * @return
     */
    private boolean taskConflict(ExecutorTask other) {
        // 全 null -> 和任何对象不冲突
        if (sourceKey == null && executorTaskType == null && lockKey == null) {
            return false;
        }
        // 只要一个非空条件不冲突，就不冲突
        if (sourceKey != null && !sourceKey.equals(other.getSourceKey())) {
            return false;
        }
        if (executorTaskType != null && !executorTaskType.equals(other.getExecutorTaskType().name())) {
            return false;
        }
        if (lockKey != null && !lockKey.equals(other.getLockKey())) {
            return false;
        }
        return true;
    }

    @Override
    public void initCheck() {
        cur_conflicts = 0;
    }

    @Override
    public boolean isRelatedConflict(ExecutorTask task) {
        // 只要一个非空字段相等就判定相关（最保守的做法）
        if (task.getSourceKey() != null && task.getSourceKey().equals(sourceKey)) {
            return true;
        }
        if (task.getExecutorTaskType() != null && task.getExecutorTaskType().name().equals(executorTaskType)) {
            return true;
        }
        if (task.getLockKey() != null && task.getLockKey().equals(lockKey)) {
            return true;
        }
        return false;
    }

    public static class Builder {
        private BaseLockConflict conflict;

        public Builder() {
            conflict = new BaseLockConflict();
        }

        /**
         * 从文件中解析出来的 map 直接构造出冲突对象
         *
         * @param parseMapObj 配置文件解析出来的 map
         * @throws Exception
         */
        public Builder(Map parseMapObj) throws Exception {
            conflict = new BaseLockConflict();
            if (parseMapObj.get("sourceKey") != null) {
                conflict.sourceKey = new SourceKey((String) parseMapObj.get("sourceKey"));
            }
            if (parseMapObj.get("executorTaskType") != null) {
                conflict.executorTaskType = (String) parseMapObj.get("executorTaskType");
            }
            if (parseMapObj.get("lockKey") != null) {
                conflict.lockKey = (String) parseMapObj.get("lockKey");
            }
            if (parseMapObj.get("semaphore") != null) {
                conflict.semaphore = (int) parseMapObj.get("semaphore");
            }
        }

        public Builder setSourceKey(SourceKey sourceKey) {
            conflict.sourceKey = sourceKey;
            return this;
        }

        public Builder setExecutorTaskType(String executorTaskType) {
            conflict.executorTaskType = executorTaskType;
            return this;
        }

        public Builder setLockKey(String lockKey) {
            conflict.lockKey = lockKey;
            return this;
        }

        public Builder setSemaphore(int semaphore) {
            conflict.semaphore = semaphore;
            return this;
        }

        public LockConflict build() {
            return conflict;
        }
    }
}
