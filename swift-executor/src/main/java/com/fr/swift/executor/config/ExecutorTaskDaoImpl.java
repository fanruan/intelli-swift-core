package com.fr.swift.executor.config;

import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.config.dao.BasicDao;

/**
 * This class created on 2019/2/26
 *
 * @author Lucifer
 * @description
 */
@SwiftBean
public class ExecutorTaskDaoImpl extends BasicDao<SwiftExecutorTaskEntity> implements ExecutorTaskDao {

    public ExecutorTaskDaoImpl() {
        super(SwiftExecutorTaskEntity.class);
    }
}
