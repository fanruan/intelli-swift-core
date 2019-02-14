package com.fr.swift.task;

/**
 * @author anchore
 * @date 2018/5/12
 */
public interface TaskResult {
    Type getType();

    Exception getCause();

    /**
     * @author anchore
     * @date 2017/12/15
     */
    enum Type {
        // 结果
        SUCCEEDED, FAILED, CANCELLED
    }
}