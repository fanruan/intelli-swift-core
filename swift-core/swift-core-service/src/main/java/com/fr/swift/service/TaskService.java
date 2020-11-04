package com.fr.swift.service;

/**
 * @author Heng.J
 * @date 2020/10/22
 * @description
 * @since swift-1.2.0
 */
public interface TaskService extends SwiftService {

    boolean dispatchTask(String clusterId, String yearMonth) throws Exception;
}
