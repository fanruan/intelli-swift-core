package com.fr.swift.executor.type;

import com.fr.swift.executor.task.ExecutorTask;
import com.fr.swift.util.Util;

/**
 * This class created on 2019/2/11
 *
 * @author Lucifer
 * @description
 */
public enum LockType {
    //
    TABLE,
    REAL_SEG,
    VIRTUAL_SEG,
    NONE;

    public static boolean isSameTable(ExecutorTask task1, ExecutorTask task2) {
        return Util.equals(task1.getSourceKey(), task2.getSourceKey());
    }

    public static boolean isTableLock(ExecutorTask task) {
        return task.getLockType() == TABLE;
    }

    public static boolean isRealLock(ExecutorTask task) {
        return task.getLockType() == REAL_SEG;
    }

    public static boolean isVirtualLock(ExecutorTask task) {
        return task.getLockType() == VIRTUAL_SEG;
    }

    public static boolean isNoneLock(ExecutorTask task) {
        return task.getLockType() == NONE;
    }

    public static boolean isSameLockKey(ExecutorTask task1, ExecutorTask task2) {
        return Util.equals(task1.getLockKey(), task2.getLockKey());
    }
}
