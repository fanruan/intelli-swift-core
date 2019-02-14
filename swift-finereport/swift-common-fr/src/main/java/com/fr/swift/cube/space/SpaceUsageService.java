package com.fr.swift.cube.space;

import com.fr.swift.source.SourceKey;

import java.util.List;

/**
 * @author anchore
 * @date 2018/4/13
 */
public interface SpaceUsageService {
    /**
     * 单表占用
     *
     * @param table 表key
     * @return bytes
     * @throws Exception 异常
     */
    long getTableUsedSpace(SourceKey table) throws Exception;

    /**
     * 多表占用
     *
     * @param tables 表key
     * @return bytes
     * @throws Exception 异常
     */
    long getTableUsedSpace(List<SourceKey> tables) throws Exception;

    /**
     * 总占用
     *
     * @return bytes
     * @throws Exception 异常
     */
    long getUsedOverall() throws Exception;

    /**
     * 总可用
     *
     * @return bytes
     * @throws Exception 异常
     */
    long getUsableOverall() throws Exception;

    /**
     * 总容量
     *
     * @return bytes
     * @throws Exception 异常
     */
    long getTotalOverall() throws Exception;
}