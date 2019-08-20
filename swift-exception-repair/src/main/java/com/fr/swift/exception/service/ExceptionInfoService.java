package com.fr.swift.exception.service;

import com.fr.swift.exception.ExceptionInfo;

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
     * @param sourceNodeId  发生异常的节点id
     * @param operateNodeId 处理异常的节点id
     * @param state         异常状态
     * @return
     */
    ExceptionInfo getExceptionInfo(String sourceNodeId, String operateNodeId, ExceptionInfo.State state);

    /**
     * 成功处理异常后删除异常信息
     *
     * @param id 唯一表示一个异常异常的id
     * @return
     */
    boolean deleteExceptionInfo(String id);

    /**
     * 持久化异常
     *
     * @param info
     * @return
     */
    boolean maintain(ExceptionInfo info);
}
