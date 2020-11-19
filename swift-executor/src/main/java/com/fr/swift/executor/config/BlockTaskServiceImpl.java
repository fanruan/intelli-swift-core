package com.fr.swift.executor.config;

import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.config.dao.SwiftDao;
import com.fr.swift.config.dao.SwiftDaoImpl;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

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
    public void save(String blockIndex, String taskContent) throws SQLException {
        dao.insert(new SwiftBlockTaskEntity(blockIndex, taskContent));
    }

    @Override
    public void deleteByBlockIndex(String blockIndex) {
        dao.deleteQuery((query, builder, from) ->
                query.select(from).where(builder.equal(from.get("blockingIndex"), blockIndex)));
    }

    @Override
    public List<String> getBlockTasksByBlockIndex(String blockIndex) {
        final List<?> select = dao.selectQuery((query, builder, from) ->
                query.select(from.get("taskContent")).where(builder.equal(from.get("blockingIndex"), blockIndex)));
        if (select.isEmpty()) {
            return Collections.emptyList();
        }
        return (List<String>) select;
    }
}
