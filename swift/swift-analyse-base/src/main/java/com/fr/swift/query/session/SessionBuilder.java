package com.fr.swift.query.session;

/**
 * @author yee
 * @date 2018/6/19
 */
public interface SessionBuilder {
    /**
     * 创建Session
     *
     * @param cacheTimeout
     * @return
     */
    Session build(long cacheTimeout);

    /**
     * 指定queryId
     *
     * @return
     */
    String getQueryId();
}
