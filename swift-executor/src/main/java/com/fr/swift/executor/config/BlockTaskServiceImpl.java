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

    private SwiftDao<SwiftBlockTaskEntity> dao = new SwiftDaoImpl<>(SwiftBlockTaskEntity.class);

    @Override
    public void save(String yearMonth, String taskContent) throws SQLException {
    }

    @Override
    public List<String> getBlockTasksByYearMonth(String yearMonth) {
        final List<?> select = dao.selectQuery((query, builder, from) ->
                query.select(from.get("taskContent")).where(builder.equal(from.get("yearMonth"), yearMonth)));
        if (select.isEmpty()) {
            return Collections.emptyList();
        }
        return (List<String>) select;
    }
}
