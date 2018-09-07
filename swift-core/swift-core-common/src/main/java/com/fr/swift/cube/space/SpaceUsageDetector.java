package com.fr.swift.cube.space;

import java.net.URI;

/**
 * @author anchore
 * @date 2018/4/13
 */
public interface SpaceUsageDetector {
    /**
     * 占用
     *
     * @param uri 地址
     * @return bytes
     * @throws Exception 异常
     */
    long detectUsed(URI uri) throws Exception;

    /**
     * 可用
     *
     * @param uri 地址
     * @return bytes
     * @throws Exception 异常
     */
    long detectUsable(URI uri) throws Exception;

    /**
     * 总量
     *
     * @param uri 地址
     * @return bytes
     * @throws Exception 异常
     */
    long detectTotal(URI uri) throws Exception;
}
