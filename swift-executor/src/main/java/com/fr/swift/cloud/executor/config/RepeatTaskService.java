package com.fr.swift.cloud.executor.config;

import java.util.List;

/**
 * @author Heng.J
 * @date 2020/12/25
 * @description
 * @since swift-1.2.0
 */
public interface RepeatTaskService {

    void save(SwiftRepeatTaskEntity repeatTaskEntity);

    void delete(SwiftRepeatTaskEntity repeatTaskEntity);

    List<SwiftRepeatTaskEntity> getAllTasks();

    List<SwiftRepeatTaskEntity> getTasksByKey(String repeatKey);
}
