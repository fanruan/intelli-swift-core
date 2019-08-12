package com.fr.swift.executor.config;

import com.fr.swift.SwiftContext;
import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.config.SwiftConfig;
import com.fr.swift.config.command.SwiftConfigCommand;
import com.fr.swift.config.command.SwiftConfigCommandBus;
import com.fr.swift.config.condition.impl.SwiftConfigConditionImpl;
import com.fr.swift.config.oper.ConfigQuery;
import com.fr.swift.config.oper.ConfigSession;
import com.fr.swift.config.oper.impl.ConfigWhereImpl;
import com.fr.swift.config.oper.impl.OrderImpl;
import com.fr.swift.config.query.SwiftConfigQuery;
import com.fr.swift.config.query.SwiftConfigQueryBus;
import com.fr.swift.executor.task.ExecutorTask;
import com.fr.swift.executor.type.DBStatusType;
import com.fr.swift.property.SwiftProperty;
import com.fr.swift.util.function.Function;

import java.sql.SQLException;
import java.util.ArrayList;
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

    private SwiftConfigCommandBus<SwiftExecutorTaskEntity> commandBus = SwiftContext.get().getBean(SwiftConfig.class).command(SwiftExecutorTaskEntity.class);
    private SwiftConfigQueryBus<SwiftExecutorTaskEntity> queryBus = SwiftContext.get().getBean(SwiftConfig.class).query(SwiftExecutorTaskEntity.class);

    public ExecutorTaskServiceImpl() {
    }

    @Override
    public boolean saveOrUpdate(final ExecutorTask executorTask) throws SQLException {
        commandBus.merge((SwiftExecutorTaskEntity) executorTask.convert());
        return true;
    }

    @Override
    public boolean batchSaveOrUpdate(final Set<ExecutorTask> executorTasks) throws SQLException {
        return commandBus.transaction(new SwiftConfigCommand<Boolean>() {
            @Override
            public Boolean apply(ConfigSession p) {
                for (ExecutorTask executorTask : executorTasks) {
                    p.merge(executorTask.convert());
                }
                return true;
            }
        });
    }

    @Override
    public List<ExecutorTask> getActiveTasksBeforeTime(final long time) {
        return queryBus.get(new SwiftConfigQuery<List<ExecutorTask>>() {
            @Override
            public List<ExecutorTask> apply(ConfigSession p) {
                final ConfigQuery<SwiftExecutorTaskEntity> entityQuery = p.createEntityQuery(SwiftExecutorTaskEntity.class);
                entityQuery.where(ConfigWhereImpl.eq("dbStatusType", DBStatusType.ACTIVE)
                        , ConfigWhereImpl.eq("clusterId", SwiftProperty.getProperty().getClusterId())
                        , ConfigWhereImpl.gt("createTime", time));
                entityQuery.orderBy(OrderImpl.asc("createTime"));
                final List<SwiftExecutorTaskEntity> entities = entityQuery.executeQuery();
                List<ExecutorTask> tasks = new ArrayList<ExecutorTask>(entities.size());
                for (SwiftExecutorTaskEntity entity : entities) {
                    tasks.add(entity.convert());
                }
                return tasks;
            }
        });
    }

    @Override
    public boolean deleteTask(final ExecutorTask executorTask) {
        return commandBus.delete(SwiftConfigConditionImpl.newInstance()
                .addWhere(ConfigWhereImpl.eq("id", ((SwiftExecutorTaskEntity) executorTask.convert()).getId()))) > 0;
    }

    @Override
    public ExecutorTask getExecutorTask(final String taskId) {
        return queryBus.select(taskId, new Function<SwiftExecutorTaskEntity, ExecutorTask>() {
            @Override
            public ExecutorTask apply(SwiftExecutorTaskEntity p) {
                return null != p ? p.convert() : null;
            }
        });
    }
}