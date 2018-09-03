package com.fr.swift.api.rpc.session;

/**
 * @author yee
 * @date 2018/8/27
 */
public interface SwiftApiSession {
    /**
     * 关闭session
     *
     * @throws Exception
     */
    void close() throws Exception;
}
