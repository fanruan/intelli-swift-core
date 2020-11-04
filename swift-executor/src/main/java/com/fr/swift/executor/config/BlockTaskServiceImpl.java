package com.fr.swift.executor.config;

import com.fr.swift.beans.annotation.SwiftBean;

import java.sql.SQLException;
import java.util.List;

/**
 * @author Heng.J
 * @date 2020/11/3
 * @description
 * @since swift-1.2.0
 */
@SwiftBean(name = "blockTaskService")
public class BlockTaskServiceImpl implements BlockTaskService {

    @Override
    public void save(String yearMonth, String taskContent) throws SQLException {
    }

    @Override
    public List<String> getBlockTasksByYearMonth(String yearMonth) {
        return null;
    }
}
