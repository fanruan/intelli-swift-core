package com.fr.swift.executor.config;

import com.fr.swift.config.dao.SwiftDao;
import com.fr.swift.config.dao.SwiftDaoImpl;
import com.fr.swift.executor.task.ExecutorTask;
import com.fr.swift.executor.type.DBStatusType;
import com.fr.swift.property.SwiftProperty;

import javax.persistence.criteria.Predicate;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
        final List<SwiftExecutorTaskEntity> entities = dao.selectQuery((query, builder, from) ->
                query.select(from)
                        .where(builder.equal(from.get("dbStatusType"), DBStatusType.ACTIVE)
                                , builder.equal(from.get("clusterId"), SwiftProperty.get().getMachineId())
                                , builder.gt(from.get("createTime"), time)
                                , from.get("executorTaskType").in(Arrays.asList(SwiftProperty.get().getExecutorTaskType()))));

        List<ExecutorTask> tasks = new ArrayList<>();
        for (SwiftExecutorTaskEntity entity : entities) {
            tasks.add(entity.convert());
        }
        return tasks;
    }

    @Override
    public List<Object[]> getActiveTasksGroupByCluster(long time) {
        String hql = "select s.clusterId,s.executorTaskType,count(*) from SwiftExecutorTaskEntity s " +
                "where s.dbStatusType =:dbStatusType and s.createTime >:createTime and s.executorTaskType in (:executorTaskType) group by s.clusterId,s.executorTaskType";
        List<Object[]> select = dao.select(hql, query -> {
            query.setParameter("dbStatusType", DBStatusType.ACTIVE);
            query.setParameter("createTime", time);
            query.setParameter("executorTaskType", Arrays.asList(SwiftProperty.get().getExecutorTaskType()));
        });
        return select;
    }

    @Override
    public List<Object[]> getMaxtimeByContent(String... likes) {
        StringBuffer hql = new StringBuffer("select s.dbStatusType,max(s.createTime) from SwiftExecutorTaskEntity s ");
        StringBuffer likeHql = new StringBuffer();
        if (likes.length > 0) {
            for (int i = 0; i < likes.length; i++) {
                if (i == 0) {
                    likeHql.append(" where ");
                } else {
                    likeHql.append(" and ");
                }
                likeHql.append(" s.taskContent like :like").append(i);
            }
        }
        hql.append(likeHql).append(" group by s.dbStatusType");
        final List<Object[]> select = dao.select(hql.toString(), query -> {
            for (int i = 0; i < likes.length; i++) {
                query.setParameter("like" + i, "%" + likes[i] + "%");
            }
        });
        return select;
    }

    @Override
    public SwiftExecutorTaskEntity getRepeatTaskByTime(long createTime, String... likes) {
        final List<SwiftExecutorTaskEntity> tasks = dao.selectQuery((query, builder, from) -> {
            List<Predicate> predicateList = new ArrayList<>();
            for (String v : likes) {
                Predicate taskContent = builder.like(from.get("taskContent"), "%" + v + "%");
                predicateList.add(taskContent);
            }
            predicateList.add(builder.equal(from.get("createTime"), createTime));
            query.select(from).where(predicateList.toArray(new Predicate[]{}));
        });
        if (tasks.isEmpty()) {
            return null;
        }
        return tasks.get(0);
    }

    @Override
    public List<SwiftExecutorTaskEntity> getRepeatTaskByTime(long beginTime, long endTime, String... likes) {
        final List<SwiftExecutorTaskEntity> tasks = dao.selectQuery((query, builder, from) -> {
            List<Predicate> predicateList = new ArrayList<>();
            for (String v : likes) {
                Predicate taskContent = builder.like(from.get("taskContent"), "%" + v + "%");
                predicateList.add(taskContent);
            }
            predicateList.add(builder.gt(from.get("createTime"), beginTime));
            predicateList.add(builder.lt(from.get("createTime"), endTime));
            predicateList.add(builder.equal(from.get("dbStatusType"), DBStatusType.REPEAT));
            query.select(from).where(predicateList.toArray(new Predicate[]{}));
        });
        if (tasks.isEmpty()) {
            return Collections.emptyList();
        }
        return tasks;
    }

    @Override
    public void delete(ExecutorTask executorTask) {
        dao.delete(SwiftExecutorTaskEntity.convertEntity(executorTask));
    }

    @Override
    public ExecutorTask get(String taskId) {
        final List<SwiftExecutorTaskEntity> tasks = dao.selectQuery((query, builder, from) ->
                query.select(from)
                        .where(builder.equal(from.get("id"), taskId)
                                , builder.equal(from.get("clusterId"), SwiftProperty.get().getMachineId())));
        if (tasks.isEmpty()) {
            return null;

        }
        return tasks.get(0).convert();
    }
}
