package com.fr.swift.cloud.executor.config;

import com.fr.swift.cloud.beans.annotation.SwiftBean;
import com.fr.swift.cloud.config.dao.SwiftDao;
import com.fr.swift.cloud.config.dao.SwiftDaoImpl;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Heng.J
 * @date 2020/11/3
 * @description
 * @since swift-1.2.0
 */
@SwiftBean(name = "blockTaskService")
public class BlockTaskServiceImpl implements BlockTaskService {

    private final SwiftDao<SwiftBlockTaskEntity> dao = new SwiftDaoImpl<>(SwiftBlockTaskEntity.class);

    @Override
    public void saveOrUpdate(SwiftBlockTaskEntity blockTaskEntity) throws SQLException {
        try {
            dao.insert(blockTaskEntity);
        } catch (Exception e) {
            dao.update(blockTaskEntity);
        }
    }

    @Override
    public void deleteById(String id) {
        dao.deleteQuery((query, builder, from) -> query.select(from).where(builder.equal(from.get("id"), id)));
    }

    @Override
    public void deleteByBlockIndex(String blockIndex) {
        dao.deleteQuery((query, builder, from) ->
                query.select(from).where(builder.equal(from.get("blockingIndex"), blockIndex)));
    }

    @Override
    public Set<String> getBlockIndexes() {
        List<?> select = dao.selectQuery((query, builder, from) -> query.select(from.get("blockingIndex")));
        return new HashSet<>((List<String>) select);
    }

    @Override
    public List<String> getTasksByBlockIndex(String blockIndex) {
        List<?> select = dao.selectQuery((query, builder, from) ->
                query.select(from.get("taskContent")).where(builder.equal(from.get("blockingIndex"), blockIndex)));
        return (List<String>) select;
    }
}
