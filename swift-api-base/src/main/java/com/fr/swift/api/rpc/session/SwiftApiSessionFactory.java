package com.fr.swift.api.rpc.session;

/**
 * @author yee
 * @date 2018/8/27
 */
public interface SwiftApiSessionFactory<T extends SwiftApiSession> {
    T openSession();
}
