package com.fr.swift.exception.service;

import com.fr.swift.exception.ExceptionInfo;

import java.util.Set;

/**
 * @author Marvin
 * @date 8/8/2019
 * @description
 * @since swift 1.1
 */
public interface ExceptionInfoService {
    /**
     * 取出异常信息
     *
     * @param operateNodeId 处理异常的节点id
     * @param state         异常状态
     * @return
     */
    Set<ExceptionInfo> getExceptionInfo(String operateNodeId, ExceptionInfo.State state);

    /**
     * 成功处理异常后删除异常信息
     *
     * @param id 唯一表示一个异常的id
     * @return
     */
    boolean removeExceptionInfo(String id);

    /**
     * 持久化异常
     *
     * @param info
     * @return
     */
    boolean maintain(ExceptionInfo info);
}
