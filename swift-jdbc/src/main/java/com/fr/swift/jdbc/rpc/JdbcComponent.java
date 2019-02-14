package com.fr.swift.jdbc.rpc;

/**
 * @author yee
 * @date 2018/9/6
 */
public interface JdbcComponent {
    /**
     * 启动
     */
    void start();

    /**
     * 停止
     */
    void stop();

    /**
     * 处理异常
     *
     * @param e
     */
    void handlerException(Exception e);
}
