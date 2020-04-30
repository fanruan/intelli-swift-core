package com.fr.swift.executor.config;

import com.fr.swift.config.dao.SwiftDao;
import com.fr.swift.config.dao.SwiftDaoImpl;
import com.fr.swift.executor.task.ExecutorTask;
import com.fr.swift.executor.type.DBStatusType;
import com.fr.swift.property.SwiftProperty;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * @author lucifer
 * @date 2020/3/16
 * @description
 * @since swift-log 10.0.5
 */
class ExecutorTaskConvertService implements ExecutorTaskService {

    private SwiftDao dao = new SwiftDaoImpl(SwiftExecutorTaskEntity.class);

    @Override
    public void save(ExecutorTask executorTask) throws SQLException {
        dao.insert(SwiftExecutorTaskEntity.convertEntity(executorTask));
    }

    @Override
    public void update(ExecutorTask executorTask) throws SQLException {
        dao.update(SwiftExecutorTaskEntity.convertEntity(executorTask));
    }

    @Override
    public void batchSave(Set<ExecutorTask> executorTasks) throws SQLException {
        dao.insert(SwiftExecutorTaskEntity.convertEntities(executorTasks));
    }

    @Override
    public List<ExecutorTask> getActiveTasksBeforeTime(long time) {
        final List<SwiftExecutorTaskEntity> entities = dao.select(criteria -> criteria.add(Restrictions.eq("dbStatusType", DBStatusType.ACTIVE))
                .add(Restrictions.eq("clusterId", SwiftProperty.get().getMachineId()))
                .add(Restrictions.gt("createTime", time))
                .add(Restrictions.in("executorTaskType", Arrays.asList(SwiftProperty.get().getExecutorTaskType()))));
        List<ExecutorTask> tasks = new ArrayList<>();
        for (SwiftExecutorTaskEntity entity : entities) {
            tasks.add(entity.convert());
        }
        return tasks;
    }

    @Override
    public List<Object[]> getActiveTasksGroupByCluster(long time) {
        ProjectionList projections = Projections.projectionList().add(Projections.groupProperty("clusterId"))
                .add(Projections.groupProperty("executorTaskType"))
                .add(Projections.rowCount());
        List select = dao.select(criteria -> criteria.setProjection(projections)
                .add(Restrictions.eq("dbStatusType", DBStatusType.ACTIVE))
                .add(Restrictions.gt("createTime", time))
                .add(Restrictions.in("executorTaskType", Arrays.asList(SwiftProperty.get().getExecutorTaskType()))));
        return select;
    }

    @Override
    public void delete(ExecutorTask executorTask) {
        dao.delete(SwiftExecutorTaskEntity.convertEntity(executorTask));
    }

    @Override
    public ExecutorTask get(String taskId) {
        final List<SwiftExecutorTaskEntity> tasks = dao.select(criteria -> criteria.add(Restrictions.eq("id", taskId))
                .add(Restrictions.eq("clusterId", SwiftProperty.get().getMachineId())));
        if (tasks.isEmpty()) {
            return null;

        }
        return tasks.get(0).convert();
    }
}
