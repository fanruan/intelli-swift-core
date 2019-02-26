package com.fr.swift.executor.queue;

import com.fr.swift.executor.task.ExecutorTask;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class created on 2019/2/11
 *
 * @author Lucifer
 * @description 代理的数据库队列
 */
// TODO: 2019/2/25 实现DB下的队列 
public final class DBQueue {

    private static DBQueue INSTANCE = new DBQueue();

    public static DBQueue getInstance() {
        return INSTANCE;
    }

    private DBQueue() {
    }

    public boolean put(ExecutorTask task) throws SQLException {
        return true;
    }

    public List<ExecutorTask> pullAll() {
        return new ArrayList<ExecutorTask>();
    }

    public List<ExecutorTask> pullBySize(int size) {
        return new ArrayList<ExecutorTask>();
    }
}

