package com.fr.swift.query.session;

/**
 * @author yee
 * @date 2018/6/19
 */
public interface SessionBuilder {
    Session build(long cacheTimeout);
}
