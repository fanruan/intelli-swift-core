package com.fr.swift.executor.config;

import java.sql.SQLException;
import java.util.List;

/**
 * @author Heng.J
 * @date 2020/11/3
 * @description 主节点因迁移任务暂存的任务
 * @since swift-1.2.0
 */
public interface BlockTaskService {

    void save(String yearMonth, String taskContent) throws SQLException;

    List<String> getBlockTasksByYearMonth(String yearMonth);
}
