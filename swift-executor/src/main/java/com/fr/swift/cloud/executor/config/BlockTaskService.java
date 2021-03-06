package com.fr.swift.cloud.executor.config;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;

/**
 * @author Heng.J
 * @date 2020/11/3
 * @description 主节点因迁移任务暂存的任务
 * @since swift-1.2.0
 */
public interface BlockTaskService {

    void saveOrUpdate(SwiftBlockTaskEntity blockTaskEntity) throws SQLException;

    void deleteById(String id);

    void deleteByBlockIndex(String blockIndex);

    Set<String> getBlockIndexes();

    List<String> getTasksByBlockIndex(String blockIndex);
}
