package com.fr.swift.executor.config;

import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.config.dao.BasicDao;
import com.fr.swift.executor.task.AbstractExecutorTask;
import com.fr.swift.executor.task.ExecutorTask;

/**
 * This class created on 2019/2/26
 *
 * @author Lucifer
 * @description
 */
@SwiftBean
public class ExecutorTaskDaoImpl extends BasicDao<ExecutorTask> implements ExecutorTaskDao {

    public ExecutorTaskDaoImpl() {
        super(AbstractExecutorTask.TYPE);
    }
}
