package com.fr.swift.exception;

/**
 * @author Marvin
 * @date 8/8/2019
 * @description
 * @since swift 1.1
 */
public interface ExceptionInfo {
    String getId();

    /**
     * 发生异常的源节点
     *
     * @return
     */
    String getSourceNodeId();

    Type getType();

    /**
     * 异常发生的时间戳
     *
     * @return
     */
    long getOccurredTime();

    /**
     * 异常的详细信息
     *
     * @return
     */
    ExceptionContext getContext();

    /**
     * 异常的处理状态：处理中，已处理，无法处理
     *
     * @return
     */
    State getState();

    /**
     * 当前正在处理异常的节点
     *
     * @return
     */
    String getOperateNodeId();

    @Override
    boolean equals(Object o);

    @Override
    int hashCode();

    enum State {
        // 待处理，处理中，已解决，未解决
        PENDING, PROCESSING, SOLVED, UNSOLVED
    }

    interface Type {
        /**
         * 自定义的异常状态码
         *
         * @return
         */
        int getExceptionCode();

        String getDescription();
    }
}