package com.fr.swift.executor.config;

import com.fr.swift.SwiftContext;
import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.config.oper.BaseTransactionWorker;
import com.fr.swift.config.oper.ConfigSession;
import com.fr.swift.config.oper.Order;
import com.fr.swift.config.oper.TransactionManager;
import com.fr.swift.config.oper.impl.ConfigWhereImpl;
import com.fr.swift.config.oper.impl.OrderImpl;
import com.fr.swift.executor.task.ExecutorTask;
import com.fr.swift.executor.type.DBStatusType;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.property.SwiftProperty;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * This class created on 2019/2/26
 *
 * @author Lucifer
 * @description
 */
@SwiftBean
public class ExecutorTaskServiceImpl implements ExecutorTaskService {

    private TransactionManager transactionManager = SwiftContext.get().getBean(TransactionManager.class);

    private ExecutorTaskDao executorTaskDao = SwiftContext.get().getBean(ExecutorTaskDao.class);

    public ExecutorTaskServiceImpl() {
    }

    @Override
    public boolean saveOrUpdate(final ExecutorTask executorTask) throws SQLException {
        return transactionManager.doTransactionIfNeed(new BaseTransactionWorker<Boolean>() {
            @Override
            public Boolean work(ConfigSession session) throws SQLException {
                return executorTaskDao.saveOrUpdate(session, (SwiftExecutorTaskEntity) executorTask.convert());
            }
        });
    }

    @Override
    public boolean batchSaveOrUpdate(final Set<ExecutorTask> executorTasks) throws SQLException {
        return transactionManager.doTransactionIfNeed(new BaseTransactionWorker<Boolean>() {
            @Override
            public Boolean work(ConfigSession session) throws SQLException {
                for (ExecutorTask executorTask : executorTasks) {
                    executorTaskDao.saveOrUpdate(session, (SwiftExecutorTaskEntity) executorTask.convert());
                }
                return true;
            }
        });
    }

    @Override
    public List<ExecutorTask> getActiveTasksBeforeTime(final long time) {
        try {
            return transactionManager.doTransactionIfNeed(new BaseTransactionWorker<List<ExecutorTask>>() {
                @Override
                public List<ExecutorTask> work(ConfigSession session) {
                    List<ExecutorTask> tasks = new ArrayList<ExecutorTask>();
                    try {
                        for (SwiftExecutorTaskEntity item : executorTaskDao.find(session, new Order[]{OrderImpl.asc("createTime")}
                                , ConfigWhereImpl.eq("dbStatusType", DBStatusType.ACTIVE)
                                , ConfigWhereImpl.eq("clusterId", SwiftProperty.getProperty().getClusterId())
                                , ConfigWhereImpl.gt("createTime", time)
                                , ConfigWhereImpl.in("executorTaskType", Arrays.asList(SwiftProperty.getProperty().getExecutorTaskType()))
                        )) {

                            tasks.add(item.convert());

                        }
                    } catch (Exception e) {
                        SwiftLoggers.getLogger().warn(e);
                    }
                    return tasks;
                }
            });
        } catch (Exception e) {
            SwiftLoggers.getLogger().warn("get active executorTasks error!", e);
            return new ArrayList<ExecutorTask>();
        }
    }

    @Override
    public boolean deleteTask(final ExecutorTask executorTask) {
        try {
            return transactionManager.doTransactionIfNeed(new BaseTransactionWorker<Boolean>() {
                @Override
                public Boolean work(ConfigSession session) throws SQLException {
                    return executorTaskDao.delete(session, (SwiftExecutorTaskEntity) executorTask.convert());
                }
            });
        } catch (Exception e) {
            SwiftLoggers.getLogger().warn("delete executorTasks error!", e);
            return false;
        }
    }

    @Override
    public ExecutorTask getExecutorTask(final String taskId) {
        try {
            return transactionManager.doTransactionIfNeed(new BaseTransactionWorker<ExecutorTask>() {
                @Override
                public ExecutorTask work(ConfigSession session) throws SQLException {
                    List<SwiftExecutorTaskEntity> entities = executorTaskDao.find(session, ConfigWhereImpl.eq("id", taskId));
                    List<ExecutorTask> executorTasks = new ArrayList<ExecutorTask>();
                    for (SwiftExecutorTaskEntity item : entities) {
                        executorTasks.add(item.convert());
                    }
                    if (executorTasks.isEmpty()) {
                        return null;
                    } else {
                        return executorTasks.get(0);
                    }
                }
            });
        } catch (Exception e) {
            SwiftLoggers.getLogger().warn("delete executorTasks error!", e);
            return null;
        }
    }
}