package com.fr.swift.api.rpc.session;

/**
 * @author yee
 * @date 2018/8/27
 */
public interface SwiftApiSessionFactory<T extends SwiftApiSession> {
    /**
     * 创建Session
     *
     * @return
     */
    T openSession();

    void init(String username, String password) throws Exception;

    /**
     * 关闭Factory
     *
     * @throws Exception
     */
    void close();
}
